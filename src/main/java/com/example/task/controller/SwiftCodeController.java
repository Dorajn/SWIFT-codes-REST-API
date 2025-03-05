package com.example.task.controller;

import com.example.task.myExceptions.IllegalOperationException;
import com.example.task.myExceptions.UnitNotFoundException;
import com.example.task.requestModels.SwiftEntryRequest;
import com.example.task.service.SwiftCodeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
public class SwiftCodeController {

    private SwiftCodeService service;

    public SwiftCodeController(SwiftCodeService service) {
        this.service = service;
    }

    @GetMapping("/swift-codes/{swiftCode}")
    public ResponseEntity<?> getSwiftCodeDetails(@PathVariable String swiftCode){
        return service.getSwiftCodeDetaild(swiftCode);
    }

    @GetMapping("/swift-codes/country/{IsoCode}")
    public ResponseEntity<?> getAllInfoAboutCountry(@PathVariable String IsoCode){
        return service.getAllInfoAboutCountry(IsoCode);
    }

    @PostMapping("/swift-codes")
    public ResponseEntity<?> addSwiftCode(@RequestBody SwiftEntryRequest request) {
        if(service.addSwiftCode(request))
            return ResponseEntity.ok("SWIFT code added successfully!");
        else
            return new ResponseEntity<>("Wrong parameters.", HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/swift-codes/{swiftCode}")
    public ResponseEntity<?> deleteBySwiftCode(@PathVariable String swiftCode){
        try{
            service.deteleBySwiftCode(swiftCode);
            return ResponseEntity.ok("Unit deleted");
        }
        catch (IllegalOperationException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (UnitNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
