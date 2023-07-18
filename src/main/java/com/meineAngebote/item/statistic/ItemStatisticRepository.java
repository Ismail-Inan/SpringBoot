package com.meineAngebote.item.statistic;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemStatisticRepository extends JpaRepository<ItemStatistic, Long> {

  ItemStatistic findByItemId(String itemId);

}
