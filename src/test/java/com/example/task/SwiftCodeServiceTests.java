package com.example.task;

import com.example.task.model.Bank;
import com.example.task.model.Branch;
import com.example.task.model.SwiftEntry;
import com.example.task.myExceptions.IllegalOperationException;
import com.example.task.myExceptions.IsoCodeNotFoundException;
import com.example.task.myExceptions.UnitNotFoundException;
import com.example.task.repo.BankRepo;
import com.example.task.repo.BranchRepo;
import com.example.task.requestModels.SwiftEntryRequest;
import com.example.task.service.SwiftCodeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SwiftCodeServiceTests {

    @Mock
    private BankRepo bankRepo;

    @Mock
    private BranchRepo branchRepo;

    @InjectMocks
    private SwiftCodeService swiftCodeService;

    @Test
    void testGetSwiftCodeDetailsForBank() {
        Bank bank = new Bank();
        bank.setId(1L);
        bank.setSwiftCode("ABCDEFGHXXX");
        bank.setName("Test Bank");
        bank.setAddress("Test Address");
        bank.setCountryCode("PL");
        bank.setCountryName("Poland");

        when(bankRepo.findBySwiftCode("ABCDEFGHXXX")).thenReturn(Optional.of(bank));
        when(branchRepo.findByBankId(1L)).thenReturn(Collections.emptyList());

        Map<String, Object> result = swiftCodeService.getSwiftCodeDetails("ABCDEFGHXXX");

        assertEquals("Test Address", result.get("address"));
        assertEquals("Test Bank", result.get("bankName"));
        assertEquals("PL", result.get("countryISO2"));
        assertEquals("Poland", result.get("countryName"));
        assertTrue((Boolean) result.get("isHeadquarter"));
    }

    @Test
    void testGetSwiftCodeDetailsForBranch() {
        Bank bank = new Bank();
        bank.setId(1L);
        bank.setSwiftCode("ABCDEFGHXXX");
        bank.setName("Test Bank");
        bank.setCountryCode("PL");
        bank.setCountryName("Poland");

        Branch branch = new Branch();
        branch.setSwiftCode("BANKPLPL212");
        branch.setBank(bank);
        branch.setAddress("Branch Address");

        when(bankRepo.findBySwiftCode("BANKPLPL212")).thenReturn(Optional.empty());
        when(branchRepo.findBySwiftCode("BANKPLPL212")).thenReturn(Optional.of(branch));

        Map<String, Object> result = swiftCodeService.getSwiftCodeDetails("BANKPLPL212");

        assertEquals("Branch Address", result.get("address"));
        assertEquals("Test Bank", result.get("bankName"));
        assertEquals("PL", result.get("countryISO2"));
        assertEquals("Poland", result.get("countryName"));
        assertFalse((Boolean) result.get("isHeadquarter"));
    }

    @Test
    void testGetSwiftCodeDetailsNotFound() {
        when(bankRepo.findBySwiftCode("UNKNOWN")).thenReturn(Optional.empty());
        when(branchRepo.findBySwiftCode("UNKNOWN")).thenReturn(Optional.empty());

        assertThrows(UnitNotFoundException.class, () -> swiftCodeService.getSwiftCodeDetails("UNKNOWN"));
    }

    @Test
    void testGetAllInfoAboutCountry() {
        Bank bank = new Bank();
        bank.setSwiftCode("ABCDEFGHXXX");
        bank.setName("Test Bank");
        bank.setCountryCode("PL");
        bank.setCountryName("Poland");

        Branch branch = new Branch();
        branch.setSwiftCode("BANKPLPL212");
        branch.setBank(bank);
        branch.setBranchName("Branch Test");

        when(bankRepo.findByCountryCode("PL")).thenReturn(Collections.singletonList(bank));
        when(branchRepo.findByBank_CountryCode("PL")).thenReturn(Collections.singletonList(branch));

        Map<String, Object> result = swiftCodeService.getAllInfoAboutCountry("PL");

        assertEquals("PL", result.get("countryISO2"));
        assertEquals("Poland", result.get("countryName"));
        List<?> swiftCodes = (List<?>) result.get("swiftCodes");
        assertEquals(2, swiftCodes.size());
    }

    @Test
    void testGetAllInfoAboutCountryNotFound() {
        when(bankRepo.findByCountryCode("UNKNOWN")).thenReturn(Collections.emptyList());
        when(branchRepo.findByBank_CountryCode("UNKNOWN")).thenReturn(Collections.emptyList());

        assertThrows(IsoCodeNotFoundException.class, () -> swiftCodeService.getAllInfoAboutCountry("UNKNOWN"));
    }


    @Test
    void testDeleteBankBySwiftCode() {
        Bank bank = new Bank();
        bank.setId(1L);
        bank.setSwiftCode("ABCDEFGHXXX");

        when(bankRepo.findBySwiftCode("ABCDEFGHXXX")).thenReturn(Optional.of(bank));
        when(branchRepo.findByBankId(1L)).thenReturn(Collections.emptyList());

        swiftCodeService.deteleBySwiftCode("ABCDEFGHXXX");

        verify(bankRepo, times(1)).delete(bank);
    }

    @Test
    void testDeleteBankWithBranches() {
        Bank bank = new Bank();
        bank.setId(1L);
        bank.setSwiftCode("ABCDEFGHXXX");

        Branch branch = new Branch();
        branch.setBank(bank);

        when(bankRepo.findBySwiftCode("ABCDEFGHXXX")).thenReturn(Optional.of(bank));
        when(branchRepo.findByBankId(1L)).thenReturn(Collections.singletonList(branch));

        assertThrows(IllegalOperationException.class, () -> swiftCodeService.deteleBySwiftCode("ABCDEFGHXXX"));
    }

    @Test
    void testDeleteBranchBySwiftCode() {
        Branch branch = new Branch();
        branch.setSwiftCode("BANKPLPL212");

        when(branchRepo.findBySwiftCode("BANKPLPL212")).thenReturn(Optional.of(branch));

        swiftCodeService.deteleBySwiftCode("BANKPLPL212");

        verify(branchRepo, times(1)).delete(branch);
    }
}
