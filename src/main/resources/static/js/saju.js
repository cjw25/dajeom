"use strict";


/* =========================================================
   CONFIG
   ========================================================= */

const API_URL =
    "/api/saju/calculate";


const FORM_STORAGE_KEY =
    "sajuForm";


const RESULT_STORAGE_KEY =
    "sajuAnalysisResult";


const MINIMUM_ANALYSIS_TIME =
    4200;


const FACT_DISPLAY_TIME =
    900;


const WAIT_PROGRESS_LIMIT =
    94;


const VIDEO_PATH = {

    opening:
        "/video/opening.mp4",

    analysis1:
        "/video/analysis1.mp4",

    analysis2:
        "/video/analysis2.mp4",

    love:
        "/video/motion_love.mp4",

    wealth:
        "/video/motion_wealth.mp4",

    network:
        "/video/motion_network.mp4"

};



/* =========================================================
   CATEGORY
   ========================================================= */

const CATEGORY_META = {

    PERSONALITY: {

        number: 1,
        chapter: "제1장",
        label: "타고난 성향",
        title: "나는 어떤 사람인가",
        analyzing: "당신이 타고난 성향과 기질을 풀어보고 있습니다."

    },


    PAST_LUCK: {

        number: 2,
        chapter: "제2장",
        label: "지나온 운",
        title: "지나온 시간의 흐름",
        analyzing: "지금까지 지나온 삶의 흐름을 살펴보고 있습니다."

    },


    SOCIAL_RELATIONSHIP: {

        number: 3,
        chapter: "제3장",
        label: "대인관계",
        title: "사람과 관계를 맺는 방식",
        analyzing: "사람을 만나고 관계를 이어가는 흐름을 살펴보고 있습니다."

    },


    LOVE_STYLE: {

        number: 4,
        chapter: "제4장",
        label: "연애 성향",
        title: "마음을 주고받는 방식",
        analyzing: "사랑에서 마음을 표현하고 받아들이는 방식을 살펴보고 있습니다."

    },


    PARTNER_LUCK: {

        number: 5,
        chapter: "제5장",
        label: "배우자운",
        title: "인연과 동반자의 흐름",
        analyzing: "인연과 동반자 관계의 흐름을 살펴보고 있습니다."

    },


    WEALTH: {

        number: 6,
        chapter: "제6장",
        label: "재물운",
        title: "재물을 다루는 흐름",
        analyzing: "재물을 만들고 지키고 운용하는 흐름을 살펴보고 있습니다."

    },


    CAREER: {

        number: 7,
        chapter: "제7장",
        label: "직업운",
        title: "일과 성취를 향하는 방식",
        analyzing: "일과 책임, 성취를 향하는 흐름을 살펴보고 있습니다."

    },


    LEGAL_RISK: {

        number: 8,
        chapter: "제8장",
        label: "분쟁과 주의",
        title: "갈등과 책임을 다루는 흐름",
        analyzing: "갈등이나 책임이 생길 때의 흐름을 살펴보고 있습니다."

    },


    HEALTH_TENDENCY: {

        number: 9,
        chapter: "제9장",
        label: "건강 경향",
        title: "균형과 회복의 흐름",
        analyzing: "생활의 균형과 회복 리듬을 살펴보고 있습니다."

    },


    CURRENT_YEAR_LUCK: {

        number: 10,
        chapter: "제10장",
        label: "올해의 운",
        title: "지금 흐르는 운의 방향",
        analyzing: "현재 시점에 들어와 있는 운의 흐름을 살펴보고 있습니다."

    },


    FUTURE_LUCK: {

        number: 11,
        chapter: "제11장",
        label: "앞으로의 운",
        title: "다가오는 흐름을 바라보는 법",
        analyzing: "앞으로 이어질 흐름을 살펴보고 있습니다."

    }

};


const CATEGORY_ORDER =
    Object
        .keys(
            CATEGORY_META
        )
        .sort(
            (
                left,
                right
            ) =>
                CATEGORY_META[left].number
                -
                CATEGORY_META[right].number
        );



/* =========================================================
   STATE
   ========================================================= */

let currentScreen =
    "intro";


let currentResult =
    null;


let currentStoryIndex =
    0;


let introFinished =
    false;


let skipFactsRequested =
    false;


let analysisRunning =
    false;


let touchStartX =
    null;


let formState = {

    name:
        "",

    calendarType:
        "SOLAR",

    leapMonth:
        false,

    gender:
        "MALE",

    birthYear:
        "",

    birthMonth:
        "",

    birthDay:
        "",

    birthHour:
        "",

    birthMinute:
        "",

    birthTimeUnknown:
        false

};



/* =========================================================
   DOM
   ========================================================= */

const screens =
    Array.from(
        document.querySelectorAll(
            "[data-screen]"
        )
    );


const backButton =
    required(
        "backButton"
    );


const homeButton =
    required(
        "homeButton"
    );


const headerSubtitle =
    required(
        "headerSubtitle"
    );


/* NAME */

const nameForm =
    required(
        "nameForm"
    );


const nameInput =
    required(
        "nameInput"
    );


const nameError =
    required(
        "nameError"
    );


const nameNextButton =
    required(
        "nameNextButton"
    );


/* BIRTH */

const birthForm =
    required(
        "birthForm"
    );


const birthYear =
    required(
        "birthYear"
    );


const birthMonth =
    required(
        "birthMonth"
    );


const birthDay =
    required(
        "birthDay"
    );


const birthHour =
    required(
        "birthHour"
    );


const birthMinute =
    required(
        "birthMinute"
    );


const birthTimeUnknown =
    required(
        "birthTimeUnknown"
    );


const timeInputArea =
    required(
        "timeInputArea"
    );


const lunarLeapArea =
    required(
        "lunarLeapArea"
    );


const lunarLeapMonth =
    required(
        "lunarLeapMonth"
    );


const birthError =
    required(
        "birthError"
    );


const birthNextButton =
    required(
        "birthNextButton"
    );


/* REVIEW */

const reviewCompleteButton =
    required(
        "reviewCompleteButton"
    );


/* COMPLETE */

const startAnalysisButton =
    required(
        "startAnalysisButton"
    );


/* ANALYSIS */

const analysisStatus =
    required(
        "analysisStatus"
    );


const analysisTitle =
    required(
        "analysisTitle"
    );


const analysisMessage =
    required(
        "analysisMessage"
    );


const analysisProgressPercent =
    required(
        "analysisProgressPercent"
    );


const analysisProgressBar =
    required(
        "analysisProgressBar"
    );


const analysisElements =
    Array.from(
        document.querySelectorAll(
            "[data-analysis-element]"
        )
    );


/* ANALYSIS CATEGORY */

const analysisFactOverlay =
    required(
        "analysisFactOverlay"
    );


