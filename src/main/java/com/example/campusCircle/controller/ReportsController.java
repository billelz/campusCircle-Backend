package com.example.campusCircle.controller;


import org.springframework.web.bind.annotation.*;
import com.example.campusCircle.model.Reports;
import com.example.campusCircle.service.ReportsService;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportsController {

    private final ReportsService reportsService;

    public ReportsController(ReportsService reportsService) {
        this.reportsService = reportsService;
    }

    @PostMapping
    public Reports create(@RequestBody Reports report) {
        return reportsService.createReport(report);
    }

    @GetMapping("/{id}")
    public Reports get(@PathVariable Long id) {
        return reportsService.getReport(id);
    }

    @GetMapping
    public List<Reports> getAll() {
        return reportsService.getAllReports();
    }

    @PutMapping("/{id}")
    public Reports update(@PathVariable Long id, @RequestBody Reports report) {
        return reportsService.updateReport(id, report);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        reportsService.deleteReport(id);
    }
}
