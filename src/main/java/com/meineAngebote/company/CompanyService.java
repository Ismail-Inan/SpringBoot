package com.meineAngebote.company;

import com.meineAngebote.s3.S3Buckets;
import com.meineAngebote.s3.S3Service;
import com.meineAngebote.security.LoginAttemptService;
import com.meineAngebote.security.auth.AuthenticationService;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
@Transactional
public class CompanyService implements UserDetailsService {

  private final static String USER_NOT_FOUND_MSG =
      "user with email %s not found";

  private final CompanyRepository companyRepository;
  private final S3Service s3Service;
  private final S3Buckets s3Buckets;
  private final AuthenticationService authenticationService;
  @Autowired
  private LoginAttemptService loginAttemptService;

  @Override
  public UserDetails loadUserByUsername(String email)
      throws UsernameNotFoundException {
    if (loginAttemptService.isBlocked()) {
      throw new RuntimeException("blocked");
    }

    try {
      return companyRepository.findByEmail(email)
          .orElseThrow(() ->
              new UsernameNotFoundException(
                  String.format(USER_NOT_FOUND_MSG, email)));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public int enableCompany(String email) {
    return companyRepository.enableCompany(email);
  }

  public Company findById(Long id) {
    return companyRepository.findById(id)
        .orElseThrow(() -> new CompanyNotFoundException("Company by id " + id + " was not found"));
  }

  public Company save(Company Company) {
    return companyRepository.save(Company);
  }

  public Company updateCompany(Company company, CompanyDTO companyDTO) {
    company.setName(companyDTO.name());
    company.setAddress(companyDTO.address());

    return companyRepository.save(company);
  }

  public ResponseEntity<Void> uploadAvatar(MultipartFile image) {
    final Company company = authenticationService.getAuthenticatedCompany();

    final String avatarId = UUID.randomUUID().toString();

    try {
      s3Service.putObject(
          s3Buckets.getAppBucket(),
          "avatars/%s/%s".formatted(company.getId(), avatarId),
          image.getBytes()
      );
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return ResponseEntity.ok().build();
  }
}