const factCategoryIndex =
    required(
        "factCategoryIndex"
    );


const factCategoryKicker =
    required(
        "factCategoryKicker"
    );


const factCategoryTitle =
    required(
        "factCategoryTitle"
    );


const factCategoryMessage =
    required(
        "factCategoryMessage"
    );


const skipFactsButton =
    required(
        "skipFactsButton"
    );


/* RESULT */

const resultTitle =
    required(
        "resultTitle"
    );


const resultSummary =
    required(
        "resultSummary"
    );


const resultPillars =
    required(
        "resultPillars"
    );


const resultStoryList =
    required(
        "resultStoryList"
    );


const resultHomeButton =
    required(
        "resultHomeButton"
    );


const toast =
    required(
        "toast"
    );


/* OPTIONAL INTRO */

const openingVideo =
    optional(
        "openingVideo"
    );


const introWorldLine1 =
    optional(
        "introWorldLine1"
    );


const introWorldLine2 =
    optional(
        "introWorldLine2"
    );


const introWorldLine3 =
    optional(
        "introWorldLine3"
    );


const introWorldEmblem =
    optional(
        "introWorldEmblem"
    );


const introWorldFinal =
    optional(
        "introWorldFinal"
    );


const introStartButton =
    optional(
        "introStartButton"
    );


const introWorldSkip =
    optional(
        "introWorldSkip"
    );


/* OPTIONAL VIDEO */

const analysisVideo1 =
    optional(
        "analysisVideo1"
    );


const analysisVideo2 =
    optional(
        "analysisVideo2"
    );


const factMotionVideo =
    optional(
        "factMotionVideo"
    );


/* OPTIONAL REVEAL */

const revealOpening =
    optional(
        "revealOpening"
    );


const revealPillarGrid =
    optional(
        "revealPillarGrid"
    );


const revealFinal =
    optional(
        "revealFinal"
    );


const revealSkipButton =
    optional(
        "revealSkipButton"
    );



/* =========================================================
   INITIALIZE
   ========================================================= */

document.addEventListener(
    "DOMContentLoaded",
    initialize
);


function initialize() {

    loadStoredForm();

    bindNavigation();

    bindIntro();

    bindName();

    bindBirth();

    bindReview();

    bindAnalysis();

    bindStoryNavigation();

    restoreFormInputs();

    updateNameValidation();

    updateBirthValidation();

    updateHeader();

    prepareVideoPaths();

    playIntroWorld();

}



/* =========================================================
   VIDEO PATH
   ========================================================= */

function prepareVideoPaths() {

    setVideoSource(
        openingVideo,
        VIDEO_PATH.opening
    );


    setVideoSource(
        analysisVideo1,
        VIDEO_PATH.analysis1
    );


    setVideoSource(
        analysisVideo2,
        VIDEO_PATH.analysis2
    );

}


function setVideoSource(
    video,
    path
) {

    if (
        !video
        ||
        !path
    ) {

        return;

    }


    if (
        video.getAttribute(
            "src"
        )
        !== path
    ) {

        video.src =
            path;

    }


    video.load();

}



/* =========================================================
   NAVIGATION
   ========================================================= */

function bindNavigation() {

    document.addEventListener(
        "click",
        event => {

            const target =
                event.target.closest(
                    "[data-go]"
                );


            if (
                !target
            ) {

                return;

            }


            const screen =
                target.dataset.go;


            if (
                screen
            ) {

                showScreen(
                    screen
                );

            }

        }
    );


    backButton.addEventListener(
        "click",
        handleBack
    );


    homeButton.addEventListener(
        "click",
        goHome
    );


    resultHomeButton.addEventListener(
        "click",
        goHome
    );

}


function handleBack() {

    if (
        currentScreen
        === "result"
        &&
        currentStoryIndex
        > 0
    ) {

        goPreviousStory();

        return;

    }


    const backMap = {

        name:
            "intro",

        birth:
            "name",

        review:
            "birth",

        complete:
            "review",

        analysis:
            "complete",

        reveal:
            "complete",

        result:
            "complete"

    };


    const destination =
        backMap[currentScreen];


    if (
        destination
    ) {

        showScreen(
            destination
        );

    }

}


function goHome() {

    currentResult =
        null;


    currentStoryIndex =
        0;


    analysisRunning =
        false;


    skipFactsRequested =
        false;


    sessionStorage.removeItem(
        RESULT_STORAGE_KEY
    );


    showScreen(
        "intro"
    );


    showIntroFinal();

}


function showScreen(
    screenName
) {

    screens.forEach(
        screen => {

            screen.classList.toggle(
                "is-active",
                screen.dataset.screen
                === screenName
            );

        }
    );


    currentScreen =
        screenName;


    updateHeader();


    window.scrollTo({

        top:
            0,

        behavior:
            "smooth"

    });

}


function updateHeader() {

    const subtitles = {

        intro:
            "천명사주",

        name:
            "이름 입력",

        birth:
            "생년월일 입력",

        review:
            "입력 확인",

        complete:
            "분석 준비",

        analysis:
            "천명 분석",

        reveal:
            "사주팔자",

        result:
            "천명록"

    };


    headerSubtitle.textContent =
        subtitles[currentScreen]
        || "천명사주";


    backButton.style.visibility =
        currentScreen
        === "intro"
            ? "hidden"
            : "visible";

}



/* =========================================================
   INTRO
   ========================================================= */

function bindIntro() {

    introWorldSkip
        ?.addEventListener(
            "click",
            () => {

                stopVideo(
                    openingVideo
                );


                showIntroFinal();

            }
        );


    introStartButton
        ?.addEventListener(
            "click",
            () => {

                showScreen(
                    "name"
                );

            }
        );

}


async function playIntroWorld() {

    if (
        introFinished
    ) {

        showIntroFinal();

        return;

    }


    hideIntroFinal();


    if (
        openingVideo
    ) {

        const played =
            await playVideo(
                openingVideo,
                VIDEO_PATH.opening,
                true
            );


        if (
            played
        ) {

            showIntroFinal();

            return;

        }

    }


    await playIntroTextFallback();


    showIntroFinal();

}


async function playIntroTextFallback() {

    const lines = [

        introWorldLine1,

        introWorldLine2,

        introWorldLine3

    ].filter(
        Boolean
    );


    for (
        const line
        of lines
    ) {

        line.classList.remove(
            "is-hidden"
        );


        line.classList.add(
            "is-visible"
        );


        await delay(
            1500
        );


        line.classList.remove(
            "is-visible"
        );


        line.classList.add(
            "is-hidden"
        );


        await delay(
            350
        );

    }

}


function showIntroFinal() {

    introFinished =
        true;


    introWorldEmblem
        ?.classList.add(
            "is-visible"
        );


    introWorldFinal
        ?.classList.add(
            "is-visible"
        );


    introWorldSkip
        ?.classList.add(
            "hidden"
        );

}


