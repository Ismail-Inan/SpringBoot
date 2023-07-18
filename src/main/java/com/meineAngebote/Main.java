package com.meineAngebote;

import com.meineAngebote.company.Company;
import com.meineAngebote.company.CompanyRepository;
import com.meineAngebote.registration.RegistrationService;
import com.meineAngebote.registration.token.ConfirmationToken;
import com.meineAngebote.registration.token.ConfirmationTokenService;
import com.meineAngebote.security.auth.RegistrationRequest;
import java.util.Optional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Main {

  public static void main(String[] args) {
    SpringApplication.run(Main.class, args);
  }

  @Bean
  CommandLineRunner runner(CompanyRepository companyRepository,
      RegistrationService registrationService,
      ConfirmationTokenService confirmationTokenService
  ) {
    return args -> {

      var request = new RegistrationRequest("ismailinanat@gmail.com", "Test123456.!");

      final Optional<Company> optionalCompany = companyRepository
          .findByEmail(request.getEmail());

      if (optionalCompany.isPresent()) {

        Optional<ConfirmationToken> token =
            confirmationTokenService.getLatestToken(optionalCompany.get());

        registrationService.confirmToken(token.get().getToken());
        //email exists
      } else {
        var a = registrationService.register(request);

        var comp = companyRepository.findById(a.getCompanyDTO().id());
        Optional<ConfirmationToken> token =
            confirmationTokenService.getLatestToken(comp.get());

        registrationService.confirmToken(token.get().getToken());

      }
    };
  }

}