package com.meineAngebote.item.image;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.meineAngebote.item.CompanyItem;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
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
public class CompanyItemImage {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false, updatable = false)
  private Long id;

  private String imageUrl;

  private int ordinal;

  @ManyToOne
  @JsonIgnore
  private CompanyItem item;

  public CompanyItemImage(String imageUrl, int ordinal, CompanyItem item) {
    this.imageUrl = imageUrl;
    this.ordinal = ordinal;
    this.item = item;
  }

}