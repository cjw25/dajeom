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
   LOADING
   ========================================================= */

const LOADING_WAIT_ROTATION_MS =
    3400;

const LOADING_STAGES = [
    {
        until:
            20,

        label:
            "분석중",

        message:
            "태어난 순간의 네 기둥을 하나씩 맞춰보고 있어."
    },

    {
        until:
            40,

        label:
            "흐름을 읽는 중",

        message:
            "타고난 성향과 사람을 대하는 방식을 살펴보고 있어."
    },

    {
        until:
            60,

        label:
            "천명록을 구성하는 중",

        message:
            "관계와 인연, 재물의 흐름을 하나씩 이어보고 있어."
    },

    {
        until:
            80,

        label:
            "조금만 기다려줘",

        message:
            "일과 책임, 생활의 흐름까지 함께 정리하고 있어."
    },

    {
        until:
            94,

        label:
            "마지막 기록을 정리하는 중",

        message:
            "현재의 흐름과 앞으로 이어질 시간을 살펴보고 있어."
    }
];

const LOADING_WAIT_MESSAGES = [
    {
        label:
            "거의 다 왔어",

        message:
            "네 이야기를 한 장씩 정리하고 있어. 조금만 더 기다려줘."
    },

    {
        label:
            "마지막 확인 중",

        message:
            "지금까지 읽은 흐름을 마지막으로 한 번 더 맞춰보고 있어."
    },

    {
        label:
            "천명록을 완성하는 중",

        message:
            "네게 보여줄 기록을 차례대로 정돈하고 있어."
    },

    {
        label:
            "조금만 더 기다려줘",

        message:
            "마지막 내용을 확인하고 있어."
    }
];

const LOADING_ACTOR_LINES = [
    "잠시만.\n지금 네 흐름을 읽고 있어.",
    "조금만.\n아직 확인할 게 남았어.",
    "생각보다\n이어지는 게 많네.",
    "거의 다 봤어.\n한 장씩 정리하고 있어.",
    "조금만 더 기다려.\n마지막 흐름을 맞춰보고 있어.",
    "이제 거의 끝났어.\n곧 네 기록을 보여줄게."
];


/* =========================================================
   CATEGORY
   ========================================================= */

