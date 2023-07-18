package com.meineAngebote.company;

import com.meineAngebote.security.auth.AuthenticationService;
import java.util.Objects;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/company")
@AllArgsConstructor
public class CompanyController {

  private final CompanyService companyService;
  private final AuthenticationService authenticationService;
  private final CompanyDTOMapper companyDTOMapper;

  @GetMapping("/public/find/{id}")
  public ResponseEntity<CompanyDTO> getCompany(@PathVariable("id") Long id) {
    Company company = companyService.findById(id);
    final CompanyDTO companyDTO = companyDTOMapper.apply(company);

    return new ResponseEntity<>(companyDTO, HttpStatus.OK);
  }

  @PostMapping(value = "/private/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Void> setAvatar(@RequestParam("image") MultipartFile image) {

    return companyService.uploadAvatar(image);
  }

  @PutMapping("/private/update/{companyId}")
  public ResponseEntity<?> update(@PathVariable Long companyId,
      @RequestBody CompanyDTO requestedCompanyDTO) {

    if (companyId == null) {
      return ResponseEntity.badRequest().body("Company ID is required");
    }

    final Company company = authenticationService.getAuthenticatedCompany();

    if (!Objects.equals(company.getId(), companyId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    if (Objects.equals(company.getName(), requestedCompanyDTO.name()) &&
        Objects.equals(company.getAddress(), requestedCompanyDTO.address())) {
      //nothing changed
      CompanyDTO companyDTO = companyDTOMapper.apply(company);
      return new ResponseEntity<>(companyDTO, HttpStatus.OK);
    }

    Company updatedCompany = companyService.updateCompany(company, requestedCompanyDTO);
    CompanyDTO companyDTO = companyDTOMapper.apply(updatedCompany);

    return new ResponseEntity<>(companyDTO, HttpStatus.OK);
  }

}