package com.meineAngebote.company;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import java.util.Collection;
import java.util.Collections;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@Entity
public class Company implements UserDetails {

  @Id
  @SequenceGenerator(
      name = "company_id_sequence",
      sequenceName = "company_id_sequence",
      allocationSize = 1
  )
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "company_id_sequence"
  )
  private Long id;
  @Column(length = 65)
  private String name;
  @Column(nullable = false)
  private String email;
  private String address;
  private String phoneNumber;
  private String website;
  @Column(nullable = false, length = 128)
  private String password;
  @Enumerated(EnumType.STRING)
  private AppUserRole appUserRole;
  private Boolean locked = false;
  private Boolean enabled = false;
  @Column(unique = true)
  private String profileImageId;

  public Company(String email,
      String password,
      AppUserRole appUserRole) {
    this.email = email;
    this.password = password;
    this.appUserRole = appUserRole;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singletonList(new SimpleGrantedAuthority(appUserRole.name()));
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return !locked;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }

}