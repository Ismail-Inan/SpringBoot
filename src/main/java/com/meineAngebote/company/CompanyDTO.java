package com.meineAngebote.company;

public record CompanyDTO(
    Long id,
    String name,
    String email,
    String address,
    String phoneNumber,
    String profileImageId
) {

}