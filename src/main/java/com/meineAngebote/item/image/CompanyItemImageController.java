package com.meineAngebote.item.image;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/company-item-image")
public class CompanyItemImageController {

  private final CompanyItemImageService companyItemImageService;

  public CompanyItemImageController(CompanyItemImageService companyItemImageService) {
    this.companyItemImageService = companyItemImageService;
  }

  @GetMapping("/public/{companyItemId}/all")
  public ResponseEntity<List<CompanyItemImageDTO>> getAllImagesByCompanyItemId(
      @PathVariable Long companyItemId) {
    List<CompanyItemImageDTO> companyItemImages = companyItemImageService.getAllImagesByItem(
        companyItemId);
    return ResponseEntity.ok(companyItemImages);
  }

}