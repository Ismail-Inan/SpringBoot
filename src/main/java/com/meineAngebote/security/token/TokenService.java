package com.meineAngebote.security.token;

import com.meineAngebote.company.Company;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TokenService {

  private final TokenRepository tokenRepository;

  public void saveCompanyToken(Company company, String jwtToken) {
    var token = Token.builder()
        .company(company)
        .token(jwtToken)
        .tokenType(TokenType.BEARER)
        .expired(false)
        .revoked(false)
        .build();
    tokenRepository.save(token);
  }

}