function hideIntroFinal() {

    introWorldEmblem
        ?.classList.remove(
            "is-visible"
        );


    introWorldFinal
        ?.classList.remove(
            "is-visible"
        );

}



/* =========================================================
   NAME
   ========================================================= */

function bindName() {

    nameInput.addEventListener(
        "input",
        () => {

            formState.name =
                nameInput
                    .value
                    .trim();


            updateNameValidation();

            saveForm();

        }
    );


    nameForm.addEventListener(
        "submit",
        event => {

            event.preventDefault();


            if (
                !validateName()
            ) {

                return;

            }


            saveForm();

            showScreen(
                "birth"
            );

        }
    );

}


function validateName() {

    const name =
        nameInput
            .value
            .trim();


    if (
        name.length
        < 1
    ) {

        nameError.textContent =
            "이름을 입력해주세요.";

        return false;

    }


    nameError.textContent =
        "";


    return true;

}


function updateNameValidation() {

    const valid =
        nameInput
            .value
            .trim()
            .length
        > 0;


    nameNextButton.disabled =
        !valid;


    if (
        valid
    ) {

        nameError.textContent =
            "";

    }

}



/* =========================================================
   BIRTH
   ========================================================= */

function bindBirth() {

    document
        .querySelectorAll(
            "[data-calendar]"
        )
        .forEach(
            button => {

                button.addEventListener(
                    "click",
                    () => {

                        formState.calendarType =
                            button.dataset.calendar;


                        document
                            .querySelectorAll(
                                "[data-calendar]"
                            )
                            .forEach(
                                item => {

                                    item.classList.toggle(
                                        "is-active",
                                        item === button
                                    );

                                }
                            );


                        updateCalendarUI();

                        updateBirthValidation();

                        saveForm();

                    }
                );

            }
        );


    document
        .querySelectorAll(
            "[data-gender]"
        )
        .forEach(
            button => {

                button.addEventListener(
                    "click",
                    () => {

                        formState.gender =
                            button.dataset.gender;


                        document
                            .querySelectorAll(
                                "[data-gender]"
                            )
                            .forEach(
                                item => {

                                    item.classList.toggle(
                                        "is-active",
                                        item === button
                                    );

                                }
                            );


                        saveForm();

                    }
                );

            }
        );


    [
        birthYear,
        birthMonth,
        birthDay,
        birthHour,
        birthMinute
    ]
        .forEach(
            input => {

                input.addEventListener(
                    "input",
                    () => {

                        syncBirthFormState();

                        updateBirthValidation();

                        saveForm();

                    }
                );

            }
        );


    birthTimeUnknown.addEventListener(
        "change",
        () => {

            formState.birthTimeUnknown =
                birthTimeUnknown.checked;


            updateTimeUI();

            updateBirthValidation();

            saveForm();

        }
    );


    lunarLeapMonth.addEventListener(
        "change",
        () => {

            formState.leapMonth =
                lunarLeapMonth.checked;


            saveForm();

        }
    );


    birthForm.addEventListener(
        "submit",
        event => {

            event.preventDefault();


            syncBirthFormState();


            if (
                !validateBirth()
            ) {

                return;

            }


            saveForm();

            renderReview();

            showScreen(
                "review"
            );

        }
    );

}


function syncBirthFormState() {

    formState.birthYear =
        birthYear.value.trim();


    formState.birthMonth =
        birthMonth.value.trim();


    formState.birthDay =
        birthDay.value.trim();


    formState.birthHour =
        birthHour.value.trim();


    formState.birthMinute =
        birthMinute.value.trim();


    formState.birthTimeUnknown =
        birthTimeUnknown.checked;


    formState.leapMonth =
        lunarLeapMonth.checked;

}


function validateBirth() {

    syncBirthFormState();


    const year =
        Number(
            formState.birthYear
        );


    const month =
        Number(
            formState.birthMonth
        );


    const day =
        Number(
            formState.birthDay
        );


    if (
        !Number.isInteger(
            year
        )
        ||
        year < 1900
        ||
        year > 2100
    ) {

        birthError.textContent =
            "출생 연도를 확인해주세요.";

        return false;

    }


    if (
        !Number.isInteger(
            month
        )
        ||
        month < 1
        ||
        month > 12
    ) {

        birthError.textContent =
            "출생 월을 확인해주세요.";

        return false;

    }


    if (
        !Number.isInteger(
            day
        )
        ||
        day < 1
        ||
        day > 31
    ) {

        birthError.textContent =
            "출생 일을 확인해주세요.";

        return false;

    }


    if (
        formState.calendarType
        === "SOLAR"
    ) {

        const date =
            new Date(
                year,
                month - 1,
                day
            );


        if (
            date.getFullYear()
            !== year
            ||
            date.getMonth()
            !== month - 1
            ||
            date.getDate()
            !== day
        ) {

            birthError.textContent =
                "올바른 생년월일을 입력해주세요.";

            return false;

        }

    }


    if (
        !formState.birthTimeUnknown
    ) {

        const hour =
            Number(
                formState.birthHour
            );


        const minute =
            Number(
                formState.birthMinute
            );


        if (
            !Number.isInteger(
                hour
            )
            ||
            hour < 0
            ||
            hour > 23
        ) {

            birthError.textContent =
                "태어난 시간을 확인해주세요.";

            return false;

        }


        if (
            !Number.isInteger(
                minute
            )
            ||
            minute < 0
            ||
            minute > 59
        ) {

            birthError.textContent =
                "태어난 분을 확인해주세요.";

            return false;

        }

    }


    birthError.textContent =
        "";


    return true;

}


function updateBirthValidation() {

    syncBirthFormState();


    const hasDate =
        formState.birthYear
        &&
        formState.birthMonth
        &&
        formState.birthDay;


    const hasTime =
        formState.birthTimeUnknown
        ||
        (
            formState.birthHour
            !== ""
            &&
            formState.birthMinute
            !== ""
        );


    birthNextButton.disabled =
        !(
            hasDate
            &&
            hasTime
        );


    if (
        hasDate
        &&
        hasTime
    ) {

        birthError.textContent =
            "";

    }

}


function updateCalendarUI() {

    const lunar =
        formState.calendarType
        === "LUNAR";


    lunarLeapArea.classList.toggle(
        "hidden",
        !lunar
    );


    if (
        !lunar
    ) {

        lunarLeapMonth.checked =
            false;


        formState.leapMonth =
            false;

    }

}


function updateTimeUI() {

    const unknown =
        birthTimeUnknown.checked;


    timeInputArea.classList.toggle(
        "is-disabled",
        unknown
    );


    birthHour.disabled =
        unknown;


    birthMinute.disabled =
        unknown;

}



/* =========================================================
   REVIEW
   ========================================================= */

