package com.dajeom.controller;

import com.dajeom.dto.SajuRequest;
import com.dajeom.dto.SajuResponse;
import com.dajeom.service.SajuService;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Objects;

@Controller
public class SajuController {

    private final SajuService sajuService;

    public SajuController(
            SajuService sajuService
    ) {

        this.sajuService =
                Objects.requireNonNull(
                        sajuService,
                        "SajuService가 필요합니다."
                );
    }

    @GetMapping("/saju")
    public String sajuPage() {

        return "saju-consultation";
    }

    @GetMapping("/saju/report")
    public String reportPage() { return "saju"; }

    @PostMapping("/api/saju/calculate")
    @ResponseBody
    public ResponseEntity<SajuResponse> calculate(
            @RequestBody SajuRequest request
    ) {

        SajuResponse response =
                sajuService.calculate(
                        request
                );

        return ResponseEntity.ok(
                response
        );
    }
}
