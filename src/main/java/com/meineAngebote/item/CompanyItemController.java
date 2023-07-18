package com.meineAngebote.item;

import static java.time.LocalDateTime.now;
import static java.util.Map.of;
import static org.springframework.http.HttpStatus.OK;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meineAngebote.company.Company;
import com.meineAngebote.domain.HttpResponse;
import com.meineAngebote.item.category.ItemCategoryUtil;
import com.meineAngebote.item.category.ItemCategoryUtil.ItemCategoryNode;
import com.meineAngebote.item.image.CompanyItemImage;
import com.meineAngebote.item.image.CompanyItemImageService;
import com.meineAngebote.security.auth.AuthenticationService;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.Tuple;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/company-item")
@AllArgsConstructor
public class CompanyItemController {

  private final CompanyItemServiceImpl itemService;
  private final CompanyItemImageService itemImageService;
  private final AuthenticationService authenticationService;
  private final CompanyItemDTOMapper companyItemDTOMapper;

  @GetMapping("/public/all/filtered")
  public ResponseEntity<HttpResponse> getItems(@RequestParam("filter") String filterJson,
      @RequestParam Optional<Integer> page,
      @RequestParam Optional<Integer> size) throws InterruptedException, JsonProcessingException {

    ObjectMapper objectMapper = new ObjectMapper();
    CompanyItemFilter filter = objectMapper.readValue(filterJson, CompanyItemFilter.class);

    return ResponseEntity.ok().body(
        HttpResponse.builder()
            .timeStamp(now().toString())
            .data(of("page",
                itemService.getCompanyItems(filter,
                    page.orElse(0), size.orElse(10))))
            .message("Companies Retrieved")
            .status(OK)
            .statusCode(OK.value())
            .build());
  }

  @GetMapping("/public/latest")
  public List<CompanyItem> getLatestItems() {
    return itemService.getLatestCompanyItems();
  }

  @GetMapping("/public/categories")
  public List<ItemCategoryNode> getCompanyItemCategories() {
    return ItemCategoryUtil.getCategories();
  }

  @GetMapping("/public/find/{id}")
  public ResponseEntity<CompanyItemDTO> getItemById(@PathVariable("id") Long id) {
    if (id == null) {
      return ResponseEntity.badRequest().build();
    }

    CompanyItem companyItem = itemService.findCompanyItemById(id);
    return ResponseEntity.ok(companyItemDTOMapper.apply(companyItem));
  }

  @GetMapping("/public/company/{companyId}")
  public ResponseEntity<CompanyItem[]> getItemsByCompanyId(@PathVariable Long companyId) {

    if (companyId == null) {
      return ResponseEntity.badRequest().build();
    }

    CompanyItem[] items = itemService.getItemsByCompanyId(companyId);
    return ResponseEntity.ok(items);
  }

  @PostMapping(value = "/private/create", consumes = "multipart/form-data")
  public ResponseEntity<?> createCompanyItem(@RequestParam("title") String title,
      @RequestParam("description") String description,
      @RequestParam("price") String price,
      @RequestParam("giveAway") boolean giveAway,
      @RequestParam("category") String category,
      @RequestParam MultipartFile[] images) {

    if (Objects.isNull(images)) {
      return ResponseEntity.badRequest().body(
          HttpResponse.builder()
              .timeStamp(now().toString())
              .message("No CompanyItem Retrieved")
              .status(HttpStatus.BAD_REQUEST)
              .statusCode(HttpStatus.BAD_REQUEST.value())
              .build()
      );
    }

    final Company company = authenticationService.getAuthenticatedCompany();

    final CompanyItem companyItem = itemService.saveCompanyItem(
        new CompanyItem(title, description, ItemCategoryUtil.ItemCategory.valueOf(category),
            Float.valueOf(price),
            giveAway, company));

    final Path root = Paths.get("uploads/" + companyItem.getId());
    try {
      Files.createDirectories(root);
    } catch (IOException e) {
      throw new RuntimeException("Could not initialize folder for upload!");
    }

    int ordinal = 0;
    for (MultipartFile file : images) {
      try {
        Path path = root.resolve(file.getOriginalFilename());
        Files.copy(file.getInputStream(), path);

        String imagePath = path.toString();

        final CompanyItemImage companyItemImage = itemImageService.saveCompanyItemImage(
            new CompanyItemImage(imagePath, ordinal++, companyItem));

        if (Objects.isNull(companyItem.getThumbnail())) {
          companyItem.setThumbnail(companyItemImage);
          itemService.updateCompanyItem(companyItem);
        }

      } catch (IOException e) {
        e.printStackTrace();
        return ResponseEntity.badRequest().body(
            HttpResponse.builder()
                .timeStamp(now().toString())
                .message("Could not Save the images")
                .status(HttpStatus.BAD_REQUEST)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build());
      }
    }

    return ResponseEntity.ok().body(
        HttpResponse.builder()
            .timeStamp(now().toString())
            .message("Images Added")
            .status(HttpStatus.OK)
            .statusCode(HttpStatus.OK.value())
            .build());
  }

