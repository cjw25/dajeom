document.addEventListener(
    "DOMContentLoaded",
    function () {

        "use strict";


        /* =====================================================
           CONSTANT
        ====================================================== */

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



        /* =====================================================
           11 CONSULTATION CATEGORY
        ====================================================== */

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

                chapter:
                    "제1장",

                label:
                    "기본 성향",

                title:
                    "나는 어떤 사람인가"

            },


            PAST_LUCK: {

                number:
                    "02",

                chapter:
                    "제2장",

                label:
                    "지나온 흐름",

                title:
                    "지나온 시간에 반복된 흐름"

            },


            SOCIAL_RELATIONSHIP: {

                number:
                    "03",

                chapter:
                    "제3장",

                label:
                    "대인관계",

                title:
                    "사람과 관계를 맺는 방식"

            },


            LOVE_STYLE: {

                number:
                    "04",

                chapter:
                    "제4장",

                label:
                    "연애 스타일",

                title:
                    "마음이 움직일 때 드러나는 모습"

            },


            PARTNER_LUCK: {

                number:
                    "05",

                chapter:
                    "제5장",

                label:
                    "인연·배우자",

                title:
                    "인연을 바라보는 기준과 거리"

            },


            WEALTH: {

                number:
                    "06",

                chapter:
                    "제6장",

                label:
                    "재물",

                title:
                    "돈을 벌고 지키고 쓰는 흐름"

            },


            CAREER: {

                number:
                    "07",

                chapter:
                    "제7장",

                label:
                    "직업·사업",

                title:
                    "일과 책임을 다루는 방식"

            },


            LEGAL_RISK: {

                number:
                    "08",

                chapter:
                    "제8장",

                label:
                    "분쟁·주의",

                title:
                    "조심해서 다뤄야 하는 지점"

            },


            HEALTH_TENDENCY: {

                number:
                    "09",

                chapter:
                    "제9장",

                label:
                    "건강·생활리듬",

                title:
                    "오래 가기 위한 생활 리듬"

            },


            CURRENT_YEAR_LUCK: {

                number:
                    "10",

                chapter:
                    "제10장",

                label:
                    "현재·올해",

                title:
                    "지금 지나고 있는 운의 장면"

            },


            FUTURE_LUCK: {

                number:
                    "11",

                chapter:
                    "제11장",

                label:
                    "앞으로의 흐름",

                title:
                    "앞으로 이어질 흐름"

            }

        };



        /* =====================================================
           SCREEN
        ====================================================== */

        const screens =
            Array.from(
                document.querySelectorAll(
                    "[data-screen]"
                )
            );



        /* =====================================================
           REQUIRED DOM
        ====================================================== */

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



        /* =====================================================
           OPTIONAL INTRO WORLD DOM
        ====================================================== */

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


        const openingVideo =
            optional(
                "openingVideo"
            );



        /* =====================================================
           NAME
        ====================================================== */

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



        /* =====================================================
           BIRTH
        ====================================================== */

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



        /* =====================================================
           REVIEW
        ====================================================== */

        const reviewCompleteButton =
            required(
                "reviewCompleteButton"
            );



        /* =====================================================
           COMPLETE
        ====================================================== */

        const startAnalysisButton =
            required(
                "startAnalysisButton"
            );



        /* =====================================================
           ANALYSIS
        ====================================================== */

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


        const analysisVideo1 =
            optional(
                "analysisVideo1"
            );


        const analysisVideo2 =
            optional(
                "analysisVideo2"
            );



        /* =====================================================
           FACT
        ====================================================== */

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


        const factMotionVideo =
            optional(
                "factMotionVideo"
            );



        /* =====================================================
           OPTIONAL REVEAL
        ====================================================== */

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



        /* =====================================================
           RESULT
        ====================================================== */

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



        /* =====================================================
           STATE
        ====================================================== */

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


        let revealSkipRequested =
            false;


        let introSkipRequested =
            false;


        let introPlayed =
            false;


        let currentStoryIndex =
            0;


        let currentResult =
            null;


        let storyTouchStartX =
            null;


        let storyTouchStartY =
            null;



        /* =====================================================
           INITIALIZE
        ====================================================== */

        initialize();


        function initialize() {

            bindNavigation();

            bindIntro();

            bindName();

            bindBirth();

            bindReview();

            bindAnalysis();

            bindFacts();

            bindReveal();

            bindStoryNavigation();


            restoreForm();


            showScreen(
                "intro",
                false
            );


            playIntroWorld();


            console.log(
                "[SAJU] saju.js 초기화 완료"
            );


            console.log(
                "[SAJU] 천명록 11장 전환 방식 활성화"
            );

        }



        /* =====================================================
           DOM UTIL
        ====================================================== */

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


        function optional(
            id
        ) {

            return document.getElementById(
                id
            );
        }



        /* =====================================================
           INTRO WORLD
        ====================================================== */

        function bindIntro() {

            if (
                introWorldSkip
            ) {

                introWorldSkip.addEventListener(
                    "click",
                    function () {

                        introSkipRequested =
                            true;


                        stopVideo(
                            openingVideo
                        );


                        showIntroFinal();

                    }
                );

            }

        }


        async function playIntroWorld() {

            if (
                introPlayed
            ) {

                showIntroFinal();

                return;
            }


            introPlayed =
                true;


            introSkipRequested =
                false;


            if (
                openingVideo
            ) {

                await playVideo(
                    openingVideo,
                    VIDEO_PATH.opening,
                    function () {

                        return introSkipRequested;

                    }
                );


                if (
                    !introSkipRequested
                ) {

                    showIntroFinal();

                }


                return;
            }


            if (
                !hasIntroWorldDom()
            ) {

                return;
            }


            await wait(
                650
            );


            if (
                introSkipRequested
            ) {

                return;
            }


            await playIntroLine(
                introWorldLine1,
                1900
            );


            if (
                introSkipRequested
            ) {

                return;
            }


            await playIntroLine(
                introWorldLine2,
                2100
            );


            if (
                introSkipRequested
            ) {

                return;
            }


            await playIntroLine(
                introWorldLine3,
                2100
            );


            if (
                introSkipRequested
            ) {

                return;
            }


            if (
                introWorldEmblem
            ) {

                introWorldEmblem.classList.add(
                    "is-visible"
                );

            }


            await wait(
                1500
            );


            if (
                introSkipRequested
            ) {

                return;
            }


            showIntroFinal();

        }


        function hasIntroWorldDom() {

            return Boolean(
                introWorldLine1
                || introWorldLine2
                || introWorldLine3
                || introWorldEmblem
                || introWorldFinal
            );

        }


        async function playIntroLine(
            element,
            duration
        ) {

            if (
                !element
            ) {

                return;
            }


            element.classList.remove(
                "is-hidden"
            );


            element.classList.add(
                "is-visible"
            );


            await wait(
                duration
            );


            if (
                introSkipRequested
            ) {

                return;
            }


            element.classList.remove(
                "is-visible"
            );


            element.classList.add(
                "is-hidden"
            );


            await wait(
                650
            );

        }


        function showIntroFinal() {

            [

                introWorldLine1,

                introWorldLine2,

                introWorldLine3

            ]
                .filter(
                    Boolean
                )
                .forEach(
                    function (
                        element
                    ) {

                        element.classList.remove(
                            "is-visible"
                        );


                        element.classList.add(
                            "is-hidden"
                        );

                    }
                );


            if (
                introWorldEmblem
            ) {

                introWorldEmblem.classList.add(
                    "is-visible"
                );

            }


            if (
                introWorldFinal
            ) {

                introWorldFinal.classList.add(
                    "is-visible"
                );

            }


            if (
                introStartButton
            ) {

                introStartButton.classList.add(
                    "is-visible"
                );

            }


            if (
                introWorldSkip
            ) {

                introWorldSkip.style.display =
                    "none";

            }

        }



        /* =====================================================
           FORM STORAGE
        ====================================================== */

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



        /* =====================================================
           NAVIGATION
        ====================================================== */

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


                    const screenName =
                        target.dataset.go;


                    if (
                        !screenExists(
                            screenName
                        )
                    ) {

                        console.warn(
                            "[SAJU] 존재하지 않는 data-go 화면",
                            screenName
                        );


                        return;
                    }


                    showScreen(
                        screenName
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
                        === "result"
                        && currentResult
                        && currentStoryIndex > 0
                    ) {

                        goPreviousStory();

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


        function screenExists(
            name
        ) {

            return screens.some(
                function (
                    screen
                ) {

                    return screen.dataset.screen
                        === name;

                }
            );

        }


        function showScreen(
            name,
            addHistory
        ) {

            const shouldAddHistory =
                addHistory !== false;


            if (
                !screenExists(
                    name
                )
            ) {

                console.warn(
                    "[SAJU] 존재하지 않는 화면 요청:",
                    name
                );


                return false;
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
                name === "birth"
            ) {

                restoreBirth();


                birthTouched =
                    false;


                validateBirth(
                    false
                );

            }


            if (
                name === "review"
            ) {

                renderReview();

            }


            if (
                name === "intro"
                && introPlayed
                && hasIntroWorldDom()
            ) {

                showIntroFinal();

            }


            if (
                name === "result"
                && currentResult
            ) {

                renderCurrentStory();

            }


            window.scrollTo({

                top:
                    0,

                behavior:
                    "auto"

            });


            return true;
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


                reveal:
                    "천명 공개",


                result:
                    "천명록"

            };


            headerSubtitle.textContent =
                labels[name]
                || "천명 사주상담";

        }



        /* =====================================================
           NAME
        ====================================================== */

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



        /* =====================================================
           BIRTH
        ====================================================== */

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



        /* =====================================================
           REVIEW
        ====================================================== */

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


            const leapRow =
                optional(
                    "reviewLeapRow"
                );


            if (
                leapRow
            ) {

                leapRow
                    .classList
                    .toggle(
                        "hidden",
                        sajuForm.calendarType
                        !== "LUNAR"
                    );

            }

        }



        /* =====================================================
           ANALYSIS
        ====================================================== */

        function bindAnalysis() {

            startAnalysisButton.addEventListener(
                "click",
                runAnalysis
            );

        }


        async function runAnalysis() {

            if (
                analysisRunning
            ) {

                return;
            }


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


            revealSkipRequested =
                false;


            startAnalysisButton.disabled =
                true;


            try {

                resetAnalysis();


                showScreen(
                    "analysis"
                );


                startProgress();


                if (
                    analysisVideo1
                ) {

                    playVideoDetached(
                        analysisVideo1,
                        VIDEO_PATH.analysis1
                    );

                }


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


                if (
                    analysisVideo2
                ) {

                    stopVideo(
                        analysisVideo1
                    );


                    await playVideo(
                        analysisVideo2,
                        VIDEO_PATH.analysis2
                    );

                }


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


                await revealCategoryFacts(
                    normalized.consultations
                );


                renderResult(
                    normalized
                );


                if (
                    screenExists(
                        "reveal"
                    )
                    && hasRevealDom()
                ) {

                    await playResultReveal(
                        normalized
                    );

                }


                showScreen(
                    "result",
                    false
                );

            } catch (
                error
            ) {

                stopProgress();


                stopVideo(
                    analysisVideo1
                );


                stopVideo(
                    analysisVideo2
                );


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



        /* =====================================================
           REQUEST
        ====================================================== */

        function createAnalysisRequest() {

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
                    sajuForm.calendarType
                    === "LUNAR"

                    && Boolean(
                        sajuForm.lunarLeapMonth
                    ),


                targetYear:
                    new Date()
                        .getFullYear(),


                luckReferenceDate:
                    localDateIso(
                        new Date()
                    ),


                consultationCategories:
                    CATEGORY_ORDER.slice(),


                consultationQuestion:
                    null

            };

        }



        /* =====================================================
           RESPONSE
        ====================================================== */

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
                || contentType.includes(
                    "application/problem+json"
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



        /* =====================================================
           PROGRESS
        ====================================================== */

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
                        index < activeElementCount
                    );

                }
            );

        }



        /* =====================================================
           NORMALIZE RESULT
        ====================================================== */

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


            if (
                !source
            ) {

                return createUnavailableSection(
                    category,
                    meta
                );

            }


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


            const available =
                resolveSectionAvailable(
                    source,
                    storyText
                );


            if (
                storyText
                && available
            ) {

                const length =
                    storyText.length;


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


        function createUnavailableSection(
            category,
            meta
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


        function resolveSectionAvailable(
            source,
            storyText
        ) {

            if (
                typeof source.available
                === "boolean"
            ) {

                return source.available;
            }


            const normalized =
                safeText(
                    storyText
                );


            if (
                normalized.includes(
                    "현재 계산 근거만으로는 이 분야의 독립적인 해석을 확정하지 않습니다."
                )
            ) {

                return false;
            }


            if (
                normalized.includes(
                    "근거가 없는 내용을 추정해서 채우지 않았습니다."
                )
            ) {

                return false;
            }


            return true;
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



        /* =====================================================
           FACT
        ====================================================== */

        function bindFacts() {

            skipFactsButton.addEventListener(
                "click",
                function () {

                    factSkipRequested =
                        true;


                    stopVideo(
                        factMotionVideo
                    );

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


                const section =
                    consultations[
                        index
                    ];


                renderFact(
                    section,
                    index
                );


                playFactMotionVideo(
                    section
                );


                await waitForFactDuration();

            }


            stopVideo(
                factMotionVideo
            );


            analysisFactOverlay.classList.remove(
                "is-show"
            );

        }


        function playFactMotionVideo(
            section
        ) {

            if (
                !factMotionVideo
                || !section
            ) {

                return;
            }


            let source =
                "";


            if (
                section.category
                === "SOCIAL_RELATIONSHIP"
            ) {

                source =
                    VIDEO_PATH.network;

            } else if (
                section.category
                === "LOVE_STYLE"
            ) {

                source =
                    VIDEO_PATH.love;

            } else if (
                section.category
                === "WEALTH"
            ) {

                source =
                    VIDEO_PATH.wealth;

            }


            if (
                !source
            ) {

                stopVideo(
                    factMotionVideo
                );


                factMotionVideo.removeAttribute(
                    "src"
                );


                return;
            }


            playVideoDetached(
                factMotionVideo,
                source
            );

        }


        async function waitForFactDuration() {

            const interval =
                50;


            let elapsed =
                0;


            while (
                elapsed
                < FACT_DISPLAY_TIME
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

                    : "GROUNDING LIMITED";


            factCategoryTitle.textContent =
                section.meta.label;


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



        /* =====================================================
           RESULT REVEAL
        ====================================================== */

        function bindReveal() {

            if (
                !revealSkipButton
            ) {

                return;
            }


            revealSkipButton.addEventListener(
                "click",
                function () {

                    revealSkipRequested =
                        true;

                }
            );

        }


        function hasRevealDom() {

            return Boolean(

                revealOpening

                && revealPillarGrid

                && revealFinal

            );

        }


        async function playResultReveal(
            result
        ) {

            if (
                !screenExists(
                    "reveal"
                )
                || !hasRevealDom()
            ) {

                return;
            }


            revealSkipRequested =
                false;


            resetReveal();


            fillRevealPillars(
                result
            );


            showScreen(
                "reveal",
                false
            );


            await wait(
                500
            );


            if (
                revealSkipRequested
            ) {

                return;
            }


            revealOpening.classList.add(
                "is-visible"
            );


            await waitCancelable(
                2200,
                function () {

                    return revealSkipRequested;

                }
            );


            if (
                revealSkipRequested
            ) {

                return;
            }


            revealOpening.classList.remove(
                "is-visible"
            );


            revealOpening.classList.add(
                "is-hidden"
            );


            await wait(
                650
            );


            if (
                revealSkipRequested
            ) {

                return;
            }


            revealPillarGrid.classList.add(
                "is-visible"
            );


            const revealOrder = [

                "year",

                "month",

                "day",

                "hour"

            ];


            for (
                const pillarName
                of revealOrder
            ) {

                if (
                    revealSkipRequested
                ) {

                    return;
                }


                const pillar =
                    document.querySelector(
                        "[data-reveal-pillar=\""
                        + pillarName
                        + "\"]"
                    );


                if (
                    pillar
                ) {

                    pillar.classList.add(
                        "is-visible"
                    );

                }


                await waitCancelable(
                    720,
                    function () {

                        return revealSkipRequested;

                    }
                );

            }


            if (
                revealSkipRequested
            ) {

                return;
            }


            await wait(
                1050
            );


            if (
                revealSkipRequested
            ) {

                return;
            }


            revealPillarGrid.classList.remove(
                "is-visible"
            );


            document
                .querySelectorAll(
                    "[data-reveal-pillar]"
                )
                .forEach(
                    function (
                        element
                    ) {

                        element.classList.remove(
                            "is-visible"
                        );

                    }
                );


            await wait(
                500
            );


            if (
                revealSkipRequested
            ) {

                return;
            }


            revealFinal.classList.add(
                "is-visible"
            );


            await waitCancelable(
                2200,
                function () {

                    return revealSkipRequested;

                }
            );

        }


        function resetReveal() {

            if (
                revealOpening
            ) {

                revealOpening.classList.remove(
                    "is-visible",
                    "is-hidden"
                );

            }


            if (
                revealPillarGrid
            ) {

                revealPillarGrid.classList.remove(
                    "is-visible"
                );

            }


            if (
                revealFinal
            ) {

                revealFinal.classList.remove(
                    "is-visible"
                );

            }


            document
                .querySelectorAll(
                    "[data-reveal-pillar]"
                )
                .forEach(
                    function (
                        element
                    ) {

                        element.classList.remove(
                            "is-visible"
                        );

                    }
                );

        }


        function fillRevealPillars(
            result
        ) {

            if (
                !hasRevealDom()
            ) {

                return;
            }


            fillRevealPillar(
                "Year",
                result.yearPillar,
                false
            );


            fillRevealPillar(
                "Month",
                result.monthPillar,
                false
            );


            fillRevealPillar(
                "Day",
                result.dayPillar,
                false
            );


            fillRevealPillar(
                "Hour",
                result.birthTimeUnknown
                    ? null
                    : result.hourPillar,
                Boolean(
                    result.birthTimeUnknown
                )
            );

        }


        function fillRevealPillar(
            prefix,
            pillar,
            unknown
        ) {

            const chineseElement =
                optional(
                    "reveal"
                    + prefix
                    + "Chinese"
                );


            const koreanElement =
                optional(
                    "reveal"
                    + prefix
                    + "Korean"
                );


            if (
                !chineseElement
                || !koreanElement
            ) {

                return;
            }


            if (
                unknown
                || !pillar
            ) {

                chineseElement.textContent =
                    "?";


                koreanElement.textContent =
                    unknown
                        ? "시간 미상"
                        : "계산값 없음";


                return;
            }


            chineseElement.textContent =
                safeText(
                    pillar.chineseName,
                    "-"
                );


            koreanElement.textContent =
                safeText(
                    pillar.koreanName,
                    ""
                );

        }



        /* =====================================================
           RESULT
        ====================================================== */

        function renderResult(
            result
        ) {

            currentResult =
                result;


            currentStoryIndex =
                0;


            resultTitle.textContent =
                sajuForm.name
                + "님의 천명록";


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
                    "계산된 사주 근거를 열한 장의 천명록으로 정리했습니다.";

            }


            renderPillars(
                result
            );


            renderCurrentStory();

        }



        /* =====================================================
           FOUR PILLARS
        ====================================================== */

        function renderPillars(
            result
        ) {

            const birthTimeUnknown =
                Boolean(
                    result.birthTimeUnknown
                );


            const pillars = [

                {

                    label:
                        "년주",

                    pillar:
                        result.yearPillar,

                    unknown:
                        false

                },


                {

                    label:
                        "월주",

                    pillar:
                        result.monthPillar,

                    unknown:
                        false

                },


                {

                    label:
                        "일주",

                    pillar:
                        result.dayPillar,

                    unknown:
                        false

                },


                {

                    label:
                        "시주",

                    pillar:
                        birthTimeUnknown
                            ? null
                            : result.hourPillar,

                    unknown:
                        birthTimeUnknown

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
                                            ${
                                                item.unknown
                                                    ? "시간 미상"
                                                    : "계산값 없음"
                                            }
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



        /* =====================================================
           STORY NAVIGATION
        ====================================================== */

        function bindStoryNavigation() {

            resultStoryList.addEventListener(
                "click",
                function (
                    event
                ) {

                    const previousButton =
                        event.target.closest(
                            "[data-story-prev]"
                        );


                    if (
                        previousButton
                    ) {

                        goPreviousStory();

                        return;
                    }


                    const nextButton =
                        event.target.closest(
                            "[data-story-next]"
                        );


                    if (
                        nextButton
                    ) {

                        goNextStory();

                        return;
                    }


                    const restartButton =
                        event.target.closest(
                            "[data-story-restart]"
                        );


                    if (
                        restartButton
                    ) {

                        currentStoryIndex =
                            0;


                        renderCurrentStory();


                        scrollStoryTop();

                        return;
                    }


                    const finishButton =
                        event.target.closest(
                            "[data-story-finish]"
                        );


                    if (
                        finishButton
                    ) {

                        window.location.href =
                            "/";

                    }

                }
            );


            resultStoryList.addEventListener(
                "touchstart",
                function (
                    event
                ) {

                    if (
                        !event.touches
                        || event.touches.length !== 1
                    ) {

                        return;
                    }


                    storyTouchStartX =
                        event.touches[0].clientX;


                    storyTouchStartY =
                        event.touches[0].clientY;

                },
                {
                    passive:
                        true
                }
            );


            resultStoryList.addEventListener(
                "touchend",
                function (
                    event
                ) {

                    if (
                        storyTouchStartX === null
                        || storyTouchStartY === null
                        || !event.changedTouches
                        || event.changedTouches.length !== 1
                    ) {

                        resetStoryTouch();

                        return;
                    }


                    const endX =
                        event.changedTouches[0].clientX;


                    const endY =
                        event.changedTouches[0].clientY;


                    const deltaX =
                        endX
                        - storyTouchStartX;


                    const deltaY =
                        endY
                        - storyTouchStartY;


                    resetStoryTouch();


                    if (
                        Math.abs(
                            deltaX
                        ) < 65
                    ) {

                        return;
                    }


                    if (
                        Math.abs(
                            deltaY
                        )
                        > Math.abs(
                            deltaX
                        )
                    ) {

                        return;
                    }


                    if (
                        deltaX < 0
                    ) {

                        goNextStory();

                    } else {

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
                function (
                    event
                ) {

                    if (
                        currentScreen
                        !== "result"
                        || !currentResult
                    ) {

                        return;
                    }


                    if (
                        event.key
                        === "ArrowLeft"
                    ) {

                        event.preventDefault();


                        goPreviousStory();

                    }


                    if (
                        event.key
                        === "ArrowRight"
                    ) {

                        event.preventDefault();


                        goNextStory();

                    }

                }
            );

        }


        function resetStoryTouch() {

            storyTouchStartX =
                null;


            storyTouchStartY =
                null;

        }


        function goPreviousStory() {

            if (
                !currentResult
            ) {

                return;
            }


            if (
                currentStoryIndex <= 0
            ) {

                return;
            }


            currentStoryIndex -=
                1;


            renderCurrentStory();


            scrollStoryTop();

        }


        function goNextStory() {

            if (
                !currentResult
            ) {

                return;
            }


            const sections =
                currentResult.consultations;


            if (
                currentStoryIndex
                < sections.length - 1
            ) {

                currentStoryIndex +=
                    1;


                renderCurrentStory();


                scrollStoryTop();


                return;
            }


            renderStoryCompletion();


            scrollStoryTop();

        }


        function scrollStoryTop() {

            requestAnimationFrame(
                function () {

                    const top =
                        resultStoryList
                            .getBoundingClientRect()
                            .top
                        + window.scrollY
                        - 90;


                    window.scrollTo({

                        top:
                            Math.max(
                                0,
                                top
                            ),

                        behavior:
                            "smooth"

                    });

                }
            );

        }



        /* =====================================================
           CURRENT STORY
        ====================================================== */

        function renderCurrentStory() {

            if (
                !currentResult
                || !Array.isArray(
                    currentResult.consultations
                )
                || currentResult.consultations.length === 0
            ) {

                resultStoryList.innerHTML =
                    `
                        <div class="story-empty">
                            천명록을 표시할 수 없습니다.
                        </div>
                    `;


                return;
            }


            currentStoryIndex =
                Math.max(
                    0,
                    Math.min(
                        currentStoryIndex,
                        currentResult.consultations.length - 1
                    )
                );


            const section =
                currentResult.consultations[
                    currentStoryIndex
                ];


            resultStoryList.innerHTML =
                renderStoryBook(
                    section,
                    currentStoryIndex,
                    currentResult.consultations.length
                );

        }


        function renderStoryBook(
            section,
            index,
            total
        ) {

            const previousDisabled =
                index === 0;


            const last =
                index === total - 1;


            const progressPercent =
                Math.round(
                    (
                        (
                            index + 1
                        )
                        / total
                    )
                    * 100
                );


            return `
                <section
                    class="cheonmyeong-book"
                    data-current-story="${escapeHtml(section.category)}"
                >

                    <div class="cheonmyeong-book-progress">

                        <div class="cheonmyeong-book-progress-head">

                            <span>
                                천명록
                            </span>

                            <strong>
                                ${String(index + 1).padStart(2, "0")}
                                /
                                ${String(total).padStart(2, "0")}
                            </strong>

                        </div>


                        <div class="cheonmyeong-book-progress-track">

                            <div
                                class="cheonmyeong-book-progress-bar"
                                style="width:${progressPercent}%"
                            ></div>

                        </div>

                    </div>


                    ${renderStoryPage(section)}


                    <nav
                        class="cheonmyeong-book-navigation"
                        aria-label="천명록 장 이동"
                    >

                        <button
                            type="button"
                            class="cheonmyeong-book-nav-button is-prev"
                            data-story-prev
                            ${previousDisabled ? "disabled" : ""}
                        >
                            <span aria-hidden="true">
                                ←
                            </span>

                            <span>
                                이전 장
                            </span>
                        </button>


                        ${
                            last

                                ? `
                                    <button
                                        type="button"
                                        class="cheonmyeong-book-nav-button is-next is-finish"
                                        data-story-next
                                    >
                                        <span>
                                            천명록 마치기
                                        </span>

                                        <span aria-hidden="true">
                                            ✓
                                        </span>
                                    </button>
                                `

                                : `
                                    <button
                                        type="button"
                                        class="cheonmyeong-book-nav-button is-next"
                                        data-story-next
                                    >
                                        <span>
                                            다음 장
                                        </span>

                                        <span aria-hidden="true">
                                            →
                                        </span>
                                    </button>
                                `
                        }

                    </nav>


                    <p class="cheonmyeong-book-swipe-hint">
                        좌우로 넘겨 다음 장을 볼 수 있습니다.
                    </p>

                </section>
            `;

        }



        /* =====================================================
           STORY PAGE
        ====================================================== */

        function renderStoryPage(
            section
        ) {

            if (
                !section.available
            ) {

                return `
                    <article class="story-card story-page is-visible">

                        <header class="story-page-header">

                            <span class="story-page-book-label">
                                천명록
                            </span>

                            <span class="story-page-chapter">
                                ${escapeHtml(section.meta.chapter)}
                            </span>

                            <h2>
                                ${escapeHtml(section.meta.title)}
                            </h2>

                            <p>
                                ${escapeHtml(section.meta.label)}
                            </p>

                        </header>


                        <div class="story-page-seal">
                            命
                        </div>


                        <div class="story-page-body">

                            <p class="story-unavailable">
                                현재 계산 결과에서는 이 분야를
                                독립적으로 설명할 근거가 충분하지 않습니다.
                                없는 내용을 임의로 추정해서 채우지 않습니다.
                            </p>

                        </div>


                        ${renderEvidence(section)}

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

                            return `
                                <span>
                                    ${escapeHtml(value)}
                                </span>
                            `;

                        }
                    )
                    .join(
                        ""
                    );


            const paragraphs =
                splitParagraphs(
                    section.storyText
                );


            const bodyHtml =
                paragraphs.length > 0

                    ? paragraphs
                        .map(
                            function (
                                paragraph
                            ) {

                                return `
                                    <p>
                                        ${escapeHtml(paragraph)}
                                    </p>
                                `;

                            }
                        )
                        .join(
                            ""
                        )

                    : `
                        <p>
                            이 분야의 장문 상담문이
                            아직 응답에 연결되지 않았습니다.
                        </p>
                    `;


            return `
                <article class="story-card story-page is-visible">

                    <header class="story-page-header">

                        <span class="story-page-book-label">
                            천명록
                        </span>

                        <span class="story-page-chapter">
                            ${escapeHtml(section.meta.chapter)}
                        </span>

                        <h2>
                            ${escapeHtml(section.meta.title)}
                        </h2>

                        <p>
                            ${escapeHtml(section.meta.label)}
                        </p>

                    </header>


                    <div class="story-page-seal">
                        命
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


                    <div class="story-body story-page-body">
                        ${bodyHtml}
                    </div>


                    ${renderEvidence(section)}

                </article>
            `;

        }


        function renderEvidence(
            section
        ) {

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

                            return `
                                <span>
                                    ${escapeHtml(value)}
                                </span>
                            `;

                        }
                    )
                    .join(
                        ""
                    );


            if (
                !evidenceHtml
            ) {

                return "";
            }


            return `
                <details class="story-evidence">

                    <summary>
                        계산 근거 보기
                    </summary>

                    <div>
                        ${evidenceHtml}
                    </div>

                </details>
            `;

        }



        /* =====================================================
           STORY COMPLETION
        ====================================================== */

        function renderStoryCompletion() {

            resultStoryList.innerHTML =
                `
                    <section class="cheonmyeong-book-complete">

                        <div class="cheonmyeong-book-complete-seal">
                            命
                        </div>


                        <p class="cheonmyeong-book-complete-kicker">
                            天命錄
                        </p>


                        <h2>
                            천명록 완독
                        </h2>


                        <p class="cheonmyeong-book-complete-copy">
                            열한 장의 천명록을
                            모두 확인했습니다.
                        </p>


                        <p class="cheonmyeong-book-complete-subcopy">
                            사주는 하나의 문장으로
                            삶을 확정하는 것이 아니라,
                            여러 흐름이 어디에서 만나고
                            어떻게 움직이는지를 살펴보는 기록입니다.
                        </p>


                        <div class="cheonmyeong-book-complete-actions">

                            <button
                                type="button"
                                class="cheonmyeong-book-complete-button is-secondary"
                                data-story-restart
                            >
                                처음부터 다시 읽기
                            </button>


                            <button
                                type="button"
                                class="cheonmyeong-book-complete-button is-primary"
                                data-story-finish
                            >
                                천명록 마치기
                            </button>

                        </div>

                    </section>
                `;

        }



        /* =====================================================
           PARAGRAPH
        ====================================================== */

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
                explicitParagraphs.length > 1
            ) {

                return explicitParagraphs;
            }


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



        /* =====================================================
           LABELS
        ====================================================== */

        function labelPolarity(
            value
        ) {

            const labels = {

                SUPPORTIVE:
                    "지원 흐름",


                PRESSURE:
                    "압력 흐름",


                CAUTION:
                    "주의 흐름",


                NEUTRAL:
                    "중립 흐름",


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


            return labels[value]
                || String(
                    value
                );
        }



        /* =====================================================
           VIDEO
        ====================================================== */

        async function playVideo(
            element,
            source,
            shouldCancel
        ) {

            if (
                !element
            ) {

                return;
            }


            if (
                source
                && element.getAttribute(
                    "src"
                ) !== source
            ) {

                element.src =
                    source;

            }


            element.currentTime =
                0;


            try {

                await element.play();

            } catch (
                error
            ) {

                console.warn(
                    "[SAJU] 영상 자동재생 실패",
                    source,
                    error
                );


                return;
            }


            await new Promise(
                function (
                    resolve
                ) {

                    let finished =
                        false;


                    const finish =
                        function () {

                            if (
                                finished
                            ) {

                                return;
                            }


                            finished =
                                true;


                            element.removeEventListener(
                                "ended",
                                finish
                            );


                            resolve();

                        };


                    element.addEventListener(
                        "ended",
                        finish
                    );


                    const watch =
                        setInterval(
                            function () {

                                if (
                                    finished
                                ) {

                                    clearInterval(
                                        watch
                                    );


                                    return;
                                }


                                if (
                                    typeof shouldCancel
                                    === "function"

                                    && shouldCancel()
                                ) {

                                    clearInterval(
                                        watch
                                    );


                                    stopVideo(
                                        element
                                    );


                                    finish();

                                }

                            },
                            50
                        );

                }
            );

        }


        function playVideoDetached(
            element,
            source
        ) {

            if (
                !element
            ) {

                return;
            }


            if (
                source
                && element.getAttribute(
                    "src"
                ) !== source
            ) {

                element.src =
                    source;

            }


            try {

                element.currentTime =
                    0;


                const promise =
                    element.play();


                if (
                    promise
                    && typeof promise.catch
                    === "function"
                ) {

                    promise.catch(
                        function (
                            error
                        ) {

                            console.warn(
                                "[SAJU] 영상 재생 실패",
                                source,
                                error
                            );

                        }
                    );

                }

            } catch (
                error
            ) {

                console.warn(
                    "[SAJU] 영상 재생 실패",
                    source,
                    error
                );

            }

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


                element.currentTime =
                    0;

            } catch (
                error
            ) {

                console.debug(
                    "[SAJU] 영상 정지 생략",
                    error
                );

            }

        }



        /* =====================================================
           LOCAL DATE
        ====================================================== */

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



        /* =====================================================
           UTIL
        ====================================================== */

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


        async function waitCancelable(
            milliseconds,
            shouldCancel
        ) {

            const interval =
                50;


            let remaining =
                milliseconds;


            while (
                remaining > 0
            ) {

                if (
                    shouldCancel()
                ) {

                    return;
                }


                const waitTime =
                    Math.min(
                        interval,
                        remaining
                    );


                await wait(
                    waitTime
                );


                remaining -=
                    waitTime;

            }

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