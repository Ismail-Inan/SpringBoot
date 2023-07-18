package com.meineAngebote.registration;


import com.meineAngebote.company.AppUserRole;
import com.meineAngebote.company.Company;
import com.meineAngebote.company.CompanyDTO;
import com.meineAngebote.company.CompanyDTOMapper;
import com.meineAngebote.company.CompanyRepository;
import com.meineAngebote.company.CompanyService;
import com.meineAngebote.email.EmailSender;
import com.meineAngebote.registration.token.ConfirmationToken;
import com.meineAngebote.registration.token.ConfirmationTokenService;
import com.meineAngebote.security.auth.AuthenticationResponse;
import com.meineAngebote.security.auth.RegistrationRequest;
import com.meineAngebote.security.config.JwtService;
import com.meineAngebote.security.token.TokenService;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class RegistrationService {

  private final CompanyService companyService;
  private final TokenService tokenService;
  private final ConfirmationTokenService confirmationTokenService;
  private final EmailSender emailSender;
  private final PasswordEncoder passwordEncoder; //  or private final BCryptPasswordEncoder bCryptPasswordEncoder;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  private final JwtService jwtService;
  private final CompanyRepository companyRepository;
  private final CompanyDTOMapper companyDTOMapper;

  public AuthenticationResponse register(RegistrationRequest request) {
    boolean isValidEmail = EmailValidator.getInstance().isValid(request.getEmail());

    if (!isValidEmail) {
      throw new IllegalArgumentException("Invalid email");
    }

    final Optional<Company> optionalCompany = companyRepository
        .findByEmail(request.getEmail());

    if (optionalCompany.isPresent()) {
      return handleRegisterWithExistingEmail(request, optionalCompany.get());
    }

    return createCompanyAndSendEmail(request);
  }

  private AuthenticationResponse handleRegisterWithExistingEmail(RegistrationRequest request,
      Company company) {

    Optional<ConfirmationToken> token = confirmationTokenService.getLatestToken(company);

    if (token.isEmpty()) {
      //create token and send email
      String emailToken = confirmationTokenService.createAndSaveConfirmationToken(
          company);
      String link =
          "http://localhost:8080/api/v1/registration/confirm?token=" + emailToken;

      emailSender.send(request.getEmail(), buildEmail(link));
      return null;
    }

    //token exists
    if (Objects.nonNull(token.get().getConfirmedAt())) {
      throw new IllegalStateException("Email already taken");
    }

    // Token exists and is not confirmed or has expired
    // Resend email with existing token or create a new token if expired

    // TODO: Implement resend token if not confirmed

    String emailToken = confirmationTokenService.createAndSaveConfirmationToken(company);
    String link = "http://localhost:8080/api/v1/registration/confirm?token=" + emailToken;

    emailSender.send(request.getEmail(), buildEmail(link));
    return null;

  }

  private AuthenticationResponse createCompanyAndSendEmail(RegistrationRequest request) {
    var company = companyRepository.save(new Company(
        request.getEmail(),
        passwordEncoder.encode(request.getPassword()),
        AppUserRole.USER
    ));

    final String emailToken = confirmationTokenService.createAndSaveConfirmationToken(company);
    final String jwtToken = jwtService.generateToken(company);
    final String refreshToken = jwtService.generateRefreshToken(company);
    tokenService.saveCompanyToken(company, jwtToken);

    final String link = "http://localhost:8080/api/v1/registration/confirm?token=" + emailToken;
    emailSender.send(request.getEmail(), buildEmail(link));

    final CompanyDTO companyDTO = companyDTOMapper.apply(company);

    return AuthenticationResponse.builder()
        .accessToken(jwtToken)
        .refreshToken(refreshToken)
        .companyDTO(companyDTO)
        .build();
  }

  @Transactional
  public String confirmToken(String token) {
    ConfirmationToken confirmationToken = confirmationTokenService
        .getToken(token)
        .orElseThrow(() ->
            new IllegalStateException("token not found"));

    if (confirmationToken.getConfirmedAt() != null) {
      throw new IllegalStateException("email already confirmed");
    }

    LocalDateTime expiredAt = confirmationToken.getExpiresAt();

    if (expiredAt.isBefore(LocalDateTime.now())) {
      throw new IllegalStateException("token expired");
    }

    confirmationTokenService.setConfirmedAt(token);
    companyService.enableCompany(
        confirmationToken.getCompany().getEmail());
    return "confirmed";
  }

  private String buildEmail(String link) {
    return
        "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n"
            +
            "\n" +
            "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
            "\n" +
            "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n"
            +
            "    <tbody><tr>\n" +
            "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
            "        \n" +
            "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n"
            +
            "          <tbody><tr>\n" +
            "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
            "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n"
            +
            "                  <tbody><tr>\n" +
            "                    <td style=\"padding-left:10px\">\n" +
            "                  \n" +
            "                    </td>\n" +
            "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n"
            +
            "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Confirm your email</span>\n"
            +
            "                    </td>\n" +
            "                  </tr>\n" +
            "                </tbody></table>\n" +
            "              </a>\n" +
            "            </td>\n" +
            "          </tr>\n" +
            "        </tbody></table>\n" +
            "        \n" +
            "      </td>\n" +
            "    </tr>\n" +
            "  </tbody></table>\n" +
            "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n"
            +
            "    <tbody><tr>\n" +
            "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
            "      <td>\n" +
            "        \n" +
            "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n"
            +
            "                  <tbody><tr>\n" +
            "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
            "                  </tr>\n" +
            "                </tbody></table>\n" +
            "        \n" +
            "      </td>\n" +
            "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
            "    </tr>\n" +
            "  </tbody></table>\n" +
            "\n" +
            "\n" +
            "\n" +
            "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n"
            +
            "    <tbody><tr>\n" +
            "      <td height=\"30\"><br></td>\n" +
            "    </tr>\n" +
            "    <tr>\n" +
            "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
            "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n"
            +
            "        \n" +
            "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hallo,</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Thank you for registering. Please click on the below link to activate your account: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\""
            + link
            + "\">Activate Now</a> </p></blockquote>\n Link will expire in 15 minutes. <p>See you soon</p>"
            +
            "        \n" +
            "      </td>\n" +
            "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
            "    </tr>\n" +
            "    <tr>\n" +
            "      <td height=\"30\"><br></td>\n" +
            "    </tr>\n" +
            "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
            "\n" +
            "</div></div>";
  }
}