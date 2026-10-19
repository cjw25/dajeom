package com.dajeom.hosting;

import com.dajeom.service.*;
import com.dajeom.service.SajuConsultationService.*;
import com.dajeom.service.SajuReadingService.*;
import java.time.Instant;
import java.util.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

@Service
@ConditionalOnProperty(name="dajeom.hosted.enabled",havingValue="true")
public class HostedSajuService {
    public record State(ReadingStart request,Instant expires,List<Chapter> chapters,Chapter concern,
                        View view,String status,String message) {
        public State { chapters=List.copyOf(chapters); }
    }
    public record Progress(String status,int completed,int total,String message,View view) {}
    private final HostedStateStore store;
    private final SajuService saju;
    private final SajuReadingService readings;
    private final SajuConsultationService consultations;
    private final JsonMapper json=JsonMapper.builder().build();

    public HostedSajuService(HostedStateStore store,SajuService saju,SajuReadingService readings,SajuConsultationService consultations) {
        this.store=store;this.saju=saju;this.readings=readings;this.consultations=consultations;
    }
    private String key(String visitor) {
        if(visitor==null || !visitor.matches("[0-9a-f]{64}"))throw new Problem(HttpStatus.UNAUTHORIZED,"브라우저 쿠키를 허용한 뒤 다시 시도해주세요.");
        return "session:"+visitor;
    }
    private State load(String key) {
        String value=store.get(key);
        if(value==null)return null;
        State state=json.readValue(value,State.class);
        return state.expires().isAfter(Instant.now()) ? state : null;
    }
    private State require(String key) {
        State state=load(key);
        if(state==null)throw new Problem(HttpStatus.GONE,"풀이가 만료됐습니다. 새 풀이를 시작해주세요.");
        return state;
    }
    private int total(State state) { return state.request().concern()==null || state.request().concern().isBlank()?18:19; }
    private Progress progress(State state) {
        return new Progress(state.status(),state.chapters().size()+(state.concern()!=null?1:0),total(state),state.message(),state.view());
    }
    private void save(String key,State state,String owner) {
        if(!store.commit(key,json.writeValueAsString(state),state.expires(),"lock:"+key,owner))
            throw new Problem(HttpStatus.CONFLICT,"이전 요청의 처리 시간이 지났습니다. 진행 상태를 다시 확인해주세요.");
    }
    private void release(String key,String owner) {
        try { store.release("lock:"+key,owner); }
        catch(IllegalStateException ignored) { System.getLogger(getClass().getName()).log(System.Logger.Level.WARNING,"Could not release hosted lease; it will expire automatically."); }
    }
    private String acquire(String key) {
        String owner=UUID.randomUUID().toString();
        if(!store.acquire("lock:"+key,owner,300))throw new Problem(HttpStatus.CONFLICT,"앞선 요청을 처리하고 있습니다. 잠시 후 다시 확인해주세요.");
        return owner;
    }
    public Progress start(ReadingStart request,String visitor) {
        String key=key(visitor),owner=acquire(key);
        try {
            State existing=load(key);
            if(existing!=null && !existing.status().equals("COMPLETE")) {
                if(!existing.request().equals(request))throw new Problem(HttpStatus.CONFLICT,"작성 중인 풀이가 있습니다. 이어서 작성하거나 먼저 지워주세요.");
                return progress(existing);
            }
            saju.prepareConsultation(request.birth()); // Validate before persisting or making any paid request.
            var state=new State(request,Instant.now().plusSeconds(3600),List.of(),null,null,"PENDING","풀이 작성을 시작합니다.");
            save(key,state,owner);return progress(state);
        } finally { release(key,owner); }
    }
    public Progress status(String visitor) {
        State state=load(key(visitor));return state==null ? null : progress(state);
    }
    public Progress advance(String visitor) {
        String key=key(visitor),owner=acquire(key);
        try {
            State state=require(key);
            if(state.status().equals("COMPLETE"))return progress(state);
            int step=state.chapters().size()+(state.concern()!=null?1:0);
            try {
                var prepared=saju.prepareConsultation(state.request().birth());
                Chapter chapter=readings.generateStep(prepared,state.request().name(),state.request().concern(),step);
                var chapters=new ArrayList<>(state.chapters());
                Chapter concern=state.concern();
                if(step<18)chapters.add(chapter);else concern=chapter;
                boolean complete=step+1==total(state);
                View view=complete ? consultations.savedView(state.request(),new Reading(chapters,concern),List.of(),Topic.SELF,Depth.STANDARD,0) : null;
                var next=new State(state.request(),state.expires(),chapters,concern,view,complete?"COMPLETE":"PENDING",
                    complete?"풀이가 완성됐습니다.":"풀이를 차례로 작성하고 있습니다.");
                save(key,next,owner);return progress(next);
            } catch(IllegalStateException failure) {
                var retry=new State(state.request(),state.expires(),state.chapters(),state.concern(),null,"RETRY",
                    "이 장을 완성하지 못했습니다. 작성된 장은 보관했습니다. 이어서 다시 시도해주세요.");
                save(key,retry,owner);return progress(retry);
            }
        } finally { release(key,owner); }
    }
    public View current(String visitor) { State state=load(key(visitor));return state==null?null:state.view(); }
    public View followup(Followup request,String visitor) {
        String key=key(visitor),owner=acquire(key);
        try {
            State state=require(key);
            if(state.view()==null)throw new Problem(HttpStatus.CONFLICT,"종합풀이를 완성한 뒤 질문해주세요.");
            View previous=state.view();
            if(previous.turnsRemaining()<=0)throw new Problem(HttpStatus.TOO_MANY_REQUESTS,"추가 질문을 모두 사용했습니다.");
            String answer=consultations.savedAnswer(state.request(),previous.messages(),request);
            var messages=new ArrayList<>(previous.messages());
            messages.add(new Message("user",request.message().strip()));messages.add(new Message("assistant",answer));
            var view=new View(previous.name(),request.topic(),request.depth(),previous.pillars(),previous.fiveElements(),List.copyOf(messages),
                previous.turnsRemaining()-1,previous.readingAvailable(),previous.availableTopics(),previous.reading());
            save(key,new State(state.request(),state.expires(),state.chapters(),state.concern(),view,"COMPLETE",state.message()),owner);
            return view;
        } finally { release(key,owner); }
    }
    public void clear(String visitor) {
        String key=key(visitor),owner=acquire(key);
        try {
            if(!store.delete(key,"lock:"+key,owner))throw new Problem(HttpStatus.CONFLICT,"진행 상태를 다시 확인해주세요.");
        } finally { release(key,owner); }
    }
}
