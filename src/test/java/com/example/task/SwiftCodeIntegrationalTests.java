package com.example.task;

import com.example.task.model.Bank;
import com.example.task.repo.BankRepo;
import com.example.task.repo.BranchRepo;
import com.example.task.service.SwiftCodeService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class SwiftCodeIntegrationalTests {

    @Autowired
    private SwiftCodeService swiftCodeService;

    @Autowired
    private BankRepo bankRepo;

    @Autowired
    private BranchRepo branchRepo;

    @Test
    void shouldSaveAndRetrieveSwiftCodeDetails() {
        Bank bank = new Bank();
        bank.setSwiftCode("BANKXXX");
        bank.setName("Test Bank");
        bank.setAddress("Test Address");
        bank.setCountryCode("PL");
        bank.setCountryName("Poland");

        bankRepo.save(bank);

        Map<String, Object> result = swiftCodeService.getSwiftCodeDetails("BANKXXX");

        assertEquals("Test Address", result.get("address"));
        assertEquals("Test Bank", result.get("bankName"));
        assertEquals("PL", result.get("countryISO2"));
        assertEquals("Poland", result.get("countryName"));
        assertTrue((Boolean) result.get("isHeadquarter"));
    }

}
