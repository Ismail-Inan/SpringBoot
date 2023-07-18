package com.meineAngebote.registration.token;

import com.meineAngebote.company.Company;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ConfirmationTokenService {

  private final ConfirmationTokenRepository confirmationTokenRepository;

  public String createAndSaveConfirmationToken(final Company company) {
    String token = UUID.randomUUID().toString();

    ConfirmationToken confirmationToken = new ConfirmationToken(
        token,
        LocalDateTime.now(),
        LocalDateTime.now().plusMinutes(15),
        company
    );

    confirmationTokenRepository.save(confirmationToken);

    return token;
  }

  public Optional<ConfirmationToken> getToken(String token) {
    return confirmationTokenRepository.findByToken(token);
  }

  public Optional<ConfirmationToken> getLatestToken(Company company) {
    return confirmationTokenRepository.findByCompanyOrderByCreatedAtDesc(company);
  }

  public int setConfirmedAt(String token) {
    return confirmationTokenRepository.updateConfirmedAt(
        token, LocalDateTime.now());
  }

}