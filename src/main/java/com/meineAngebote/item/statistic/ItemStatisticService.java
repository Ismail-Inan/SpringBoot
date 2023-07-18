package com.meineAngebote.item.statistic;

import org.springframework.stereotype.Service;

@Service
public class ItemStatisticService {

  private final ItemStatisticRepository itemStatisticRepository;

  public ItemStatisticService(ItemStatisticRepository itemStatisticRepository) {
    this.itemStatisticRepository = itemStatisticRepository;
  }

  public void increaseItemVisits(String itemId) {
    ItemStatistic itemStatistic = itemStatisticRepository.findByItemId(itemId);
    if (itemStatistic == null) {
      itemStatistic = new ItemStatistic(itemId, 1L);
    } else {
      itemStatistic.increaseVisitCount();
    }
    itemStatisticRepository.save(itemStatistic);
  }

  public Long getItemViewsCount(String itemId) {
    ItemStatistic itemStatistic = itemStatisticRepository.findByItemId(itemId);
    if (itemStatistic == null) {
      return 0L;
    }
    return itemStatistic.getVisitCount();
  }

}