function bindReview() {

    reviewCompleteButton.addEventListener(
        "click",
        () => {

            saveForm();

            showScreen(
                "complete"
            );

        }
    );

}


function renderReview() {

    setText(
        "reviewName",
        formState.name
    );


    setText(
        "reviewGender",
        formState.gender
        === "MALE"
            ? "남성"
            : "여성"
    );


    setText(
        "reviewCalendar",
        formState.calendarType
        === "SOLAR"
            ? "양력"
            : "음력"
    );


    const leapRow =
        optional(
            "reviewLeapRow"
        );


    if (
        formState.calendarType
        === "LUNAR"
    ) {

        leapRow
            ?.classList.remove(
                "hidden"
            );


        setText(
            "reviewLeap",
            formState.leapMonth
                ? "윤달"
                : "평달"
        );

    }
    else {

        leapRow
            ?.classList.add(
                "hidden"
            );

    }


    setText(
        "reviewBirthDate",
        `${formState.birthYear}년 ${Number(formState.birthMonth)}월 ${Number(formState.birthDay)}일`
    );


    setText(
        "reviewBirthTime",
        formState.birthTimeUnknown
            ? "태어난 시간 모름"
            : `${pad2(formState.birthHour)}:${pad2(formState.birthMinute)}`
    );

}



/* =========================================================
   ANALYSIS
   ========================================================= */

function bindAnalysis() {

    startAnalysisButton.addEventListener(
        "click",
        startAnalysis
    );


    skipFactsButton.addEventListener(
        "click",
        () => {

            skipFactsRequested =
                true;


            analysisFactOverlay
                .classList.remove(
                    "is-show"
                );


            stopVideo(
                factMotionVideo
            );

        }
    );


    revealSkipButton
        ?.addEventListener(
            "click",
            showResult
        );

}


async function startAnalysis() {

    if (
        analysisRunning
    ) {

        return;

    }


    analysisRunning =
        true;


    skipFactsRequested =
        false;


    showScreen(
        "analysis"
    );


    resetAnalysisUI();


    const startedAt =
        Date.now();


    const progressController =
        startProgressAnimation();


    try {

        if (
            analysisVideo1
        ) {

            analysisVideo1
                .classList.remove(
                    "hidden"
                );


            analysisVideo2
                ?.classList.add(
                    "hidden"
                );


            await playVideo(
                analysisVideo1,
                VIDEO_PATH.analysis1,
                false
            );

        }


        analysisStatus.textContent =
            "사주 분석 중";


        analysisTitle.textContent =
            "당신의 천명록을 만들고 있습니다";


        analysisMessage.textContent =
            "타고난 명과 현재의 흐름을 분야별로 풀어가고 있습니다.";


        const response =
            await fetch(
                API_URL,
                {

                    method:
                        "POST",

                    headers: {

                        "Content-Type":
                            "application/json"

                    },

                    body:
                        JSON.stringify(
                            createAnalysisRequest()
                        )

                }
            );


        if (
            !response.ok
        ) {

            const message =
                await safeErrorBody(
                    response
                );


            throw new Error(
                `사주 분석 요청 실패 (${response.status}) ${message}`
            );

        }


        const rawResult =
            await response.json();


        currentResult =
            normalizeResult(
                rawResult
            );


        sessionStorage.setItem(
            RESULT_STORAGE_KEY,
            JSON.stringify(
                currentResult
            )
        );


        const elapsed =
            Date.now()
            -
            startedAt;


        if (
            elapsed
            < MINIMUM_ANALYSIS_TIME
        ) {

            await delay(
                MINIMUM_ANALYSIS_TIME
                -
                elapsed
            );

        }


        if (
            analysisVideo2
        ) {

            analysisVideo1
                ?.classList.add(
                    "hidden"
                );


            analysisVideo2
                .classList.remove(
                    "hidden"
                );


            await playVideo(
                analysisVideo2,
                VIDEO_PATH.analysis2,
                false
            );

        }


        finishProgress(
            progressController
        );


        await showCategoryFlow(
            currentResult.consultations
        );


        await playReveal();


    } catch (
        error
    ) {

        console.error(
            error
        );


        finishProgress(
            progressController
        );


        showToast(
            error.message
            || "사주 분석 중 오류가 발생했습니다."
        );


        showScreen(
            "complete"
        );

    } finally {

        analysisRunning =
            false;

    }

}


function createAnalysisRequest() {

    const now =
        new Date();


    const targetYear =
        now.getFullYear();


    const luckReferenceDate =
        [
            now.getFullYear(),
            pad2(
                now.getMonth()
                +
                1
            ),
            pad2(
                now.getDate()
            )
        ].join(
            "-"
        );


    const birthDate =
        [
            formState.birthYear,
            pad2(
                formState.birthMonth
            ),
            pad2(
                formState.birthDay
            )
        ].join(
            "-"
        );


    const birthTime =
        formState.birthTimeUnknown
            ? null
            : `${pad2(formState.birthHour)}:${pad2(formState.birthMinute)}`;


    return {

        calendarType:
            formState.calendarType,

        birthDate:
            birthDate,

        birthTime:
            birthTime,

        birthTimeUnknown:
            formState.birthTimeUnknown,

        gender:
            formState.gender,

        leapMonth:
            formState.calendarType
            === "LUNAR"
                ? formState.leapMonth
                : false,

        targetYear:
            targetYear,

        luckReferenceDate:
            luckReferenceDate,

        consultationCategories:
            CATEGORY_ORDER,

        consultationQuestion:
            null

    };

}


function resetAnalysisUI() {

    analysisStatus.textContent =
        "천명 분석 중";


    analysisTitle.textContent =
        "사주팔자를 계산하고 있습니다";


    analysisMessage.textContent =
        "타고난 명과 흐르는 운을 하나씩 풀어가고 있습니다.";


    analysisProgressPercent.textContent =
        "0%";


    analysisProgressBar.style.width =
        "0%";


    analysisElements.forEach(
        element => {

            element.classList.remove(
                "hidden"
            );

        }
    );


    analysisFactOverlay.classList.remove(
        "is-show"
    );


    document
        .querySelectorAll(
            ".element-grid span"
        )
        .forEach(
            element => {

                element.classList.remove(
                    "is-active"
                );

            }
        );

}


