package com.dajeom.controller;

import com.dajeom.dto.SajuResponse;
import com.dajeom.service.SajuConsultationService;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import com.dajeom.hosting.*;
import org.springframework.beans.factory.ObjectProvider;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/saju/consultations")
public class SajuConsultationController {
    private final SajuConsultationService consultations;
    private final HostedSajuService hosted;
    public SajuConsultationController(SajuConsultationService consultations,ObjectProvider<HostedSajuService> hosted) {
        this.consultations=consultations;this.hosted=hosted.getIfAvailable();
    }
    private String visitor(HttpServletRequest request) { return (String)request.getAttribute(HostedVisitorFilter.OWNER); }
    @PostMapping public SajuConsultationService.View start(@Valid @RequestBody SajuConsultationService.Start request, HttpSession session) {
        if(hosted!=null)throw new SajuConsultationService.Problem(org.springframework.http.HttpStatus.NOT_FOUND,"종합풀이 화면에서 시작해주세요.");
        return consultations.start(request,session);
    }
    @PostMapping("/reading") public ResponseEntity<?> reading(@Valid @RequestBody SajuConsultationService.ReadingStart request,HttpSession session,HttpServletRequest http) {
        return hosted==null ? ResponseEntity.ok(consultations.startReading(request,session)) : ResponseEntity.accepted().body(hosted.start(request,visitor(http)));
    }
    @GetMapping("/reading/progress") public ResponseEntity<?> progress(HttpServletRequest http) {
        var progress=hosted==null?null:hosted.status(visitor(http));
        return progress==null?ResponseEntity.noContent().build():ResponseEntity.ok(progress);
    }
    @PostMapping("/reading/advance") public ResponseEntity<?> advance(HttpServletRequest http) {
        if(hosted==null)return ResponseEntity.notFound().build();
        return ResponseEntity.ok(hosted.advance(visitor(http)));
    }
    @PostMapping("/messages") public SajuConsultationService.View followup(@Valid @RequestBody SajuConsultationService.Followup request,HttpSession session,HttpServletRequest http) {
        return hosted==null?consultations.followup(request,session):hosted.followup(request,visitor(http));
    }
    @GetMapping("/current") public ResponseEntity<SajuConsultationService.View> current(HttpSession session,HttpServletRequest http) {
        var view=hosted==null?consultations.current(session):hosted.current(visitor(http));
        return view == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(view);
    }
    @PostMapping("/report") public SajuResponse report(HttpSession session) {
        if(hosted!=null)throw new SajuConsultationService.Problem(org.springframework.http.HttpStatus.NOT_FOUND,"종합풀이 화면을 이용해주세요.");
        return consultations.report(session);
    }
    @DeleteMapping("/current") public ResponseEntity<Void> clear(HttpSession session,HttpServletRequest http) {
        if(hosted==null)consultations.clear(session);else hosted.clear(visitor(http)); return ResponseEntity.noContent().build();
    }
}
