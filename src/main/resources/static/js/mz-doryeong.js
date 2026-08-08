document.addEventListener("DOMContentLoaded", function () {

    const API_URL =
        "/api/saju/calculate";

    const STORAGE_KEY =
        "mzDoryeongForm";

    const MINIMUM_LOADING_TIME =
        1800;


    /*
     * MZ 천명도령 상담 순서.
     *
     * 백엔드에서 consultations 배열의 순서가 달라져도
     * 프론트에서는 반드시 이 순서로 상담한다.
     */
    const CONSULTATION_ORDER = [

        "PERSONALITY",

        "PAST_LUCK",

        "SOCIAL_RELATIONSHIP",

        "LOVE_STYLE",

        "PARTNER_LUCK",

        "WEALTH",

        "CAREER",

        "LEGAL_RISK",

        "HEALTH_TENDENCY",

        "CURRENT_YEAR_LUCK",

        "FUTURE_LUCK"
    ];


    const CONSULTATION_NAMES = {

        PERSONALITY:
            "나의 기본 성향",

        PAST_LUCK:
            "지나온 흐름",

        SOCIAL_RELATIONSHIP:
            "주변 사람·대인관계",

        LOVE_STYLE:
            "연애 스타일",

        PARTNER_LUCK:
            "인연·배우자운",

        WEALTH:
            "재물운",

        CAREER:
            "직업·사업운",

        LEGAL_RISK:
            "분쟁·주의운",

        HEALTH_TENDENCY:
            "건강 관리 경향",

        CURRENT_YEAR_LUCK:
            "현재와 올해의 흐름",

        FUTURE_LUCK:
            "앞으로의 흐름"
    };


    const CONSULTATION_SHORT_NAMES = {

        PERSONALITY:
            "성향",

        PAST_LUCK:
            "과거운",

        SOCIAL_RELATIONSHIP:
            "대인관계",

        LOVE_STYLE:
            "연애",

        PARTNER_LUCK:
            "배우자",

        WEALTH:
            "재물",

        CAREER:
            "직업",

        LEGAL_RISK:
            "분쟁",

        HEALTH_TENDENCY:
            "건강",

        CURRENT_YEAR_LUCK:
            "올해운",

        FUTURE_LUCK:
            "미래운"
    };


    const screens =
        Array.from(
            document.querySelectorAll(
                "[data-screen]"
            )
        );


    const backButton =
        document.getElementById(
            "backButton"
        );

    const homeButton =
        document.getElementById(
            "homeButton"
        );

    const headerStep =
        document.getElementById(
            "headerStep"
        );


    const form =
        document.getElementById(
            "sajuInputForm"
        );

    const nameInput =
        document.getElementById(
            "nameInput"
        );

    const birthYear =
        document.getElementById(
            "birthYear"
        );

    const birthMonth =
        document.getElementById(
            "birthMonth"
        );

    const birthDay =
        document.getElementById(
            "birthDay"
        );

    const birthHour =
        document.getElementById(
            "birthHour"
        );

    const birthMinute =
        document.getElementById(
            "birthMinute"
        );

    const birthTimeUnknown =
        document.getElementById(
            "birthTimeUnknown"
        );

    const timeInputWrap =
        document.getElementById(
            "timeInputWrap"
        );

    const lunarLeapWrap =
        document.getElementById(
            "lunarLeapWrap"
        );

    const lunarLeapMonth =
        document.getElementById(
            "lunarLeapMonth"
        );

    const inputError =
        document.getElementById(
            "inputError"
        );


    const analyzeButton =
        document.getElementById(
            "analyzeButton"
        );


    const progressBar =
        document.getElementById(
            "progressBar"
        );

    const progressPercent =
        document.getElementById(
            "progressPercent"
        );

    const progressLabel =
        document.getElementById(
            "progressLabel"
        );

    const loadingMessage =
        document.getElementById(
            "loadingMessage"
        );

    const loadingSteps =
        Array.from(
            document.querySelectorAll(
                "[data-loading-step]"
            )
        );


    const resultCharacterWrap =
        document.getElementById(
            "resultCharacterWrap"
        );

    const characterSpeech =
        document.getElementById(
            "characterSpeech"
        );

    const currentConsultationNumber =
        document.getElementById(
            "currentConsultationNumber"
        );

    const currentConsultationTitle =
        document.getElementById(
            "currentConsultationTitle"
        );

    const consultationProgressText =
        document.getElementById(
            "consultationProgressText"
        );

    const consultationProgressBar =
        document.getElementById(
            "consultationProgressBar"
        );

    const consultationSequence =
        document.getElementById(
            "consultationSequence"
        );

    const speechStatus =
        document.getElementById(
            "speechStatus"
        );

    const pauseVoiceButton =
        document.getElementById(
            "pauseVoiceButton"
        );

    const replayVoiceButton =
        document.getElementById(
            "replayVoiceButton"
        );

    const restartConsultationButton =
        document.getElementById(
            "restartConsultationButton"
        );

    const consultationComplete =
        document.getElementById(
            "consultationComplete"
        );


    const retryButton =
        document.getElementById(
            "retryButton"
        );

    const resultHomeButton =
        document.getElementById(
            "resultHomeButton"
        );

    const toast =
        document.getElementById(
            "toast"
        );


    const state = {

        currentScreen:
            "intro",

        history:
            [],

        calendarType:
            "SOLAR",

        gender:
            "",

        result:
            null,

        orderedConsultations:
            [],

        currentConsultationIndex:
            0,

        currentUtterance:
            null,

        speaking:
            false,

        paused:
            false,

        autoPlay:
            true,

        progress:
            0,

        progressTimer:
            null,

        toastTimer:
            null
    };


    initialize();


    function initialize() {

        bindNavigation();

        bindInputs();

        bindForm();

        bindAnalysis();

        bindConsultationControls();

        restoreForm();

        showScreen(
            "intro",
            false
        );
    }


    /* =====================================================
       NAVIGATION
       ===================================================== */

    function bindNavigation() {

        document.addEventListener(
            "click",
            function (event) {

                const button =
                    event.target.closest(
                        "[data-go]"
                    );

                if (!button) {

                    return;
                }


                stopSpeech();


                showScreen(
                    button.dataset.go
                );
            }
        );


        backButton.addEventListener(
            "click",
            function () {

                stopSpeech();


                const previous =
                    state.history.pop();


                if (previous) {

                    showScreen(
                        previous,
                        false
                    );

                    return;
                }


                window.location.href =
                    "/";
            }
        );


        homeButton.addEventListener(
            "click",
            function () {

                stopSpeech();


                window.location.href =
                    "/";
            }
        );


        retryButton.addEventListener(
            "click",
            function () {

                stopSpeech();


                showScreen(
                    "input"
                );
            }
        );


        resultHomeButton.addEventListener(
            "click",
            function () {

                stopSpeech();


                window.location.href =
                    "/";
            }
        );
    }


    function showScreen(
        screenName,
        addHistory
    ) {

        const shouldAddHistory =
            addHistory !== false;


        if (
            shouldAddHistory
            && screenName !== state.currentScreen
        ) {

            state.history.push(
                state.currentScreen
            );
        }


        screens.forEach(
            function (screen) {

                screen.classList.toggle(
                    "is-active",
                    screen.dataset.screen
                    === screenName
                );
            }
        );


        state.currentScreen =
            screenName;


        headerStep.textContent =
            getHeaderTitle(
                screenName
            );


        const loading =
            screenName === "loading";


        backButton.style.visibility =
            loading
                ? "hidden"
                : "visible";


        homeButton.style.visibility =
            loading
                ? "hidden"
                : "visible";


        if (
            screenName === "review"
        ) {

            renderReview();
        }


        window.scrollTo({
            top: 0,
            behavior: "auto"
        });
    }


    function getHeaderTitle(
        screenName
    ) {

        const titles = {

            intro:
                "정통 종합사주",

            input:
                "출생정보 입력",

            review:
                "출생정보 확인",

            loading:
                "사주 분석",

            result:
                "천명도령 상담"
        };


        return titles[screenName]
            || "MZ 천명도령";
    }


    /* =====================================================
       INPUT
       ===================================================== */

    function bindInputs() {

        document
            .querySelectorAll(
                ".numeric-input"
            )
            .forEach(
                function (input) {

                    input.addEventListener(
                        "input",
                        function () {

                            input.value =
                                input.value.replace(
                                    /[^0-9]/g,
                                    ""
                                );
                        }
                    );
                }
            );


        document
            .querySelectorAll(
                "[data-calendar]"
            )
            .forEach(
                function (button) {

                    button.addEventListener(
                        "click",
                        function () {

                            document
                                .querySelectorAll(
                                    "[data-calendar]"
                                )
                                .forEach(
                                    function (item) {

                                        item.classList.remove(
                                            "is-active"
                                        );
                                    }
                                );


                            button.classList.add(
                                "is-active"
                            );


                            state.calendarType =
                                button.dataset.calendar;


                            const lunar =
                                state.calendarType
                                === "LUNAR";


                            lunarLeapWrap
                                .classList
                                .toggle(
                                    "is-hidden",
                                    !lunar
                                );


                            if (!lunar) {

                                lunarLeapMonth.checked =
                                    false;
                            }
                        }
                    );
                }
            );


        document
            .querySelectorAll(
                "[data-gender]"
            )
            .forEach(
                function (button) {

                    button.addEventListener(
                        "click",
                        function () {

                            document
                                .querySelectorAll(
                                    "[data-gender]"
                                )
                                .forEach(
                                    function (item) {

                                        item.classList.remove(
                                            "is-active"
                                        );
                                    }
                                );


                            button.classList.add(
                                "is-active"
                            );


                            state.gender =
                                button.dataset.gender;
                        }
                    );
                }
            );


        birthTimeUnknown.addEventListener(
            "change",
            function () {

                const unknown =
                    birthTimeUnknown.checked;


                timeInputWrap
                    .classList
                    .toggle(
                        "is-disabled",
                        unknown
                    );


                birthHour.disabled =
                    unknown;


                birthMinute.disabled =
                    unknown;


                if (unknown) {

                    birthHour.value =
                        "";

                    birthMinute.value =
                        "";
                }
            }
        );
    }


    /* =====================================================
       FORM
       ===================================================== */

    function bindForm() {

        form.addEventListener(
            "submit",
            function (event) {

                event.preventDefault();


                inputError.textContent =
                    "";


                try {

                    const data =
                        collectFormData();


                    validateFormData(
                        data
                    );


                    saveForm(
                        data
                    );


                    renderReview();


                    showScreen(
                        "review"
                    );

                } catch (error) {

                    inputError.textContent =
                        error.message;
                }
            }
        );
    }


    function collectFormData() {

        const unknown =
            birthTimeUnknown.checked;


        return {

            name:
                nameInput.value.trim(),

            calendarType:
                state.calendarType,

            gender:
                state.gender,

            birthDate:
                birthYear.value
                + "-"
                + pad(
                    birthMonth.value
                )
                + "-"
                + pad(
                    birthDay.value
                ),

            birthTime:
                unknown

                    ? null

                    : pad(
                        birthHour.value
                    )
                    + ":"
                    + pad(
                        birthMinute.value
                    ),

            birthTimeUnknown:
                unknown,

            leapMonth:
                state.calendarType === "LUNAR"
                && lunarLeapMonth.checked
        };
    }


    function validateFormData(
        data
    ) {

        if (!data.name) {

            throw new Error(
                "이름 또는 닉네임을 입력해 주세요."
            );
        }


        if (
            data.name.length < 2
        ) {

            throw new Error(
                "이름은 두 글자 이상 입력해 주세요."
            );
        }


        if (!data.gender) {

            throw new Error(
                "성별을 선택해 주세요."
            );
        }


        const parts =
            data.birthDate.split(
                "-"
            );


        const year =
            Number(
                parts[0]
            );


        const month =
            Number(
                parts[1]
            );


        const day =
            Number(
                parts[2]
            );


        if (
            !year
            || !month
            || !day
        ) {

            throw new Error(
                "생년월일을 모두 입력해 주세요."
            );
        }


        if (
            month < 1
            || month > 12
        ) {

            throw new Error(
                "월은 1월부터 12월까지 입력할 수 있습니다."
            );
        }


        if (
            data.calendarType
            === "LUNAR"
        ) {

            if (
                day < 1
                || day > 30
            ) {

                throw new Error(
                    "음력 날짜는 1일부터 30일까지 입력할 수 있습니다."
                );
            }

        } else {

            if (
                !isValidSolarDate(
                    year,
                    month,
                    day
                )
            ) {

                throw new Error(
                    "올바른 양력 생년월일을 입력해 주세요."
                );
            }
        }


        if (
            !data.birthTimeUnknown
        ) {

            const timeParts =
                String(
                    data.birthTime
                )
                    .split(
                        ":"
                    );


            const hour =
                Number(
                    timeParts[0]
                );


            const minute =
                Number(
                    timeParts[1]
                );


            if (
                hour < 0
                || hour > 23
                || minute < 0
                || minute > 59
            ) {

                throw new Error(
                    "출생시간을 확인해 주세요."
                );
            }
        }
    }


    function isValidSolarDate(
        year,
        month,
        day
    ) {

        const date =
            new Date(
                year,
                month - 1,
                day
            );


        return (
            date.getFullYear() === year
            && date.getMonth() === month - 1
            && date.getDate() === day
        );
    }


    function saveForm(
        data
    ) {

        sessionStorage.setItem(
            STORAGE_KEY,
            JSON.stringify(
                data
            )
        );
    }


    function loadForm() {

        try {

            const text =
                sessionStorage.getItem(
                    STORAGE_KEY
                );


            if (!text) {

                return null;
            }


            return JSON.parse(
                text
            );

        } catch (error) {

            return null;
        }
    }


    function restoreForm() {

        const data =
            loadForm();


        if (!data) {

            return;
        }


        nameInput.value =
            data.name
            || "";


        state.calendarType =
            data.calendarType
            || "SOLAR";


        state.gender =
            data.gender
            || "";


        document
            .querySelectorAll(
                "[data-calendar]"
            )
            .forEach(
                function (button) {

                    button.classList.toggle(
                        "is-active",
                        button.dataset.calendar
                        === state.calendarType
                    );
                }
            );


        document
            .querySelectorAll(
                "[data-gender]"
            )
            .forEach(
                function (button) {

                    button.classList.toggle(
                        "is-active",
                        button.dataset.gender
                        === state.gender
                    );
                }
            );


        const lunar =
            state.calendarType
            === "LUNAR";


        lunarLeapWrap
            .classList
            .toggle(
                "is-hidden",
                !lunar
            );


        lunarLeapMonth.checked =
            Boolean(
                data.leapMonth
            );


        if (
            data.birthDate
        ) {

            const parts =
                data.birthDate.split(
                    "-"
                );


            birthYear.value =
                parts[0]
                || "";


            birthMonth.value =
                parts[1]
                || "";


            birthDay.value =
                parts[2]
                || "";
        }


        birthTimeUnknown.checked =
            Boolean(
                data.birthTimeUnknown
            );


        timeInputWrap
            .classList
            .toggle(
                "is-disabled",
                data.birthTimeUnknown
            );


        birthHour.disabled =
            data.birthTimeUnknown;


        birthMinute.disabled =
            data.birthTimeUnknown;


        if (
            data.birthTime
            && !data.birthTimeUnknown
        ) {

            const parts =
                data.birthTime.split(
                    ":"
                );


            birthHour.value =
                parts[0]
                || "";


            birthMinute.value =
                parts[1]
                || "";

        } else {

            birthHour.value =
                "";


            birthMinute.value =
                "";
        }
    }


    /* =====================================================
       REVIEW
       ===================================================== */

    function renderReview() {

        const data =
            loadForm()
            || collectFormData();


        setText(
            "reviewName",
            data.name
        );


        setText(
            "reviewGender",

            data.gender === "MALE"
                ? "남성"
                : "여성"
        );


        setText(
            "reviewCalendar",

            data.calendarType === "LUNAR"
                ? "음력"
                : "양력"
        );


        document
            .getElementById(
                "reviewLeapRow"
            )
            .classList
            .toggle(
                "is-hidden",
                data.calendarType
                !== "LUNAR"
            );


        setText(
            "reviewLeap",

            data.leapMonth
                ? "윤달"
                : "평달"
        );


        setText(
            "reviewBirthDate",
            data.birthDate
        );


        setText(
            "reviewBirthTime",

            data.birthTimeUnknown
                ? "시간 모름"
                : data.birthTime
        );


        setText(
            "reviewCurrentYear",

            new Date()
                .getFullYear()
            + "년"
        );
    }


    /* =====================================================
       ANALYSIS
       ===================================================== */

    function bindAnalysis() {

        analyzeButton.addEventListener(
            "click",
            runAnalysis
        );
    }


    async function runAnalysis() {

        const formData =
            loadForm();


        if (!formData) {

            showToast(
                "출생정보가 없습니다."
            );

            return;
        }


        const startedAt =
            Date.now();


        try {

            resetProgress();


            showScreen(
                "loading"
            );


            startProgress();


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
                            JSON.stringify({

                                calendarType:
                                    formData.calendarType,

                                birthDate:
                                    formData.birthDate,

                                birthTime:
                                    formData.birthTimeUnknown
                                        ? null
                                        : formData.birthTime,

                                birthTimeUnknown:
                                    Boolean(
                                        formData.birthTimeUnknown
                                    ),

                                gender:
                                    formData.gender,

                                leapMonth:
                                    formData.calendarType
                                    === "LUNAR"

                                    && Boolean(
                                        formData.leapMonth
                                    ),

                                /*
                                 * 올해 운세는 자동.
                                 */
                                targetYear:
                                    new Date()
                                        .getFullYear()
                            })
                    }
                );


            const result =
                await readResponse(
                    response
                );


            if (!response.ok) {

                throw new Error(
                    result.message
                    || "사주 분석에 실패했습니다."
                );
            }


            const elapsed =
                Date.now()
                - startedAt;


            if (
                elapsed
                < MINIMUM_LOADING_TIME
            ) {

                await wait(
                    MINIMUM_LOADING_TIME
                    - elapsed
                );
            }


            await finishProgress();


            state.result =
                result;


            prepareConsultations(
                result
            );


            showScreen(
                "result"
            );


            /*
             * 중요:
             *
             * 결과 화면에 들어온 뒤
             * 사용자가 재생 버튼을 누르지 않아도
             * 자동으로 1번 성향 상담부터 시작한다.
             */
            window.setTimeout(
                function () {

                    startConsultationFrom(
                        0
                    );

                },
                450
            );


        } catch (error) {

            stopProgress();


            console.error(
                "사주 분석 오류",
                error
            );


            showToast(
                error.message
                || "분석 중 오류가 발생했습니다."
            );


            showScreen(
                "review",
                false
            );
        }
    }


    async function readResponse(
        response
    ) {

        const text =
            await response.text();


        if (!text) {

            return {};
        }


        try {

            return JSON.parse(
                text
            );

        } catch (error) {

            return {
                message:
                    text
            };
        }
    }


    /* =====================================================
       LOADING
       ===================================================== */

    function resetProgress() {

        stopProgress();


        state.progress =
            0;


        updateProgress(
            0
        );
    }


    function startProgress() {

        state.progressTimer =
            window.setInterval(
                function () {

                    if (
                        state.progress >= 92
                    ) {

                        return;
                    }


                    state.progress +=
                        state.progress < 30
                            ? 3
                            : state.progress < 65
                                ? 2
                                : 1;


                    state.progress =
                        Math.min(
                            92,
                            state.progress
                        );


                    updateProgress(
                        state.progress
                    );

                },
                90
            );
    }


    function stopProgress() {

        if (
            state.progressTimer
            !== null
        ) {

            window.clearInterval(
                state.progressTimer
            );


            state.progressTimer =
                null;
        }
    }


    async function finishProgress() {

        stopProgress();


        while (
            state.progress < 100
        ) {

            state.progress =
                Math.min(
                    100,
                    state.progress + 2
                );


            updateProgress(
                state.progress
            );


            await wait(
                35
            );
        }


        await wait(
            250
        );
    }


    function updateProgress(
        progress
    ) {

        progressBar.style.width =
            progress
            + "%";


        progressPercent.textContent =
            progress
            + "%";


        let index =
            0;


        let label =
            "출생정보 확인";


        let message =
            "출생정보를 확인하고 있습니다.";


        if (
            progress >= 25
        ) {

            index =
                1;

            label =
                "사주팔자 계산";

            message =
                "년주·월주·일주·시주를 계산하고 있습니다.";
        }


        if (
            progress >= 55
        ) {

            index =
                2;

            label =
                "대운·세운 분석";

            message =
                "현재 대운과 올해 세운을 분석하고 있습니다.";
        }


        if (
            progress >= 80
        ) {

            index =
                3;

            label =
                "상담 구성";

            message =
                "성향부터 미래운까지 상담 순서를 구성하고 있습니다.";
        }


        progressLabel.textContent =
            label;


        loadingMessage.textContent =
            message;


        loadingSteps.forEach(
            function (
                step,
                stepIndex
            ) {

                step.classList.toggle(
                    "is-active",
                    stepIndex === index
                    && progress < 100
                );


                step.classList.toggle(
                    "is-complete",
                    stepIndex < index
                    || progress >= 100
                );


                const number =
                    step.querySelector(
                        "strong"
                    );


                if (number) {

                    number.textContent =
                        stepIndex < index
                        || progress >= 100

                            ? "✓"

                            : String(
                                stepIndex + 1
                            );
                }
            }
        );
    }


    /* =====================================================
       CONSULTATION PREPARATION
       ===================================================== */

    function prepareConsultations(
        result
    ) {

        const consultations =
            Array.isArray(
                result.consultations
            )
                ? result.consultations
                : [];


        /*
         * 서버 배열 순서 무시.
         *
         * CONSULTATION_ORDER 기준으로 재정렬.
         */
        state.orderedConsultations =
            CONSULTATION_ORDER
                .map(
                    function (category) {

                        return consultations.find(
                            function (section) {

                                return section.category
                                    === category;
                            }
                        );
                    }
                )
                .filter(
                    Boolean
                );


        state.currentConsultationIndex =
            0;


        state.autoPlay =
            true;


        state.paused =
            false;


        consultationComplete
            .classList
            .add(
                "is-hidden"
            );


        updateSequenceUi(
            0
        );
    }


    /* =====================================================
       AUTO CONSULTATION
       ===================================================== */

    function startConsultationFrom(
        index
    ) {

        stopSpeech();


        if (
            !state.orderedConsultations.length
        ) {

            characterSpeech.textContent =
                "상담 데이터가 없습니다.";


            speechStatus.textContent =
                "상담 데이터 없음";


            return;
        }


        if (
            index < 0
            || index >= state.orderedConsultations.length
        ) {

            finishConsultation();

            return;
        }


        state.currentConsultationIndex =
            index;


        state.autoPlay =
            true;


        playCurrentConsultation();
    }


    function playCurrentConsultation() {

        const section =
            state.orderedConsultations[
                state.currentConsultationIndex
            ];


        if (!section) {

            moveToNextConsultation();

            return;
        }


        const category =
            section.category;


        const title =
            CONSULTATION_NAMES[
                category
            ]
            || section.title
            || "사주 상담";


        const speech =
            buildConsultationSpeech(
                section
            );


        updateCurrentConsultationUi(
            section,
            title,
            speech
        );


        speakCurrentSection(
            speech,
            function () {

                /*
                 * 현재 운세 음성이 끝나면
                 * 다음 운세를 자동 실행.
                 */
                if (
                    state.autoPlay
                ) {

                    window.setTimeout(
                        moveToNextConsultation,
                        550
                    );
                }
            }
        );
    }


    function moveToNextConsultation() {

        const nextIndex =
            state.currentConsultationIndex
            + 1;


        if (
            nextIndex
            >= state.orderedConsultations.length
        ) {

            finishConsultation();

            return;
        }


        state.currentConsultationIndex =
            nextIndex;


        playCurrentConsultation();
    }


    function buildConsultationSpeech(
        section
    ) {

        const formData =
            loadForm()
            || {};


        const name =
            formData.name
            || "고객";


        const category =
            section.category;


        const shortName =
            CONSULTATION_SHORT_NAMES[
                category
            ]
            || "운세";


        const intro =
            createSectionIntro(
                category,
                name
            );


        const parts = [

            intro,

            section.openingMessage,

            section.summaryMessage,

            section.guidanceMessage
        ]
            .filter(
                function (value) {

                    return value !== null
                        && value !== undefined
                        && String(value).trim()
                        !== "";
                }
            );


        /*
         * 여기에는
         *
         * 신뢰도,
         * 근거 코드,
         * direction,
         * confidence,
         * 보고서 점수
         *
         * 등을 읽지 않는다.
         *
         * 실제 사람이 상담하듯 문장만 읽는다.
         */
        return parts.join(
            " "
        );
    }


    function createSectionIntro(
        category,
        name
    ) {

        const intros = {

            PERSONALITY:
                name
                + "님, 먼저 기본 성향부터 볼게요.",

            PAST_LUCK:
                "이번에는 지금까지 지나온 흐름을 볼게요.",

            SOCIAL_RELATIONSHIP:
                "이제 주변 사람들과의 관계를 볼게요.",

            LOVE_STYLE:
                "다음은 연애에서 어떤 모습을 보이는지 볼게요.",

            PARTNER_LUCK:
                "이번에는 인연과 배우자 흐름을 볼게요.",

            WEALTH:
                "이제 재물의 흐름을 볼게요.",

            CAREER:
                "다음은 직업과 일의 방향을 볼게요.",

            LEGAL_RISK:
                "이번에는 분쟁이나 주의해야 할 부분을 볼게요.",

            HEALTH_TENDENCY:
                "이제 생활과 건강 관리 경향을 볼게요.",

            CURRENT_YEAR_LUCK:
                "그리고 가장 중요한 올해의 흐름을 볼게요.",

            FUTURE_LUCK:
                "마지막으로 앞으로의 흐름을 볼게요."
        };


        return intros[
            category
        ]
        || "";
    }


    function updateCurrentConsultationUi(
        section,
        title,
        speech
    ) {

        const index =
            state.currentConsultationIndex;


        const total =
            state.orderedConsultations.length;


        currentConsultationNumber.textContent =
            String(
                index + 1
            )
                .padStart(
                    2,
                    "0"
                );


        currentConsultationTitle.textContent =
            title;


        consultationProgressText.textContent =
            (index + 1)
            + " / "
            + total;


        const percentage =
            (
                (index + 1)
                / total
            )
            * 100;


        consultationProgressBar.style.width =
            percentage
            + "%";


        characterSpeech.textContent =
            speech;


        updateSequenceUi(
            index
        );
    }


    function updateSequenceUi(
        activeIndex
    ) {

        const buttons =
            Array.from(
                consultationSequence
                    .querySelectorAll(
                        "[data-category]"
                    )
            );


        buttons.forEach(
            function (
                button,
                index
            ) {

                button.classList.toggle(
                    "is-active",
                    index === activeIndex
                );


                button.classList.toggle(
                    "is-complete",
                    index < activeIndex
                );
            }
        );
    }


    function finishConsultation() {

        stopSpeech();


        state.autoPlay =
            false;


        state.paused =
            false;


        resultCharacterWrap
            .classList
            .remove(
                "is-speaking"
            );


        speechStatus.textContent =
            "상담 완료";


        characterSpeech.textContent =
            "여기까지가 지금 사주에서 볼 수 있는 전체적인 흐름이에요. 운은 정답을 정해주는 것이 아니라, 언제 힘을 쓰고 언제 조심할지를 판단하는 데 활용하면 됩니다.";


        consultationProgressText.textContent =
            "11 / 11";


        consultationProgressBar.style.width =
            "100%";


        consultationComplete
            .classList
            .remove(
                "is-hidden"
            );


        consultationSequence
            .querySelectorAll(
                "[data-category]"
            )
            .forEach(
                function (button) {

                    button.classList.remove(
                        "is-active"
                    );


                    button.classList.add(
                        "is-complete"
                    );
                }
            );


        pauseVoiceButton.textContent =
            "일시정지";
    }


    /* =====================================================
       SPEECH SYNTHESIS
       ===================================================== */

    function speakCurrentSection(
        text,
        onComplete
    ) {

        if (
            !("speechSynthesis" in window)
        ) {

            speechStatus.textContent =
                "음성 미지원";


            showToast(
                "현재 브라우저에서 음성 재생을 지원하지 않습니다."
            );


            return;
        }


        window.speechSynthesis.cancel();


        const utterance =
            new SpeechSynthesisUtterance(
                text
            );


        utterance.lang =
            "ko-KR";


        /*
         * 천명도령 임시 목소리.
         *
         * 실제 Typecast/OpenAI TTS 연결 시
         * 이 부분을 API 음성 재생으로 교체한다.
         */
        utterance.rate =
            0.94;


        utterance.pitch =
            0.88;


        utterance.volume =
            1;


        applyKoreanVoice(
            utterance
        );


        utterance.onstart =
            function () {

                state.currentUtterance =
                    utterance;


                state.speaking =
                    true;


                state.paused =
                    false;


                resultCharacterWrap
                    .classList
                    .add(
                        "is-speaking"
                    );


                speechStatus.textContent =
                    "자동 재생 중";


                pauseVoiceButton.textContent =
                    "일시정지";
            };


        utterance.onend =
            function () {

                state.currentUtterance =
                    null;


                state.speaking =
                    false;


                state.paused =
                    false;


                resultCharacterWrap
                    .classList
                    .remove(
                        "is-speaking"
                    );


                speechStatus.textContent =
                    "다음 운세로 이동 중";


                if (
                    typeof onComplete
                    === "function"
                ) {

                    onComplete();
                }
            };


        utterance.onerror =
            function (event) {

                /*
                 * cancel 때문에 발생하는 interrupted 오류는
                 * 실제 오류로 표시하지 않는다.
                 */
                if (
                    event.error === "interrupted"
                    || event.error === "canceled"
                ) {

                    return;
                }


                state.speaking =
                    false;


                resultCharacterWrap
                    .classList
                    .remove(
                        "is-speaking"
                    );


                speechStatus.textContent =
                    "음성 오류";
            };


        state.currentUtterance =
            utterance;


        /*
         * 결과 화면 진입 후 자동 호출.
         */
        window.speechSynthesis.speak(
            utterance
        );
    }


    function applyKoreanVoice(
        utterance
    ) {

        const voices =
            window.speechSynthesis
                .getVoices();


        if (
            !voices
            || !voices.length
        ) {

            return;
        }


        const preferred =
            voices.find(
                function (voice) {

                    return voice.lang
                        && voice.lang
                            .toLowerCase()
                            .startsWith(
                                "ko"
                            );
                }
            );


        if (preferred) {

            utterance.voice =
                preferred;
        }
    }


    /* =====================================================
       CONTROLS
       ===================================================== */

    function bindConsultationControls() {

        pauseVoiceButton.addEventListener(
            "click",
            function () {

                if (
                    !("speechSynthesis" in window)
                ) {

                    return;
                }


                if (
                    state.paused
                ) {

                    window.speechSynthesis
                        .resume();


                    state.paused =
                        false;


                    state.speaking =
                        true;


                    resultCharacterWrap
                        .classList
                        .add(
                            "is-speaking"
                        );


                    pauseVoiceButton.textContent =
                        "일시정지";


                    speechStatus.textContent =
                        "자동 재생 중";


                    return;
                }


                if (
                    window.speechSynthesis
                        .speaking
                ) {

                    window.speechSynthesis
                        .pause();


                    state.paused =
                        true;


                    state.speaking =
                        false;


                    resultCharacterWrap
                        .classList
                        .remove(
                            "is-speaking"
                        );


                    pauseVoiceButton.textContent =
                        "계속 듣기";


                    speechStatus.textContent =
                        "일시정지";
                }
            }
        );


        replayVoiceButton.addEventListener(
            "click",
            function () {

                state.autoPlay =
                    true;


                playCurrentConsultation();
            }
        );


        restartConsultationButton.addEventListener(
            "click",
            function () {

                consultationComplete
                    .classList
                    .add(
                        "is-hidden"
                    );


                startConsultationFrom(
                    0
                );
            }
        );


        /*
         * 아래 카테고리를 직접 누르면
         * 해당 운세부터 자동 상담을 다시 시작.
         */
        consultationSequence.addEventListener(
            "click",
            function (event) {

                const button =
                    event.target.closest(
                        "[data-category]"
                    );


                if (!button) {

                    return;
                }


                const category =
                    button.dataset.category;


                const index =
                    state.orderedConsultations
                        .findIndex(
                            function (section) {

                                return section.category
                                    === category;
                            }
                        );


                if (
                    index < 0
                ) {

                    showToast(
                        "해당 운세 데이터가 없습니다."
                    );


                    return;
                }


                consultationComplete
                    .classList
                    .add(
                        "is-hidden"
                    );


                startConsultationFrom(
                    index
                );
            }
        );
    }


    function stopSpeech() {

        if (
            "speechSynthesis" in window
        ) {

            window.speechSynthesis
                .cancel();
        }


        state.currentUtterance =
            null;


        state.speaking =
            false;


        state.paused =
            false;


        resultCharacterWrap
            ?.classList
            .remove(
                "is-speaking"
            );
    }


    /* =====================================================
       COMMON
       ===================================================== */

    function pad(
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


    function setText(
        id,
        value
    ) {

        const element =
            document.getElementById(
                id
            );


        if (!element) {

            return;
        }


        element.textContent =
            value === null
            || value === undefined

                ? ""

                : String(
                    value
                );
    }


    function wait(
        milliseconds
    ) {

        return new Promise(
            function (resolve) {

                window.setTimeout(
                    resolve,
                    milliseconds
                );
            }
        );
    }


    function showToast(
        message
    ) {

        window.clearTimeout(
            state.toastTimer
        );


        toast.textContent =
            message;


        toast.classList.add(
            "is-show"
        );


        state.toastTimer =
            window.setTimeout(
                function () {

                    toast.classList.remove(
                        "is-show"
                    );

                },
                2600
            );
    }
});