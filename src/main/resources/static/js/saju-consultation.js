"use strict";

(() => {
    const $ = id => document.getElementById(id);
    const API = "/api/saju/consultations";
    const STORAGE_KEY = "dajeom.savedConsultations.v1";
    const TOPICS = {
        SELF: {label:"나 알아보기", question:"나는 어떤 상황에서 나답게 빛날 수 있을까요?", next:["내 강점을 일상에서 어떻게 살리면 좋을까요?", "반복되는 실수를 줄이려면 어떻게 해야 할까요?"]},
        LOVE: {label:"연애·관계", question:"연애할 때 비슷한 갈등을 반복해요. 내 패턴이 궁금해요.", next:["마음을 표현할 때 어떤 점을 신경 쓰면 좋을까요?", "관계에서 내 기준을 어떻게 세우면 좋을까요?"]},
        CAREER: {label:"취업·이직", question:"새로운 일을 해보고 싶어요. 나에게 맞는 일하는 방식은 뭘까요?", next:["지금 직장에서 내 강점을 어떻게 보여주면 좋을까요?", "새로운 일을 시작하기 전에 무엇을 준비하면 좋을까요?"]},
        WEALTH: {label:"돈·일상", question:"돈을 모으고 싶은데 비슷한 소비 패턴이 반복돼요.", next:["내 소비 습관에서 먼저 돌아볼 점은 뭘까요?", "돈을 관리하는 작은 습관을 하나 추천해주세요."]},
        RELATIONSHIPS: {label:"인간관계", question:"가까운 사람에게 내 마음을 전하는 게 어려워요.", next:["가까운 사람과 편하게 거리를 조절하는 방법은 뭘까요?", "내 의견을 표현할 때 어떤 점을 연습하면 좋을까요?"]},
        WELLBEING: {label:"마음·생활", question:"요즘 쉽게 지쳐요. 나에게 맞는 휴식 방식이 궁금해요.", next:["생활 속에서 에너지를 아낄 방법을 알려주세요.", "나에게 맞는 쉬는 습관을 어떻게 찾아볼까요?"]},
        PAST: {label:"지나온 흐름", question:"지나온 흐름에서 어떤 패턴을 돌아볼 수 있을까요?", next:["이 흐름을 돌아볼 때 스스로에게 어떤 질문을 하면 좋을까요?", "지나온 경험에서 지금 가져올 수 있는 배움은 뭘까요?"]}
    };
    const ELEMENTS = [["wood","목","#a5bf87"],["fire","화","#d7997e"],["earth","토","#d0b574"],["metal","금","#d9d9cb"],["water","수","#87aeb6"]];
    let topic = "SELF";
    let current = null;
    let busy = false;
    let report = null;
    let toastTimer;

    function show(view) {
        for (const id of ["welcomeView","setupView","loadingView","chatView"]) $(id).hidden = id !== view;
        window.scrollTo({top:0,behavior:"instant"});
        const heading = $(view).querySelector("h1,h2");
        if (heading) { heading.tabIndex = -1; heading.focus({preventScroll:true}); }
    }

    function toast(text) {
        clearTimeout(toastTimer);
        $("toast").textContent = text;
        $("toast").hidden = false;
        toastTimer = setTimeout(() => { $("toast").hidden = true; },3500);
    }

    async function request(path,options={}) {
        let response;
        try {
            response = await fetch(API+path,{
                ...options,
                headers:{"Content-Type":"application/json",...options.headers},
                signal:AbortSignal.timeout(path === "/report" ? 660000 : 150000)
            });
        } catch (error) {
            throw new Error(error.name === "TimeoutError"
                ? "답변이 예상보다 오래 걸리고 있어요. 잠시 후 다시 확인해주세요."
                : "서버에 연결하지 못했어요. 연결을 확인한 뒤 다시 시도해주세요.");
        }
        if (response.status === 204) return null;
        let result;
        try { result = await response.json(); }
        catch { throw new Error("답변을 읽는 데 문제가 생겼어요. 다시 시도해주세요."); }
        if (!response.ok) {
            const error = new Error(result.message || "답변을 준비하지 못했어요. 잠시 후 다시 시도해주세요.");
            error.status = response.status;
            throw error;
        }
        return result;
    }

    function setBusy(value) {
        busy=value;
        $("startConsultationButton").disabled=value;
        $("newConsultationButton").disabled=value;
        $("reportButton").disabled=value;
        $("chatTopic").disabled=value;
        $("chatDepth").disabled=value;
        const disabled = value || !current?.readingAvailable || current?.turnsRemaining === 0;
        $("sendButton").disabled=disabled;
        $("chatInput").disabled=disabled;
        $("followupChips").querySelectorAll("button").forEach(button => { button.disabled=disabled; });
        $("messages").setAttribute("aria-busy",String(value));
    }

    function setTopic(value) {
        topic=TOPICS[value] ? value : "SELF";
        $("topicGrid").querySelectorAll("button").forEach(button => {
            const selected=button.dataset.topic === topic;
            button.classList.toggle("selected",selected);
            button.setAttribute("aria-pressed",String(selected));
        });
        $("initialQuestion").placeholder=TOPICS[topic].question;
        $("setupTopicLabel").textContent="✦ "+TOPICS[topic].label;
        $("situation").placeholder=topic === "LOVE" ? "예: 연애 중인데 서운한 마음을 표현하는 게 어려워요."
            : topic === "CAREER" ? "예: 직장 3년 차예요. 새로운 일을 해보고 싶지만 안정적인 생활도 중요해요."
            : "예: 요즘 어떤 고민을 하고 있는지, 어떤 점이 달라졌으면 하는지 알려주세요.";
    }

    $("topicGrid").addEventListener("click",event => {
        const button=event.target.closest("button[data-topic]");
        if (button) setTopic(button.dataset.topic);
    });
    $("openSetupButton").addEventListener("click",() => { show("setupView"); $("nickname").focus(); });
    $("setupBackButton").addEventListener("click",() => show("welcomeView"));
    document.querySelectorAll('input[name="calendar"]').forEach(input => input.addEventListener("change",() => {
        const lunar=input.value === "LUNAR" && input.checked;
        $("leapLabel").hidden=!lunar;
        if (!lunar) $("leapMonth").checked=false;
    }));
    $("unknownTime").addEventListener("change",() => {
        const unknown=$("unknownTime").checked;
        $("birthTime").disabled=unknown;
        $("birthTime").required=!unknown;
        $("timeHint").textContent=unknown ? "시간을 모르면 세 기둥과 오행만 표시해요. 맞춤풀이는 생성하지 않아요." : "출생기록에 적힌 한국 출생 시각을 입력해주세요.";
    });
    for (const id of ["year","month","day"]) $(id).addEventListener("input",() => {
        $(id).value=$(id).value.replace(/[^0-9]/g,"").slice(0,id === "year" ? 4 : 2);
    });

    function startData() {
        const year=Number($("year").value),month=Number($("month").value),day=Number($("day").value);
        const today=new Intl.DateTimeFormat("en-CA",{timeZone:"Asia/Seoul",year:"numeric",month:"2-digit",day:"2-digit"}).format(new Date());
        const calendar=document.querySelector('input[name="calendar"]:checked').value;
        if (year<1900 || year>Number(today.slice(0,4)) || month<1 || month>12 || day<1 || day>(calendar === "LUNAR" ? 30 : 31)) throw new Error("출생 연도와 월·일을 확인해주세요. 1900년부터 현재까지 입력할 수 있어요.");
        const date=`${year}-${String(month).padStart(2,"0")}-${String(day).padStart(2,"0")}`;
        if (calendar === "SOLAR") {
            const parsed=new Date(Date.UTC(year,month-1,day));
            if (parsed.getUTCMonth() !== month-1 || parsed.getUTCDate() !== day || date>today) throw new Error("존재하는 생년월일인지, 미래 날짜는 아닌지 확인해주세요.");
        }
        if (!$("nickname").value.trim()) throw new Error("이름이나 닉네임을 입력해주세요.");
        return {name:$("nickname").value.trim(),topic,depth:document.querySelector('input[name="depth"]:checked').value,
            situation:$("situation").value.trim(),question:$("initialQuestion").value.trim(),
            birth:{calendarType:calendar,birthDate:date,birthTime:$("unknownTime").checked ? null : $("birthTime").value,
                gender:document.querySelector('input[name="gender"]:checked').value,leapMonth:calendar === "LUNAR" && $("leapMonth").checked}};
    }

    $("birthForm").addEventListener("submit",async event => {
        event.preventDefault();
        if (busy) return;
        $("setupError").textContent="";
        let data;
        try { data=startData(); } catch(error) { $("setupError").textContent=error.message; return; }
        setBusy(true); show("loadingView");
        try {
            const result=await request("",{method:"POST",body:JSON.stringify(data)});
            report=null;
            renderConversation(result);
            $("resumeBanner").hidden=false;
            show("chatView");
        } catch(error) {
            $("setupError").textContent=error.message;
            show("setupView");
        } finally { setBusy(false); }
    });

    function renderConversation(result) {
        if (!result?.pillars || !Array.isArray(result.messages) || !result.fiveElements) throw new Error("상담 결과 형식이 올바르지 않아요. 다시 시작해주세요.");
        current=result;
        $("profileName").textContent=result.name;
        $("chatTopic").value=result.topic;
        $("chatDepth").value=result.depth;
        $("chatTopic").querySelectorAll("option").forEach(option => { option.disabled=!result.availableTopics.includes(option.value); });
        $("turnsNote").textContent=`이어서 ${result.turnsRemaining}번 더 질문할 수 있어요`;
        $("pillars").replaceChildren();
        for (const [key,label] of [["year","년주"],["month","월주"],["day","일주"],["hour","시주"]]) {
            const cell=document.createElement("div"); cell.className="pillar";
            const heading=document.createElement("small"); heading.textContent=label;
            const chars=document.createElement("strong");
            for (const char of result.pillars[key] ? [result.pillars[key].stem,result.pillars[key].branch] : ["—","—"]) {
                const span=document.createElement("span"); span.textContent=char; chars.append(span);
            }
            cell.append(heading,chars); $("pillars").append(cell);
        }
        const total=Object.values(result.fiveElements).reduce((sum,count) => sum+count,0);
        $("elementCountNote").textContent=`${total}글자 기준`;
        $("elements").replaceChildren();
        for (const [key,label,color] of ELEMENTS) {
            const row=document.createElement("div"); row.className="element-row";
            const name=document.createElement("span"); name.textContent=label;
            const track=document.createElement("div"); track.className="element-track"; track.setAttribute("aria-hidden","true");
            const fill=document.createElement("div"); fill.className="element-fill"; fill.style.width=`${result.fiveElements[key]/total*100}%`; fill.style.setProperty("--color",color); track.append(fill);
            const count=document.createElement("span"); count.textContent=result.fiveElements[key];
            row.append(name,track,count); $("elements").append(row);
        }
        renderMessages(result.messages,$("messages"));
        renderFollowups();
        $("chatError").textContent=!result.readingAvailable ? "출생시간을 확인한 뒤 ‘새 상담’에서 다시 시작해주세요."
            : result.turnsRemaining === 0 ? "이번 상담의 질문을 모두 사용했어요. 새 상담을 시작할 수 있어요." : "";
        setBusy(busy);
    }

    function renderMessages(messages,target) {
        target.replaceChildren();
        messages.forEach(message => {
            const article=document.createElement("article");
            article.className="message "+(message.role === "user" ? "user" : "assistant");
            if (message.role === "user") article.textContent=message.text;
            else {
                const label=document.createElement("div"); label.className="speaker"; label.textContent="✦ 도령 · AI 사주 상담"; article.append(label);
                let first=true;
                message.text.split(/\n+/).filter(line => line.trim()).forEach(line => {
                    const clean=line.replace(/^#{1,6}\s*/,"").replace(/\*\*/g,"").trim();
                    const heading=["사주에서 읽은 패턴","지금 상황에 연결하면","오늘 해볼 작은 행동"].includes(clean);
                    const element=document.createElement(heading ? "h3" : "p");
                    element.textContent=clean;
                    if (first && !heading) element.className="answer-lead";
                    first=false; article.append(element);
                });
            }
            target.append(article);
        });
    }

    function renderFollowups() {
        $("followupChips").replaceChildren();
        if (!current?.readingAvailable || !current.turnsRemaining) return;
        for (const text of TOPICS[$("chatTopic").value].next) {
            const button=document.createElement("button"); button.type="button"; button.textContent=text;
            button.addEventListener("click",() => { $("chatInput").value=text; $("chatInput").focus(); });
            $("followupChips").append(button);
        }
    }
    $("chatTopic").addEventListener("change",renderFollowups);
    $("chatForm").addEventListener("submit",async event => {
        event.preventDefault();
        const message=$("chatInput").value.trim();
        if (busy || !message || !current?.readingAvailable || !current.turnsRemaining) return;
        setBusy(true); $("chatError").textContent="";
        const pending=document.createElement("p"); pending.className="pending-message"; pending.textContent="✦ 도령이 앞선 이야기와 함께 읽고 있어요…"; $("messages").append(pending);
        try {
            const result=await request("/messages",{method:"POST",body:JSON.stringify({message,topic:$("chatTopic").value,depth:$("chatDepth").value})});
            renderConversation(result); $("chatInput").value="";
            $("messages").lastElementChild?.scrollIntoView({behavior:"smooth",block:"start"});
        } catch(error) {
            $("chatError").textContent=error.message;
            if (error.status === 410) { current.turnsRemaining=0; $("resumeBanner").hidden=true; }
        } finally { pending.remove(); setBusy(false); }
    });
    $("chatInput").addEventListener("keydown",event => {
        if (event.key === "Enter" && !event.shiftKey && !event.isComposing) { event.preventDefault(); $("chatForm").requestSubmit(); }
    });
    $("resumeButton").addEventListener("click",async () => {
        if (busy) return;
        try {
            const result=await request("/current");
            if (!result) throw new Error("이전 상담이 만료됐어요. 새로 시작해주세요.");
            renderConversation(result); show("chatView");
        } catch(error) { $("resumeBanner").hidden=true; toast(error.message); }
    });
    $("newConsultationButton").addEventListener("click",() => { if (!busy) { show("welcomeView"); renderSaved(); } });

    $("reportButton").addEventListener("click",async () => {
        if (busy) return;
        $("reportDialog").showModal();
        if (report) { renderReport(); return; }
        $("reportContent").replaceChildren();
        $("reportStatus").textContent="7개 주제의 풀이를 준비하고 있어요. 선택한 고민보다 넓은 이야기를 읽는 데 시간이 걸릴 수 있어요.";
        setBusy(true);
        try { report=await request("/report",{method:"POST"}); renderReport(); }
        catch(error) { $("reportStatus").textContent=error.message+" 창을 닫은 뒤 다시 눌러주세요."; }
        finally { setBusy(false); }
    });
    function renderReport() {
        $("reportStatus").textContent="궁금한 주제를 펼쳐 읽어보세요. 생성할 수 없는 항목은 안내로 구분했어요.";
        $("reportContent").replaceChildren();
        report.interpretations.forEach((item,index) => {
            const details=document.createElement("details"); details.open=index === 0;
            const title=document.createElement("summary"); title.textContent=`0${item.order} · ${item.title}`;
            if (item.status === "UNAVAILABLE") { const note=document.createElement("span"); note.className="unavailable-label"; note.textContent="풀이 미생성"; title.append(note); }
            const content=document.createElement("p"); content.textContent=item.content;
            details.append(title,content); $("reportContent").append(details);
        });
    }
    $("closeReportButton").addEventListener("click",() => $("reportDialog").close());
    $("closeSavedButton").addEventListener("click",() => $("savedDialog").close());

    function savedRecords() {
        try {
            const parsed=JSON.parse(localStorage.getItem(STORAGE_KEY) || "[]");
            return Array.isArray(parsed) ? parsed.filter(item => typeof item.name === "string" && typeof item.date === "string" && TOPICS[item.topic] && Array.isArray(item.messages)).slice(0,5) : [];
        } catch { return []; }
    }
    function renderSaved() {
        const records=savedRecords();
        $("savedSection").hidden=!records.length;
        $("savedList").replaceChildren();
        records.forEach(record => {
            const button=document.createElement("button"); button.type="button"; button.className="saved-card";
            const name=document.createElement("strong"); name.textContent=record.name+" · "+TOPICS[record.topic].label;
            const date=document.createElement("small"); date.textContent=new Date(record.date).toLocaleDateString("ko-KR")+" · 저장한 상담 보기 ↗";
            button.append(name,date);
            button.addEventListener("click",() => {
                const messages=record.messages.filter(item => ["user","assistant"].includes(item.role) && typeof item.text === "string").slice(0,24);
                renderMessages(messages,$("savedContent")); $("savedDialog").showModal();
            });
            $("savedList").append(button);
        });
    }
    $("saveButton").addEventListener("click",() => {
        if (!current) return;
        try {
            const date=new Date().toISOString();
            const signature=current.messages[0]?.text+current.messages[1]?.text;
            const records=savedRecords().filter(record => record.signature !== signature);
            records.unshift({name:current.name,topic:current.topic,date,signature,messages:current.messages});
            localStorage.setItem(STORAGE_KEY,JSON.stringify(records.slice(0,5)));
            renderSaved(); toast("이 기기에 상담을 저장했어요. 새 상담 화면에서 다시 볼 수 있어요.");
        } catch { toast("이 브라우저에서는 저장할 수 없어요. 요약 공유를 이용해주세요."); }
    });
    $("clearSavedButton").addEventListener("click",() => {
        try { localStorage.removeItem(STORAGE_KEY); renderSaved(); toast("이 기기에 저장한 상담을 모두 지웠어요."); }
        catch { toast("저장 기록을 지우지 못했어요. 브라우저 저장 설정을 확인해주세요."); }
    });
    $("shareButton").addEventListener("click",async () => {
        const answer=current?.messages.find(message => message.role === "assistant")?.text;
        if (!answer) return;
        const text="다점에서 읽은 나의 이야기\n\n"+answer.split("\n").find(line => line.trim())+"\n\n다점 · AI 사주 상담";
        try {
            if (navigator.share) await navigator.share({title:"나의 사주 한 문장",text});
            else { await navigator.clipboard.writeText(text); toast("생년월일 없이 요약 한 문장을 복사했어요."); }
        } catch(error) { if (error.name !== "AbortError") toast("공유할 수 없어요. 답변에서 원하는 문장을 복사해주세요."); }
    });

    const initialTopic=new URLSearchParams(location.search).get("topic");
    setTopic(initialTopic || "SELF");
    renderSaved();
    request("/current").then(result => { if (result && !busy) { current=result; $("resumeBanner").hidden=false; } }).catch(() => {});
})();