function startProgressAnimation() {

    let progress =
        0;


    const fiveElements =
        Array.from(
            document.querySelectorAll(
                ".element-grid span"
            )
        );


    const interval =
        window.setInterval(
            () => {

                if (
                    progress
                    >= WAIT_PROGRESS_LIMIT
                ) {

                    return;

                }


                progress +=
                    progress < 60
                        ? 1.4
                        : 0.35;


                progress =
                    Math.min(
                        progress,
                        WAIT_PROGRESS_LIMIT
                    );


                setAnalysisProgress(
                    progress
                );


                if (
                    fiveElements.length
                    > 0
                ) {

                    const activeCount =
                        Math.min(
                            fiveElements.length,
                            Math.ceil(
                                (
                                    progress
                                    /
                                    WAIT_PROGRESS_LIMIT
                                )
                                *
                                fiveElements.length
                            )
                        );


                    fiveElements.forEach(
                        (
                            element,
                            index
                        ) => {

                            element.classList.toggle(
                                "is-active",
                                index
                                < activeCount
                            );

                        }
                    );

                }

            },
            80
        );


    return {

        interval

    };

}


function finishProgress(
    controller
) {

    if (
        controller
        ?.interval
    ) {

        clearInterval(
            controller.interval
        );

    }


    setAnalysisProgress(
        100
    );

}


function setAnalysisProgress(
    value
) {

    const normalized =
        Math.max(
            0,
            Math.min(
                100,
                Math.round(
                    value
                )
            )
        );


    analysisProgressPercent.textContent =
        `${normalized}%`;


    analysisProgressBar.style.width =
        `${normalized}%`;

}



/* =========================================================
   CATEGORY FLOW
   ========================================================= */

async function showCategoryFlow(
    consultations
) {

    if (
        !Array.isArray(
            consultations
        )
        ||
        consultations.length
        === 0
    ) {

        return;

    }


    skipFactsRequested =
        false;


    for (
        let index = 0;
        index < consultations.length;
        index++
    ) {

        if (
            skipFactsRequested
        ) {

            break;

        }


        const consultation =
            consultations[index];


        renderCategoryFlow(
            consultation,
            index,
            consultations.length
        );


        analysisFactOverlay
            .classList.add(
                "is-show"
            );


        await delay(
            FACT_DISPLAY_TIME
        );

    }


    analysisFactOverlay
        .classList.remove(
            "is-show"
        );


    stopVideo(
        factMotionVideo
    );

}


function renderCategoryFlow(
    consultation,
    index,
    total
) {

    const category =
        consultation.category;


    const meta =
        CATEGORY_META[category]
        ||
        {

            number:
                index + 1,

            chapter:
                `제${index + 1}장`,

            label:
                category,

            analyzing:
                "당신의 흐름을 살펴보고 있습니다."

        };


    factCategoryIndex.textContent =
        `${pad2(index + 1)} / ${pad2(total)}`;


    factCategoryKicker.textContent =
        meta.chapter;


    factCategoryTitle.textContent =
        meta.label;


    factCategoryMessage.textContent =
        meta.analyzing;


    playCategoryMotion(
        category
    );

}


function playCategoryMotion(
    category
) {

    if (
        !factMotionVideo
    ) {

        return;

    }


    let path =
        null;


    switch (
        category
    ) {

        case "SOCIAL_RELATIONSHIP":

            path =
                VIDEO_PATH.network;

            break;


        case "LOVE_STYLE":

        case "PARTNER_LUCK":

            path =
                VIDEO_PATH.love;

            break;


        case "WEALTH":

        case "CAREER":

            path =
                VIDEO_PATH.wealth;

            break;


        default:

            path =
                null;

    }


    if (
        !path
    ) {

        stopVideo(
            factMotionVideo
        );


        factMotionVideo
            .removeAttribute(
                "src"
            );


        return;

    }


    factMotionVideo.src =
        path;


    factMotionVideo.load();


    factMotionVideo
        .play()
        .catch(
            () => {

            }
        );

}



/* =========================================================
   REVEAL
   ========================================================= */

async function playReveal() {

    showScreen(
        "reveal"
    );


    renderRevealPillars();


    revealOpening
        ?.classList.remove(
            "is-visible",
            "is-hidden"
        );


    revealPillarGrid
        ?.classList.remove(
            "is-visible"
        );


    revealFinal
        ?.classList.remove(
            "is-visible"
        );


    revealOpening
        ?.classList.add(
            "is-visible"
        );


    await delay(
        1300
    );


    revealOpening
        ?.classList.add(
            "is-hidden"
        );


    await delay(
        450
    );


    revealPillarGrid
        ?.classList.add(
            "is-visible"
        );


    const pillars =
        Array.from(
            document.querySelectorAll(
                "[data-reveal-pillar]"
            )
        );


    for (
        const pillar
        of pillars
    ) {

        pillar.classList.add(
            "is-visible"
        );


        await delay(
            260
        );

    }


    await delay(
        1500
    );


    revealPillarGrid
        ?.classList.remove(
            "is-visible"
        );


    await delay(
        300
    );


    revealFinal
        ?.classList.add(
            "is-visible"
        );


    await delay(
        1600
    );


    showResult();

}


function renderRevealPillars() {

    const pillars =
        extractPillars(
            currentResult
        );


    setPillarText(
        "revealYearChinese",
        "revealYearKorean",
        pillars[0]
    );


    setPillarText(
        "revealMonthChinese",
        "revealMonthKorean",
        pillars[1]
    );


    setPillarText(
        "revealDayChinese",
        "revealDayKorean",
        pillars[2]
    );


    setPillarText(
        "revealHourChinese",
        "revealHourKorean",
        pillars[3]
    );

}


function setPillarText(
    chineseId,
    koreanId,
    pillar
) {

    setText(
        chineseId,
        pillar?.chinese
        || "-"
    );


    setText(
        koreanId,
        pillar?.korean
        || "-"
    );

}



/* =========================================================
   RESULT
   ========================================================= */

function showResult() {

    if (
        !currentResult
    ) {

        const stored =
            sessionStorage.getItem(
                RESULT_STORAGE_KEY
            );


        if (
            stored
        ) {

            try {

                currentResult =
                    JSON.parse(
                        stored
                    );

            } catch (
                ignored
            ) {

                currentResult =
                    null;

            }

        }

    }


    if (
        !currentResult
    ) {

        showToast(
            "분석 결과를 찾을 수 없습니다."
        );


        showScreen(
            "complete"
        );


        return;

    }


    currentStoryIndex =
        0;


    renderResultHeader();

    renderResultPillars();

    renderCurrentStory();


    showScreen(
        "result"
    );

}


function renderResultHeader() {

    resultTitle.textContent =
        formState.name
            ? `${formState.name}님의 천명록`
            : "천명록";


    resultSummary.textContent =
        "타고난 명과 흐르는 운을 열한 장의 기록으로 정리했습니다.";

}


