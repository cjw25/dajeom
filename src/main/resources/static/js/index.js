document.addEventListener("DOMContentLoaded", function () {

    const MINIMUM_ANALYSIS_LOADING_TIME = 4200;
    const MAX_WAITING_PROGRESS = 94;

    const CONSULTATION_MODE_KEY =
        "sajuConsultationMode";

    const WEBTOON_MODE =
        "WEBTOON";

    const MZ_DORYEONG_MODE =
        "MZ_DORYEONG";

    const MZ_SAJU_API_URL =
        "/api/saju/calculate";

    const ENABLE_MZ_MOCK_FALLBACK =
        false;

    const screens =
        Array.from(
            document.querySelectorAll(
                "[data-screen]"
            )
        );

    const backButton =
        document.querySelector(
            "#backButton"
        );

    const homeButton =
        document.querySelector(
            "#homeButton"
        );

    const headerTitle =
        document.querySelector(
            "#headerTitle"
        );

    const headerProgress =
        document.querySelector(
            "#headerProgress"
        );

    const bottomNavigation =
        document.querySelector(
            "#bottomNavigation"
        );

    const toast =
        document.querySelector(
            "#toast"
        );

    const nameForm =
        document.querySelector(
            "#nameForm"
        );

    const nameInput =
        document.querySelector(
            "#nameInput"
        );

    const nameError =
        document.querySelector(
            "#nameError"
        );

    const nameNextButton =
        document.querySelector(
            "#nameNextButton"
        );

    const birthForm =
        document.querySelector(
            "#birthForm"
        );

    const birthYear =
        document.querySelector(
            "#birthYear"
        );

    const birthMonth =
        document.querySelector(
            "#birthMonth"
        );

    const birthDay =
        document.querySelector(
            "#birthDay"
        );

    const birthHour =
        document.querySelector(
            "#birthHour"
        );

    const birthMinute =
        document.querySelector(
            "#birthMinute"
        );

    const birthTimeUnknown =
        document.querySelector(
            "#birthTimeUnknown"
        );

    const timeInputArea =
        document.querySelector(
            "#timeInputArea"
        );

    const lunarLeapArea =
        document.querySelector(
            "#lunarLeapArea"
        );

    const lunarLeapMonth =
        document.querySelector(
            "#lunarLeapMonth"
        );

    const birthError =
        document.querySelector(
            "#birthError"
        );

    const birthNextButton =
        document.querySelector(
            "#birthNextButton"
        );

    const birthSpeech =
        document.querySelector(
            "#birthSpeech"
        );

    const submitProfileButton =
        document.querySelector(
            "#submitProfileButton"
        );

    const startAnalysisButton =
        document.querySelector(
            "#startAnalysisButton"
        );

    const analysisProgressRing =
        document.querySelector(
            "#analysisProgressRing"
        );

    const analysisProgressPercent =
        document.querySelector(
            "#analysisProgressPercent"
        );

    const analysisProgressBar =
        document.querySelector(
            "#analysisProgressBar"
        );

    const analysisProgressStatus =
        document.querySelector(
            "#analysisProgressStatus"
        );

    const analysisProgressNumber =
        document.querySelector(
            "#analysisProgressNumber"
        );

    const analysisLoadingMessage =
        document.querySelector(
            "#analysisLoadingMessage"
        );

    const analysisLoadingSteps =
        Array.from(
            document.querySelectorAll(
                "[data-analysis-step]"
            )
        );

    const screenHistory = [];

    const sajuForm =
        loadSajuForm();

    let currentScreen =
        "home";

    let toastTimer =
        null;

    let analysisProgressTimer =
        null;

    let currentAnalysisProgress =
        0;

    let birthFormTouched =
        false;

    let analysisRunning =
        false;

    let webtoonObserver =
        null;

    initialize();

    function initialize() {

        bindNavigationEvents();

        bindMzReportEvents();

        bindNameEvents();

        bindBirthEvents();

        bindProfileEvents();

        bindAnalysisEvents();

        restoreNameForm();

        startAnalysisButton.disabled =
            false;

        startAnalysisButton.textContent =
            "사주 분석 시작";

        applyConsultationModeUi();

        restoreMzResult();

        showScreen(
            "home",
            false
        );
    }

    function createEmptySajuForm() {

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

    function loadSajuForm() {

        try {

            const stored =
                sessionStorage.getItem(
                    "sajuForm"
                );

            return stored

                ? Object.assign(
                    createEmptySajuForm(),
                    JSON.parse(stored)
                )

                : createEmptySajuForm();

        } catch (error) {

            console.error(
                "사주 입력정보 불러오기 오류",
                error
            );

            return createEmptySajuForm();
        }
    }

    function saveSajuForm() {

        sessionStorage.setItem(
            "sajuForm",
            JSON.stringify(sajuForm)
        );
    }

    function resetSajuForm() {

        Object.assign(
            sajuForm,
            createEmptySajuForm()
        );

        sessionStorage.removeItem(
            "sajuForm"
        );

        nameInput.value = "";

        birthYear.value = "";

        birthMonth.value = "";

        birthDay.value = "";

        birthHour.value = "";

        birthMinute.value = "";

        birthTimeUnknown.checked =
            false;

        lunarLeapMonth.checked =
            false;

        birthHour.disabled =
            false;

        birthMinute.disabled =
            false;

        timeInputArea.classList.remove(
            "is-disabled"
        );

        lunarLeapArea.classList.add(
            "hidden"
        );

        document
            .querySelectorAll(
                "[data-calendar]"
            )
            .forEach(
                function (button) {

                    button.classList.toggle(
                        "is-active",
                        button.dataset.calendar
                        === "SOLAR"
                    );
                }
            );

        document
            .querySelectorAll(
                "[data-gender]"
            )
            .forEach(
                function (button) {

                    button.classList.remove(
                        "is-active"
                    );
                }
            );

        nameError.textContent = "";

        birthError.textContent = "";

        nameNextButton.disabled =
            true;

        birthNextButton.disabled =
            true;
    }

    function bindNavigationEvents() {

        document
            .querySelectorAll(
                "[data-go]"
            )
            .forEach(
                function (button) {

                    button.addEventListener(
                        "click",
                        function () {

                            if (
                                button.dataset
                                    .consultationMode
                            ) {

                                setConsultationMode(
                                    button.dataset
                                        .consultationMode
                                );
                            }

                            if (!analysisRunning) {

                                showScreen(
                                    button.dataset.go
                                );
                            }
                        }
                    );
                }
            );

        document
            .querySelectorAll(
                "[data-message]"
            )
            .forEach(
                function (button) {

                    button.addEventListener(
                        "click",
                        function () {

                            showToast(
                                button.dataset.message
                                + " 기능은 다음 단계에서 연결합니다."
                            );
                        }
                    );
                }
            );

        backButton.addEventListener(
            "click",
            function () {

                if (analysisRunning) {
                    return;
                }

                const previous =
                    screenHistory.pop();

                showScreen(
                    previous || "home",
                    false
                );
            }
        );

        homeButton.addEventListener(
            "click",
            function () {

                if (analysisRunning) {
                    return;
                }

                screenHistory.length = 0;

                showScreen(
                    "home",
                    false
                );
            }
        );
    }

    function getHeaderInformation(
        screenName
    ) {

        const information = {

            home: {
                title: "AI 운세",
                progress: ""
            },

            intro: {
                title: "AI 사주",
                progress: "상담 안내"
            },

            name: {
                title: "AI 사주",
                progress: "기본정보 1 / 2"
            },

            birth: {
                title: "AI 사주",
                progress: "기본정보 2 / 2"
            },

            review: {
                title: "AI 사주",
                progress: "입력정보 확인"
            },

            complete: {
                title: "AI 사주",
                progress: "저장 완료"
            },

            analysis: {
                title: "AI 사주",
                progress: "웹툰 결과 구성 중"
            },

            result: {
                title: "나의 사주 이야기",
                progress: "웹툰 결과"
            },

            "mz-result": {
                title: "MZ 천명도령",
                progress: "정통 종합사주"
            }
        };

        return information[screenName]
            || information.home;
    }

    function showScreen(
        screenName,
        addHistory
    ) {

        const shouldAddHistory =
            addHistory !== false;

        if (
            (
                currentScreen === "result"
                || currentScreen === "mz-result"
            )
            && screenName !== currentScreen
        ) {

            stopWebtoonReveal();
        }

        if (
            shouldAddHistory
            && currentScreen !== screenName
        ) {

            screenHistory.push(
                currentScreen
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

        currentScreen =
            screenName;

        applyConsultationModeUi();

        const header =
            getHeaderInformation(
                screenName
            );

        headerTitle.textContent =
            header.title;

        headerProgress.textContent =
            header.progress;

        const isHome =
            screenName === "home";

        backButton.classList.toggle(
            "is-hidden",
            isHome
        );

        homeButton.classList.toggle(
            "is-hidden",
            isHome
        );

        bottomNavigation.classList.toggle(
            "hidden",
            !isHome
        );

        if (screenName === "birth") {

            restoreBirthForm();

            birthFormTouched =
                false;

            validateBirthForm(
                false
            );
        }

        if (screenName === "review") {

            renderReview();
        }

        window.scrollTo({
            top: 0,
            behavior: "auto"
        });

        if (screenName === "result") {

            requestAnimationFrame(
                function () {

                    requestAnimationFrame(
                        startWebtoonReveal
                    );
                }
            );
        }
    }

    function showToast(message) {

        clearTimeout(
            toastTimer
        );

        toast.textContent =
            message;

        toast.classList.add(
            "is-show"
        );

        toastTimer =
            setTimeout(
                function () {

                    toast.classList.remove(
                        "is-show"
                    );

                },
                2300
            );
    }

    function bindNameEvents() {

        nameInput.addEventListener(
            "input",
            validateName
        );

        nameForm.addEventListener(
            "submit",
            function (event) {

                event.preventDefault();

                if (!validateName()) {
                    return;
                }

                sajuForm.name =
                    nameInput.value.trim();

                saveSajuForm();

                birthSpeech.innerHTML =
                    escapeHtml(
                        sajuForm.name
                    )
                    + "님의 생년월일과<br>"
                    + "태어난 시간을 알려주세요.";

                showScreen(
                    "birth"
                );
            }
        );
    }

    function restoreNameForm() {

        if (sajuForm.name) {

            nameInput.value =
                sajuForm.name;

            validateName();
        }
    }

    function validateName() {

        const name =
            nameInput.value.trim();

        if (!name) {

            nameError.textContent = "";

            nameNextButton.disabled =
                true;

            return false;
        }

        if (name.length < 2) {

            nameError.textContent =
                "이름은 두 글자 이상 입력해주세요.";

            nameNextButton.disabled =
                true;

            return false;
        }

        if (name.length > 20) {

            nameError.textContent =
                "이름은 20자 이하로 입력해주세요.";

            nameNextButton.disabled =
                true;

            return false;
        }

        nameError.textContent = "";

        nameNextButton.disabled =
            false;

        return true;
    }

    function bindBirthEvents() {

        document
            .querySelectorAll(
                ".numeric-input"
            )
            .forEach(
                function (input) {

                    input.addEventListener(
                        "input",
                        function () {

                            birthFormTouched =
                                true;

                            input.value =
                                input.value.replace(
                                    /[^0-9]/g,
                                    ""
                                );

                            validateBirthForm(
                                true
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

                            birthFormTouched =
                                true;

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

                            sajuForm.calendarType =
                                button.dataset.calendar;

                            const isLunar =
                                sajuForm.calendarType
                                === "LUNAR";

                            lunarLeapArea.classList.toggle(
                                "hidden",
                                !isLunar
                            );

                            if (!isLunar) {

                                lunarLeapMonth.checked =
                                    false;

                                sajuForm.lunarLeapMonth =
                                    false;
                            }

                            validateBirthForm(
                                true
                            );
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

                            birthFormTouched =
                                true;

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

                            sajuForm.gender =
                                button.dataset.gender;

                            validateBirthForm(
                                true
                            );
                        }
                    );
                }
            );

        lunarLeapMonth.addEventListener(
            "change",
            function () {

                sajuForm.lunarLeapMonth =
                    lunarLeapMonth.checked;
            }
        );

        birthTimeUnknown.addEventListener(
            "change",
            function () {

                birthFormTouched =
                    true;

                sajuForm.birthTimeUnknown =
                    birthTimeUnknown.checked;

                timeInputArea.classList.toggle(
                    "is-disabled",
                    birthTimeUnknown.checked
                );

                birthHour.disabled =
                    birthTimeUnknown.checked;

                birthMinute.disabled =
                    birthTimeUnknown.checked;

                if (
                    birthTimeUnknown.checked
                ) {

                    birthHour.value = "";

                    birthMinute.value = "";
                }

                validateBirthForm(
                    true
                );
            }
        );

        birthForm.addEventListener(
            "submit",
            function (event) {

                event.preventDefault();

                birthFormTouched =
                    true;

                if (
                    !validateBirthForm(
                        true
                    )
                ) {
                    return;
                }

                sajuForm.birthDate =
                    birthYear.value
                    + "-"
                    + padNumber(
                        birthMonth.value
                    )
                    + "-"
                    + padNumber(
                        birthDay.value
                    );

                sajuForm.birthTimeUnknown =
                    birthTimeUnknown.checked;

                sajuForm.birthTime =
                    birthTimeUnknown.checked

                        ? null

                        : padNumber(
                            birthHour.value
                        )
                        + ":"
                        + padNumber(
                            birthMinute.value
                        );

                sajuForm.lunarLeapMonth =
                    sajuForm.calendarType
                    === "LUNAR"

                    && lunarLeapMonth.checked;

                saveSajuForm();

                showScreen(
                    "review"
                );
            }
        );
    }

    function restoreBirthForm() {

        if (sajuForm.name) {

            birthSpeech.innerHTML =
                escapeHtml(
                    sajuForm.name
                )
                + "님의 생년월일과<br>"
                + "태어난 시간을 알려주세요.";
        }

        document
            .querySelectorAll(
                "[data-calendar]"
            )
            .forEach(
                function (button) {

                    button.classList.toggle(
                        "is-active",
                        button.dataset.calendar
                        === sajuForm.calendarType
                    );
                }
            );

        const isLunar =
            sajuForm.calendarType
            === "LUNAR";

        lunarLeapArea.classList.toggle(
            "hidden",
            !isLunar
        );

        lunarLeapMonth.checked =
            isLunar
            && sajuForm.lunarLeapMonth;

        document
            .querySelectorAll(
                "[data-gender]"
            )
            .forEach(
                function (button) {

                    button.classList.toggle(
                        "is-active",
                        button.dataset.gender
                        === sajuForm.gender
                    );
                }
            );

        if (sajuForm.birthDate) {

            const parts =
                sajuForm.birthDate.split(
                    "-"
                );

            birthYear.value =
                parts[0] || "";

            birthMonth.value =
                parts[1] || "";

            birthDay.value =
                parts[2] || "";
        }

        birthTimeUnknown.checked =
            sajuForm.birthTimeUnknown;

        timeInputArea.classList.toggle(
            "is-disabled",
            sajuForm.birthTimeUnknown
        );

        birthHour.disabled =
            sajuForm.birthTimeUnknown;

        birthMinute.disabled =
            sajuForm.birthTimeUnknown;

        if (
            sajuForm.birthTime
            && !sajuForm.birthTimeUnknown
        ) {

            const timeParts =
                sajuForm.birthTime.split(
                    ":"
                );

            birthHour.value =
                timeParts[0] || "";

            birthMinute.value =
                timeParts[1] || "";

        } else {

            birthHour.value = "";

            birthMinute.value = "";
        }
    }

    function validateBirthForm(
        showMessage
    ) {

        const shouldShow =
            showMessage !== false
            && birthFormTouched;

        if (!sajuForm.gender) {

            return setBirthError(
                "성별을 선택해주세요.",
                shouldShow
            );
        }

        if (
            !birthYear.value
            || !birthMonth.value
            || !birthDay.value
        ) {

            return setBirthError(
                "생년월일을 모두 입력해주세요.",
                shouldShow
            );
        }

        const validDate =
            sajuForm.calendarType
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

        if (!validDate) {

            return setBirthError(
                "올바른 생년월일을 입력해주세요.",
                shouldShow
            );
        }

        if (
            !birthTimeUnknown.checked
            && (
                !birthHour.value
                || !birthMinute.value
            )
        ) {

            return setBirthError(
                "태어난 시간을 입력하거나 시간 모름을 선택해주세요.",
                shouldShow
            );
        }

        if (
            !birthTimeUnknown.checked
            && !isValidTime(
                birthHour.value,
                birthMinute.value
            )
        ) {

            return setBirthError(
                "올바른 출생시간을 입력해주세요.",
                shouldShow
            );
        }

        birthError.textContent = "";

        birthNextButton.disabled =
            false;

        return true;
    }

    function setBirthError(
        message,
        showMessage
    ) {

        birthError.textContent =
            showMessage
                ? message
                : "";

        birthNextButton.disabled =
            true;

        return false;
    }

    function isValidSolarDate(
        year,
        month,
        day
    ) {

        const y =
            Number(year);

        const m =
            Number(month);

        const d =
            Number(day);

        const currentYear =
            new Date()
                .getFullYear();

        if (
            y < 1900
            || y > currentYear
        ) {
            return false;
        }

        const date =
            new Date(
                y,
                m - 1,
                d
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
            date.getFullYear() === y
            && date.getMonth() === m - 1
            && date.getDate() === d
            && date <= today
        );
    }

    function isValidLunarDate(
        year,
        month,
        day
    ) {

        const y =
            Number(year);

        const m =
            Number(month);

        const d =
            Number(day);

        return (
            y >= 1900
            && y <= new Date().getFullYear()
            && m >= 1
            && m <= 12
            && d >= 1
            && d <= 30
        );
    }

    function isValidTime(
        hour,
        minute
    ) {

        const h =
            Number(hour);

        const m =
            Number(minute);

        return (
            h >= 0
            && h <= 23
            && m >= 0
            && m <= 59
        );
    }

    function padNumber(value) {

        return String(value)
            .padStart(
                2,
                "0"
            );
    }

    function renderReview() {

        setText(
            "#reviewName",
            sajuForm.name
        );

        setText(
            "#reviewGender",

            sajuForm.gender === "MALE"
                ? "남성"
                : "여성"
        );

        setText(
            "#reviewCalendar",

            sajuForm.calendarType === "SOLAR"
                ? "양력"
                : "음력"
        );

        const leapRow =
            document.querySelector(
                "#reviewLunarLeapRow"
            );

        leapRow.classList.toggle(
            "hidden",
            sajuForm.calendarType
            !== "LUNAR"
        );

        setText(
            "#reviewLunarLeap",

            sajuForm.lunarLeapMonth
                ? "윤달"
                : "평달"
        );

        setText(
            "#reviewBirthDate",
            sajuForm.birthDate
        );

        setText(
            "#reviewBirthTime",

            sajuForm.birthTimeUnknown
                ? "시간 모름"
                : sajuForm.birthTime
        );
    }

    function bindProfileEvents() {

        submitProfileButton.addEventListener(
            "click",
            async function () {

                if (isMzDoryeongMode()) {

                    const validationMessage =
                        validateMzSajuInput();

                    if (validationMessage) {

                        showToast(
                            validationMessage
                        );

                        return;
                    }

                    sessionStorage.setItem(
                        "mzSajuForm",
                        JSON.stringify(
                            sajuForm
                        )
                    );

                    showScreen(
                        "complete"
                    );

                    return;
                }

                submitProfileButton.disabled =
                    true;

                submitProfileButton.textContent =
                    "저장 중...";

                try {

                    const response =
                        await fetch(
                            "/api/saju/profiles",
                            {
                                method: "POST",

                                headers: {
                                    "Content-Type":
                                        "application/json"
                                },

                                body:
                                    JSON.stringify(
                                        sajuForm
                                    )
                            }
                        );

                    const body =
                        await readJsonResponse(
                            response
                        );

                    if (!response.ok) {

                        throw new Error(
                            body.message
                            || "저장에 실패했습니다."
                        );
                    }

                    sessionStorage.setItem(
                        "sajuProfileId",

                        String(
                            body.profileId
                        )
                    );

                    resetSajuForm();

                    showScreen(
                        "complete"
                    );

                } catch (error) {

                    console.error(
                        "사주 기본정보 저장 오류",
                        error
                    );

                    showToast(
                        error.message
                        || "저장 중 오류가 발생했습니다."
                    );

                } finally {

                    submitProfileButton.disabled =
                        false;

                    submitProfileButton.textContent =
                        "확인 및 저장";
                }
            }
        );
    }

    function bindAnalysisEvents() {

        startAnalysisButton.addEventListener(
            "click",
            async function () {

                if (analysisRunning) {
                    return;
                }

                if (isMzDoryeongMode()) {

                    await runMzDoryeongAnalysis();

                    return;
                }

                const profileId =
                    Number(
                        sessionStorage.getItem(
                            "sajuProfileId"
                        )
                    );

                if (
                    !Number.isInteger(
                        profileId
                    )
                    || profileId <= 0
                ) {

                    showToast(
                        "저장된 사주 기본정보가 없습니다."
                    );

                    return;
                }

                analysisRunning =
                    true;

                startAnalysisButton.disabled =
                    true;

                startAnalysisButton.textContent =
                    "분석 중...";

                try {

                    resetAnalysisProgress();

                    showScreen(
                        "analysis"
                    );

                    startAnalysisProgress();

                    const results =
                        await Promise.all([

                            fetch(
                                "/api/saju/analyses",
                                {
                                    method: "POST",

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
                            ),

                            wait(
                                MINIMUM_ANALYSIS_LOADING_TIME
                            )
                        ]);

                    const response =
                        results[0];

                    const result =
                        await readJsonResponse(
                            response
                        );

                    if (!response.ok) {

                        throw new Error(
                            result.message
                            || "사주 분석에 실패했습니다."
                        );
                    }

                    sessionStorage.setItem(
                        "sajuAnalysisId",

                        String(
                            result.analysisId
                        )
                    );

                    sessionStorage.setItem(
                        "sajuAnalysisResult",

                        JSON.stringify(
                            result
                        )
                    );

                    await completeAnalysisProgress();

                    renderSajuResult(
                        result
                    );

                    screenHistory.length =
                        0;

                    screenHistory.push(
                        "home"
                    );

                    showScreen(
                        "result",
                        false
                    );

                } catch (error) {

                    stopAnalysisProgress();

                    console.error(
                        "사주 분석 오류",
                        error
                    );

                    showToast(
                        error.message
                        || "사주 분석 중 오류가 발생했습니다."
                    );

                    showScreen(
                        "complete",
                        false
                    );

                } finally {

                    analysisRunning =
                        false;

                    startAnalysisButton.disabled =
                        false;

                    startAnalysisButton.textContent =
                        "사주 분석 시작";
                }
            }
        );
    }

    function resetAnalysisProgress() {

        stopAnalysisProgress();

        currentAnalysisProgress =
            0;

        updateAnalysisProgress(
            0
        );

        analysisLoadingSteps.forEach(
            function (step, index) {

                step.classList.remove(
                    "is-active",
                    "is-complete"
                );

                step
                    .querySelector(
                        ".analysis-step-number"
                    )
                    .textContent =
                        String(index + 1);
            }
        );

        if (analysisLoadingSteps[0]) {

            analysisLoadingSteps[0]
                .classList
                .add(
                    "is-active"
                );
        }
    }

    function startAnalysisProgress() {

        stopAnalysisProgress();

        analysisProgressTimer =
            setInterval(
                function () {

                    if (
                        currentAnalysisProgress
                        >= MAX_WAITING_PROGRESS
                    ) {
                        return;
                    }

                    let increase =
                        1;

                    if (
                        currentAnalysisProgress
                        < 20
                    ) {

                        increase =
                            2;

                    } else if (
                        currentAnalysisProgress
                        < 60
                    ) {

                        increase =
                            1;

                    } else if (
                        currentAnalysisProgress
                        < 85
                    ) {

                        increase =
                            Math.random() > 0.45
                                ? 1
                                : 0;

                    } else {

                        increase =
                            Math.random() > 0.72
                                ? 1
                                : 0;
                    }

                    currentAnalysisProgress =
                        Math.min(
                            currentAnalysisProgress
                            + increase,

                            MAX_WAITING_PROGRESS
                        );

                    updateAnalysisProgress(
                        currentAnalysisProgress
                    );

                },
                90
            );
    }

    function stopAnalysisProgress() {

        if (
            analysisProgressTimer
            !== null
        ) {

            clearInterval(
                analysisProgressTimer
            );

            analysisProgressTimer =
                null;
        }
    }

    async function completeAnalysisProgress() {

        stopAnalysisProgress();

        while (
            currentAnalysisProgress
            < 100
        ) {

            currentAnalysisProgress =
                Math.min(
                    currentAnalysisProgress
                    + 2,

                    100
                );

            updateAnalysisProgress(
                currentAnalysisProgress
            );

            await wait(
                45
            );
        }

        analysisLoadingMessage.textContent =
            isMzDoryeongMode()
                ? "원국과 운의 흐름을 정리한 종합 보고서가 완성되었습니다."
                : "당신의 사주 이야기가 완성되었습니다.";

        analysisProgressStatus.textContent =
            isMzDoryeongMode()
                ? "종합 보고서 완성"
                : "웹툰 결과 완성";

        await wait(
            650
        );
    }

    function updateAnalysisProgress(
        progress
    ) {

        const normalized =
            Math.max(
                0,

                Math.min(
                    100,
                    progress
                )
            );

        analysisProgressRing
            .style
            .setProperty(
                "--progress",
                String(normalized)
            );

        analysisProgressPercent.textContent =
            normalized + "%";

        analysisProgressBar.style.width =
            normalized + "%";

        analysisProgressNumber.textContent =
            normalized
            + " / 100";

        updateAnalysisStage(
            normalized
        );
    }

    function updateAnalysisStage(
        progress
    ) {

        let activeStep =
            0;

        let message =
            "입력한 생년월일과 달력 정보를 확인하고 있습니다.";

        let status =
            "음양력 정보 확인";

        if (isMzDoryeongMode()) {

            message =
                "입력한 생년월일과 출생시간을 확인하고 있습니다.";

            status =
                "출생정보 확인";
        }

        if (progress >= 25) {

            activeStep =
                1;

            message =
                isMzDoryeongMode()
                    ? "원국의 사주팔자와 오행 세력을 계산하고 있습니다."
                    : "년주·월주·일주·시주와 오행을 계산하고 있습니다.";

            status =
                "사주팔자 계산";
        }

        if (progress >= 55) {

            activeStep =
                2;

            message =
                isMzDoryeongMode()
                    ? "용신과 대운·세운의 상호작용을 분석하고 있습니다."
                    : "십성·관계·신살과 운의 흐름을 분석하고 있습니다.";

            status =
                isMzDoryeongMode()
                    ? "원국·운세 상호작용"
                    : "삶의 흐름 분석";
        }

        if (progress >= 82) {

            activeStep =
                3;

            message =
                isMzDoryeongMode()
                    ? "핵심 신호와 11개 분야 상세 보고서를 구성하고 있습니다."
                    : "계산값을 소비자가 이해하기 쉬운 웹툰 장면으로 바꾸고 있습니다.";

            status =
                isMzDoryeongMode()
                    ? "11개 보고서 구성"
                    : "웹툰 이야기 구성";
        }

        if (progress >= 100) {

            message =
                isMzDoryeongMode()
                    ? "정통 종합사주 보고서가 완성되었습니다."
                    : "모든 장면이 완성되었습니다.";

            status =
                isMzDoryeongMode()
                    ? "종합 보고서 완성"
                    : "웹툰 결과 완성";
        }

        analysisLoadingMessage.textContent =
            message;

        analysisProgressStatus.textContent =
            status;

        analysisLoadingSteps.forEach(
            function (step, index) {

                const number =
                    step.querySelector(
                        ".analysis-step-number"
                    );

                if (
                    progress >= 100
                    || index < activeStep
                ) {

                    step.classList.remove(
                        "is-active"
                    );

                    step.classList.add(
                        "is-complete"
                    );

                    number.textContent =
                        "✓";

                    return;
                }

                if (index === activeStep) {

                    step.classList.remove(
                        "is-complete"
                    );

                    step.classList.add(
                        "is-active"
                    );

                    number.textContent =
                        String(index + 1);

                    return;
                }

                step.classList.remove(
                    "is-active",
                    "is-complete"
                );

                number.textContent =
                    String(index + 1);
            }
        );
    }

    function getConsultationMode() {

        const storedMode =
            sessionStorage.getItem(
                CONSULTATION_MODE_KEY
            );

        return storedMode === MZ_DORYEONG_MODE
            ? MZ_DORYEONG_MODE
            : WEBTOON_MODE;
    }

    function setConsultationMode(mode) {

        const normalizedMode =
            mode === MZ_DORYEONG_MODE
                ? MZ_DORYEONG_MODE
                : WEBTOON_MODE;

        sessionStorage.setItem(
            CONSULTATION_MODE_KEY,
            normalizedMode
        );

        applyConsultationModeUi();
    }

    function isMzDoryeongMode() {

        return getConsultationMode()
            === MZ_DORYEONG_MODE;
    }

    function applyConsultationModeUi() {

        const mzMode =
            isMzDoryeongMode();

        document.body.classList.toggle(
            "is-mz-doryeong-mode",
            mzMode
            && currentScreen !== "home"
        );

        const characterPath =
            mzMode
                ? "/img/mz-doryeong-intro.png"
                : "/img/saju-character.png";

        document
            .querySelectorAll(
                ".mode-character-image"
            )
            .forEach(
                function (image) {

                    image.onerror =
                        function () {

                            image.onerror =
                                null;

                            image.src =
                                "/img/saju-character.png";
                        };

                    image.src =
                        characterPath;

                    image.alt =
                        mzMode
                            ? "MZ 천명도령"
                            : "관상선생";
                }
            );

        document
            .querySelectorAll(
                ".mode-counselor-name"
            )
            .forEach(
                function (element) {

                    element.textContent =
                        mzMode
                            ? "MZ 천명도령"
                            : "관상선생";
                }
            );

        setText(
            "#introCounselorName",
            mzMode
                ? "MZ 천명도령"
                : "관상선생"
        );

        setText(
            "#introCounselorSpeech",
            mzMode
                ? "긴 말 안 할게요. 지금 막히는 이유와 앞으로 힘을 써야 할 곳부터 바로 짚어드릴게요."
                : "반갑습니다. 어려운 명리 용어는 잠시 내려놓고, 당신의 삶에 어떤 의미인지 이야기로 풀어드릴게요."
        );

        setText(
            "#introModeBadge",
            mzMode
                ? "NEW · 정통 종합사주"
                : "AI 사주 상담사"
        );

        setText(
            "#introModeTitle",
            mzMode
                ? "MZ 천명도령"
                : "관상선생"
        );

        setText(
            "#introModeDescription",
            mzMode
                ? "원국·용신·대운·세운을 계산하고, 기본 성향부터 재물·직업·연애·현재운까지 11개 분야로 정리합니다."
                : "생년월일과 출생시간을 기반으로 만세력을 계산한 뒤, 소비자가 이해하기 쉬운 웹툰형 해석을 제공합니다."
        );

        setText(
            "#introResultStepText",
            mzMode
                ? "도령의 10초 요약과 11개 분야 정밀 보고서 확인"
                : "분석 후 웹툰형 사주 결과 확인"
        );

        setText(
            "#nameScreenSpeech",
            mzMode
                ? "결과에서 어떻게 불러드릴까요? 이름이나 닉네임을 알려주세요."
                : "이야기 속에서 불러드릴 이름을 알려주세요."
        );

        setText(
            "#completeModeTitle",
            mzMode
                ? "도령에게 물어볼 준비 완료"
                : "기본정보 저장 완료"
        );

        setText(
            "#completeModeDescription",
            mzMode
                ? "이제 원국부터 용신·대운·세운까지 계산해, 핵심과 상세 보고서를 함께 보여드릴게요."
                : "입력한 정보를 바탕으로 만세력을 계산하고, 웹툰형 사주 이야기를 만들 수 있습니다."
        );

        setText(
            "#analysisModeBadge",
            mzMode
                ? "MZ 천명도령 정밀 분석"
                : "AI 만세력 분석"
        );

        setText(
            "#analysisModeTitle",
            mzMode
                ? "당신의 원국과 운의 흐름을 뜯어보고 있습니다"
                : "당신의 사주 이야기를 만들고 있습니다"
        );

        setText(
            "#analysisThirdStepTitle",
            mzMode
                ? "용신·대운·세운 분석"
                : "십성·신살·대운 분석"
        );

        setText(
            "#analysisThirdStepDescription",
            mzMode
                ? "원국과 현재 운이 부딪히는 지점을 확인합니다."
                : "관계와 운의 흐름을 분석합니다."
        );

        setText(
            "#analysisFourthStepTitle",
            mzMode
                ? "11개 분야 보고서 구성"
                : "웹툰 이야기 구성"
        );

        setText(
            "#analysisFourthStepDescription",
            mzMode
                ? "핵심 신호를 짧고 선명한 카드로 정리합니다."
                : "계산값을 이해하기 쉬운 장면으로 바꿉니다."
        );

        if (!analysisRunning) {

            startAnalysisButton.textContent =
                mzMode
                    ? "MZ 천명도령에게 물어보기"
                    : "사주 분석 시작";
        }
    }

    function validateMzSajuInput() {

        if (!sajuForm.name) {

            return "이름이나 닉네임을 입력해주세요.";
        }

        if (!sajuForm.gender) {

            return "성별을 선택해주세요.";
        }

        if (
            !/^\d{4}-\d{2}-\d{2}$/
                .test(
                    sajuForm.birthDate
                    || ""
                )
        ) {

            return "생년월일을 정확하게 입력해주세요.";
        }

        if (
            !sajuForm.birthTimeUnknown
            && !/^\d{2}:\d{2}$/
                .test(
                    sajuForm.birthTime
                    || ""
                )
        ) {

            return "출생시간을 정확하게 입력하거나 시간 모름을 선택해주세요.";
        }

        return "";
    }

    async function runMzDoryeongAnalysis() {

        const validationMessage =
            validateMzSajuInput();

        if (validationMessage) {

            showToast(
                validationMessage
            );

            return;
        }

        analysisRunning =
            true;

        startAnalysisButton.disabled =
            true;

        startAnalysisButton.textContent =
            "원국 확인 중...";

        try {

            resetAnalysisProgress();

            showScreen(
                "analysis"
            );

            startAnalysisProgress();

            const request =
                createMzSajuRequest();

            const result =
                await requestMzSajuAnalysis(
                    request
                );

            sessionStorage.setItem(
                "mzSajuAnalysisResult",
                JSON.stringify(
                    result
                )
            );

            await completeAnalysisProgress();

            renderMzSajuResult(
                result
            );

            screenHistory.length =
                0;

            screenHistory.push(
                "home"
            );

            showScreen(
                "mz-result",
                false
            );

        } catch (error) {

            stopAnalysisProgress();

            console.error(
                "MZ 천명도령 분석 오류",
                error
            );

            showToast(
                error.message
                || "정통 종합사주 분석 중 오류가 발생했습니다."
            );

            showScreen(
                "complete",
                false
            );

        } finally {

            analysisRunning =
                false;

            startAnalysisButton.disabled =
                false;

            startAnalysisButton.textContent =
                "MZ 천명도령에게 물어보기";
        }
    }

    function createMzSajuRequest() {

        return {
            calendarType:
                sajuForm.calendarType,

            birthDate:
                sajuForm.birthDate,

            birthTime:
                sajuForm.birthTimeUnknown
                    ? null
                    : sajuForm.birthTime,

            birthTimeUnknown:
                Boolean(
                    sajuForm.birthTimeUnknown
                ),

            gender:
                sajuForm.gender,

            leapMonth:
                sajuForm.calendarType === "LUNAR"
                && Boolean(
                    sajuForm.lunarLeapMonth
                ),

            targetYear:
                new Date()
                    .getFullYear()
        };
    }

    async function requestMzSajuAnalysis(request) {

        const startedAt =
            Date.now();

        try {

            const response =
                await fetch(
                    MZ_SAJU_API_URL,
                    {
                        method: "POST",

                        headers: {
                            "Content-Type":
                                "application/json"
                        },

                        body:
                            JSON.stringify(
                                request
                            )
                    }
                );

            const result =
                await readJsonResponse(
                    response
                );

            if (!response.ok) {

                throw new Error(
                    result.message
                    || "정통 종합사주 분석 요청에 실패했습니다."
                );
            }

            await waitForMinimumAnalysisTime(
                startedAt
            );

            return normalizeMzSajuApiResponse(
                result
            );

        } catch (error) {

            if (!ENABLE_MZ_MOCK_FALLBACK) {

                throw error;
            }

            console.warn(
                "백엔드 연결 전이라 MZ 천명도령 목업 결과를 사용합니다.",
                error
            );

            await waitForMinimumAnalysisTime(
                startedAt
            );

            return createMzMockResult(
                request
            );
        }
    }

    async function waitForMinimumAnalysisTime(
        startedAt
    ) {

        const elapsed =
            Date.now()
            - startedAt;

        if (
            elapsed
            < MINIMUM_ANALYSIS_LOADING_TIME
        ) {

            await wait(
                MINIMUM_ANALYSIS_LOADING_TIME
                - elapsed
            );
        }
    }

    function normalizeMzSajuApiResponse(result) {

        if (!result || typeof result !== "object") {

            return result;
        }

        if (
            result.sajuReport
            && Array.isArray(
                result.sajuReport.sections
            )
        ) {

            return result;
        }

        const pillars = [
            result.yearPillar,
            result.monthPillar,
            result.dayPillar,
            result.hourPillar
        ].filter(Boolean);

        const fullSajuChinese =
            pillars
                .map(
                    function (pillar) {

                        return safeText(
                            pillar.chineseName,
                            ""
                        );
                    }
                )
                .filter(Boolean)
                .join(" ");

        const fullSajuKorean =
            pillars
                .map(
                    function (pillar) {

                        return safeText(
                            pillar.koreanName,
                            ""
                        );
                    }
                )
                .filter(Boolean)
                .join(" ");

        const dayPillar =
            result.dayPillar
            || {};

        const yongshinCandidates =
            Array.isArray(
                result.yongshinCandidates
            )
                ? result.yongshinCandidates
                : [];

        const huishinCandidates =
            Array.isArray(
                result.huishinCandidates
            )
                ? result.huishinCandidates
                : [];

        const decadeLuck =
            result.decadeLuck
            || {};

        const annualLuck =
            result.annualLuck
            || {};

        const consultations =
            Array.isArray(
                result.consultations
            )
                ? result.consultations
                : [];

        const currentYearSection =
            consultations.find(
                function (section) {

                    return section.category
                        === "CURRENT_YEAR_LUCK";
                }
            )
            || null;

        const reportSections =
            consultations.map(
                function (
                    section,
                    index
                ) {

                    const basisLabels =
                        Array.isArray(
                            section.basisLabels
                        )
                            ? section.basisLabels
                            : [];

                    const contentParts = [
                        section.guidanceMessage
                    ].filter(Boolean);

                    if (basisLabels.length) {

                        contentParts.push(
                            "분석 근거: "
                            + basisLabels.join(" · ")
                        );
                    }

                    return {
                        category:
                            safeText(
                                section.category,
                                "SECTION_"
                                + (index + 1)
                            ),

                        categoryKorean:
                            safeText(
                                section.title,
                                "상세 해석"
                            ),

                        categoryDescription:
                            basisLabels.length
                                ? basisLabels.join(" · ")
                                : "명리 계산 근거 기반 해석",

                        title:
                            safeText(
                                section.openingMessage,
                                section.title
                                || "상세 해석"
                            ),

                        coreSummary:
                            safeText(
                                section.summaryMessage,
                                "핵심 흐름을 확인해보세요."
                            ),

                        content:
                            contentParts.join("\n\n"),

                        characterCount:
                            safeText(
                                section.narrationText,
                                ""
                            ).length,

                        polarity:
                            section.polarity,

                        polarityKorean:
                            mzPolarityKorean(
                                section.polarity
                            ),

                        intensity:
                            section.intensity,

                        intensityKorean:
                            mzIntensityKorean(
                                section.intensity
                            ),

                        confidence:
                            section.confidence,

                        confidenceKorean:
                            mzConfidenceKorean(
                                section.confidence
                            ),

                        evidenceCount:
                            section.evidenceCount
                            ?? 0,

                        usedSignalCodes:
                            basisLabels
                    };
                }
            );

        return Object.assign(
            {},
            result,
            {
                fullSajuChinese:
                    fullSajuChinese,

                fullSajuKorean:
                    fullSajuKorean,

                dayMaster:
                    safeText(
                        result.dayMasterChineseName,
                        "-"
                    ),

                dayMasterKorean:
                    joinNonEmpty(
                        [
                            result.dayMasterKoreanName,
                            result.dayMasterChineseName
                                ? "(" + result.dayMasterChineseName + ")"
                                : ""
                        ],
                        " "
                    ),

                dayMasterElementKorean:
                    mzElementKorean(
                        dayPillar.stemElement
                    ),

                dayMasterYinYangKorean:
                    mzYinYangKorean(
                        dayPillar.stemYinYang
                    ),

                strengthAnalysis: {
                    level:
                        result.strengthLevel,

                    levelKorean:
                        safeText(
                            result.strengthLevel,
                            "-"
                        ),

                    strengthLevelKorean:
                        safeText(
                            result.strengthLevel,
                            "-"
                        )
                },

                usefulGodAnalysis: {
                    primaryUsefulGod: {
                        elementKorean:
                            safeText(
                                yongshinCandidates[0],
                                "미정"
                            ),

                        typeKorean:
                            "용신 후보"
                    },

                    secondaryFavorableGod: {
                        elementKorean:
                            safeText(
                                huishinCandidates[0],
                                "미정"
                            ),

                        typeKorean:
                            "희신 후보"
                    }
                },

                luckAnalysis: {
                    directionKorean:
                        safeText(
                            decadeLuck.direction,
                            "장기 흐름"
                        ),

                    currentDecadeLuck:
                        decadeLuck.calculationAvailable
                        && decadeLuck.currentPillar
                            ? {
                                pillar:
                                    decadeLuck.currentPillar,

                                startDate:
                                    formatMzDateTime(
                                        decadeLuck.currentStartDateTime
                                    ),

                                endDate:
                                    formatMzDateTime(
                                        decadeLuck.currentEndDateTime
                                    )
                            }
                            : null,

                    annualLuck: {
                        targetYear:
                            annualLuck.targetYear
                            || result.targetYear,

                        pillar:
                            annualLuck.pillar
                            || null,

                        stemTenGod:
                            annualLuck.stemTenGod,

                        branchMainTenGod:
                            annualLuck.branchMainTenGod
                    }
                },

                luckInteractionAnalysis: {
                    combinedLevelKorean:
                        currentYearSection
                            ? mzPolarityKorean(
                                currentYearSection.polarity
                            )
                            : "운의 흐름 확인",

                    combinedIntensityKorean:
                        currentYearSection
                            ? mzIntensityKorean(
                                currentYearSection.intensity
                            )
                            : "-",

                    combinedMessage:
                        currentYearSection
                            ? joinNonEmpty(
                                [
                                    currentYearSection.summaryMessage,
                                    currentYearSection.guidanceMessage
                                ],
                                " "
                            )
                            : "현재 대운과 세운의 흐름을 함께 확인해보세요.",

                    summarySignals:
                        currentYearSection
                        && Array.isArray(
                            currentYearSection.basisLabels
                        )
                            ? currentYearSection.basisLabels
                            : []
                },

                sajuReport: {
                    targetYear:
                        result.targetYear,

                    fullSajuChinese:
                        fullSajuChinese,

                    fullSajuKorean:
                        fullSajuKorean,

                    sections:
                        reportSections,

                    sectionCount:
                        reportSections.length,

                    totalCharacterCount:
                        reportSections.reduce(
                            function (
                                sum,
                                section
                            ) {

                                return sum
                                    + section.characterCount;
                            },
                            0
                        ),

                    generationMode:
                        "JAVA_CONSULTATION",

                    ruleVersion:
                        "DAJEOM_FRONT_ADAPTER_V1"
                },

                disclaimer:
                    "본 결과는 명리 계산을 기반으로 제공하는 참고 정보입니다. 건강·법률·투자 등 전문적인 판단을 대신하지 않습니다."
            }
        );
    }

    function mzPolarityKorean(value) {

        const labels = {
            SUPPORTIVE: "지원 흐름",
            CAUTION: "주의 흐름",
            MIXED: "복합 흐름",
            NEUTRAL: "중립"
        };

        return labels[value]
            || "중립";
    }

    function mzIntensityKorean(value) {

        const labels = {
            NONE: "근거 부족",
            LOW: "약함",
            MEDIUM: "보통",
            HIGH: "강함"
        };

        return labels[value]
            || "-";
    }

    function mzConfidenceKorean(value) {

        const labels = {
            LOW: "낮음",
            MEDIUM: "보통",
            HIGH: "높음"
        };

        return labels[value]
            || "-";
    }

    function mzElementKorean(value) {

        const labels = {
            WOOD: "목",
            FIRE: "화",
            EARTH: "토",
            METAL: "금",
            WATER: "수"
        };

        return labels[value]
            || safeText(
                value,
                "-"
            );
    }

    function mzYinYangKorean(value) {

        const labels = {
            YANG: "양",
            YIN: "음"
        };

        return labels[value]
            || safeText(
                value,
                "-"
            );
    }

    function formatMzDateTime(value) {

        if (!value) {
            return "";
        }

        return String(value)
            .replace("T", " ")
            .replace(
                /([+-]\d{2}:\d{2}|Z)(\[[^\]]+\])?$/,
                ""
            )
            .slice(
                0,
                16
            );
    }

    function restoreMzResult() {

        try {

            const stored =
                sessionStorage.getItem(
                    "mzSajuAnalysisResult"
                );

            if (!stored) {
                return;
            }

            renderMzSajuResult(
                normalizeMzSajuApiResponse(
                    JSON.parse(
                        stored
                    )
                )
            );

        } catch (error) {

            console.error(
                "MZ 천명도령 결과 복원 오류",
                error
            );
        }
    }

    function renderMzSajuResult(result) {

        const report =
            result.sajuReport
            || {};

        const sections =
            Array.isArray(
                report.sections
            )
                ? report.sections
                : [];

        const targetYear =
            result.targetYear
            || report.targetYear
            || new Date()
                .getFullYear();

        const fullChinese =
            safeText(
                result.fullSajuChinese
                || report.fullSajuChinese,
                "사주팔자 계산 결과"
            );

        const fullKorean =
            safeText(
                result.fullSajuKorean
                || report.fullSajuKorean,
                ""
            );

        const interaction =
            result.luckInteractionAnalysis
            || {};

        const combinedScore =
            safeText(
                interaction.combinedIntensityKorean
                || interaction.combinedScore,
                "-"
            );

        const combinedLevel =
            safeText(
                interaction.combinedLevelKorean,
                "운의 흐름 확인"
            );

        setText(
            "#mzResultTitle",
            sajuForm.name
            + "님, 사주 핵심부터 바로 볼게요"
        );

        setText(
            "#mzResultLead",
            targetYear
            + "년을 기준으로 원국·용신·현재 대운·세운을 한 번에 정리했습니다."
        );

        setText(
            "#mzResultOneLiner",
            createMzOneLiner(
                result
            )
        );

        setText(
            "#mzFullSajuChinese",
            fullChinese
        );

        setText(
            "#mzFullSajuKorean",
            fullKorean
        );

        setText(
            "#mzYearSignalTitle",
            targetYear
            + "년의 신호"
        );

        setText(
            "#mzCombinedScore",
            combinedScore
        );

        setText(
            "#mzCombinedLevel",
            combinedLevel
        );

        setText(
            "#mzCombinedMessage",
            createCombinedMessage(
                interaction
            )
        );

        setText(
            "#mzReportCount",
            sections.length
            + "개 보고서"
        );

        setText(
            "#mzResultDisclaimer",
            safeText(
                result.disclaimer,
                "본 결과는 명리 계산을 기반으로 제공되는 참고 정보이며 중요한 결정은 현실적인 조건과 함께 판단해야 합니다."
            )
        );

        renderMzQuickSummary(
            result
        );

        renderMzReportNavigation(
            sections
        );

        renderMzReportSections(
            sections
        );
    }

    function createMzOneLiner(result) {

        const strength =
            result.strengthAnalysis
            || {};

        const useful =
            result.usefulGodAnalysis
            || {};

        const primary =
            useful.primaryUsefulGod
            || {};

        const strengthName =
            safeText(
                strength.levelKorean
                || strength.strengthLevelKorean,
                "균형형"
            );

        const elementName =
            safeText(
                primary.elementKorean,
                "보완 기운"
            );

        return "핵심은 "
            + strengthName
            + "의 힘을 "
            + elementName
            + " 방식으로 살리는 겁니다. 무리하게 버티기보다 유리한 환경을 먼저 잡으세요.";
    }

    function createCombinedMessage(
        interaction
    ) {

        if (
            interaction
            && interaction.combinedMessage
        ) {

            return interaction.combinedMessage;
        }

        return "현재 대운과 세운의 상호작용을 확인해 행동 시점과 주의할 부분을 함께 살펴보세요.";
    }

    function renderMzQuickSummary(result) {

        const container =
            document.querySelector(
                "#mzQuickSummary"
            );

        if (!container) {
            return;
        }

        const strength =
            result.strengthAnalysis
            || {};

        const useful =
            result.usefulGodAnalysis
            || {};

        const luck =
            result.luckAnalysis
            || {};

        const interaction =
            result.luckInteractionAnalysis
            || {};

        const primary =
            useful.primaryUsefulGod
            || {};

        const secondary =
            useful.secondaryFavorableGod
            || {};

        const decade =
            luck.currentDecadeLuck
            || {};

        const annual =
            luck.annualLuck
            || {};

        const cards = [
            {
                label: "나를 움직이는 일간",
                value:
                    safeText(
                        result.dayMasterKorean
                        || result.dayMaster,
                        "-"
                    ),
                description:
                    joinNonEmpty(
                        [
                            result.dayMasterElementKorean,
                            result.dayMasterYinYangKorean
                        ],
                        " · "
                    )
            },
            {
                label: "기본 체력",
                value:
                    safeText(
                        strength.levelKorean
                        || strength.strengthLevelKorean
                        || strength.level,
                        "-"
                    ),
                description:
                    "일간과 주변 오행의 세력 비교"
            },
            {
                label: "1순위 용신",
                value:
                    safeText(
                        primary.elementKorean,
                        "-"
                    ),
                description:
                    safeText(
                        primary.typeKorean,
                        "핵심 보완 오행"
                    )
            },
            {
                label: "보조 희신",
                value:
                    safeText(
                        secondary.elementKorean,
                        "-"
                    ),
                description:
                    safeText(
                        secondary.typeKorean,
                        "용신을 돕는 오행"
                    )
            },
            {
                label: "현재 대운",
                value:
                    safeText(
                        decade.pillar
                        && decade.pillar.koreanName,
                        "계산 범위 외"
                    ),
                description:
                    decade.startDate
                    && decade.endDate
                        ? decade.startDate
                            + " ~ "
                            + decade.endDate
                        : safeText(
                            luck.directionKorean,
                            "장기 흐름"
                        )
            },
            {
                label:
                    safeText(
                        annual.targetYear
                        || result.targetYear,
                        new Date()
                            .getFullYear()
                    )
                    + "년 세운",
                value:
                    safeText(
                        annual.pillar
                        && annual.pillar.koreanName,
                        safeText(
                            interaction.combinedIntensityKorean,
                            "연간 흐름"
                        )
                    ),
                description:
                    safeText(
                        interaction.combinedLevelKorean,
                        "연간 흐름"
                    )
            }
        ];

        container.innerHTML =
            cards.map(
                function (card) {

                    return `
                        <article class="mz-quick-card">

                            <span>
                                ${escapeHtml(card.label)}
                            </span>

                            <strong>
                                ${escapeHtml(card.value)}
                            </strong>

                            <small>
                                ${escapeHtml(card.description)}
                            </small>

                        </article>
                    `;
                }
            ).join("");
    }

    function renderMzReportNavigation(
        sections
    ) {

        const navigation =
            document.querySelector(
                "#mzReportNavigation"
            );

        if (!navigation) {
            return;
        }

        navigation.innerHTML =
            sections.map(
                function (
                    section,
                    index
                ) {

                    const id =
                        createMzSectionId(
                            section,
                            index
                        );

                    return `
                        <button type="button"
                                data-mz-report-target="${escapeHtml(id)}">
                            ${escapeHtml(
                                safeText(
                                    section.categoryKorean,
                                    "상세 "
                                    + (index + 1)
                                )
                            )}
                        </button>
                    `;
                }
            ).join("");
    }

    function renderMzReportSections(
        sections
    ) {

        const container =
            document.querySelector(
                "#mzReportList"
            );

        if (!container) {
            return;
        }

        if (!sections.length) {

            container.innerHTML =
                `
                <div class="mz-report-empty">
                    상세 보고서 데이터가 없습니다.<br>
                    백엔드 응답의 consultations를 확인해주세요.
                </div>
                `;

            return;
        }

        container.innerHTML =
            sections.map(
                function (
                    section,
                    index
                ) {

                    const id =
                        createMzSectionId(
                            section,
                            index
                        );

                    return `
                        <details class="mz-report-card"
                                 id="${escapeHtml(id)}"
                                 ${index === 0
                                    ? "open"
                                    : ""}>

                            <summary>

                                <span class="mz-report-number">
                                    ${String(index + 1)
                                        .padStart(
                                            2,
                                            "0"
                                        )}
                                </span>

                                <span class="mz-report-heading">

                                    <span>
                                        ${escapeHtml(
                                            safeText(
                                                section.categoryKorean,
                                                "상세 해석"
                                            )
                                        )}
                                    </span>

                                    <strong>
                                        ${escapeHtml(
                                            safeText(
                                                section.title
                                                || section.categoryKorean,
                                                "상세 해석"
                                            )
                                        )}
                                    </strong>

                                </span>

                                <span class="mz-report-arrow">
                                    ⌄
                                </span>

                            </summary>

                            <div class="mz-report-body">

                                <p class="mz-report-core">
                                    ${escapeHtml(
                                        safeText(
                                            section.coreSummary,
                                            "핵심 신호를 확인해보세요."
                                        )
                                    )}
                                </p>

                                <div class="mz-report-content">
                                    ${formatMzReportContent(
                                        section.content
                                    )}
                                </div>

                                ${renderMzScores(
                                    section
                                )}

                            </div>

                        </details>
                    `;
                }
            ).join("");
    }

    function renderMzScores(section) {

        const signals = [
            {
                label: "방향",
                value:
                    safeText(
                        section.polarityKorean,
                        "중립"
                    )
            },
            {
                label: "강도",
                value:
                    safeText(
                        section.intensityKorean,
                        "-"
                    )
            },
            {
                label: "신뢰도",
                value:
                    safeText(
                        section.confidenceKorean,
                        "-"
                    )
            }
        ];

        return `
            <div class="mz-report-scores">
                ${signals.map(
                    function (signal) {

                        return `
                            <div class="mz-report-score">
                                <span>
                                    ${escapeHtml(signal.label)}
                                </span>
                                <strong>
                                    ${escapeHtml(signal.value)}
                                </strong>
                            </div>
                        `;
                    }
                ).join("")}
            </div>
        `;
    }

    function formatMzReportContent(content) {

        return safeText(
            content,
            "상세 해석 내용이 없습니다."
        )
            .split(
                /\n{2,}/
            )
            .map(
                function (paragraph) {

                    return paragraph.trim();
                }
            )
            .filter(Boolean)
            .map(
                function (paragraph) {

                    return "<p>"
                        + escapeHtml(
                            paragraph
                        )
                        + "</p>";
                }
            )
            .join("");
    }

    function createMzSectionId(
        section,
        index
    ) {

        return (
            "mz-report-"
            + safeText(
                section.category,
                "section-"
                + index
            )
        )
            .toLowerCase()
            .replace(
                /[^a-z0-9_-]/g,
                "-"
            );
    }

    function bindMzReportEvents() {

        document.addEventListener(
            "click",
            function (event) {

                const button =
                    event.target.closest(
                        "[data-mz-report-target]"
                    );

                if (!button) {
                    return;
                }

                const target =
                    document.getElementById(
                        button.dataset
                            .mzReportTarget
                    );

                if (!target) {
                    return;
                }

                target.open =
                    true;

                target.scrollIntoView({
                    behavior: "smooth",
                    block: "start"
                });
            }
        );
    }

    function renderSajuResult(result) {

        const data =
            createStoryData(
                result
            );

        setText(
            "#resultTitle",
            data.name
            + "님의 사주 이야기"
        );

        setText(
            "#resultSummary",
            data.coverSummary
        );

        setText(
            "#resultDisclaimer",

            safeText(
                result.disclaimer,

                "본 결과는 만세력 계산을 기반으로 제공되는 참고 정보입니다."
            )
        );

        renderWebtoonStory(
            result,
            data
        );
    }

    function createStoryData(result) {

        const interpretations =
            Array.isArray(
                result.interpretations
            )
                ? result.interpretations
                : [];

        const name =
            safeText(
                result.name,
                "당신"
            );

        const dayMaster =
            safeText(
                result.dayMaster,
                "일간"
            );

        const zodiac =
            safeText(
                result.zodiac,
                "띠"
            );

        const elementBalance =
            result.elementBalance
            || {};

        const strength =
            result.strength
            || {};

        const luck =
            result.luck
            || {};

        const dominant =
            safeText(
                elementBalance.dominant,
                "강한 기운"
            );

        const weakest =
            safeText(
                elementBalance.weakest,
                "부족한 기운"
            );

        const strengthType =
            safeText(
                strength.classification,
                "균형형"
            );

        const usefulGod =
            safeText(
                strength.usefulGod,
                "보완 기운"
            );

        const currentDecade =
            safeText(
                luck.currentDecade,
                "현재 대운"
            );

        const personality =
            findInterpretationContent(
                interpretations,
                [
                    "성격",
                    "기질",
                    "성향",
                    "자아"
                ],
                name
                + "님은 자기 기준이 분명하고, 한번 정한 방향을 쉽게 포기하지 않는 편입니다."
            );

        const innerMind =
            findInterpretationContent(
                interpretations,
                [
                    "내면",
                    "감정",
                    "심리"
                ],
                "겉으로는 단단해 보여도 속으로는 생각이 많습니다."
            );

        const relationship =
            findInterpretationContent(
                interpretations,
                [
                    "인간관계",
                    "대인관계",
                    "관계"
                ],
                name
                + "님은 신뢰할 수 있는 관계를 오래 지키는 편입니다."
            );

        const love =
            findInterpretationContent(
                interpretations,
                [
                    "연애",
                    "애정",
                    "결혼"
                ],
                "연애에서는 진심과 안정감을 중요하게 봅니다."
            );

        const money =
            findInterpretationContent(
                interpretations,
                [
                    "재물",
                    "금전",
                    "돈"
                ],
                "재물운은 한 번의 큰 승부보다 안정적으로 키우는 흐름에 가깝습니다."
            );

        const career =
            findInterpretationContent(
                interpretations,
                [
                    "직업",
                    "진로",
                    "일",
                    "사업",
                    "창업"
                ],
                "전문성을 쌓고 직접 판단할 수 있는 분야에서 장점이 살아납니다."
            );

        const study =
            findInterpretationContent(
                interpretations,
                [
                    "시험",
                    "학업",
                    "공부"
                ],
                "시험과 학습은 반복 훈련과 실전 감각을 쌓을수록 좋습니다."
            );

        const health =
            findInterpretationContent(
                interpretations,
                [
                    "건강",
                    "생활",
                    "컨디션"
                ],
                "수면과 휴식, 식사 시간을 일정하게 관리하는 것이 중요합니다."
            );

        const overall =
            findInterpretationContent(
                interpretations,
                [
                    "종합",
                    "총평",
                    "조언"
                ],
                name
                + "님의 사주는 힘을 어디에 쓰느냐에 따라 결과 차이가 크게 나타나는 구조입니다."
            );

        const luckNote =
            safeText(
                luck.note,
                "지금은 방향을 정하고 실력과 기반을 쌓는 과정이 중요합니다."
            );

        return {
            name: name,
            dayMaster: dayMaster,
            zodiac: zodiac,
            dominant: dominant,
            weakest: weakest,
            strengthType: strengthType,
            usefulGod: usefulGod,
            currentDecade: currentDecade,
            personality: personality,
            innerMind: innerMind,
            relationship: relationship,
            love: love,
            money: money,
            career: career,
            study: study,
            health: health,
            overall: overall,
            luckNote: luckNote,
            coverSummary:
                firstSentence(
                    personality,
                    105
                )
        };
    }

    function renderWebtoonStory(
        result,
        data
    ) {

        const container =
            document.querySelector(
                "#webtoonResult"
            );

        const episodes =
            buildEpisodes(
                result,
                data
            );

        container.innerHTML =
            episodes.map(
                function (
                    episode,
                    index
                ) {

                    return renderEpisode(
                        episode,
                        index
                    );
                }
            ).join("");

        bindStoryNotes();

        prepareWebtoonReveal();
    }

    function buildEpisodes(
        result,
        data
    ) {

        return [];
    }

    function renderEpisode(
        episode,
        index
    ) {

        return "";
    }

    function renderCharacterEpisode(
        episode,
        number,
        className
    ) {

        const paragraphs =
            episode.dialogue.map(
                function (text) {

                    return (
                        "<p>"
                        + escapeHtml(text)
                        + "</p>"
                    );
                }
            ).join("");

        return `
            <article class="${className}">

                <img class="scene-image ${escapeHtml(episode.imageClass)}"
                     src="/img/saju-character.png"
                     alt="관상선생">

                <div class="scene-shade">
                </div>

                ${renderSceneHeading(
                    episode,
                    number
                )}

                <div class="scene-dialogue-layer">

                    <div class="comic-bubble ${escapeHtml(episode.bubbleSide)}">

                        <strong>
                            관상선생
                        </strong>

                        ${paragraphs}

                    </div>

                    <div class="comic-caption">
                        ${episode.caption}
                    </div>

                    ${renderEvidence(
                        episode.evidence
                    )}

                </div>

            </article>
        `;
    }

    function renderSceneHeading(
        episode,
        number
    ) {

        return `
            <div class="scene-heading">

                <div class="scene-heading-copy">

                    <span class="scene-kicker">
                        ${escapeHtml(episode.kicker)}
                    </span>

                    <h2 class="scene-title">
                        ${escapeHtml(episode.title)}
                    </h2>

                </div>

                <span class="scene-number">
                    ${number}
                </span>

            </div>
        `;
    }

    function renderEvidence(
        evidence
    ) {

        if (!evidence) {
            return "";
        }

        const items =
            arrayOrEmpty(
                evidence.items
            )
            .map(
                function (item) {

                    return `
                        <div class="evidence-item">

                            <span>
                                ${escapeHtml(item.label)}
                            </span>

                            <strong title="${escapeHtml(item.value)}">
                                ${escapeHtml(item.value)}
                            </strong>

                        </div>
                    `;
                }
            )
            .join("");

        return `
            <details class="story-note">

                <summary>

                    <div>

                        <strong>
                            ${escapeHtml(evidence.title)}
                        </strong>

                        <small>
                            ${escapeHtml(evidence.subtitle)}
                        </small>

                    </div>

                    <span class="story-note-arrow">
                        ⌄
                    </span>

                </summary>

                <div class="story-note-body">

                    <div class="evidence-grid">
                        ${items}
                    </div>

                    <p class="evidence-explain">
                        ${escapeHtml(evidence.explain)}
                    </p>

                </div>

            </details>
        `;
    }

    function bindStoryNotes() {

        document
            .querySelectorAll(
                "#webtoonResult .story-note"
            )
            .forEach(
                function (details) {

                    details.addEventListener(
                        "toggle",
                        function () {

                            if (!details.open) {
                                return;
                            }

                            const episode =
                                details.closest(
                                    ".webtoon-episode"
                                );

                            if (!episode) {
                                return;
                            }

                            episode
                                .querySelectorAll(
                                    ".story-note[open]"
                                )
                                .forEach(
                                    function (other) {

                                        if (
                                            other
                                            !== details
                                        ) {

                                            other.open =
                                                false;
                                        }
                                    }
                                );
                        }
                    );
                }
            );
    }

    function prepareWebtoonReveal() {

        stopWebtoonReveal();

        document
            .querySelectorAll(
                "#webtoonResult .webtoon-episode"
            )
            .forEach(
                function (episode) {

                    episode.classList.remove(
                        "is-visible"
                    );
                }
            );
    }

    function startWebtoonReveal() {

        stopWebtoonReveal();

        const episodes =
            Array.from(
                document.querySelectorAll(
                    "#webtoonResult .webtoon-episode"
                )
            );

        if (!episodes.length) {
            return;
        }

        if (
            !("IntersectionObserver" in window)
        ) {

            episodes.forEach(
                function (episode) {

                    episode.classList.add(
                        "is-visible"
                    );
                }
            );

            return;
        }

        webtoonObserver =
            new IntersectionObserver(

                function (entries) {

                    entries.forEach(
                        function (entry) {

                            if (
                                !entry.isIntersecting
                            ) {
                                return;
                            }

                            entry.target.classList.add(
                                "is-visible"
                            );

                            webtoonObserver.unobserve(
                                entry.target
                            );
                        }
                    );
                },

                {
                    threshold: 0.1,

                    rootMargin:
                        "0px 0px -8% 0px"
                }
            );

        episodes.forEach(
            function (episode) {

                webtoonObserver.observe(
                    episode
                );
            }
        );
    }

    function stopWebtoonReveal() {

        if (webtoonObserver) {

            webtoonObserver.disconnect();

            webtoonObserver =
                null;
        }
    }

    function findInterpretationContent(
        interpretations,
        keywords,
        fallback
    ) {

        const found =
            interpretations.find(
                function (item) {

                    const category =
                        safeText(
                            item.category
                        ).toLowerCase();

                    const title =
                        safeText(
                            item.title
                        ).toLowerCase();

                    return keywords.some(
                        function (keyword) {

                            const normalized =
                                keyword.toLowerCase();

                            return (
                                category.includes(
                                    normalized
                                )

                                || title.includes(
                                    normalized
                                )
                            );
                        }
                    );
                }
            );

        return found

            ? safeText(
                found.content,
                fallback
            )

            : fallback;
    }

    function arrayOrEmpty(
        value
    ) {

        return Array.isArray(
            value
        )

            ? value

            : [];
    }

    function safeText(
        value,
        fallback
    ) {

        if (
            value === null
            || value === undefined
            || value === ""
        ) {

            return fallback || "";
        }

        return String(value);
    }

    function setText(
        selector,
        value
    ) {

        const element =
            document.querySelector(
                selector
            );

        if (element) {

            element.textContent =
                value === null
                || value === undefined

                    ? ""

                    : String(value);
        }
    }

    function wait(
        milliseconds
    ) {

        return new Promise(
            function (resolve) {

                setTimeout(
                    resolve,
                    milliseconds
                );
            }
        );
    }

    async function readJsonResponse(
        response
    ) {

        try {

            return await response.json();

        } catch (error) {

            return {
                message:
                    "서버 응답을 확인할 수 없습니다."
            };
        }
    }

    function firstSentence(
        text,
        maximumLength
    ) {

        const normalized =
            safeText(text)
                .replace(
                    /\s+/g,
                    " "
                )
                .trim();

        if (
            normalized.length
            <= maximumLength
        ) {

            return normalized;
        }

        return normalized.substring(
            0,
            maximumLength
        ).trim() + "…";
    }

    function joinNonEmpty(
        values,
        separator
    ) {

        return values
            .filter(
                function (value) {

                    return value !== undefined
                        && value !== null
                        && value !== "";
                }
            )
            .join(
                separator
            );
    }

    function escapeHtml(
        value
    ) {

        if (
            value === null
            || value === undefined
        ) {

            return "";
        }

        const element =
            document.createElement(
                "div"
            );

        element.textContent =
            String(value);

        return element.innerHTML;
    }
});