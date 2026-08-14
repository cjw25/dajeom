package com.dajeom.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 다점 화면 페이지 이동을 담당하는 Controller.
 *
 * API 요청은 각 API Controller가 담당하고,
 * HomeController는 Thymeleaf 화면 반환만 담당한다.
 */
@Controller
public class HomeController {

    /**
     * 메인 페이지.
     *
     * templates/index.html
     */
    @GetMapping("/")
    public String home() {

        return "index";
    }

    /**
     * AI 타로 페이지.
     *
     * templates/tarot.html
     */
    @GetMapping("/tarot")
    public String tarot() {

        return "tarot";
    }

    @GetMapping("/saju")
    public String saju() {

        return "saju";
    }
}