function renderResultPillars() {

    const pillars =
        extractPillars(
            currentResult
        );


    const labels = [

        "年柱",

        "月柱",

        "日柱",

        "時柱"

    ];


    const koreanLabels = [

        "년주",

        "월주",

        "일주",

        "시주"

    ];


    resultPillars.innerHTML =
        "";


    pillars.forEach(
        (
            pillar,
            index
        ) => {

            const article =
                document.createElement(
                    "article"
                );


            article.className =
                "pillar-card";


            article.innerHTML =
                `
                    <span>
                        ${escapeHtml(labels[index])}
                    </span>

                    <strong>
                        ${escapeHtml(pillar.chinese || "-")}
                    </strong>

                    <small>
                        ${escapeHtml(koreanLabels[index])}
                        ${pillar.korean ? ` · ${escapeHtml(pillar.korean)}` : ""}
                    </small>
                `;


            resultPillars.appendChild(
                article
            );

        }
    );

}



/* =========================================================
   STORY BOOK
   ========================================================= */

function renderCurrentStory() {

    if (
        !currentResult
        ||
        !Array.isArray(
            currentResult.consultations
        )
    ) {

        return;

    }


    const consultations =
        currentResult.consultations;


    if (
        currentStoryIndex
        >= consultations.length
    ) {

        renderStoryComplete();

        return;

    }


    const consultation =
        consultations[currentStoryIndex];


    renderStoryBook(
        consultation,
        currentStoryIndex,
        consultations.length
    );

}


function renderStoryBook(
    consultation,
    index,
    total
) {

    const meta =
        CATEGORY_META[
            consultation.category
        ]
        ||
        {

            number:
                index + 1,

            chapter:
                `제${index + 1}장`,

            label:
                consultation.category,

            title:
                consultation.category

        };


    const progress =
        (
            (
                index + 1
            )
            /
            total
        )
        *
        100;


    const unavailable =
        isUnavailableConsultation(
            consultation
        );


    /*
     * =================================================
     * 중요
     * =================================================
     *
     * 최종 화면은 storyText만 사용한다.
     *
     * narrationText를 fallback으로 사용하지 않는다.
     *
     * OpenAI 문장화가 실패한 경우
     * Legacy 백엔드 문장이 사용자에게 다시 노출되는 것을
     * 방지한다.
     */

    const storyText =
        consultation.storyText
        ||
        "현재 이 장의 상담문을 표시할 수 없습니다.";


    const paragraphs =
        splitParagraphs(
            storyText
        );


    resultStoryList.innerHTML =
        `
            <div class="cheonmyeong-book">

                <div class="cheonmyeong-book-progress">

                    <div class="cheonmyeong-book-progress-head">

                        <span>
                            ${escapeHtml(meta.chapter)}
                        </span>

                        <strong>
                            ${pad2(index + 1)} / ${pad2(total)}
                        </strong>

                    </div>

                    <div class="cheonmyeong-book-progress-track">

                        <div
                            class="cheonmyeong-book-progress-bar"
                            style="width:${progress}%"
                        ></div>

                    </div>

                </div>


                <article class="story-page">

                    <header class="story-page-header">

                        <span class="story-page-book-label">
                            天命錄
                        </span>

                        <span class="story-page-chapter">
                            ${escapeHtml(meta.chapter)}
                        </span>

                        <h2>
                            ${escapeHtml(meta.title)}
                        </h2>

                        <p>
                            ${escapeHtml(meta.label)}
                        </p>

                    </header>


                    <div class="story-page-seal">
                        命
                    </div>


                    <div class="story-page-body">

                        ${
                            paragraphs
                                .map(
                                    paragraph =>
                                        `
                                            <p class="${unavailable ? "story-unavailable" : ""}">
                                                ${escapeHtml(paragraph)}
                                            </p>
                                        `
                                )
                                .join("")
                        }

                    </div>

                </article>


                <div class="cheonmyeong-book-navigation">

                    <button
                        class="cheonmyeong-book-nav-button"
                        type="button"
                        data-story-prev
                        ${index === 0 ? "disabled" : ""}
                    >
                        ‹ 이전 장
                    </button>


                    <button
                        class="cheonmyeong-book-nav-button is-next ${index === total - 1 ? "is-finish" : ""}"
                        type="button"
                        data-story-next
                    >
                        ${
                            index === total - 1
                                ? "천명록 마치기"
                                : "다음 장 ›"
                        }
                    </button>

                </div>


                <p class="cheonmyeong-book-swipe-hint">
                    좌우로 넘겨 다음 장을 읽을 수 있습니다.
                </p>

            </div>
        `;


    bindRenderedStoryButtons();

}


function bindRenderedStoryButtons() {

    resultStoryList
        .querySelector(
            "[data-story-prev]"
        )
        ?.addEventListener(
            "click",
            goPreviousStory
        );


    resultStoryList
        .querySelector(
            "[data-story-next]"
        )
        ?.addEventListener(
            "click",
            goNextStory
        );


    resultStoryList
        .querySelector(
            "[data-story-restart]"
        )
        ?.addEventListener(
            "click",
            () => {

                currentStoryIndex =
                    0;


                renderCurrentStory();

                scrollStoryTop();

            }
        );


    resultStoryList
        .querySelector(
            "[data-story-finish]"
        )
        ?.addEventListener(
            "click",
            goHome
        );

}


function goPreviousStory() {

    if (
        !currentResult
        ||
        currentStoryIndex
        <= 0
    ) {

        return;

    }


    currentStoryIndex--;


    renderCurrentStory();

    scrollStoryTop();

}


function goNextStory() {

    if (
        !currentResult
    ) {

        return;

    }


    currentStoryIndex++;


    renderCurrentStory();

    scrollStoryTop();

}


function renderStoryComplete() {

    resultStoryList.innerHTML =
        `
            <div class="cheonmyeong-book-complete">

                <div class="cheonmyeong-book-complete-seal">
                    錄
                </div>

                <p class="cheonmyeong-book-complete-kicker">
                    天命十一章 完
                </p>

                <h2>
                    천명록 완독
                </h2>

                <p class="cheonmyeong-book-complete-copy">
                    열한 장의 기록을 모두 펼쳤습니다.
                </p>

                <p class="cheonmyeong-book-complete-subcopy">
                    사주는 삶을 대신 결정하는 답이 아니라,
                    자신이 가진 흐름을 이해하는 하나의 지도입니다.
                </p>

                <div class="cheonmyeong-book-complete-actions">

                    <button
                        class="cheonmyeong-book-complete-button is-secondary"
                        type="button"
                        data-story-restart
                    >
                        처음부터 다시 읽기
                    </button>

                    <button
                        class="cheonmyeong-book-complete-button is-primary"
                        type="button"
                        data-story-finish
                    >
                        천명록 마치기
                    </button>

                </div>

            </div>
        `;


    bindRenderedStoryButtons();

}


function scrollStoryTop() {

    resultStoryList.scrollIntoView({

        behavior:
            "smooth",

        block:
            "start"

    });

}



/* =========================================================
   SWIPE / KEYBOARD
   ========================================================= */

