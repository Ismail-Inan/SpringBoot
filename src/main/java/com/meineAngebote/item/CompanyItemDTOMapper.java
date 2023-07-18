package com.meineAngebote.item;

import com.meineAngebote.company.CompanyDTOMapper;
import com.meineAngebote.item.image.CompanyItemImageDTOMapper;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyItemDTOMapper implements Function<CompanyItem, CompanyItemDTO> {

  private final CompanyDTOMapper companyDTOMapper;
  private final CompanyItemImageDTOMapper companyItemImageDTOMapper;

  @Override
  public CompanyItemDTO apply(CompanyItem item) {
    return new CompanyItemDTO(
        item.getId(),
        item.getTitle(),
        item.getDescription(),
        item.getPrice(),
        item.isGiveAway(),
        companyDTOMapper.apply(item.getOwner()),
        companyItemImageDTOMapper.apply(item.getThumbnail()),
        item.getCategory()
    );
  }

}