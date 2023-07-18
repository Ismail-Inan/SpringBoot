package com.meineAngebote.item;

import jakarta.persistence.Tuple;
import java.util.List;
import org.springframework.data.domain.Page;

public interface CompanyItemService {

  Page<CompanyItem> getCompanyItems(CompanyItemFilter filter, int page, int size);

  List<CompanyItem> getLatestCompanyItems();

  CompanyItem[] getItemsByCompanyId(Long companyId);

  Tuple getMinMaxPrices();

  Tuple getMinMaxPrices(String category, String searchText);

  Tuple getMinMaxPricesForCategory(String category);

  Tuple getMinMaxPricesForSearchText(String searchText);
}