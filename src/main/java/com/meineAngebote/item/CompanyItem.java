package com.meineAngebote.item;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.meineAngebote.company.Company;
import com.meineAngebote.item.category.ItemCategoryUtil;
import com.meineAngebote.item.image.CompanyItemImage;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@JsonInclude(NON_DEFAULT)
public class CompanyItem {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false, updatable = false)
  private Long id;
  @ManyToOne
  private Company owner;
  private String title;
  @Column(length = 3000)
  private String description;
  private ItemCategoryUtil.ItemCategory category;
  private Float price;
  private boolean giveAway;
  private boolean deleted;
  private LocalDateTime publishedAt;
  private LocalDateTime expiresAt;
  @OneToOne
  private CompanyItemImage thumbnail;

  public CompanyItem(String title, String description, ItemCategoryUtil.ItemCategory category,
      Float price, boolean giveAway, Company owner) {
    this.title = title;
    this.description = description;
    this.category = category;
    this.price = price;
    this.giveAway = giveAway;
    this.owner = owner;
    this.deleted = false;
  }

}