function bindStoryNavigation() {

    resultStoryList.addEventListener(
        "touchstart",
        event => {

            touchStartX =
                event
                    .changedTouches[0]
                    .clientX;

        },
        {
            passive:
                true
        }
    );


    resultStoryList.addEventListener(
        "touchend",
        event => {

            if (
                touchStartX
                === null
            ) {

                return;

            }


            const endX =
                event
                    .changedTouches[0]
                    .clientX;


            const delta =
                endX
                -
                touchStartX;


            touchStartX =
                null;


            if (
                Math.abs(
                    delta
                )
                < 55
            ) {

                return;

            }


            if (
                delta < 0
            ) {

                goNextStory();

            }
            else {

                goPreviousStory();

            }

        },
        {
            passive:
                true
        }
    );


    document.addEventListener(
        "keydown",
        event => {

            if (
                currentScreen
                !== "result"
            ) {

                return;

            }


            if (
                event.key
                === "ArrowLeft"
            ) {

                goPreviousStory();

            }


            if (
                event.key
                === "ArrowRight"
            ) {

                goNextStory();

            }

        }
    );

}



/* =========================================================
   NORMALIZE RESULT
   ========================================================= */

function normalizeResult(
    raw
) {

    const consultations =
        Array.isArray(
            raw?.consultations
        )
            ? raw.consultations
            : [];


    const byCategory =
        new Map();


    consultations.forEach(
        consultation => {

            if (
                consultation?.category
            ) {

                byCategory.set(
                    consultation.category,
                    consultation
                );

            }

        }
    );


    const normalizedConsultations =
        CATEGORY_ORDER.map(
            category => {

                const source =
                    byCategory.get(
                        category
                    )
                    ||
                    {};


                return {

                    ...source,

                    category:
                        category,

                    /*
                     * 중요:
                     *
                     * storyText만 사용한다.
                     *
                     * 기존 narrationText는
                     * 사용자 화면용 fallback으로 사용하지 않는다.
                     */
                    storyText:
                        normalizeStoryText(
                            source.storyText
                            ||
                            ""
                        )

                };

            }
        );


    return {

        ...raw,

        consultations:
            normalizedConsultations

    };

}


function normalizeStoryText(
    value
) {

    if (
        value === null
        ||
        value === undefined
    ) {

        return "";

    }


    return String(
        value
    )
        .trim();

}



/* =========================================================
   PILLARS
   ========================================================= */

function extractPillars(
    result
) {

    const empty = {

        chinese:
            "-",

        korean:
            ""

    };


    if (
        !result
    ) {

        return [
            {...empty},
            {...empty},
            {...empty},
            {...empty}
        ];

    }


    /*
     * =================================================
     * 현재 Dajeom SajuAnalysisResponse 공식 구조
     * =================================================
     *
     * 백엔드 응답 최상위:
     *
     * yearPillar
     * monthPillar
     * dayPillar
     * hourPillar
     */

    if (
        result.yearPillar
        ||
        result.monthPillar
        ||
        result.dayPillar
        ||
        result.hourPillar
    ) {

        return [

            normalizeSinglePillar(
                result.yearPillar
            ),

            normalizeSinglePillar(
                result.monthPillar
            ),

            normalizeSinglePillar(
                result.dayPillar
            ),

            normalizeSinglePillar(
                result.hourPillar
            )

        ];

    }


    /*
     * =================================================
     * 이전 구조 호환
     * =================================================
     */

    const candidates = [

        result.pillars,

        result.fourPillars,

        result.saju?.pillars,

        result.saju?.fourPillars,

        result.chart?.pillars

    ];


    for (
        const candidate
        of candidates
    ) {

        const parsed =
            normalizePillarCandidate(
                candidate
            );


        if (
            parsed
        ) {

            return parsed;

        }

    }


    return [
        {...empty},
        {...empty},
        {...empty},
        {...empty}
    ];

}


function normalizePillarCandidate(
    candidate
) {

    if (
        !candidate
    ) {

        return null;

    }


    if (
        Array.isArray(
            candidate
        )
        &&
        candidate.length
        >= 4
    ) {

        return candidate
            .slice(
                0,
                4
            )
            .map(
                normalizeSinglePillar
            );

    }


    if (
        typeof candidate
        === "object"
    ) {

        const values = [

            candidate.year
            || candidate.yearPillar,

            candidate.month
            || candidate.monthPillar,

            candidate.day
            || candidate.dayPillar,

            candidate.hour
            || candidate.time
            || candidate.hourPillar

        ];


        if (
            values.some(
                Boolean
            )
        ) {

            return values.map(
                normalizeSinglePillar
            );

        }

    }


    return null;

}


function normalizeSinglePillar(
    pillar
) {

    if (
        !pillar
    ) {

        return {

            chinese:
                "-",

            korean:
                ""

        };

    }


    if (
        typeof pillar
        === "string"
    ) {

        return {

            chinese:
                pillar,

            korean:
                ""

        };

    }


    /*
     * =================================================
     * 현재 PillarResponse 공식 필드
     * =================================================
     *
     * koreanName
     * chineseName
     * stemKoreanName
     * stemChineseCharacter
     * branchKoreanName
     * branchChineseCharacter
     */

    const directChinese =
        pillar.chineseName
        ||
        pillar.chinese
        ||
        pillar.ganzhi
        ||
        pillar.ganji
        ||
        pillar.display
        ||
        "";


    const directKorean =
        pillar.koreanName
        ||
        pillar.korean
        ||
        pillar.reading
        ||
        pillar.hangul
        ||
        "";


    if (
        directChinese
        ||
        directKorean
    ) {

        return {

            chinese:
                directChinese
                || "-",

            korean:
                directKorean

        };

    }


    /*
     * =================================================
     * Stem / Branch 별도 필드 호환
     * =================================================
     */

    const stemChinese =
        pillar.stemChineseCharacter
        ||
        pillar.heavenlyStem
        ||
        pillar.stem
        ||
        pillar.gan
        ||
        "";


    const branchChinese =
        pillar.branchChineseCharacter
        ||
        pillar.earthlyBranch
        ||
        pillar.branch
        ||
        pillar.ji
        ||
        "";


    const stemKorean =
        pillar.stemKoreanName
        ||
        "";


    const branchKorean =
        pillar.branchKoreanName
        ||
        "";


    const chinese =
        `${stemChinese}${branchChinese}`;


    const korean =
        `${stemKorean}${branchKorean}`;


    return {

        chinese:
            chinese
            ||
            "-",

        korean:
            korean

    };

}



/* =========================================================
   UNAVAILABLE
   ========================================================= */

function isUnavailableConsultation(
    consultation
) {

    if (
        consultation?.available
        === false
    ) {

        return true;

    }


    /*
     * narrationText는 검사하지 않는다.
     *
     * 최종 사용자 화면은 storyText만 기준으로 한다.
     */

    const text =
        String(
            consultation?.storyText
            ||
            ""
        );


    return text.includes(
        "현재 계산 근거만으로는 이 분야의 독립적인 해석을 확정하지 않습니다"
    )
    ||
    text.includes(
        "근거가 없는 내용을 추정해서 채우지 않았습니다"
    );

}



