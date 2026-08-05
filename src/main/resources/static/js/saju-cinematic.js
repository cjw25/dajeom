document.addEventListener(
    "DOMContentLoaded",
    function () {

        const MIN_ANALYSIS_TIME =
            3200;

        const STORAGE_KEYS = {
            form:
                "sajuCinematicForm",

            profileId:
                "sajuCinematicProfileId",

            analysisId:
                "sajuCinematicAnalysisId",

            result:
                "sajuCinematicAnalysisResult"
        };

        /*
         * 영상에서 사용할 장면 이미지 경로다.
         *
         * 파일이 없으면 자동으로
         * /img/saju-character.png 이미지가 대신 표시된다.
         */
        const IMAGE_PATHS = {
            fallback:
                "/img/saju-character.png",

            intro:
                "/img/saju-character.png",

            chart:
                "/img/saju-cinematic/chart.png",

            element:
                "/img/saju-cinematic/element.png",

            strength:
                "/img/saju-cinematic/strength.png",

            personality:
                "/img/saju-cinematic/personality.png",

            career:
                "/img/saju-cinematic/career.png",

            wealth:
                "/img/saju-cinematic/wealth.png",

            relationship:
                "/img/saju-cinematic/relationship.png",

            health:
                "/img/saju-cinematic/health.png",

            study:
                "/img/saju-cinematic/study.png",

            startup:
                "/img/saju-cinematic/startup.png",

            shinsal:
                "/img/saju-cinematic/shinsal.png",

            luck:
                "/img/saju-cinematic/luck.png",

            ending:
                "/img/saju-character.png"
        };

        const CATEGORY_IMAGE_MAP = {
            성격:
                IMAGE_PATHS.personality,

            기질:
                IMAGE_PATHS.personality,

            직업:
                IMAGE_PATHS.career,

            취업:
                IMAGE_PATHS.career,

            진로:
                IMAGE_PATHS.career,

            재물:
                IMAGE_PATHS.wealth,

            금전:
                IMAGE_PATHS.wealth,

            연애:
                IMAGE_PATHS.relationship,

            인연:
                IMAGE_PATHS.relationship,

            관계:
                IMAGE_PATHS.relationship,

            건강:
                IMAGE_PATHS.health,

            시험:
                IMAGE_PATHS.study,

            학업:
                IMAGE_PATHS.study,

            학습:
                IMAGE_PATHS.study,

            창업:
                IMAGE_PATHS.startup,

            사업:
                IMAGE_PATHS.startup
        };

        const state = {
            currentScreen:
                "input",

            form:
                createEmptyForm(),

            result:
                null,

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

        /* =====================================================
           공통 요소
        ===================================================== */

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

        /* =====================================================
           입력 요소
        ===================================================== */

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

        /* =====================================================
           확인 요소
        ===================================================== */

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

        /* =====================================================
           분석 요소
        ===================================================== */

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

        /* =====================================================
           영상 요소
        ===================================================== */

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

        /* =====================================================
           결과 요소
        ===================================================== */

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

        /* =====================================================
           초기화
        ===================================================== */

        function initialize() {

            clearCinematicStorage();

            state.form =
                createEmptyForm();

            state.result =
                null;

            resetInputForm();

            bindEvents();

            showScreen(
                "input"
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

        function clearCinematicStorage() {

            sessionStorage.removeItem(
                STORAGE_KEYS.form
            );

            sessionStorage.removeItem(
                STORAGE_KEYS.profileId
            );

            sessionStorage.removeItem(
                STORAGE_KEYS.analysisId
            );

            sessionStorage.removeItem(
                STORAGE_KEYS.result
            );
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

        /* =====================================================
           이벤트
        ===================================================== */

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
                function () {

                    stopCinematic();

                    window.location.href =
                        "/";
                }
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

                    stopCinematic();

                    clearCinematicStorage();

                    state.form =
                        createEmptyForm();

                    state.result =
                        null;

                    state.scenes =
                        [];

                    resetInputForm();

                    showScreen(
                        "input"
                    );
                }
            );

            goHomeButton.addEventListener(
                "click",
                function () {

                    stopCinematic();

                    window.location.href =
                        "/";
                }
            );
        }

        function handleBackButton() {

            if (
                state.currentScreen
                === "input"
            ) {
                window.location.href =
                    "/";

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
                stopCinematic();

                showScreen(
                    "review"
                );

                return;
            }

            if (
                state.currentScreen
                === "result"
            ) {
                window.location.href =
                    "/";
            }
        }

        /* =====================================================
           화면 전환
        ===================================================== */

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

        /* =====================================================
           입력 검증
        ===================================================== */

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
                name.length > 20
            ) {
                return setInputError(
                    "이름은 20자 이하로 입력해주세요."
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
                    !birthHour.value
                    || !birthMinute.value
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

            sessionStorage.setItem(
                STORAGE_KEYS.form,
                JSON.stringify(
                    state.form
                )
            );

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

        /* =====================================================
           확인 화면
        ===================================================== */

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

        /* =====================================================
           저장 및 분석 요청
        ===================================================== */

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

                const profileResponse =
                    await fetch(
                        "/api/saju/profiles",
                        {
                            method:
                                "POST",

                            headers: {
                                "Content-Type":
                                    "application/json"
                            },

                            body:
                                JSON.stringify(
                                    state.form
                                )
                        }
                    );

                const profileResult =
                    await safeJson(
                        profileResponse
                    );

                if (
                    !profileResponse.ok
                ) {
                    throw new Error(
                        profileResult.message
                        || "사주정보 저장에 실패했습니다."
                    );
                }

                const profileId =
                    Number(
                        profileResult.profileId
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

                sessionStorage.setItem(
                    STORAGE_KEYS.profileId,
                    String(
                        profileId
                    )
                );

                analysisMessage.textContent =
                    "사주 명식표와 오행을 계산하고 있습니다.";

                const analysisResponse =
                    await fetch(
                        "/api/saju/analyses",
                        {
                            method:
                                "POST",

                            headers: {
                                "Content-Type":
                                    "application/json"
                            },

                            body:
                                JSON.stringify({
                                    profileId:
                                        profileId
                                })
                        }
                    );

                const analysisResult =
                    await safeJson(
                        analysisResponse
                    );

                if (
                    !analysisResponse.ok
                ) {
                    throw new Error(
                        analysisResult.message
                        || "사주 분석에 실패했습니다."
                    );
                }

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

                state.result =
                    analysisResult;

                state.scenes =
                    createScenes(
                        analysisResult
                    );

                sessionStorage.setItem(
                    STORAGE_KEYS.result,
                    JSON.stringify(
                        analysisResult
                    )
                );

                if (
                    analysisResult.analysisId
                ) {
                    sessionStorage.setItem(
                        STORAGE_KEYS.analysisId,
                        String(
                            analysisResult.analysisId
                        )
                    );
                }

                renderFullResult(
                    analysisResult
                );

                analysisMessage.textContent =
                    "이미지 영상 해설 준비가 완료되었습니다.";

                await wait(
                    550
                );

                /*
                 * 분석이 끝난 뒤 결과로 바로 가지 않고
                 * 반드시 영상형 화면을 먼저 재생한다.
                 */
                startCinematic();

            } catch (error) {

                stopFakeProgress();

                console.error(
                    "천명해설 분석 오류",
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

        async function safeJson(
            response
        ) {

            const text =
                await response.text();

            if (
                !text
            ) {
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
           분석 진행률
        ===================================================== */

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
                            next +=
                                4;

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
                            next +=
                                3;

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
                            next +=
                                2;

                            setProgress(
                                next,
                                "구조 분석",
                                "오행, 십성, 신살과 대운을 분석하고 있습니다."
                            );

                            return;
                        }

                        if (
                            next < 94
                        ) {
                            next +=
                                1;

                            setProgress(
                                next,
                                "영상 구성",
                                "캐릭터 이미지와 쉬운 해설을 장면으로 구성하고 있습니다."
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
                                ? "이미지와 자막을 정리하고 있습니다."
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

        /* =====================================================
           영상 장면 생성
        ===================================================== */

        function createScenes(
            result
        ) {

            const scenes =
                [];

            const name =
                result.name
                || state.form.name
                || "회원";

            scenes.push({
                kicker:
                    "天命解說",

                title:
                    "사주의 문을 엽니다",

                header:
                    "영상 해설 시작",

                text:
                    name
                    + "님, 지금부터 캐릭터 이미지와 사주 명식표를 함께 보면서 어려운 용어를 생활에서 이해하기 쉬운 말로 풀어드리겠습니다.",

                duration:
                    6500,

                camera:
                    "camera-wide",

                backgroundImage:
                    IMAGE_PATHS.intro,

                foregroundImage:
                    null,

                visualType:
                    null
            });

            scenes.push({
                kicker:
                    "四柱命式",

                title:
                    "태어난 순간의 사주 명식표",

                header:
                    "사주 원국",

                text:
                    createChartNarration(
                        result
                    ),

                duration:
                    8500,

                camera:
                    "camera-soft",

                backgroundImage:
                    IMAGE_PATHS.chart,

                foregroundImage:
                    null,

                visualType:
                    "CHART"
            });

            scenes.push({
                kicker:
                    "五行分布",

                title:
                    "오행의 분포를 살펴봅니다",

                header:
                    "오행 분석",

                text:
                    createElementNarration(
                        result.elementBalance
                    ),

                duration:
                    8200,

                camera:
                    "camera-left",

                backgroundImage:
                    IMAGE_PATHS.element,

                foregroundImage:
                    null,

                visualType:
                    "ELEMENT"
            });

            scenes.push({
                kicker:
                    "日干强弱",

                title:
                    "중심 기운과 보완 방향",

                header:
                    "신강·신약과 십성",

                text:
                    createStrengthNarration(
                        result
                    ),

                duration:
                    8200,

                camera:
                    "camera-right",

                backgroundImage:
                    IMAGE_PATHS.strength,

                foregroundImage:
                    null,

                visualType:
                    "STRENGTH"
            });

            const interpretations =
                Array.isArray(
                    result.interpretations
                )
                    ? result.interpretations
                    : [];

            interpretations.forEach(
                function (
                    item,
                    index
                ) {

                    const category =
                        item.category
                        || "분야별 해설";

                    const sceneImage =
                        resolveCategoryImage(
                            category,
                            item.title
                        );

                    scenes.push({
                        kicker:
                            category,

                        title:
                            item.title
                            || category,

                        header:
                            category
                            + " 해설",

                        text:
                            simplifyInterpretationText(
                                item.content
                                || "해석 내용이 없습니다."
                            ),

                        duration:
                            calculateSceneDuration(
                                item.content
                            ),

                        camera:
                            index % 2 === 0
                                ? "camera-left"
                                : "camera-right",

                        backgroundImage:
                            sceneImage,

                        foregroundImage:
                            sceneImage,

                        foregroundCaption:
                            category
                            + "을 상징하는 이미지",

                        visualType:
                            null
                    });
                }
            );

            const shinsal =
                Array.isArray(
                    result.shinsal
                )
                    ? result.shinsal
                    : [];

            if (
                shinsal.length > 0
            ) {
                scenes.push({
                    kicker:
                        "神煞補助",

                    title:
                        "신살은 무서운 예언이 아닙니다",

                    header:
                        "신살 쉬운 설명",

                    text:
                        createShinsalNarration(
                            shinsal
                        ),

                    duration:
                        8500,

                    camera:
                        "camera-close",

                    backgroundImage:
                        IMAGE_PATHS.shinsal,

                    foregroundImage:
                        null,

                    visualType:
                        "SHINSAL"
                });
            }

            if (
                result.luck
            ) {
                scenes.push({
                    kicker:
                        "大運歲運",

                    title:
                        "큰 운의 흐름을 살펴봅니다",

                    header:
                        "대운과 세운",

                    text:
                        createLuckNarration(
                            result.luck
                        ),

                    duration:
                        8500,

                    camera:
                        "camera-medium",

                    backgroundImage:
                        IMAGE_PATHS.luck,

                    foregroundImage:
                        null,

                    visualType:
                        "LUCK"
                });
            }

            scenes.push({
                kicker:
                    "綜合解說",

                title:
                    "전체 흐름을 정리합니다",

                header:
                    "최종 정리",

                text:
                    simplifyInterpretationText(
                        result.summary
                        || createEasySummary(
                            result
                        )
                    ),

                duration:
                    8800,

                camera:
                    "camera-close",

                backgroundImage:
                    IMAGE_PATHS.ending,

                foregroundImage:
                    IMAGE_PATHS.ending,

                foregroundCaption:
                    "천명해설 마무리 장면",

                visualType:
                    null
            });

            return scenes;
        }

        function resolveCategoryImage(
            category,
            title
        ) {

            const searchable =
                String(
                    category
                    || ""
                )
                + " "
                + String(
                    title
                    || ""
                );

            for (
                const [
                    keyword,
                    imagePath
                ]
                of Object.entries(
                    CATEGORY_IMAGE_MAP
                )
            ) {
                if (
                    searchable.includes(
                        keyword
                    )
                ) {
                    return imagePath;
                }
            }

            return IMAGE_PATHS.fallback;
        }

        function calculateSceneDuration(
            text
        ) {

            const length =
                String(
                    text
                    || ""
                )
                    .length;

            return Math.max(
                6800,
                Math.min(
                    9800,
                    5200
                    + length
                    * 25
                )
            );
        }

        /* =====================================================
           영상 재생
        ===================================================== */

        function startCinematic() {

            stopCinematic();

            if (
                !state.result
            ) {
                showToast(
                    "분석 결과가 없습니다."
                );

                showScreen(
                    "review"
                );

                return;
            }

            state.scenes =
                createScenes(
                    state.result
                );

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
                350
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
                || IMAGE_PATHS.fallback
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
                scene.foregroundCaption
                || "장면 이미지";

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

            switch (
                scene.visualType
            ) {
                case "CHART":

                    sceneVisualContent.innerHTML =
                        createVideoChartHtml(
                            state.result
                        );

                    break;

                case "ELEMENT":

                    sceneVisualContent.innerHTML =
                        createVideoElementHtml(
                            state.result.elementBalance
                        );

                    break;

                case "STRENGTH":

                    sceneVisualContent.innerHTML =
                        createVideoStrengthHtml(
                            state.result
                        );

                    break;

                case "SHINSAL":

                    sceneVisualContent.innerHTML =
                        createVideoShinsalHtml(
                            state.result.shinsal
                        );

                    break;

                case "LUCK":

                    sceneVisualContent.innerHTML =
                        createVideoLuckHtml(
                            state.result.luck
                        );

                    break;

                default:

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
                320
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

                        index +=
                            1;

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

        /* =====================================================
           영상 안 명식표
        ===================================================== */

        function createVideoChartHtml(
            result
        ) {

            const ordered =
                getOrderedPillars(
                    result.pillars
                );

            return `
                <div class="video-chart-title">

                    <strong>
                        ${escapeHtml(
                            result.name
                            || state.form.name
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

                    <div class="video-chart-label">
                        시
                    </div>

                    <div class="video-chart-label">
                        일
                    </div>

                    <div class="video-chart-label">
                        월
                    </div>

                    <div class="video-chart-label">
                        년
                    </div>

                </div>

                <div class="video-chart-grid">

                    ${ordered
                        .map(
                            function (pillar) {

                                return `
                                    <div class="video-chart-info">
                                        ${escapeHtml(
                                            withEasyMeaning(
                                                pillar.stemTenGod,
                                                tenGodEasyLabel(
                                                    pillar.stemTenGod
                                                )
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
                                            withEasyMeaning(
                                                pillar.branchTenGod,
                                                tenGodEasyLabel(
                                                    pillar.branchTenGod
                                                )
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

                                return `
                                    <div class="video-chart-info">
                                        ${escapeHtml(
                                            withEasyMeaning(
                                                pillar.twelveStage,
                                                twelveStageEasyLabel(
                                                    pillar.twelveStage
                                                )
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
                <div class="video-pillar-card ${escapeHtml(className)}">

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

        function createVideoElementHtml(
            elementBalance
        ) {

            const items =
                normalizeElementItems(
                    elementBalance
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

        function createVideoStrengthHtml(
            result
        ) {

            const strength =
                result.strength
                || {};

            const dominantTenGod =
                result.tenGodBalance
                    ?.dominant
                || "-";

            return `
                <div class="video-mini-list">

                    ${createVideoMiniItem(
                        "중심 기운",
                        strength.classification
                        || "분석값 없음"
                    )}

                    ${createVideoMiniItem(
                        "도움 되는 기운",
                        strength.usefulGod
                        || "분석값 없음"
                    )}

                    ${createVideoMiniItem(
                        "잘 활용하면 좋은 기운",
                        strength.favorableGod
                        || "분석값 없음"
                    )}

                    ${createVideoMiniItem(
                        "두드러진 십성",
                        dominantTenGod
                        + " · "
                        + tenGodEasyLabel(
                            dominantTenGod
                        )
                    )}

                </div>
            `;
        }

        function createVideoShinsalHtml(
            shinsal
        ) {

            const items =
                Array.isArray(
                    shinsal
                )
                    ? shinsal.slice(
                        0,
                        4
                    )
                    : [];

            return `
                <div class="video-mini-list">

                    ${items
                        .map(
                            function (item) {

                                return createVideoMiniItem(
                                    item.name
                                    || "신살",

                                    explainShinsalEasy(
                                        item.name,
                                        item.description
                                    )
                                );
                            }
                        )
                        .join("")}

                </div>
            `;
        }

        function createVideoLuckHtml(
            luck
        ) {

            if (
                !luck
            ) {
                return createVideoMiniItem(
                    "대운",
                    "대운 정보가 없습니다."
                );
            }

            return `
                <div class="video-mini-list">

                    ${createVideoMiniItem(
                        "운행 방향",
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
                        "현재 대운",
                        valueOrDash(
                            luck.currentDecade
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

        /* =====================================================
           전체 결과 렌더링
        ===================================================== */

        function renderFullResult(
            result
        ) {

            const name =
                result.name
                || state.form.name
                || "회원";

            resultTitle.textContent =
                name
                + "님의 사주";

            resultSummary.textContent =
                simplifyInterpretationText(
                    result.summary
                    || createEasySummary(
                        result
                    )
                );

            chartPersonTitle.textContent =
                name
                + "님의 사주 명식표";

            chartDateTitle.textContent =
                formatBirthTitle(
                    state.form.birthDate
                );

            resultChart.innerHTML =
                createResultChartHtml(
                    result
                );

            renderResultElements(
                result
            );

            renderResultEasyInterpretations(
                result
            );

            renderResultShinsal(
                result
            );

            renderResultLuck(
                result
            );

            renderResultAdvanced(
                result
            );

            resultDisclaimer.textContent =
                result.disclaimer
                || "사주 해설은 가능성과 경향을 설명하는 참고용 콘텐츠입니다. 중요한 결정은 현실적인 정보와 함께 판단해야 합니다.";
        }

        function createResultChartHtml(
            result
        ) {

            const ordered =
                getOrderedPillars(
                    result.pillars
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
                                                withEasyMeaning(
                                                    pillar.stemTenGod,
                                                    tenGodEasyLabel(
                                                        pillar.stemTenGod
                                                    )
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
                                                withEasyMeaning(
                                                    pillar.branchTenGod,
                                                    tenGodEasyLabel(
                                                        pillar.branchTenGod
                                                    )
                                                )
                                            )}
                                        </div>
                                    `;
                                }
                            )
                            .join("")}

                    </div>

                    <div class="result-chart-divider">

                        <div class="result-chart-row result-chart-info">

                            ${ordered
                                .map(
                                    function (pillar) {

                                        return `
                                            <div>
                                                ${escapeHtml(
                                                    withEasyMeaning(
                                                        pillar.twelveStage,
                                                        twelveStageEasyLabel(
                                                            pillar.twelveStage
                                                        )
                                                    )
                                                )}
                                            </div>
                                        `;
                                    }
                                )
                                .join("")}

                        </div>

                    </div>

                    <div class="result-chart-divider">

                        <div class="result-chart-row result-chart-info">

                            ${ordered
                                .map(
                                    function (pillar) {

                                        return `
                                            <div>
                                                ${escapeHtml(
                                                    formatListText(
                                                        findPillarShinsalNames(
                                                            result.shinsal,
                                                            pillar.type
                                                        )
                                                    )
                                                )}
                                            </div>
                                        `;
                                    }
                                )
                                .join("")}

                        </div>

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
                <div class="result-pillar-card ${escapeHtml(className)}">

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
            result
        ) {

            const elementBalance =
                result.elementBalance
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
                        가장 강한 기운 · ${escapeHtml(dominant)}
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
                        보완이 필요한 기운 · ${escapeHtml(weakest)}
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
            result
        ) {

            const items =
                [];

            items.push({
                title:
                    "성향 핵심",

                content:
                    createStrengthEasyExplanation(
                        result
                    )
            });

            const interpretations =
                Array.isArray(
                    result.interpretations
                )
                    ? result.interpretations
                    : [];

            interpretations.forEach(
                function (item) {

                    items.push({
                        title:
                            (
                                item.category
                                    ? item.category
                                    + " · "
                                    : ""
                            )
                            + (
                                item.title
                                || "사주 해설"
                            ),

                        content:
                            simplifyInterpretationText(
                                item.content
                                || "해석 내용이 없습니다."
                            )
                    });
                }
            );

            resultEasyList.innerHTML =
                items
                    .map(
                        function (item) {

                            return `
                                <article class="easy-card">

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

        function renderResultShinsal(
            result
        ) {

            const shinsal =
                Array.isArray(
                    result.shinsal
                )
                    ? result.shinsal
                    : [];

            if (
                shinsal.length === 0
            ) {
                resultShinsalList.innerHTML = `
                    <article class="easy-card">

                        <strong>
                            신살 요약
                        </strong>

                        <p>
                            현재 표시할 주요 신살이 많지 않습니다. 신살은 전체 사주를 보조적으로 설명하는 참고 자료입니다.
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
                                            explainShinsalEasy(
                                                item.name,
                                                item.description
                                            )
                                        )}
                                    </p>

                                </article>
                            `;
                        }
                    )
                    .join("");
        }

        function renderResultLuck(
            result
        ) {

            const luck =
                result.luck;

            if (
                !luck
            ) {
                resultLuckList.innerHTML = `
                    <article class="easy-card">

                        <strong>
                            대운
                        </strong>

                        <p>
                            현재 대운 정보가 없습니다.
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
                        대운을 쉽게 이해하면
                    </strong>

                    <p>
                        대운은 좋은 시기와 나쁜 시기를 단정하는 표가 아니라,
                        인생에서 어떤 주제가 강해지는지를 보는 큰 흐름입니다.
                        실제 선택에서는 경력, 자금, 건강과 주변 환경을 함께 확인해야 합니다.
                    </p>

                </article>
            `;
        }

        function renderResultAdvanced(
            result
        ) {

            const cards =
                [];

            if (
                result.calendar
            ) {
                cards.push({
                    title:
                        "달력 정보",

                    content:
                        "입력 달력: "
                        + valueOrDash(
                            result.calendar.inputCalendarType
                        )
                        + "\n양력: "
                        + valueOrDash(
                            result.calendar.solarDate
                        )
                        + "\n음력: "
                        + valueOrDash(
                            result.calendar.lunarDate
                        )
                        + "\n출생시간: "
                        + (
                            result.calendar.birthTimeUnknown
                                ? "시간 모름"
                                : valueOrDash(
                                    result.calendar.birthTime
                                )
                        )
                        + "\n계산 기준: "
                        + valueOrDash(
                            result.calendar.calculationStandard
                        )
                });
            }

            if (
                result.strength
            ) {
                cards.push({
                    title:
                        "신강·신약과 보완 기운",

                    content:
                        "분류: "
                        + valueOrDash(
                            result.strength.classification
                        )
                        + "\n점수: "
                        + valueOrDash(
                            result.strength.score
                        )
                        + "\n용신: "
                        + valueOrDash(
                            result.strength.usefulGod
                        )
                        + "\n희신: "
                        + valueOrDash(
                            result.strength.favorableGod
                        )
                        + "\n기신: "
                        + valueOrDash(
                            result.strength.unfavorableGod
                        )
                        + "\n설명: "
                        + valueOrDash(
                            result.strength.note
                        )
                });
            }

            if (
                result.structure
            ) {
                cards.push({
                    title:
                        "태원·태식·명궁·신궁",

                    content:
                        "태원: "
                        + valueOrDash(
                            result.structure.fetalOriginKorean
                            || result.structure.fetalOrigin
                        )
                        + "\n태식: "
                        + valueOrDash(
                            result.structure.fetalBreathKorean
                            || result.structure.fetalBreath
                        )
                        + "\n명궁: "
                        + valueOrDash(
                            result.structure.lifePalaceKorean
                            || result.structure.lifePalace
                        )
                        + "\n신궁: "
                        + valueOrDash(
                            result.structure.bodyPalaceKorean
                            || result.structure.bodyPalace
                        )
                        + "\n설명: "
                        + valueOrDash(
                            result.structure.note
                        )
                });
            }

            if (
                result.relations
            ) {
                cards.push({
                    title:
                        "합충형파해",

                    content:
                        createRelationText(
                            result.relations
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
                                추가 전문 정보가 없습니다.
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

        /* =====================================================
           명식표 정규화
        ===================================================== */

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
                        || extractStemFromGanZhi(
                            pillar.ganZhi
                        );

                    const branchCode =
                        pillar.branch
                        || extractBranchFromGanZhi(
                            pillar.ganZhi
                        );

                    const stemMeta =
                        getStemMeta(
                            stemCode
                        );

                    const branchMeta =
                        getBranchMeta(
                            branchCode
                        );

                    const hiddenStems =
                        Array.isArray(
                            pillar.hiddenStems
                        )
                            ? pillar.hiddenStems
                            : [];

                    const mainHiddenStem =
                        hiddenStems[0]
                        || null;

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

                        /*
                         * 지지 십성은 대표 지장간의 십성을 사용한다.
                         */
                        branchTenGod:
                            known
                                ? valueOrDash(
                                    mainHiddenStem
                                        ?.tenGod
                                )
                                : "-",

                        twelveStage:
                            known
                                ? valueOrDash(
                                    pillar.twelveStage
                                )
                                : "-",

                        hiddenStems:
                            hiddenStems,

                        naYin:
                            valueOrDash(
                                pillar.naYin
                            ),

                        xun:
                            valueOrDash(
                                pillar.xun
                            ),

                        xunKong:
                            valueOrDash(
                                pillar.xunKong
                            ),

                        note:
                            valueOrDash(
                                pillar.note
                            )
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

                branchTenGod:
                    "-",

                twelveStage:
                    "-",

                hiddenStems:
                    [],

                naYin:
                    "-",

                xun:
                    "-",

                xunKong:
                    "-",

                note:
                    "-"
            };
        }

        function extractStemFromGanZhi(
            ganZhi
        ) {

            if (
                !ganZhi
                || typeof ganZhi
                !== "string"
            ) {
                return "";
            }

            return ganZhi.charAt(
                0
            );
        }

        function extractBranchFromGanZhi(
            ganZhi
        ) {

            if (
                !ganZhi
                || typeof ganZhi
                !== "string"
            ) {
                return "";
            }

            return ganZhi.charAt(
                1
            );
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

            if (
                normalizedYinYang === "-"
                && normalizedElement === "-"
            ) {
                return "-";
            }

            return (
                normalizedYinYang === "-"
                    ? ""
                    : normalizedYinYang
            )
            + (
                normalizedElement === "-"
                    ? ""
                    : normalizedElement
            );
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

        function findPillarShinsalNames(
            shinsal,
            pillarType
        ) {

            if (
                !Array.isArray(
                    shinsal
                )
            ) {
                return [];
            }

            const locationNames = {
                HOUR: [
                    "시주",
                    "시지",
                    "시간"
                ],

                DAY: [
                    "일주",
                    "일지",
                    "일간"
                ],

                MONTH: [
                    "월주",
                    "월지",
                    "월간"
                ],

                YEAR: [
                    "년주",
                    "연주",
                    "년지",
                    "연지",
                    "년간",
                    "연간"
                ]
            };

            const keywords =
                locationNames[
                    pillarType
                ]
                || [];

            return shinsal
                .filter(
                    function (item) {

                        const location =
                            String(
                                item
                                    ?.location
                                || ""
                            );

                        return keywords.some(
                            function (keyword) {

                                return location.includes(
                                    keyword
                                );
                            }
                        );
                    }
                )
                .map(
                    function (item) {

                        return item.name
                        || "신살";
                    }
                );
        }

        /* =====================================================
           쉬운 해설 문장
        ===================================================== */

        function createChartNarration(
            result
        ) {

            const ordered =
                getOrderedPillars(
                    result.pillars
                );

            const known =
                ordered
                    .filter(
                        function (pillar) {

                            return pillar.known;
                        }
                    )
                    .map(
                        function (pillar) {

                            return pillar.stemKorean
                                + pillar.branchKorean;
                        }
                    )
                    .join(", ");

            if (
                !known
            ) {
                return "사주 명식표의 네 기둥을 확인하고 있습니다.";
            }

            return "화면의 네 기둥은 태어난 해, 달, 날과 시간을 나타냅니다. "
                + "가운데 일주는 나 자신을 보는 중심이며, 현재 명식은 "
                + known
                + "의 흐름으로 계산되었습니다.";
        }

        function createElementNarration(
            elementBalance
        ) {

            const dominant =
                normalizeElementName(
                    elementBalance
                        ?.dominant
                );

            const weakest =
                normalizeElementName(
                    elementBalance
                        ?.weakest
                );

            return "오행은 목, 화, 토, 금, 수의 다섯 기운을 뜻합니다. "
                + dominant
                + " 기운이 가장 두드러져 "
                + explainElementStrongest(
                    dominant
                )
                + " 반대로 "
                + weakest
                + " 기운은 상대적으로 적어 "
                + explainElementWeakest(
                    weakest
                );
        }

        function createStrengthNarration(
            result
        ) {

            const strength =
                result.strength
                || {};

            const dominantTenGod =
                result.tenGodBalance
                    ?.dominant
                || "확인되지 않음";

            return "중심 기운은 "
                + valueOrDash(
                    strength.classification
                )
                + "으로 분석됩니다. 두드러진 십성은 "
                + dominantTenGod
                + "이며, 쉽게 말하면 "
                + tenGodEasyMeaning(
                    dominantTenGod
                )
                + " 도움 되는 기운은 "
                + valueOrDash(
                    strength.usefulGod
                )
                + "입니다.";
        }

        function createShinsalNarration(
            shinsal
        ) {

            return shinsal
                .slice(
                    0,
                    4
                )
                .map(
                    function (item) {

                        return (
                            item.name
                            || "신살"
                        )
                        + "은 "
                        + explainShinsalEasy(
                            item.name,
                            item.description
                        );
                    }
                )
                .join(" ");
        }

        function createLuckNarration(
            luck
        ) {

            return "대운은 인생에서 어떤 주제가 강해지는지를 보는 큰 흐름입니다. "
                + "현재 대운은 "
                + valueOrDash(
                    luck.currentDecade
                )
                + "이며 운행 방향은 "
                + valueOrDash(
                    luck.direction
                )
                + "입니다. 좋고 나쁨만 단정하기보다 현실의 경력과 자금, 건강을 함께 살펴봐야 합니다.";
        }

        function createStrengthEasyExplanation(
            result
        ) {

            const strength =
                result.strength
                || {};

            const classification =
                String(
                    strength.classification
                    || ""
                );

            const usefulGod =
                valueOrDash(
                    strength.usefulGod
                );

            const favorableGod =
                valueOrDash(
                    strength.favorableGod
                );

            if (
                classification.includes(
                    "신강"
                )
            ) {
                return "자기 중심과 버티는 힘이 비교적 강한 편입니다. "
                    + "주도성이 장점이지만 혼자 모든 일을 감당하거나 자신의 방식만 고집하지 않도록 균형을 잡는 것이 중요합니다. "
                    + "도움 되는 기운은 "
                    + usefulGod
                    + ", 잘 활용하면 좋은 기운은 "
                    + favorableGod
                    + "입니다.";
            }

            if (
                classification.includes(
                    "신약"
                )
            ) {
                return "주변 상황을 세밀하게 느끼고 배려하는 힘이 있지만 환경의 영향을 많이 받아 쉽게 지칠 수 있습니다. "
                    + "혼자 버티기보다 사람과 환경의 도움을 잘 활용하는 것이 중요합니다. "
                    + "보완에 도움 되는 기운은 "
                    + usefulGod
                    + ", 잘 활용하면 좋은 기운은 "
                    + favorableGod
                    + "입니다.";
            }

            if (
                classification.includes(
                    "중화"
                )
            ) {
                return "한쪽으로 크게 치우치지 않아 상황에 맞춰 균형을 잡는 힘이 있습니다. "
                    + "운과 환경에 따라 장점이 다르게 나타날 수 있으며 도움 되는 기운은 "
                    + usefulGod
                    + "입니다.";
            }

            return "사주의 중심 기운은 계절과 전체 구조를 함께 살펴봐야 합니다. "
                + "현재 판정은 참고값으로 이해하고 실제 생활환경과 함께 보는 것이 좋습니다.";
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
            || "가장 강한 기운은 장점으로 활용할 수 있지만 한쪽으로 지나치게 쓰이지 않도록 균형을 함께 보는 것이 좋습니다.";
        }

        function explainElementWeakest(
            element
        ) {

            const map = {
                목:
                    "작은 목표부터 바로 시작하고 배움의 흐름을 꾸준히 이어가는 연습이 도움이 됩니다.",

                화:
                    "생각과 감정을 말과 행동으로 조금 더 분명하게 표현하는 연습이 도움이 됩니다.",

                토:
                    "수면, 식사와 일정처럼 반복되는 생활 리듬을 안정시키는 것이 도움이 됩니다.",

                금:
                    "기준과 우선순위를 미리 정하고 불필요한 일을 정리하는 습관이 도움이 됩니다.",

                수:
                    "한 가지 방식만 고집하지 않고 상황에 맞춰 생각과 계획을 유연하게 조정하는 연습이 도움이 됩니다."
            };

            return map[
                element
            ]
            || "부족한 기운은 결함이 아니라 생활 습관과 환경을 통해 의식적으로 보완할 수 있는 부분입니다.";
        }

        function explainShinsalEasy(
            name,
            description
        ) {

            const normalized =
                String(
                    name
                    || ""
                );

            if (
                normalized.includes(
                    "백호"
                )
            ) {
                return "이름이 강해서 무섭게 느껴질 수 있지만 사고가 반드시 생긴다는 뜻은 아닙니다. "
                    + "위기 상황에서 판단이 빠르고 필요할 때 강하게 대응하는 성향으로 이해하는 것이 좋습니다.";
            }

            if (
                normalized.includes(
                    "역마"
                )
            ) {
                return "이동과 변화의 흐름을 뜻합니다. "
                    + "여행, 이직, 환경 변화와 새로운 분야에 도전할 때 활동성이 살아날 수 있습니다.";
            }

            if (
                normalized.includes(
                    "화개"
                )
            ) {
                return "혼자 깊이 생각하고 한 분야를 오래 파고드는 성향입니다. "
                    + "예술, 연구, 상담, 철학과 전문 기술 분야에서 장점으로 나타날 수 있습니다.";
            }

            if (
                normalized.includes(
                    "도화"
                )
            ) {
                return "사람의 시선을 끌거나 매력이 드러나는 흐름입니다. "
                    + "연애뿐 아니라 대인관계와 표현 활동에서도 존재감으로 나타날 수 있습니다.";
            }

            if (
                normalized.includes(
                    "장성"
                )
            ) {
                return "책임을 맡고 사람이나 일을 이끄는 주도성과 리더십을 보조적으로 설명합니다.";
            }

            if (
                normalized.includes(
                    "천을귀인"
                )
            ) {
                return "어려운 상황에서 도움을 받거나 해결의 실마리를 만나는 흐름으로 설명합니다.";
            }

            if (
                normalized.includes(
                    "문창"
                )
            ) {
                return "공부, 문서, 글쓰기, 기획과 표현 능력을 보조하는 흐름입니다.";
            }

            if (
                normalized.includes(
                    "괴강"
                )
            ) {
                return "강한 자존감과 독립성, 결단력이 나타날 수 있습니다. "
                    + "자신의 기준을 유지하되 타인과의 완급 조절이 중요합니다.";
            }

            if (
                normalized.includes(
                    "양인"
                )
            ) {
                return "결단력과 추진력이 강한 흐름입니다. "
                    + "감정이나 행동이 너무 급해지지 않도록 조절하면 장점으로 활용할 수 있습니다.";
            }

            if (
                normalized.includes(
                    "귀문"
                )
            ) {
                return "감각과 집중력이 예민할 수 있다는 의미입니다. "
                    + "생각과 긴장이 지나치게 쌓이지 않도록 휴식이 중요합니다.";
            }

            if (
                description
                && String(
                    description
                ).trim()
            ) {
                return description
                    + " 다만 이 요소 하나만으로 특정 사건이나 결과를 단정하지 않습니다.";
            }

            return "전체 사주 흐름을 보조적으로 설명하는 요소입니다. "
                + "특정 사건이 반드시 생긴다는 의미로 받아들이지 않는 것이 좋습니다.";
        }

        function simplifyInterpretationText(
            text
        ) {

            if (
                !text
            ) {
                return "해석 내용이 없습니다.";
            }

            return String(
                text
            )
                .replace(
                    /용신/g,
                    "균형을 잡는 데 도움 되는 기운"
                )
                .replace(
                    /희신/g,
                    "잘 활용하면 좋은 기운"
                )
                .replace(
                    /기신/g,
                    "지나치면 부담이 될 수 있는 기운"
                )
                .replace(
                    /신약/g,
                    "주변 영향을 섬세하게 느끼는 편"
                )
                .replace(
                    /신강/g,
                    "자기 중심과 버티는 힘이 있는 편"
                )
                .replace(
                    /백호살/g,
                    "위기 대응력이 강하게 나타나는 백호살"
                )
                .replace(
                    /역마살/g,
                    "이동과 변화의 흐름을 뜻하는 역마살"
                )
                .replace(
                    /화개살/g,
                    "깊이 탐구하는 성향을 뜻하는 화개살"
                );
        }

        function tenGodEasyLabel(
            tenGod
        ) {

            const map = {
                일원:
                    "나 자신",

                비견:
                    "독립심",

                겁재:
                    "경쟁·추진",

                식신:
                    "생산·표현",

                상관:
                    "아이디어·표현",

                편재:
                    "기회형 재물",

                정재:
                    "안정형 재물",

                편관:
                    "압박·책임",

                정관:
                    "규칙·책임",

                편인:
                    "직관·탐구",

                정인:
                    "배움·보호"
            };

            return map[
                tenGod
            ]
            || "";
        }

        function tenGodEasyMeaning(
            tenGod
        ) {

            const map = {
                비견:
                    "자기주도성과 동료의식이 강하게 나타날 수 있습니다.",

                겁재:
                    "경쟁심과 빠른 추진력이 나타날 수 있습니다.",

                식신:
                    "기술과 결과물을 꾸준히 만들어내는 힘이 있습니다.",

                상관:
                    "아이디어와 표현력, 기존 방식을 바꾸는 힘이 있습니다.",

                편재:
                    "사람과 기회를 통해 돈과 자원을 움직이는 성향입니다.",

                정재:
                    "안정적인 수입과 계획적인 관리를 중요하게 보는 성향입니다.",

                편관:
                    "압박 속에서 책임지고 대응하는 힘이 있습니다.",

                정관:
                    "규칙과 책임, 신뢰를 중요하게 보는 성향입니다.",

                편인:
                    "직관과 독창적인 탐구 성향이 있습니다.",

                정인:
                    "배움과 문서, 자격을 통해 안정적으로 성장하는 힘이 있습니다."
            };

            return map[
                tenGod
            ]
            || "사람과 일, 돈과 표현 방식을 설명하는 보조 요소입니다.";
        }

        function twelveStageEasyLabel(
            stage
        ) {

            const map = {
                장생:
                    "성장 시작",

                목욕:
                    "경험·표현",

                관대:
                    "자신감 형성",

                건록:
                    "자립·안정",

                제왕:
                    "강한 추진",

                쇠:
                    "정리·관리",

                병:
                    "속도 조절",

                사:
                    "전환·마무리",

                묘:
                    "저장·내면",

                절:
                    "새 전환 준비",

                태:
                    "가능성 형성",

                양:
                    "보호·성장"
            };

            return map[
                stage
            ]
            || "";
        }

        function createEasySummary(
            result
        ) {

            const name =
                result.name
                || state.form.name
                || "회원";

            const dominant =
                normalizeElementName(
                    result.elementBalance
                        ?.dominant
                );

            const weakest =
                normalizeElementName(
                    result.elementBalance
                        ?.weakest
                );

            return name
                + "님의 사주는 "
                + dominant
                + " 기운의 장점이 비교적 잘 드러나며, "
                + weakest
                + " 기운과 관련된 생활 습관을 보완하면 전체 균형을 잡는 데 도움이 됩니다.";
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

        /* =====================================================
           공통 유틸
        ===================================================== */

        function withEasyMeaning(
            primary,
            secondary
        ) {

            const first =
                cleanDash(
                    primary
                );

            const second =
                cleanDash(
                    secondary
                );

            if (
                first !== "-"
                && second !== "-"
                && second !== ""
            ) {
                return first
                    + "\n("
                    + second
                    + ")";
            }

            if (
                first !== "-"
            ) {
                return first;
            }

            if (
                second !== "-"
            ) {
                return second;
            }

            return "-";
        }

        function cleanDash(
            value
        ) {

            if (
                value === null
                || value === undefined
                || value === ""
                || value === "null"
            ) {
                return "-";
            }

            return String(
                value
            );
        }

        function formatListText(
            list
        ) {

            if (
                !Array.isArray(
                    list
                )
                || list.length === 0
            ) {
                return "(없음)";
            }

            return list.join(
                "\n"
            );
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
                )
                    .split(
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
                    2300
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
            )
                .padStart(
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