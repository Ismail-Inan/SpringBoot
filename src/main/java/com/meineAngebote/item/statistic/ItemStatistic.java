package com.meineAngebote.item.statistic;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ItemStatistic {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String itemId;

  //save views of months/all time/weekly?
  private Long visitCount;

  public ItemStatistic(String itemId, Long visitCount) {
    this.itemId = itemId;
    this.visitCount = visitCount;
  }

  public void increaseVisitCount() {
    this.visitCount++;
  }

}