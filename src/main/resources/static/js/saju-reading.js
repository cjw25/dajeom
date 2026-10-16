"use strict";
(() => {
    const $=id=>document.getElementById(id);
    const API="/api/saju/consultations";
    const KEY="dajeom.savedReadings.v1";
    const GROUPS=[
        {id:"self",title:"나의 본질",image:"/img/category_bg.png"},
        {id:"relationships",title:"사람과 인연",video:"/video/motion_network.mp4"},
        {id:"love",title:"연애와 결혼",video:"/video/motion_love.mp4"},
        {id:"wealth",title:"재물과 생활",video:"/video/motion_wealth.mp4"},
        {id:"work",title:"적성과 일",image:"/img/category_bg.png"},
        {id:"wellbeing",title:"생활과 회복",image:"/img/frame13_blur.jpg"},
        {id:"past",title:"지나온 흐름",image:"/img/category_bg.png"}
    ];
    const ELEMENTS=[["wood","목","#a5bf87"],["fire","화","#d7997e"],["earth","토","#d0b574"],["metal","금","#d9d9cb"],["water","수","#87aeb6"]];
    let current=null,busy=false,toastTimer;
    const videoObserver=new IntersectionObserver(entries=>entries.forEach(({target,isIntersecting})=>{
        if(isIntersecting && !window.matchMedia("(prefers-reduced-motion: reduce)").matches) target.play().catch(()=>{});
        else target.pause();
    }),{threshold:.25});

    function show(id){
        for(const view of ["welcomeView","setupView","loadingView","readingView"])$(view).hidden=view!==id;
        if(id!=="readingView")$("readingChapters").querySelectorAll("video").forEach(video=>video.pause());
        window.scrollTo({top:0,behavior:"instant"});
        const heading=$(id).querySelector("h1,h2");if(heading){heading.tabIndex=-1;heading.focus({preventScroll:true});}
    }
    function toast(text){clearTimeout(toastTimer);$("toast").textContent=text;$("toast").hidden=false;toastTimer=setTimeout(()=>{$("toast").hidden=true;},3500);}
    async function request(path,options={}){
        let response;
        try{response=await fetch(API+path,{...options,headers:{"Content-Type":"application/json"},signal:AbortSignal.timeout(path==="/reading"?660000:150000)});}
        catch(error){throw new Error(error.name==="TimeoutError"?"풀이 작성이 예상보다 오래 걸립니다. 잠시 후 다시 확인해주세요.":"서버에 연결하지 못했습니다. 연결을 확인한 뒤 다시 시도해주세요.");}
        if(response.status===204)return null;
        let data;try{data=await response.json();}catch{throw new Error("풀이 결과를 읽지 못했습니다. 다시 시도해주세요.");}
        if(!response.ok){const error=new Error(data.message||"풀이를 완성하지 못했습니다. 잠시 후 다시 시도해주세요.");error.status=response.status;throw error;}
        return data;
    }
    function setBusy(value){busy=value;$("startReadingButton").disabled=value;$("newReadingButton").disabled=value;
        for(const id of ["sendButton","chatInput","chatTopic","chatDepth"])$(id).disabled=value||!current?.readingAvailable||current?.turnsRemaining===0;
    }
    $("openSetupButton").addEventListener("click",()=>{show("setupView");$("nickname").focus();});
    $("setupBackButton").addEventListener("click",()=>show("welcomeView"));
    $("newReadingButton").addEventListener("click",()=>{if(!busy){show("welcomeView");renderSaved();}});
    document.querySelectorAll('input[name="calendar"]').forEach(input=>input.addEventListener("change",()=>{
        const lunar=document.querySelector('input[name="calendar"]:checked').value==="LUNAR";$("leapLabel").hidden=!lunar;if(!lunar)$("leapMonth").checked=false;
    }));
    $("unknownTime").addEventListener("change",()=>{const unknown=$("unknownTime").checked;$("birthTime").disabled=unknown;$("birthTime").required=!unknown;$("timeHint").textContent=unknown?"시간을 모르면 세 기둥과 오행만 확인할 수 있습니다. 종합풀이 본문은 생성하지 않습니다.":"출생기록에 적힌 한국 출생 시각을 입력해주세요.";});
    for(const id of ["year","month","day"])$(id).addEventListener("input",()=>{$(id).value=$(id).value.replace(/[^0-9]/g,"").slice(0,id==="year"?4:2);});
    function startData(){
        const year=Number($("year").value),month=Number($("month").value),day=Number($("day").value);
        const today=new Intl.DateTimeFormat("en-CA",{timeZone:"Asia/Seoul",year:"numeric",month:"2-digit",day:"2-digit"}).format(new Date());
        const calendar=document.querySelector('input[name="calendar"]:checked').value;
        if(year<1900||year>Number(today.slice(0,4))||month<1||month>12||day<1||day>(calendar==="LUNAR"?30:31))throw new Error("출생 연도와 월·일을 확인해주세요. 1900년부터 현재까지 입력할 수 있습니다.");
        const date=`${year}-${String(month).padStart(2,"0")}-${String(day).padStart(2,"0")}`;
        if(calendar==="SOLAR"){const parsed=new Date(Date.UTC(year,month-1,day));if(parsed.getUTCMonth()!==month-1||parsed.getUTCDate()!==day||date>today)throw new Error("올바른 생년월일인지, 미래 날짜는 아닌지 확인해주세요.");}
        if(!$("nickname").value.trim())throw new Error("이름이나 닉네임을 입력해주세요.");
        return{name:$("nickname").value.trim(),birth:{calendarType:calendar,birthDate:date,birthTime:$("unknownTime").checked?null:$("birthTime").value,gender:document.querySelector('input[name="gender"]:checked').value,leapMonth:calendar==="LUNAR"&&$("leapMonth").checked}};
    }
    $("birthForm").addEventListener("submit",async event=>{
        event.preventDefault();if(busy)return;$("setupError").textContent="";
        let data;try{data=startData();}catch(error){$("setupError").textContent=error.message;return;}
        setBusy(true);show("loadingView");
        try{renderReading(await request("/reading",{method:"POST",body:JSON.stringify(data)}));$("resumeBanner").hidden=false;show("readingView");}
        catch(error){$("setupError").textContent=error.message;show("setupView");}
        finally{setBusy(false);}
    });
    function renderReading(data){
        if(!data?.reading||!Array.isArray(data.reading.chapters)||data.reading.chapters.length!==18)throw new Error("종합풀이의 장 구성을 확인할 수 없습니다. 다시 시도해주세요.");
        current=data;$("readingName").textContent=data.name;
        $("pillars").replaceChildren();
        for(const[key,label]of[["year","년주"],["month","월주"],["day","일주"],["hour","시주"]]){
            const cell=document.createElement("div");cell.className="pillar";const small=document.createElement("small");small.textContent=label;const chars=document.createElement("strong");
            for(const char of data.pillars[key]?[data.pillars[key].stem,data.pillars[key].branch]:["—","—"]){const span=document.createElement("span");span.textContent=char;chars.append(span);}cell.append(small,chars);$("pillars").append(cell);
        }
        const total=Object.values(data.fiveElements).reduce((a,b)=>a+b,0);$("elementCountNote").textContent=`확인 가능한 ${total}글자의 구성입니다. 오행의 개수는 운의 좋고 나쁨을 점수화한 값이 아닙니다.`;
        $("elements").replaceChildren();
        for(const[key,label,color]of ELEMENTS){const row=document.createElement("div");row.className="element-row";const name=document.createElement("span");name.textContent=label;const track=document.createElement("div");track.className="element-track";track.setAttribute("aria-hidden","true");const fill=document.createElement("div");fill.className="element-fill";fill.style.width=`${data.fiveElements[key]/total*100}%`;fill.style.setProperty("--color",color);track.append(fill);const count=document.createElement("span");count.textContent=data.fiveElements[key];row.append(name,track,count);$("elements").append(row);}
        videoObserver.disconnect();$("readingToc").replaceChildren();$("readingChapters").replaceChildren();
        let number=0;
        GROUPS.forEach((group,index)=>{
            const link=document.createElement("a");link.href="#book-"+group.id;link.textContent=`0${index+1} · ${group.title}`;$("readingToc").append(link);
            const section=document.createElement("section");section.id="book-"+group.id;section.className="reading-group";
            const cover=document.createElement("div");cover.className="reading-group-cover";
            if(group.video){const video=document.createElement("video");video.src=group.video;video.muted=true;video.loop=true;video.playsInline=true;video.preload="none";video.setAttribute("aria-hidden","true");video.poster="/img/category_bg.png";cover.append(video);videoObserver.observe(video);}
            else{const image=document.createElement("img");image.src=group.image;image.alt="";image.loading="lazy";cover.append(image);}
            const heading=document.createElement("div");const small=document.createElement("small");small.textContent=`천명록 · ${index+1}부`;const title=document.createElement("h2");title.textContent=group.title;heading.append(small,title);cover.append(heading);section.append(cover);
            data.reading.chapters.filter(chapter=>chapter.groupId===group.id).forEach(chapter=>{
                const article=document.createElement("article");article.className="reading-chapter";article.id=chapter.id;const indexLabel=document.createElement("span");indexLabel.className="chapter-number";indexLabel.textContent=`CHAPTER ${String(++number).padStart(2,"0")}`;const h3=document.createElement("h3");h3.textContent=chapter.title;const content=document.createElement("p");content.textContent=chapter.content;if(chapter.status!=="READY")content.className="unavailable-note";article.append(indexLabel,h3,content);section.append(article);
            });$("readingChapters").append(section);
        });
        renderChat();setBusy(busy);
    }
    function renderChat(){
        $("messages").replaceChildren();
        for(const message of current.messages){const article=document.createElement("article");article.className="message "+(message.role==="user"?"user":"assistant");if(message.role==="user")article.textContent=message.text;else{const label=document.createElement("div");label.className="speaker";label.textContent="천명도령 · 추가 설명";const text=document.createElement("p");text.textContent=message.text;article.append(label,text);}$("messages").append(article);}
        $("chatTopic").value=current.topic;$("chatDepth").value=current.depth;$("chatTopic").querySelectorAll("option").forEach(option=>option.disabled=!current.availableTopics.includes(option.value));$("turnsNote").textContent=`추가 질문 ${current.turnsRemaining}회 남음`;
        $("chatError").textContent=!current.readingAvailable?"출생시간을 확인한 뒤 새 사주 보기를 시작해주세요.":!current.turnsRemaining?"추가 질문을 모두 사용했습니다.":"";
    }
    $("chatForm").addEventListener("submit",async event=>{
        event.preventDefault();const message=$("chatInput").value.trim();if(busy||!message||!current?.readingAvailable||!current.turnsRemaining)return;
        setBusy(true);$("chatError").textContent="천명도령이 추가 설명을 준비하고 있습니다…";
        try{const data=await request("/messages",{method:"POST",body:JSON.stringify({message,topic:$("chatTopic").value,depth:$("chatDepth").value})});current=data;renderChat();$("chatInput").value="";}
        catch(error){$("chatError").textContent=error.message;if(error.status===410)current.turnsRemaining=0;}
        finally{setBusy(false);}
    });
    $("chatInput").addEventListener("keydown",event=>{if(event.key==="Enter"&&!event.shiftKey&&!event.isComposing){event.preventDefault();$("chatForm").requestSubmit();}});
    $("resumeButton").addEventListener("click",async()=>{if(busy)return;try{const data=await request("/current");if(!data?.reading)throw new Error("이전 풀이가 만료됐습니다. 새 사주 보기를 시작해주세요.");renderReading(data);show("readingView");}catch(error){$("resumeBanner").hidden=true;toast(error.message);}});
    function records(){try{const data=JSON.parse(localStorage.getItem(KEY)||"[]");return Array.isArray(data)?data.filter(item=>typeof item.name==="string"&&Array.isArray(item.chapters)).slice(0,5):[];}catch{return[];}}
    function renderSaved(){const data=records();$("savedSection").hidden=!data.length;$("savedList").replaceChildren();data.forEach(record=>{const button=document.createElement("button");button.type="button";button.className="saved-card";const title=document.createElement("strong");title.textContent=record.name+"님의 천명록";const date=document.createElement("small");date.textContent=new Date(record.date).toLocaleDateString("ko-KR")+" · 18개 장";button.append(title,date);button.addEventListener("click",()=>{$("savedContent").replaceChildren();record.chapters.filter(chapter=>typeof chapter.title==="string"&&typeof chapter.content==="string").slice(0,18).forEach(chapter=>{const article=document.createElement("article");article.className="saved-chapter";const h3=document.createElement("h3");h3.textContent=chapter.title;const p=document.createElement("p");p.textContent=chapter.content;article.append(h3,p);$("savedContent").append(article);});$("savedDialog").showModal();});$("savedList").append(button);});}
    $("saveButton").addEventListener("click",()=>{if(!current?.reading)return;try{const signature=current.reading.chapters[0].content;const data=records().filter(record=>record.signature!==signature);data.unshift({name:current.name,date:new Date().toISOString(),signature,chapters:current.reading.chapters});localStorage.setItem(KEY,JSON.stringify(data.slice(0,5)));renderSaved();toast("천명록 18개 장을 이 기기에 보관했습니다.");}catch{toast("이 브라우저에서는 보관할 수 없습니다.");}});
    $("clearSavedButton").addEventListener("click",()=>{try{localStorage.removeItem(KEY);renderSaved();toast("이 기기에 보관한 천명록을 모두 지웠습니다.");}catch{toast("기기 보관함을 지우지 못했습니다.");}});
    $("shareButton").addEventListener("click",async()=>{const chapter=current?.reading?.chapters.find(chapter=>chapter.status==="READY");if(!chapter)return;const summary=chapter.content.split(/\n|(?<=[.!?。])\s/)[0];const text="천명도령이 읽어준 나의 이야기\n\n"+summary+"\n\n다점 · 종합사주";try{if(navigator.share)await navigator.share({title:"나의 천명록 한 문장",text});else{await navigator.clipboard.writeText(text);toast("첫 장의 요약을 복사했습니다.");}}catch(error){if(error.name!=="AbortError")toast("공유할 수 없습니다. 원하는 문장을 복사해주세요.");}});
    $("closeSavedButton").addEventListener("click",()=>$("savedDialog").close());
    $("openingVideoButton").addEventListener("click",()=>{$("openingDialog").showModal();$("originalOpeningVideo").play().catch(()=>{});});
    $("closeOpeningButton").addEventListener("click",()=>$("openingDialog").close());$("openingDialog").addEventListener("close",()=>$("originalOpeningVideo").pause());
    renderSaved();request("/current").then(data=>{if(data?.reading&&!busy){current=data;$("resumeBanner").hidden=false;}}).catch(()=>{});
})();