  @PutMapping("/private/update")
  public ResponseEntity<?> updateCompanyItem(@RequestParam("userItemId") String userItemId,
      @RequestParam("title") String title,
      @RequestParam("description") String description,
      @RequestParam("price") String price,
      @RequestParam("giveAway") boolean giveAway,
      @RequestParam("category") String category,
      @RequestParam MultipartFile[] images) {

    if (Objects.isNull(images)) {
      return ResponseEntity.badRequest().body(
          HttpResponse.builder()
              .timeStamp(now().toString())
              .message("No images Retrieved")
              .status(HttpStatus.BAD_REQUEST)
              .statusCode(HttpStatus.BAD_REQUEST.value())
              .build()
      );
    }

    CompanyItem companyItem = itemService.findCompanyItemById(Long.valueOf(userItemId));

    if (Objects.isNull(companyItem)) {
      return ResponseEntity.badRequest().body(
          HttpResponse.builder()
              .timeStamp(now().toString())
              .message("CompanyItem not found")
              .status(HttpStatus.NOT_FOUND)
              .statusCode(HttpStatus.NOT_FOUND.value())
              .build()
      );
    }

    if (!Objects.equals(authenticationService.getAuthenticatedCompany().getId(),
        companyItem.getOwner().getId())) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    companyItem.setCategory(ItemCategoryUtil.ItemCategory.valueOf(category));
    companyItem.setDescription(description);
    companyItem.setPrice(Float.valueOf(price));
    companyItem.setTitle(title);
    companyItem.setGiveAway(giveAway);

    companyItem = itemService.saveCompanyItem(companyItem);

    final Path root = Paths.get("uploads/" + companyItem.getId());
    try {
      Files.createDirectories(root);
    } catch (IOException e) {
      throw new RuntimeException("Could not initialize folder for upload!");
    }

    int ordinal = 0;
    //delete existing images if they are deselected
    var fileNames = new ArrayList<>();
    for (MultipartFile image : images) {
      fileNames.add(image.getOriginalFilename());
    }
    try (Stream<Path> paths = Files.list(root)) {
      paths.filter(Files::isRegularFile).forEach(path -> {
        if (!fileNames.contains(path.getFileName().toString())) {
          try {
            Files.delete(path);
          } catch (IOException e) {
            throw new RuntimeException("Error deleting file: " + path, e);
          }
        }
      });
    } catch (IOException e) {
      throw new RuntimeException("Error listing files in directory: " + root, e);
    }

    //save uploaded images if needed
    for (MultipartFile file : images) {
      try {
        // Check if file already exists
        String filename = file.getOriginalFilename();
        Path filePath = Paths.get(root + "/" + filename);
        if (Files.exists(filePath)) {
          continue;
        }

        Path path = root.resolve(filename);
        Files.copy(file.getInputStream(), path);

        String imagePath = path.toString();

        final CompanyItemImage companyItemImage = itemImageService.saveCompanyItemImage(
            new CompanyItemImage(imagePath, ordinal++, companyItem));

        if (Objects.isNull(companyItem.getThumbnail())) {
          companyItem.setThumbnail(companyItemImage);
        }

        itemImageService.saveCompanyItemImage(companyItemImage);
      } catch (IOException e) {
        e.printStackTrace();
        return ResponseEntity.badRequest().body(
            HttpResponse.builder()
                .timeStamp(now().toString())
                .message("Could not Save the images")
                .status(HttpStatus.BAD_REQUEST)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build());
      }
    }

    itemService.updateCompanyItem(companyItem);

    return ResponseEntity.ok().body(
        HttpResponse.builder()
            .timeStamp(now().toString())
            .message("Images Added")
            .status(HttpStatus.OK)
            .statusCode(HttpStatus.OK.value())
            .build());
  }

  @DeleteMapping("/private/delete/{id}")
  public ResponseEntity<?> deleteCompanyItem(@PathVariable("id") Long id) {
    itemService.deleteCompanyItem(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @GetMapping("/public/filter/min-max-price")
  public ResponseEntity<Pair<Float, Double>> getMinMaxPrices(
      @RequestParam(name = "category", required = false) String category,
      @RequestParam(name = "searchText", required = false) String searchText) {

    boolean categoryIsSetAndValid = false;
    if (StringUtils.isNotBlank(category)) {
      boolean validCategory = Arrays.stream(ItemCategoryUtil.ItemCategory.values())
          .map(ItemCategoryUtil.ItemCategory::name)
          .anyMatch(category::equalsIgnoreCase);

      if (!validCategory) {
        throw new IllegalArgumentException("Invalid category: " + category);
      }

      categoryIsSetAndValid = true;
    }

    if (categoryIsSetAndValid) {
      if (StringUtils.isEmpty(searchText)) {
        return ResponseEntity.ok(convertToPair(itemService.getMinMaxPricesForCategory(category)));
      } else {
        return ResponseEntity.ok(convertToPair(itemService.getMinMaxPrices(category, searchText)));
      }
    }

    if (StringUtils.isNotEmpty(searchText)) {
      return ResponseEntity.ok(convertToPair(itemService.getMinMaxPricesForSearchText(searchText)));
    }

    return ResponseEntity.ok(convertToPair(itemService.getMinMaxPrices()));
  }

  private Pair<Float, Double> convertToPair(Tuple tuple) {
    return Pair.of(tuple.get(0, Float.class), tuple.get(1, Double.class));
  }

}