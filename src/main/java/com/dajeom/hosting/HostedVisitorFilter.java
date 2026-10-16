package com.dajeom.hosting;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@ConditionalOnProperty(name="dajeom.hosted.enabled",havingValue="true")
public class HostedVisitorFilter extends OncePerRequestFilter {
    public static final String OWNER=HostedVisitorFilter.class.getName()+".owner";
    public static final String COOKIE="dajeom_visitor";
    private final HostedStateStore store;
    public HostedVisitorFilter(HostedStateStore store) { this.store=store; }
    @Override protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain chain) throws ServletException,IOException {
        response.setHeader("Cache-Control","no-store");
        if(request.getRequestURI().equals("/api/saju/calculate")) { response.sendError(404);return; }
        if(!request.getMethod().equals("GET") && request.getHeader("Origin")!=null) {
            try {
                if(!request.getServerName().equalsIgnoreCase(URI.create(request.getHeader("Origin")).getHost())) {
                    response.sendError(403);return;
                }
            } catch(IllegalArgumentException invalid) { response.sendError(403);return; }
        }
        String token=Arrays.stream(Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]))
            .filter(cookie -> cookie.getName().equals(COOKIE)).map(Cookie::getValue)
            .filter(value -> value.matches("[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}"))
            .findFirst().orElse(null);
        if(token==null) {
            token=UUID.randomUUID().toString();
            response.addHeader("Set-Cookie",ResponseCookie.from(COOKIE,token).httpOnly(true).secure(request.isSecure())
                .sameSite("Lax").path("/").maxAge(86400).build().toString());
        }
        try {
            request.setAttribute(OWNER,HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8))));
        } catch(java.security.NoSuchAlgorithmException impossible) { throw new ServletException(impossible); }
        if(request.getMethod().equals("POST")) {
            String path=request.getRequestURI();
            int personal=0,global=0;
            if(path.equals("/api/saju/consultations/reading")) { personal=10;global=100; }
            if(path.equals("/api/saju/consultations/reading/advance")) { personal=400;global=4000; }
            if(path.equals("/api/saju/consultations/messages") || path.equals("/api/tarot/reading")) { personal=60;global=1000; }
            if(personal>0) {
                String day=java.time.LocalDate.now(java.time.ZoneId.of("Asia/Seoul")).toString();
                String address;
                try { address=HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(request.getRemoteAddr().getBytes(StandardCharsets.UTF_8))); }
                catch(java.security.NoSuchAlgorithmException impossible) { throw new ServletException(impossible); }
                try {
                    if(!store.consume(day+":"+path+":ip:"+address,personal) || !store.consume(day+":"+path+":all",global)) {
                        response.setStatus(429);response.setContentType("application/json;charset=UTF-8");
                        response.getWriter().write("{\"message\":\"오늘 이용 가능한 요청 횟수를 초과했습니다. 잠시 후 다시 이용해주세요.\"}");return;
                    }
                } catch(IllegalStateException unavailable) {
                    response.setStatus(503);response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"message\":\"저장소 연결을 확인하지 못했습니다. 잠시 후 다시 시도해주세요.\"}");return;
                }
            }
        }
        chain.doFilter(request,response);
    }
    @Override protected boolean shouldNotFilter(HttpServletRequest request) { return !request.getRequestURI().startsWith("/api/"); }
}