/* =========================================================
   STORY PARAGRAPH
   ========================================================= */

function splitParagraphs(
    text
) {

    const normalized =
        String(
            text
            || ""
        )
        .replace(
            /\r\n/g,
            "\n"
        )
        .trim();


    if (
        !normalized
    ) {

        return [
            "해석 내용이 없습니다."
        ];

    }


    const directParagraphs =
        normalized
            .split(
                /\n{2,}/
            )
            .map(
                paragraph =>
                    paragraph.trim()
            )
            .filter(
                Boolean
            );


    if (
        directParagraphs.length
        > 1
    ) {

        return directParagraphs;

    }


    const sentences =
        normalized
            .split(
                /(?<=[.!?。])\s+/
            )
            .map(
                sentence =>
                    sentence.trim()
            )
            .filter(
                Boolean
            );


    if (
        sentences.length
        <= 3
    ) {

        return [
            normalized
        ];

    }


    const paragraphs =
        [];


    let current =
        [];


    sentences.forEach(
        sentence => {

            current.push(
                sentence
            );


            if (
                current.length
                >= 3
            ) {

                paragraphs.push(
                    current.join(
                        " "
                    )
                );


                current =
                    [];

            }

        }
    );


    if (
        current.length
        > 0
    ) {

        paragraphs.push(
            current.join(
                " "
            )
        );

    }


    return paragraphs;

}



/* =========================================================
   VIDEO
   ========================================================= */

async function playVideo(
    element,
    source,
    restart
) {

    if (
        !element
    ) {

        return false;

    }


    if (
        source
        &&
        element.getAttribute(
            "src"
        )
        !== source
    ) {

        element.src =
            source;


        element.load();

    }


    if (
        restart
    ) {

        try {

            element.currentTime =
                0;

        } catch (
            ignored
        ) {

        }

    }


    try {

        const playPromise =
            element.play();


        if (
            playPromise
        ) {

            await playPromise;

        }

    } catch (
        error
    ) {

        console.warn(
            "Video play failed:",
            source,
            error
        );


        return false;

    }


    return new Promise(
        resolve => {

            let finished =
                false;


            const finish =
                () => {

                    if (
                        finished
                    ) {

                        return;

                    }


                    finished =
                        true;


                    cleanup();


                    resolve(
                        true
                    );

                };


            const cleanup =
                () => {

                    element.removeEventListener(
                        "ended",
                        finish
                    );


                    element.removeEventListener(
                        "error",
                        finish
                    );

                };


            element.addEventListener(
                "ended",
                finish,
                {
                    once:
                        true
                }
            );


            element.addEventListener(
                "error",
                finish,
                {
                    once:
                        true
                }
            );


            if (
                element.ended
            ) {

                finish();

            }

        }
    );

}


function stopVideo(
    element
) {

    if (
        !element
    ) {

        return;

    }


    try {

        element.pause();

    } catch (
        ignored
    ) {

    }

}



/* =========================================================
   STORAGE
   ========================================================= */

function saveForm() {

    sessionStorage.setItem(
        FORM_STORAGE_KEY,
        JSON.stringify(
            formState
        )
    );

}


function loadStoredForm() {

    const stored =
        sessionStorage.getItem(
            FORM_STORAGE_KEY
        );


    if (
        !stored
    ) {

        return;

    }


    try {

        const parsed =
            JSON.parse(
                stored
            );


        formState = {

            ...formState,

            ...parsed

        };

    } catch (
        ignored
    ) {

    }

}


function restoreFormInputs() {

    nameInput.value =
        formState.name
        || "";


    birthYear.value =
        formState.birthYear
        || "";


    birthMonth.value =
        formState.birthMonth
        || "";


    birthDay.value =
        formState.birthDay
        || "";


    birthHour.value =
        formState.birthHour
        || "";


    birthMinute.value =
        formState.birthMinute
        || "";


    birthTimeUnknown.checked =
        Boolean(
            formState.birthTimeUnknown
        );


    lunarLeapMonth.checked =
        Boolean(
            formState.leapMonth
        );


    document
        .querySelectorAll(
            "[data-calendar]"
        )
        .forEach(
            button => {

                button.classList.toggle(
                    "is-active",
                    button.dataset.calendar
                    === formState.calendarType
                );

            }
        );


    document
        .querySelectorAll(
            "[data-gender]"
        )
        .forEach(
            button => {

                button.classList.toggle(
                    "is-active",
                    button.dataset.gender
                    === formState.gender
                );

            }
        );


    updateCalendarUI();

    updateTimeUI();

}



/* =========================================================
   ERROR
   ========================================================= */

async function safeErrorBody(
    response
) {

    try {

        const text =
            await response.text();


        if (
            !text
        ) {

            return "";

        }


        return text.length
        > 500
            ? text.substring(
                0,
                500
            )
            : text;

    } catch (
        ignored
    ) {

        return "";

    }

}



/* =========================================================
   TOAST
   ========================================================= */

let toastTimer =
    null;


function showToast(
    message
) {

    if (
        !message
    ) {

        return;

    }


    toast.textContent =
        message;


    toast.classList.add(
        "is-show"
    );


    if (
        toastTimer
    ) {

        clearTimeout(
            toastTimer
        );

    }


    toastTimer =
        setTimeout(
            () => {

                toast.classList.remove(
                    "is-show"
                );

            },
            3500
        );

}



/* =========================================================
   UTILITY
   ========================================================= */

function required(
    id
) {

    const element =
        document.getElementById(
            id
        );


    if (
        !element
    ) {

        throw new Error(
            `필수 DOM을 찾을 수 없습니다: #${id}`
        );

    }


    return element;

}


function optional(
    id
) {

    return document.getElementById(
        id
    );

}


function setText(
    id,
    value
) {

    const element =
        optional(
            id
        );


    if (
        element
    ) {

        element.textContent =
            value
            ?? "";

    }

}


function pad2(
    value
) {

    return String(
        value
    )
        .padStart(
            2,
            "0"
        );

}


function delay(
    milliseconds
) {

    return new Promise(
        resolve =>
            setTimeout(
                resolve,
                milliseconds
            )
    );

}


function escapeHtml(
    value
) {

    return String(
        value
        ?? ""
    )
        .replaceAll(
            "&",
            "&amp;"
        )
        .replaceAll(
            "<",
            "&lt;"
        )
        .replaceAll(
            ">",
            "&gt;"
        )
        .replaceAll(
            "\"",
            "&quot;"
        )
        .replaceAll(
            "'",
            "&#039;"
        );

}