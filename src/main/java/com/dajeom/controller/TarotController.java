package com.dajeom.controller;

import com.dajeom.dto.TarotDrawRequest;
import com.dajeom.dto.TarotResponse;
import com.dajeom.dto.TarotSessionCreateRequest;
import com.dajeom.service.TarotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tarot")
@RequiredArgsConstructor
public class TarotController {

    private final TarotService tarotService;

    /**
     * 사용할 수 있는 타로 스프레드 조회.
     */
    @GetMapping("/spreads")
    public List<TarotResponse.SpreadResponse> getSpreads() {

        return tarotService.getSpreads();
    }

    /**
     * 서버에서 78장을 실제로 섞고 선택 세션을 생성한다.
     *
     * 카드의 실제 정체는 이 응답에 포함하지 않는다.
     */
    @PostMapping("/sessions")
    @ResponseStatus(HttpStatus.CREATED)
    public TarotResponse.SessionStartResponse createSession(
            @Valid
            @RequestBody
            TarotSessionCreateRequest request
    ) {
        return tarotService.createSession(request);
    }

    /**
     * 사용자가 선택한 덱 슬롯을 저장한다.
     *
     * 이 단계에서도 카드 앞면은 공개하지 않는다.
     */
    @PostMapping(
            "/sessions/{sessionKey}/draws"
    )
    public TarotResponse.DrawResponse draw(
            @PathVariable
            String sessionKey,

            @Valid
            @RequestBody
            TarotDrawRequest request
    ) {
        return tarotService.draw(
                sessionKey,
                request
        );
    }

    /**
     * 스프레드의 카드 수만큼 선택한 뒤
     * 카드 앞면과 정·역방향을 공개한다.
     */
    @PostMapping(
            "/sessions/{sessionKey}/reveal"
    )
    public TarotResponse.RevealResponse reveal(
            @PathVariable
            String sessionKey
    ) {
        return tarotService.reveal(
                sessionKey
        );
    }
}