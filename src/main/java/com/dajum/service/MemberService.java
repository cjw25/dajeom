package com.dajum.service;

import com.dajum.dto.SignupRequest;
import com.dajum.entity.Member;
import com.dajum.exception.DuplicateEmailException;
import com.dajum.exception.PasswordMismatchException;
import com.dajum.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(
            MemberRepository memberRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Long signup(SignupRequest request) {
        String email = normalizeEmail(request.getEmail());

        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new PasswordMismatchException();
        }

        if (memberRepository.existsByEmail(email)) {
            throw new DuplicateEmailException();
        }

        String encodedPassword =
                passwordEncoder.encode(request.getPassword());

        Member member = Member.create(
                email,
                encodedPassword,
                request.getNickname().trim()
        );

        return memberRepository.save(member).getId();
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}