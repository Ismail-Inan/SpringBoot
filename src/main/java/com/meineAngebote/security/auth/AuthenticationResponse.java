package com.meineAngebote.security.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meineAngebote.company.CompanyDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

  @JsonProperty("accessToken")
  private String accessToken;
  @JsonProperty("refreshToken")
  private String refreshToken;
  private CompanyDTO companyDTO;
}