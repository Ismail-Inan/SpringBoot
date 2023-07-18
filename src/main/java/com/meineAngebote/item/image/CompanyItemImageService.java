package com.meineAngebote.item.image;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class CompanyItemImageService {

  private final CompanyItemImageRepository companyItemImageRepository;

  public CompanyItemImageService(CompanyItemImageRepository companyItemImageRepository) {
    this.companyItemImageRepository = companyItemImageRepository;
  }

  public CompanyItemImage saveCompanyItemImage(@NonNull CompanyItemImage image) {
    return companyItemImageRepository.save(image);
  }

  public List<CompanyItemImageDTO> getAllImagesByItem(Long companyItemId) {
    List<CompanyItemImage> companyItemImages = companyItemImageRepository.findByItemId(
        companyItemId);
    return mapToDTOList(companyItemImages);
  }

  private List<CompanyItemImageDTO> mapToDTOList(List<CompanyItemImage> companyItemImages) {
    return companyItemImages.stream()
        .map(this::mapToDTO)
        .collect(Collectors.toList());
  }

  private CompanyItemImageDTO mapToDTO(CompanyItemImage companyItemImage) {
    return new CompanyItemImageDTO(
        companyItemImage.getId(),
        companyItemImage.getImageUrl(),
        companyItemImage.getOrdinal()
    );
  }
}
