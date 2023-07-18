package com.meineAngebote.item;

import jakarta.persistence.Tuple;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyItemRepository extends JpaRepository<CompanyItem, Long>,
    JpaSpecificationExecutor<CompanyItem> {

  void deleteCompanyItemById(Long id);

  Optional<CompanyItem> findUserItemById(Long id);

  CompanyItem[] findByOwnerId(Long companyId);

  List<CompanyItem> findFirst5ByOrderByPublishedAtDesc();

  @Query(
      "SELECT FLOOR(MIN(ci.price)), CEIL(MAX(ci.price)) FROM CompanyItem ci WHERE ci.category = :category "
          + "AND ci.title LIKE %:searchText%")
  Tuple findMinMaxPrices(@Param("category") String category,
      @Param("searchText") String searchText);

  @Query("SELECT FLOOR(MIN(ci.price)), CEIL(MAX(ci.price)) FROM CompanyItem ci")
  Tuple findMinMaxPrices();

  @Query("SELECT FLOOR(MIN(ci.price)), CEIL(MAX(ci.price)) FROM CompanyItem ci WHERE ci.category = :category")
  Tuple findMinMaxPricesForCategory(String category);

  @Query("SELECT FLOOR(MIN(ci.price)), CEIL(MAX(ci.price)) FROM CompanyItem ci WHERE ci.title LIKE %:searchText%")
  Tuple findMinMaxPricesForSearchText(String searchText);

}