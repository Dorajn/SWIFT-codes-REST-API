package com.example.task.repo;

import com.example.task.model.Branch;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BranchRepo extends JpaRepository<Branch, String> {
    List<Branch> findByBankId(Long bankId);
    Optional<Branch> findBySwiftCode(String swiftCode);
    List<Branch> findByBank_CountryCode(String bankCountryCode);
}
