package com.dajeom.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    @GetMapping("/")
    public String home() { return "index"; }

    @GetMapping("/tarot")
    public String tarot() { return "tarot"; }

    @GetMapping("/mz-doryeong")
    public String mzDoryeong() { return "redirect:/saju"; }

    @GetMapping("/saju/cinematic")
    public String cinematic() { return "redirect:/saju"; }

    @GetMapping({"/saju/start", "/saju/name", "/saju/birth-date"})
    public String legacySaju() { return "redirect:/saju"; }
}
