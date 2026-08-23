package com.dajeom.controller;

import com.dajeom.dto.TarotReadingRequest;
import com.dajeom.dto.TarotReadingResponse;
import com.dajeom.service.TarotReadingService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tarot")
public class TarotApiController {


    private final TarotReadingService
            tarotReadingService;


    public TarotApiController(
            TarotReadingService tarotReadingService
    ) {

        this.tarotReadingService =
                tarotReadingService;
    }


    @PostMapping("/reading")
    public ResponseEntity<TarotReadingResponse>
    reading(
            @RequestBody
            TarotReadingRequest request
    ) {

        if (
                request.cards() == null
                        ||
                        request.cards().size() != 3
        ) {

            return ResponseEntity
                    .badRequest()
                    .build();
        }


        TarotReadingResponse response =
                tarotReadingService
                        .createReading(
                                request
                        );


        return ResponseEntity.ok(
                response
        );
    }
}