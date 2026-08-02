package com.dajum.controller;

import com.dajum.dto.SignupRequest;
import com.dajum.exception.DuplicateEmailException;
import com.dajum.exception.PasswordMismatchException;
import com.dajum.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final MemberService memberService;

    public AuthController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/signup")
    public String signupForm(
            org.springframework.ui.Model model
    ) {
        model.addAttribute(
                "signupRequest",
                new SignupRequest()
        );

        return "auth/signup";
    }

    @PostMapping("/signup")
    public String signup(
            @Valid @ModelAttribute
            SignupRequest signupRequest,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "auth/signup";
        }

        try {
            memberService.signup(signupRequest);

        } catch (PasswordMismatchException exception) {
            bindingResult.rejectValue(
                    "passwordConfirm",
                    "password.mismatch",
                    exception.getMessage()
            );

            return "auth/signup";

        } catch (DuplicateEmailException exception) {
            bindingResult.rejectValue(
                    "email",
                    "email.duplicate",
                    exception.getMessage()
            );

            return "auth/signup";
        }

        redirectAttributes.addFlashAttribute(
                "signupSuccess",
                "회원가입이 완료되었습니다. 로그인해 주세요."
        );

        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "auth/login";
    }
}