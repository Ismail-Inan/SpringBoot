package com.meineAngebote.item;

import lombok.Data;

@Data
public class CompanyItemFilter {

  private Double minPrice;
  private Double maxPrice;
  private Boolean forFree;
  private String searchText;
  private String category;

  @Override
  public String toString() {
    return "SidebarFilter{" +
        "minPrice=" + minPrice +
        ", maxPrice=" + maxPrice +
        ", forFree=" + forFree +
        ", searchText='" + searchText + '\'' +
        ", category='" + category + '\'' +
        '}';
  }
}
