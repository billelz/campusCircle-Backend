package com.example.campusCircle.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.campusCircle.model.Reports;
import com.example.campusCircle.repository.ReportsRepository;

@Service
public class ReportsService {

    private final ReportsRepository reportsRepository;

    public ReportsService(ReportsRepository reportsRepository) {
        this.reportsRepository = reportsRepository;
    }

    public Reports createReport(Reports report) {
        return reportsRepository.save(report);
    }

    public Reports getReport(Long id) {
        return reportsRepository.findById(id).orElseThrow(() -> new RuntimeException("Report not found"));
    }

    public List<Reports> getAllReports() {
        return reportsRepository.findAll();
    }

    public Reports updateReport(Long id, Reports updated) {
        Reports existing = getReport(id);

        existing.setContentId(updated.getContentId());
        existing.setReporter(updated.getReporter());
        existing.setReason(updated.getReason());
        existing.setStatus(updated.getStatus());
        existing.setResolvedBy(updated.getResolvedBy());
        existing.setResolution(updated.getResolution());

        return reportsRepository.save(existing);
    }

    public void deleteReport(Long id) {
        reportsRepository.deleteById(id);
    }
}
