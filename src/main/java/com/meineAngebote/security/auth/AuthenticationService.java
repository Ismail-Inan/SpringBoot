package com.meineAngebote.security.auth;

import com.meineAngebote.company.Company;
import com.meineAngebote.company.CompanyDTO;
import com.meineAngebote.company.CompanyDTOMapper;
import com.meineAngebote.company.CompanyRepository;
import com.meineAngebote.security.config.JwtService;
import com.meineAngebote.security.token.TokenRepository;
import com.meineAngebote.security.token.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

  private final TokenService tokenService;
  private final CompanyRepository repository;
  private final TokenRepository tokenRepository;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  private final CompanyDTOMapper companyDTOMapper;

  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    if (request == null || request.getEmail() == null || request.getPassword() == null) {
      throw new IllegalArgumentException("Invalid authentication request");
    }

    String email = request.getEmail();

    Optional<Company> companyOptional = repository.findByEmail(email);

    if (companyOptional.isEmpty()) {
      throw new IllegalArgumentException("Passwort oder Email ist falsch");
    }

    try {
      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              email,
              request.getPassword()
          )
      );
      Company company = (Company) authentication.getPrincipal();
      CompanyDTO companyDTO = companyDTOMapper.apply(company);

      var jwtToken = jwtService.generateToken(company);
      var refreshToken = jwtService.generateRefreshToken(company);
      revokeAllCompanyTokens(company);
      tokenService.saveCompanyToken(company, jwtToken);

      return AuthenticationResponse.builder()
          .accessToken(jwtToken)
          .refreshToken(refreshToken)
          .companyDTO(companyDTO)
          .build();
    } catch (AuthenticationException e) {
      // Handle authentication failure
      throw new IllegalArgumentException("Invalid credentials");
    }
  }

  private void revokeAllCompanyTokens(Company company) {
    var validUserTokens = tokenRepository.findAllValidTokenByCompany(company.getId());
    if (validUserTokens.isEmpty()) {
      return;
    }
    validUserTokens.forEach(token -> {
      token.setExpired(true);
      token.setRevoked(true);
    });
    tokenRepository.saveAll(validUserTokens);
  }

  public AuthenticationResponse refreshToken(HttpServletRequest request)
      throws IOException {
    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      return null;
    }

    final String refreshToken = authHeader.substring(7);
    final String userEmail = jwtService.extractUsername(refreshToken);

    if (userEmail == null) {
      return null;
    }
    var user = this.repository.findByEmail(userEmail)
        .orElseThrow();

    if (!jwtService.isTokenValid(refreshToken, user)) {
      return null;
    }
    var accessToken = jwtService.generateToken(user);
    revokeAllCompanyTokens(user);
    tokenService.saveCompanyToken(user, accessToken);

    return AuthenticationResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .build();
  }

  public Company getAuthenticatedCompany() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null && auth.getPrincipal() instanceof Company) {
      return (Company) auth.getPrincipal();
    } else {
      return null;
    }
  }

  public boolean isAuthenticated() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    return auth != null && auth.isAuthenticated();
  }

}