const CATEGORY_META = {
    PERSONALITY: {
        number:
            1,

        chapter:
            "제1장",

        label:
            "타고난 성향",

        title:
            "나는 어떤 사람인가",

        intro:
            "첫 번째 기록은 네가 어떤 사람인지부터 시작할게.",

        visual:
            "image",

        source:
            "/img/category_bg.png"
    },

    PAST_LUCK: {
        number:
            2,

        chapter:
            "제2장",

        label:
            "지나온 운",

        title:
            "지나온 시간의 흐름",

        intro:
            "이번에는 지금까지 네가 지나온 시간의 흐름을 돌아보자.",

        visual:
            "image",

        source:
            "/img/loading_bg.png"
    },

    SOCIAL_RELATIONSHIP: {
        number:
            3,

        chapter:
            "제3장",

        label:
            "대인관계",

        title:
            "사람과 관계를 맺는 방식",

        intro:
            "이제 사람을 만나고 관계를 이어가는 네 방식을 볼게.",

        visual:
            "video",

        source:
            VIDEO_PATH.network
    },

    LOVE_STYLE: {
        number:
            4,

        chapter:
            "제4장",

        label:
            "연애 성향",

        title:
            "마음을 주고받는 방식",

        intro:
            "이번 장은 사랑에 관한 이야기야. 마음을 주고받는 방식을 살펴보자.",

        visual:
            "video",

        source:
            VIDEO_PATH.love
    },

    PARTNER_LUCK: {
        number:
            5,

        chapter:
            "제5장",

        label:
            "배우자운",

        title:
            "인연과 동반자의 흐름",

        intro:
            "사랑 다음에는 오래 함께할 인연의 흐름을 읽어볼게.",

        visual:
            "video",

        source:
            VIDEO_PATH.love
    },

    WEALTH: {
        number:
            6,

        chapter:
            "제6장",

        label:
            "재물운",

        title:
            "재물을 다루는 흐름",

        intro:
            "이제 돈을 만들고 지키고 움직이는 네 흐름을 살펴보자.",

        visual:
            "video",

        source:
            VIDEO_PATH.wealth
    },

    CAREER: {
        number:
            7,

        chapter:
            "제7장",

        label:
            "직업운",

        title:
            "일과 성취를 향하는 방식",

        intro:
            "이번에는 일과 책임, 그리고 네가 성취를 만들어가는 방식을 볼게.",

        visual:
            "video",

        source:
            VIDEO_PATH.network
    },

    LEGAL_RISK: {
        number:
            8,

        chapter:
            "제8장",

        label:
            "분쟁과 주의",

        title:
            "갈등과 책임을 다루는 흐름",

        intro:
            "조금 더 조심해서 봐야 할 부분이야. 갈등과 책임의 흐름을 살펴볼게.",

        visual:
            "image",

        source:
            "/img/category_bg.png"
    },

    HEALTH_TENDENCY: {
        number:
            9,

        chapter:
            "제9장",

        label:
            "건강 경향",

        title:
            "균형과 회복의 흐름",

        intro:
            "이번 장에서는 생활의 균형과 회복 리듬을 살펴보자.",

        visual:
            "image",

        source:
            "/img/loading_bg.png"
    },

    CURRENT_YEAR_LUCK: {
        number:
            10,

        chapter:
            "제10장",

        label:
            "올해의 운",

        title:
            "지금 흐르는 운의 방향",

        intro:
            "이제 현재로 돌아오자. 지금 네 앞에 흐르고 있는 운을 볼게.",

        visual:
            "image",

        source:
            "/img/category_bg.png"
    },

    FUTURE_LUCK: {
        number:
            11,

        chapter:
            "제11장",

        label:
            "앞으로의 운",

        title:
            "다가오는 흐름을 바라보는 법",

        intro:
            "마지막 장이야. 이제 앞으로 이어질 흐름을 천천히 바라보자.",

        visual:
            "image",

        source:
            "/img/category_bg.png"
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

const OFFICIAL_SECTION_STATUS =
    new Set(
        [
            "ANALYSIS_UNAVAILABLE",
            "NOT_REQUESTED",
            "UNAVAILABLE",
            "GENERATION_UNAVAILABLE",
            "READY"
        ]
    );

const RENDERABLE_SECTION_STATUS =
    new Set(
        [
            "READY",
            "UNAVAILABLE",
            "GENERATION_UNAVAILABLE"
        ]
    );


/* =========================================================
   FIVE ELEMENT
   ========================================================= */

const FIVE_ELEMENT_META = {
    WOOD: {
        order:
            1,

        hanja:
            "木",

        korean:
            "목"
    },

    FIRE: {
        order:
            2,

        hanja:
            "火",

        korean:
            "화"
    },

    EARTH: {
        order:
            3,

        hanja:
            "土",

        korean:
            "토"
    },

    METAL: {
        order:
            4,

        hanja:
            "金",

        korean:
            "금"
    },

    WATER: {
        order:
            5,

        hanja:
            "水",

        korean:
            "수"
    }
};

const FIVE_ELEMENT_ORDER =
    Object
        .keys(
            FIVE_ELEMENT_META
        )
        .sort(
            (
                left,
                right
            ) =>
                FIVE_ELEMENT_META[left].order
                -
                FIVE_ELEMENT_META[right].order
        );


/* =========================================================
   STATE
   ========================================================= */

let currentScreen =
    "start";

let currentResult =
    null;

let currentStoryIndex =
    0;

let bookOpened =
    false;

let analysisRunning =
    false;

let analysisRequestPromise =
    null;

let loadingTimer =
    null;

let loadingWaitTimer =
    null;

let loadingProgress =
    0;

let loadingDialogueStage =
    -1;

let loadingWaitMessageIndex =
    0;

let loadingActorRunId =
    0;

let loadingActorTimer =
    null;

let loadingActorLineIndex =
    0;

let toastTimer =
    null;

let touchStartX =
    null;

let currentStoryVideo =
    null;

let guideRunId =
    0;

let guideHideTimer =
    null;

let formState = {
    name:
        "",

    calendarType:
        "SOLAR",

    leapMonth:
        false,

    gender:
        "",

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

const startButton =
    required("startButton");

const openingVideo =
    required("openingVideo");

const skipOpeningButton =
    required("skipOpeningButton");

const nameInput =
    required("nameInput");

const nameNextButton =
    required("nameNextButton");

const nameError =
    required("nameError");

const birthYear =
    required("birthYear");

const birthMonth =
    required("birthMonth");

const birthDay =
    required("birthDay");

const solarButton =
    required("solarButton");

const lunarButton =
    required("lunarButton");

const leapMonthButton =
    required("leapMonthButton");

const birthError =
    required("birthError");

const birthNextButton =
    required("birthNextButton");

const timeRaw =
    required("timeRaw");

const timeMaskWrap =
    required("timeMaskWrap");

const unknownTimeButton =
    required("unknownTimeButton");

const timeError =
    required("timeError");

const timeNextButton =
    required("timeNextButton");

const femaleButton =
    required("femaleButton");

const maleButton =
    required("maleButton");

const genderError =
    required("genderError");

const analyzeButton =
    required("analyzeButton");

const analysisVideo1 =
    required("analysisVideo1");

const skipAnalysisButton =
    required("skipAnalysisButton");

const loadingLabel =
    required("loadingLabel");

const loadingPercent =
    required("loadingPercent");

const loadingMessage =
    required("loadingMessage");

const loadingActorDialogue =
    required("loadingActorDialogue");

const loadingActorText =
    required("loadingActorText");

const analysisVideo2 =
    required("analysisVideo2");

const skipAnalysisFinishButton =
    required("skipAnalysisFinishButton");

const resultIntro =
    required("resultIntro");

const resultName =
    required("resultName");

const pillarGrid =
    required("pillarGrid");

const fiveElementGrid =
    required("fiveElementGrid");

const fiveElementNote =
    required("fiveElementNote");

const openBookButton =
    required("openBookButton");

const resultStoryList =
    required("resultStoryList");

const resultScrollArea =
    required("resultScrollArea");

const resultHomeTopButton =
    required("resultHomeTopButton");

const toast =
    required("toast");

const guideLayer =
    required("guideLayer");

const guideText =
    required("guideText");

const guideSpeaker =
    required("guideSpeaker");


/* =========================================================
   INITIALIZE
   ========================================================= */

initialize();

function initialize() {
    loadStoredForm();

    restoreFormState();

    bindNavigation();

    bindIntro();

    bindName();

    bindBirth();

    bindTime();

    bindGender();

    bindAnalysis();

    bindResult();

    bindStoryNavigation();

    prepareVideoPaths();

    updateCalendarUI();

    updateTimeMask();

    updateTimeUI();

    updateGenderUI();
}


/* =========================================================
   GUIDE
   ========================================================= */

function cancelGuide() {
    guideRunId++;

    if (
        guideHideTimer
    ) {
        clearTimeout(
            guideHideTimer
        );

        guideHideTimer =
            null;
    }

    guideLayer
        .classList
        .remove(
            "visible"
        );
}

async function say(
    text,
    options = {}
) {
    const runId =
        ++guideRunId;

    const placement =
        options.placement
        ||
        "bottom";

    const speed =
        options.speed
        ??
        24;

    const hold =
        options.hold
        ??
        1300;

    guideSpeaker.textContent =
        options.speaker
        ||
        "천명 안내자";

    guideText.textContent =
        "";

    guideLayer
        .classList
        .remove(
            "top",
            "middle"
        );

    if (
        placement
        === "top"
    ) {
        guideLayer
            .classList
            .add(
                "top"
            );
    }

    guideLayer
        .classList
        .add(
            "visible"
        );

    for (
        const character
        of Array.from(
            String(text ?? "")
        )
    ) {
        if (
            runId
            !== guideRunId
        ) {
            return false;
        }

        guideText.textContent +=
            character;

        await delay(
            speed
        );
    }

    await delay(
        hold
    );

    if (
        runId
        !== guideRunId
    ) {
        return false;
    }

    if (
        options.keep
        !== true
    ) {
        guideLayer
            .classList
            .remove(
                "visible"
            );
    }

    return true;
}

async function guideSequence(
    lines,
    options = {}
) {
    for (
        const line
        of lines
    ) {
        const completed =
            await say(
                line,
                options
            );

        if (
            !completed
        ) {
            return false;
        }

        await delay(
            options.gap
            ??
            180
        );
    }

    return true;
}


/* =========================================================
   NAVIGATION
   ========================================================= */

function bindNavigation() {
    document.addEventListener(
        "click",
        event => {
            const back =
                event
                    .target
                    .closest(
                        "[data-back]"
                    );

            if (
                !back
            ) {
                return;
            }

            showScreen(
                back.dataset.back
            );
        }
    );
}

function showScreen(
    name,
    options = {}
) {
    cancelGuide();

    if (
        name
        !== "loading"
    ) {
        stopLoadingActorDialogue();
    }

    screens.forEach(
        screen => {
            screen
                .classList
                .toggle(
                    "active",
                    screen.dataset.screen
                    === name
                );
        }
    );

    currentScreen =
        name;

    if (
        options.dialogue
        === false
    ) {
        return;
    }

    window.setTimeout(
        () => {
            if (
                currentScreen
                === name
            ) {
                runSceneDialogue(
                    name
                );
            }
        },
        220
    );
}

function runSceneDialogue(
    screen
) {
    switch (
        screen
    ) {
        case "name":
            say(
                "먼저, 내가 널 뭐라고 부르면 될까?",
                {
                    placement:
                        "top"
                }
            );
            break;

        case "birth":
            say(
                formState.name
                    ? `좋아, ${formState.name}. 이제 네가 태어난 날을 알려줘.`
                    : "이제 네가 태어난 날을 알려줘.",
                {
                    placement:
                        "top"
                }
            );
            break;

        case "time":
            say(
                "그리고 태어난 시간을 알고 있다면 더 깊이 살펴볼 수 있어.",
                {
                    placement:
                        "top"
                }
            );
            break;

        case "gender":
            say(
                "거의 다 왔어. 마지막으로 성별을 알려줘.",
                {
                    placement:
                        "top"
                }
            );
            break;
    }
}


/* =========================================================
   INTRO
   ========================================================= */

function bindIntro() {
    startButton.addEventListener(
        "click",
        async () => {
            showScreen(
                "opening",
                {
                    dialogue:
                        false
                }
            );

            guideSequence(
                [
                    "왔구나. 네가 태어난 순간부터 이미 하나의 기록이 시작되고 있었어.",
                    "그 기록을 찾으려면 먼저 몇 가지를 알려줘."
                ],
                {
                    placement:
                        "bottom",
                    hold:
                        1000
                }
            );

            const played =
                await playVideo(
                    openingVideo,
                    VIDEO_PATH.opening,
                    true
                );

            if (
                played
                &&
                currentScreen
                === "opening"
            ) {
                showScreen(
                    "name"
                );
            }
        }
    );

    skipOpeningButton.addEventListener(
        "click",
        () => {
            finishVideoImmediately(
                openingVideo
            );

            if (
                currentScreen
                === "opening"
            ) {
                showScreen(
                    "name"
                );
            }
        }
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
                nameInput.value.trim();

            nameError.textContent =
                "";

            saveForm();
        }
    );

    nameInput.addEventListener(
        "keydown",
        event => {
            if (
                event.key
                === "Enter"
            ) {
                event.preventDefault();

                moveNameNext();
            }
        }
    );

    nameNextButton.addEventListener(
        "click",
        moveNameNext
    );
}

function moveNameNext() {
    formState.name =
        nameInput
            .value
            .trim();

    if (
        !formState.name
    ) {
        nameError.textContent =
            "이름을 입력해주세요.";

        return;
    }

    saveForm();

    showScreen(
        "birth"
    );

    setTimeout(
        () =>
            birthYear.focus(),
        200
    );
}


/* =========================================================
   BIRTH
   ========================================================= */

function bindBirth() {
    bindNumericInput(
        birthYear,
        4
    );

    bindNumericInput(
        birthMonth,
        2
    );

    bindNumericInput(
        birthDay,
        2
    );

    birthYear.addEventListener(
        "input",
        () => {
            syncDateStateFromInputs();

            saveForm();

            if (
                birthYear.value.length
                === 4
            ) {
                birthMonth.focus();
            }
        }
    );

    birthMonth.addEventListener(
        "input",
        () => {
            syncDateStateFromInputs();

            saveForm();

            if (
                birthMonth.value.length
                === 2
            ) {
                birthDay.focus();
            }
        }
    );

    birthMonth.addEventListener(
        "blur",
        () => {
            normalizeMonthInput();

            syncDateStateFromInputs();

            saveForm();
        }
    );

    birthDay.addEventListener(
        "input",
        () => {
            syncDateStateFromInputs();

            saveForm();
        }
    );

    birthDay.addEventListener(
        "blur",
        () => {
            normalizeDayInput();

            syncDateStateFromInputs();

            saveForm();
        }
    );

    solarButton.addEventListener(
        "click",
        () =>
            setCalendar(
                "SOLAR"
            )
    );

    lunarButton.addEventListener(
        "click",
        () =>
            setCalendar(
                "LUNAR"
            )
    );

    leapMonthButton.addEventListener(
        "click",
        () => {
            if (
                formState.calendarType
                !== "LUNAR"
            ) {
                return;
            }

            formState.leapMonth =
                !formState.leapMonth;

            updateCalendarUI();

            saveForm();
        }
    );

    birthNextButton.addEventListener(
        "click",
        () => {
            normalizeMonthInput();

            normalizeDayInput();

            syncDateStateFromInputs();

            if (
                !validateBirthDate()
            ) {
                return;
            }

            saveForm();

            showScreen(
                "time"
            );
        }
    );
}

function bindNumericInput(
    input,
    maxLength
) {
    input.addEventListener(
        "input",
        () => {
            input.value =
                onlyDigits(
                    input.value
                )
                    .slice(
                        0,
                        maxLength
                    );
        }
    );
}

function normalizeMonthInput() {
    const value =
        onlyDigits(
            birthMonth.value
        );

    if (
        value.length
        === 1
    ) {
        birthMonth.value =
            value.padStart(
                2,
                "0"
            );
    }
}

function normalizeDayInput() {
    const value =
        onlyDigits(
            birthDay.value
        );

    if (
        value.length
        === 1
    ) {
        birthDay.value =
            value.padStart(
                2,
                "0"
            );
    }
}

function syncDateStateFromInputs() {
    formState.birthYear =
        onlyDigits(
            birthYear.value
        );

    formState.birthMonth =
        onlyDigits(
            birthMonth.value
        );

    formState.birthDay =
        onlyDigits(
            birthDay.value
        );
}

function setCalendar(
    type
) {
    formState.calendarType =
        type;

    if (
        type
        !== "LUNAR"
    ) {
        formState.leapMonth =
            false;
    }

    updateCalendarUI();

    saveForm();
}

function updateCalendarUI() {
    solarButton
        .classList
        .toggle(
            "active",
            formState.calendarType
            === "SOLAR"
        );

    lunarButton
        .classList
        .toggle(
            "active",
            formState.calendarType
            === "LUNAR"
        );

    leapMonthButton
        .classList
        .toggle(
            "hidden",
            formState.calendarType
            !== "LUNAR"
        );

    leapMonthButton
        .classList
        .toggle(
            "active",
            formState.leapMonth
        );

    leapMonthButton.textContent =
        formState.leapMonth
            ? "윤달"
            : "평달";
}


/* =========================================================
   TIME
   ========================================================= */

function bindTime() {
    timeRaw.addEventListener(
        "input",
        () => {
            timeRaw.value =
                onlyDigits(
                    timeRaw.value
                )
                    .slice(
                        0,
                        4
                    );

            formState.birthTimeUnknown =
                false;

            syncTimeStateFromRaw();

            updateTimeMask();

            updateTimeUI();

            saveForm();
        }
    );

    unknownTimeButton.addEventListener(
        "click",
        () => {
            formState.birthTimeUnknown =
                !formState.birthTimeUnknown;

            if (
                formState.birthTimeUnknown
            ) {
                formState.birthHour =
                    "";

                formState.birthMinute =
                    "";

                timeRaw.value =
                    "";

                say(
                    "괜찮아. 알고 있는 기록만으로도 볼 수 있는 데까지 살펴볼게.",
                    {
                        placement:
                            "top"
                    }
                );
            }

            updateTimeMask();

            updateTimeUI();

            saveForm();
        }
    );

    timeNextButton.addEventListener(
        "click",
        () => {
            syncTimeStateFromRaw();

            if (
                !validateBirthTime()
            ) {
                return;
            }

            saveForm();

            showScreen(
                "gender"
            );
        }
    );
}

function syncTimeStateFromRaw() {
    if (
        formState.birthTimeUnknown
    ) {
        return;
    }

    const value =
        onlyDigits(
            timeRaw.value
        )
            .slice(
                0,
                4
            );

    formState.birthHour =
        value.slice(
            0,
            2
        );

    formState.birthMinute =
        value.slice(
            2,
            4
        );
}

function updateTimeMask() {
    const digits =
        formState.birthTimeUnknown
            ? ""
            : onlyDigits(
                timeRaw.value
            );

    for (
        let index = 0;
        index < 4;
        index++
    ) {
        updateMaskChar(
            `t${index}`,
            digits[index]
        );
    }
}

function updateMaskChar(
    id,
    value
) {
    const element =
        document.getElementById(
            id
        );

    element.textContent =
        value
        ||
        "0";

    element
        .classList
        .toggle(
            "placeholder",
            !value
        );
}

function updateTimeUI() {
    timeMaskWrap
        .classList
        .toggle(
            "disabled",
            formState.birthTimeUnknown
        );

    timeRaw.disabled =
        formState.birthTimeUnknown;

    unknownTimeButton
        .classList
        .toggle(
            "active",
            formState.birthTimeUnknown
        );
}


/* =========================================================
   GENDER
   ========================================================= */

function bindGender() {
    femaleButton.addEventListener(
        "click",
        () =>
            setGender(
                "FEMALE"
            )
    );

    maleButton.addEventListener(
        "click",
        () =>
            setGender(
                "MALE"
            )
    );

    analyzeButton.addEventListener(
        "click",
        startAnalysis
    );
}

function setGender(
    gender
) {
    formState.gender =
        gender;

    updateGenderUI();

    saveForm();

    say(
        formState.name
            ? `좋아, ${formState.name}. 이제 필요한 기록은 모두 모였어.`
            : "좋아. 이제 필요한 기록은 모두 모였어.",
        {
            placement:
                "top"
        }
    );
}

function updateGenderUI() {
    femaleButton
        .classList
        .toggle(
            "active",
            formState.gender
            === "FEMALE"
        );

    maleButton
        .classList
        .toggle(
            "active",
            formState.gender
            === "MALE"
        );
}


/* =========================================================
   ANALYSIS
   ========================================================= */

function bindAnalysis() {
    skipAnalysisButton.addEventListener(
        "click",
        () => {
            finishVideoImmediately(
                analysisVideo1
            );

            if (
                currentScreen
                === "analysis-video"
            ) {
                showLoadingScreen();
            }
        }
    );

    skipAnalysisFinishButton.addEventListener(
        "click",
        () => {
            finishVideoImmediately(
                analysisVideo2
            );

            if (
                currentResult
                &&
                currentScreen
                === "analysis-finish"
            ) {
                showResult();
            }
        }
    );
}

async function startAnalysis() {
    if (
        analysisRunning
    ) {
        return;
    }

    if (
        !formState.gender
    ) {
        genderError.textContent =
            "성별을 선택해주세요.";

        return;
    }

    if (
        !validateAllForm()
    ) {
        return;
    }

    analysisRunning =
        true;

    saveForm();

    loadingProgress =
        0;

    loadingDialogueStage =
        -1;

    loadingWaitMessageIndex =
        0;

    try {
        analysisRequestPromise =
            requestAnalysis();

        showScreen(
            "analysis-video",
            {
                dialogue:
                    false
            }
        );

        guideSequence(
            [
                "이제 네가 태어난 순간의 네 기둥을 찾고 있어.",
                "그 안에서 성향과 관계, 재물과 일의 흐름을 하나씩 읽어볼게."
            ],
            {
                placement:
                    "bottom",
                hold:
                    900
            }
        );

        await Promise.race(
            [
                playVideo(
                    analysisVideo1,
                    VIDEO_PATH.analysis1,
                    true
                ),

                delay(
                    4500
                )
            ]
        );

        if (
            currentScreen
            === "analysis-video"
        ) {
            stopVideo(
                analysisVideo1
            );

            showLoadingScreen();
        }

        currentResult =
            await analysisRequestPromise;

        sessionStorage.setItem(
            RESULT_STORAGE_KEY,
            JSON.stringify(
                currentResult
            )
        );

        finishLoadingProgress();

        await delay(
            950
        );

        stopLoadingTimer();

        showScreen(
            "analysis-finish",
            {
                dialogue:
                    false
            }
        );

        guideSequence(
            [
                "다 읽었어.",
                "이제부터는 네 이야기야. 한 장씩 천천히 펼쳐보자."
            ],
            {
                placement:
                    "bottom",
                hold:
                    900
            }
        );

        await playVideo(
            analysisVideo2,
            VIDEO_PATH.analysis2,
            true
        );

        if (
            currentScreen
            === "analysis-finish"
        ) {
            showResult();
        }
    }
    catch (
        error
    ) {
        console.error(
            error
        );

        stopLoadingTimer();

        showToast(
            error.message
            ||
            "사주 분석 중 오류가 발생했습니다."
        );

        showScreen(
            "gender"
        );
    }
    finally {
        analysisRunning =
            false;

        analysisRequestPromise =
            null;
    }
}


/* =========================================================
   LOADING
   ========================================================= */

function showLoadingScreen() {
    if (
        currentScreen
        === "loading"
    ) {
        return;
    }

    cancelGuide();

    showScreen(
        "loading",
        {
            dialogue:
                false
        }
    );

    startLoadingTimer();

    startLoadingActorDialogue();
}

function startLoadingTimer() {
    stopLoadingTimer(
        false
    );

    loadingProgress =
        Math.max(
            0,
            Math.min(
                loadingProgress,
                94
            )
        );

    loadingDialogueStage =
        -1;

    updateLoadingProgress(
        loadingProgress
    );

    updateLoadingStatus(
        loadingProgress
    );

    loadingTimer =
        setInterval(
            () => {
                if (
                    loadingProgress
                    >= 94
                ) {
                    loadingProgress =
                        94;

                    updateLoadingProgress(
                        94
                    );

                    startLoadingWaitRotation();

                    return;
                }

                const step =
                    loadingProgress < 30
                        ? 1.35
                        : loadingProgress < 55
                            ? 0.90
                            : loadingProgress < 75
                                ? 0.52
                                : loadingProgress < 88
                                    ? 0.25
                                    : 0.10;

                loadingProgress =
                    Math.min(
                        94,
                        loadingProgress
                        +
                        step
                    );

                updateLoadingProgress(
                    loadingProgress
                );

                updateLoadingStatus(
                    loadingProgress
                );
            },
            90
        );
}

function updateLoadingStatus(
    progress
) {
    if (
        progress
        >= 100
    ) {
        stopLoadingWaitRotation();

        applyLoadingCopy(
            "분석 완료",
            "천명록이 완성됐어. 이제 결과를 펼칠게."
        );

        return;
    }

    if (
        progress
        >= 94
    ) {
        startLoadingWaitRotation();

        return;
    }

    stopLoadingWaitRotation();

    const stageIndex =
        LOADING_STAGES
            .findIndex(
                stage =>
                    progress
                    <
                    stage.until
            );

    const normalizedIndex =
        stageIndex >= 0
            ? stageIndex
            : LOADING_STAGES.length - 1;

    if (
        loadingDialogueStage
        === normalizedIndex
    ) {
        return;
    }

    loadingDialogueStage =
        normalizedIndex;

    const stage =
        LOADING_STAGES[
            normalizedIndex
        ];

    applyLoadingCopy(
        stage.label,
        stage.message
    );
}

function applyLoadingCopy(
    label,
    message
) {
    loadingLabel.textContent =
        label;

    loadingMessage.textContent =
        message;

    restartCssAnimation(
        loadingLabel,
        "copy-change"
    );

    restartCssAnimation(
        loadingMessage,
        "copy-change"
    );
}

function startLoadingWaitRotation() {
    if (
        loadingWaitTimer
    ) {
        return;
    }

    loadingWaitMessageIndex =
        0;

    applyCurrentLoadingWaitMessage();

    loadingWaitTimer =
        setInterval(
            () => {
                loadingWaitMessageIndex =
                    (
                        loadingWaitMessageIndex
                        +
                        1
                    )
                    %
                    LOADING_WAIT_MESSAGES.length;

                applyCurrentLoadingWaitMessage();
            },
            LOADING_WAIT_ROTATION_MS
        );
}

function applyCurrentLoadingWaitMessage() {
    const item =
        LOADING_WAIT_MESSAGES[
            loadingWaitMessageIndex
        ];

    applyLoadingCopy(
        item.label,
        item.message
    );
}

function stopLoadingWaitRotation() {
    if (
        loadingWaitTimer
    ) {
        clearInterval(
            loadingWaitTimer
        );

        loadingWaitTimer =
            null;
    }
}

function updateLoadingProgress(
    value
) {
    loadingPercent.textContent =
        `${Math.round(value)}%`;
}

function finishLoadingProgress() {
    loadingProgress =
        100;

    stopLoadingWaitRotation();

    updateLoadingProgress(
        100
    );

    applyLoadingCopy(
        "분석 완료",
        "천명록이 완성됐어. 이제 결과를 펼칠게."
    );

    showLoadingActorFinalLine();
}

function stopLoadingTimer(
    stopActor = true
) {
    if (
        loadingTimer
    ) {
        clearInterval(
            loadingTimer
        );

        loadingTimer =
            null;
    }

    stopLoadingWaitRotation();

    if (
        stopActor
    ) {
        stopLoadingActorDialogue();
    }
}


/* =========================================================
   LOADING ACTOR
   ========================================================= */

function startLoadingActorDialogue() {
    stopLoadingActorDialogue();

    const runId =
        ++loadingActorRunId;

    loadingActorLineIndex =
        0;

    loadingActorDialogue
        .classList
        .add(
            "visible"
        );

    const run =
        async () => {
            if (
                runId
                !== loadingActorRunId
                ||
                currentScreen
                !== "loading"
            ) {
                return;
            }

            const line =
                LOADING_ACTOR_LINES[
                    loadingActorLineIndex
                    %
                    LOADING_ACTOR_LINES.length
                ];

            await typeLoadingActorText(
                line,
                runId
            );

            if (
                runId
                !== loadingActorRunId
                ||
                currentScreen
                !== "loading"
            ) {
                return;
            }

            loadingActorLineIndex++;

            loadingActorTimer =
                setTimeout(
                    run,
                    2300
                );
        };

    run();
}

async function typeLoadingActorText(
    text,
    runId
) {
    loadingActorText.textContent =
        "";

    for (
        const character
        of Array.from(
            text
        )
    ) {
        if (
            runId
            !== loadingActorRunId
        ) {
            return;
        }

        loadingActorText.textContent +=
            character;

        await delay(
            character
            === "\n"
                ? 110
                : 34
        );
    }
}

function showLoadingActorFinalLine() {
    stopLoadingActorDialogue(
        false
    );

    loadingActorDialogue
        .classList
        .add(
            "visible"
        );

    loadingActorText.textContent =
        "다 봤어.\n이제 네 기록을 보여줄게.";
}

function stopLoadingActorDialogue(
    hide = true
) {
    loadingActorRunId++;

    if (
        loadingActorTimer
    ) {
        clearTimeout(
            loadingActorTimer
        );

        loadingActorTimer =
            null;
    }

    if (
        hide
    ) {
        loadingActorDialogue
            .classList
            .remove(
                "visible"
            );
    }
}


/* =========================================================
   API
   ========================================================= */

async function requestAnalysis() {
    let response;

    try {
        response =
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
    }
    catch (
        ignored
    ) {
        throw new Error(
            "분석 서버에 연결할 수 없습니다. 잠시 후 다시 시도해주세요."
        );
    }

    if (
        !response.ok
    ) {
        throw await createApiRequestError(
            response
        );
    }

    let raw;

    try {
        raw =
            await response.json();
    }
    catch (
        ignored
    ) {
        throw new Error(
            "분석 결과를 확인하는 중 문제가 발생했습니다. 다시 시도해주세요."
        );
    }

    return normalizeResult(
        raw
    );
}

function createAnalysisRequest() {
    const referenceDate =
        currentKoreanDateString();

    return {
        calendarType:
            formState.calendarType,

        birthDate:
            `${formState.birthYear}-${pad2(formState.birthMonth)}-${pad2(formState.birthDay)}`,

        birthTime:
            formState.birthTimeUnknown
                ? null
                : `${pad2(formState.birthHour)}:${pad2(formState.birthMinute)}`,

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
            Number(
                referenceDate.slice(
                    0,
                    4
                )
            ),

        luckReferenceDate:
            referenceDate,

        /*
         * 현재 화면은 날짜 단위의 현재 흐름을 요청한다.
         *
         * 프론트에서 임의의 정확한 시각을 만들어
         * Timeline을 확정하지 않는다.
         */
        luckReferenceTime:
            null,

        /*
         * 빈 목록 + 질문 없음
         *
         * =
         *
         * 백엔드 공식 종합운 요청 계약.
         */
        consultationCategories:
            [],

        consultationQuestion:
            null,

        relationshipTarget:
            "UNSPECIFIED",

        relationshipStatus:
            "UNSPECIFIED",

        workStatus:
            "UNSPECIFIED"
    };
}


/* =========================================================
   RESULT
   ========================================================= */

function bindResult() {
    resultHomeTopButton.addEventListener(
        "click",
        goHome
    );

    openBookButton.addEventListener(
        "click",
        openBook
    );
}

async function showResult() {
    if (
        !currentResult
    ) {
        return;
    }

    currentStoryIndex =
        0;

    bookOpened =
        false;

    resultName.textContent =
        formState.name
            ? `${formState.name}님의 명식`
            : "천명사주 명식";

    renderResultPillars();

    renderFiveElements();

    resultIntro
        .classList
        .remove(
            "hidden"
        );

    resultStoryList
        .classList
        .add(
            "hidden"
        );

    resultStoryList.innerHTML =
        "";

    showScreen(
        "result",
        {
            dialogue:
                false
        }
    );

    resultScrollArea.scrollTop =
        0;

    await delay(
        380
    );

    if (
        currentScreen
        !== "result"
    ) {
        return;
    }

    await guideSequence(
        [
            formState.name
                ? `${formState.name}, 이게 네가 태어난 순간의 네 기둥이야.`
                : "이게 네가 태어난 순간의 네 기둥이야.",

            "다섯 흐름이 어떻게 자리하고 있는지도 함께 볼 수 있어."
        ],
        {
            placement:
                "bottom",

            hold:
                900
        }
    );
}

function openBook() {
    if (
        !currentResult
    ) {
        return;
    }

    const consultations =
        getRenderableConsultations(
            currentResult
        );

    if (
        consultations.length
        === 0
    ) {
        showToast(
            "현재 표시할 수 있는 상담 기록이 없습니다."
        );

        return;
    }

    cancelGuide();

    bookOpened =
        true;

    currentStoryIndex =
        0;

    resultIntro
        .classList
        .add(
            "hidden"
        );

    resultStoryList
        .classList
        .remove(
            "hidden"
        );

    renderCurrentStory(
        true
    );

    resultScrollArea.scrollTop =
        0;
}


/* =========================================================
   PILLARS
   ========================================================= */

function renderResultPillars() {
    const pillars =
        extractPillars(
            currentResult
        );

    const ordered =
        [
            {
                label:
                    "시주",

                pillar:
                    pillars.hour
            },

            {
                label:
                    "일주",

                pillar:
                    pillars.day,

                focus:
                    true
            },

            {
                label:
                    "월주",

                pillar:
                    pillars.month
            },

            {
                label:
                    "년주",

                pillar:
                    pillars.year
            }
        ];

    pillarGrid.innerHTML =
        ordered
            .map(
                item =>
                    `
                        <div class="pillar-head ${item.focus ? "focus" : ""}">
                            ${escapeHtml(item.label)}
                        </div>
                    `
            )
            .join(
                ""
            )

        +

        ordered
            .map(
                item =>
                    `
                        <div class="pillar-cell ${item.focus ? "focus" : ""}">
                            ${escapeHtml(item.pillar.stem)}
                        </div>
                    `
            )
            .join(
                ""
            )

        +

        ordered
            .map(
                item =>
                    `
                        <div class="pillar-cell ${item.focus ? "focus" : ""}">
                            ${escapeHtml(item.pillar.branch)}
                        </div>
                    `
            )
            .join(
                ""
            );
}


/* =========================================================
   FIVE ELEMENTS
   ========================================================= */

function renderFiveElements() {
    const result =
        extractFiveElementCounts(
            currentResult
        );

    fiveElementGrid.innerHTML =
        FIVE_ELEMENT_ORDER
            .map(
                element => {
                    const meta =
                        FIVE_ELEMENT_META[
                            element
                        ];

                    const count =
                        result.counts[
                            element
                        ]
                        ??
                        0;

                    const dots =
                        count > 0
                            ?
                            Array
                                .from(
                                    {
                                        length:
                                            count
                                    }
                                )
                                .map(
                                    () =>
                                        `<span class="five-element-dot"></span>`
                                )
                                .join(
                                    ""
                                )
                            :
                            `<span class="five-element-zero">—</span>`;

                    return `
                        <div class="five-element-item">

                            <span class="five-element-hanja">
                                ${escapeHtml(meta.hanja)}
                            </span>

                            <span class="five-element-korean">
                                ${escapeHtml(meta.korean)}
                            </span>

                            <span class="five-element-dots">
                                ${dots}
                            </span>

                            <span class="five-element-count">
                                <strong>${count}</strong>개
                            </span>

                        </div>
                    `;
                }
            )
            .join(
                ""
            );

    fiveElementNote.textContent =
        result.characterCount >= 8
            ? "네 기둥의 여덟 글자에 드러난 오행의 개수입니다. 실제 세력 강약과는 별개의 표시입니다."
            : `확인 가능한 ${result.characterCount}글자 기준의 오행 구성입니다. 실제 세력 강약과는 별개의 표시입니다.`;
}

function extractFiveElementCounts(
    result
) {
    const counts = {
        WOOD:
            0,

        FIRE:
            0,

        EARTH:
            0,

        METAL:
            0,

        WATER:
            0
    };

    const sourcePillars =
        [
            result?.yearPillar,
            result?.monthPillar,
            result?.dayPillar,
            result?.hourPillar
        ];

    let characterCount =
        0;

    sourcePillars.forEach(
        pillar => {
            if (
                !pillar
            ) {
                return;
            }

            [
                pillar.stemElement,
                pillar.branchElement
            ]
                .map(
                    normalizeElementName
                )
                .forEach(
                    element => {
                        if (
                            element
                            &&
                            Object.hasOwn(
                                counts,
                                element
                            )
                        ) {
                            counts[
                                element
                            ]++;

                            characterCount++;
                        }
                    }
                );
        }
    );

    return {
        counts:
            counts,

        characterCount:
            characterCount
    };
}

function normalizeElementName(
    value
) {
    const normalized =
        String(
            value
            ??
            ""
        )
            .trim()
            .toUpperCase();

    return Object.hasOwn(
        FIVE_ELEMENT_META,
        normalized
    )
        ? normalized
        : null;
}


/* =========================================================
   STORY
   ========================================================= */

function renderCurrentStory(
    announce = true
) {
    if (
        !bookOpened
    ) {
        return;
    }

    stopCurrentStoryVideo();

    const consultations =
        getRenderableConsultations(
            currentResult
        );

    if (
        currentStoryIndex
        >= consultations.length
    ) {
        renderStoryComplete();

        return;
    }

    const consultation =
        consultations[
            currentStoryIndex
        ];

    renderStoryBook(
        consultation,
        currentStoryIndex,
        consultations.length
    );

    if (
        announce
    ) {
        setTimeout(
            announceCurrentStory,
            260
        );
    }
}

function announceCurrentStory() {
    const consultation =
        getRenderableConsultations(
            currentResult
        )[
            currentStoryIndex
        ];

    if (
        !consultation
    ) {
        return;
    }

    const meta =
        CATEGORY_META[
            consultation.category
        ];

    if (
        meta
    ) {
        say(
            meta.intro,
            {
                placement:
                    "bottom",

                hold:
                    1000
            }
        );
    }
}


/* =========================================================
   STORY RENDER
   ========================================================= */

function renderStoryBook(
    consultation,
    index,
    total
) {
    const meta =
        CATEGORY_META[
            consultation.category
        ];

    if (
        !meta
    ) {
        return;
    }

    const storyText =
        normalizeStoryText(
            consultation.storyText
        )
        ||
        "현재 이 장의 상담문을 표시할 수 없습니다.";

    const storyEmphasis =
        normalizeStoryEmphasis(
            consultation.storyEmphasis,
            storyText
        );

    const paragraphs =
        splitParagraphs(
            storyText
        );

    const unavailable =
        isUnavailableConsultation(
            consultation
        );

    const progress =
        (
            (
                index
                +
                1
            )
            /
            total
        )
        *
        100;

    const visual =
        meta.visual
        === "video"
            ?
            `
                <section class="story-visual">

                    <video
                            class="story-visual-video"
                            data-current-story-video
                            playsinline
                            muted
                            loop
                            preload="metadata"
                    >
                        <source
                                src="${escapeHtml(meta.source)}"
                                type="video/mp4"
                        >
                    </video>

                    <div class="story-visual-shade"></div>

                    <div class="story-visual-copy">

                        <small>
                            ${pad2(meta.number)} · ${escapeHtml(meta.chapter)}
                        </small>

                        <strong>
                            ${escapeHtml(meta.label)}
                        </strong>

                    </div>

                </section>
            `
            :
            `
                <section class="story-visual">

                    <img
                            class="story-visual-image"
                            src="${escapeHtml(meta.source)}"
                            alt=""
                    >

                    <div class="story-visual-shade"></div>

                    <div class="story-visual-copy">

                        <small>
                            ${pad2(meta.number)} · ${escapeHtml(meta.chapter)}
                        </small>

                        <strong>
                            ${escapeHtml(meta.label)}
                        </strong>

                    </div>

                </section>
            `;

    const consumedEmphasis =
        new Set();

    resultStoryList.innerHTML =
        `
            <div class="story-book">

                <div class="story-progress">

                    <div class="story-progress-head">

                        <span>
                            ${escapeHtml(meta.chapter)}
                        </span>

                        <strong>
                            ${pad2(index + 1)} / ${pad2(total)}
                        </strong>

                    </div>

                    <div class="story-progress-track">

                        <div
                                class="story-progress-bar"
                                style="width:${progress}%"
                        ></div>

                    </div>

                </div>

                ${visual}

                <section class="story-reading">

                    <span class="story-book-label">
                        天命錄
                    </span>

                    <h2>
                        ${escapeHtml(
                            firstNonBlank(
                                consultation.title,
                                meta.title
                            )
                        )}
                    </h2>

                    <div class="story-reading-subtitle">
                        ${escapeHtml(meta.chapter)}
                        ·
                        ${escapeHtml(meta.label)}
                    </div>

                    <div class="story-paragraphs">

                        ${
                            paragraphs
                                .map(
                                    paragraph => {
                                        const paragraphEmphasis =
                                            storyEmphasis
                                                .filter(
                                                    phrase =>
                                                        !consumedEmphasis.has(
                                                            phrase
                                                        )
                                                        &&
                                                        paragraph.includes(
                                                            phrase
                                                        )
                                                );

                                        paragraphEmphasis.forEach(
                                            phrase => {
                                                consumedEmphasis.add(
                                                    phrase
                                                );
                                            }
                                        );

                                        const hasEmphasis =
                                            paragraphEmphasis.length
                                            >
                                            0;

                                        return `
                                            <p class="
                                                ${unavailable ? "story-unavailable" : ""}
                                                ${hasEmphasis ? "has-emphasis" : ""}
                                            ">
                                                ${
                                                    renderStoryParagraph(
                                                        paragraph,
                                                        paragraphEmphasis
                                                    )
                                                }
                                            </p>
                                        `;
                                    }
                                )
                                .join(
                                    ""
                                )
                        }

                    </div>

                </section>

                <div class="story-navigation">

                    <button
                            class="story-nav-button"
                            type="button"
                            data-story-prev
                            ${index === 0 ? "disabled" : ""}
                    >
                        ‹ 이전 장
                    </button>

                    <button
                            class="story-nav-button next"
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

                <p class="story-swipe-hint">
                    좌우로 넘겨 다음 장을 읽을 수 있습니다.
                </p>

            </div>
        `;

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

    startCurrentStoryVideo();
}


/*
 * =========================================================
 * Safe strong renderer
 * =========================================================
 *
 * OpenAI HTML을 실행하지 않는다.
 *
 * storyText와 storyEmphasis 모두
 * 일반 문자열로만 전달받는다.
 *
 * 실제 storyText에 존재하는
 * 검증된 강조 구절 위치에만
 * Frontend가 직접 <strong>을 만든다.
 *
 * 모든 사용자/모델 텍스트는
 * escapeHtml()을 먼저 거친다.
 */

function renderStoryParagraph(
    paragraph,
    emphasisSource
) {
    const source =
        String(
            paragraph
            ??
            ""
        );

    if (
        !source
    ) {
        return "";
    }

    const phrases =
        normalizeStoryEmphasis(
            emphasisSource,
            source
        );

    if (
        phrases.length
        === 0
    ) {
        return escapeHtml(
            source
        );
    }

    const matches =
        [];

    phrases.forEach(
        phrase => {
            const start =
                source.indexOf(
                    phrase
                );

            if (
                start < 0
            ) {
                return;
            }

            const end =
                start
                +
                phrase.length;

            const overlaps =
                matches.some(
                    match =>
                        start < match.end
                        &&
                        match.start < end
                );

            if (
                overlaps
            ) {
                return;
            }

            matches.push(
                {
                    start:
                        start,

                    end:
                        end
                }
            );
        }
    );

    if (
        matches.length
        === 0
    ) {
        return escapeHtml(
            source
        );
    }

    matches.sort(
        (
            left,
            right
        ) =>
            left.start
            -
            right.start
    );

    let cursor =
        0;

    let html =
        "";

    matches.forEach(
        match => {
            html +=
                escapeHtml(
                    source.slice(
                        cursor,
                        match.start
                    )
                );

            html +=
                `
                    <strong
                            class="story-emphasis"
                            style="
                                color: inherit !important;
                                font-weight: 800 !important;
                                font-style: normal !important;
                                background: none !important;
                                text-shadow: none !important;
                            "
                    >${escapeHtml(
                        source.slice(
                            match.start,
                            match.end
                        )
                    )}</strong>
                `;

            cursor =
                match.end;
        }
    );

    html +=
        escapeHtml(
            source.slice(
                cursor
            )
        );

    return html;
}


/* =========================================================
   STORY NAV
   ========================================================= */

function goPreviousStory() {
    if (
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
        ||
        !bookOpened
    ) {
        return;
    }

    const total =
        getRenderableConsultations(
            currentResult
        )
            .length;

    if (
        currentStoryIndex
        >= total
    ) {
        return;
    }

    currentStoryIndex++;

    renderCurrentStory();

    scrollStoryTop();
}

function scrollStoryTop() {
    setTimeout(
        () => {
            resultScrollArea.scrollTo(
                {
                    top:
                        0,

                    behavior:
                        "smooth"
                }
            );
        },
        20
    );
}


/* =========================================================
   COMPLETE
   ========================================================= */

function renderStoryComplete() {
    cancelGuide();

    stopCurrentStoryVideo();

    resultStoryList.innerHTML =
        `
            <section class="story-complete">

                <div class="story-complete-seal">
                    錄
                </div>

                <span>
                    天命錄 完
                </span>

                <h2>
                    천명록 완독
                </h2>

                <p>
                    지금 펼쳐진 기록을 모두 읽었습니다.
                    사주는 삶을 대신 결정하는 답이 아니라,
                    자신의 흐름을 이해하고 선택을 준비하기 위한
                    하나의 지도입니다.
                </p>

                <div class="story-complete-actions">

                    <button
                            class="story-complete-button restart"
                            type="button"
                            data-story-restart
                    >
                        처음부터 다시 읽기
                    </button>

                    <button
                            class="story-complete-button home"
                            type="button"
                            data-story-home
                    >
                        천명록 마치기
                    </button>

                </div>

            </section>
        `;

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
            "[data-story-home]"
        )
        ?.addEventListener(
            "click",
            goHome
        );

    say(
        formState.name
            ? `${formState.name}, 여기까지가 지금 네게 펼쳐진 천명록이야.`
            : "여기까지가 지금 네게 펼쳐진 천명록이야.",
        {
            placement:
                "bottom",

            hold:
                1800
        }
    );
}


/* =========================================================
   SWIPE / KEYBOARD
   ========================================================= */

function bindStoryNavigation() {
    resultStoryList.addEventListener(
        "touchstart",
        event => {
            if (
                !bookOpened
            ) {
                return;
            }

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

            const delta =
                event
                    .changedTouches[0]
                    .clientX
                -
                touchStartX;

            touchStartX =
                null;

            if (
                Math.abs(
                    delta
                )
                <
                60
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
                ||
                !bookOpened
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
   STORY VIDEO
   ========================================================= */

function startCurrentStoryVideo() {
    currentStoryVideo =
        resultStoryList
            .querySelector(
                "[data-current-story-video]"
            );

    currentStoryVideo
        ?.play()
        .catch(
            () => {}
        );
}

function stopCurrentStoryVideo() {
    if (
        !currentStoryVideo
    ) {
        return;
    }

    try {
        currentStoryVideo.pause();
    }
    catch (
        ignored
    ) {
    }

    currentStoryVideo =
        null;
}


/* =========================================================
   VALIDATION
   ========================================================= */

function validateAllForm() {
    normalizeMonthInput();

    normalizeDayInput();

    syncDateStateFromInputs();

    syncTimeStateFromRaw();

    if (
        !formState.name
    ) {
        showScreen(
            "name"
        );

        return false;
    }

    if (
        !validateBirthDate()
    ) {
        showScreen(
            "birth"
        );

        return false;
    }

    if (
        !validateBirthTime()
    ) {
        showScreen(
            "time"
        );

        return false;
    }

    return true;
}

function validateBirthDate() {
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
        formState.birthYear.length
        !== 4
    ) {
        birthError.textContent =
            "출생 연도를 확인해주세요.";

        return false;
    }

    if (
        month < 1
        ||
        month > 12
    ) {
        birthError.textContent =
            "출생 월을 확인해주세요.";

        return false;
    }

    if (
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

    birthError.textContent =
        "";

    return true;
}

function validateBirthTime() {
    if (
        formState.birthTimeUnknown
    ) {
        timeError.textContent =
            "";

        return true;
    }

    const hour =
        Number(
            formState.birthHour
        );

    const minute =
        Number(
            formState.birthMinute
        );

    if (
        formState.birthHour.length
        !== 2
        ||
        formState.birthMinute.length
        !== 2
    ) {
        timeError.textContent =
            "시간을 4자리로 입력해주세요.";

        return false;
    }

    if (
        hour < 0
        ||
        hour > 23
        ||
        minute < 0
        ||
        minute > 59
    ) {
        timeError.textContent =
            "태어난 시간을 확인해주세요.";

        return false;
    }

    timeError.textContent =
        "";

    return true;
}


/* =========================================================
   RESULT NORMALIZE
   ========================================================= */

function normalizeResult(
    raw
) {
    if (
        !raw
        ||
        typeof raw
        !== "object"
    ) {
        throw new Error(
            "분석 결과 형식이 올바르지 않습니다."
        );
    }

    if (
        !Array.isArray(
            raw.consultations
        )
    ) {
        throw new Error(
            "분석 결과의 상담 목록을 확인할 수 없습니다."
        );
    }

    if (
        raw.consultations.length
        !== CATEGORY_ORDER.length
    ) {
        throw new Error(
            "분석 결과의 상담 항목 수가 올바르지 않습니다."
        );
    }

    const seenCategories =
        new Set();

    const normalizedConsultations =
        raw.consultations
            .map(
                source =>
                    normalizeConsultationSection(
                        source
                    )
            );

    normalizedConsultations.forEach(
        consultation => {
            if (
                seenCategories.has(
                    consultation.category
                )
            ) {
                throw new Error(
                    "분석 결과에 중복된 상담 항목이 있습니다."
                );
            }

            seenCategories.add(
                consultation.category
            );
        }
    );

    if (
        seenCategories.size
        !== CATEGORY_ORDER.length
        ||
        CATEGORY_ORDER.some(
            category =>
                !seenCategories.has(
                    category
                )
        )
    ) {
        throw new Error(
            "분석 결과의 상담 분야 구성이 올바르지 않습니다."
        );
    }

    normalizedConsultations.sort(
        (
            left,
            right
        ) => {
            const displayOrderDifference =
                left.displayOrder
                -
                right.displayOrder;

            if (
                displayOrderDifference
                !== 0
            ) {
                return displayOrderDifference;
            }

            return left.order
                -
                right.order;
        }
    );

    return {
        ...raw,

        consultations:
            normalizedConsultations
    };
}

function normalizeConsultationSection(
    source
) {
    if (
        !source
        ||
        typeof source
        !== "object"
    ) {
        throw new Error(
            "상담 결과 항목 형식이 올바르지 않습니다."
        );
    }

    const category =
        String(
            source.category
            ??
            ""
        )
            .trim();

    if (
        !Object.hasOwn(
            CATEGORY_META,
            category
        )
    ) {
        throw new Error(
            "알 수 없는 상담 분야가 응답에 포함되어 있습니다."
        );
    }

    const status =
        String(
            source.status
            ??
            ""
        )
            .trim();

    if (
        !OFFICIAL_SECTION_STATUS.has(
            status
        )
    ) {
        throw new Error(
            "상담 결과 상태가 올바르지 않습니다."
        );
    }

    const order =
        Number(
            source.order
        );

    const displayOrder =
        Number(
            source.displayOrder
        );

    if (
        !Number.isInteger(
            order
        )
        ||
        order < 1
        ||
        !Number.isInteger(
            displayOrder
        )
        ||
        displayOrder < 1
    ) {
        throw new Error(
            "상담 결과 순서 정보가 올바르지 않습니다."
        );
    }

    const storyText =
        normalizeStoryText(
            source.storyText
        );

    if (
        status
        === "READY"
        &&
        !storyText
    ) {
        throw new Error(
            "완료된 상담 결과의 본문이 비어 있습니다."
        );
    }

    if (
        (
            status
            === "NOT_REQUESTED"
            ||
            status
            === "ANALYSIS_UNAVAILABLE"
        )
        &&
        storyText
    ) {
        throw new Error(
            "표시 대상이 아닌 상담 항목에 본문이 포함되어 있습니다."
        );
    }

    if (
        (
            status
            === "UNAVAILABLE"
            ||
            status
            === "GENERATION_UNAVAILABLE"
        )
        &&
        !storyText
    ) {
        throw new Error(
            "안내가 필요한 상담 항목의 본문이 비어 있습니다."
        );
    }

    return {
        ...source,

        category:
            category,

        order:
            order,

        displayOrder:
            displayOrder,

        focused:
            source.focused
            === true,

        status:
            status,

        title:
            firstNonBlank(
                source.title,
                CATEGORY_META[
                    category
                ].label
            ),

        storyText:
            storyText,

        storyEmphasis:
            status
            === "READY"
                ? normalizeStoryEmphasis(
                    source.storyEmphasis,
                    storyText
                )
                : []
    };
}

function getRenderableConsultations(
    result
) {
    if (
        !Array.isArray(
            result?.consultations
        )
    ) {
        return [];
    }

    return result.consultations.filter(
        consultation =>
            RENDERABLE_SECTION_STATUS.has(
                consultation.status
            )
    );
}

function normalizeStoryText(
    value
) {
    return String(
        value
        ??
        ""
    )
        .replace(
            /\r\n/g,
            "\n"
        )
        .replace(
            /\r/g,
            "\n"
        )
        .trim();
}

function normalizeStoryEmphasis(
    value,
    storyText
) {
    if (
        !Array.isArray(
            value
        )
        ||
        !storyText
    ) {
        return [];
    }

    const seen =
        new Set();

    const result =
        [];

    value.forEach(
        raw => {
            const phrase =
                String(
                    raw
                    ??
                    ""
                )
                    .trim();

            if (
                !phrase
                ||
                phrase.includes(
                    "\n"
                )
                ||
                seen.has(
                    phrase
                )
                ||
                !storyText.includes(
                    phrase
                )
            ) {
                return;
            }

            seen.add(
                phrase
            );

            result.push(
                phrase
            );
        }
    );

    return result;
}


/* =========================================================
   PILLARS
   ========================================================= */

function extractPillars(
    result
) {
    return {
        year:
            normalizeSinglePillar(
                firstDefined(
                    result?.yearPillar,
                    result?.pillars?.year,
                    result?.saju?.yearPillar
                )
            ),

        month:
            normalizeSinglePillar(
                firstDefined(
                    result?.monthPillar,
                    result?.pillars?.month,
                    result?.saju?.monthPillar
                )
            ),

        day:
            normalizeSinglePillar(
                firstDefined(
                    result?.dayPillar,
                    result?.pillars?.day,
                    result?.saju?.dayPillar
                )
            ),

        hour:
            normalizeSinglePillar(
                firstDefined(
                    result?.hourPillar,
                    result?.pillars?.hour,
                    result?.saju?.hourPillar
                )
            )
    };
}

function normalizeSinglePillar(
    pillar
) {
    if (
        !pillar
    ) {
        return {
            stem:
                "-",

            branch:
                "-"
        };
    }

    const chinese =
        firstNonBlank(
            pillar.chineseName,
            pillar.chinese,
            pillar.nameChinese
        );

    const chars =
        Array.from(
            chinese
        );

    return {
        stem:
            firstNonBlank(
                pillar.stemChineseCharacter,
                pillar.stemChineseName,
                chars[0],
                "-"
            ),

        branch:
            firstNonBlank(
                pillar.branchChineseCharacter,
                pillar.branchChineseName,
                chars[1],
                "-"
            )
    };
}


/* =========================================================
   STORY UTIL
   ========================================================= */

function isUnavailableConsultation(
    consultation
) {
    const status =
        consultation
            ?.status;

    return status
        === "UNAVAILABLE"
        ||
        status
        === "GENERATION_UNAVAILABLE";
}

function splitParagraphs(
    text
) {
    const normalized =
        normalizeStoryText(
            text
        );

    if (
        !normalized
    ) {
        return [
            "해석 내용이 없습니다."
        ];
    }

    const direct =
        normalized
            .split(
                /\n\s*\n+/
            )
            .map(
                item =>
                    item.trim()
            )
            .filter(
                Boolean
            );

    if (
        direct.length
        > 1
    ) {
        return direct;
    }

    const sentences =
        normalized
            .match(
                /[^.!?。！？]+[.!?。！？]?/g
            )
            ?.map(
                value =>
                    value.trim()
            )
            .filter(
                Boolean
            )
        ||
        [];

    if (
        sentences.length
        <= 3
    ) {
        return [
            normalized
        ];
    }

    const target =
        Math.min(
            6,
            Math.max(
                3,
                Math.ceil(
                    sentences.length
                    /
                    3
                )
            )
        );

    const perParagraph =
        Math.ceil(
            sentences.length
            /
            target
        );

    const paragraphs =
        [];

    for (
        let index = 0;
        index < sentences.length;
        index += perParagraph
    ) {
        paragraphs.push(
            sentences
                .slice(
                    index,
                    index
                    +
                    perParagraph
                )
                .join(
                    " "
                )
        );
    }

    return paragraphs;
}


/* =========================================================
   VIDEO
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
    source
) {
    if (
        video.getAttribute(
            "src"
        )
        !== source
    ) {
        video.setAttribute(
            "src",
            source
        );

        video.load();
    }
}

async function playVideo(
    video,
    source,
    restart
) {
    setVideoSource(
        video,
        source
    );

    if (
        restart
    ) {
        try {
            video.currentTime =
                0;
        }
        catch (
            ignored
        ) {
        }
    }

    return new Promise(
        async resolve => {
            let settled =
                false;

            const finish =
                result => {
                    if (
                        settled
                    ) {
                        return;
                    }

                    settled =
                        true;

                    video.removeEventListener(
                        "ended",
                        onEnded
                    );

                    video.removeEventListener(
                        "error",
                        onError
                    );

                    resolve(
                        result
                    );
                };

            const onEnded =
                () =>
                    finish(
                        true
                    );

            const onError =
                () =>
                    finish(
                        false
                    );

            video.addEventListener(
                "ended",
                onEnded
            );

            video.addEventListener(
                "error",
                onError
            );

            try {
                await video.play();
            }
            catch (
                ignored
            ) {
                finish(
                    false
                );
            }
        }
    );
}

function stopVideo(
    video
) {
    try {
        video.pause();
    }
    catch (
        ignored
    ) {
    }
}

function finishVideoImmediately(
    video
) {
    stopVideo(
        video
    );

    video.dispatchEvent(
        new Event(
            "ended"
        )
    );
}


/* =========================================================
   HOME
   ========================================================= */

function goHome() {
    cancelGuide();

    stopCurrentStoryVideo();

    stopLoadingTimer();

    currentResult =
        null;

    currentStoryIndex =
        0;

    bookOpened =
        false;

    sessionStorage.removeItem(
        RESULT_STORAGE_KEY
    );

    resultStoryList.innerHTML =
        "";

    resultStoryList
        .classList
        .add(
            "hidden"
        );

    resultIntro
        .classList
        .remove(
            "hidden"
        );

    showScreen(
        "start",
        {
            dialogue:
                false
        }
    );
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
    try {
        const stored =
            JSON.parse(
                sessionStorage.getItem(
                    FORM_STORAGE_KEY
                )
            );

        if (
            stored
        ) {
            formState = {
                ...formState,
                ...stored
            };
        }
    }
    catch (
        ignored
    ) {
    }
}

function restoreFormState() {
    nameInput.value =
        formState.name;

    birthYear.value =
        formState.birthYear;

    birthMonth.value =
        formState.birthMonth
            ? pad2(
                formState.birthMonth
            )
            : "";

    birthDay.value =
        formState.birthDay
            ? pad2(
                formState.birthDay
            )
            : "";

    timeRaw.value =
        formState.birthTimeUnknown
            ? ""
            : `${formState.birthHour}${formState.birthMinute}`;
}


/* =========================================================
   ERROR
   ========================================================= */

async function createApiRequestError(
    response
) {
    let body =
        null;

    try {
        body =
            await response.json();
    }
    catch (
        ignored
    ) {
    }

    const errorCode =
        String(
            body?.errorCode
            ??
            ""
        )
            .trim();

    const message =
        resolveApiErrorMessage(
            errorCode,
            response.status
        );

    const error =
        new Error(
            message
        );

    error.code =
        errorCode;

    error.httpStatus =
        response.status;

    return error;
}

function resolveApiErrorMessage(
    errorCode,
    httpStatus
) {
    switch (
        errorCode
    ) {
        case "REQUEST_VALIDATION_FAILED":
            return "입력한 정보를 다시 확인해주세요.";

        case "REQUEST_BODY_INVALID":
            return "입력 형식을 다시 확인해주세요.";

        case "INVALID_REQUEST":
            return "입력한 정보의 조합을 다시 확인해주세요.";

        case "BIRTH_TIME_REQUIRED":
            return "정확한 계산을 위해 출생시간이 필요합니다.";

        case "INVALID_LUNAR_DATE":
            return "음력 날짜와 평달·윤달 여부를 확인해주세요.";

        case "RESOURCE_NOT_FOUND":
            return "사주 분석 서비스를 찾을 수 없습니다.";

        case "DETERMINISTIC_CONTRACT_VIOLATION":
        case "INTERNAL_SERVER_ERROR":
            return "분석 중 일시적인 문제가 발생했습니다. 다시 시도해주세요.";
    }

    if (
        Number(
            httpStatus
        )
        >= 500
    ) {
        return "분석 중 일시적인 문제가 발생했습니다. 다시 시도해주세요.";
    }

    return "사주 분석 요청을 처리할 수 없습니다. 입력 정보를 확인해주세요.";
}


/* =========================================================
   TOAST
   ========================================================= */

function showToast(
    message
) {
    toast.textContent =
        message;

    toast.classList.add(
        "show"
    );

    clearTimeout(
        toastTimer
    );

    toastTimer =
        setTimeout(
            () =>
                toast
                    .classList
                    .remove(
                        "show"
                    ),
            3500
        );
}


/* =========================================================
   UTILITY
   ========================================================= */

function restartCssAnimation(
    element,
    className
) {
    element
        .classList
        .remove(
            className
        );

    void element.offsetWidth;

    element
        .classList
        .add(
            className
        );
}

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
            `필수 DOM 없음: #${id}`
        );
    }

    return element;
}

function onlyDigits(
    value
) {
    return String(
        value
        ||
        ""
    )
        .replace(
            /\D/g,
            ""
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

function currentKoreanDateString() {
    const parts =
        new Intl.DateTimeFormat(
            "en-CA",
            {
                timeZone:
                    "Asia/Seoul",

                year:
                    "numeric",

                month:
                    "2-digit",

                day:
                    "2-digit"
            }
        )
            .formatToParts(
                new Date()
            );

    const values =
        Object.fromEntries(
            parts
                .filter(
                    part =>
                        part.type
                        !== "literal"
                )
                .map(
                    part =>
                        [
                            part.type,
                            part.value
                        ]
                )
        );

    return `${values.year}-${values.month}-${values.day}`;
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

function firstDefined(
    ...values
) {
    for (
        const value
        of values
    ) {
        if (
            value !== undefined
            &&
            value !== null
        ) {
            return value;
        }
    }

    return null;
}

function firstNonBlank(
    ...values
) {
    for (
        const value
        of values
    ) {
        if (
            value === undefined
            ||
            value === null
        ) {
            continue;
        }

        const normalized =
            String(
                value
            )
                .trim();

        if (
            normalized
        ) {
            return normalized;
        }
    }

    return "";
}

function escapeHtml(
    value
) {
    return String(
        value
        ??
        ""
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