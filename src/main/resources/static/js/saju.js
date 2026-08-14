document.addEventListener(
    "DOMContentLoaded",
    function () {

        "use strict";


        /*
         * =================================================
         * CONSTANT
         * =================================================
         */

        const API_URL =
            "/api/saju/calculate";


        const FORM_STORAGE_KEY =
            "sajuForm";


        const RESULT_STORAGE_KEY =
            "sajuAnalysisResult";


        /*
         * API가 너무 빨리 끝나더라도
         * 분석 연출이 최소한 보이는 시간.
         */
        const MINIMUM_ANALYSIS_TIME =
            4200;


        /*
         * Category Fact 한 장 표시 시간.
         */
        const FACT_DISPLAY_TIME =
            900;


        /*
         * API 응답 대기 중
         * Progress가 100%까지 먼저 가지 않도록 제한.
         */
        const WAIT_PROGRESS_LIMIT =
            94;


        /*
         * =================================================
         * 11 CONSULTATION CATEGORY
         * =================================================
         */

        const CATEGORY_ORDER = [

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


        const CATEGORY_META = {

            PERSONALITY: {

                number:
                    "01",

                label:
                    "기본 성향",

                title:
                    "나는 어떤 사람인가"

            },


            PAST_LUCK: {

                number:
                    "02",

                label:
                    "지나온 흐름",

                title:
                    "지나온 시간에 반복된 흐름"

            },


            SOCIAL_RELATIONSHIP: {

                number:
                    "03",

                label:
                    "대인관계",

                title:
                    "사람과 관계를 맺는 방식"

            },


            LOVE_STYLE: {

                number:
                    "04",

                label:
                    "연애 스타일",

                title:
                    "마음이 움직일 때 드러나는 모습"

            },


            PARTNER_LUCK: {

                number:
                    "05",

                label:
                    "인연·배우자",

                title:
                    "인연을 바라보는 기준과 거리"

            },


            WEALTH: {

                number:
                    "06",

                label:
                    "재물",

                title:
                    "돈을 벌고 지키고 쓰는 흐름"

            },


            CAREER: {

                number:
                    "07",

                label:
                    "직업·사업",

                title:
                    "일과 책임을 다루는 방식"

            },


            LEGAL_RISK: {

                number:
                    "08",

                label:
                    "분쟁·주의",

                title:
                    "조심해서 다뤄야 하는 지점"

            },


            HEALTH_TENDENCY: {

                number:
                    "09",

                label:
                    "건강·생활리듬",

                title:
                    "오래 가기 위한 생활 리듬"

            },


            CURRENT_YEAR_LUCK: {

                number:
                    "10",

                label:
                    "현재·올해",

                title:
                    "지금 지나고 있는 운의 장면"

            },


            FUTURE_LUCK: {

                number:
                    "11",

                label:
                    "앞으로의 흐름",

                title:
                    "앞으로 이어질 흐름"

            }

        };


        /*
         * =================================================
         * SCREEN
         * =================================================
         */

        const screens =
            Array.from(
                document.querySelectorAll(
                    "[data-screen]"
                )
            );


        /*
         * =================================================
         * REQUIRED DOM
         * =================================================
         */

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


        /*
         * NAME
         */

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


        /*
         * BIRTH
         */

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


        /*
         * REVIEW
         */

        const reviewCompleteButton =
            required(
                "reviewCompleteButton"
            );


        /*
         * COMPLETE
         */

        const startAnalysisButton =
            required(
                "startAnalysisButton"
            );


        /*
         * ANALYSIS
         */

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


        /*
         * FACT
         */

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


        const factStatusRow =
            required(
                "factStatusRow"
            );


        const factBasis =
            required(
                "factBasis"
            );


        const skipFactsButton =
            required(
                "skipFactsButton"
            );


        /*
         * RESULT
         */

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


        /*
         * =================================================
         * STATE
         * =================================================
         */

        const sajuForm =
            loadForm();


        const screenHistory =
            [];


        let currentScreen =
            "intro";


        let birthTouched =
            false;


        let analysisRunning =
            false;


        let analysisProgress =
            0;


        let progressTimer =
            null;


        let toastTimer =
            null;


        let factSkipRequested =
            false;


        let storyObserver =
            null;


        /*
         * =================================================
         * INITIALIZE
         * =================================================
         */

        initialize();


        function initialize() {

            bindNavigation();

            bindName();

            bindBirth();

            bindReview();

            bindAnalysis();

            bindFacts();


            restoreForm();


            showScreen(
                "intro",
                false
            );


            console.log(
                "[SAJU] saju.js 초기화 완료"
            );


            console.log(
                "[SAJU] 분석 버튼 이벤트 등록 완료"
            );
        }


        /*
         * =================================================
         * REQUIRED
         * =================================================
         */

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
                    "필수 DOM을 찾을 수 없습니다: #"
                    + id
                );
            }


            return element;
        }


        /*
         * =================================================
         * FORM STORAGE
         * =================================================
         */

        function createEmptyForm() {

            return {

                name:
                    "",


                calendarType:
                    "SOLAR",


                gender:
                    "",


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


        function loadForm() {

            try {

                const stored =
                    sessionStorage.getItem(
                        FORM_STORAGE_KEY
                    );


                if (
                        !stored
                ) {

                    return createEmptyForm();
                }


                return Object.assign(
                    createEmptyForm(),
                    JSON.parse(
                        stored
                    )
                );

            } catch (
                error
            ) {

                console.warn(
                    "[SAJU] 저장된 입력정보 복원 실패",
                    error
                );


                return createEmptyForm();
            }
        }


        function saveForm() {

            sessionStorage.setItem(
                FORM_STORAGE_KEY,
                JSON.stringify(
                    sajuForm
                )
            );
        }


        /*
         * =================================================
         * NAVIGATION
         * =================================================
         */

        function bindNavigation() {

            document.addEventListener(
                "click",
                function (
                    event
                ) {

                    const target =
                        event.target.closest(
                            "[data-go]"
                        );


                    if (
                            !target
                    ) {

                        return;
                    }


                    if (
                            analysisRunning
                    ) {

                        return;
                    }


                    event.preventDefault();


                    showScreen(
                        target.dataset.go
                    );
                }
            );


            backButton.addEventListener(
                "click",
                function () {

                    if (
                            analysisRunning
                    ) {

                        return;
                    }


                    if (
                            currentScreen
                            === "intro"
                    ) {

                        window.location.href =
                            "/";

                        return;
                    }


                    const previousScreen =
                        screenHistory.pop()
                        || "intro";


                    showScreen(
                        previousScreen,
                        false
                    );
                }
            );


            homeButton.addEventListener(
                "click",
                function () {

                    if (
                            analysisRunning
                    ) {

                        return;
                    }


                    window.location.href =
                        "/";
                }
            );


            resultHomeButton.addEventListener(
                "click",
                function () {

                    window.location.href =
                        "/";
                }
            );
        }


        function showScreen(
            name,
            addHistory
        ) {

            const shouldAddHistory =
                addHistory !== false;


            const targetExists =
                screens.some(
                    function (
                        screen
                    ) {

                        return screen.dataset.screen
                            === name;
                    }
                );


            if (
                    !targetExists
            ) {

                throw new Error(
                    "존재하지 않는 화면입니다: "
                    + name
                );
            }


            if (
                    shouldAddHistory
                    && currentScreen !== name
            ) {

                screenHistory.push(
                    currentScreen
                );
            }


            screens.forEach(
                function (
                    screen
                ) {

                    screen.classList.toggle(
                        "is-active",
                        screen.dataset.screen
                        === name
                    );
                }
            );


            currentScreen =
                name;


            updateHeader(
                name
            );


            if (
                    name
                    === "birth"
            ) {

                restoreBirth();


                birthTouched =
                    false;


                validateBirth(
                    false
                );
            }


            if (
                    name
                    === "review"
            ) {

                renderReview();
            }


            if (
                    name
                    === "result"
            ) {

                requestAnimationFrame(
                    startStoryObserver
                );

            } else {

                stopStoryObserver();
            }


            window.scrollTo({
                top: 0,
                behavior: "auto"
            });
        }


        function updateHeader(
            name
        ) {

            const labels = {

                intro:
                    "천명 사주상담",


                name:
                    "기본정보 1 / 2",


                birth:
                    "기본정보 2 / 2",


                review:
                    "입력정보 확인",


                complete:
                    "분석 준비",


                analysis:
                    "명리 분석",


                result:
                    "종합 결과"

            };


            headerSubtitle.textContent =
                labels[name]
                || "천명 사주상담";
        }


        /*
         * =================================================
         * NAME
         * =================================================
         */

        function bindName() {

            nameInput.addEventListener(
                "input",
                validateName
            );


            nameForm.addEventListener(
                "submit",
                function (
                    event
                ) {

                    event.preventDefault();


                    if (
                            !validateName()
                    ) {

                        return;
                    }


                    sajuForm.name =
                        nameInput
                            .value
                            .trim();


                    saveForm();


                    showScreen(
                        "birth"
                    );
                }
            );
        }


        function restoreForm() {

            if (
                    sajuForm.name
            ) {

                nameInput.value =
                    sajuForm.name;
            }


            validateName();
        }


        function validateName() {

            const value =
                nameInput
                    .value
                    .trim();


            let errorMessage =
                "";


            if (
                    value
                    && value.length < 2
            ) {

                errorMessage =
                    "이름은 두 글자 이상 입력해주세요.";
            }


            nameError.textContent =
                errorMessage;


            nameNextButton.disabled =
                !value
                || Boolean(
                    errorMessage
                );


            return Boolean(
                value
            )
                && !errorMessage;
        }


        /*
         * =================================================
         * BIRTH
         * =================================================
         */

        function bindBirth() {

            document
                .querySelectorAll(
                    ".numeric-input"
                )
                .forEach(
                    function (
                        input
                    ) {

                        input.addEventListener(
                            "input",
                            function () {

                                birthTouched =
                                    true;


                                input.value =
                                    input.value.replace(
                                        /[^0-9]/g,
                                        ""
                                    );


                                validateBirth(
                                    true
                                );
                            }
                        );
                    }
                );


            /*
             * CALENDAR
             */

            document
                .querySelectorAll(
                    "[data-calendar]"
                )
                .forEach(
                    function (
                        button
                    ) {

                        button.addEventListener(
                            "click",
                            function () {

                                birthTouched =
                                    true;


                                document
                                    .querySelectorAll(
                                        "[data-calendar]"
                                    )
                                    .forEach(
                                        function (
                                            item
                                        ) {

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


                                lunarLeapArea.classList.toggle(
                                    "hidden",
                                    sajuForm.calendarType
                                    !== "LUNAR"
                                );


                                if (
                                        sajuForm.calendarType
                                        !== "LUNAR"
                                ) {

                                    lunarLeapMonth.checked =
                                        false;
                                }


                                validateBirth(
                                    true
                                );
                            }
                        );
                    }
                );


            /*
             * GENDER
             */

            document
                .querySelectorAll(
                    "[data-gender]"
                )
                .forEach(
                    function (
                        button
                    ) {

                        button.addEventListener(
                            "click",
                            function () {

                                birthTouched =
                                    true;


                                document
                                    .querySelectorAll(
                                        "[data-gender]"
                                    )
                                    .forEach(
                                        function (
                                            item
                                        ) {

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


                                validateBirth(
                                    true
                                );
                            }
                        );
                    }
                );


            /*
             * UNKNOWN TIME
             */

            birthTimeUnknown.addEventListener(
                "change",
                function () {

                    birthTouched =
                        true;


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


                    /*
                     * 출생시간 미상은 그대로 미상.
                     * 임의 시간을 넣지 않는다.
                     */

                    if (
                            unknown
                    ) {

                        birthHour.value =
                            "";


                        birthMinute.value =
                            "";
                    }


                    validateBirth(
                        true
                    );
                }
            );


            /*
             * SUBMIT
             */

            birthForm.addEventListener(
                "submit",
                function (
                    event
                ) {

                    event.preventDefault();


                    birthTouched =
                        true;


                    if (
                            !validateBirth(
                                true
                            )
                    ) {

                        return;
                    }


                    sajuForm.birthDate =
                        birthYear.value
                        + "-"
                        + pad2(
                            birthMonth.value
                        )
                        + "-"
                        + pad2(
                            birthDay.value
                        );


                    sajuForm.birthTimeUnknown =
                        birthTimeUnknown.checked;


                    sajuForm.birthTime =
                        birthTimeUnknown.checked

                            ? null

                            : pad2(
                                birthHour.value
                            )
                            + ":"
                            + pad2(
                                birthMinute.value
                            );


                    sajuForm.lunarLeapMonth =
                        sajuForm.calendarType
                        === "LUNAR"

                        && lunarLeapMonth.checked;


                    saveForm();


                    showScreen(
                        "review"
                    );
                }
            );
        }


        function restoreBirth() {

            /*
             * CALENDAR
             */

            document
                .querySelectorAll(
                    "[data-calendar]"
                )
                .forEach(
                    function (
                        button
                    ) {

                        button.classList.toggle(
                            "is-active",
                            button.dataset.calendar
                            === sajuForm.calendarType
                        );
                    }
                );


            /*
             * GENDER
             */

            document
                .querySelectorAll(
                    "[data-gender]"
                )
                .forEach(
                    function (
                        button
                    ) {

                        button.classList.toggle(
                            "is-active",
                            button.dataset.gender
                            === sajuForm.gender
                        );
                    }
                );


            lunarLeapArea.classList.toggle(
                "hidden",
                sajuForm.calendarType
                !== "LUNAR"
            );


            lunarLeapMonth.checked =
                Boolean(
                    sajuForm.lunarLeapMonth
                );


            /*
             * DATE
             */

            if (
                    sajuForm.birthDate
            ) {

                const parts =
                    sajuForm.birthDate.split(
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


            /*
             * TIME
             */

            birthTimeUnknown.checked =
                Boolean(
                    sajuForm.birthTimeUnknown
                );


            timeInputArea.classList.toggle(
                "is-disabled",
                birthTimeUnknown.checked
            );


            birthHour.disabled =
                birthTimeUnknown.checked;


            birthMinute.disabled =
                birthTimeUnknown.checked;


            if (
                    sajuForm.birthTime
                    && !sajuForm.birthTimeUnknown
            ) {

                const timeParts =
                    sajuForm.birthTime.split(
                        ":"
                    );


                birthHour.value =
                    timeParts[0]
                    || "";


                birthMinute.value =
                    timeParts[1]
                    || "";

            } else if (
                    sajuForm.birthTimeUnknown
            ) {

                birthHour.value =
                    "";


                birthMinute.value =
                    "";
            }
        }


        function validateBirth(
            showMessage
        ) {

            const show =
                showMessage !== false
                && birthTouched;


            if (
                    !sajuForm.gender
            ) {

                return birthInvalid(
                    "성별을 선택해주세요.",
                    show
                );
            }


            if (
                    !birthYear.value
                    || !birthMonth.value
                    || !birthDay.value
            ) {

                return birthInvalid(
                    "생년월일을 모두 입력해주세요.",
                    show
                );
            }


            if (
                    !validDate()
            ) {

                return birthInvalid(
                    "올바른 생년월일을 입력해주세요.",
                    show
                );
            }


            if (
                    !birthTimeUnknown.checked
                    && (
                    !birthHour.value
                    || !birthMinute.value
            )
            ) {

                return birthInvalid(
                    "태어난 시간을 입력하거나 시간 모름을 선택해주세요.",
                    show
                );
            }


            if (
                    !birthTimeUnknown.checked
                    && !validTime()
            ) {

                return birthInvalid(
                    "올바른 시간을 입력해주세요.",
                    show
                );
            }


            birthError.textContent =
                "";


            birthNextButton.disabled =
                false;


            return true;
        }


        function birthInvalid(
            message,
            show
        ) {

            birthError.textContent =
                show
                    ? message
                    : "";


            birthNextButton.disabled =
                true;


            return false;
        }


        function validDate() {

            const year =
                Number(
                    birthYear.value
                );


            const month =
                Number(
                    birthMonth.value
                );


            const day =
                Number(
                    birthDay.value
                );


            const currentYear =
                new Date()
                    .getFullYear();


            if (
                    !Number.isInteger(
                        year
                    )
                    || !Number.isInteger(
                        month
                    )
                    || !Number.isInteger(
                        day
                    )
            ) {

                return false;
            }


            if (
                    year < 1900
                    || year > currentYear
                    || month < 1
                    || month > 12
                    || day < 1
            ) {

                return false;
            }


            /*
             * 음력의 실제 유효성은
             * Java KoreanLunarCalendarCalculator가 최종 판단.
             *
             * 프론트에서는 기본 범위만 확인.
             */

            if (
                    sajuForm.calendarType
                    === "LUNAR"
            ) {

                return day <= 30;
            }


            const date =
                new Date(
                    year,
                    month - 1,
                    day
                );


            return date.getFullYear()
                === year

                && date.getMonth()
                === month - 1

                && date.getDate()
                === day;
        }


        function validTime() {

            const hour =
                Number(
                    birthHour.value
                );


            const minute =
                Number(
                    birthMinute.value
                );


            return Number.isInteger(
                hour
            )

                && Number.isInteger(
                    minute
                )

                && hour >= 0

                && hour <= 23

                && minute >= 0

                && minute <= 59;
        }


        /*
         * =================================================
         * REVIEW
         * =================================================
         */

        function bindReview() {

            reviewCompleteButton.addEventListener(
                "click",
                function () {

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
                sajuForm.name
            );


            setText(
                "reviewGender",
                sajuForm.gender
                === "MALE"

                    ? "남성"

                    : "여성"
            );


            setText(
                "reviewCalendar",
                sajuForm.calendarType
                === "SOLAR"

                    ? "양력"

                    : "음력"
            );


            setText(
                "reviewLeap",
                sajuForm.lunarLeapMonth

                    ? "윤달"

                    : "평달"
            );


            setText(
                "reviewBirthDate",
                sajuForm.birthDate
            );


            setText(
                "reviewBirthTime",
                sajuForm.birthTimeUnknown

                    ? "시간 모름"

                    : sajuForm.birthTime
            );


            required(
                "reviewLeapRow"
            )
                .classList
                .toggle(
                    "hidden",
                    sajuForm.calendarType
                    !== "LUNAR"
                );
        }


        /*
         * =================================================
         * ANALYSIS BUTTON
         * =================================================
         */

        function bindAnalysis() {

            startAnalysisButton.addEventListener(
                "click",
                runAnalysis
            );
        }


        /*
         * =================================================
         * RUN ANALYSIS
         * =================================================
         */

        async function runAnalysis() {

            if (
                    analysisRunning
            ) {

                return;
            }


            /*
             * sessionStorage가 직접 수정되었거나
             * 비정상 진입한 경우 방어.
             */

            if (
                    !sajuForm.name
                    || !sajuForm.birthDate
                    || !sajuForm.gender
            ) {

                showToast(
                    "입력정보를 다시 확인해주세요."
                );


                showScreen(
                    "name",
                    false
                );


                return;
            }


            analysisRunning =
                true;


            factSkipRequested =
                false;


            startAnalysisButton.disabled =
                true;


            try {

                resetAnalysis();


                showScreen(
                    "analysis"
                );


                startProgress();


                const request =
                    createAnalysisRequest();


                console.log(
                    "[SAJU] POST /api/saju/calculate"
                );


                console.log(
                    "[SAJU] request",
                    request
                );


                const startedAt =
                    Date.now();


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
                                    request
                                )

                        }
                    );


                const responseBody =
                    await readResponseBody(
                        response
                    );


                if (
                        !response.ok
                ) {

                    throw new Error(
                        resolveErrorMessage(
                            responseBody,
                            response.status
                        )
                    );
                }


                if (
                        !responseBody
                        || typeof responseBody
                        !== "object"
                ) {

                    throw new Error(
                        "서버 응답 형식이 올바르지 않습니다."
                    );
                }


                /*
                 * 최소 연출 시간.
                 */

                const elapsed =
                    Date.now()
                    - startedAt;


                if (
                        elapsed
                        < MINIMUM_ANALYSIS_TIME
                ) {

                    await wait(
                        MINIMUM_ANALYSIS_TIME
                        - elapsed
                    );
                }


                stopProgress();


                await finishProgress();


                const normalized =
                    normalizeResult(
                        responseBody
                    );


                sessionStorage.setItem(
                    RESULT_STORAGE_KEY,
                    JSON.stringify(
                        normalized
                    )
                );


                console.log(
                    "[SAJU] calculate response",
                    normalized
                );


                /*
                 * 분석 중에는
                 * Category별 Fact만 순차 표시.
                 */

                await revealCategoryFacts(
                    normalized.consultations
                );


                /*
                 * 최종화면에서만 Story 출력.
                 */

                renderResult(
                    normalized
                );


                showScreen(
                    "result",
                    false
                );

            } catch (
                error
            ) {

                stopProgress();


                console.error(
                    "[SAJU] 분석 실패",
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
            }
        }


        /*
         * =================================================
         * REQUEST
         * =================================================
         */

        function createAnalysisRequest() {

            return {

                calendarType:
                    sajuForm.calendarType,


                birthDate:
                    sajuForm.birthDate,


                /*
                 * 출생시간 미상은 null.
                 * 00:00 등의 가상 시간을 만들지 않는다.
                 */

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
                    sajuForm.calendarType
                    === "LUNAR"

                    && Boolean(
                        sajuForm.lunarLeapMonth
                    ),


                targetYear:
                    new Date()
                        .getFullYear(),


                /*
                 * 중요:
                 *
                 * 16 → 17 → 18 → 19 → 20
                 * deterministic grounding 경로 활성화.
                 *
                 * UTC 기준 toISOString() 사용 금지.
                 * 사용자 Civil Date 그대로 보존.
                 */

                luckReferenceDate:
                    localDateIso(
                        new Date()
                    ),


                /*
                 * luckReferenceTime은 보내지 않는다.
                 *
                 * date-only semantics 유지.
                 */


                consultationCategories:
                    CATEGORY_ORDER.slice(),


                consultationQuestion:
                    null

            };
        }


        /*
         * =================================================
         * RESPONSE READ
         * =================================================
         */

        async function readResponseBody(
            response
        ) {

            const contentType =
                response.headers.get(
                    "content-type"
                )
                || "";


            if (
                    contentType.includes(
                        "application/json"
                    )
            ) {

                return await response.json();
            }


            const text =
                await response.text();


            if (
                    !text
            ) {

                return null;
            }


            try {

                return JSON.parse(
                    text
                );

            } catch (
                error
            ) {

                return {
                    message:
                        text
                };
            }
        }


        function resolveErrorMessage(
            body,
            status
        ) {

            if (
                    body
                    && typeof body === "object"
            ) {

                const candidate =
                    firstNonEmpty(

                        body.message,

                        body.error,

                        body.detail,

                        body.title

                    );


                if (
                        candidate
                ) {

                    return candidate;
                }
            }


            return "사주 분석 요청에 실패했습니다. HTTP "
                + status;
        }


        /*
         * =================================================
         * ANALYSIS PROGRESS
         * =================================================
         */

        function resetAnalysis() {

            analysisProgress =
                0;


            updateProgress(
                0
            );


            analysisFactOverlay.classList.remove(
                "is-show"
            );


            factStatusRow.innerHTML =
                "";


            factBasis.innerHTML =
                "";


            analysisElements.forEach(
                function (
                    element
                ) {

                    element.classList.remove(
                        "is-active"
                    );
                }
            );
        }


        function startProgress() {

            stopProgress();


            progressTimer =
                setInterval(
                    function () {

                        if (
                                analysisProgress
                                >= WAIT_PROGRESS_LIMIT
                        ) {

                            return;
                        }


                        if (
                                analysisProgress < 20
                        ) {

                            analysisProgress +=
                                2;

                        } else if (
                                analysisProgress < 60
                        ) {

                            analysisProgress +=
                                1;

                        } else {

                            analysisProgress +=
                                0.5;
                        }


                        analysisProgress =
                            Math.min(
                                WAIT_PROGRESS_LIMIT,
                                analysisProgress
                            );


                        updateProgress(
                            analysisProgress
                        );

                    },
                    90
                );
        }


        function stopProgress() {

            if (
                    progressTimer !== null
            ) {

                clearInterval(
                    progressTimer
                );


                progressTimer =
                    null;
            }
        }


        async function finishProgress() {

            while (
                analysisProgress < 100
            ) {

                analysisProgress =
                    Math.min(
                        100,
                        analysisProgress + 2
                    );


                updateProgress(
                    analysisProgress
                );


                await wait(
                    32
                );
            }
        }


        function updateProgress(
            rawValue
        ) {

            const value =
                Math.round(
                    rawValue
                );


            analysisProgressPercent.textContent =
                value
                + "%";


            analysisProgressBar.style.width =
                value
                + "%";


            let status =
                "출생정보 확인";


            let title =
                "명식을 구성하고 있습니다";


            let message =
                "입력한 출생정보를 확인하고 있습니다.";


            if (
                    value >= 20
            ) {

                status =
                    "원국 분석";


                title =
                    "원국의 구조를 살펴봅니다";


                message =
                    "오행의 세력과 일간의 상태를 계산하고 있습니다.";
            }


            if (
                    value >= 42
            ) {

                status =
                    "용신·관계 분석";


                title =
                    "원국 내부의 관계를 교차 확인합니다";


                message =
                    "용신, 십신, 궁위와 관계 근거를 확인하고 있습니다.";
            }


            if (
                    value >= 65
            ) {

                status =
                    "대운·세운 분석";


                title =
                    "현재 운의 흐름을 연결합니다";


                message =
                    "원국과 대운·세운의 관계를 분석하고 있습니다.";
            }


            if (
                    value >= 82
            ) {

                status =
                    "상담 근거 구성";


                title =
                    "11개 분야의 근거를 정리합니다";


                message =
                    "계산된 근거를 상담 분야별로 정리하고 있습니다.";
            }


            if (
                    value >= 97
            ) {

                status =
                    "분석 완료";


                title =
                    "계산 결과를 정리했습니다";


                message =
                    "분야별 계산 근거를 확인합니다.";
            }


            analysisStatus.textContent =
                status;


            analysisTitle.textContent =
                title;


            analysisMessage.textContent =
                message;


            const activeElementCount =
                Math.min(
                    5,
                    Math.max(
                        0,
                        Math.ceil(
                            value / 17
                        )
                    )
                );


            analysisElements.forEach(
                function (
                    element,
                    index
                ) {

                    element.classList.toggle(
                        "is-active",
                        index
                        < activeElementCount
                    );
                }
            );
        }


        /*
         * =================================================
         * NORMALIZE RESULT
         * =================================================
         */

        function normalizeResult(
            result
        ) {

            const rawConsultations =
                Array.isArray(
                    result.consultations
                )

                    ? result.consultations

                    : [];


            const sectionMap =
                new Map();


            rawConsultations.forEach(
                function (
                    source
                ) {

                    if (
                            source
                            && source.category
                    ) {

                        sectionMap.set(
                            String(
                                source.category
                            ),
                            source
                        );
                    }
                }
            );


            const normalizedConsultations =
                CATEGORY_ORDER.map(
                    function (
                        category
                    ) {

                        return normalizeSection(
                            category,
                            sectionMap.get(
                                category
                            )
                        );
                    }
                );


            return {

                ...result,

                consultations:
                    normalizedConsultations

            };
        }


        function normalizeSection(
            category,
            source
        ) {

            const meta =
                CATEGORY_META[
                    category
                ];


            /*
             * API에 Section 자체가 없는 경우.
             *
             * 프론트에서 내용을 추정하지 않는다.
             */

            if (
                    !source
            ) {

                return {

                    category:
                        category,


                    meta:
                        meta,


                    available:
                        false,


                    summary:
                        "",


                    storyText:
                        "",


                    polarity:
                        "",


                    intensity:
                        "",


                    confidence:
                        "",


                    evidenceCount:
                        0,


                    basisLabels:
                        []

                };
            }


            /*
             * 현재 DTO에는 available 필드가 없지만
             * 향후 노출될 경우 그대로 존중한다.
             */

            const available =
                source.available !== false;


            const basisLabels =
                Array.isArray(
                    source.basisLabels
                )

                    ? source.basisLabels
                        .filter(
                            function (
                                value
                            ) {

                                return value !== null
                                    && value !== undefined
                                    && String(
                                        value
                                    )
                                        .trim()
                                        .length > 0;
                            }
                        )
                        .map(
                            function (
                                value
                            ) {

                                return String(
                                    value
                                )
                                    .trim();
                            }
                        )

                    : [];


            /*
             * 중요한 우선순위:
             *
             * 1. storyText
             *    정밀화 20차 → 외부 LLM 장문
             *
             * 2. narrationText
             *    기존 legacy narration
             *
             * 3. 기존 메시지 결합
             *
             * 프론트는 새 문장을 만들어내지 않는다.
             */

            const storyText =
                firstNonEmpty(

                    source.storyText,

                    source.narrationText,

                    joinUnique(

                        source.openingMessage,

                        source.summaryMessage,

                        source.guidanceMessage

                    )

                );


            if (
                    source.storyText
            ) {

                const length =
                    String(
                        source.storyText
                    )
                        .length;


                if (
                        length < 1600
                        || length > 2400
                ) {

                    console.warn(
                        "[SAJU] "
                        + category
                        + " storyText 분량 "
                        + length
                        + "자"
                    );
                }
            }


            return {

                category:
                    category,


                meta:
                    meta,


                available:
                    available,


                summary:
                    firstNonEmpty(

                        source.summaryMessage,

                        source.openingMessage,

                        meta.label

                    ),


                storyText:
                    storyText,


                polarity:
                    safeText(
                        source.polarity
                    ),


                intensity:
                    safeText(
                        source.intensity
                    ),


                confidence:
                    safeText(
                        source.confidence
                    ),


                evidenceCount:
                    normalizeEvidenceCount(
                        source.evidenceCount,
                        basisLabels.length
                    ),


                basisLabels:
                    basisLabels

            };
        }


        function normalizeEvidenceCount(
            sourceCount,
            fallback
        ) {

            const count =
                Number(
                    sourceCount
                );


            if (
                    Number.isFinite(
                        count
                    )
                    && count >= 0
            ) {

                return Math.floor(
                    count
                );
            }


            return fallback;
        }


        /*
         * =================================================
         * FACT
         * =================================================
         */

        function bindFacts() {

            skipFactsButton.addEventListener(
                "click",
                function () {

                    factSkipRequested =
                        true;
                }
            );
        }


        async function revealCategoryFacts(
            consultations
        ) {

            analysisFactOverlay.classList.add(
                "is-show"
            );


            for (
                let index = 0;
                index < consultations.length;
                index += 1
            ) {

                if (
                        factSkipRequested
                ) {

                    break;
                }


                renderFact(
                    consultations[index],
                    index
                );


                await waitForFactDuration();
            }


            analysisFactOverlay.classList.remove(
                "is-show"
            );
        }


        async function waitForFactDuration() {

            const interval =
                50;


            let elapsed =
                0;


            while (
                elapsed < FACT_DISPLAY_TIME
            ) {

                if (
                        factSkipRequested
                ) {

                    return;
                }


                await wait(
                    interval
                );


                elapsed +=
                    interval;
            }
        }


        function renderFact(
            section,
            index
        ) {

            factCategoryIndex.textContent =
                String(
                    index + 1
                )
                    .padStart(
                        2,
                        "0"
                    )
                + " / "
                + String(
                    CATEGORY_ORDER.length
                )
                    .padStart(
                        2,
                        "0"
                    );


            factCategoryKicker.textContent =
                section.available

                    ? "DETERMINISTIC FACT"

                    : "UNAVAILABLE";


            factCategoryTitle.textContent =
                section.meta.label;


            /*
             * 분석 화면에서는
             * 서술형 summary/story를 출력하지 않는다.
             *
             * 팩트 필드만 출력.
             */

            const statusValues =
                [];


            if (
                    section.polarity
            ) {

                statusValues.push(
                    labelPolarity(
                        section.polarity
                    )
                );
            }


            if (
                    section.intensity
            ) {

                statusValues.push(
                    labelIntensity(
                        section.intensity
                    )
                );
            }


            if (
                    section.confidence
            ) {

                statusValues.push(
                    labelConfidence(
                        section.confidence
                    )
                );
            }


            statusValues.push(
                "근거 "
                + section.evidenceCount
                + "개"
            );


            factStatusRow.innerHTML =
                statusValues
                    .map(
                        function (
                            value
                        ) {

                            return "<span>"
                                + escapeHtml(
                                    value
                                )
                                + "</span>";
                        }
                    )
                    .join(
                        ""
                    );


            if (
                    section.basisLabels.length
                    === 0
            ) {

                factBasis.innerHTML =
                    "<span>제공된 계산 근거 없음</span>";

                return;
            }


            factBasis.innerHTML =
                section.basisLabels
                    .slice(
                        0,
                        12
                    )
                    .map(
                        function (
                            value
                        ) {

                            return "<span>"
                                + escapeHtml(
                                    value
                                )
                                + "</span>";
                        }
                    )
                    .join(
                        ""
                    );
        }


        /*
         * =================================================
         * RESULT
         * =================================================
         */

        function renderResult(
            result
        ) {

            resultTitle.textContent =
                sajuForm.name
                + "님의 사주 이야기";


            const firstSection =
                result.consultations.find(
                    function (
                        section
                    ) {

                        return section.available
                            && section.summary;
                    }
                );


            if (
                    firstSection
            ) {

                resultSummary.textContent =
                    shorten(
                        firstSection.summary,
                        120
                    );

            } else {

                resultSummary.textContent =
                    "계산된 사주 근거를 분야별로 정리했습니다.";
            }


            renderPillars(
                result
            );


            resultStoryList.innerHTML =
                result.consultations
                    .map(
                        function (
                            section,
                            index
                        ) {

                            return renderStory(
                                section,
                                index
                            );
                        }
                    )
                    .join(
                        ""
                    );
        }


        /*
         * =================================================
         * FOUR PILLARS
         * =================================================
         */

        function renderPillars(
            result
        ) {

            const pillars = [

                {

                    label:
                        "년주",

                    pillar:
                        result.yearPillar

                },


                {

                    label:
                        "월주",

                    pillar:
                        result.monthPillar

                },


                {

                    label:
                        "일주",

                    pillar:
                        result.dayPillar

                },


                {

                    label:
                        "시주",

                    pillar:
                        result.birthTimeUnknown

                            ? null

                            : result.hourPillar

                }

            ];


            resultPillars.innerHTML =
                pillars
                    .map(
                        function (
                            item
                        ) {

                            if (
                                    !item.pillar
                            ) {

                                return `
                                    <article class="pillar-card">

                                        <span>
                                            ${escapeHtml(item.label)}
                                        </span>

                                        <strong>
                                            ?
                                        </strong>

                                        <small>
                                            시간 미상
                                        </small>

                                    </article>
                                `;
                            }


                            return `
                                <article class="pillar-card">

                                    <span>
                                        ${escapeHtml(item.label)}
                                    </span>

                                    <strong>
                                        ${escapeHtml(
                                            safeText(
                                                item.pillar.chineseName,
                                                "-"
                                            )
                                        )}
                                    </strong>

                                    <small>
                                        ${escapeHtml(
                                            safeText(
                                                item.pillar.koreanName,
                                                ""
                                            )
                                        )}
                                    </small>

                                </article>
                            `;
                        }
                    )
                    .join(
                        ""
                    );
        }


        /*
         * =================================================
         * STORY
         * =================================================
         */

        function renderStory(
            section,
            index
        ) {

            const darkClass =
                index % 2 === 1

                    ? "is-dark"

                    : "";


            if (
                    !section.available
            ) {

                return `
                    <article class="story-card ${darkClass}">

                        <div class="story-heading">

                            <div>

                                <span>
                                    CHAPTER ${escapeHtml(section.meta.number)}
                                </span>

                                <h2>
                                    ${escapeHtml(section.meta.title)}
                                </h2>

                            </div>

                            <strong>
                                ${escapeHtml(section.meta.number)}
                            </strong>

                        </div>

                        <p class="story-unavailable">
                            현재 계산 결과에서는 이 분야를 독립적으로 설명할 근거가 제공되지 않았습니다.
                            없는 내용을 추정해서 채우지 않습니다.
                        </p>

                    </article>
                `;
            }


            const facts =
                [];


            if (
                    section.polarity
            ) {

                facts.push(
                    labelPolarity(
                        section.polarity
                    )
                );
            }


            if (
                    section.intensity
            ) {

                facts.push(
                    labelIntensity(
                        section.intensity
                    )
                );
            }


            if (
                    section.confidence
            ) {

                facts.push(
                    labelConfidence(
                        section.confidence
                    )
                );
            }


            facts.push(
                "근거 "
                + section.evidenceCount
                + "개"
            );


            const factHtml =
                facts
                    .map(
                        function (
                            value
                        ) {

                            return "<span>"
                                + escapeHtml(
                                    value
                                )
                                + "</span>";
                        }
                    )
                    .join(
                        ""
                    );


            const paragraphs =
                splitParagraphs(
                    section.storyText
                );


            /*
             * Story를 프론트에서 새로 만들지 않는다.
             */

            const bodyHtml =
                paragraphs.length > 0

                    ? paragraphs
                        .map(
                            function (
                                paragraph
                            ) {

                                return "<p>"
                                    + escapeHtml(
                                        paragraph
                                    )
                                    + "</p>";
                            }
                        )
                        .join(
                            ""
                        )

                    : `
                        <p>
                            이 분야의 장문 스토리 원문이 아직 응답에 연결되지 않았습니다.
                        </p>
                    `;


            const evidenceHtml =
                section.basisLabels
                    .slice(
                        0,
                        14
                    )
                    .map(
                        function (
                            value
                        ) {

                            return "<span>"
                                + escapeHtml(
                                    value
                                )
                                + "</span>";
                        }
                    )
                    .join(
                        ""
                    );


            return `
                <article class="story-card ${darkClass}">

                    <div class="story-heading">

                        <div>

                            <span>
                                CHAPTER ${escapeHtml(section.meta.number)}
                            </span>

                            <h2>
                                ${escapeHtml(section.meta.title)}
                            </h2>

                        </div>

                        <strong>
                            ${escapeHtml(section.meta.number)}
                        </strong>

                    </div>


                    <div class="story-facts">
                        ${factHtml}
                    </div>


                    ${
                        section.summary

                            ? `
                                <p class="story-summary">
                                    ${escapeHtml(section.summary)}
                                </p>
                            `

                            : ""
                    }


                    <div class="story-body">
                        ${bodyHtml}
                    </div>


                    ${
                        evidenceHtml

                            ? `
                                <details class="story-evidence">

                                    <summary>
                                        계산 근거 보기
                                    </summary>

                                    <div>
                                        ${evidenceHtml}
                                    </div>

                                </details>
                            `

                            : ""
                    }

                </article>
            `;
        }


        /*
         * =================================================
         * STORY PARAGRAPH
         * =================================================
         */

        function splitParagraphs(
            source
        ) {

            const text =
                safeText(
                    source
                )
                    .trim();


            if (
                    !text
            ) {

                return [];
            }


            /*
             * LLM이 이미 문단을 만든 경우
             * 그대로 보존.
             */

            const explicitParagraphs =
                text
                    .split(
                        /\n\s*\n/
                    )
                    .map(
                        function (
                            paragraph
                        ) {

                            return paragraph.trim();
                        }
                    )
                    .filter(
                        Boolean
                    );


            if (
                    explicitParagraphs.length
                    > 1
            ) {

                return explicitParagraphs;
            }


            /*
             * 기존 narrationText처럼
             * 한 덩어리로 온 경우에만
             * 화면 가독성을 위해 문장 경계에서 나눈다.
             *
             * 새로운 문장은 생성하지 않는다.
             */

            const sentences =
                text
                    .split(
                        /(?<=[.!?。])\s+/
                    )
                    .map(
                        function (
                            sentence
                        ) {

                            return sentence.trim();
                        }
                    )
                    .filter(
                        Boolean
                    );


            if (
                    sentences.length <= 1
            ) {

                return [
                    text
                ];
            }


            const result =
                [];


            let buffer =
                [];


            let currentLength =
                0;


            sentences.forEach(
                function (
                    sentence
                ) {

                    buffer.push(
                        sentence
                    );


                    currentLength +=
                        sentence.length;


                    if (
                            currentLength >= 240
                    ) {

                        result.push(
                            buffer.join(
                                " "
                            )
                        );


                        buffer =
                            [];


                        currentLength =
                            0;
                    }
                }
            );


            if (
                    buffer.length > 0
            ) {

                result.push(
                    buffer.join(
                        " "
                    )
                );
            }


            return result;
        }


        /*
         * =================================================
         * STORY REVEAL
         * =================================================
         */

        function startStoryObserver() {

            stopStoryObserver();


            const cards =
                Array.from(
                    document.querySelectorAll(
                        ".story-card"
                    )
                );


            if (
                    !(
                        "IntersectionObserver"
                        in window
                    )
            ) {

                cards.forEach(
                    function (
                        card
                    ) {

                        card.classList.add(
                            "is-visible"
                        );
                    }
                );


                return;
            }


            storyObserver =
                new IntersectionObserver(
                    function (
                        entries
                    ) {

                        entries.forEach(
                            function (
                                entry
                            ) {

                                if (
                                        !entry.isIntersecting
                                ) {

                                    return;
                                }


                                entry.target.classList.add(
                                    "is-visible"
                                );


                                storyObserver.unobserve(
                                    entry.target
                                );
                            }
                        );
                    },
                    {

                        threshold:
                            0.08

                    }
                );


            cards.forEach(
                function (
                    card
                ) {

                    storyObserver.observe(
                        card
                    );
                }
            );
        }


        function stopStoryObserver() {

            if (
                    storyObserver
            ) {

                storyObserver.disconnect();


                storyObserver =
                    null;
            }
        }


        /*
         * =================================================
         * LABELS
         * =================================================
         *
         * 확실히 알고 있는 값만 한글화.
         *
         * 정의되지 않은 Enum은
         * 의미를 추측하지 않고 원문 그대로 표시.
         */

        function labelPolarity(
            value
        ) {

            const labels = {

                SUPPORTIVE:
                    "지원 흐름",


                PRESSURE:
                    "압력 흐름",


                MIXED:
                    "복합 흐름",


                CONTEXTUAL:
                    "상황 의존"

            };


            return labels[value]
                || String(
                    value
                );
        }


        function labelIntensity(
            value
        ) {

            const labels = {

                LOW:
                    "낮은 강도",


                MEDIUM:
                    "중간 강도",


                HIGH:
                    "높은 강도",


                NONE:
                    "강도 없음"

            };


            return labels[value]
                || String(
                    value
                );
        }


        function labelConfidence(
            value
        ) {

            const labels = {

                LOW:
                    "낮은 신뢰도",


                MEDIUM:
                    "중간 신뢰도",


                HIGH:
                    "높은 신뢰도"

            };


            /*
             * DEFINITE / POSSIBLE 등
             * 다른 불확실성 체계와
             * 임의로 같은 의미라고 처리하지 않는다.
             */

            return labels[value]
                || String(
                    value
                );
        }


        /*
         * =================================================
         * LOCAL DATE
         * =================================================
         */

        function localDateIso(
            date
        ) {

            return date.getFullYear()
                + "-"
                + String(
                    date.getMonth()
                    + 1
                )
                    .padStart(
                        2,
                        "0"
                    )
                + "-"
                + String(
                    date.getDate()
                )
                    .padStart(
                        2,
                        "0"
                    );
        }


        /*
         * =================================================
         * UTIL
         * =================================================
         */

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


        function safeText(
            value,
            fallback
        ) {

            if (
                    value === null
                    || value === undefined
                    || value === ""
            ) {

                return fallback
                    || "";
            }


            return String(
                value
            );
        }


        function firstNonEmpty() {

            const values =
                Array.from(
                    arguments
                );


            for (
                const value
                of values
            ) {

                const text =
                    safeText(
                        value
                    )
                        .trim();


                if (
                        text
                ) {

                    return text;
                }
            }


            return "";
        }


        function joinUnique() {

            const values =
                Array.from(
                    arguments
                );


            const seen =
                new Set();


            return values
                .map(
                    function (
                        value
                    ) {

                        return safeText(
                            value
                        )
                            .trim();
                    }
                )
                .filter(
                    Boolean
                )
                .filter(
                    function (
                        value
                    ) {

                        if (
                                seen.has(
                                    value
                                )
                        ) {

                            return false;
                        }


                        seen.add(
                            value
                        );


                        return true;
                    }
                )
                .join(
                    "\n\n"
                );
        }


        function shorten(
            value,
            maxLength
        ) {

            const text =
                safeText(
                    value
                );


            if (
                    text.length
                    <= maxLength
            ) {

                return text;
            }


            return text.slice(
                0,
                maxLength
            )
                + "…";
        }


        function setText(
            id,
            value
        ) {

            const element =
                document.getElementById(
                    id
                );


            if (
                    !element
            ) {

                return;
            }


            element.textContent =
                safeText(
                    value
                );
        }


        function wait(
            milliseconds
        ) {

            return new Promise(
                function (
                    resolve
                ) {

                    setTimeout(
                        resolve,
                        milliseconds
                    );
                }
            );
        }


        function showToast(
            message
        ) {

            clearTimeout(
                toastTimer
            );


            toast.textContent =
                safeText(
                    message,
                    "오류가 발생했습니다."
                );


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
                    2400
                );
        }


        function escapeHtml(
            value
        ) {

            const element =
                document.createElement(
                    "div"
                );


            element.textContent =
                safeText(
                    value
                );


            return element.innerHTML;
        }

    }
);