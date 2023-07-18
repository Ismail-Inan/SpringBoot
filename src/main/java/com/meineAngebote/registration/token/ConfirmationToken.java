package com.meineAngebote.registration.token;

import com.meineAngebote.company.Company;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class ConfirmationToken {

  @SequenceGenerator(
      name = "confirmation_token_sequence",
      sequenceName = "confirmation_token_sequence",
      allocationSize = 1
  )
  @Id
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "confirmation_token_sequence"
  )
  private Long id;

  @Column(nullable = false, unique = true)
  private String token;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
  private LocalDateTime expiresAt;

  private LocalDateTime confirmedAt;

  @ManyToOne
  @JoinColumn(
      nullable = false,
      name = "app_user_id"
  )
  private Company company;

  public ConfirmationToken(String token,
      LocalDateTime createdAt,
      LocalDateTime expiresAt,
      Company company) {
    this.token = token;
    this.createdAt = createdAt;
    this.expiresAt = expiresAt;
    this.company = company;
  }
}
