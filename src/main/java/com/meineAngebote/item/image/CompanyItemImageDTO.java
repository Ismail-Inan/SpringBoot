package com.meineAngebote.item.image;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyItemImageDTO {

  private Long id;
  private String imageUrl;
  private int ordinal;
}