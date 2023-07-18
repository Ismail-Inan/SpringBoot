package com.meineAngebote.item;

import com.meineAngebote.company.CompanyDTO;
import com.meineAngebote.item.category.ItemCategoryUtil.ItemCategory;
import com.meineAngebote.item.image.CompanyItemImageDTO;

public record CompanyItemDTO(
    Long id,
    String title,
    String description,
    Float price,
    Boolean giveAway,
    CompanyDTO owner,
    CompanyItemImageDTO thumbnail,
    ItemCategory category
) {

}