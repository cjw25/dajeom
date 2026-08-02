document.addEventListener("DOMContentLoaded", function () {

    const MINIMUM_ANALYSIS_LOADING_TIME = 4200;
    const MAX_WAITING_PROGRESS = 94;

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

        bindNameEvents();

        bindBirthEvents();

        bindProfileEvents();

        bindAnalysisEvents();

        restoreNameForm();

        startAnalysisButton.disabled =
            false;

        startAnalysisButton.textContent =
            "사주 분석 시작";

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
            currentScreen === "result"
            && screenName !== "result"
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
            "당신의 사주 이야기가 완성되었습니다.";

        analysisProgressStatus.textContent =
            "웹툰 결과 완성";

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

        if (progress >= 25) {

            activeStep =
                1;

            message =
                "년주·월주·일주·시주와 오행을 계산하고 있습니다.";

            status =
                "사주팔자 계산";
        }

        if (progress >= 55) {

            activeStep =
                2;

            message =
                "십성·관계·신살과 운의 흐름을 분석하고 있습니다.";

            status =
                "삶의 흐름 분석";
        }

        if (progress >= 82) {

            activeStep =
                3;

            message =
                "계산값을 소비자가 이해하기 쉬운 웹툰 장면으로 바꾸고 있습니다.";

            status =
                "웹툰 이야기 구성";
        }

        if (progress >= 100) {

            message =
                "모든 장면이 완성되었습니다.";

            status =
                "웹툰 결과 완성";
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
                + "님은 자기 기준이 분명하고, 한번 정한 방향을 쉽게 포기하지 않는 편입니다. "
                + "스스로 납득해야 움직이며, 마음이 정해지면 오래 밀고 가는 힘이 있습니다."
            );

        const innerMind =
            findInterpretationContent(

                interpretations,

                [
                    "내면",
                    "감정",
                    "심리"
                ],

                "겉으로는 단단해 보여도 속으로는 생각이 많습니다. "
                + "감정을 바로 꺼내기보다 혼자 정리하려는 경향이 있어, "
                + "주변에서는 차분하거나 속을 알기 어렵다고 느낄 수 있습니다."
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
                + "님은 사람을 쉽게 넓게 사귀기보다 신뢰할 수 있는 관계를 오래 지키는 편입니다. "
                + "가까운 사이일수록 솔직한 표현과 거리 조절이 중요합니다."
            );

        const love =
            findInterpretationContent(

                interpretations,

                [
                    "연애",
                    "애정",
                    "결혼"
                ],

                "연애에서는 순간적인 설렘보다 진심과 안정감을 중요하게 봅니다. "
                + "마음을 여는 데는 시간이 걸리지만, 믿음이 생기면 생각보다 깊고 오래 가는 타입입니다."
            );

        const money =
            findInterpretationContent(

                interpretations,

                [
                    "재물",
                    "금전",
                    "돈"
                ],

                "재물운은 한 번의 큰 승부보다 실력과 경험을 쌓아 안정적으로 키우는 흐름에 가깝습니다. "
                + "돈이 들어오는 것만큼 지출 기준을 세우는 일이 중요합니다."
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

                "남이 정해준 틀에서 반복만 하는 일보다 스스로 판단하고 전문성을 쌓는 분야에서 힘이 잘 붙습니다. "
                + "책임과 기술, 분석력이 필요한 일에서 장점이 살아납니다."
            );

        const study =
            findInterpretationContent(

                interpretations,

                [
                    "시험",
                    "학업",
                    "공부"
                ],

                "시험과 학습은 짧은 벼락치기보다 반복 훈련과 실전 감각을 쌓을수록 성과가 좋아지는 흐름입니다."
            );

        const health =
            findInterpretationContent(

                interpretations,

                [
                    "건강",
                    "생활",
                    "컨디션"
                ],

                "운이 흔들릴 때 가장 먼저 생활 리듬이 무너지기 쉽습니다. "
                + "수면, 휴식, 식사 시간을 일정하게 유지하는 것이 전체 흐름을 안정시키는 데 도움이 됩니다."
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
                + "님의 사주는 약해서 흔들리는 구조라기보다, "
                + "힘을 어디에 쓰느냐에 따라 결과 차이가 크게 벌어지는 구조에 가깝습니다."
            );

        const luckNote =
            safeText(

                luck.note,

                "지금은 결과를 급하게 만들기보다 방향을 정하고 "
                + "실력과 기반을 쌓는 과정이 중요한 시기입니다."
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

        const elementItems =
            arrayOrEmpty(
                result.elementBalance
                && result.elementBalance.items
            );

        const yinYang =
            result.yinYangBalance
            || {};

        const relations =
            result.relations
            || {};

        const allRelations =
            arrayOrEmpty(
                relations.heavenlyStemRelations
            )
            .concat(
                arrayOrEmpty(
                    relations.earthlyBranchRelations
                )
            );

        const shinsal =
            arrayOrEmpty(
                result.shinsal
            );

        const luck =
            result.luck
            || {};

        const calendar =
            result.calendar
            || {};

        const strength =
            result.strength
            || {};

        const structure =
            result.structure
            || {};

        const pillars =
            arrayOrEmpty(
                result.pillars
            );

        return [

            {
                type: "character",
                className: "scene-tall",
                kicker: "PROLOGUE",
                title: "당신의 사주를 펼쳐봅니다",
                imageClass: "focus-wide",
                bubbleSide: "left",

                dialogue: [

                    data.name
                    + "님, 지금부터 사주에 담긴 이야기를 한 장면씩 살펴보겠습니다.",

                    "어려운 용어를 외우기보다 실제 삶에서 어떤 모습으로 나타나는지에 집중해볼게요."
                ],

                caption:
                    "<strong>이 이야기는</strong> 입력한 생년월일과 출생시간으로 계산한 만세력 결과를 바탕으로 구성됩니다.",

                evidence: {

                    title:
                        "첫 장면의 계산 기록",

                    subtitle:
                        "입력값과 기본 명식",

                    items: [

                        evidenceItem(
                            "양력",
                            calendar.solarDate
                        ),

                        evidenceItem(
                            "음력",
                            calendar.lunarDate
                        ),

                        evidenceItem(
                            "태어난 시간",
                            calendar.birthTime
                        ),

                        evidenceItem(
                            "띠",
                            data.zodiac
                        )
                    ],

                    explain:
                        "이 기본정보를 기준으로 년주·월주·일주·시주와 이후의 모든 해석이 계산됩니다."
                }
            },

            {
                type: "character",
                className: "scene-tall",
                kicker: "EPISODE 01",
                title: "나는 어떤 사람인가",
                imageClass: "focus-right",
                bubbleSide: "right",

                dialogue: [
                    data.personality,
                    data.innerMind
                ],

                caption:
                    "<strong>핵심:</strong> 겉으로 드러나는 힘과 실제 내면의 속도는 다를 수 있습니다.",

                evidence: {

                    title:
                        "왜 이렇게 해석했나요?",

                    subtitle:
                        "일간·신강신약·십성 근거",

                    items: [

                        evidenceItem(
                            "일간",
                            data.dayMaster
                        ),

                        evidenceItem(
                            "일간 오행",
                            strength.dayMasterElement
                        ),

                        evidenceItem(
                            "신강·신약",
                            data.strengthType
                        ),

                        evidenceItem(
                            "주요 십성",

                            result.tenGodBalance
                            && result.tenGodBalance.dominant
                        )
                    ],

                    explain:
                        data.dayMaster
                        + "의 성향과 "
                        + data.strengthType
                        + " 구조, 주요 십성의 작용을 함께 보고 기본 성향을 풀었습니다."
                }
            },

            {
                type: "element",
                className: "scene-paper scene-dark",
                kicker: "EPISODE 02",
                title: "마음속 기운은 어떻게 흐를까",

                lines: [

                    data.innerMind,

                    "가장 강하게 드러나는 기운은 "
                    + data.dominant
                    + "이고, 상대적으로 보완이 필요한 기운은 "
                    + data.weakest
                    + "입니다.",

                    data.health
                ],

                elementItems:
                    elementItems,

                evidence: {

                    title:
                        "오행과 음양 기록",

                    subtitle:
                        "기운의 강약과 보완 방향",

                    items: [

                        evidenceItem(
                            "강한 오행",
                            data.dominant
                        ),

                        evidenceItem(
                            "부족 오행",
                            data.weakest
                        ),

                        evidenceItem(
                            "양 비율",
                            percentageText(
                                yinYang.yangPercentage
                            )
                        ),

                        evidenceItem(
                            "음 비율",
                            percentageText(
                                yinYang.yinPercentage
                            )
                        ),

                        evidenceItem(
                            "용신 보조값",
                            strength.usefulGod
                        ),

                        evidenceItem(
                            "희신 보조값",
                            strength.favorableGod
                        )
                    ],

                    explain:
                        "오행 분포와 음양 비율은 감정 표현, 행동 속도, 생활 리듬의 균형을 해석할 때 참고하는 계산 근거입니다."
                }
            },

            {
                type: "character",
                className: "scene-tall",
                kicker: "EPISODE 03",
                title: "사람과 사랑 앞에서의 모습",
                imageClass: "focus-left",
                bubbleSide: "left",

                dialogue: [
                    data.relationship,
                    data.love
                ],

                caption:
                    "<strong>관계의 열쇠:</strong> 마음을 숨기기보다 필요한 순간에 정확히 표현하는 것이 중요합니다.",

                evidence: {

                    title:
                        "관계 해석의 계산 기록",

                    subtitle:
                        "십성·합충·신살 근거",

                    items: [

                        evidenceItem(
                            "주요 십성",

                            result.tenGodBalance
                            && result.tenGodBalance.dominant
                        ),

                        evidenceItem(
                            "관계 작용",

                            allRelations.length
                                ? allRelations[0].category
                                : "두드러진 작용 없음"
                        ),

                        evidenceItem(
                            "주요 신살",

                            shinsal.length
                                ? shinsal[0].name
                                : "두드러진 신살 없음"
                        ),

                        evidenceItem(
                            "일지 기준",

                            getPillarText(
                                pillars,
                                "일주"
                            )
                        )
                    ],

                    explain:
                        "십성의 분포와 지지 사이의 관계, 신살을 함께 참고해 가까운 관계에서 반복되기 쉬운 모습을 풀었습니다."
                }
            },

            {
                type: "narration",
                className: "scene-paper scene-warm",
                kicker: "EPISODE 04",
                title: "돈은 어떻게 들어오고 새어 나갈까",

                lines: [

                    data.money,

                    "빠른 결과를 좇기보다 자신의 실력과 기준을 자산으로 바꿀 때 재물운이 더 안정적으로 이어집니다.",

                    "수입이 늘어나는 시기일수록 지출 기준과 비상자금을 먼저 정해두는 편이 좋습니다."
                ],

                accent:
                    "한 번의 큰 승부보다 오래 유지되는 구조를 만드는 것이 핵심입니다.",

                evidence: {

                    title:
                        "재물 해석의 계산 기록",

                    subtitle:
                        "재성·오행·용신 근거",

                    items: [

                        evidenceItem(
                            "강한 오행",
                            data.dominant
                        ),

                        evidenceItem(
                            "부족 오행",
                            data.weakest
                        ),

                        evidenceItem(
                            "주요 십성",

                            result.tenGodBalance
                            && result.tenGodBalance.dominant
                        ),

                        evidenceItem(
                            "용신 보조값",
                            data.usefulGod
                        )
                    ],

                    explain:
                        "재물 해석은 재성 하나만 단독으로 보지 않고 오행의 균형, 일간의 힘, 현재 운의 흐름을 함께 참고합니다."
                }
            },

            {
                type: "character",
                className: "scene-tall",
                kicker: "EPISODE 05",
                title: "일에서 능력이 살아나는 순간",
                imageClass: "focus-close",
                bubbleSide: "right",

                dialogue: [
                    data.career,
                    data.study
                ],

                caption:
                    "<strong>직업의 방향:</strong> 판단력·전문성·책임을 사용할수록 자신만의 경쟁력이 커집니다.",

                evidence: {

                    title:
                        "직업과 시험 해석 기록",

                    subtitle:
                        "십성·신강신약·대운 근거",

                    items: [

                        evidenceItem(
                            "신강·신약",
                            data.strengthType
                        ),

                        evidenceItem(
                            "주요 십성",

                            result.tenGodBalance
                            && result.tenGodBalance.dominant
                        ),

                        evidenceItem(
                            "현재 대운",
                            data.currentDecade
                        ),

                        evidenceItem(
                            "기신 보조값",
                            strength.unfavorableGod
                        )
                    ],

                    explain:
                        "직업운은 타고난 성향과 현재 대운이 만나는 지점을 보고, 어떤 방식의 일이 오래 지속될 수 있는지 중심으로 풀었습니다."
                }
            },

            {
                type: "relations",
                className: "scene-paper scene-pink",
                kicker: "EPISODE 06",
                title: "기운이 만나고 부딪히는 장면",

                lines: [

                    "사주의 기운들은 서로 따로 움직이지 않습니다.",

                    "어떤 기운은 만나 힘을 합치고, 어떤 기운은 부딪히며 변화와 긴장을 만듭니다."
                ],

                relations:
                    allRelations,

                shinsal:
                    shinsal,

                evidence: {

                    title:
                        "합·충·형·파·해와 신살 기록",

                    subtitle:
                        "명식 안의 관계 작용",

                    items: [

                        evidenceItem(
                            "관계 개수",
                            allRelations.length
                            + "개"
                        ),

                        evidenceItem(
                            "신살 개수",
                            shinsal.length
                            + "개"
                        ),

                        evidenceItem(
                            "대표 관계",

                            allRelations.length
                                ? allRelations[0].category
                                : "없음"
                        ),

                        evidenceItem(
                            "대표 신살",

                            shinsal.length
                                ? shinsal[0].name
                                : "없음"
                        )
                    ],

                    explain:
                        "이 항목은 사건을 단정하는 예언이 아니라, 갈등·변화·인연이 어떤 방식으로 체감될 수 있는지 보는 보조 근거입니다."
                }
            },

            {
                type: "character",
                className: "scene-tall",
                kicker: "EPISODE 07",
                title: "지금 지나가는 큰 운의 흐름",
                imageClass: "focus-wide",
                bubbleSide: "left",

                dialogue: [

                    "현재 "
                    + data.name
                    + "님에게는 "
                    + data.currentDecade
                    + "의 흐름이 들어와 있습니다.",

                    data.luckNote
                ],

                caption:
                    "<strong>현재의 선택:</strong> 조급하게 결론을 만들기보다 지금 쌓는 경험이 다음 흐름의 기반이 됩니다.",

                evidence: {

                    title:
                        "현재 대운 계산 기록",

                    subtitle:
                        "운행 방향과 시작 시점",

                    items: [

                        evidenceItem(
                            "현재 대운",
                            data.currentDecade
                        ),

                        evidenceItem(
                            "운행 방향",
                            luck.direction
                        ),

                        evidenceItem(
                            "기운 시점",
                            luck.startAge
                        ),

                        evidenceItem(
                            "대운 시작일",
                            luck.startDate
                        )
                    ],

                    explain:
                        "대운은 약 10년 단위의 큰 환경 변화를 보는 흐름이며, 실제 체감은 개인의 선택과 현재 상황에 따라 달라질 수 있습니다."
                }
            },

            {
                type: "luck",
                className: "scene-paper scene-dark",
                kicker: "EPISODE 08",
                title: "시간의 길 위에서 보는 대운과 세운",

                lines: [

                    "큰 흐름은 대운으로, 해마다 달라지는 기운은 세운으로 읽습니다.",

                    "현재 구간을 중심으로 앞뒤 흐름을 비교하면 준비할 때와 움직일 때를 더 쉽게 구분할 수 있습니다."
                ],

                luck:
                    luck,

                evidence: {

                    title:
                        "대운·세운 계산 기록",

                    subtitle:
                        "10년 흐름과 연도별 변화",

                    items: [

                        evidenceItem(
                            "현재 대운",
                            data.currentDecade
                        ),

                        evidenceItem(
                            "대운 수",

                            arrayOrEmpty(
                                luck.decadeFortunes
                            ).length
                            + "개"
                        ),

                        evidenceItem(
                            "운행 방향",
                            luck.direction
                        ),

                        evidenceItem(
                            "시작 나이",
                            luck.startAge
                        )
                    ],

                    explain:
                        "각 연도의 간지와 십성, 공망 등의 값을 함께 보여주되, 결과는 가능성과 경향성으로 받아들이는 것이 좋습니다."
                }
            },

            {
                type: "ledger",
                className: "scene-paper scene-warm",
                kicker: "EPISODE 09",
                title: "관상선생의 비밀 장부",

                lines: [

                    "이제 이야기 뒤에 숨은 명식 전체를 한 번에 펼쳐보겠습니다.",

                    "일반 소비자는 여기까지 읽지 않아도 충분하지만, 계산값이 궁금하다면 아래 장부를 열어볼 수 있습니다."
                ],

                calendar:
                    calendar,

                pillars:
                    pillars,

                structure:
                    structure,

                strength:
                    strength
            },

            {
                type: "ending",
                className: "scene-paper scene-pink",
                kicker: "ENDING",
                title: "사주는 정해진 결말이 아닙니다",

                lines: [

                    data.overall,

                    data.name
                    + "님에게 중요한 것은 강한 기운을 한 방향에 집중하고, "
                    + "부족한 "
                    + data.weakest
                    + "의 균형을 생활 속에서 보완하는 일입니다.",

                    "중요한 결정은 감정이 가장 높을 때보다 생각을 한 번 정리한 뒤 실행하세요. "
                    + "그러면 사주의 장점이 더 안정적으로 살아납니다."
                ],

                accent:
                    "사주는 미래를 고정하는 답이 아니라, 자신을 이해하고 선택을 조정하는 지도입니다."
            }
        ];
    }

    function renderEpisode(
        episode,
        index
    ) {

        const number =
            String(index + 1)
                .padStart(
                    2,
                    "0"
                );

        const className =
            "webtoon-episode "
            + safeText(
                episode.className
            );

        if (
            episode.type
            === "character"
        ) {

            return renderCharacterEpisode(
                episode,
                number,
                className
            );
        }

        if (
            episode.type
            === "element"
        ) {

            return renderElementEpisode(
                episode,
                number,
                className
            );
        }

        if (
            episode.type
            === "relations"
        ) {

            return renderRelationsEpisode(
                episode,
                number,
                className
            );
        }

        if (
            episode.type
            === "luck"
        ) {

            return renderLuckEpisode(
                episode,
                number,
                className
            );
        }

        if (
            episode.type
            === "ledger"
        ) {

            return renderLedgerEpisode(
                episode,
                number,
                className
            );
        }

        return renderNarrationEpisode(
            episode,
            number,
            className
        );
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

    function renderNarrationEpisode(
        episode,
        number,
        className
    ) {

        const lines =
            episode.lines.map(
                function (text) {

                    return `
                        <div class="narration-line">

                            <p>
                                ${escapeHtml(text)}
                            </p>

                        </div>
                    `;
                }
            ).join("");

        const accent =
            episode.accent

                ? `
                    <div class="narration-line accent">

                        <p>
                            ${escapeHtml(episode.accent)}
                        </p>

                    </div>
                `

                : "";

        return `
            <article class="${className}">

                ${renderSceneHeading(
                    episode,
                    number
                )}

                <div class="narration-card">

                    ${lines}

                    ${accent}

                    ${renderEvidence(
                        episode.evidence
                    )}

                </div>

            </article>
        `;
    }

    function renderElementEpisode(
        episode,
        number,
        className
    ) {

        const lines =
            episode.lines.map(
                function (text) {

                    return `
                        <div class="narration-line">

                            <p>
                                ${escapeHtml(text)}
                            </p>

                        </div>
                    `;
                }
            ).join("");

        return `
            <article class="${className}">

                ${renderSceneHeading(
                    episode,
                    number
                )}

                <div class="narration-card">

                    ${lines}

                    <div class="narration-line accent">

                        <p>
                            오행은 좋고 나쁨을 나누는 점수가 아니라,
                            어떤 기운을 많이 쓰고 어떤 부분에서
                            균형이 필요한지 보여주는 지도입니다.
                        </p>

                    </div>

                    ${renderElementBoard(
                        episode.elementItems
                    )}

                    ${renderEvidence(
                        episode.evidence
                    )}

                </div>

            </article>
        `;
    }

    function renderRelationsEpisode(
        episode,
        number,
        className
    ) {

        const relationsHtml =
            episode.relations.length

                ? episode.relations.map(
                    function (item) {

                        return `
                            <div class="relation-item">

                                <strong>
                                    ${escapeHtml(item.category)}
                                    ·
                                    ${escapeHtml(item.members)}
                                </strong>

                                <p>
                                    ${escapeHtml(item.description)}
                                </p>

                            </div>
                        `;
                    }
                ).join("")

                : `
                    <div class="relation-item">

                        <strong>
                            두드러진 관계 작용 없음
                        </strong>

                        <p>
                            현재 계산 기준에서 강하게 표시되는
                            합·충·형·파·해가 없습니다.
                        </p>

                    </div>
                `;

        const shinsalHtml =
            episode.shinsal.length

                ? episode.shinsal.map(
                    function (item) {

                        return `
                            <div class="shinsal-item">

                                <strong>
                                    ${escapeHtml(item.name)}
                                    ·
                                    ${escapeHtml(item.location)}
                                </strong>

                                <p>
                                    ${escapeHtml(item.description)}
                                </p>

                            </div>
                        `;
                    }
                ).join("")

                : `
                    <div class="shinsal-item">

                        <strong>
                            두드러진 신살 없음
                        </strong>

                        <p>
                            현재 계산 기준에서 강하게 표시되는
                            주요 신살이 없습니다.
                        </p>

                    </div>
                `;

        return `
            <article class="${className}">

                ${renderSceneHeading(
                    episode,
                    number
                )}

                <div class="narration-card">

                    ${
                        episode.lines.map(
                            function (text) {

                                return `
                                    <div class="narration-line">

                                        <p>
                                            ${escapeHtml(text)}
                                        </p>

                                    </div>
                                `;
                            }
                        ).join("")
                    }

                    <div class="narration-line">

                        <p>
                            <strong>
                                합·충·형·파·해
                            </strong>
                        </p>

                        <div class="relation-list">
                            ${relationsHtml}
                        </div>

                    </div>

                    <div class="narration-line">

                        <p>
                            <strong>
                                신살 이야기
                            </strong>
                        </p>

                        <div class="shinsal-list">
                            ${shinsalHtml}
                        </div>

                    </div>

                    ${renderEvidence(
                        episode.evidence
                    )}

                </div>

            </article>
        `;
    }

    function renderLuckEpisode(
        episode,
        number,
        className
    ) {

        const fortunes =
            arrayOrEmpty(
                episode.luck.decadeFortunes
            );

        const timeline =
            fortunes.length

                ? fortunes.map(
                    renderDecadeFortune
                ).join("")

                : `
                    <div class="relation-item">

                        <strong>
                            대운 정보 없음
                        </strong>

                        <p>
                            서버 응답에 대운 목록이 포함되지 않았습니다.
                        </p>

                    </div>
                `;

        return `
            <article class="${className}">

                ${renderSceneHeading(
                    episode,
                    number
                )}

                <div class="narration-card">

                    ${
                        episode.lines.map(
                            function (text) {

                                return `
                                    <div class="narration-line">

                                        <p>
                                            ${escapeHtml(text)}
                                        </p>

                                    </div>
                                `;
                            }
                        ).join("")
                    }

                    <div class="narration-line">

                        <p>
                            <strong>
                                대운의 시간표
                            </strong>
                        </p>

                        <div class="luck-timeline">
                            ${timeline}
                        </div>

                    </div>

                    ${renderEvidence(
                        episode.evidence
                    )}

                </div>

            </article>
        `;
    }

    function renderDecadeFortune(
        decade
    ) {

        const years =
            arrayOrEmpty(
                decade.yearlyFortunes
            );

        const yearHtml =
            years.length

                ? years.map(
                    function (year) {

                        return `
                            <div class="year-card">

                                <strong>
                                    ${escapeHtml(year.year)}년
                                    ·
                                    ${escapeHtml(year.ganZhiKorean)}
                                </strong>

                                ${escapeHtml(year.age)}세
                                ·
                                ${escapeHtml(year.tenGod)}

                                <br>

                                공망:
                                ${escapeHtml(year.xunKong || "-")}

                            </div>
                        `;
                    }
                ).join("")

                : `
                    <div class="year-card">

                        <strong>
                            세운 정보 없음
                        </strong>

                        연도별 데이터가 없습니다.

                    </div>
                `;

        return `
            <details class="luck-block ${
                decade.current
                    ? "is-current"
                    : ""
            }"
            ${
                decade.current
                    ? "open"
                    : ""
            }>

                <summary>

                    <strong>
                        ${escapeHtml(decade.ganZhiKorean)}
                        대운
                    </strong>

                    <span>

                        ${escapeHtml(decade.startAge)}세
                        ~
                        ${escapeHtml(decade.endAge)}세

                        <br>

                        ${escapeHtml(decade.startYear)}
                        ~
                        ${escapeHtml(decade.endYear)}

                    </span>

                </summary>

                <div class="year-grid">
                    ${yearHtml}
                </div>

            </details>
        `;
    }

    function renderLedgerEpisode(
        episode,
        number,
        className
    ) {

        const calendar =
            episode.calendar
            || {};

        const structure =
            episode.structure
            || {};

        const strength =
            episode.strength
            || {};

        return `
            <article class="${className}">

                ${renderSceneHeading(
                    episode,
                    number
                )}

                <div class="narration-card">

                    ${
                        episode.lines.map(
                            function (text) {

                                return `
                                    <div class="narration-line">

                                        <p>
                                            ${escapeHtml(text)}
                                        </p>

                                    </div>
                                `;
                            }
                        ).join("")
                    }

                    <details class="story-note">

                        <summary>

                            <div>

                                <strong>
                                    사주팔자 장부 펼치기
                                </strong>

                                <small>
                                    년주·월주·일주·시주
                                </small>

                            </div>

                            <span class="story-note-arrow">
                                ⌄
                            </span>

                        </summary>

                        <div class="story-note-body">

                            ${renderPillarBoard(
                                episode.pillars
                            )}

                        </div>

                    </details>

                    <details class="story-note">

                        <summary>

                            <div>

                                <strong>
                                    달력과 기본 계산 기록
                                </strong>

                                <small>
                                    양력·음력·계산 기준
                                </small>

                            </div>

                            <span class="story-note-arrow">
                                ⌄
                            </span>

                        </summary>

                        <div class="story-note-body">

                            <div class="secret-ledger">

                                ${ledgerRow(
                                    "입력 달력",
                                    calendar.inputCalendarType
                                )}

                                ${ledgerRow(
                                    "양력",
                                    calendar.solarDate
                                )}

                                ${ledgerRow(
                                    "음력",
                                    calendar.lunarDate
                                )}

                                ${ledgerRow(
                                    "태어난 시간",
                                    calendar.birthTime
                                )}

                                ${ledgerRow(
                                    "계산 기준",
                                    calendar.calculationStandard
                                )}

                            </div>

                        </div>

                    </details>

                    <details class="story-note">

                        <summary>

                            <div>

                                <strong>
                                    신강신약·용신 장부
                                </strong>

                                <small>
                                    일간의 힘과 보완 방향
                                </small>

                            </div>

                            <span class="story-note-arrow">
                                ⌄
                            </span>

                        </summary>

                        <div class="story-note-body">

                            <div class="secret-ledger">

                                ${ledgerRow(
                                    "보조 점수",

                                    strength.score !== undefined
                                        ? strength.score + "점"
                                        : "-"
                                )}

                                ${ledgerRow(
                                    "판정",
                                    strength.classification
                                )}

                                ${ledgerRow(
                                    "일간 오행",
                                    strength.dayMasterElement
                                )}

                                ${ledgerRow(
                                    "용신 보조값",
                                    strength.usefulGod
                                )}

                                ${ledgerRow(
                                    "희신 보조값",
                                    strength.favorableGod
                                )}

                                ${ledgerRow(
                                    "기신 보조값",
                                    strength.unfavorableGod
                                )}

                                ${ledgerRow(
                                    "참고",
                                    strength.note
                                )}

                            </div>

                        </div>

                    </details>

                    <details class="story-note">

                        <summary>

                            <div>

                                <strong>
                                    태원·태식·명궁·신궁
                                </strong>

                                <small>
                                    전문 보조정보
                                </small>

                            </div>

                            <span class="story-note-arrow">
                                ⌄
                            </span>

                        </summary>

                        <div class="story-note-body">

                            <div class="secret-ledger">

                                ${ledgerRow(
                                    "태원",

                                    joinStructure(
                                        structure.fetalOriginKorean,
                                        structure.fetalOrigin,
                                        structure.fetalOriginNaYin
                                    )
                                )}

                                ${ledgerRow(
                                    "태식",

                                    joinStructure(
                                        structure.fetalBreathKorean,
                                        structure.fetalBreath,
                                        structure.fetalBreathNaYin
                                    )
                                )}

                                ${ledgerRow(
                                    "명궁",

                                    joinStructure(
                                        structure.lifePalaceKorean,
                                        structure.lifePalace
                                    )
                                )}

                                ${ledgerRow(
                                    "신궁",

                                    joinStructure(
                                        structure.bodyPalaceKorean,
                                        structure.bodyPalace
                                    )
                                )}

                                ${ledgerRow(
                                    "참고",
                                    structure.note
                                )}

                            </div>

                        </div>

                    </details>

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

    function renderElementBoard(
        items
    ) {

        if (!items.length) {

            return `
                <div class="narration-line">

                    <p>
                        오행 분포 정보가 없습니다.
                    </p>

                </div>
            `;
        }

        const rows =
            items.map(
                function (item) {

                    const percentage =
                        Number(
                            item.percentage
                        )
                        || 0;

                    const width =
                        Math.max(
                            0,

                            Math.min(
                                100,
                                percentage
                            )
                        );

                    return `
                        <div class="element-row">

                            <strong>
                                ${escapeHtml(item.name)}
                            </strong>

                            <div class="element-track">

                                <div class="element-value"
                                     style="width:${width}%">
                                </div>

                            </div>

                            <span>
                                ${percentage}%
                            </span>

                        </div>
                    `;
                }
            ).join("");

        return `
            <div class="narration-line">

                <p>
                    <strong>
                        오행의 분포
                    </strong>
                </p>

                <div class="element-board">
                    ${rows}
                </div>

            </div>
        `;
    }

    function renderPillarBoard(
        pillars
    ) {

        if (!pillars.length) {

            return `
                <div class="relation-item">

                    <strong>
                        사주팔자 정보 없음
                    </strong>

                    <p>
                        서버 응답에 기둥 데이터가 없습니다.
                    </p>

                </div>
            `;
        }

        return `
            <div class="pillar-board">

                ${
                    pillars.map(
                        function (pillar) {

                            if (!pillar.known) {

                                return `
                                    <div class="pillar-mini">

                                        <span>
                                            ${escapeHtml(pillar.type)}
                                        </span>

                                        <strong>
                                            ?
                                        </strong>

                                        <small>
                                            시간 미상
                                        </small>

                                    </div>
                                `;
                            }

                            return `
                                <div class="pillar-mini">

                                    <span>
                                        ${escapeHtml(pillar.type)}
                                    </span>

                                    <strong>
                                        ${escapeHtml(pillar.ganZhi)}
                                    </strong>

                                    <small>
                                        ${escapeHtml(pillar.ganZhiKorean)}
                                    </small>

                                </div>
                            `;
                        }
                    ).join("")
                }

            </div>
        `;
    }

    function ledgerRow(
        label,
        value
    ) {

        return `
            <div class="ledger-row">

                <span>
                    ${escapeHtml(label)}
                </span>

                <strong>
                    ${escapeHtml(
                        safeText(
                            value,
                            "-"
                        )
                    )}
                </strong>

            </div>
        `;
    }

    function joinStructure(
        korean,
        original,
        extra
    ) {

        const main =
            korean

                ? korean
                + (
                    original
                        ? " (" + original + ")"
                        : ""
                )

                : safeText(
                    original,
                    "시간 미상"
                );

        return extra

            ? main
            + " · "
            + extra

            : main;
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

    function getPillarText(
        pillars,
        type
    ) {

        const pillar =
            pillars.find(
                function (item) {

                    return item.type
                        === type;
                }
            );

        if (
            !pillar
            || !pillar.known
        ) {

            return "시간 미상";
        }

        return safeText(
            pillar.ganZhiKorean,
            pillar.ganZhi
        );
    }

    function evidenceItem(
        label,
        value
    ) {

        return {
            label: label,

            value:
                safeText(
                    value,
                    "-"
                )
        };
    }

    function percentageText(
        value
    ) {

        const number =
            Number(value);

        return Number.isFinite(
            number
        )

            ? number + "%"

            : "-";
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

        const marks = [
            ".",
            "!",
            "?",
            "。",
            "！",
            "？"
        ];

        for (
            let index = 0;
            index < Math.min(
                normalized.length,
                maximumLength
            );
            index += 1
        ) {

            if (
                marks.includes(
                    normalized.charAt(index)
                )
            ) {

                return normalized.substring(
                    0,
                    index + 1
                );
            }
        }

        return normalized.substring(
            0,
            maximumLength
        ).trim() + "…";
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