package com.meineAngebote.item;

import com.meineAngebote.item.category.ItemCategoryUtil;
import com.meineAngebote.item.category.ItemCategoryUtil.ItemCategory;
import com.meineAngebote.item.category.ItemCategoryUtil.ItemCategoryNode;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CompanyItemServiceImpl implements CompanyItemService {

  private final CompanyItemRepository companyItemRepository;

  public CompanyItem saveCompanyItem(@NonNull CompanyItem companyItem) {
    return companyItemRepository.save(companyItem);
  }

  public List<CompanyItem> findAllUserItems() {
    return companyItemRepository.findAll();
  }

  public CompanyItem updateCompanyItem(CompanyItem CompanyItem) {
    return companyItemRepository.save(CompanyItem);
  }

  public CompanyItem findCompanyItemById(Long id) {
    return companyItemRepository.findUserItemById(id)
        .orElseThrow(
            () -> new CompanyItemNotFoundException("Item by id " + id + " was not found"));
  }

  public CompanyItem[] getItemsByCompanyId(Long companyId) {
    return companyItemRepository.findByOwnerId(companyId);
  }

  public void deleteCompanyItem(Long id) {
    companyItemRepository.deleteCompanyItemById(id);
  }

  @Override
  public Page<CompanyItem> getCompanyItems(CompanyItemFilter filter, int page, int size) {

    Specification<CompanyItem> specification = (root, query, cb) -> {

      Predicate predicate = cb.conjunction();
      if (filter.getSearchText() != null) {
        predicate = cb.and(predicate,
            cb.like(root.get("title"), "%" + filter.getSearchText() + "%"));
      }
      if (filter.getCategory() != null && !filter.getCategory().equalsIgnoreCase("all")) {
        boolean validCategory = Arrays.stream(ItemCategoryUtil.ItemCategory.values())
            .map(ItemCategoryUtil.ItemCategory::name)
            .anyMatch(filter.getCategory()::equalsIgnoreCase);

        if (!validCategory) {
          throw new IllegalArgumentException("Invalid category: " + filter.getCategory());
        }

        ItemCategoryUtil.ItemCategory itemCategory = ItemCategoryUtil.ItemCategory.valueOf(
            filter.getCategory());

        final List<ItemCategory> categories = new ArrayList<>();
        categories.add(itemCategory);

        final List<ItemCategoryNode> children = ItemCategoryUtil.getAllChildren(itemCategory);
        if (!children.isEmpty()) {
          categories.addAll(children.stream().map(ItemCategoryNode::getCategory).toList());
        }

        predicate = cb.and(predicate, root.get("category").in(categories));
      }
      if (filter.getMaxPrice() != null) {
        predicate = cb.and(predicate,
            cb.lessThanOrEqualTo(root.get("price"), filter.getMaxPrice()));
      }
      if (filter.getMinPrice() != null) {
        predicate = cb.and(predicate,
            cb.greaterThanOrEqualTo(root.get("price"), filter.getMinPrice()));
      }
      if (filter.getForFree() != null) {
        predicate = cb.and(predicate, cb.equal(root.get("forFree"), filter.getForFree()));
      }
      return predicate;
    };

    Pageable pageable = PageRequest.of(page, size);
    return companyItemRepository.findAll(specification, pageable);
  }

  @Override
  public List<CompanyItem> getLatestCompanyItems() {
    return companyItemRepository.findFirst5ByOrderByPublishedAtDesc();
  }

  @Override
  public Tuple getMinMaxPrices() {
    return companyItemRepository.findMinMaxPrices();
  }

  @Override
  public Tuple getMinMaxPrices(String category, String searchText) {
    return companyItemRepository.findMinMaxPrices(category, searchText);
  }

  @Override
  public Tuple getMinMaxPricesForCategory(String category) {
    return companyItemRepository.findMinMaxPricesForCategory(category);
  }

  @Override
  public Tuple getMinMaxPricesForSearchText(String searchText) {
    return companyItemRepository.findMinMaxPricesForSearchText(searchText);
  }

}