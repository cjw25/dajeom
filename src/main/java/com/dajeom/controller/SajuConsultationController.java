package com.dajeom.controller;

import com.dajeom.dto.SajuResponse;
import com.dajeom.service.SajuConsultationService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/saju/consultations")
public class SajuConsultationController {
    private final SajuConsultationService consultations;
    public SajuConsultationController(SajuConsultationService consultations) { this.consultations=consultations; }
    @PostMapping public SajuConsultationService.View start(@Valid @RequestBody SajuConsultationService.Start request, HttpSession session) {
        return consultations.start(request,session);
    }
    @PostMapping("/reading") public SajuConsultationService.View reading(@Valid @RequestBody SajuConsultationService.ReadingStart request,HttpSession session) {
        return consultations.startReading(request,session);
    }
    @PostMapping("/messages") public SajuConsultationService.View followup(@Valid @RequestBody SajuConsultationService.Followup request,HttpSession session) {
        return consultations.followup(request,session);
    }
    @GetMapping("/current") public ResponseEntity<SajuConsultationService.View> current(HttpSession session) {
        var view=consultations.current(session);
        return view == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(view);
    }
    @PostMapping("/report") public SajuResponse report(HttpSession session) { return consultations.report(session); }
    @DeleteMapping("/current") public ResponseEntity<Void> clear(HttpSession session) {
        consultations.clear(session); return ResponseEntity.noContent().build();
    }
}
