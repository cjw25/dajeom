document.addEventListener(
    "DOMContentLoaded",
    function () {

        const MIN_ANALYSIS_TIME =
            3200;

        const IMAGE_PATHS = {
            fallback:
                "/img/saju-character.png",

            opening:
                "/img/saju-character.png",

            manse:
                "/img/saju-cinematic/chart.png",

            energy:
                "/img/saju-cinematic/element.png",

            personality:
                "/img/saju-cinematic/personality.png",

            strength:
                "/img/saju-cinematic/strength.png",

            wealth:
                "/img/saju-cinematic/wealth.png",

            career:
                "/img/saju-cinematic/career.png",

            relationship:
                "/img/saju-cinematic/relationship.png",

            romance:
                "/img/saju-cinematic/relationship.png",

            luck:
                "/img/saju-cinematic/luck.png",

            future:
                "/img/saju-cinematic/luck.png",

            caution:
                "/img/saju-cinematic/shinsal.png",

            advice:
                "/img/saju-character.png"
        };

        const state = {
            currentScreen:
                "input",

            form:
                createEmptyForm(),

            profileId:
                null,

            analysisId:
                null,

            analysis:
                null,

            cinematic:
                null,

            forecasts:
                [],

            scenes:
                [],

            sceneIndex:
                -1,

            sceneTimer:
                null,

            captionTimer:
                null,

            progressTimer:
                null,

            toastTimer:
                null,

            progressValue:
                0
        };

        const screens =
            document.querySelectorAll(
                "[data-screen]"
            );

        const headerTitle =
            document.getElementById(
                "headerTitle"
            );

        const headerStep =
            document.getElementById(
                "headerStep"
            );

        const backButton =
            document.getElementById(
                "backButton"
            );

        const homeButtonTop =
            document.getElementById(
                "homeButtonTop"
            );

        const skipButton =
            document.getElementById(
                "skipButton"
            );

        const toast =
            document.getElementById(
                "toast"
            );

        const sajuInputForm =
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

        const lunarLeapMonth =
            document.getElementById(
                "lunarLeapMonth"
            );

        const lunarLeapWrap =
            document.getElementById(
                "lunarLeapWrap"
            );

        const timeInputWrap =
            document.getElementById(
                "timeInputWrap"
            );

        const inputError =
            document.getElementById(
                "inputError"
            );

        const reviewName =
            document.getElementById(
                "reviewName"
            );

        const reviewGender =
            document.getElementById(
                "reviewGender"
            );

        const reviewCalendar =
            document.getElementById(
                "reviewCalendar"
            );

        const reviewLeapRow =
            document.getElementById(
                "reviewLeapRow"
            );

        const reviewLeap =
            document.getElementById(
                "reviewLeap"
            );

        const reviewBirthDate =
            document.getElementById(
                "reviewBirthDate"
            );

        const reviewBirthTime =
            document.getElementById(
                "reviewBirthTime"
            );

        const editButton =
            document.getElementById(
                "editButton"
            );

        const analyzeButton =
            document.getElementById(
                "analyzeButton"
            );

        const analysisCircle =
            document.getElementById(
                "analysisCircle"
            );

        const analysisPercent =
            document.getElementById(
                "analysisPercent"
            );

        const analysisStage =
            document.getElementById(
                "analysisStage"
            );

        const analysisMessage =
            document.getElementById(
                "analysisMessage"
            );

        const analysisBar =
            document.getElementById(
                "analysisBar"
            );

        const analysisProgressText =
            document.getElementById(
                "analysisProgressText"
            );

        const analysisProgressNumber =
            document.getElementById(
                "analysisProgressNumber"
            );

        const analysisSteps =
            Array.from(
                document.querySelectorAll(
                    "[data-analysis-step]"
                )
            );

        const cinematicStage =
            document.getElementById(
                "cinematicStage"
            );

        const sceneBackgroundImage =
            document.getElementById(
                "sceneBackgroundImage"
            );

        const sceneImageFrame =
            document.getElementById(
                "sceneImageFrame"
            );

        const sceneForegroundImage =
            document.getElementById(
                "sceneForegroundImage"
            );

        const sceneImageCaption =
            document.getElementById(
                "sceneImageCaption"
            );

        const sceneVisualPanel =
            document.getElementById(
                "sceneVisualPanel"
            );

        const sceneVisualContent =
            document.getElementById(
                "sceneVisualContent"
            );

        const sceneHeading =
            document.getElementById(
                "sceneHeading"
            );

        const sceneKicker =
            document.getElementById(
                "sceneKicker"
            );

        const sceneTitle =
            document.getElementById(
                "sceneTitle"
            );

        const captionBox =
            document.getElementById(
                "captionBox"
            );

        const captionText =
            document.getElementById(
                "captionText"
            );

        const cinematicProgressValue =
            document.getElementById(
                "cinematicProgressValue"
            );

        const currentSceneNumber =
            document.getElementById(
                "currentSceneNumber"
            );

        const totalSceneNumber =
            document.getElementById(
                "totalSceneNumber"
            );

        const resultTitle =
            document.getElementById(
                "resultTitle"
            );

        const resultSummary =
            document.getElementById(
                "resultSummary"
            );

        const chartPersonTitle =
            document.getElementById(
                "chartPersonTitle"
            );

        const chartDateTitle =
            document.getElementById(
                "chartDateTitle"
            );

        const resultChart =
            document.getElementById(
                "resultChart"
            );

        const resultElementList =
            document.getElementById(
                "resultElementList"
            );

        const resultElementEasyList =
            document.getElementById(
                "resultElementEasyList"
            );

        const resultEasyList =
            document.getElementById(
                "resultEasyList"
            );

        const resultShinsalList =
            document.getElementById(
                "resultShinsalList"
            );

        const resultLuckList =
            document.getElementById(
                "resultLuckList"
            );

        const resultForecastList =
            document.getElementById(
                "resultForecastList"
            );

        const resultAdvancedList =
            document.getElementById(
                "resultAdvancedList"
            );

        const resultDisclaimer =
            document.getElementById(
                "resultDisclaimer"
            );

        const replayButton =
            document.getElementById(
                "replayButton"
            );

        const restartButton =
            document.getElementById(
                "restartButton"
            );

        const goHomeButton =
            document.getElementById(
                "goHomeButton"
            );

        initialize();

        function initialize() {

            bindEvents();

            resetApplication();

            showScreen(
                "input"
            );

            window.addEventListener(
                "pageshow",
                function (event) {

                    if (
                        event.persisted
                    ) {
                        stopCinematic();
                    }
                }
            );
        }

        function createEmptyForm() {

            return {
                name:
                    "",

                gender:
                    "",

                calendarType:
                    "SOLAR",

                lunarLeapMonth:
                    false,

                birthDate:
                    "",

                birthTime:
                    null,

                birthTimeUnknown:
                    false
            };
        }

        function resetApplication() {

            stopCinematic();

            stopFakeProgress();

            state.form =
                createEmptyForm();

            state.profileId =
                null;

            state.analysisId =
                null;

            state.analysis =
                null;

            state.cinematic =
                null;

            state.forecasts =
                [];

            state.scenes =
                [];

            state.sceneIndex =
                -1;

            state.progressValue =
                0;

            resetInputForm();

            resetSceneUi();
        }

        function resetInputForm() {

            nameInput.value =
                "";

            birthYear.value =
                "";

            birthMonth.value =
                "";

            birthDay.value =
                "";

            birthHour.value =
                "";

            birthMinute.value =
                "";

            birthTimeUnknown.checked =
                false;

            lunarLeapMonth.checked =
                false;

            birthHour.disabled =
                false;

            birthMinute.disabled =
                false;

            timeInputWrap.classList.remove(
                "disabled"
            );

            lunarLeapWrap.classList.add(
                "hidden"
            );

            document
                .querySelectorAll(
                    "[data-gender]"
                )
                .forEach(
                    function (button) {

                        button.classList.remove(
                            "active"
                        );
                    }
                );

            document
                .querySelectorAll(
                    "[data-calendar]"
                )
                .forEach(
                    function (button) {

                        button.classList.toggle(
                            "active",
                            button.dataset.calendar
                            === "SOLAR"
                        );
                    }
                );

            inputError.textContent =
                "";
        }

        function bindEvents() {

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

                                inputError.textContent =
                                    "";
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
                                                "active"
                                            );
                                        }
                                    );

                                button.classList.add(
                                    "active"
                                );

                                state.form.gender =
                                    button.dataset.gender;

                                inputError.textContent =
                                    "";
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
                                                "active"
                                            );
                                        }
                                    );

                                button.classList.add(
                                    "active"
                                );

                                state.form.calendarType =
                                    button.dataset.calendar;

                                const isLunar =
                                    state.form.calendarType
                                    === "LUNAR";

                                lunarLeapWrap.classList.toggle(
                                    "hidden",
                                    !isLunar
                                );

                                if (
                                    !isLunar
                                ) {
                                    lunarLeapMonth.checked =
                                        false;

                                    state.form.lunarLeapMonth =
                                        false;
                                }

                                inputError.textContent =
                                    "";
                            }
                        );
                    }
                );

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

                    birthHour.disabled =
                        unknown;

                    birthMinute.disabled =
                        unknown;

                    if (
                        unknown
                    ) {
                        birthHour.value =
                            "";

                        birthMinute.value =
                            "";
                    }

                    inputError.textContent =
                        "";
                }
            );

            sajuInputForm.addEventListener(
                "submit",
                function (event) {

                    event.preventDefault();

                    if (
                        !validateAndSaveForm()
                    ) {
                        return;
                    }

                    renderReview();

                    showScreen(
                        "review"
                    );
                }
            );

            editButton.addEventListener(
                "click",
                function () {

                    showScreen(
                        "input"
                    );
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

                    showScreen(
                        "input"
                    );
                }
            );

            goHomeButton.addEventListener(
                "click",
                goHome
            );
        }

        function goHome() {

            stopCinematic();

            window.location.href =
                "/";
        }

        function handleBackButton() {

            if (
                state.currentScreen
                === "input"
            ) {
                goHome();

                return;
            }

            if (
                state.currentScreen
                === "review"
            ) {
                showScreen(
                    "input"
                );

                return;
            }

            if (
                state.currentScreen
                === "analysis"
            ) {
                return;
            }

            if (
                state.currentScreen
                === "cinematic"
            ) {
                finishCinematic();

                return;
            }

            if (
                state.currentScreen
                === "result"
            ) {
                goHome();
            }
        }

        function showScreen(
            screenName
        ) {

            screens.forEach(
                function (screen) {

                    screen.classList.toggle(
                        "active",
                        screen.dataset.screen
                        === screenName
                    );
                }
            );

            state.currentScreen =
                screenName;

            const headerInformation = {
                input: {
                    title:
                        "천명해설",

                    step:
                        "사주정보 입력"
                },

                review: {
                    title:
                        "천명해설",

                    step:
                        "입력정보 확인"
                },

                analysis: {
                    title:
                        "천명해설",

                    step:
                        "사주 분석 중"
                },

                cinematic: {
                    title:
                        "천명해설",

                    step:
                        "영상 해설"
                },

                result: {
                    title:
                        "천명해설",

                    step:
                        "전체 결과"
                }
            };

            const information =
                headerInformation[
                    screenName
                ]
                || headerInformation.input;

            headerTitle.textContent =
                information.title;

            headerStep.textContent =
                information.step;

            skipButton.classList.toggle(
                "hidden",
                screenName
                !== "cinematic"
            );

            homeButtonTop.classList.toggle(
                "hidden",
                screenName
                === "cinematic"
            );

            backButton.style.visibility =
                screenName
                === "analysis"
                    ? "hidden"
                    : "visible";

            window.scrollTo({
                top:
                    0,

                behavior:
                    "auto"
            });
        }

        function validateAndSaveForm() {

            const name =
                nameInput.value.trim();

            if (
                name.length < 2
            ) {
                return setInputError(
                    "이름은 두 글자 이상 입력해주세요."
                );
            }

            if (
                !state.form.gender
            ) {
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
                state.form.calendarType
                === "SOLAR"
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

            if (
                !validBirthDate
            ) {
                return setInputError(
                    "올바른 생년월일을 입력해주세요."
                );
            }

            if (
                !birthTimeUnknown.checked
            ) {
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

            state.form.name =
                name;

            state.form.birthDate =
                birthYear.value
                + "-"
                + pad2(
                    birthMonth.value
                )
                + "-"
                + pad2(
                    birthDay.value
                );

            state.form.birthTimeUnknown =
                birthTimeUnknown.checked;

            state.form.birthTime =
                birthTimeUnknown.checked
                    ? null
                    : pad2(
                        birthHour.value
                    )
                    + ":"
                    + pad2(
                        birthMinute.value
                    );

            state.form.lunarLeapMonth =
                state.form.calendarType
                === "LUNAR"
                && lunarLeapMonth.checked;

            inputError.textContent =
                "";

            return true;
        }

        function setInputError(
            message
        ) {

            inputError.textContent =
                message;

            return false;
        }

        function isValidSolarDate(
            year,
            month,
            day
        ) {

            const numericYear =
                Number(
                    year
                );

            const numericMonth =
                Number(
                    month
                );

            const numericDay =
                Number(
                    day
                );

            const currentYear =
                new Date()
                    .getFullYear();

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

            const today =
                new Date();

            today.setHours(
                0,
                0,
                0,
                0
            );

            return (
                date.getFullYear()
                === numericYear

                && date.getMonth()
                === numericMonth - 1

                && date.getDate()
                === numericDay

                && date <= today
            );
        }

        function isValidLunarDate(
            year,
            month,
            day
        ) {

            const numericYear =
                Number(
                    year
                );

            const numericMonth =
                Number(
                    month
                );

            const numericDay =
                Number(
                    day
                );

            const currentYear =
                new Date()
                    .getFullYear();

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

            const numericHour =
                Number(
                    hour
                );

            const numericMinute =
                Number(
                    minute
                );

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
                state.form.gender
                === "MALE"
                    ? "남성"
                    : "여성";

            reviewCalendar.textContent =
                state.form.calendarType
                === "SOLAR"
                    ? "양력"
                    : "음력";

            reviewLeapRow.classList.toggle(
                "hidden",
                state.form.calendarType
                !== "LUNAR"
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

            analyzeButton.disabled =
                true;

            analyzeButton.textContent =
                "분석 중...";

            showScreen(
                "analysis"
            );

            startFakeProgress();

            try {
                const startedAt =
                    Date.now();

                analysisMessage.textContent =
                    "입력한 사주 정보를 저장하고 있습니다.";

                const profileResult =
                    await requestJson(
                        "/api/saju/profiles",
                        {
                            method:
                                "POST",

                            body:
                                JSON.stringify(
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
                    !Number.isInteger(
                        profileId
                    )
                    || profileId <= 0
                ) {
                    throw new Error(
                        "저장된 사주정보 번호를 확인할 수 없습니다."
                    );
                }

                state.profileId =
                    profileId;

                analysisMessage.textContent =
                    "만세력 원국과 오행, 십성을 계산하고 있습니다.";

                const analysisResult =
                    await requestJson(
                        "/api/saju/analyses",
                        {
                            method:
                                "POST",

                            body:
                                JSON.stringify({
                                    profileId:
                                        profileId
                                })
                        }
                    );

                const analysisId =
                    Number(
                        analysisResult.analysisId
                        ?? analysisResult.id
                    );

                if (
                    !Number.isInteger(
                        analysisId
                    )
                    || analysisId <= 0
                ) {
                    throw new Error(
                        "생성된 분석 번호를 확인할 수 없습니다."
                    );
                }

                state.analysisId =
                    analysisId;

                analysisMessage.textContent =
                    "대운과 세운, 월운을 영상 장면으로 구성하고 있습니다.";

                const [
                    cinematicResult,
                    forecastsResult
                ] =
                    await Promise.all([
                        requestJson(
                            "/api/saju/analyses/"
                            + analysisId
                            + "/cinematic"
                        ),

                        requestJson(
                            "/api/saju/analyses/"
                            + analysisId
                            + "/forecast/years"
                        ).catch(
                            function (error) {

                                console.warn(
                                    "연도별 운세 조회 실패",
                                    error
                                );

                                return [];
                            }
                        )
                    ]);

                const elapsed =
                    Date.now()
                    - startedAt;

                const remaining =
                    Math.max(
                        MIN_ANALYSIS_TIME
                        - elapsed,
                        0
                    );

                if (
                    remaining > 0
                ) {
                    await wait(
                        remaining
                    );
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

                state.forecasts =
                    Array.isArray(
                        forecastsResult
                    )
                        ? forecastsResult
                        : [];

                state.scenes =
                    normalizeCinematicScenes(
                        cinematicResult.scenes
                    );

                if (
                    state.scenes.length === 0
                ) {
                    throw new Error(
                        "영상 장면 데이터를 생성하지 못했습니다."
                    );
                }

                renderFullResult();

                analysisMessage.textContent =
                    "영상형 사주 해설 준비가 완료되었습니다.";

                await wait(
                    500
                );

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

                showScreen(
                    "review"
                );

            } finally {

                analyzeButton.disabled =
                    false;

                analyzeButton.textContent =
                    "분석하고 영상 시작";
            }
        }

        async function requestJson(
            url,
            options
        ) {

            const requestOptions = {
                method:
                    "GET",

                headers: {
                    "Accept":
                        "application/json"
                },

                ...options
            };

            requestOptions.headers = {
                "Accept":
                    "application/json",

                ...(options?.headers || {})
            };

            if (
                requestOptions.body
            ) {
                requestOptions.headers[
                    "Content-Type"
                ] =
                    "application/json";
            }

            const csrfToken =
                document
                    .querySelector(
                        'meta[name="_csrf"]'
                    )
                    ?.getAttribute(
                        "content"
                    );

            const csrfHeader =
                document
                    .querySelector(
                        'meta[name="_csrf_header"]'
                    )
                    ?.getAttribute(
                        "content"
                    );

            if (
                csrfToken
                && csrfHeader
            ) {
                requestOptions.headers[
                    csrfHeader
                ] =
                    csrfToken;
            }

            const response =
                await fetch(
                    url,
                    requestOptions
                );

            const text =
                await response.text();

            let data = {};

            if (
                text
            ) {
                try {
                    data =
                        JSON.parse(
                            text
                        );

                } catch (error) {
                    data = {
                        message:
                            text
                    };
                }
            }

            if (
                !response.ok
            ) {
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

            state.progressValue =
                0;

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

                        if (
                            next < 22
                        ) {
                            next += 4;

                            setProgress(
                                next,
                                "출생정보",
                                "양력·음력과 출생 정보를 확인하고 있습니다."
                            );

                            return;
                        }

                        if (
                            next < 52
                        ) {
                            next += 3;

                            setProgress(
                                next,
                                "명식 계산",
                                "년주·월주·일주·시주를 계산하고 있습니다."
                            );

                            return;
                        }

                        if (
                            next < 78
                        ) {
                            next += 2;

                            setProgress(
                                next,
                                "운의 흐름",
                                "대운·세운·월운의 반복 작용을 계산하고 있습니다."
                            );

                            return;
                        }

                        if (
                            next < 94
                        ) {
                            next += 1;

                            setProgress(
                                next,
                                "영상 구성",
                                "내레이션과 자막, 화면 장면을 구성하고 있습니다."
                            );
                        }

                    },
                    140
                );
        }

        function stopFakeProgress() {

            if (
                state.progressTimer
            ) {
                clearInterval(
                    state.progressTimer
                );

                state.progressTimer =
                    null;
            }
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
                        target
                        - start;

                    const startedAt =
                        performance.now();

                    function frame(
                        now
                    ) {

                        const elapsed =
                            now
                            - startedAt;

                        const ratio =
                            Math.min(
                                elapsed
                                / duration,
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

                        if (
                            ratio < 1
                        ) {
                            requestAnimationFrame(
                                frame
                            );

                            return;
                        }

                        resolve();
                    }

                    requestAnimationFrame(
                        frame
                    );
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
                rounded
                + "%";

            analysisBar.style.width =
                rounded
                + "%";

            analysisProgressNumber.textContent =
                rounded
                + " / 100";

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

        function updateAnalysisSteps(
            progress
        ) {

            let activeStep =
                0;

            if (
                progress < 25
            ) {
                activeStep =
                    0;

            } else if (
                progress < 55
            ) {
                activeStep =
                    1;

            } else if (
                progress < 80
            ) {
                activeStep =
                    2;

            } else if (
                progress < 100
            ) {
                activeStep =
                    3;

            } else {
                activeStep =
                    analysisSteps.length;
            }

            analysisSteps.forEach(
                function (
                    step,
                    index
                ) {

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

                    if (
                        icon
                    ) {
                        icon.textContent =
                            complete
                                ? "✓"
                                : String(
                                    index + 1
                                );
                    }
                }
            );
        }

        function normalizeCinematicScenes(
            source
        ) {

            if (
                !Array.isArray(
                    source
                )
            ) {
                return [];
            }

            return source
                .filter(
                    function (scene) {

                        return scene
                        && typeof scene
                        === "object";
                    }
                )
                .sort(
                    function (
                        first,
                        second
                    ) {

                        return Number(
                            first.order
                            || 0
                        )
                        - Number(
                            second.order
                            || 0
                        );
                    }
                )
                .map(
                    function (
                        scene,
                        index
                    ) {

                        const sceneType =
                            String(
                                scene.sceneType
                                || "OPENING"
                            );

                        return {
                            order:
                                Number(
                                    scene.order
                                    || index + 1
                                ),

                            sceneType:
                                sceneType,

                            kicker:
                                sceneTypeKicker(
                                    sceneType
                                ),

                            title:
                                scene.title
                                || "천명해설",

                            header:
                                sceneTypeLabel(
                                    sceneType
                                ),

                            text:
                                scene.narration
                                || scene.subtitle
                                || "해설을 준비하고 있습니다.",

                            subtitle:
                                scene.subtitle
                                || "",

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
                                scene.title
                                || "영상 장면",

                            visualType:
                                resolveVisualType(
                                    sceneType
                                ),

                            period:
                                scene.period
                                || null,

                            highlights:
                                Array.isArray(
                                    scene.highlights
                                )
                                    ? scene.highlights
                                    : [],

                            evidences:
                                Array.isArray(
                                    scene.evidences
                                )
                                    ? scene.evidences
                                    : []
                        };
                    }
                );
        }

        function normalizeSceneDuration(
            seconds
        ) {

            const numeric =
                Number(
                    seconds
                );

            if (
                !Number.isFinite(
                    numeric
                )
            ) {
                return 9000;
            }

            return Math.max(
                5000,
                Math.min(
                    20000,
                    numeric
                    * 1000
                )
            );
        }

        function sceneTypeKicker(
            sceneType
        ) {

            const map = {
                OPENING:
                    "天命解說",

                MANSE_CALENDAR:
                    "四柱命式",

                BASIC_ENERGY:
                    "五行氣運",

                PERSONALITY_OUTER:
                    "外面性格",

                PERSONALITY_INNER:
                    "內面心性",

                STRENGTHS:
                    "長點解說",

                WEAKNESSES:
                    "補完方向",

                WEALTH:
                    "財物運",

                CAREER_BUSINESS:
                    "職業事業",

                RELATIONSHIPS:
                    "人間關係",

                ROMANCE:
                    "戀愛因緣",

                CURRENT_DAEUN:
                    "大運流轉",

                FUTURE_OVERVIEW:
                    "未來流向",

                YEAR_FORECAST:
                    "歲運解說",

                MONTH_PERIOD:
                    "月運期間",

                CAUTION_PERIOD:
                    "注意期間",

                OPPORTUNITY_PERIOD:
                    "機會期間",

                ADVICE:
                    "現實助言",

                EVIDENCE:
                    "命理根據"
            };

            return map[
                sceneType
            ]
            || "天命解說";
        }

        function sceneTypeLabel(
            sceneType
        ) {

            const map = {
                OPENING:
                    "영상 해설 시작",

                MANSE_CALENDAR:
                    "사주 명식표",

                BASIC_ENERGY:
                    "타고난 기본 기운",

                PERSONALITY_OUTER:
                    "겉으로 보이는 성격",

                PERSONALITY_INNER:
                    "실제 속마음",

                STRENGTHS:
                    "장점",

                WEAKNESSES:
                    "단점과 보완",

                WEALTH:
                    "재물운",

                CAREER_BUSINESS:
                    "직업과 사업운",

                RELATIONSHIPS:
                    "사람복",

                ROMANCE:
                    "연애와 인연",

                CURRENT_DAEUN:
                    "현재 대운",

                FUTURE_OVERVIEW:
                    "미래 전체 흐름",

                YEAR_FORECAST:
                    "연도별 세운",

                MONTH_PERIOD:
                    "집중 작용월",

                CAUTION_PERIOD:
                    "주의 기간",

                OPPORTUNITY_PERIOD:
                    "기회 활용 기간",

                ADVICE:
                    "현실적인 조언",

                EVIDENCE:
                    "해석 근거"
            };

            return map[
                sceneType
            ]
            || "천명해설";
        }

        function resolveCamera(
            sceneType,
            visualKey,
            index
        ) {

            if (
                sceneType
                === "MANSE_CALENDAR"
                || sceneType
                === "BASIC_ENERGY"
            ) {
                return "camera-soft";
            }

            if (
                sceneType
                === "CAUTION_PERIOD"
            ) {
                return "camera-close";
            }

            if (
                sceneType
                === "CURRENT_DAEUN"
                || sceneType
                === "FUTURE_OVERVIEW"
                || sceneType
                === "YEAR_FORECAST"
            ) {
                return "camera-medium";
            }

            if (
                String(
                    visualKey
                    || ""
                ).includes(
                    "CLOSE"
                )
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
                OPENING:
                    IMAGE_PATHS.opening,

                MANSE_CALENDAR:
                    IMAGE_PATHS.manse,

                BASIC_ENERGY:
                    IMAGE_PATHS.energy,

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

            return map[
                sceneType
            ]
            || visualKeyToImage(
                visualKey
            )
            || IMAGE_PATHS.fallback;
        }

        function visualKeyToImage(
            visualKey
        ) {

            const key =
                String(
                    visualKey
                    || ""
                ).toUpperCase();

            if (
                key.includes(
                    "WEALTH"
                )
            ) {
                return IMAGE_PATHS.wealth;
            }

            if (
                key.includes(
                    "CAREER"
                )
                || key.includes(
                    "BUSINESS"
                )
            ) {
                return IMAGE_PATHS.career;
            }

            if (
                key.includes(
                    "ROMANCE"
                )
                || key.includes(
                    "RELATION"
                )
            ) {
                return IMAGE_PATHS.relationship;
            }

            if (
                key.includes(
                    "CAUTION"
                )
                || key.includes(
                    "WARNING"
                )
            ) {
                return IMAGE_PATHS.caution;
            }

            if (
                key.includes(
                    "YEAR"
                )
                || key.includes(
                    "FUTURE"
                )
                || key.includes(
                    "DAEUN"
                )
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

        function resolveVisualType(
            sceneType
        ) {

            if (
                sceneType
                === "MANSE_CALENDAR"
            ) {
                return "CHART";
            }

            if (
                sceneType
                === "BASIC_ENERGY"
            ) {
                return "ELEMENT";
            }

            if (
                sceneType
                === "CURRENT_DAEUN"
            ) {
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
                ].includes(
                    sceneType
                )
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

                showScreen(
                    "result"
                );

                return;
            }

            state.sceneIndex =
                -1;

            totalSceneNumber.textContent =
                String(
                    state.scenes.length
                );

            currentSceneNumber.textContent =
                "0";

            cinematicProgressValue.style.width =
                "0";

            resetSceneUi();

            showScreen(
                "cinematic"
            );

            window.setTimeout(
                function () {

                    playScene(
                        0
                    );
                },
                300
            );
        }

        function playScene(
            index
        ) {

            clearSceneTimers();

            if (
                index
                >= state.scenes.length
            ) {
                finishCinematic();

                return;
            }

            state.sceneIndex =
                index;

            const scene =
                state.scenes[
                    index
                ];

            applyScene(
                scene
            );

            updateCinematicProgress(
                index
            );

            showCaption(
                scene.text,
                scene.duration
            );

            state.sceneTimer =
                window.setTimeout(
                    function () {

                        playScene(
                            index + 1
                        );
                    },
                    scene.duration
                );
        }

        function applyScene(
            scene
        ) {

            cinematicStage.className =
                "cinematic-stage "
                + scene.camera;

            headerStep.textContent =
                scene.header;

            sceneKicker.textContent =
                scene.kicker;

            sceneTitle.textContent =
                scene.title;

            sceneHeading.classList.remove(
                "show"
            );

            captionBox.classList.remove(
                "show"
            );

            sceneVisualPanel.classList.remove(
                "show"
            );

            sceneImageFrame.classList.remove(
                "show"
            );

            setImageWithFallback(
                sceneBackgroundImage,
                scene.backgroundImage
            );

            renderSceneImage(
                scene
            );

            renderSceneVisual(
                scene
            );

            window.requestAnimationFrame(
                function () {

                    sceneHeading.classList.add(
                        "show"
                    );
                }
            );
        }

        function renderSceneImage(
            scene
        ) {

            if (
                !scene.foregroundImage
            ) {
                sceneImageFrame.classList.add(
                    "hidden"
                );

                return;
            }

            sceneImageFrame.classList.remove(
                "hidden"
            );

            sceneImageCaption.textContent =
                scene.foregroundCaption;

            setImageWithFallback(
                sceneForegroundImage,
                scene.foregroundImage
            );

            window.requestAnimationFrame(
                function () {

                    sceneImageFrame.classList.add(
                        "show"
                    );
                }
            );
        }

        function setImageWithFallback(
            imageElement,
            source
        ) {

            imageElement.onerror =
                function () {

                    imageElement.onerror =
                        null;

                    imageElement.src =
                        IMAGE_PATHS.fallback;
                };

            imageElement.src =
                source
                || IMAGE_PATHS.fallback;
        }

        function renderSceneVisual(
            scene
        ) {

            sceneVisualContent.innerHTML =
                "";

            if (
                scene.visualType
                === "CHART"
            ) {
                sceneVisualContent.innerHTML =
                    createVideoChartHtml();

            } else if (
                scene.visualType
                === "ELEMENT"
            ) {
                sceneVisualContent.innerHTML =
                    createVideoElementHtml();

            } else if (
                scene.visualType
                === "LUCK"
            ) {
                sceneVisualContent.innerHTML =
                    createVideoLuckHtml();

            } else if (
                scene.visualType
                === "DETAIL"
            ) {
                sceneVisualContent.innerHTML =
                    createSceneDetailHtml(
                        scene
                    );

            } else {
                sceneVisualPanel.classList.add(
                    "hidden"
                );

                return;
            }

            sceneVisualPanel.classList.remove(
                "hidden"
            );

            window.requestAnimationFrame(
                function () {

                    sceneVisualPanel.classList.add(
                        "show"
                    );
                }
            );
        }

        function createSceneDetailHtml(
            scene
        ) {

            const periodHtml =
                createScenePeriodHtml(
                    scene.period
                );

            const highlights =
                scene.highlights
                    .filter(
                        isNotBlank
                    )
                    .slice(
                        0,
                        7
                    );

            const evidences =
                scene.evidences
                    .filter(
                        isNotBlank
                    )
                    .slice(
                        0,
                        6
                    );

            const highlightHtml =
                highlights
                    .map(
                        function (item) {

                            return createVideoMiniItem(
                                "핵심 내용",
                                item
                            );
                        }
                    )
                    .join("");

            const evidenceHtml =
                evidences
                    .map(
                        function (item) {

                            return createVideoMiniItem(
                                "해석 근거",
                                item
                            );
                        }
                    )
                    .join("");

            if (
                !periodHtml
                && !highlightHtml
                && !evidenceHtml
            ) {
                return createVideoMiniItem(
                    "천명해설",
                    scene.subtitle
                    || scene.text
                );
            }

            return periodHtml
                + '<div class="scene-detail-list">'
                + highlightHtml
                + evidenceHtml
                + "</div>";
        }

        function createScenePeriodHtml(
            period
        ) {

            if (
                !period
                || typeof period
                !== "object"
            ) {
                return "";
            }

            const displayText =
                period.displayText
                || createPeriodDisplayText(
                    period
                );

            if (
                !displayText
            ) {
                return "";
            }

            return `
                <div class="scene-period-card">

                    <strong>
                        영향 기간
                    </strong>

                    <p>
                        ${escapeHtml(
                            displayText
                        )}
                    </p>

                </div>
            `;
        }

        function createPeriodDisplayText(
            period
        ) {

            const start =
                formatYearMonthValue(
                    period.startYearMonth
                );

            const peakStart =
                formatYearMonthValue(
                    period.peakStartYearMonth
                );

            const peakEnd =
                formatYearMonthValue(
                    period.peakEndYearMonth
                );

            const end =
                formatYearMonthValue(
                    period.endYearMonth
                );

            const parts =
                [];

            if (
                start !== "-"
                && end !== "-"
            ) {
                parts.push(
                    "전체 영향 "
                    + start
                    + " ~ "
                    + end
                );
            }

            if (
                peakStart !== "-"
                && peakEnd !== "-"
            ) {
                parts.push(
                    "집중 작용 "
                    + peakStart
                    + " ~ "
                    + peakEnd
                );
            }

            return parts.join(
                " · "
            );
        }

        function showCaption(
            text,
            duration
        ) {

            captionText.textContent =
                "";

            window.setTimeout(
                function () {

                    captionBox.classList.add(
                        "show"
                    );

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

            if (
                state.captionTimer
            ) {
                clearInterval(
                    state.captionTimer
                );
            }

            const characters =
                Array.from(
                    String(
                        text
                        || ""
                    )
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

            let index =
                0;

            state.captionTimer =
                window.setInterval(
                    function () {

                        index += 1;

                        captionText.textContent =
                            characters
                                .slice(
                                    0,
                                    index
                                )
                                .join("");

                        if (
                            index
                            >= characters.length
                        ) {
                            clearInterval(
                                state.captionTimer
                            );

                            state.captionTimer =
                                null;
                        }
                    },
                    interval
                );
        }

        function updateCinematicProgress(
            index
        ) {

            const progress =
                (
                    (index + 1)
                    / state.scenes.length
                )
                * 100;

            currentSceneNumber.textContent =
                String(
                    index + 1
                );

            cinematicProgressValue.style.width =
                progress
                + "%";
        }

        function finishCinematic() {

            stopCinematic();

            resetSceneUi();

            showScreen(
                "result"
            );
        }

        function stopCinematic() {

            clearSceneTimers();
        }

        function clearSceneTimers() {

            if (
                state.sceneTimer
            ) {
                clearTimeout(
                    state.sceneTimer
                );

                state.sceneTimer =
                    null;
            }

            if (
                state.captionTimer
            ) {
                clearInterval(
                    state.captionTimer
                );

                state.captionTimer =
                    null;
            }
        }

        function resetSceneUi() {

            sceneHeading.classList.remove(
                "show"
            );

            captionBox.classList.remove(
                "show"
            );

            sceneVisualPanel.classList.remove(
                "show"
            );

            sceneVisualPanel.classList.add(
                "hidden"
            );

            sceneImageFrame.classList.remove(
                "show"
            );

            sceneImageFrame.classList.add(
                "hidden"
            );

            sceneVisualContent.innerHTML =
                "";

            captionText.textContent =
                "";
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
                        .map(
                            function (pillar) {

                                return `
                                    <div class="video-chart-info">
                                        ${escapeHtml(
                                            valueOrDash(
                                                pillar.stemTenGod
                                            )
                                        )}
                                    </div>
                                `;
                            }
                        )
                        .join("")}

                </div>

                <div class="video-chart-grid">

                    ${ordered
                        .map(
                            function (pillar) {

                                return createVideoPillarCardHtml(
                                    pillar.stemKorean,
                                    pillar.stemChinese,
                                    pillar.stemDisplay,
                                    pillar.stemClass
                                );
                            }
                        )
                        .join("")}

                </div>

                <div class="video-chart-grid"
                     style="margin-top: 5px;">

                    ${ordered
                        .map(
                            function (pillar) {

                                return createVideoPillarCardHtml(
                                    pillar.branchKorean,
                                    pillar.branchChinese,
                                    pillar.branchDisplay,
                                    pillar.branchClass
                                );
                            }
                        )
                        .join("")}

                </div>

                <div class="video-chart-grid">

                    ${ordered
                        .map(
                            function (pillar) {

                                return `
                                    <div class="video-chart-info">
                                        ${escapeHtml(
                                            valueOrDash(
                                                pillar.twelveStage
                                            )
                                        )}
                                    </div>
                                `;
                            }
                        )
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
                            valueOrDash(
                                korean
                            )
                        )}
                    </span>

                    <strong>
                        ${escapeHtml(
                            valueOrDash(
                                chinese
                            )
                        )}
                    </strong>

                    <small>
                        ${escapeHtml(
                            valueOrDash(
                                display
                            )
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
                        .map(
                            function (item) {

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
                            }
                        )
                        .join("")}

                </div>
            `;
        }

        function createVideoLuckHtml() {

            const luck =
                state.analysis
                    ?.luck;

            if (
                !luck
            ) {
                return createVideoMiniItem(
                    "현재 대운",
                    "현재 대운 정보를 확인할 수 없습니다."
                );
            }

            return `
                <div class="video-mini-list">

                    ${createVideoMiniItem(
                        "현재 대운",
                        valueOrDash(
                            luck.currentDecade
                        )
                    )}

                    ${createVideoMiniItem(
                        "대운 방향",
                        valueOrDash(
                            luck.direction
                        )
                    )}

                    ${createVideoMiniItem(
                        "대운 시작",
                        valueOrDash(
                            luck.startAge
                        )
                    )}

                    ${createVideoMiniItem(
                        "대운 설명",
                        valueOrDash(
                            luck.note
                        )
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
                            valueOrDash(
                                title
                            )
                        )}
                    </strong>

                    <p>
                        ${escapeHtml(
                            valueOrDash(
                                content
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
                cinematic.title
                || name
                + "님의 사주";

            resultSummary.textContent =
                analysis.summary
                || cinematic.subtitle
                || "만세력 원국과 미래 흐름을 종합한 결과입니다.";

            chartPersonTitle.textContent =
                name
                + "님의 사주 명식표";

            chartDateTitle.textContent =
                formatBirthTitle(
                    state.form.birthDate
                );

            resultChart.innerHTML =
                createResultChartHtml(
                    analysis
                );

            renderResultElements(
                analysis
            );

            renderResultEasyInterpretations(
                analysis
            );

            renderResultShinsal(
                analysis
            );

            renderResultLuck(
                analysis
            );

            renderResultForecasts();

            renderResultAdvanced(
                analysis
            );

            resultDisclaimer.textContent =
                cinematic.disclaimer
                || analysis.disclaimer
                || "사주 해설은 가능성과 경향을 설명하는 참고용 콘텐츠입니다.";
        }

        function createResultChartHtml(
            analysis
        ) {

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
                            .map(
                                function (pillar) {

                                    return `
                                        <div>
                                            ${escapeHtml(
                                                valueOrDash(
                                                    pillar.stemTenGod
                                                )
                                            )}
                                        </div>
                                    `;
                                }
                            )
                            .join("")}

                    </div>

                    <div class="result-chart-row">

                        ${ordered
                            .map(
                                function (pillar) {

                                    return createResultPillarCardHtml(
                                        pillar.stemKorean,
                                        pillar.stemChinese,
                                        pillar.stemDisplay,
                                        pillar.stemClass
                                    );
                                }
                            )
                            .join("")}

                    </div>

                    <div class="result-chart-row">

                        ${ordered
                            .map(
                                function (pillar) {

                                    return createResultPillarCardHtml(
                                        pillar.branchKorean,
                                        pillar.branchChinese,
                                        pillar.branchDisplay,
                                        pillar.branchClass
                                    );
                                }
                            )
                            .join("")}

                    </div>

                    <div class="result-chart-row result-chart-info">

                        ${ordered
                            .map(
                                function (pillar) {

                                    return `
                                        <div>
                                            ${escapeHtml(
                                                valueOrDash(
                                                    pillar.twelveStage
                                                )
                                            )}
                                        </div>
                                    `;
                                }
                            )
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
                            valueOrDash(
                                korean
                            )
                        )}
                    </span>

                    <strong>
                        ${escapeHtml(
                            valueOrDash(
                                chinese
                            )
                        )}
                    </strong>

                    <small>
                        ${escapeHtml(
                            valueOrDash(
                                display
                            )
                        )}
                    </small>

                </div>
            `;
        }

        function renderResultElements(
            analysis
        ) {

            const elementBalance =
                analysis.elementBalance
                || {};

            const items =
                normalizeElementItems(
                    elementBalance
                );

            resultElementList.innerHTML =
                items
                    .map(
                        function (item) {

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
                        }
                    )
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
                                analysis.summary
                                || "쉬운 사주 해설을 확인할 수 없습니다."
                            )}
                        </p>

                    </article>
                `;

                return;
            }

            resultEasyList.innerHTML =
                interpretations
                    .map(
                        function (item) {

                            const title =
                                [
                                    item.category,
                                    item.title
                                ]
                                    .filter(
                                        isNotBlank
                                    )
                                    .join(
                                        " · "
                                    )
                                || "사주 해설";

                            return `
                                <article class="easy-card">

                                    <strong>
                                        ${escapeHtml(
                                            title
                                        )}
                                    </strong>

                                    <p>
                                        ${escapeHtml(
                                            item.content
                                            || "해석 내용이 없습니다."
                                        )}
                                    </p>

                                </article>
                            `;
                        }
                    )
                    .join("");
        }

        function renderResultShinsal(
            analysis
        ) {

            const shinsal =
                Array.isArray(
                    analysis.shinsal
                )
                    ? analysis.shinsal
                    : [];

            if (
                shinsal.length === 0
            ) {
                resultShinsalList.innerHTML = `
                    <article class="easy-card">

                        <strong>
                            주요 신살
                        </strong>

                        <p>
                            표시할 주요 신살이 없습니다. 신살은 전체 사주를 보조하는 참고 요소입니다.
                        </p>

                    </article>
                `;

                return;
            }

            resultShinsalList.innerHTML =
                shinsal
                    .map(
                        function (item) {

                            return `
                                <article class="easy-card">

                                    <strong>
                                        ${escapeHtml(
                                            item.name
                                            || "주요 신살"
                                        )}
                                    </strong>

                                    <p>
                                        ${escapeHtml(
                                            item.description
                                            || "전체 원국을 보조적으로 설명하는 요소입니다."
                                        )}
                                    </p>

                                </article>
                            `;
                        }
                    )
                    .join("");
        }

        function renderResultLuck(
            analysis
        ) {

            const luck =
                analysis.luck;

            if (
                !luck
            ) {
                resultLuckList.innerHTML = `
                    <article class="easy-card">

                        <strong>
                            현재 대운
                        </strong>

                        <p>
                            현재 대운 정보를 확인할 수 없습니다.
                        </p>

                    </article>
                `;

                return;
            }

            resultLuckList.innerHTML = `
                <article class="easy-card">

                    <strong>
                        현재 큰 흐름
                    </strong>

                    <p>
                        현재 대운은 ${escapeHtml(
                            valueOrDash(
                                luck.currentDecade
                            )
                        )}으로 표시됩니다.
                        운행 방향은 ${escapeHtml(
                            valueOrDash(
                                luck.direction
                            )
                        )}이며 대운 시작은 ${escapeHtml(
                            valueOrDash(
                                luck.startAge
                            )
                        )} 전후입니다.
                    </p>

                </article>

                <article class="easy-card">

                    <strong>
                        대운 설명
                    </strong>

                    <p>
                        ${escapeHtml(
                            luck.note
                            || "대운은 약 10년 동안 이어지는 큰 흐름이며 매년의 세운과 월운이 그 위에 겹칩니다."
                        )}
                    </p>

                </article>
            `;
        }

        function renderResultForecasts() {

            if (
                !Array.isArray(
                    state.forecasts
                )
                || state.forecasts.length === 0
            ) {
                resultForecastList.innerHTML = `
                    <article class="forecast-card">

                        <strong>
                            연도별 흐름
                        </strong>

                        <p>
                            연도별 미래 흐름을 확인할 수 없습니다.
                        </p>

                    </article>
                `;

                return;
            }

            resultForecastList.innerHTML =
                state.forecasts
                    .map(
                        function (forecast) {

                            const period =
                                forecast.influencePeriod
                                || {};

                            const scores =
                                forecast.scores
                                || {};

                            const periodText =
                                createPeriodDisplayText(
                                    period
                                );

                            return `
                                <article class="forecast-card">

                                    <strong>
                                        ${escapeHtml(
                                            forecast.year
                                            + "년 · "
                                            + valueOrDash(
                                                forecast.theme
                                            )
                                        )}
                                    </strong>

                                    <div class="forecast-card-meta">

                                        <span>
                                            ${escapeHtml(
                                                valueOrDash(
                                                    forecast.ganji
                                                )
                                            )}
                                        </span>

                                        <span>
                                            ${escapeHtml(
                                                valueOrDash(
                                                    forecast.confidence
                                                )
                                            )}
                                        </span>

                                        <span>
                                            근거 ${escapeHtml(
                                                valueOrDash(
                                                    forecast.evidenceCount
                                                )
                                            )}개
                                        </span>

                                    </div>

                                    <p>
                                        ${escapeHtml(
                                            forecast.summary
                                            || forecast.headline
                                            || "연도별 흐름입니다."
                                        )}
                                    </p>

                                    ${periodText
                                        ? `
                                            <p style="margin-top: 8px;">
                                                <strong style="font-size: 9px;">
                                                    영향 기간
                                                </strong>
                                                ${escapeHtml(
                                                    periodText
                                                )}
                                            </p>
                                        `
                                        : ""
                                    }

                                    <div class="forecast-score-grid">

                                        ${createForecastScore(
                                            "직업",
                                            scores.career
                                        )}

                                        ${createForecastScore(
                                            "재물",
                                            scores.wealth
                                        )}

                                        ${createForecastScore(
                                            "사업",
                                            scores.business
                                        )}

                                        ${createForecastScore(
                                            "관계",
                                            scores.relationship
                                        )}

                                        ${createForecastScore(
                                            "연애",
                                            scores.romance
                                        )}

                                        ${createForecastScore(
                                            "변화",
                                            scores.change
                                        )}

                                        ${createForecastScore(
                                            "주의",
                                            scores.caution
                                        )}

                                    </div>

                                </article>
                            `;
                        }
                    )
                    .join("");
        }

        function createForecastScore(
            label,
            score
        ) {

            return `
                <div>

                    <span>
                        ${escapeHtml(
                            label
                        )}
                    </span>

                    <strong>
                        ${escapeHtml(
                            valueOrDash(
                                score
                            )
                        )}
                    </strong>

                </div>
            `;
        }

        function renderResultAdvanced(
            analysis
        ) {

            const cards =
                [];

            if (
                analysis.calendar
            ) {
                cards.push({
                    title:
                        "달력 정보",

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

            if (
                analysis.strength
            ) {
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
                        + valueOrDash(
                            analysis.strength.note
                        )
                });
            }

            if (
                analysis.structure
            ) {
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

            if (
                analysis.relations
            ) {
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
                        .map(
                            function (item) {

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
                            }
                        )
                        .join("");
        }

        function getOrderedPillars(
            source
        ) {

            const normalized =
                normalizePillars(
                    source
                );

            const order = [
                "HOUR",
                "DAY",
                "MONTH",
                "YEAR"
            ];

            return order.map(
                function (type) {

                    return normalized.find(
                        function (pillar) {

                            return pillar.type
                            === type;
                        }
                    )
                    || createEmptyPillar(
                        type
                    );
                }
            );
        }

        function normalizePillars(
            source
        ) {

            if (
                !Array.isArray(
                    source
                )
            ) {
                return [];
            }

            return source.map(
                function (pillar) {

                    const known =
                        pillar.known
                        !== false;

                    const stemCode =
                        pillar.stem
                        || String(
                            pillar.ganZhi
                            || ""
                        ).charAt(
                            0
                        );

                    const branchCode =
                        pillar.branch
                        || String(
                            pillar.ganZhi
                            || ""
                        ).charAt(
                            1
                        );

                    const stemMeta =
                        getStemMeta(
                            stemCode
                        );

                    const branchMeta =
                        getBranchMeta(
                            branchCode
                        );

                    return {
                        type:
                            normalizePillarType(
                                pillar.type
                            ),

                        known:
                            known,

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
                }
            );
        }

        function createEmptyPillar(
            type
        ) {

            return {
                type:
                    type,

                known:
                    false,

                stemChinese:
                    "?",

                stemKorean:
                    "-",

                stemDisplay:
                    "-",

                stemClass:
                    "element-earth",

                branchChinese:
                    "?",

                branchKorean:
                    "-",

                branchDisplay:
                    "-",

                branchClass:
                    "element-earth",

                stemTenGod:
                    "-",

                twelveStage:
                    "-"
            };
        }

        function normalizePillarType(
            type
        ) {

            const value =
                String(
                    type
                    || ""
                )
                    .trim()
                    .toUpperCase();

            if (
                value.includes(
                    "HOUR"
                )
                || value.includes(
                    "TIME"
                )
                || value === "시주"
            ) {
                return "HOUR";
            }

            if (
                value.includes(
                    "DAY"
                )
                || value === "일주"
            ) {
                return "DAY";
            }

            if (
                value.includes(
                    "MONTH"
                )
                || value === "월주"
            ) {
                return "MONTH";
            }

            if (
                value.includes(
                    "YEAR"
                )
                || value === "년주"
                || value === "연주"
            ) {
                return "YEAR";
            }

            return value;
        }

        function getStemMeta(
            code
        ) {

            const map = {
                甲: {
                    korean:
                        "갑",

                    chinese:
                        "甲"
                },

                乙: {
                    korean:
                        "을",

                    chinese:
                        "乙"
                },

                丙: {
                    korean:
                        "병",

                    chinese:
                        "丙"
                },

                丁: {
                    korean:
                        "정",

                    chinese:
                        "丁"
                },

                戊: {
                    korean:
                        "무",

                    chinese:
                        "戊"
                },

                己: {
                    korean:
                        "기",

                    chinese:
                        "己"
                },

                庚: {
                    korean:
                        "경",

                    chinese:
                        "庚"
                },

                辛: {
                    korean:
                        "신",

                    chinese:
                        "辛"
                },

                壬: {
                    korean:
                        "임",

                    chinese:
                        "壬"
                },

                癸: {
                    korean:
                        "계",

                    chinese:
                        "癸"
                }
            };

            return map[
                code
            ]
            || {
                korean:
                    "-",

                chinese:
                    "?"
            };
        }

        function getBranchMeta(
            code
        ) {

            const map = {
                子: {
                    korean:
                        "자",

                    chinese:
                        "子"
                },

                丑: {
                    korean:
                        "축",

                    chinese:
                        "丑"
                },

                寅: {
                    korean:
                        "인",

                    chinese:
                        "寅"
                },

                卯: {
                    korean:
                        "묘",

                    chinese:
                        "卯"
                },

                辰: {
                    korean:
                        "진",

                    chinese:
                        "辰"
                },

                巳: {
                    korean:
                        "사",

                    chinese:
                        "巳"
                },

                午: {
                    korean:
                        "오",

                    chinese:
                        "午"
                },

                未: {
                    korean:
                        "미",

                    chinese:
                        "未"
                },

                申: {
                    korean:
                        "신",

                    chinese:
                        "申"
                },

                酉: {
                    korean:
                        "유",

                    chinese:
                        "酉"
                },

                戌: {
                    korean:
                        "술",

                    chinese:
                        "戌"
                },

                亥: {
                    korean:
                        "해",

                    chinese:
                        "亥"
                }
            };

            return map[
                code
            ]
            || {
                korean:
                    "-",

                chinese:
                    "?"
            };
        }

        function createElementDisplay(
            yinYang,
            element
        ) {

            const normalizedYinYang =
                normalizeYinYang(
                    yinYang
                );

            const normalizedElement =
                normalizeElementName(
                    element
                );

            return (
                normalizedYinYang === "-"
                    ? ""
                    : normalizedYinYang
            )
            + (
                normalizedElement === "-"
                    ? ""
                    : normalizedElement
            )
            || "-";
        }

        function normalizeYinYang(
            value
        ) {

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

        function normalizeElementName(
            value
        ) {

            const text =
                String(
                    value
                    || ""
                )
                    .trim()
                    .toLowerCase();

            const map = {
                목:
                    "목",

                wood:
                    "목",

                木:
                    "목",

                화:
                    "화",

                fire:
                    "화",

                火:
                    "화",

                토:
                    "토",

                earth:
                    "토",

                土:
                    "토",

                금:
                    "금",

                metal:
                    "금",

                金:
                    "금",

                수:
                    "수",

                water:
                    "수",

                水:
                    "수"
            };

            return map[
                text
            ]
            || "-";
        }

        function getElementClass(
            element
        ) {

            const normalized =
                normalizeElementName(
                    element
                );

            const map = {
                목:
                    "element-wood",

                화:
                    "element-fire",

                토:
                    "element-earth",

                금:
                    "element-metal",

                수:
                    "element-water"
            };

            return map[
                normalized
            ]
            || "element-earth";
        }

        function normalizeElementItems(
            elementBalance
        ) {

            const source =
                Array.isArray(
                    elementBalance
                        ?.items
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

            const itemMap =
                new Map();

            source.forEach(
                function (item) {

                    itemMap.set(
                        normalizeElementName(
                            item.name
                        ),
                        item
                    );
                }
            );

            return order.map(
                function (name) {

                    const item =
                        itemMap.get(
                            name
                        )
                        || {};

                    return {
                        name:
                            name,

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
                }
            );
        }

        function explainElementStrongest(
            element
        ) {

            const map = {
                목:
                    "새로운 것을 배우고 성장시키는 힘이 강점입니다.",

                화:
                    "표현력과 추진력, 빠른 행동력이 강점입니다.",

                토:
                    "책임감과 안정감, 꾸준히 유지하는 힘이 강점입니다.",

                금:
                    "기준이 분명하고 판단과 정리를 잘하는 힘이 강점입니다.",

                수:
                    "상황을 읽는 감각과 유연하게 적응하는 힘이 강점입니다."
            };

            return map[
                element
            ]
            || "강한 기운은 장점이지만 지나치게 사용되지 않도록 균형을 보는 것이 좋습니다.";
        }

        function explainElementWeakest(
            element
        ) {

            const map = {
                목:
                    "작은 목표부터 시작하고 배움의 흐름을 이어가는 연습이 도움이 됩니다.",

                화:
                    "생각과 감정을 말과 행동으로 표현하는 연습이 도움이 됩니다.",

                토:
                    "수면과 식사, 일정 같은 생활 리듬을 안정시키는 것이 도움이 됩니다.",

                금:
                    "기준과 우선순위를 정하고 불필요한 일을 정리하는 습관이 도움이 됩니다.",

                수:
                    "상황에 맞춰 생각과 계획을 유연하게 조정하는 연습이 도움이 됩니다."
            };

            return map[
                element
            ]
            || "부족한 기운은 생활 습관과 환경으로 보완할 수 있는 부분입니다.";
        }

        function createRelationText(
            relations
        ) {

            const parts =
                [];

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

            heavenly.forEach(
                function (item) {

                    parts.push(
                        valueOrDash(
                            item.category
                        )
                        + " · "
                        + valueOrDash(
                            item.members
                        )
                        + "\n"
                        + valueOrDash(
                            item.description
                        )
                    );
                }
            );

            earthly.forEach(
                function (item) {

                    parts.push(
                        valueOrDash(
                            item.category
                        )
                        + " · "
                        + valueOrDash(
                            item.members
                        )
                        + "\n"
                        + valueOrDash(
                            item.description
                        )
                    );
                }
            );

            return parts.length > 0
                ? parts.join(
                    "\n\n"
                )
                : "특별히 표시할 관계 정보가 많지 않습니다.";
        }

        function formatYearMonthValue(
            value
        ) {

            if (
                value === null
                || value === undefined
                || value === ""
            ) {
                return "-";
            }

            if (
                Array.isArray(
                    value
                )
                && value.length >= 2
            ) {
                return Number(
                    value[0]
                )
                + "년 "
                + Number(
                    value[1]
                )
                + "월";
            }

            const text =
                String(
                    value
                );

            const matched =
                text.match(
                    /^(\d{4})-(\d{1,2})/
                );

            if (
                matched
            ) {
                return Number(
                    matched[1]
                )
                + "년 "
                + Number(
                    matched[2]
                )
                + "월";
            }

            return text;
        }

        function formatBirthTitle(
            dateText
        ) {

            if (
                !dateText
                || dateText === "-"
            ) {
                return "-";
            }

            const parts =
                String(
                    dateText
                ).split(
                    "-"
                );

            if (
                parts.length !== 3
            ) {
                return dateText;
            }

            return Number(
                parts[0]
            )
            + "년 "
            + Number(
                parts[1]
            )
            + "월 "
            + Number(
                parts[2]
            )
            + "일";
        }

        function clampPercentage(
            value
        ) {

            const numeric =
                Number(
                    value
                    || 0
                );

            if (
                !Number.isFinite(
                    numeric
                )
            ) {
                return 0;
            }

            return Math.max(
                0,
                Math.min(
                    100,
                    Number(
                        numeric.toFixed(
                            1
                        )
                    )
                )
            );
        }

        function showToast(
            message
        ) {

            clearTimeout(
                state.toastTimer
            );

            toast.textContent =
                message;

            toast.classList.add(
                "show"
            );

            state.toastTimer =
                window.setTimeout(
                    function () {

                        toast.classList.remove(
                            "show"
                        );
                    },
                    2400
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

        function pad2(
            value
        ) {

            return String(
                value
            ).padStart(
                2,
                "0"
            );
        }

        function valueOrDash(
            value
        ) {

            if (
                value === null
                || value === undefined
                || value === ""
            ) {
                return "-";
            }

            return String(
                value
            );
        }

        function isNotBlank(
            value
        ) {

            return value !== null
                && value !== undefined
                && String(
                    value
                ).trim() !== "";
        }

        function escapeHtml(
            value
        ) {

            return String(
                value
                ?? ""
            )
                .replace(
                    /&/g,
                    "&amp;"
                )
                .replace(
                    /</g,
                    "&lt;"
                )
                .replace(
                    />/g,
                    "&gt;"
                )
                .replace(
                    /"/g,
                    "&quot;"
                )
                .replace(
                    /'/g,
                    "&#039;"
                );
        }
    }
);