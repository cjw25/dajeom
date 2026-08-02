package com.dajum.service;

import com.dajum.entity.Member;
import com.dajum.entity.MemberStatus;
import com.dajum.repository.MemberRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public CustomUserDetailsService(
            MemberRepository memberRepository
    ) {
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        String normalizedEmail =
                email.trim().toLowerCase(Locale.ROOT);

        Member member = memberRepository
                .findByEmail(normalizedEmail)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "회원 정보를 찾을 수 없습니다."
                        )
                );

        boolean enabled =
                member.getStatus() == MemberStatus.ACTIVE;

        return User.builder()
                .username(member.getEmail())
                .password(member.getPasswordHash())
                .roles(member.getRole().name())
                .disabled(!enabled)
                .build();
    }
}