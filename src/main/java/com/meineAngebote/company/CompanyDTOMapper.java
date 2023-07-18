package com.meineAngebote.company;

import java.util.function.Function;
import org.springframework.stereotype.Service;

@Service
public class CompanyDTOMapper implements Function<Company, CompanyDTO> {

  @Override
  public CompanyDTO apply(Company company) {
    return new CompanyDTO(
        company.getId(),
        company.getName(),
        company.getEmail(),
        company.getAddress(),
        company.getPhoneNumber(),
        company.getProfileImageId()
    );
  }
}