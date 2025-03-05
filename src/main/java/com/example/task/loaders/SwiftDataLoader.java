package com.example.task.loaders;

import com.example.task.service.SwiftCodeService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;


@Component
public class SwiftDataLoader {

    private SwiftCodeService service;

    public SwiftDataLoader(SwiftCodeService service) {
        this.service = service;
    }

    @PostConstruct
    public void loadData() {
        service.loadExcelData("src/main/resources/Interns_2025_SWIFT_CODES - Sheet1.csv");
    }

}
