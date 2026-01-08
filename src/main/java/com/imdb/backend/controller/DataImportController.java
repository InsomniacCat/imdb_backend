package com.imdb.backend.controller;

import com.imdb.backend.service.DataImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/import")
public class DataImportController {

    @Autowired
    private DataImportService dataImportService;

    @PostMapping("/test")
    public ResponseEntity<String> importTest(@RequestParam String file) {
        return ResponseEntity.ok(dataImportService.importData(file, true));
    }

    @PostMapping("/all")
    public ResponseEntity<String> importAll(@RequestParam String file) {
        return ResponseEntity.ok(dataImportService.importData(file, false));
    }
}
