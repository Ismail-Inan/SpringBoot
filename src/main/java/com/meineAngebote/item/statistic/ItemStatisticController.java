package com.meineAngebote.item.statistic;

import com.meineAngebote.item.CompanyItem;
import com.meineAngebote.item.CompanyItemServiceImpl;
import com.meineAngebote.security.auth.AuthenticationService;
import java.util.Objects;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/item-statistic")
public class ItemStatisticController {

  private final ItemStatisticService itemStatisticService;
  private final CompanyItemServiceImpl userItemService;
  private final AuthenticationService authenticationService;

  @PostMapping("/public/{itemId}/increase-visit")
  public ResponseEntity<Void> increaseItemVisits(@PathVariable String itemId) {

    if (itemId == null || !itemId.matches("\\d+")) {
      return ResponseEntity.badRequest().build();
    }

    itemStatisticService.increaseItemVisits(itemId);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/private/{itemId}/views")
  public ResponseEntity<Long> getItemViewsCount(@PathVariable String itemId) {

    if (itemId == null || !itemId.matches("\\d+")) {
      return ResponseEntity.badRequest().build();
    }

    CompanyItem companyItem = userItemService.findCompanyItemById(Long.valueOf(itemId));

    if (Objects.isNull(companyItem)) {
      return ResponseEntity.notFound().build();
    }

    if (!Objects.equals(authenticationService.getAuthenticatedCompany().getId(),
        companyItem.getOwner().getId())) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    return ResponseEntity.ok(itemStatisticService.getItemViewsCount(itemId));
  }

}