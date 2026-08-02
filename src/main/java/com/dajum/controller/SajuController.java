package com.dajum.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/saju")
public class SajuController {

    @GetMapping("/input")
    public String inputForm() {
        return "saju/input";
    }
}