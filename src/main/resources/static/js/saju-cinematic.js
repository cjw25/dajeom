document.addEventListener("DOMContentLoaded", function () {

    const MIN_ANALYSIS_TIME = 3200;

    const IMAGE_PATHS = {
        fallback: "/img/saju-character.png",
        opening: "/img/saju-character.png",
        manse: "/img/saju-cinematic/chart.png",
        energy: "/img/saju-cinematic/element.png",
        personality: "/img/saju-cinematic/personality.png",
        strength: "/img/saju-cinematic/strength.png",
        wealth: "/img/saju-cinematic/wealth.png",
        career: "/img/saju-cinematic/career.png",
        relationship: "/img/saju-cinematic/relationship.png",
        romance: "/img/saju-cinematic/relationship.png",
        luck: "/img/saju-cinematic/luck.png",
        future: "/img/saju-cinematic/luck.png",
        caution: "/img/saju-cinematic/shinsal.png",
        advice: "/img/saju-character.png"
    };

    const state = {
        currentScreen: "input",
        form: createEmptyForm(),

        profileId: null,
        analysisId: null,

        analysis: null,
        cinematic: null,
        scenes: [],

        sceneIndex: -1,

        sceneTimer: null,
        captionTimer: null,
        progressTimer: null,
        toastTimer: null,

        progressValue: 0
    };

    const screens = document.querySelectorAll("[data-screen]");

    const headerTitle = document.getElementById("headerTitle");
    const headerStep = document.getElementById("headerStep");

    const backButton = document.getElementById("backButton");
    const homeButtonTop = document.getElementById("homeButtonTop");
    const skipButton = document.getElementById("skipButton");

    const toast = document.getElementById("toast");

    const sajuInputForm = document.getElementById("sajuInputForm");

    const nameInput = document.getElementById("nameInput");

    const birthYear = document.getElementById("birthYear");
    const birthMonth = document.getElementById("birthMonth");
    const birthDay = document.getElementById("birthDay");

    const birthHour = document.getElementById("birthHour");
    const birthMinute = document.getElementById("birthMinute");

    const birthTimeUnknown = document.getElementById("birthTimeUnknown");

    const lunarLeapMonth = document.getElementById("lunarLeapMonth");
    const lunarLeapWrap = document.getElementById("lunarLeapWrap");

    const timeInputWrap = document.getElementById("timeInputWrap");
    const inputError = document.getElementById("inputError");

    const reviewName = document.getElementById("reviewName");
    const reviewGender = document.getElementById("reviewGender");
    const reviewCalendar = document.getElementById("reviewCalendar");

    const reviewLeapRow = document.getElementById("reviewLeapRow");
    const reviewLeap = document.getElementById("reviewLeap");

    const reviewBirthDate = document.getElementById("reviewBirthDate");
    const reviewBirthTime = document.getElementById("reviewBirthTime");

    const editButton = document.getElementById("editButton");
    const analyzeButton = document.getElementById("analyzeButton");

    const analysisCircle = document.getElementById("analysisCircle");
    const analysisPercent = document.getElementById("analysisPercent");
    const analysisStage = document.getElementById("analysisStage");
    const analysisMessage = document.getElementById("analysisMessage");

    const analysisBar = document.getElementById("analysisBar");
    const analysisProgressText =
        document.getElementById("analysisProgressText");
    const analysisProgressNumber =
        document.getElementById("analysisProgressNumber");

    const analysisSteps = Array.from(
        document.querySelectorAll("[data-analysis-step]")
    );

    const cinematicStage = document.getElementById("cinematicStage");

    const sceneBackgroundImage =
        document.getElementById("sceneBackgroundImage");

    const sceneImageFrame =
        document.getElementById("sceneImageFrame");

    const sceneForegroundImage =
        document.getElementById("sceneForegroundImage");

    const sceneImageCaption =
        document.getElementById("sceneImageCaption");

    const sceneVisualPanel =
        document.getElementById("sceneVisualPanel");

    const sceneVisualContent =
        document.getElementById("sceneVisualContent");

    const sceneHeading =
        document.getElementById("sceneHeading");

    const sceneKicker =
        document.getElementById("sceneKicker");

    const sceneTitle =
        document.getElementById("sceneTitle");

    const captionBox =
        document.getElementById("captionBox");

    const captionText =
        document.getElementById("captionText");

    const cinematicProgressValue =
        document.getElementById("cinematicProgressValue");

    const currentSceneNumber =
        document.getElementById("currentSceneNumber");

    const totalSceneNumber =
        document.getElementById("totalSceneNumber");

    const resultTitle =
        document.getElementById("resultTitle");

    const resultSummary =
        document.getElementById("resultSummary");

    const chartPersonTitle =
        document.getElementById("chartPersonTitle");

    const chartDateTitle =
        document.getElementById("chartDateTitle");

    const resultChart =
        document.getElementById("resultChart");

    const resultElementList =
        document.getElementById("resultElementList");

    const resultElementEasyList =
        document.getElementById("resultElementEasyList");

    const resultEasyList =
        document.getElementById("resultEasyList");

    const resultShinsalList =
        document.getElementById("resultShinsalList");

    const resultLuckList =
        document.getElementById("resultLuckList");

    const resultForecastList =
        document.getElementById("resultForecastList");

    const resultAdvancedList =
        document.getElementById("resultAdvancedList");

    const resultDisclaimer =
        document.getElementById("resultDisclaimer");

    const replayButton =
        document.getElementById("replayButton");

    const restartButton =
        document.getElementById("restartButton");

    const goHomeButton =
        document.getElementById("goHomeButton");

    initialize();

    function initialize() {
        bindEvents();
        updateStaticCopy();
        resetApplication();
        showScreen("input");

        window.addEventListener("pageshow", function (event) {
            if (event.persisted) {
                stopCinematic();
            }
        });
    }

    /**
     * 기존 HTML에 남아 있는 연도·월 관련 문구를
     * 상담형 해설 문구로 변경한다.
     */
    function updateStaticCopy() {
        const inputHeroDescription =
            document.querySelector(".input-hero-copy p");

        if (inputHeroDescription) {
            inputHeroDescription.textContent =
                "만세력 원국과 오행, 십성, 신살과 현재의 큰 흐름을 바탕으로 "
                + "성격과 재물, 직업, 인간관계의 반복 패턴을 영상으로 설명합니다.";
        }

        const thirdAnalysisStep =
            document.querySelector('[data-analysis-step="2"]');

        if (thirdAnalysisStep) {
            const title =
                thirdAnalysisStep.querySelector("strong");

            const description =
                thirdAnalysisStep.querySelector("small");

            if (title) {
                title.textContent =
                    "현재 흐름과 반복 패턴 분석";
            }

            if (description) {
                description.textContent =
                    "원국과 현재 대운에서 반복되는 성향과 선택을 정리합니다.";
            }
        }

        const luckSection =
            resultLuckList
                ? resultLuckList.closest(".result-section")
                : null;

        if (luckSection) {
            const heading =
                luckSection.querySelector(
                    ".result-section-heading h2"
                );

            const description =
                luckSection.querySelector(
                    ".result-section-heading p"
                );

            if (heading) {
                heading.textContent =
                    "현재의 큰 흐름";
            }

            if (description) {
                description.textContent =
                    "특정 날짜를 예측하지 않고 현재 반복되는 과제와 선택을 살펴봅니다.";
            }
        }

        /*
         * 기존 HTML에 남아 있는
         * 연도별 흐름 영역은 프론트에서 표시하지 않는다.
         */
        const forecastSection =
            resultForecastList
                ? resultForecastList.closest(".result-section")
                : null;

        if (forecastSection) {
            forecastSection.classList.add("hidden");
            forecastSection.setAttribute(
                "aria-hidden",
                "true"
            );
        }
    }

    function createEmptyForm() {
        return {
            name: "",
            gender: "",
            calendarType: "SOLAR",
            lunarLeapMonth: false,
            birthDate: "",
            birthTime: null,
            birthTimeUnknown: false
        };
    }

    function resetApplication() {
        stopCinematic();
        stopFakeProgress();

        state.form = createEmptyForm();

        state.profileId = null;
        state.analysisId = null;

        state.analysis = null;
        state.cinematic = null;
        state.scenes = [];

        state.sceneIndex = -1;
        state.progressValue = 0;

        resetInputForm();
        resetAnalysisUi();
        resetSceneUi();
        resetResultUi();
    }

    function resetInputForm() {
        nameInput.value = "";

        birthYear.value = "";
        birthMonth.value = "";
        birthDay.value = "";

        birthHour.value = "";
        birthMinute.value = "";

        birthTimeUnknown.checked = false;
        lunarLeapMonth.checked = false;

        birthHour.disabled = false;
        birthMinute.disabled = false;

        timeInputWrap.classList.remove("disabled");
        lunarLeapWrap.classList.add("hidden");

        document
            .querySelectorAll("[data-gender]")
            .forEach(function (button) {
                button.classList.remove("active");
            });

        document
            .querySelectorAll("[data-calendar]")
            .forEach(function (button) {
                button.classList.toggle(
                    "active",
                    button.dataset.calendar === "SOLAR"
                );
            });

        inputError.textContent = "";
    }

    function resetAnalysisUi() {
        state.progressValue = 0;

        if (analysisCircle) {
            analysisCircle.style.setProperty(
                "--progress",
                "0"
            );
        }

        if (analysisPercent) {
            analysisPercent.textContent = "0%";
        }

        if (analysisStage) {
            analysisStage.textContent = "준비 중";
        }

        if (analysisMessage) {
            analysisMessage.textContent =
                "입력한 정보를 확인하고 있습니다.";
        }

        if (analysisBar) {
            analysisBar.style.width = "0%";
        }

        if (analysisProgressText) {
            analysisProgressText.textContent =
                "분석 준비";
        }

        if (analysisProgressNumber) {
            analysisProgressNumber.textContent =
                "0 / 100";
        }

        analysisSteps.forEach(function (step, index) {
            step.classList.remove(
                "active",
                "complete"
            );

            if (index === 0) {
                step.classList.add("active");
            }

            const icon =
                step.querySelector(":scope > span");

            if (icon) {
                icon.textContent =
                    String(index + 1);
            }
        });
    }

    function resetResultUi() {
        if (resultTitle) {
            resultTitle.textContent =
                "사주 전체 결과";
        }

        if (resultSummary) {
            resultSummary.textContent = "";
        }

        if (resultChart) {
            resultChart.innerHTML = "";
        }

        if (resultElementList) {
            resultElementList.innerHTML = "";
        }

        if (resultElementEasyList) {
            resultElementEasyList.innerHTML = "";
        }

        if (resultEasyList) {
            resultEasyList.innerHTML = "";
        }

        if (resultShinsalList) {
            resultShinsalList.innerHTML = "";
        }

        if (resultLuckList) {
            resultLuckList.innerHTML = "";
        }

        if (resultForecastList) {
            resultForecastList.innerHTML = "";
        }

        if (resultAdvancedList) {
            resultAdvancedList.innerHTML = "";
        }

        if (resultDisclaimer) {
            resultDisclaimer.textContent = "";
        }
    }

    function bindEvents() {
        document
            .querySelectorAll(".numeric-input")
            .forEach(function (input) {
                input.addEventListener(
                    "input",
                    function () {
                        input.value =
                            input.value.replace(
                                /[^0-9]/g,
                                ""
                            );

                        inputError.textContent = "";
                    }
                );
            });

        document
            .querySelectorAll("[data-gender]")
            .forEach(function (button) {
                button.addEventListener(
                    "click",
                    function () {
                        document
                            .querySelectorAll("[data-gender]")
                            .forEach(function (item) {
                                item.classList.remove("active");
                            });

                        button.classList.add("active");

                        state.form.gender =
                            button.dataset.gender;

                        inputError.textContent = "";
                    }
                );
            });

        document
            .querySelectorAll("[data-calendar]")
            .forEach(function (button) {
                button.addEventListener(
                    "click",
                    function () {
                        document
                            .querySelectorAll("[data-calendar]")
                            .forEach(function (item) {
                                item.classList.remove("active");
                            });

                        button.classList.add("active");

                        state.form.calendarType =
                            button.dataset.calendar;

                        const isLunar =
                            state.form.calendarType === "LUNAR";

                        lunarLeapWrap.classList.toggle(
                            "hidden",
                            !isLunar
                        );

                        if (!isLunar) {
                            lunarLeapMonth.checked = false;
                            state.form.lunarLeapMonth = false;
                        }

                        inputError.textContent = "";
                    }
                );
            });

        lunarLeapMonth.addEventListener(
            "change",
            function () {
                state.form.lunarLeapMonth =
                    lunarLeapMonth.checked;
            }
        );

        birthTimeUnknown.addEventListener(
            "change",
            function () {
                const unknown =
                    birthTimeUnknown.checked;

                state.form.birthTimeUnknown =
                    unknown;

                timeInputWrap.classList.toggle(
                    "disabled",
                    unknown
                );

                birthHour.disabled = unknown;
                birthMinute.disabled = unknown;

                if (unknown) {
                    birthHour.value = "";
                    birthMinute.value = "";
                }

                inputError.textContent = "";
            }
        );

        sajuInputForm.addEventListener(
            "submit",
            function (event) {
                event.preventDefault();

                if (!validateAndSaveForm()) {
                    return;
                }

                renderReview();
                showScreen("review");
            }
        );

        editButton.addEventListener(
            "click",
            function () {
                showScreen("input");
            }
        );

        analyzeButton.addEventListener(
            "click",
            startAnalysis
        );

        backButton.addEventListener(
            "click",
            handleBackButton
        );

        homeButtonTop.addEventListener(
            "click",
            goHome
        );

        skipButton.addEventListener(
            "click",
            finishCinematic
        );

        replayButton.addEventListener(
            "click",
            startCinematic
        );

        restartButton.addEventListener(
            "click",
            function () {
                resetApplication();
                showScreen("input");
            }
        );

        goHomeButton.addEventListener(
            "click",
            goHome
        );
    }

    function goHome() {
        stopCinematic();
        window.location.href = "/";
    }

    function handleBackButton() {
        if (state.currentScreen === "input") {
            goHome();
            return;
        }

        if (state.currentScreen === "review") {
            showScreen("input");
            return;
        }

        if (state.currentScreen === "analysis") {
            return;
        }

        if (state.currentScreen === "cinematic") {
            finishCinematic();
            return;
        }

        if (state.currentScreen === "result") {
            goHome();
        }
    }

    function showScreen(screenName) {
        screens.forEach(function (screen) {
            screen.classList.toggle(
                "active",
                screen.dataset.screen === screenName
            );
        });

        state.currentScreen = screenName;

        const headerInformation = {
            input: {
                title: "천명해설",
                step: "사주정보 입력"
            },

            review: {
                title: "천명해설",
                step: "입력정보 확인"
            },

            analysis: {
                title: "천명해설",
                step: "사주 분석 중"
            },

            cinematic: {
                title: "천명해설",
                step: "영상 해설"
            },

            result: {
                title: "천명해설",
                step: "전체 결과"
            }
        };

        const information =
            headerInformation[screenName]
            || headerInformation.input;

        headerTitle.textContent =
            information.title;

        headerStep.textContent =
            information.step;

        skipButton.classList.toggle(
            "hidden",
            screenName !== "cinematic"
        );

        homeButtonTop.classList.toggle(
            "hidden",
            screenName === "cinematic"
        );

        backButton.style.visibility =
            screenName === "analysis"
                ? "hidden"
                : "visible";

        window.scrollTo({
            top: 0,
            behavior: "auto"
        });
    }

    function validateAndSaveForm() {
        const name =
            nameInput.value.trim();

        if (name.length < 2) {
            return setInputError(
                "이름은 두 글자 이상 입력해주세요."
            );
        }

        if (!state.form.gender) {
            return setInputError(
                "성별을 선택해주세요."
            );
        }

        if (
            !birthYear.value
            || !birthMonth.value
            || !birthDay.value
        ) {
            return setInputError(
                "생년월일을 모두 입력해주세요."
            );
        }

        const validBirthDate =
            state.form.calendarType === "SOLAR"
                ? isValidSolarDate(
                    birthYear.value,
                    birthMonth.value,
                    birthDay.value
                )
                : isValidLunarDate(
                    birthYear.value,
                    birthMonth.value,
                    birthDay.value
                );

        if (!validBirthDate) {
            return setInputError(
                "올바른 생년월일을 입력해주세요."
            );
        }

        if (!birthTimeUnknown.checked) {
            if (
                birthHour.value === ""
                || birthMinute.value === ""
            ) {
                return setInputError(
                    "출생시간을 입력하거나 시간 모름을 선택해주세요."
                );
            }

            if (
                !isValidTime(
                    birthHour.value,
                    birthMinute.value
                )
            ) {
                return setInputError(
                    "출생시간 형식이 올바르지 않습니다."
                );
            }
        }

        state.form.name = name;

        state.form.birthDate =
            birthYear.value
            + "-"
            + pad2(birthMonth.value)
            + "-"
            + pad2(birthDay.value);

        state.form.birthTimeUnknown =
            birthTimeUnknown.checked;

        state.form.birthTime =
            birthTimeUnknown.checked
                ? null
                : pad2(birthHour.value)
                + ":"
                + pad2(birthMinute.value);

        state.form.lunarLeapMonth =
            state.form.calendarType === "LUNAR"
            && lunarLeapMonth.checked;

        inputError.textContent = "";

        return true;
    }

    function setInputError(message) {
        inputError.textContent = message;
        return false;
    }

    function isValidSolarDate(
        year,
        month,
        day
    ) {
        const numericYear = Number(year);
        const numericMonth = Number(month);
        const numericDay = Number(day);

        const currentYear =
            new Date().getFullYear();

        if (
            numericYear < 1900
            || numericYear > currentYear
        ) {
            return false;
        }

        const date =
            new Date(
                numericYear,
                numericMonth - 1,
                numericDay
            );

        const today = new Date();

        today.setHours(
            0,
            0,
            0,
            0
        );

        return (
            date.getFullYear() === numericYear
            && date.getMonth() === numericMonth - 1
            && date.getDate() === numericDay
            && date <= today
        );
    }

    function isValidLunarDate(
        year,
        month,
        day
    ) {
        const numericYear = Number(year);
        const numericMonth = Number(month);
        const numericDay = Number(day);

        const currentYear =
            new Date().getFullYear();

        return (
            numericYear >= 1900
            && numericYear <= currentYear
            && numericMonth >= 1
            && numericMonth <= 12
            && numericDay >= 1
            && numericDay <= 30
        );
    }

    function isValidTime(
        hour,
        minute
    ) {
        const numericHour = Number(hour);
        const numericMinute = Number(minute);

        return (
            numericHour >= 0
            && numericHour <= 23
            && numericMinute >= 0
            && numericMinute <= 59
        );
    }

    function renderReview() {
        reviewName.textContent =
            state.form.name;

        reviewGender.textContent =
            state.form.gender === "MALE"
                ? "남성"
                : "여성";

        reviewCalendar.textContent =
            state.form.calendarType === "SOLAR"
                ? "양력"
                : "음력";

        reviewLeapRow.classList.toggle(
            "hidden",
            state.form.calendarType !== "LUNAR"
        );

        reviewLeap.textContent =
            state.form.lunarLeapMonth
                ? "윤달"
                : "평달";

        reviewBirthDate.textContent =
            formatBirthTitle(
                state.form.birthDate
            );

        reviewBirthTime.textContent =
            state.form.birthTimeUnknown
                ? "시간 모름"
                : state.form.birthTime;
    }

    async function startAnalysis() {
        analyzeButton.disabled = true;
        analyzeButton.textContent = "분석 중...";

        showScreen("analysis");
        startFakeProgress();

        try {
            const startedAt = Date.now();

            analysisMessage.textContent =
                "입력한 사주 정보를 저장하고 있습니다.";

            const profileResult =
                await requestJson(
                    "/api/saju/profiles",
                    {
                        method: "POST",
                        body: JSON.stringify(
                            state.form
                        )
                    }
                );

            const profileId =
                Number(
                    profileResult.profileId
                    ?? profileResult.id
                );

            if (
                !Number.isInteger(profileId)
                || profileId <= 0
            ) {
                throw new Error(
                    "저장된 사주정보 번호를 확인할 수 없습니다."
                );
            }

            state.profileId = profileId;

            analysisMessage.textContent =
                "만세력 원국과 오행, 십성을 계산하고 있습니다.";

            const analysisResult =
                await requestJson(
                    "/api/saju/analyses",
                    {
                        method: "POST",
                        body: JSON.stringify({
                            profileId: profileId
                        })
                    }
                );

            const analysisId =
                Number(
                    analysisResult.analysisId
                    ?? analysisResult.id
                );

            if (
                !Number.isInteger(analysisId)
                || analysisId <= 0
            ) {
                throw new Error(
                    "생성된 분석 번호를 확인할 수 없습니다."
                );
            }

            state.analysisId = analysisId;

            analysisMessage.textContent =
                "반복되는 성향과 현실적인 조언을 영상 장면으로 구성하고 있습니다.";

            /*
             * 연도별 예측 API는 호출하지 않는다.
             *
             * 영상 장면 데이터만 가져온다.
             */
            const cinematicResult =
                await requestJson(
                    "/api/saju/analyses/"
                    + analysisId
                    + "/cinematic"
                );

            const elapsed =
                Date.now() - startedAt;

            const remaining =
                Math.max(
                    MIN_ANALYSIS_TIME - elapsed,
                    0
                );

            if (remaining > 0) {
                await wait(remaining);
            }

            stopFakeProgress();

            await animateProgressTo(
                100,
                520
            );

            state.analysis =
                analysisResult;

            state.cinematic =
                cinematicResult;

            state.scenes =
                normalizeCinematicScenes(
                    cinematicResult.scenes
                );

            if (state.scenes.length === 0) {
                throw new Error(
                    "영상 장면 데이터를 생성하지 못했습니다."
                );
            }

            renderFullResult();

            analysisMessage.textContent =
                "영상형 사주 해설 준비가 완료되었습니다.";

            await wait(500);

            startCinematic();

        } catch (error) {
            stopFakeProgress();

            console.error(
                "영상형 사주 분석 오류",
                error
            );

            showToast(
                error.message
                || "분석 중 오류가 발생했습니다."
            );

            showScreen("review");

        } finally {
            analyzeButton.disabled = false;
            analyzeButton.textContent =
                "분석하고 영상 시작";
        }
    }

    async function requestJson(
        url,
        options
    ) {
        const requestOptions = {
            method: "GET",
            headers: {
                "Accept": "application/json"
            },
            ...options
        };

        requestOptions.headers = {
            "Accept": "application/json",
            ...(options?.headers || {})
        };

        if (requestOptions.body) {
            requestOptions.headers[
                "Content-Type"
            ] = "application/json";
        }

        const csrfToken =
            document
                .querySelector('meta[name="_csrf"]')
                ?.getAttribute("content");

        const csrfHeader =
            document
                .querySelector('meta[name="_csrf_header"]')
                ?.getAttribute("content");

        if (csrfToken && csrfHeader) {
            requestOptions.headers[
                csrfHeader
            ] = csrfToken;
        }

        const response =
            await fetch(
                url,
                requestOptions
            );

        const text =
            await response.text();

        let data = {};

        if (text) {
            try {
                data = JSON.parse(text);

            } catch (error) {
                data = {
                    message: text
                };
            }
        }

        if (!response.ok) {
            throw new Error(
                data.message
                || data.error
                || "요청 처리에 실패했습니다."
            );
        }

        return data;
    }

    function startFakeProgress() {
        stopFakeProgress();

        state.progressValue = 0;

        setProgress(
            0,
            "준비 중",
            "입력 정보를 확인하고 있습니다."
        );

        state.progressTimer =
            window.setInterval(
                function () {
                    let next =
                        state.progressValue;

                    if (next < 22) {
                        next += 4;

                        setProgress(
                            next,
                            "출생정보",
                            "양력·음력과 출생 정보를 확인하고 있습니다."
                        );

                        return;
                    }

                    if (next < 52) {
                        next += 3;

                        setProgress(
                            next,
                            "명식 계산",
                            "년주·월주·일주·시주를 계산하고 있습니다."
                        );

                        return;
                    }

                    if (next < 78) {
                        next += 2;

                        setProgress(
                            next,
                            "성향 분석",
                            "원국과 현재 큰 흐름에서 반복되는 성향을 분석하고 있습니다."
                        );

                        return;
                    }

                    if (next < 94) {
                        next += 1;

                        setProgress(
                            next,
                            "영상 구성",
                            "상담형 내레이션과 자막, 화면 장면을 구성하고 있습니다."
                        );
                    }
                },
                140
            );
    }

    function stopFakeProgress() {
        if (!state.progressTimer) {
            return;
        }

        clearInterval(
            state.progressTimer
        );

        state.progressTimer = null;
    }

    function animateProgressTo(
        target,
        duration
    ) {
        return new Promise(
            function (resolve) {
                const start =
                    state.progressValue;

                const difference =
                    target - start;

                const startedAt =
                    performance.now();

                function frame(now) {
                    const elapsed =
                        now - startedAt;

                    const ratio =
                        Math.min(
                            elapsed / duration,
                            1
                        );

                    const eased =
                        1
                        - Math.pow(
                            1 - ratio,
                            3
                        );

                    setProgress(
                        start
                        + difference
                        * eased,

                        ratio < 1
                            ? "영상 구성"
                            : "완료",

                        ratio < 1
                            ? "영상 장면을 정리하고 있습니다."
                            : "영상 해설 준비가 완료되었습니다."
                    );

                    if (ratio < 1) {
                        requestAnimationFrame(
                            frame
                        );

                        return;
                    }

                    resolve();
                }

                requestAnimationFrame(frame);
            }
        );
    }

    function setProgress(
        value,
        stageText,
        messageText
    ) {
        state.progressValue =
            Math.max(
                0,
                Math.min(
                    100,
                    value
                )
            );

        const rounded =
            Math.round(
                state.progressValue
            );

        analysisCircle.style.setProperty(
            "--progress",
            rounded
        );

        analysisPercent.textContent =
            rounded + "%";

        analysisBar.style.width =
            rounded + "%";

        analysisProgressNumber.textContent =
            rounded + " / 100";

        analysisStage.textContent =
            stageText;

        analysisProgressText.textContent =
            stageText;

        analysisMessage.textContent =
            messageText;

        updateAnalysisSteps(
            rounded
        );
    }

    function updateAnalysisSteps(progress) {
        let activeStep = 0;

        if (progress < 25) {
            activeStep = 0;

        } else if (progress < 55) {
            activeStep = 1;

        } else if (progress < 80) {
            activeStep = 2;

        } else if (progress < 100) {
            activeStep = 3;

        } else {
            activeStep =
                analysisSteps.length;
        }

        analysisSteps.forEach(
            function (step, index) {
                const complete =
                    index < activeStep
                    || progress >= 100;

                const active =
                    index === activeStep
                    && progress < 100;

                step.classList.toggle(
                    "complete",
                    complete
                );

                step.classList.toggle(
                    "active",
                    active
                );

                const icon =
                    step.querySelector(
                        ":scope > span"
                    );

                if (icon) {
                    icon.textContent =
                        complete
                            ? "✓"
                            : String(index + 1);
                }
            }
        );
    }

    function normalizeCinematicScenes(source) {
        if (!Array.isArray(source)) {
            return [];
        }

        return source
            .filter(function (scene) {
                return (
                    scene
                    && typeof scene === "object"
                );
            })
            .sort(function (first, second) {
                return (
                    Number(first.order || 0)
                    - Number(second.order || 0)
                );
            })
            .map(function (scene, index) {
                const sceneType =
                    String(
                        scene.sceneType
                        || "OPENING"
                    );

                return {
                    order: Number(
                        scene.order
                        || index + 1
                    ),

                    sceneType: sceneType,

                    kicker:
                        sceneTypeKicker(
                            sceneType
                        ),

                    title:
                        sanitizeNarrativeText(
                            scene.title
                            || "천명해설"
                        ),

                    header:
                        sceneTypeLabel(
                            sceneType
                        ),

                    text:
                        sanitizeNarrativeText(
                            scene.narration
                            || scene.subtitle
                            || "해설을 준비하고 있습니다."
                        ),

                    subtitle:
                        sanitizeNarrativeText(
                            scene.subtitle
                            || ""
                        ),

                    duration:
                        normalizeSceneDuration(
                            scene.durationSeconds
                        ),

                    camera:
                        resolveCamera(
                            sceneType,
                            scene.visualKey,
                            index
                        ),

                    backgroundImage:
                        resolveSceneImage(
                            sceneType,
                            scene.visualKey
                        ),

                    foregroundImage:
                        resolveForegroundImage(
                            sceneType,
                            scene.visualKey
                        ),

                    foregroundCaption:
                        sanitizeNarrativeText(
                            scene.title
                            || "영상 장면"
                        ),

                    visualType:
                        resolveVisualType(
                            sceneType
                        ),

                    /*
                     * 기간 데이터는 프론트에서 사용하지 않는다.
                     */
                    highlights:
                        sanitizeTextList(
                            scene.highlights
                        ),

                    evidences:
                        sanitizeTextList(
                            scene.evidences
                        )
                };
            });
    }

    function normalizeSceneDuration(seconds) {
        const numeric =
            Number(seconds);

        if (!Number.isFinite(numeric)) {
            return 9000;
        }

        return Math.max(
            5000,
            Math.min(
                20000,
                numeric * 1000
            )
        );
    }

    function sceneTypeKicker(sceneType) {
        const map = {
            OPENING: "天命解說",
            MANSE_CALENDAR: "四柱命式",
            BASIC_ENERGY: "五行氣運",
            PERSONALITY_OUTER: "外面性格",
            PERSONALITY_INNER: "內面心性",
            STRENGTHS: "長點解說",
            WEAKNESSES: "補完方向",
            WEALTH: "財物解說",
            CAREER_BUSINESS: "職業事業",
            RELATIONSHIPS: "人間關係",
            ROMANCE: "戀愛因緣",
            CURRENT_DAEUN: "大運流轉",
            FUTURE_OVERVIEW: "反復流向",
            YEAR_FORECAST: "反復傾向",
            MONTH_PERIOD: "影響傾向",
            CAUTION_PERIOD: "注意選擇",
            OPPORTUNITY_PERIOD: "機會活用",
            ADVICE: "現實助言",
            EVIDENCE: "命理根據"
        };

        return map[sceneType]
            || "天命解說";
    }

    function sceneTypeLabel(sceneType) {
        const map = {
            OPENING: "영상 해설 시작",
            MANSE_CALENDAR: "사주 명식표",
            BASIC_ENERGY: "타고난 기본 기운",
            PERSONALITY_OUTER: "겉으로 보이는 성격",
            PERSONALITY_INNER: "실제 속마음",
            STRENGTHS: "장점",
            WEAKNESSES: "약점과 보완",
            WEALTH: "재물 습관",
            CAREER_BUSINESS: "직업과 사업",
            RELATIONSHIPS: "사람복",
            ROMANCE: "연애와 관계",
            CURRENT_DAEUN: "현재의 큰 흐름",
            FUTURE_OVERVIEW: "앞으로 반복되는 패턴",
            YEAR_FORECAST: "반복되는 흐름",
            MONTH_PERIOD: "영향 패턴",
            CAUTION_PERIOD: "조심할 선택",
            OPPORTUNITY_PERIOD: "기회 활용",
            ADVICE: "현실적인 조언",
            EVIDENCE: "해석 근거"
        };

        return map[sceneType]
            || "천명해설";
    }

    function resolveCamera(
        sceneType,
        visualKey,
        index
    ) {
        if (
            sceneType === "MANSE_CALENDAR"
            || sceneType === "BASIC_ENERGY"
        ) {
            return "camera-soft";
        }

        if (sceneType === "CAUTION_PERIOD") {
            return "camera-close";
        }

        if (
            sceneType === "CURRENT_DAEUN"
            || sceneType === "FUTURE_OVERVIEW"
            || sceneType === "YEAR_FORECAST"
        ) {
            return "camera-medium";
        }

        if (
            String(
                visualKey
                || ""
            ).includes("CLOSE")
        ) {
            return "camera-close";
        }

        return index % 2 === 0
            ? "camera-left"
            : "camera-right";
    }

    function resolveSceneImage(
        sceneType,
        visualKey
    ) {
        const map = {
            OPENING: IMAGE_PATHS.opening,
            MANSE_CALENDAR: IMAGE_PATHS.manse,
            BASIC_ENERGY: IMAGE_PATHS.energy,

            PERSONALITY_OUTER:
                IMAGE_PATHS.personality,

            PERSONALITY_INNER:
                IMAGE_PATHS.personality,

            STRENGTHS:
                IMAGE_PATHS.strength,

            WEAKNESSES:
                IMAGE_PATHS.strength,

            WEALTH:
                IMAGE_PATHS.wealth,

            CAREER_BUSINESS:
                IMAGE_PATHS.career,

            RELATIONSHIPS:
                IMAGE_PATHS.relationship,

            ROMANCE:
                IMAGE_PATHS.romance,

            CURRENT_DAEUN:
                IMAGE_PATHS.luck,

            FUTURE_OVERVIEW:
                IMAGE_PATHS.future,

            YEAR_FORECAST:
                IMAGE_PATHS.future,

            MONTH_PERIOD:
                IMAGE_PATHS.future,

            CAUTION_PERIOD:
                IMAGE_PATHS.caution,

            OPPORTUNITY_PERIOD:
                IMAGE_PATHS.future,

            ADVICE:
                IMAGE_PATHS.advice,

            EVIDENCE:
                IMAGE_PATHS.manse
        };

        return (
            map[sceneType]
            || visualKeyToImage(visualKey)
            || IMAGE_PATHS.fallback
        );
    }

    function visualKeyToImage(visualKey) {
        const key =
            String(
                visualKey
                || ""
            ).toUpperCase();

        if (key.includes("WEALTH")) {
            return IMAGE_PATHS.wealth;
        }

        if (
            key.includes("CAREER")
            || key.includes("BUSINESS")
        ) {
            return IMAGE_PATHS.career;
        }

        if (
            key.includes("ROMANCE")
            || key.includes("RELATION")
        ) {
            return IMAGE_PATHS.relationship;
        }

        if (
            key.includes("CAUTION")
            || key.includes("WARNING")
        ) {
            return IMAGE_PATHS.caution;
        }

        if (
            key.includes("YEAR")
            || key.includes("FUTURE")
            || key.includes("DAEUN")
        ) {
            return IMAGE_PATHS.future;
        }

        return null;
    }

    function resolveForegroundImage(
        sceneType,
        visualKey
    ) {
        const excludedTypes = [
            "OPENING",
            "MANSE_CALENDAR",
            "BASIC_ENERGY",
            "CURRENT_DAEUN",
            "FUTURE_OVERVIEW",
            "YEAR_FORECAST",
            "MONTH_PERIOD",
            "CAUTION_PERIOD",
            "OPPORTUNITY_PERIOD",
            "EVIDENCE"
        ];

        if (
            excludedTypes.includes(
                sceneType
            )
        ) {
            return null;
        }

        return resolveSceneImage(
            sceneType,
            visualKey
        );
    }

    function resolveVisualType(sceneType) {
        if (sceneType === "MANSE_CALENDAR") {
            return "CHART";
        }

        if (sceneType === "BASIC_ENERGY") {
            return "ELEMENT";
        }

        if (sceneType === "CURRENT_DAEUN") {
            return "LUCK";
        }

        if (
            [
                "FUTURE_OVERVIEW",
                "YEAR_FORECAST",
                "MONTH_PERIOD",
                "CAUTION_PERIOD",
                "OPPORTUNITY_PERIOD",
                "EVIDENCE"
            ].includes(sceneType)
        ) {
            return "DETAIL";
        }

        return null;
    }

    function startCinematic() {
        stopCinematic();

        if (
            !state.cinematic
            || state.scenes.length === 0
        ) {
            showToast(
                "영상 장면 데이터가 없습니다."
            );

            showScreen("result");
            return;
        }

        state.sceneIndex = -1;

        totalSceneNumber.textContent =
            String(state.scenes.length);

        currentSceneNumber.textContent = "0";

        cinematicProgressValue.style.width =
            "0%";

        resetSceneUi();
        showScreen("cinematic");

        window.setTimeout(
            function () {
                playScene(0);
            },
            300
        );
    }

    function playScene(index) {
        clearSceneTimers();

        if (index >= state.scenes.length) {
            finishCinematic();
            return;
        }

        state.sceneIndex = index;

        const scene =
            state.scenes[index];

        applyScene(scene);
        updateCinematicProgress(index);

        showCaption(
            scene.text,
            scene.duration
        );

        state.sceneTimer =
            window.setTimeout(
                function () {
                    playScene(index + 1);
                },
                scene.duration
            );
    }

    function applyScene(scene) {
        cinematicStage.className =
            "cinematic-stage "
            + scene.camera;

        headerStep.textContent =
            scene.header;

        sceneKicker.textContent =
            scene.kicker;

        sceneTitle.textContent =
            scene.title;

        sceneHeading.classList.remove("show");
        captionBox.classList.remove("show");
        sceneVisualPanel.classList.remove("show");
        sceneImageFrame.classList.remove("show");

        setImageWithFallback(
            sceneBackgroundImage,
            scene.backgroundImage
        );

        renderSceneImage(scene);
        renderSceneVisual(scene);

        window.requestAnimationFrame(
            function () {
                sceneHeading.classList.add("show");
            }
        );
    }

    function renderSceneImage(scene) {
        if (!scene.foregroundImage) {
            sceneImageFrame.classList.add("hidden");
            return;
        }

        sceneImageFrame.classList.remove("hidden");

        sceneImageCaption.textContent =
            scene.foregroundCaption;

        setImageWithFallback(
            sceneForegroundImage,
            scene.foregroundImage
        );

        window.requestAnimationFrame(
            function () {
                sceneImageFrame.classList.add("show");
            }
        );
    }

    function setImageWithFallback(
        imageElement,
        source
    ) {
        imageElement.onerror = function () {
            imageElement.onerror = null;
            imageElement.src =
                IMAGE_PATHS.fallback;
        };

        imageElement.src =
            source
            || IMAGE_PATHS.fallback;
    }

    function renderSceneVisual(scene) {
        sceneVisualContent.innerHTML = "";

        if (scene.visualType === "CHART") {
            sceneVisualContent.innerHTML =
                createVideoChartHtml();

        } else if (
            scene.visualType === "ELEMENT"
        ) {
            sceneVisualContent.innerHTML =
                createVideoElementHtml();

        } else if (
            scene.visualType === "LUCK"
        ) {
            sceneVisualContent.innerHTML =
                createVideoLuckHtml();

        } else if (
            scene.visualType === "DETAIL"
        ) {
            sceneVisualContent.innerHTML =
                createSceneDetailHtml(scene);

        } else {
            sceneVisualPanel.classList.add("hidden");
            return;
        }

        sceneVisualPanel.classList.remove("hidden");

        window.requestAnimationFrame(
            function () {
                sceneVisualPanel.classList.add("show");
            }
        );
    }

    /**
     * 영향 기간은 출력하지 않는다.
     *
     * 핵심 내용과 해석 근거만 보여준다.
     */
    function createSceneDetailHtml(scene) {
        const highlights =
            scene.highlights
                .filter(isNotBlank)
                .slice(0, 7);

        const evidences =
            scene.evidences
                .filter(isNotBlank)
                .slice(0, 6);

        const highlightHtml =
            highlights
                .map(function (item) {
                    return createVideoMiniItem(
                        "핵심 내용",
                        item
                    );
                })
                .join("");

        const evidenceHtml =
            evidences
                .map(function (item) {
                    return createVideoMiniItem(
                        "해석 근거",
                        item
                    );
                })
                .join("");

        if (
            !highlightHtml
            && !evidenceHtml
        ) {
            return createVideoMiniItem(
                "천명해설",
                scene.subtitle
                || scene.text
            );
        }

        return (
            '<div class="scene-detail-list">'
            + highlightHtml
            + evidenceHtml
            + "</div>"
        );
    }

    function showCaption(
        text,
        duration
    ) {
        captionText.textContent = "";

        window.setTimeout(
            function () {
                captionBox.classList.add("show");

                typeCaption(
                    text,
                    duration
                );
            },
            300
        );
    }

    function typeCaption(
        text,
        duration
    ) {
        if (state.captionTimer) {
            clearInterval(
                state.captionTimer
            );
        }

        const characters =
            Array.from(
                String(text || "")
            );

        const availableTime =
            Math.max(
                duration - 1500,
                1200
            );

        const interval =
            Math.max(
                17,
                Math.min(
                    52,
                    availableTime
                    / Math.max(
                        characters.length,
                        1
                    )
                )
            );

        let index = 0;

        state.captionTimer =
            window.setInterval(
                function () {
                    index += 1;

                    captionText.textContent =
                        characters
                            .slice(0, index)
                            .join("");

                    if (
                        index
                        >= characters.length
                    ) {
                        clearInterval(
                            state.captionTimer
                        );

                        state.captionTimer = null;
                    }
                },
                interval
            );
    }

    function updateCinematicProgress(index) {
        const progress =
            (
                (index + 1)
                / state.scenes.length
            )
            * 100;

        currentSceneNumber.textContent =
            String(index + 1);

        cinematicProgressValue.style.width =
            progress + "%";
    }

    function finishCinematic() {
        stopCinematic();
        resetSceneUi();
        showScreen("result");
    }

    function stopCinematic() {
        clearSceneTimers();
    }

    function clearSceneTimers() {
        if (state.sceneTimer) {
            clearTimeout(
                state.sceneTimer
            );

            state.sceneTimer = null;
        }

        if (state.captionTimer) {
            clearInterval(
                state.captionTimer
            );

            state.captionTimer = null;
        }
    }

    function resetSceneUi() {
        sceneHeading.classList.remove("show");
        captionBox.classList.remove("show");

        sceneVisualPanel.classList.remove("show");
        sceneVisualPanel.classList.add("hidden");

        sceneImageFrame.classList.remove("show");
        sceneImageFrame.classList.add("hidden");

        sceneVisualContent.innerHTML = "";
        captionText.textContent = "";
    }

    function createVideoChartHtml() {
        const ordered =
            getOrderedPillars(
                state.analysis?.pillars
            );

        return `
            <div class="video-chart-title">

                <strong>
                    ${escapeHtml(
                        state.form.name
                    )}님의 사주
                </strong>

                <span>
                    ${escapeHtml(
                        formatBirthTitle(
                            state.form.birthDate
                        )
                    )}
                </span>

            </div>

            <div class="video-chart-grid">
                <div class="video-chart-label">시</div>
                <div class="video-chart-label">일</div>
                <div class="video-chart-label">월</div>
                <div class="video-chart-label">년</div>
            </div>

            <div class="video-chart-grid">

                ${ordered
                    .map(function (pillar) {
                        return `
                            <div class="video-chart-info">
                                ${escapeHtml(
                                    valueOrDash(
                                        pillar.stemTenGod
                                    )
                                )}
                            </div>
                        `;
                    })
                    .join("")}

            </div>

            <div class="video-chart-grid">

                ${ordered
                    .map(function (pillar) {
                        return createVideoPillarCardHtml(
                            pillar.stemKorean,
                            pillar.stemChinese,
                            pillar.stemDisplay,
                            pillar.stemClass
                        );
                    })
                    .join("")}

            </div>

            <div class="video-chart-grid"
                 style="margin-top: 5px;">

                ${ordered
                    .map(function (pillar) {
                        return createVideoPillarCardHtml(
                            pillar.branchKorean,
                            pillar.branchChinese,
                            pillar.branchDisplay,
                            pillar.branchClass
                        );
                    })
                    .join("")}

            </div>

            <div class="video-chart-grid">

                ${ordered
                    .map(function (pillar) {
                        return `
                            <div class="video-chart-info">
                                ${escapeHtml(
                                    valueOrDash(
                                        pillar.twelveStage
                                    )
                                )}
                            </div>
                        `;
                    })
                    .join("")}

            </div>
        `;
    }

    function createVideoPillarCardHtml(
        korean,
        chinese,
        display,
        className
    ) {
        return `
            <div class="video-pillar-card ${escapeHtml(
                className
            )}">

                <span>
                    ${escapeHtml(
                        valueOrDash(korean)
                    )}
                </span>

                <strong>
                    ${escapeHtml(
                        valueOrDash(chinese)
                    )}
                </strong>

                <small>
                    ${escapeHtml(
                        valueOrDash(display)
                    )}
                </small>

            </div>
        `;
    }

    function createVideoElementHtml() {
        const items =
            normalizeElementItems(
                state.analysis
                    ?.elementBalance
            );

        return `
            <div class="video-element-list">

                ${items
                    .map(function (item) {
                        return `
                            <div class="video-element-row">

                                <strong>
                                    ${escapeHtml(
                                        item.name
                                    )}
                                </strong>

                                <div class="video-element-track">

                                    <div class="video-element-value"
                                         style="width: ${item.percentage}%">
                                    </div>

                                </div>

                                <span>
                                    ${item.count}개 ·
                                    ${item.percentage}%
                                </span>

                            </div>
                        `;
                    })
                    .join("")}

            </div>
        `;
    }

    function createVideoLuckHtml() {
        const luck =
            state.analysis?.luck;

        if (!luck) {
            return createVideoMiniItem(
                "현재의 큰 흐름",
                "현재 큰 흐름의 상세 정보를 확인할 수 없습니다."
            );
        }

        const currentDecade =
            sanitizeTimeLabel(
                luck.currentDecade
            );

        const note =
            sanitizeNarrativeText(
                luck.note
                || "대운은 특정 날짜의 사건을 예측하는 표가 아니라 반복되는 삶의 주제를 살펴보는 큰 흐름입니다."
            );

        return `
            <div class="video-mini-list">

                ${createVideoMiniItem(
                    "현재의 큰 흐름",
                    currentDecade
                )}

                ${createVideoMiniItem(
                    "대운 방향",
                    valueOrDash(
                        luck.direction
                    )
                )}

                ${createVideoMiniItem(
                    "흐름을 보는 방법",
                    note
                )}

            </div>
        `;
    }

    function createVideoMiniItem(
        title,
        content
    ) {
        return `
            <article class="video-mini-item">

                <strong>
                    ${escapeHtml(
                        valueOrDash(title)
                    )}
                </strong>

                <p>
                    ${escapeHtml(
                        sanitizeNarrativeText(
                            valueOrDash(content)
                        )
                    )}
                </p>

            </article>
        `;
    }

    function renderFullResult() {
        const analysis =
            state.analysis
            || {};

        const cinematic =
            state.cinematic
            || {};

        const name =
            state.form.name
            || analysis.name
            || "사용자";

        resultTitle.textContent =
            sanitizeNarrativeText(
                cinematic.title
                || name + "님의 사주"
            );

        resultSummary.textContent =
            sanitizeNarrativeText(
                analysis.summary
                || cinematic.subtitle
                || "만세력 원국과 반복되는 성향을 종합한 결과입니다."
            );

        chartPersonTitle.textContent =
            name
            + "님의 사주 명식표";

        /*
         * 출생일은 예측 날짜가 아니므로 그대로 표시한다.
         */
        chartDateTitle.textContent =
            formatBirthTitle(
                state.form.birthDate
            );

        resultChart.innerHTML =
            createResultChartHtml(
                analysis
            );

        renderResultElements(analysis);
        renderResultEasyInterpretations(analysis);
        renderResultShinsal(analysis);
        renderResultLuck(analysis);
        renderResultAdvanced(analysis);

        resultDisclaimer.textContent =
            sanitizeNarrativeText(
                cinematic.disclaimer
                || analysis.disclaimer
                || "사주 해설은 타고난 성향과 반복되는 선택을 이해하기 위한 참고용 콘텐츠입니다."
            );
    }

    function createResultChartHtml(analysis) {
        const ordered =
            getOrderedPillars(
                analysis.pillars
            );

        return `
            <div class="result-chart-table">

                <div class="result-chart-row result-chart-header">
                    <div>시</div>
                    <div>일</div>
                    <div>월</div>
                    <div>년</div>
                </div>

                <div class="result-chart-row result-chart-info">

                    ${ordered
                        .map(function (pillar) {
                            return `
                                <div>
                                    ${escapeHtml(
                                        valueOrDash(
                                            pillar.stemTenGod
                                        )
                                    )}
                                </div>
                            `;
                        })
                        .join("")}

                </div>

                <div class="result-chart-row">

                    ${ordered
                        .map(function (pillar) {
                            return createResultPillarCardHtml(
                                pillar.stemKorean,
                                pillar.stemChinese,
                                pillar.stemDisplay,
                                pillar.stemClass
                            );
                        })
                        .join("")}

                </div>

                <div class="result-chart-row">

                    ${ordered
                        .map(function (pillar) {
                            return createResultPillarCardHtml(
                                pillar.branchKorean,
                                pillar.branchChinese,
                                pillar.branchDisplay,
                                pillar.branchClass
                            );
                        })
                        .join("")}

                </div>

                <div class="result-chart-row result-chart-info">

                    ${ordered
                        .map(function (pillar) {
                            return `
                                <div>
                                    ${escapeHtml(
                                        valueOrDash(
                                            pillar.twelveStage
                                        )
                                    )}
                                </div>
                            `;
                        })
                        .join("")}

                </div>

            </div>
        `;
    }

    function createResultPillarCardHtml(
        korean,
        chinese,
        display,
        className
    ) {
        return `
            <div class="result-pillar-card ${escapeHtml(
                className
            )}">

                <span>
                    ${escapeHtml(
                        valueOrDash(korean)
                    )}
                </span>

                <strong>
                    ${escapeHtml(
                        valueOrDash(chinese)
                    )}
                </strong>

                <small>
                    ${escapeHtml(
                        valueOrDash(display)
                    )}
                </small>

            </div>
        `;
    }

    function renderResultElements(analysis) {
        const elementBalance =
            analysis.elementBalance
            || {};

        const items =
            normalizeElementItems(
                elementBalance
            );

        resultElementList.innerHTML =
            items
                .map(function (item) {
                    return `
                        <div class="result-element-row">

                            <div class="result-element-label">

                                <strong>
                                    ${escapeHtml(
                                        item.name
                                    )}
                                </strong>

                                <span>
                                    ${item.count}개 ·
                                    ${item.percentage}%
                                </span>

                            </div>

                            <div class="result-element-track">

                                <div class="result-element-value"
                                     style="width: ${item.percentage}%">
                                </div>

                            </div>

                        </div>
                    `;
                })
                .join("");

        const dominant =
            normalizeElementName(
                elementBalance.dominant
            );

        const weakest =
            normalizeElementName(
                elementBalance.weakest
            );

        resultElementEasyList.innerHTML = `
            <article class="easy-card">

                <strong>
                    가장 강한 기운 · ${escapeHtml(
                        dominant
                    )}
                </strong>

                <p>
                    ${escapeHtml(
                        explainElementStrongest(
                            dominant
                        )
                    )}
                </p>

            </article>

            <article class="easy-card">

                <strong>
                    보완이 필요한 기운 · ${escapeHtml(
                        weakest
                    )}
                </strong>

                <p>
                    ${escapeHtml(
                        explainElementWeakest(
                            weakest
                        )
                    )}
                </p>

            </article>
        `;
    }

    function renderResultEasyInterpretations(
        analysis
    ) {
        const interpretations =
            Array.isArray(
                analysis.interpretations
            )
                ? analysis.interpretations
                : [];

        if (
            interpretations.length === 0
        ) {
            resultEasyList.innerHTML = `
                <article class="easy-card">

                    <strong>
                        종합 해설
                    </strong>

                    <p>
                        ${escapeHtml(
                            sanitizeNarrativeText(
                                analysis.summary
                                || "쉬운 사주 해설을 확인할 수 없습니다."
                            )
                        )}
                    </p>

                </article>
            `;

            return;
        }

        resultEasyList.innerHTML =
            interpretations
                .map(function (item) {
                    const title =
                        [
                            item.category,
                            item.title
                        ]
                            .filter(isNotBlank)
                            .map(
                                sanitizeNarrativeText
                            )
                            .join(" · ")
                        || "사주 해설";

                    const content =
                        sanitizeNarrativeText(
                            item.content
                            || "해석 내용이 없습니다."
                        );

                    return `
                        <article class="easy-card">

                            <strong>
                                ${escapeHtml(title)}
                            </strong>

                            <p>
                                ${escapeHtml(content)}
                            </p>

                        </article>
                    `;
                })
                .join("");
    }

    function renderResultShinsal(analysis) {
        const shinsal =
            Array.isArray(
                analysis.shinsal
            )
                ? analysis.shinsal
                : [];

        if (shinsal.length === 0) {
            resultShinsalList.innerHTML = `
                <article class="easy-card">

                    <strong>
                        주요 신살
                    </strong>

                    <p>
                        표시할 주요 신살이 없습니다.
                        신살은 전체 사주를 보조하는 참고 요소입니다.
                    </p>

                </article>
            `;

            return;
        }

        resultShinsalList.innerHTML =
            shinsal
                .map(function (item) {
                    const name =
                        sanitizeNarrativeText(
                            item.name
                            || "주요 신살"
                        );

                    const description =
                        sanitizeNarrativeText(
                            item.description
                            || "전체 원국을 보조적으로 설명하는 요소입니다."
                        );

                    return `
                        <article class="easy-card">

                            <strong>
                                ${escapeHtml(name)}
                            </strong>

                            <p>
                                ${escapeHtml(description)}
                            </p>

                        </article>
                    `;
                })
                .join("");
    }

    function renderResultLuck(analysis) {
        const luck =
            analysis.luck;

        if (!luck) {
            resultLuckList.innerHTML = `
                <article class="easy-card">

                    <strong>
                        현재의 큰 흐름
                    </strong>

                    <p>
                        현재 큰 흐름의 상세 정보를 확인할 수 없습니다.
                    </p>

                </article>
            `;

            return;
        }

        const currentDecade =
            sanitizeTimeLabel(
                luck.currentDecade
            );

        const direction =
            valueOrDash(
                luck.direction
            );

        const note =
            sanitizeNarrativeText(
                luck.note
                || "대운은 특정 날짜에 사건이 생긴다고 맞히는 표가 아니라 삶에서 반복되는 큰 주제를 보는 흐름입니다."
            );

        resultLuckList.innerHTML = `
            <article class="easy-card">

                <strong>
                    현재의 큰 흐름
                </strong>

                <p>
                    현재는 ${escapeHtml(
                        currentDecade
                    )}의 기운이 중심이 됩니다.
                    대운은 ${escapeHtml(
                        direction
                    )} 방식으로 이어집니다.
                    이것은 특정 날짜에 사건이 생긴다는 뜻이 아니라
                    직업과 돈, 관계에서 어떤 선택이 반복되기 쉬운지를
                    살펴보는 큰 배경입니다.
                </p>

            </article>

            <article class="easy-card">

                <strong>
                    현재 흐름에서 볼 점
                </strong>

                <p>
                    ${escapeHtml(note)}
                </p>

            </article>
        `;
    }

    function renderResultAdvanced(analysis) {
        const cards = [];

        if (analysis.calendar) {
            cards.push({
                title: "달력 정보",

                /*
                 * 이 날짜는 사용자의 출생정보이므로
                 * 미래 예측 날짜 제거 대상이 아니다.
                 */
                content:
                    "입력 달력: "
                    + valueOrDash(
                        analysis.calendar.inputCalendarType
                    )
                    + "\n양력: "
                    + valueOrDash(
                        analysis.calendar.solarDate
                    )
                    + "\n음력: "
                    + valueOrDash(
                        analysis.calendar.lunarDate
                    )
                    + "\n출생시간: "
                    + (
                        analysis.calendar.birthTimeUnknown
                            ? "시간 모름"
                            : valueOrDash(
                                analysis.calendar.birthTime
                            )
                    )
                    + "\n계산 기준: "
                    + valueOrDash(
                        analysis.calendar.calculationStandard
                    )
            });
        }

        if (analysis.strength) {
            cards.push({
                title:
                    "신강·신약과 보완 기운",

                content:
                    "분류: "
                    + valueOrDash(
                        analysis.strength.classification
                    )
                    + "\n점수: "
                    + valueOrDash(
                        analysis.strength.score
                    )
                    + "\n용신: "
                    + valueOrDash(
                        analysis.strength.usefulGod
                    )
                    + "\n희신: "
                    + valueOrDash(
                        analysis.strength.favorableGod
                    )
                    + "\n기신: "
                    + valueOrDash(
                        analysis.strength.unfavorableGod
                    )
                    + "\n설명: "
                    + sanitizeNarrativeText(
                        valueOrDash(
                            analysis.strength.note
                        )
                    )
            });
        }

        if (analysis.structure) {
            cards.push({
                title:
                    "태원·태식·명궁·신궁",

                content:
                    "태원: "
                    + valueOrDash(
                        analysis.structure.fetalOriginKorean
                        || analysis.structure.fetalOrigin
                    )
                    + "\n태식: "
                    + valueOrDash(
                        analysis.structure.fetalBreathKorean
                        || analysis.structure.fetalBreath
                    )
                    + "\n명궁: "
                    + valueOrDash(
                        analysis.structure.lifePalaceKorean
                        || analysis.structure.lifePalace
                    )
                    + "\n신궁: "
                    + valueOrDash(
                        analysis.structure.bodyPalaceKorean
                        || analysis.structure.bodyPalace
                    )
            });
        }

        if (analysis.relations) {
            cards.push({
                title:
                    "합·충·형·파·해",

                content:
                    createRelationText(
                        analysis.relations
                    )
            });
        }

        resultAdvancedList.innerHTML =
            cards.length === 0
                ? `
                    <article class="advanced-card">

                        <strong>
                            전문 정보
                        </strong>

                        <p>
                            표시할 추가 전문 정보가 없습니다.
                        </p>

                    </article>
                `
                : cards
                    .map(function (item) {
                        return `
                            <article class="advanced-card">

                                <strong>
                                    ${escapeHtml(
                                        item.title
                                    )}
                                </strong>

                                <p>
                                    ${escapeHtml(
                                        item.content
                                    )}
                                </p>

                            </article>
                        `;
                    })
                    .join("");
    }

    function getOrderedPillars(source) {
        const normalized =
            normalizePillars(source);

        const order = [
            "HOUR",
            "DAY",
            "MONTH",
            "YEAR"
        ];

        return order.map(function (type) {
            return (
                normalized.find(
                    function (pillar) {
                        return pillar.type === type;
                    }
                )
                || createEmptyPillar(type)
            );
        });
    }

    function normalizePillars(source) {
        if (!Array.isArray(source)) {
            return [];
        }

        return source.map(function (pillar) {
            const known =
                pillar.known !== false;

            const stemCode =
                pillar.stem
                || String(
                    pillar.ganZhi
                    || ""
                ).charAt(0);

            const branchCode =
                pillar.branch
                || String(
                    pillar.ganZhi
                    || ""
                ).charAt(1);

            const stemMeta =
                getStemMeta(stemCode);

            const branchMeta =
                getBranchMeta(branchCode);

            return {
                type:
                    normalizePillarType(
                        pillar.type
                    ),

                known: known,

                stemChinese:
                    known
                        ? stemMeta.chinese
                        : "?",

                stemKorean:
                    known
                        ? (
                            pillar.stemKorean
                            || stemMeta.korean
                        )
                        : "-",

                stemDisplay:
                    known
                        ? createElementDisplay(
                            pillar.stemYinYang,
                            pillar.stemElement
                        )
                        : "-",

                stemClass:
                    getElementClass(
                        pillar.stemElement
                    ),

                branchChinese:
                    known
                        ? branchMeta.chinese
                        : "?",

                branchKorean:
                    known
                        ? (
                            pillar.branchKorean
                            || branchMeta.korean
                        )
                        : "-",

                branchDisplay:
                    known
                        ? createElementDisplay(
                            pillar.branchYinYang,
                            pillar.branchElement
                        )
                        : "-",

                branchClass:
                    getElementClass(
                        pillar.branchElement
                    ),

                stemTenGod:
                    known
                        ? valueOrDash(
                            pillar.stemTenGod
                        )
                        : "-",

                twelveStage:
                    known
                        ? valueOrDash(
                            pillar.twelveStage
                        )
                        : "-"
            };
        });
    }

    function createEmptyPillar(type) {
        return {
            type: type,
            known: false,

            stemChinese: "?",
            stemKorean: "-",
            stemDisplay: "-",
            stemClass: "element-earth",

            branchChinese: "?",
            branchKorean: "-",
            branchDisplay: "-",
            branchClass: "element-earth",

            stemTenGod: "-",
            twelveStage: "-"
        };
    }

    function normalizePillarType(type) {
        const value =
            String(
                type
                || ""
            )
                .trim()
                .toUpperCase();

        if (
            value.includes("HOUR")
            || value.includes("TIME")
            || value === "시주"
        ) {
            return "HOUR";
        }

        if (
            value.includes("DAY")
            || value === "일주"
        ) {
            return "DAY";
        }

        if (
            value.includes("MONTH")
            || value === "월주"
        ) {
            return "MONTH";
        }

        if (
            value.includes("YEAR")
            || value === "년주"
            || value === "연주"
        ) {
            return "YEAR";
        }

        return value;
    }

    function getStemMeta(code) {
        const map = {
            甲: {
                korean: "갑",
                chinese: "甲"
            },

            乙: {
                korean: "을",
                chinese: "乙"
            },

            丙: {
                korean: "병",
                chinese: "丙"
            },

            丁: {
                korean: "정",
                chinese: "丁"
            },

            戊: {
                korean: "무",
                chinese: "戊"
            },

            己: {
                korean: "기",
                chinese: "己"
            },

            庚: {
                korean: "경",
                chinese: "庚"
            },

            辛: {
                korean: "신",
                chinese: "辛"
            },

            壬: {
                korean: "임",
                chinese: "壬"
            },

            癸: {
                korean: "계",
                chinese: "癸"
            }
        };

        return map[code]
            || {
                korean: "-",
                chinese: "?"
            };
    }

    function getBranchMeta(code) {
        const map = {
            子: {
                korean: "자",
                chinese: "子"
            },

            丑: {
                korean: "축",
                chinese: "丑"
            },

            寅: {
                korean: "인",
                chinese: "寅"
            },

            卯: {
                korean: "묘",
                chinese: "卯"
            },

            辰: {
                korean: "진",
                chinese: "辰"
            },

            巳: {
                korean: "사",
                chinese: "巳"
            },

            午: {
                korean: "오",
                chinese: "午"
            },

            未: {
                korean: "미",
                chinese: "未"
            },

            申: {
                korean: "신",
                chinese: "申"
            },

            酉: {
                korean: "유",
                chinese: "酉"
            },

            戌: {
                korean: "술",
                chinese: "戌"
            },

            亥: {
                korean: "해",
                chinese: "亥"
            }
        };

        return map[code]
            || {
                korean: "-",
                chinese: "?"
            };
    }

    function createElementDisplay(
        yinYang,
        element
    ) {
        const normalizedYinYang =
            normalizeYinYang(yinYang);

        const normalizedElement =
            normalizeElementName(element);

        return (
            (
                normalizedYinYang === "-"
                    ? ""
                    : normalizedYinYang
            )
            + (
                normalizedElement === "-"
                    ? ""
                    : normalizedElement
            )
            || "-"
        );
    }

    function normalizeYinYang(value) {
        const text =
            String(
                value
                || ""
            )
                .trim()
                .toUpperCase();

        if (
            text === "YANG"
            || text === "양"
            || text === "陽"
        ) {
            return "양";
        }

        if (
            text === "YIN"
            || text === "음"
            || text === "陰"
        ) {
            return "음";
        }

        return "-";
    }

    function normalizeElementName(value) {
        const text =
            String(
                value
                || ""
            )
                .trim()
                .toLowerCase();

        const map = {
            목: "목",
            wood: "목",
            木: "목",

            화: "화",
            fire: "화",
            火: "화",

            토: "토",
            earth: "토",
            土: "토",

            금: "금",
            metal: "금",
            金: "금",

            수: "수",
            water: "수",
            水: "수"
        };

        return map[text]
            || "-";
    }

    function getElementClass(element) {
        const normalized =
            normalizeElementName(element);

        const map = {
            목: "element-wood",
            화: "element-fire",
            토: "element-earth",
            금: "element-metal",
            수: "element-water"
        };

        return map[normalized]
            || "element-earth";
    }

    function normalizeElementItems(
        elementBalance
    ) {
        const source =
            Array.isArray(
                elementBalance?.items
            )
                ? elementBalance.items
                : [];

        const order = [
            "목",
            "화",
            "토",
            "금",
            "수"
        ];

        const itemMap = new Map();

        source.forEach(function (item) {
            itemMap.set(
                normalizeElementName(
                    item.name
                ),
                item
            );
        });

        return order.map(function (name) {
            const item =
                itemMap.get(name)
                || {};

            return {
                name: name,

                count:
                    Number(
                        item.count
                        || 0
                    ),

                percentage:
                    clampPercentage(
                        item.percentage
                    )
            };
        });
    }

    function explainElementStrongest(element) {
        const map = {
            목:
                "새로운 것을 배우고 성장시키는 힘이 강점입니다. "
                + "다만 시작한 일을 마무리하기 전에 새로운 일만 계속 벌이지 않도록 해야 합니다.",

            화:
                "표현력과 추진력, 빠른 행동력이 강점입니다. "
                + "감정이 올라온 상태에서 너무 빠르게 결정하지 않는 것이 중요합니다.",

            토:
                "책임감과 안정감, 꾸준히 유지하는 힘이 강점입니다. "
                + "모든 책임을 혼자 감당하지 않도록 해야 합니다.",

            금:
                "기준이 분명하고 판단과 정리를 잘하는 힘이 강점입니다. "
                + "완벽하지 않다는 이유로 사람과 기회를 너무 빨리 잘라내지 않아야 합니다.",

            수:
                "상황을 읽는 감각과 유연하게 적응하는 힘이 강점입니다. "
                + "생각만 오래 하다가 결정을 미루지 않도록 기준을 세워야 합니다."
        };

        return map[element]
            || "강한 기운은 장점이지만 지나치게 사용되지 않도록 전체 균형을 살펴야 합니다.";
    }

    function explainElementWeakest(element) {
        const map = {
            목:
                "작은 목표부터 바로 시작하고 배움의 흐름을 꾸준히 이어가는 연습이 도움이 됩니다.",

            화:
                "생각과 감정을 혼자 쌓아두기보다 말과 행동으로 분명하게 표현하는 연습이 도움이 됩니다.",

            토:
                "수면과 식사, 일정처럼 반복되는 생활 리듬을 안정시키는 것이 도움이 됩니다.",

            금:
                "기준과 우선순위를 미리 정하고 불필요한 일을 정리하는 습관이 도움이 됩니다.",

            수:
                "한 가지 방식만 고집하지 않고 상황에 맞춰 생각과 계획을 조정하는 연습이 도움이 됩니다."
        };

        return map[element]
            || "부족한 기운은 결함이 아니라 생활 습관과 환경으로 보완할 수 있는 부분입니다.";
    }

    function createRelationText(relations) {
        const parts = [];

        const heavenly =
            Array.isArray(
                relations.heavenlyStemRelations
            )
                ? relations.heavenlyStemRelations
                : [];

        const earthly =
            Array.isArray(
                relations.earthlyBranchRelations
            )
                ? relations.earthlyBranchRelations
                : [];

        heavenly.forEach(function (item) {
            parts.push(
                valueOrDash(item.category)
                + " · "
                + valueOrDash(item.members)
                + "\n"
                + sanitizeNarrativeText(
                    valueOrDash(
                        item.description
                    )
                )
            );
        });

        earthly.forEach(function (item) {
            parts.push(
                valueOrDash(item.category)
                + " · "
                + valueOrDash(item.members)
                + "\n"
                + sanitizeNarrativeText(
                    valueOrDash(
                        item.description
                    )
                )
            );
        });

        return parts.length > 0
            ? parts.join("\n\n")
            : "특별히 표시할 관계 정보가 많지 않습니다.";
    }

    /**
     * 출생일 표시 전용이다.
     *
     * 미래 예측 날짜 제거 기능을 적용하지 않는다.
     */
    function formatBirthTitle(dateText) {
        if (
            !dateText
            || dateText === "-"
        ) {
            return "-";
        }

        const parts =
            String(dateText).split("-");

        if (parts.length !== 3) {
            return dateText;
        }

        return (
            Number(parts[0])
            + "년 "
            + Number(parts[1])
            + "월 "
            + Number(parts[2])
            + "일"
        );
    }

    function clampPercentage(value) {
        const numeric =
            Number(value || 0);

        if (!Number.isFinite(numeric)) {
            return 0;
        }

        return Math.max(
            0,
            Math.min(
                100,
                Number(
                    numeric.toFixed(1)
                )
            )
        );
    }

    function sanitizeTimeLabel(value) {
        let text =
            sanitizeNarrativeText(
                value
                || ""
            );

        text = text
            .replace(
                /해당\s*흐름\s*[~\-–—]\s*해당\s*흐름/g,
                ""
            )
            .replace(
                /해당\s*흐름/g,
                ""
            )
            .replace(
                /해당\s*시기/g,
                ""
            )
            .replace(
                /[()[\]]/g,
                " "
            )
            .replace(
                /^[\s,·:;~\-–—]+/,
                ""
            )
            .replace(
                /[\s,·:;~\-–—]+$/,
                ""
            )
            .replace(
                /\s{2,}/g,
                " "
            )
            .trim();

        if (
            !text
            || /^[\d\s~\-–—]+$/.test(text)
        ) {
            return "현재 대운";
        }

        return text;
    }

    /**
     * 사용자에게 보이는 해설에서
     * 특정 연도와 월 표현을 제거한다.
     *
     * 출생일 표시는 이 함수를 사용하지 않는다.
     */
    function sanitizeNarrativeText(value) {
        if (
            value === null
            || value === undefined
        ) {
            return "";
        }

        let text = String(value);

        /*
         * 2028년 5월 형태
         */
        text = text.replace(
            /(^|[^\d])(?:19|20|21)\d{2}\s*년\s*\d{1,2}\s*월/g,
            "$1해당 시기"
        );

        /*
         * 2028-05, 2028.05, 2028/05 형태
         */
        text = text.replace(
            /(^|[^\d])(?:19|20|21)\d{2}\s*[-./]\s*\d{1,2}(?:\s*[-./]\s*\d{1,2})?/g,
            "$1해당 시기"
        );

        /*
         * 연도 범위
         */
        text = text.replace(
            /(^|[^\d])(?:19|20|21)\d{2}\s*[~\-–—]\s*(?:19|20|21)\d{2}/g,
            "$1해당 흐름"
        );

        /*
         * 2028년 형태
         */
        text = text.replace(
            /(^|[^\d])(?:19|20|21)\d{2}\s*년/g,
            "$1해당 흐름"
        );

        /*
         * 단독 4자리 연도
         */
        text = text.replace(
            /(^|[^\d])(?:19|20|21)\d{2}(?!\d)/g,
            "$1해당 흐름"
        );

        /*
         * 5월 3일 형태
         */
        text = text.replace(
            /(^|[^\d])\d{1,2}\s*월\s*\d{1,2}\s*일/g,
            "$1해당 시기"
        );

        /*
         * 5월, 5월부터, 5월경 형태
         *
         * 12개월처럼 기간 단위를 뜻하는 표현은 제외한다.
         */
        text = text.replace(
            /(^|[^\d])\d{1,2}\s*월(?!\s*개)(?:부터|까지|경|초|중순|말|말경)?/g,
            "$1해당 시기"
        );

        text = text
            .replace(
                /해당\s*흐름의\s*흐름/g,
                "해당 흐름"
            )
            .replace(
                /해당\s*시기\s*(?:부터|에서)\s*해당\s*시기\s*까지/g,
                "해당 흐름 동안"
            )
            .replace(
                /해당\s*흐름\s*(?:부터|에서)\s*해당\s*흐름\s*까지/g,
                "해당 흐름 동안"
            )
            .replace(
                /해당\s*시기\s+해당\s*시기/g,
                "해당 시기"
            )
            .replace(
                /해당\s*흐름\s+해당\s*흐름/g,
                "해당 흐름"
            )
            .replace(
                /\s{2,}/g,
                " "
            )
            .replace(
                /\s+([,.!?])/g,
                "$1"
            )
            .trim();

        return text;
    }

    function sanitizeTextList(values) {
        if (!Array.isArray(values)) {
            return [];
        }

        return values
            .filter(isNotBlank)
            .map(sanitizeNarrativeText)
            .filter(isNotBlank)
            .filter(function (value, index, array) {
                return array.indexOf(value) === index;
            });
    }

    function showToast(message) {
        clearTimeout(
            state.toastTimer
        );

        toast.textContent = message;
        toast.classList.add("show");

        state.toastTimer =
            window.setTimeout(
                function () {
                    toast.classList.remove("show");
                },
                2400
            );
    }

    function wait(milliseconds) {
        return new Promise(
            function (resolve) {
                window.setTimeout(
                    resolve,
                    milliseconds
                );
            }
        );
    }

    function pad2(value) {
        return String(value).padStart(
            2,
            "0"
        );
    }

    function valueOrDash(value) {
        if (
            value === null
            || value === undefined
            || value === ""
        ) {
            return "-";
        }

        return String(value);
    }

    function isNotBlank(value) {
        return (
            value !== null
            && value !== undefined
            && String(value).trim() !== ""
        );
    }

    function escapeHtml(value) {
        return String(
            value
            ?? ""
        )
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#039;");
    }
});