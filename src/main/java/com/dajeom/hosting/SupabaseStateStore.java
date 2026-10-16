package com.dajeom.hosting;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

@Component
@ConditionalOnProperty(name="dajeom.hosted.enabled",havingValue="true")
public class SupabaseStateStore implements HostedStateStore {
    private final URI base;
    private final String secret;
    private final HttpClient client=HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    private final JsonMapper json=JsonMapper.builder().build();

    public SupabaseStateStore(@Value("${SUPABASE_URL:}")String url,@Value("${SUPABASE_SECRET_KEY:}")String secret) {
        if(url.isBlank() || secret.isBlank())throw new IllegalStateException("Supabase URL과 서버 비밀키 설정이 필요합니다.");
        base=URI.create(url.replaceAll("/+$","")+"/rest/v1/");
        if(!"https".equals(base.getScheme()) && !java.util.Set.of("localhost","127.0.0.1").contains(base.getHost()))
            throw new IllegalArgumentException("Supabase 연결에는 HTTPS가 필요합니다.");
        this.secret=secret;
    }

    private JsonNode call(String method,String path,Object body) {
        var request=HttpRequest.newBuilder(base.resolve(path)).timeout(Duration.ofSeconds(10))
            .header("apikey",secret).header("Content-Type","application/json").header("Accept","application/json");
        // Modern sb_secret keys go in apikey, not the JWT Authorization header.
        if(secret.startsWith("eyJ"))request.header("Authorization","Bearer "+secret);
        request.method(method,body==null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(json.writeValueAsString(body)));
        try {
            var response=client.send(request.build(),HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if(response.statusCode()<200 || response.statusCode()>=300)
                throw new IllegalStateException("상담 저장소 요청에 실패했습니다. HTTP "+response.statusCode());
            return response.body().isBlank() ? json.nullNode() : json.readTree(response.body());
        } catch(InterruptedException failure) {
            Thread.currentThread().interrupt();throw new IllegalStateException("상담 저장 요청이 중단됐습니다.",failure);
        } catch(java.io.IOException failure) { throw new IllegalStateException("상담 저장소에 연결하지 못했습니다.",failure); }
    }

    private String encode(String value) { return URLEncoder.encode(value,StandardCharsets.UTF_8); }
    @Override public String get(String key) {
        var rows=call("GET","dajeom_state?select=value&key=eq."+encode(key)+"&expires_at=gt."+encode(Instant.now().toString()),null);
        return rows.isArray() && !rows.isEmpty() ? rows.get(0).get("value").toString() : null;
    }
    @Override public boolean acquire(String key,String owner,int seconds) {
        return call("POST","rpc/dajeom_lock_acquire",Map.of("p_key",key,"p_owner",owner,"p_seconds",seconds)).asBoolean();
    }
    @Override public boolean commit(String key,String value,Instant expires,String lockKey,String owner) {
        return call("POST","rpc/dajeom_state_commit",Map.of("p_key",key,"p_value",json.readTree(value),
            "p_expires_at",expires.toString(),"p_lock_key",lockKey,"p_owner",owner)).asBoolean();
    }
    @Override public void release(String key,String owner) {
        call("POST","rpc/dajeom_lock_release",Map.of("p_key",key,"p_owner",owner));
    }
    @Override public boolean delete(String key,String lockKey,String owner) {
        return call("POST","rpc/dajeom_state_delete",Map.of("p_key",key,"p_lock_key",lockKey,"p_owner",owner)).asBoolean();
    }
    @Override public boolean consume(String bucket,int limit) {
        return call("POST","rpc/dajeom_budget_consume",Map.of("p_key","budget:"+bucket,"p_limit",limit)).asBoolean();
    }
}
