document.addEventListener("DOMContentLoaded", function () {

    const screens =
        document.querySelectorAll("[data-screen]");

    const backButton =
        document.querySelector("#backButton");

    const homeButton =
        document.querySelector("#homeButton");

    const headerTitle =
        document.querySelector("#headerTitle");

    const headerProgress =
        document.querySelector("#headerProgress");

    const bottomNavigation =
        document.querySelector("#bottomNavigation");

    const toast =
        document.querySelector("#toast");

    const nameForm =
        document.querySelector("#nameForm");

    const nameInput =
        document.querySelector("#nameInput");

    const nameError =
        document.querySelector("#nameError");

    const nameNextButton =
        document.querySelector("#nameNextButton");

    const birthForm =
        document.querySelector("#birthForm");

    const birthYear =
        document.querySelector("#birthYear");

    const birthMonth =
        document.querySelector("#birthMonth");

    const birthDay =
        document.querySelector("#birthDay");

    const birthHour =
        document.querySelector("#birthHour");

    const birthMinute =
        document.querySelector("#birthMinute");

    const birthTimeUnknown =
        document.querySelector("#birthTimeUnknown");

    const timeInputArea =
        document.querySelector("#timeInputArea");

    const lunarLeapArea =
        document.querySelector("#lunarLeapArea");

    const lunarLeapMonth =
        document.querySelector("#lunarLeapMonth");

    const birthError =
        document.querySelector("#birthError");

    const birthNextButton =
        document.querySelector("#birthNextButton");

    const birthSpeech =
        document.querySelector("#birthSpeech");

    const submitProfileButton =
        document.querySelector("#submitProfileButton");

    const startAnalysisButton =
        document.querySelector("#startAnalysisButton");

    const analysisLoadingMessage =
        document.querySelector("#analysisLoadingMessage");

    const analysisLoadingSteps =
        document.querySelectorAll(
            ".analysis-loading-list div"
        );

    const screenHistory = [];

    let currentScreen = "home";
    let toastTimer;
    let loadingMessageTimer;
    let birthFormTouched = false;

    const sajuForm =
        loadSajuForm();

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
                sessionStorage.getItem("sajuForm");

            if (!stored) {
                return createEmptySajuForm();
            }

            return {
                ...createEmptySajuForm(),
                ...JSON.parse(stored)
            };

        } catch (error) {

            console.error(
                "사주 입력정보를 불러오지 못했습니다.",
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

        sessionStorage.removeItem("sajuForm");

        nameInput.value = "";

        birthYear.value = "";
        birthMonth.value = "";
        birthDay.value = "";

        birthHour.value = "";
        birthMinute.value = "";

        birthTimeUnknown.checked = false;
        lunarLeapMonth.checked = false;

        timeInputArea.classList.remove("disabled");
        lunarLeapArea.classList.add("hidden");

        birthHour.disabled = false;
        birthMinute.disabled = false;

        document.querySelectorAll("[data-calendar]")
            .forEach(function (button) {

                button.classList.toggle(
                    "active",
                    button.dataset.calendar === "SOLAR"
                );
            });

        document.querySelectorAll("[data-gender]")
            .forEach(function (button) {

                button.classList.remove("active");
            });

        nameError.textContent = "";
        birthError.textContent = "";

        nameNextButton.disabled = true;
        birthNextButton.disabled = true;
    }

    function showToast(message) {

        clearTimeout(toastTimer);

        toast.textContent =
            message;

        toast.classList.add("show");

        toastTimer =
            setTimeout(function () {

                toast.classList.remove("show");

            }, 1800);
    }

    function getHeaderInformation(screenName) {

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
                progress: "만세력 계산 중"
            },

            result: {
                title: "AI 사주",
                progress: "분석 결과"
            }
        };

        return information[screenName];
    }

    function showScreen(
        screenName,
        addHistory = true
    ) {
        if (
            addHistory
            && currentScreen !== screenName
        ) {
            screenHistory.push(currentScreen);
        }

        screens.forEach(function (screen) {

            screen.classList.toggle(
                "active",
                screen.dataset.screen === screenName
            );
        });

        currentScreen =
            screenName;

        const headerInformation =
            getHeaderInformation(screenName);

        headerTitle.textContent =
            headerInformation.title;

        headerProgress.textContent =
            headerInformation.progress;

        const isHome =
            screenName === "home";

        backButton.classList.toggle(
            "invisible",
            isHome
        );

        homeButton.classList.toggle(
            "invisible",
            isHome
        );

        bottomNavigation.classList.toggle(
            "hidden",
            !isHome
        );

        if (screenName === "birth") {

            restoreBirthForm();

            birthFormTouched = false;

            validateBirthForm(false);
        }

        if (screenName === "review") {
            renderReview();
        }

        window.scrollTo({
            top: 0,
            behavior: "auto"
        });
    }

    document.querySelectorAll("[data-go]")
        .forEach(function (button) {

            button.addEventListener(
                "click",
                function () {

                    showScreen(
                        button.dataset.go
                    );
                }
            );
        });

    document.querySelectorAll("[data-message]")
        .forEach(function (button) {

            button.addEventListener(
                "click",
                function () {

                    showToast(
                        button.dataset.message
                        + " 기능은 다음 단계에서 연결합니다."
                    );
                }
            );
        });

    backButton.addEventListener(
        "click",
        function () {

            const previousScreen =
                screenHistory.pop();

            if (!previousScreen) {

                showScreen(
                    "home",
                    false
                );

                return;
            }

            showScreen(
                previousScreen,
                false
            );
        }
    );

    homeButton.addEventListener(
        "click",
        function () {

            screenHistory.length = 0;

            showScreen(
                "home",
                false
            );
        }
    );

    function validateName() {

        const name =
            nameInput.value.trim();

        if (name.length === 0) {

            nameError.textContent = "";
            nameNextButton.disabled = true;

            return false;
        }

        if (name.length < 2) {

            nameError.textContent =
                "이름은 두 글자 이상 입력해주세요.";

            nameNextButton.disabled = true;

            return false;
        }

        nameError.textContent = "";
        nameNextButton.disabled = false;

        return true;
    }

    if (sajuForm.name) {

        nameInput.value =
            sajuForm.name;

        validateName();
    }

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
                escapeHtml(sajuForm.name)
                + "님의 생년월일과<br>"
                + "태어난 시간을 알려주세요.";

            showScreen("birth");
        }
    );

    function keepNumbersOnly(input) {

        input.value =
            input.value.replace(
                /[^0-9]/g,
                ""
            );
    }

    document.querySelectorAll(".numeric-input")
        .forEach(function (input) {

            input.addEventListener(
                "input",
                function () {

                    birthFormTouched = true;

                    keepNumbersOnly(input);

                    validateBirthForm(true);
                }
            );
        });

    document.querySelectorAll("[data-calendar]")
        .forEach(function (button) {

            button.addEventListener(
                "click",
                function () {

                    birthFormTouched = true;

                    document
                        .querySelectorAll("[data-calendar]")
                        .forEach(function (item) {

                            item.classList.remove("active");
                        });

                    button.classList.add("active");

                    sajuForm.calendarType =
                        button.dataset.calendar;

                    const isLunar =
                        sajuForm.calendarType === "LUNAR";

                    lunarLeapArea.classList.toggle(
                        "hidden",
                        !isLunar
                    );

                    if (!isLunar) {

                        lunarLeapMonth.checked = false;

                        sajuForm.lunarLeapMonth = false;
                    }

                    validateBirthForm(true);
                }
            );
        });

    document.querySelectorAll("[data-gender]")
        .forEach(function (button) {

            button.addEventListener(
                "click",
                function () {

                    birthFormTouched = true;

                    document
                        .querySelectorAll("[data-gender]")
                        .forEach(function (item) {

                            item.classList.remove("active");
                        });

                    button.classList.add("active");

                    sajuForm.gender =
                        button.dataset.gender;

                    validateBirthForm(true);
                }
            );
        });

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

            birthFormTouched = true;

            sajuForm.birthTimeUnknown =
                birthTimeUnknown.checked;

            timeInputArea.classList.toggle(
                "disabled",
                birthTimeUnknown.checked
            );

            birthHour.disabled =
                birthTimeUnknown.checked;

            birthMinute.disabled =
                birthTimeUnknown.checked;

            if (birthTimeUnknown.checked) {

                birthHour.value = "";
                birthMinute.value = "";
            }

            validateBirthForm(true);
        }
    );

    function isValidSolarDate(
        year,
        month,
        day
    ) {
        const numericYear =
            Number(year);

        const numericMonth =
            Number(month);

        const numericDay =
            Number(day);

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

        return (
            date.getFullYear() === numericYear
            && date.getMonth() === numericMonth - 1
            && date.getDate() === numericDay
        );
    }

    function isValidLunarDate(
        year,
        month,
        day
    ) {
        return (
            Number(year) >= 1900
            && Number(year) <= new Date().getFullYear()
            && Number(month) >= 1
            && Number(month) <= 12
            && Number(day) >= 1
            && Number(day) <= 30
        );
    }

    function isValidTime(
        hour,
        minute
    ) {
        return (
            Number(hour) >= 0
            && Number(hour) <= 23
            && Number(minute) >= 0
            && Number(minute) <= 59
        );
    }

    function setBirthValidationError(
        message,
        showMessage
    ) {
        birthError.textContent =
            showMessage
                ? message
                : "";

        birthNextButton.disabled = true;

        return false;
    }

    function validateBirthForm(
        showMessage = birthFormTouched
    ) {
        if (!sajuForm.gender) {

            return setBirthValidationError(
                "성별을 선택해주세요.",
                showMessage
            );
        }

        if (
            !birthYear.value
            || !birthMonth.value
            || !birthDay.value
        ) {
            return setBirthValidationError(
                "생년월일을 모두 입력해주세요.",
                showMessage
            );
        }

        const validDate =
            sajuForm.calendarType === "SOLAR"
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

            return setBirthValidationError(
                "올바른 생년월일을 입력해주세요.",
                showMessage
            );
        }

        if (
            !birthTimeUnknown.checked
            && (
                birthHour.value === ""
                || birthMinute.value === ""
            )
        ) {
            return setBirthValidationError(
                "태어난 시간을 입력하거나 시간 모름을 선택해주세요.",
                showMessage
            );
        }

        if (
            !birthTimeUnknown.checked
            && !isValidTime(
                birthHour.value,
                birthMinute.value
            )
        ) {
            return setBirthValidationError(
                "올바른 출생시간을 입력해주세요.",
                showMessage
            );
        }

        birthError.textContent = "";
        birthNextButton.disabled = false;

        return true;
    }

    function padNumber(value) {

        return String(value)
            .padStart(2, "0");
    }

    birthForm.addEventListener(
        "submit",
        function (event) {

            event.preventDefault();

            birthFormTouched = true;

            if (!validateBirthForm(true)) {
                return;
            }

            sajuForm.birthDate =
                birthYear.value
                + "-"
                + padNumber(birthMonth.value)
                + "-"
                + padNumber(birthDay.value);

            sajuForm.birthTimeUnknown =
                birthTimeUnknown.checked;

            sajuForm.birthTime =
                birthTimeUnknown.checked
                    ? null
                    : padNumber(birthHour.value)
                    + ":"
                    + padNumber(birthMinute.value);

            sajuForm.lunarLeapMonth =
                sajuForm.calendarType === "LUNAR"
                && lunarLeapMonth.checked;

            saveSajuForm();

            showScreen("review");
        }
    );

    function restoreBirthForm() {

        if (sajuForm.name) {

            birthSpeech.innerHTML =
                escapeHtml(sajuForm.name)
                + "님의 생년월일과<br>"
                + "태어난 시간을 알려주세요.";
        }

        document.querySelectorAll("[data-calendar]")
            .forEach(function (button) {

                button.classList.toggle(
                    "active",
                    button.dataset.calendar
                    === sajuForm.calendarType
                );
            });

        const isLunar =
            sajuForm.calendarType === "LUNAR";

        lunarLeapArea.classList.toggle(
            "hidden",
            !isLunar
        );

        lunarLeapMonth.checked =
            isLunar
            && sajuForm.lunarLeapMonth;

        document.querySelectorAll("[data-gender]")
            .forEach(function (button) {

                button.classList.toggle(
                    "active",
                    button.dataset.gender
                    === sajuForm.gender
                );
            });

        if (sajuForm.birthDate) {

            const dateParts =
                sajuForm.birthDate.split("-");

            birthYear.value =
                dateParts[0] || "";

            birthMonth.value =
                dateParts[1] || "";

            birthDay.value =
                dateParts[2] || "";
        }

        birthTimeUnknown.checked =
            sajuForm.birthTimeUnknown;

        timeInputArea.classList.toggle(
            "disabled",
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
                sajuForm.birthTime.split(":");

            birthHour.value =
                timeParts[0] || "";

            birthMinute.value =
                timeParts[1] || "";

        } else {

            birthHour.value = "";
            birthMinute.value = "";
        }
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

        const reviewLunarLeapRow =
            document.querySelector(
                "#reviewLunarLeapRow"
            );

        reviewLunarLeapRow.classList.toggle(
            "hidden",
            sajuForm.calendarType !== "LUNAR"
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

    submitProfileButton.addEventListener(
        "click",
        async function () {

            submitProfileButton.disabled = true;
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

                            body: JSON.stringify(
                                sajuForm
                            )
                        }
                    );

                const responseBody =
                    await readJsonResponse(response);

                if (!response.ok) {

                    throw new Error(
                        responseBody.message
                        || "저장에 실패했습니다."
                    );
                }

                sessionStorage.setItem(
                    "sajuProfileId",
                    String(responseBody.profileId)
                );

                resetSajuForm();

                showScreen("complete");

            } catch (error) {

                console.error(error);

                showToast(
                    error.message
                    || "저장 중 오류가 발생했습니다."
                );

            } finally {

                submitProfileButton.disabled = false;
                submitProfileButton.textContent =
                    "확인 및 저장";
            }
        }
    );

    startAnalysisButton.addEventListener(
        "click",
        async function () {

            const profileId =
                Number(
                    sessionStorage.getItem(
                        "sajuProfileId"
                    )
                );

            if (
                !Number.isInteger(profileId)
                || profileId <= 0
            ) {
                showToast(
                    "저장된 사주 기본정보가 없습니다."
                );

                return;
            }

            startAnalysisButton.disabled = true;

            startLoadingMessages();

            showScreen("analysis");

            try {

                const response =
                    await fetch(
                        "/api/saju/analyses",
                        {
                            method: "POST",

                            headers: {
                                "Content-Type":
                                    "application/json"
                            },

                            body: JSON.stringify({
                                profileId: profileId
                            })
                        }
                    );

                const result =
                    await readJsonResponse(response);

                if (!response.ok) {

                    throw new Error(
                        result.message
                        || "사주 분석에 실패했습니다."
                    );
                }

                sessionStorage.setItem(
                    "sajuAnalysisId",
                    String(result.analysisId)
                );

                sessionStorage.setItem(
                    "sajuAnalysisResult",
                    JSON.stringify(result)
                );

                stopLoadingMessages();

                renderSajuResult(result);

                showScreen("result");

            } catch (error) {

                stopLoadingMessages();

                console.error(error);

                showToast(
                    error.message
                    || "사주 분석 중 오류가 발생했습니다."
                );

                showScreen(
                    "complete",
                    false
                );

            } finally {

                startAnalysisButton.disabled = false;
            }
        }
    );

    function startLoadingMessages() {

        stopLoadingMessages();

        const messages = [
            "음력과 양력 정보를 확인하고 있습니다.",
            "절입 시각을 기준으로 사주팔자를 계산하고 있습니다.",
            "오행·음양·십성·지장간을 계산하고 있습니다.",
            "대운과 세운의 흐름을 정리하고 있습니다."
        ];

        let index = 0;

        analysisLoadingMessage.textContent =
            messages[index];

        updateLoadingStep(0);

        loadingMessageTimer =
            setInterval(function () {

                index =
                    Math.min(
                        index + 1,
                        messages.length - 1
                    );

                analysisLoadingMessage.textContent =
                    messages[index];

                updateLoadingStep(
                    Math.min(
                        index,
                        2
                    )
                );

            }, 650);
    }

    function stopLoadingMessages() {

        clearInterval(
            loadingMessageTimer
        );

        loadingMessageTimer = null;
    }

    function updateLoadingStep(activeIndex) {

        analysisLoadingSteps.forEach(
            function (step, index) {

                step.classList.toggle(
                    "active",
                    index <= activeIndex
                );
            }
        );
    }

    function renderSajuResult(result) {

        setText(
            "#resultTitle",
            result.name + "님의 사주"
        );

        setText(
            "#resultSummary",
            result.summary
        );

        renderCalendar(result);
        renderPillars(result.pillars);
        renderBalance(
            "#elementResult",
            result.elementBalance.items,
            "주도 오행: "
            + result.elementBalance.dominant
            + " · 부족 오행: "
            + result.elementBalance.weakest
        );

        renderYinYang(
            result.yinYangBalance
        );

        renderBalance(
            "#tenGodResult",
            result.tenGodBalance.items,
            "주요 십성: "
            + result.tenGodBalance.dominant
        );

        renderStrength(
            result.strength
        );

        renderStructure(
            result.structure
        );

        renderRelations(
            result.relations
        );

        renderShinsal(
            result.shinsal
        );

        renderLuck(
            result.luck
        );

        renderInterpretations(
            result.interpretations
        );

        setText(
            "#resultDisclaimer",
            result.disclaimer
        );
    }

    function renderCalendar(result) {

        const calendar =
            result.calendar;

        document.querySelector(
            "#calendarResult"
        ).innerHTML = [
            createInformationRow(
                "입력 달력",
                calendar.inputCalendarType
            ),

            createInformationRow(
                "양력",
                calendar.solarDate
            ),

            createInformationRow(
                "음력",
                calendar.lunarDate
            ),

            createInformationRow(
                "태어난 시간",
                calendar.birthTime
            ),

            createInformationRow(
                "띠",
                result.zodiac
            ),

            createInformationRow(
                "일간",
                result.dayMaster
            ),

            createInformationRow(
                "계산 기준",
                calendar.calculationStandard
            )
        ].join("");
    }

    function renderPillars(pillars) {

        document.querySelector(
            "#pillarResult"
        ).innerHTML =
            pillars.map(function (pillar) {

                if (!pillar.known) {

                    return `
                        <div class="pillar-card unknown">

                            <span class="pillar-type">
                                ${escapeHtml(pillar.type)}
                            </span>

                            <strong class="pillar-character">
                                ?
                            </strong>

                            <span class="pillar-detail">
                                시간 모름
                            </span>

                        </div>
                    `;
                }

                const hiddenStems =
                    pillar.hiddenStems
                        .map(function (hidden) {

                            return `
                                <span>
                                    ${escapeHtml(hidden.stemKorean)}
                                    ·
                                    ${escapeHtml(hidden.tenGod)}
                                </span>
                            `;
                        })
                        .join("");

                return `
                    <div class="pillar-card">

                        <span class="pillar-type">
                            ${escapeHtml(pillar.type)}
                        </span>

                        <strong class="pillar-character">
                            ${escapeHtml(pillar.ganZhi)}
                        </strong>

                        <span class="pillar-korean">
                            ${escapeHtml(pillar.ganZhiKorean)}
                        </span>

                        <span class="pillar-detail">
                            ${escapeHtml(pillar.stemElement)}
                            /
                            ${escapeHtml(pillar.branchElement)}
                            <br>
                            ${escapeHtml(pillar.stemTenGod)}
                            ·
                            ${escapeHtml(pillar.twelveStage)}
                            <br>
                            ${escapeHtml(pillar.naYin)}
                        </span>

                        <div class="hidden-stem-list">
                            ${hiddenStems}
                        </div>

                    </div>
                `;
            })
            .join("");
    }

    function renderBalance(
        selector,
        items,
        summary
    ) {
        const rows =
            items.map(function (item) {

                return `
                    <div class="balance-row">

                        <div class="balance-label">

                            <strong>
                                ${escapeHtml(item.name)}
                            </strong>

                            <span>
                                ${item.count}개 ·
                                ${item.percentage}%
                            </span>

                        </div>

                        <div class="balance-track">

                            <div class="balance-value"
                                 style="width: ${item.percentage}%">
                            </div>

                        </div>

                    </div>
                `;
            })
            .join("");

        document.querySelector(
            selector
        ).innerHTML =
            createInformationRow(
                "요약",
                summary
            )
            + rows;
    }

    function renderYinYang(balance) {

        document.querySelector(
            "#yinYangResult"
        ).innerHTML = `

            ${createInformationRow(
                "음양 균형",
                balance.balance
            )}

            <div class="balance-row">

                <div class="balance-label">
                    <strong>양</strong>
                    <span>
                        ${balance.yangCount}개 ·
                        ${balance.yangPercentage}%
                    </span>
                </div>

                <div class="balance-track">
                    <div class="balance-value"
                         style="width: ${balance.yangPercentage}%">
                    </div>
                </div>

            </div>

            <div class="balance-row">

                <div class="balance-label">
                    <strong>음</strong>
                    <span>
                        ${balance.yinCount}개 ·
                        ${balance.yinPercentage}%
                    </span>
                </div>

                <div class="balance-track">
                    <div class="balance-value"
                         style="width: ${balance.yinPercentage}%">
                    </div>
                </div>

            </div>
        `;
    }

    function renderStrength(strength) {

        document.querySelector(
            "#strengthResult"
        ).innerHTML = [

            createInformationRow(
                "보조 점수",
                strength.score + "점"
            ),

            createInformationRow(
                "판정",
                strength.classification
            ),

            createInformationRow(
                "일간 오행",
                strength.dayMasterElement
            ),

            createInformationRow(
                "용신 보조값",
                strength.usefulGod
            ),

            createInformationRow(
                "희신 보조값",
                strength.favorableGod
            ),

            createInformationRow(
                "기신 보조값",
                strength.unfavorableGod
            ),

            `
                <p class="result-disclaimer">
                    ${escapeHtml(strength.note)}
                </p>
            `
        ].join("");
    }

    function renderStructure(structure) {

        document.querySelector(
            "#structureResult"
        ).innerHTML = [

            createInformationRow(
                "태원",
                valueOrUnknown(
                    structure.fetalOriginKorean,
                    structure.fetalOrigin
                )
                + " · "
                + valueOrDash(
                    structure.fetalOriginNaYin
                )
            ),

            createInformationRow(
                "태식",
                valueOrUnknown(
                    structure.fetalBreathKorean,
                    structure.fetalBreath
                )
                + " · "
                + valueOrDash(
                    structure.fetalBreathNaYin
                )
            ),

            createInformationRow(
                "명궁",
                valueOrUnknown(
                    structure.lifePalaceKorean,
                    structure.lifePalace
                )
            ),

            createInformationRow(
                "신궁",
                valueOrUnknown(
                    structure.bodyPalaceKorean,
                    structure.bodyPalace
                )
            ),

            `
                <p class="result-disclaimer">
                    ${escapeHtml(structure.note)}
                </p>
            `
        ].join("");
    }

    function renderRelations(relations) {

        const allRelations = [
            ...relations.heavenlyStemRelations,
            ...relations.earthlyBranchRelations
        ];

        if (allRelations.length === 0) {

            document.querySelector(
                "#relationResult"
            ).innerHTML = `
                <span class="relation-tag">
                    두드러진 합충형파해가 없습니다.
                </span>
            `;

            return;
        }

        document.querySelector(
            "#relationResult"
        ).innerHTML =
            allRelations.map(function (relation) {

                return `
                    <span class="relation-tag">

                        <strong>
                            ${escapeHtml(relation.category)}
                        </strong>

                        ·

                        ${escapeHtml(relation.members)}

                        ·

                        ${escapeHtml(relation.description)}

                    </span>
                `;
            })
            .join("");
    }

    function renderShinsal(shinsal) {

        if (!shinsal || shinsal.length === 0) {

            document.querySelector(
                "#shinsalResult"
            ).innerHTML = `
                <div class="list-item">
                    <strong>주요 신살 없음</strong>
                    <p>
                        현재 적용한 핵심 신살 기준에서
                        두드러진 항목이 없습니다.
                    </p>
                </div>
            `;

            return;
        }

        document.querySelector(
            "#shinsalResult"
        ).innerHTML =
            shinsal.map(function (item) {

                return `
                    <div class="list-item">

                        <strong>
                            ${escapeHtml(item.name)}
                        </strong>

                        <span>
                            위치:
                            ${escapeHtml(item.location)}
                        </span>

                        <p>
                            ${escapeHtml(item.description)}
                        </p>

                    </div>
                `;
            })
            .join("");
    }

    function renderLuck(luck) {

        document.querySelector(
            "#luckSummaryResult"
        ).innerHTML = [

            createInformationRow(
                "운행 방향",
                luck.direction
            ),

            createInformationRow(
                "기운 시점",
                luck.startAge
            ),

            createInformationRow(
                "대운 시작일",
                luck.startDate
            ),

            createInformationRow(
                "현재 대운",
                luck.currentDecade
            ),

            `
                <p class="result-disclaimer">
                    ${escapeHtml(luck.note)}
                </p>
            `
        ].join("");

        document.querySelector(
            "#luckResult"
        ).innerHTML =
            luck.decadeFortunes
                .map(function (decade) {

                    const years =
                        decade.yearlyFortunes
                            .map(function (year) {

                                return `
                                    <div class="year-fortune-item">

                                        <strong>
                                            ${year.year}년
                                            ·
                                            ${escapeHtml(
                                                year.ganZhiKorean
                                            )}
                                        </strong>

                                        ${year.age}세
                                        ·
                                        ${escapeHtml(year.tenGod)}
                                        <br>

                                        공망:
                                        ${escapeHtml(
                                            year.xunKong || "-"
                                        )}

                                    </div>
                                `;
                            })
                            .join("");

                    return `
                        <details class="luck-item ${
                            decade.current
                                ? "current"
                                : ""
                        }"
                        ${decade.current ? "open" : ""}>

                            <summary>

                                <strong>
                                    ${escapeHtml(
                                        decade.ganZhiKorean
                                    )}
                                    대운
                                </strong>

                                <span>
                                    ${decade.startAge}세 ~
                                    ${decade.endAge}세
                                    ·
                                    ${decade.startYear} ~
                                    ${decade.endYear}
                                </span>

                            </summary>

                            <div class="year-fortune-list">
                                ${years}
                            </div>

                        </details>
                    `;
                })
                .join("");
    }

    function renderInterpretations(interpretations) {

        document.querySelector(
            "#interpretationResult"
        ).innerHTML =
            interpretations.map(function (item) {

                return `
                    <article class="interpretation-item">

                        <span class="interpretation-category">
                            ${escapeHtml(item.category)}
                        </span>

                        <h3>
                            ${escapeHtml(item.title)}
                        </h3>

                        <p>
                            ${escapeHtml(item.content)}
                        </p>

                    </article>
                `;
            })
            .join("");
    }

    function createInformationRow(
        label,
        value
    ) {
        return `
            <div class="information-row">

                <span>
                    ${escapeHtml(label)}
                </span>

                <strong>
                    ${escapeHtml(
                        value === null
                        || value === undefined
                            ? "-"
                            : String(value)
                    )}
                </strong>

            </div>
        `;
    }

    function valueOrUnknown(
        korean,
        original
    ) {
        if (!original) {
            return "시간 미상";
        }

        return korean
            ? korean + " (" + original + ")"
            : original;
    }

    function valueOrDash(value) {

        return value || "-";
    }

    function setText(
        selector,
        value
    ) {
        const element =
            document.querySelector(selector);

        if (element) {
            element.textContent =
                value ?? "";
        }
    }

    async function readJsonResponse(response) {

        try {

            return await response.json();

        } catch (error) {

            return {
                message:
                    "서버 응답을 확인할 수 없습니다."
            };
        }
    }

    function escapeHtml(value) {

        if (
            value === null
            || value === undefined
        ) {
            return "";
        }

        const element =
            document.createElement("div");

        element.textContent =
            String(value);

        return element.innerHTML;
    }

    showScreen(
        "home",
        false
    );
});