package com.example.task.service;

import com.example.task.model.Bank;
import com.example.task.model.Branch;
import com.example.task.model.SwiftEntry;
import com.example.task.myExceptions.IllegalOperationException;
import com.example.task.myExceptions.UnitNotFoundException;
import com.example.task.requestModels.SwiftEntryRequest;
import com.example.task.repo.BankRepo;
import com.example.task.repo.BranchRepo;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SwiftCodeService {

    private BankRepo bankRepo;
    private BranchRepo branchRepo;

    public SwiftCodeService(BankRepo bankRepo, BranchRepo branchRepo) {
        this.bankRepo = bankRepo;
        this.branchRepo = branchRepo;
    }

    @Transactional
    public void loadExcelData(String path) {

        List<Bank> banks = new ArrayList<>();
        List<Branch> branches = new ArrayList<>();
        String[] nextLine;

        try(CSVReader reader = new CSVReader(new FileReader(path))){
            reader.readNext();

            while ((nextLine = reader.readNext()) != null) {
                String swiftCode = nextLine[1];
                if(swiftCode.endsWith("XXX")) {
                    banks.add(getBankEntity(nextLine));
                }
            }
            bankRepo.saveAll(banks);
        }
        catch (IOException | CsvException e) {
            e.printStackTrace();
        }

        try(CSVReader reader = new CSVReader(new FileReader(path))){
            reader.readNext();

            while ((nextLine = reader.readNext()) != null) {
                String swiftCode = nextLine[1];

                if(!swiftCode.endsWith("XXX")){
                    String bankSwiftCode = swiftCode.substring(0, 8) + "XXX";
                    Optional<Bank> bank = banks.stream()
                            .filter(b -> b.getSwiftCode().equals(bankSwiftCode))
                            .findFirst();

                    if (bank.isPresent()) {
                        branches.add(getBranchEntity(nextLine, bank.get()));
                    } else {
                        System.out.println("Brak banku centralnego dla oddziału: " + swiftCode);
                    }
                }
            }
            branchRepo.saveAll(branches);
        }
        catch (IOException | CsvException e) {
            e.printStackTrace();
        }

    }

    private Bank getBankEntity(String[] row){
        Bank bank = new Bank();
        bank.setSwiftCode(row[1]);
        bank.setName(row[3]);
        bank.setAddress(row[4]);
        bank.setTownName(row[5]);
        bank.setCountryName(row[6]);
        bank.setCountryCode(row[0]);
        bank.setTimeZone(row[7]);
        return bank;
    }

    private Branch getBranchEntity(String[] row, Bank bank) {
        Branch branch = new Branch();
        branch.setSwiftCode(row[1]);
        branch.setBank(bank);
        branch.setBranchName(row[3]);
        branch.setAddress(row[4]);
        branch.setTownName(row[5]);
        return branch;
    }

    public ResponseEntity<?> getSwiftCodeDetaild(String swiftCode) {
        Optional<Bank> bankOpt = bankRepo.findBySwiftCode(swiftCode);

        if(bankOpt.isPresent()){
            Bank bank = bankOpt.get();
            Long bankId = bank.getId();
            List<Branch> branches = branchRepo.findByBankId(bankId);

            Map<String, Object> response = new HashMap<>();
            response.put("address", bank.getAddress());
            response.put("bankName", bank.getName());
            response.put("countryISO2", bank.getCountryCode());
            response.put("countryName", bank.getCountryName());
            response.put("isHeadquarter", true);
            response.put("swiftCode", bank.getSwiftCode());

            List<Map<String, Object>> branchList = branches.stream().map(branch -> {
                Map<String, Object> branchMap = new HashMap<>();
                branchMap.put("address", branch.getAddress());
                branchMap.put("bankName", branch.getBranchName());
                branchMap.put("countryISO2", bank.getCountryCode());
                branchMap.put("isHeadquarter", false);
                branchMap.put("swiftCode", branch.getSwiftCode());
                return branchMap;
            }).collect(Collectors.toList());

            response.put("branches", branchList);

            return new ResponseEntity<>(
              response,
              HttpStatus.OK
            );
        }

        Optional<Branch> branchOpt = branchRepo.findBySwiftCode(swiftCode);
        if (branchOpt.isPresent()) {
            Branch branch = branchOpt.get();
            Bank headquaters = branch.getBank();

            Map<String, Object> response = new HashMap<>();
            response.put("address", branch.getAddress());
            response.put("bankName", headquaters.getName());
            response.put("countryISO2", headquaters.getCountryCode());
            response.put("countryName", headquaters.getCountryName());
            response.put("isHeadquarter", false);
            response.put("swiftCode", branch.getSwiftCode());
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("SWIFT code not found");
    }

    public ResponseEntity<?> getAllInfoAboutCountry(String isoCode) {
        List<Branch> branches = branchRepo.findByBank_CountryCode(isoCode);
        List<Bank> banks = bankRepo.findByCountryCode(isoCode);

        Map<String, Object> response = new HashMap<>();
        String countryName = branches.getFirst().getBank().getCountryName();

        List<Map<String, Object>> swiftCodes = branches.stream().map(branch -> {
            HashMap<String, Object> map = new HashMap<>();
            map.put("address", branch.getAddress());
            map.put("bankName", branch.getBranchName());
            map.put("countryISO2", isoCode);
            map.put("isHeadquarter", false);
            map.put("swiftCode", branch.getSwiftCode());
            return map;

        }).collect(Collectors.toList());

        banks.forEach(bank -> {
            HashMap<String, Object> map = new HashMap<>();
            map.put("address", bank.getAddress());
            map.put("bankName", bank.getName());
            map.put("countryISO2", isoCode);
            map.put("isHeadquarter", true);
            map.put("swiftCode", bank.getSwiftCode());
            swiftCodes.add(map);
        });

        response.put("swiftCodes", swiftCodes);
        response.put("countryISO2", isoCode);
        response.put("countryName", countryName);

        return new ResponseEntity<>(
            response, HttpStatus.OK
        );
    }

    public boolean addSwiftCode(SwiftEntryRequest request) {
        SwiftEntry entry = mapRequest(request);

        if (entry.isHeadquarter()) {
            Bank bank = bankRepo.findBySwiftCode(entry.getSwiftCode())
                    .orElseGet(Bank::new);

            bank.setSwiftCode(entry.getSwiftCode());
            bank.setName(entry.getBankName());
            bank.setAddress(entry.getAddress());
            bank.setCountryCode(entry.getCountryISO2());
            bank.setCountryName(entry.getCountryName());
            bank.setTimeZone(null);
            bankRepo.save(bank);
            return true;
        }
        else {
            String headquarterSwiftCode = entry.getSwiftCode().substring(0, 8) + "XXX";
            Optional<Bank> bankOpt = bankRepo.findBySwiftCode(headquarterSwiftCode);

            if(bankOpt.isPresent()){
                Bank bank = bankOpt.get();
                Branch branch = new Branch();
                branch.setAddress(entry.getAddress());
                branch.setBank(bank);
                branch.setSwiftCode(entry.getSwiftCode());
                branch.setBranchName(entry.getBankName());

                branchRepo.save(branch);
                return true;
            }

         }
        return false;
    }

    private SwiftEntry mapRequest(SwiftEntryRequest request) {
        SwiftEntry entry = new SwiftEntry();
        entry.setAddress(request.address());
        entry.setSwiftCode(request.swiftCode());
        entry.setBankName(request.bankName());
        entry.setHeadquarter(request.isHeadquarter().equals("true"));
        entry.setCountryISO2(request.countryISO2());
        entry.setCountryName(request.countryName());
        return entry;
    }

    public void deteleBySwiftCode(String swiftCode) {

        if(swiftCode.endsWith("XXX")){
            Bank bank = bankRepo.findBySwiftCode(swiftCode)
                    .orElseThrow(() -> new UnitNotFoundException("Bank with Swift Code " + swiftCode + " not found."));

            List<Branch> branches = branchRepo.findByBankId(bank.getId());
            if(!branches.isEmpty()){
                throw new IllegalOperationException("Bank with Swift Code " + swiftCode + " can not be deleted, because it has branches.");
            }
            bankRepo.delete(bank);
        }
        else{
            Branch branch = branchRepo.findBySwiftCode(swiftCode)
                    .orElseThrow(() -> new UnitNotFoundException("Branch with Swift Code " + swiftCode + " not found."));

            branchRepo.delete(branch);
        }
    }
}
