package com.meineAngebote.item.image;

import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyItemImageDTOMapper implements Function<CompanyItemImage, CompanyItemImageDTO> {

  @Override
  public CompanyItemImageDTO apply(CompanyItemImage companyItemImage) {
    return new CompanyItemImageDTO(
        companyItemImage.getId(),
        companyItemImage.getImageUrl(),
        companyItemImage.getOrdinal()
    );
  }

}