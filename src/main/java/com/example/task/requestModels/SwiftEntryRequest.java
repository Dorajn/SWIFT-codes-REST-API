package com.example.task.requestModels;

public record SwiftEntryRequest(String address,
         String bankName,
         String countryISO2,
         String countryName,
         String isHeadquarter,
         String swiftCode) {

}
