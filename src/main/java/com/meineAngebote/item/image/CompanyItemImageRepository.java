package com.meineAngebote.item.image;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyItemImageRepository extends JpaRepository<CompanyItemImage, Long> {

  List<CompanyItemImage> findByItemId(Long itemId);

}