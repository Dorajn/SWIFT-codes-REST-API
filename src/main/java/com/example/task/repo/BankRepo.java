package com.example.task.repo;

import com.example.task.model.Bank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BankRepo extends JpaRepository<Bank, String> {
    Optional<Bank> findBySwiftCode(String swiftCode);
    List<Bank> findByCountryCode (String code);
}
