package com.meineAngebote.company;

import com.meineAngebote.s3.S3Buckets;
import com.meineAngebote.s3.S3Service;
import com.meineAngebote.security.LoginAttemptService;
import com.meineAngebote.security.auth.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

  @Mock
  private CompanyRepository companyRepository;
  @Mock
  private S3Service s3Service;
  @Mock
  private S3Buckets s3Buckets;
  @Mock
  private AuthenticationService authenticationService;
  @Mock
  private LoginAttemptService loginAttemptService;
  private CompanyService companyService;

  @BeforeEach
  void setUp() {
    companyService = new CompanyService(companyRepository, s3Service,
        s3Buckets, authenticationService, loginAttemptService);
  }

}