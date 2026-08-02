document.addEventListener("DOMContentLoaded", function () {
    "use strict";

    const SHUFFLE_DURATION = 2600;
    const CLICK_MOVE_LIMIT = 10;
    const CARD_CENTER_DELAY = 320;

    const screens = Array.from(
        document.querySelectorAll("[data-tarot-screen]")
    );

    const tarotBackButton =
        document.querySelector("#tarotBackButton");

    const tarotHomeButton =
        document.querySelector("#tarotHomeButton");

    const headerStep =
        document.querySelector("#headerStep");

    const startTarotButton =
        document.querySelector("#startTarotButton");

    const tarotQuestion =
        document.querySelector("#tarotQuestion");

    const questionTextCount =
        document.querySelector("#questionTextCount");

    const spreadList =
        document.querySelector("#spreadList");

    const selectedSpreadInfo =
        document.querySelector("#selectedSpreadInfo");

    const allowReversed =
        document.querySelector("#allowReversed");

    const questionError =
        document.querySelector("#questionError");

    const createTarotSessionButton =
        document.querySelector("#createTarotSessionButton");

    const shuffleDeck =
        document.querySelector("#shuffleDeck");

    const shuffleMessage =
        document.querySelector("#shuffleMessage");

    const shuffleProgressBar =
        document.querySelector("#shuffleProgressBar");

    const shuffleProgressText =
        document.querySelector("#shuffleProgressText");

    const drawSpreadName =
        document.querySelector("#drawSpreadName");

    const drawTitle =
        document.querySelector("#drawTitle");

    const drawGuide =
        document.querySelector("#drawGuide");

    const selectedSlotList =
        document.querySelector("#selectedSlotList");

    const selectionCountText =
        document.querySelector("#selectionCountText");

    const deckCountText =
        document.querySelector("#deckCountText");

    const tarotWheelStage =
        document.querySelector("#tarotWheelStage");

    const tarotDeckWheel =
        document.querySelector("#tarotDeckWheel");

    const tarotHand =
        document.querySelector("#tarotHand");

    const centerCardNumber =
        document.querySelector("#centerCardNumber");

    const revealHeading =
        document.querySelector("#revealHeading");

    const revealDescription =
        document.querySelector("#revealDescription");

    const revealCardList =
        document.querySelector("#revealCardList");

    const revealStatus =
        document.querySelector("#revealStatus");

    const easyReadingSection =
        document.querySelector("#easyReadingSection");

    const easyReadingList =
        document.querySelector("#easyReadingList");

    const combinedReadingSection =
        document.querySelector("#combinedReadingSection");

    const combinedReadingText =
        document.querySelector("#combinedReadingText");

    const practicalAdviceList =
        document.querySelector("#practicalAdviceList");

    const revealActions =
        document.querySelector("#revealActions");

    const restartTarotButton =
        document.querySelector("#restartTarotButton");

    const tarotToast =
        document.querySelector("#tarotToast");

    const tarotState = {
        currentScreen: "intro",

        spreads: [],

        selectedSpreadId: null,
        selectedSpread: null,

        sessionKey: null,
        question: "",

        layoutCode: "AUTO",

        requiredCardCount: 0,
        deckSize: 0,

        slotIndexes: [],
        positions: [],

        pickedSlots: new Set(),
        revealedCards: [],

        stepAngle: 0,
        rotation: 0,
        centerArrayIndex: 0,

        pointerDown: false,
        pointerId: null,
        pointerStartX: 0,
        pointerStartRotation: 0,
        pointerDistance: 0,
        pressedCardElement: null,

        animating: false
    };

    let toastTimer = null;

    initialize();

    function initialize() {
        validateRequiredElements();
        bindNavigationEvents();
        bindQuestionEvents();
        bindWheelEvents();
        bindRestartEvent();

        loadSpreads();
        showScreen("intro");
    }

    function validateRequiredElements() {
        const requiredElements = {
            tarotBackButton,
            tarotHomeButton,
            headerStep,
            startTarotButton,
            tarotQuestion,
            questionTextCount,
            spreadList,
            selectedSpreadInfo,
            allowReversed,
            questionError,
            createTarotSessionButton,
            shuffleDeck,
            shuffleMessage,
            shuffleProgressBar,
            shuffleProgressText,
            drawSpreadName,
            drawTitle,
            drawGuide,
            selectedSlotList,
            selectionCountText,
            deckCountText,
            tarotWheelStage,
            tarotDeckWheel,
            tarotHand,
            centerCardNumber,
            revealHeading,
            revealDescription,
            revealCardList,
            revealStatus,
            easyReadingSection,
            easyReadingList,
            combinedReadingSection,
            combinedReadingText,
            practicalAdviceList,
            revealActions,
            restartTarotButton,
            tarotToast
        };

        const missing =
            Object.entries(requiredElements)
                .filter(function (entry) {
                    return !entry[1];
                })
                .map(function (entry) {
                    return entry[0];
                });

        if (missing.length > 0) {
            throw new Error(
                "tarot.html에서 필요한 요소를 찾지 못했습니다: "
                + missing.join(", ")
            );
        }
    }

    function bindNavigationEvents() {
        startTarotButton.addEventListener(
            "click",
            function () {
                showScreen("question");
            }
        );

        tarotBackButton.addEventListener(
            "click",
            function () {
                if (tarotState.animating) {
                    return;
                }

                switch (tarotState.currentScreen) {
                    case "intro":
                        window.location.href = "/";
                        break;

                    case "question":
                        showScreen("intro");
                        break;

                    case "shuffle":
                    case "draw":
                    case "reveal":
                        showScreen("question");
                        break;

                    default:
                        showScreen("intro");
                }
            }
        );

        tarotHomeButton.addEventListener(
            "click",
            function () {
                window.location.href = "/";
            }
        );
    }

    function showScreen(screenName) {
        screens.forEach(
            function (screen) {
                screen.classList.toggle(
                    "is-active",
                    screen.dataset.tarotScreen
                    === screenName
                );
            }
        );

        tarotState.currentScreen =
            screenName;

        const stepLabels = {
            intro: "타로 상담",
            question: "질문과 배열 선택",
            shuffle: "자동 셔플",
            draw: "카드 선택",
            reveal: "카드 공개와 해설"
        };

        headerStep.textContent =
            stepLabels[screenName]
            || "AI 타로";

        window.scrollTo({
            top: 0,
            behavior: "auto"
        });
    }

    async function loadSpreads() {
        createTarotSessionButton.disabled =
            true;

        try {
            const response =
                await fetch(
                    "/api/tarot/spreads"
                );

            const body =
                await readJsonResponse(
                    response
                );

            if (!response.ok) {
                throw new Error(
                    body.message
                    || "카드 배열을 불러오지 못했습니다."
                );
            }

            tarotState.spreads =
                Array.isArray(body)
                    ? body
                    : [];

            if (
                tarotState.spreads.length
                === 0
            ) {
                throw new Error(
                    "현재 사용할 수 있는 카드 배열이 없습니다."
                );
            }

            tarotState.selectedSpreadId =
                null;

            tarotState.selectedSpread =
                null;

            renderSpreads();
            updateSelectedSpreadInfo();

        } catch (error) {
            spreadList.innerHTML = `
                <div class="spread-error-box">
                    ${escapeHtml(error.message)}
                </div>
            `;

            questionError.textContent =
                error.message;
        }
    }

    function renderSpreads() {
        spreadList.innerHTML =
            tarotState.spreads
                .map(
                    function (spread) {
                        return `
                            <label class="spread-option">

                                <input type="radio"
                                       name="tarotSpread"
                                       value="${spread.spreadId}">

                                <span class="spread-option-content">

                                    <span class="spread-count">
                                        ${spread.cardCount}
                                    </span>

                                    <span class="spread-copy">

                                        <strong>
                                            ${escapeHtml(spread.name)}
                                        </strong>

                                        <small>
                                            ${escapeHtml(
                                                spread.description
                                            )}
                                        </small>

                                    </span>

                                    <span class="spread-check">
                                        ✓
                                    </span>

                                </span>

                            </label>
                        `;
                    }
                )
                .join("");

        spreadList
            .querySelectorAll(
                "input[name='tarotSpread']"
            )
            .forEach(
                function (input) {
                    input.addEventListener(
                        "change",
                        function () {
                            const spreadId =
                                Number(input.value);

                            tarotState.selectedSpreadId =
                                spreadId;

                            tarotState.selectedSpread =
                                tarotState.spreads.find(
                                    function (spread) {
                                        return spread.spreadId
                                            === spreadId;
                                    }
                                ) || null;

                            updateSelectedSpreadInfo();
                        }
                    );
                }
            );
    }

    function updateSelectedSpreadInfo() {
        const spread =
            tarotState.selectedSpread;

        if (!spread) {
            selectedSpreadInfo.classList.add(
                "is-empty"
            );

            selectedSpreadInfo.innerHTML = `
                <strong>
                    아직 카드 배열을 선택하지 않았습니다.
                </strong>

                <span>
                    질문에 맞는 배열을 하나 선택해주세요.
                </span>
            `;

            createTarotSessionButton.disabled =
                true;

            createTarotSessionButton.textContent =
                "배열을 선택해주세요";

            return;
        }

        const positions =
            Array.isArray(spread.positions)
                ? spread.positions
                : [];

        selectedSpreadInfo.classList.remove(
            "is-empty"
        );

        selectedSpreadInfo.innerHTML = `
            <strong>
                ${escapeHtml(spread.name)}
                ·
                ${spread.cardCount}장
            </strong>

            <span>
                ${escapeHtml(
                    positions
                        .map(
                            function (position) {
                                return position.positionName;
                            }
                        )
                        .join(" · ")
                )}
            </span>
        `;

        createTarotSessionButton.disabled =
            false;

        createTarotSessionButton.textContent =
            spread.cardCount
            + "장 카드 섞기";
    }

    function bindQuestionEvents() {
        tarotQuestion.addEventListener(
            "input",
            function () {
                questionTextCount.textContent =
                    tarotQuestion.value.length
                    + " / 1000";
            }
        );

        document
            .querySelectorAll(
                "[data-question-example]"
            )
            .forEach(
                function (button) {
                    button.addEventListener(
                        "click",
                        function () {
                            document
                                .querySelectorAll(
                                    "[data-question-example]"
                                )
                                .forEach(
                                    function (item) {
                                        item.classList.remove(
                                            "is-selected"
                                        );
                                    }
                                );

                            button.classList.add(
                                "is-selected"
                            );

                            tarotQuestion.value =
                                button.dataset.questionExample
                                || "";

                            questionTextCount.textContent =
                                tarotQuestion.value.length
                                + " / 1000";
                        }
                    );
                }
            );

        createTarotSessionButton.addEventListener(
            "click",
            createTarotSession
        );
    }

    async function createTarotSession() {
        if (!tarotState.selectedSpreadId) {
            questionError.textContent =
                "카드 배열을 선택해주세요.";

            return;
        }

        questionError.textContent =
            "";

        createTarotSessionButton.disabled =
            true;

        createTarotSessionButton.textContent =
            "카드 준비 중...";

        try {
            const question =
                tarotQuestion.value.trim();

            const response =
                await fetch(
                    "/api/tarot/sessions",
                    {
                        method: "POST",

                        headers: {
                            "Content-Type":
                                "application/json"
                        },

                        body: JSON.stringify({
                            spreadId:
                                tarotState.selectedSpreadId,

                            question:
                                question || null,

                            reversedEnabled:
                                allowReversed.checked
                        })
                    }
                );

            const body =
                await readJsonResponse(
                    response
                );

            if (!response.ok) {
                throw new Error(
                    body.message
                    || "타로카드를 준비하지 못했습니다."
                );
            }

            resetSessionState();

            tarotState.sessionKey =
                body.sessionKey;

            tarotState.question =
                question;

            tarotState.layoutCode =
                body.layoutCode
                || "AUTO";

            tarotState.requiredCardCount =
                Number(
                    body.requiredCardCount
                );

            tarotState.deckSize =
                Number(body.deckSize);

            tarotState.slotIndexes =
                Array.isArray(
                    body.slotIndexes
                )
                    ? body.slotIndexes
                    : [];

            tarotState.positions =
                Array.isArray(
                    body.positions
                )
                    ? body.positions
                    : [];

            if (
                tarotState.positions.length
                !== tarotState.requiredCardCount
            ) {
                throw new Error(
                    "카드 배열의 위치 정보가 올바르지 않습니다."
                );
            }

            renderSelectedSlots();

            showScreen("shuffle");

            await runShuffleAnimation();

            renderFullTarotWheel();

            showScreen("draw");

            updateDrawInformation();

        } catch (error) {
            questionError.textContent =
                error.message;

            showToast(
                error.message
            );

            showScreen("question");

        } finally {
            updateSelectedSpreadInfo();
        }
    }

    function resetSessionState() {
        tarotState.sessionKey =
            null;

        tarotState.layoutCode =
            "AUTO";

        tarotState.requiredCardCount =
            0;

        tarotState.deckSize =
            0;

        tarotState.slotIndexes =
            [];

        tarotState.positions =
            [];

        tarotState.pickedSlots =
            new Set();

        tarotState.revealedCards =
            [];

        tarotState.stepAngle =
            0;

        tarotState.rotation =
            0;

        tarotState.centerArrayIndex =
            0;

        resetPointerState();

        tarotState.animating =
            false;

        tarotDeckWheel.innerHTML =
            "";

        selectedSlotList.innerHTML =
            "";

        revealCardList.innerHTML =
            "";

        easyReadingList.innerHTML =
            "";

        combinedReadingText.textContent =
            "";

        practicalAdviceList.innerHTML =
            "";

        easyReadingSection.classList.remove(
            "is-visible"
        );

        combinedReadingSection.classList.remove(
            "is-visible"
        );

        revealActions.classList.remove(
            "is-visible"
        );

        resetHand();
    }

    async function runShuffleAnimation() {
        tarotState.animating =
            true;

        shuffleProgressBar.style.width =
            "0%";

        shuffleProgressText.textContent =
            "0%";

        shuffleDeck.classList.add(
            "is-running"
        );

        const startedAt =
            performance.now();

        let progress =
            0;

        while (progress < 100) {
            const elapsed =
                performance.now()
                - startedAt;

            progress =
                Math.min(
                    100,
                    Math.round(
                        elapsed
                        / SHUFFLE_DURATION
                        * 100
                    )
                );

            shuffleProgressBar.style.width =
                progress + "%";

            shuffleProgressText.textContent =
                progress + "%";

            if (progress < 35) {
                shuffleMessage.textContent =
                    "질문을 마음속으로 천천히 떠올려주세요.";

            } else if (progress < 75) {
                shuffleMessage.textContent =
                    "78장의 카드 순서를 무작위로 섞고 있습니다.";

            } else {
                shuffleMessage.textContent =
                    "카드가 준비되었습니다.";
            }

            await wait(45);
        }

        shuffleDeck.classList.remove(
            "is-running"
        );

        tarotState.animating =
            false;
    }

    function getLayoutClass() {
        const layouts = {
            SINGLE:
                "layout-single",

            TIMELINE_3:
                "layout-timeline-3",

            RELATIONSHIP_5:
                "layout-relationship-5",

            CHOICE_6:
                "layout-choice-6",

            CELTIC_10:
                "layout-celtic-10"
        };

        return layouts[
            tarotState.layoutCode
        ] || "layout-auto";
    }

    function applySpreadLayout(
        element
    ) {
        element.className =
            "spread-board "
            + getLayoutClass();

        const columnCount =
            Math.min(
                3,
                Math.max(
                    1,
                    tarotState.requiredCardCount
                )
            );

        element.style.setProperty(
            "--spread-columns",
            String(columnCount)
        );
    }

    function renderSelectedSlots() {
        applySpreadLayout(
            selectedSlotList
        );

        selectedSlotList.innerHTML =
            tarotState.positions
                .map(
                    function (position) {
                        return `
                            <article class="spread-position-item"
                                     data-position-order="${position.positionOrder}">

                                <span class="spread-position-label">
                                    ${escapeHtml(
                                        position.positionName
                                    )}
                                </span>

                                <div class="selected-slot-card">
                                </div>

                            </article>
                        `;
                    }
                )
                .join("");
    }

    function fillSelectedSlot(
        drawResponse
    ) {
        const positionItem =
            selectedSlotList.querySelector(
                "[data-position-order='"
                + drawResponse.drawOrder
                + "']"
            );

        if (!positionItem) {
            return;
        }

        const slotCard =
            positionItem.querySelector(
                ".selected-slot-card"
            );

        if (slotCard) {
            slotCard.classList.add(
                "is-filled"
            );
        }
    }

    function renderFullTarotWheel() {
        const totalCards =
            tarotState.slotIndexes.length;

        if (totalCards === 0) {
            throw new Error(
                "타로 덱 정보가 없습니다."
            );
        }

        tarotState.stepAngle =
            360 / totalCards;

        tarotState.centerArrayIndex =
            Math.floor(
                Math.random()
                * totalCards
            );

        tarotState.rotation =
            -tarotState.centerArrayIndex
            * tarotState.stepAngle;

        tarotDeckWheel.innerHTML =
            tarotState.slotIndexes
                .map(
                    function (
                        slotIndex,
                        arrayIndex
                    ) {
                        const slotAngle =
                            arrayIndex
                            * tarotState.stepAngle;

                        return `
                            <button type="button"
                                    class="tarot-wheel-card"
                                    data-slot-index="${slotIndex}"
                                    data-array-index="${arrayIndex}"
                                    style="--slot-angle:${slotAngle}deg;"
                                    aria-label="${arrayIndex + 1}번째 카드 선택">

                                <span class="tarot-card-visual">
                                </span>

                            </button>
                        `;
                    }
                )
                .join("");

        updateWheelRotation();
    }

    function updateWheelRotation() {
        tarotDeckWheel.style.setProperty(
            "--deck-rotation",
            tarotState.rotation
            + "deg"
        );

        updateCenteredCard();
    }

    function updateCenteredCard() {
        const totalCards =
            tarotState.slotIndexes.length;

        if (totalCards === 0) {
            return;
        }

        tarotState.centerArrayIndex =
            modulo(
                Math.round(
                    -tarotState.rotation
                    / tarotState.stepAngle
                ),
                totalCards
            );

        tarotDeckWheel
            .querySelectorAll(
                ".tarot-wheel-card"
            )
            .forEach(
                function (card) {
                    card.classList.toggle(
                        "is-centered",
                        Number(
                            card.dataset.arrayIndex
                        )
                        === tarotState.centerArrayIndex
                    );
                }
            );

        centerCardNumber.textContent =
            (
                tarotState.centerArrayIndex
                + 1
            )
            + " / "
            + totalCards;
    }

    function bindWheelEvents() {
        tarotWheelStage.addEventListener(
            "pointerdown",
            onWheelPointerDown
        );

        window.addEventListener(
            "pointermove",
            onWheelPointerMove,
            {
                passive: false
            }
        );

        window.addEventListener(
            "pointerup",
            onWheelPointerUp
        );

        window.addEventListener(
            "pointercancel",
            onWheelPointerCancel
        );

        tarotDeckWheel.addEventListener(
            "click",
            function (event) {
                /*
                 * 마우스와 터치는 pointerup에서 처리한다.
                 * 키보드 Enter 또는 Space 클릭만 처리한다.
                 */
                if (event.detail !== 0) {
                    return;
                }

                const cardElement =
                    event.target.closest(
                        ".tarot-wheel-card"
                    );

                if (cardElement) {
                    selectCardElement(
                        cardElement
                    );
                }
            }
        );
    }

    function onWheelPointerDown(
        event
    ) {
        if (
            tarotState.animating
            || tarotState.slotIndexes.length
            === 0
            || event.button > 0
        ) {
            return;
        }

        event.preventDefault();

        tarotState.pointerDown =
            true;

        tarotState.pointerId =
            event.pointerId;

        tarotState.pointerStartX =
            event.clientX;

        tarotState.pointerStartRotation =
            tarotState.rotation;

        tarotState.pointerDistance =
            0;

        tarotState.pressedCardElement =
            event.target.closest(
                ".tarot-wheel-card"
            );

        tarotDeckWheel.classList.add(
            "is-dragging"
        );
    }

    function onWheelPointerMove(
        event
    ) {
        if (
            !tarotState.pointerDown
            || event.pointerId
            !== tarotState.pointerId
        ) {
            return;
        }

        event.preventDefault();

        const distance =
            event.clientX
            - tarotState.pointerStartX;

        tarotState.pointerDistance =
            distance;

        if (Math.abs(distance) < 3) {
            return;
        }

        tarotState.rotation =
            tarotState.pointerStartRotation
            + distance * 0.24;

        updateWheelRotation();
    }

    function onWheelPointerUp(
        event
    ) {
        if (
            !tarotState.pointerDown
            || event.pointerId
            !== tarotState.pointerId
        ) {
            return;
        }

        const pressedCardElement =
            tarotState.pressedCardElement;

        const movedDistance =
            Math.abs(
                tarotState.pointerDistance
            );

        const shouldSelectCard =
            pressedCardElement
            && movedDistance
            <= CLICK_MOVE_LIMIT;

        tarotDeckWheel.classList.remove(
            "is-dragging"
        );

        resetPointerState();

        if (shouldSelectCard) {
            selectCardElement(
                pressedCardElement
            );

            return;
        }

        snapWheelToNearestCard();
    }

    function onWheelPointerCancel(
        event
    ) {
        if (
            !tarotState.pointerDown
            || event.pointerId
            !== tarotState.pointerId
        ) {
            return;
        }

        tarotDeckWheel.classList.remove(
            "is-dragging"
        );

        resetPointerState();
        snapWheelToNearestCard();
    }

    function resetPointerState() {
        tarotState.pointerDown =
            false;

        tarotState.pointerId =
            null;

        tarotState.pointerStartX =
            0;

        tarotState.pointerStartRotation =
            tarotState.rotation;

        tarotState.pointerDistance =
            0;

        tarotState.pressedCardElement =
            null;
    }

    function snapWheelToNearestCard() {
        if (
            tarotState.slotIndexes.length
            === 0
        ) {
            return;
        }

        const nearestIndex =
            modulo(
                Math.round(
                    -tarotState.rotation
                    / tarotState.stepAngle
                ),
                tarotState.slotIndexes.length
            );

        moveCardToCenter(
            nearestIndex
        );
    }

    async function selectCardElement(
        cardElement
    ) {
        if (
            tarotState.animating
            || !cardElement
        ) {
            return;
        }

        const slotIndex =
            Number(
                cardElement.dataset.slotIndex
            );

        const arrayIndex =
            Number(
                cardElement.dataset.arrayIndex
            );

        if (
            !Number.isInteger(slotIndex)
            || !Number.isInteger(arrayIndex)
        ) {
            showToast(
                "선택한 카드 정보를 확인할 수 없습니다."
            );

            return;
        }

        if (
            tarotState.pickedSlots.has(
                slotIndex
            )
        ) {
            showToast(
                "이미 선택한 카드입니다."
            );

            return;
        }

        tarotState.animating =
            true;

        moveCardToCenter(
            arrayIndex
        );

        await wait(
            CARD_CENTER_DELAY
        );

        await performCardPick(
            slotIndex,
            cardElement
        );
    }

    function moveCardToCenter(
        targetIndex
    ) {
        const totalCards =
            tarotState.slotIndexes.length;

        if (totalCards === 0) {
            return;
        }

        let difference =
            targetIndex
            - tarotState.centerArrayIndex;

        if (
            difference
            > totalCards / 2
        ) {
            difference -=
                totalCards;
        }

        if (
            difference
            < -totalCards / 2
        ) {
            difference +=
                totalCards;
        }

        tarotState.rotation -=
            difference
            * tarotState.stepAngle;

        updateWheelRotation();
    }

    async function performCardPick(
        slotIndex,
        cardElement
    ) {
        if (!tarotState.sessionKey) {
            tarotState.animating =
                false;

            showToast(
                "타로 세션 정보가 없습니다."
            );

            return;
        }

        tarotHand.src =
            "/img/tarot/tarot-hand-open.png";

        tarotHand.classList.add(
            "is-visible"
        );

        await wait(400);

        tarotHand.src =
            "/img/tarot/tarot-hand-grab.png";

        tarotHand.classList.add(
            "is-grabbing"
        );

        cardElement.classList.add(
            "is-grabbed"
        );

        await wait(250);

        try {
            const response =
                await fetch(
                    "/api/tarot/sessions/"
                    + encodeURIComponent(
                        tarotState.sessionKey
                    )
                    + "/draws",
                    {
                        method: "POST",

                        headers: {
                            "Content-Type":
                                "application/json"
                        },

                        body: JSON.stringify({
                            slotIndex:
                                slotIndex
                        })
                    }
                );

            const body =
                await readJsonResponse(
                    response
                );

            if (!response.ok) {
                throw new Error(
                    body.message
                    || "카드를 선택하지 못했습니다."
                );
            }

            tarotState.pickedSlots.add(
                slotIndex
            );

            tarotHand.src =
                "/img/tarot/tarot-hand-lift.png";

            tarotHand.classList.add(
                "is-lifting"
            );

            cardElement.classList.add(
                "is-lifting"
            );

            await wait(470);

            cardElement.classList.add(
                "is-picked"
            );

            fillSelectedSlot(body);

            resetHand();
            updateDrawInformation();

            if (body.readyToReveal) {
                await wait(450);

                await revealSelectedCards();

                return;
            }

            tarotState.animating =
                false;

            moveToNextAvailableCard();

        } catch (error) {
            cardElement.classList.remove(
                "is-grabbed",
                "is-lifting"
            );

            resetHand();

            tarotState.animating =
                false;

            showToast(
                error.message
            );
        }
    }

    function resetHand() {
        tarotHand.classList.remove(
            "is-visible",
            "is-grabbing",
            "is-lifting"
        );

        tarotHand.src =
            "/img/tarot/tarot-hand-open.png";
    }

    function moveToNextAvailableCard() {
        const totalCards =
            tarotState.slotIndexes.length;

        for (
            let offset = 1;
            offset <= totalCards;
            offset++
        ) {
            const nextIndex =
                modulo(
                    tarotState.centerArrayIndex
                    + offset,
                    totalCards
                );

            const nextSlot =
                tarotState.slotIndexes[
                    nextIndex
                ];

            if (
                !tarotState.pickedSlots.has(
                    nextSlot
                )
            ) {
                moveCardToCenter(
                    nextIndex
                );

                return;
            }
        }
    }

    function updateDrawInformation() {
        const selectedCount =
            tarotState.pickedSlots.size;

        const requiredCount =
            tarotState.requiredCardCount;

        drawSpreadName.textContent =
            tarotState.selectedSpread
                ? tarotState.selectedSpread.name
                : "";

        selectionCountText.textContent =
            "선택 "
            + selectedCount
            + " / "
            + requiredCount;

        deckCountText.textContent =
            (
                tarotState.deckSize
                - selectedCount
            )
            + "장 남음";

        if (
            selectedCount
            >= requiredCount
        ) {
            drawTitle.textContent =
                requiredCount
                + "장의 카드를 모두 선택했습니다";

            drawGuide.textContent =
                "선택한 카드를 공개하고 해설을 준비합니다.";

            return;
        }

        const position =
            tarotState.positions[
                selectedCount
            ];

        drawTitle.textContent =
            (
                selectedCount + 1
            )
            + "번째 카드를 선택해주세요";

        drawGuide.textContent =
            position
                ? position.positionName
                    + "을 나타낼 카드를 눌러주세요."
                : "끌리는 카드를 눌러주세요.";
    }

    async function revealSelectedCards() {
        try {
            const response =
                await fetch(
                    "/api/tarot/sessions/"
                    + encodeURIComponent(
                        tarotState.sessionKey
                    )
                    + "/reveal",
                    {
                        method: "POST"
                    }
                );

            const body =
                await readJsonResponse(
                    response
                );

            if (!response.ok) {
                throw new Error(
                    body.message
                    || "카드를 공개하지 못했습니다."
                );
            }

            tarotState.layoutCode =
                body.layoutCode
                || tarotState.layoutCode;

            tarotState.revealedCards =
                Array.isArray(
                    body.cards
                )
                    ? body.cards
                    : [];

            renderRevealCards();

            showScreen("reveal");

            await flipCardsAndShowReadings();

        } catch (error) {
            tarotState.animating =
                false;

            showToast(
                error.message
            );
        }
    }

    function renderRevealCards() {
        revealHeading.textContent =
            tarotState.requiredCardCount
            + "장의 카드가 전하는 이야기";

        revealDescription.textContent =
            tarotState.selectedSpread
                ? tarotState.selectedSpread.name
                    + " 배열에 맞춰 카드가 공개됩니다."
                : "카드 위치에 맞춰 해설을 확인합니다.";

        applySpreadLayout(
            revealCardList
        );

        revealCardList.innerHTML =
            tarotState.revealedCards
                .map(
                    function (card) {
                        return `
                            <article class="spread-position-item"
                                     data-position-order="${card.drawOrder}">

                                <span class="spread-position-label">
                                    ${escapeHtml(
                                        card.positionName
                                    )}
                                </span>

                                <div class="reveal-flip-card"
                                     data-reveal-order="${card.drawOrder}">

                                    <div class="reveal-flip-inner">

                                        <div class="reveal-card-back">
                                        </div>

                                        <div class="reveal-card-front">

                                            <span class="card-front-fallback">
                                                ✦
                                            </span>

                                            <img src="${escapeHtml(
                                                card.imageUrl
                                            )}"
                                                 alt="${escapeHtml(
                                                     card.nameKo
                                                 )}"
                                                 class="${
                                                     card.reversed
                                                         ? "is-reversed"
                                                         : ""
                                                 }"
                                                 onerror="this.style.display='none'">

                                        </div>

                                    </div>

                                </div>

                            </article>
                        `;
                    }
                )
                .join("");

        easyReadingList.innerHTML =
            tarotState.revealedCards
                .map(
                    function (card) {
                        const reading =
                            createEasyReading(
                                card
                            );

                        return `
                            <article class="easy-reading-item"
                                     data-reading-order="${card.drawOrder}">

                                <div class="reading-card-header">

                                    <span class="reading-order">
                                        ${card.drawOrder}
                                    </span>

                                    <div class="reading-card-copy">

                                        <span>
                                            ${escapeHtml(
                                                card.positionName
                                            )}
                                        </span>

                                        <strong>
                                            ${escapeHtml(
                                                card.nameKo
                                            )}
                                        </strong>

                                        <small class="reading-direction">
                                            ${
                                                card.reversed
                                                    ? "역방향"
                                                    : "정방향"
                                            }
                                        </small>

                                    </div>

                                </div>

                                <p class="reading-summary">
                                    ${escapeHtml(
                                        reading.summary
                                    )}
                                </p>

                                <p class="reading-detail">
                                    ${escapeHtml(
                                        reading.detail
                                    )}
                                </p>

                            </article>
                        `;
                    }
                )
                .join("");

        easyReadingSection.classList.remove(
            "is-visible"
        );

        combinedReadingSection.classList.remove(
            "is-visible"
        );

        revealActions.classList.remove(
            "is-visible"
        );
    }

    async function flipCardsAndShowReadings() {
        const flipCards =
            Array.from(
                revealCardList.querySelectorAll(
                    ".reveal-flip-card"
                )
            );

        easyReadingSection.classList.add(
            "is-visible"
        );

        for (
            let index = 0;
            index < flipCards.length;
            index++
        ) {
            const order =
                index + 1;

            revealStatus.textContent =
                order
                + "번째 카드를 공개하고 있습니다.";

            await wait(420);

            flipCards[
                index
            ].classList.add(
                "is-open"
            );

            await wait(520);

            const readingItem =
                easyReadingList.querySelector(
                    "[data-reading-order='"
                    + order
                    + "']"
                );

            if (readingItem) {
                readingItem.classList.add(
                    "is-visible"
                );
            }

            await wait(380);
        }

        revealStatus.textContent =
            "모든 카드가 공개되었습니다.";

        renderCombinedReading();

        combinedReadingSection.classList.add(
            "is-visible"
        );

        revealActions.classList.add(
            "is-visible"
        );

        tarotState.animating =
            false;
    }

    function createEasyReading(
        card
    ) {
        const positionName =
            card.positionName
            || "이 위치";

        const meaning =
            normalizeSentence(
                card.meaning
            );

        const questionPrefix =
            tarotState.question
                ? "당신이 물어본 질문에 연결해 보면, "
                : "현재 상황에 연결해 보면, ";

        let summary =
            positionName
            + "에서는 지금 상황을 서두르기보다 한 번 더 살펴보라는 메시지가 나타납니다.";

        if (
            includesAny(
                positionName,
                [
                    "상대방",
                    "마음",
                    "감정"
                ]
            )
        ) {
            summary =
                "상대방의 마음이 전혀 없다고 보기보다는, 감정을 바로 행동으로 옮기지 못하고 있을 가능성이 보여요.";

        } else if (
            includesAny(
                positionName,
                [
                    "문제",
                    "가로막",
                    "방해"
                ]
            )
        ) {
            summary =
                "현재 가장 큰 문제는 상황 자체보다 서로의 생각을 정확히 확인하지 못하고 있다는 점일 수 있어요.";

        } else if (
            includesAny(
                positionName,
                [
                    "미래",
                    "결과",
                    "흐름"
                ]
            )
        ) {
            summary =
                "앞으로의 흐름은 아직 확정되지 않았으며, 지금 어떤 행동을 선택하느냐에 따라 달라질 수 있어요.";

        } else if (
            includesAny(
                positionName,
                [
                    "선택",
                    "A",
                    "B"
                ]
            )
        ) {
            summary =
                "이 선택은 단순히 좋고 나쁨보다 내가 감당할 수 있는 과정인지 함께 살펴봐야 해요.";

        } else if (
            includesAny(
                positionName,
                [
                    "현재"
                ]
            )
        ) {
            summary =
                "지금은 답을 급하게 정하기보다 현재 느끼는 감정과 현실적인 상황을 분리해서 보는 것이 중요해요.";

        } else if (
            includesAny(
                positionName,
                [
                    "과거"
                ]
            )
        ) {
            summary =
                "과거에 겪었던 경험이나 감정이 지금의 판단에도 계속 영향을 주고 있을 수 있어요.";

        } else if (
            includesAny(
                positionName,
                [
                    "조언",
                    "메시지"
                ]
            )
        ) {
            summary =
                "지금 필요한 것은 결과를 미리 확정하는 일이 아니라, 현실에서 바꿀 수 있는 행동 하나를 찾는 일이에요.";
        }

        const detail =
            questionPrefix
            + card.nameKo
            + " 카드는 "
            + meaning
            + " 이 말은 미래가 반드시 그렇게 된다는 뜻이 아니라, 현재 어떤 감정과 문제를 먼저 확인해야 하는지를 알려주는 흐름으로 볼 수 있어요.";

        return {
            summary:
                summary,

            detail:
                detail
        };
    }

    function renderCombinedReading() {
        const cards =
            tarotState.revealedCards;

        const spreadName =
            tarotState.selectedSpread
                ? tarotState.selectedSpread.name
                : "선택한 카드 배열";

        if (cards.length === 0) {
            combinedReadingText.textContent =
                "공개된 카드가 없습니다.";

            practicalAdviceList.innerHTML =
                "";

            return;
        }

        const firstCard =
            cards[0];

        const lastCard =
            cards[
                cards.length - 1
            ];

        let middleText =
            "";

        if (cards.length > 2) {
            const middleCards =
                cards.slice(
                    1,
                    cards.length - 1
                );

            middleText =
                " 그 사이에 나온 "
                + middleCards
                    .map(
                        function (card) {
                            return card.positionName;
                        }
                    )
                    .join(", ")
                + " 카드는 현재 흐름을 복잡하게 만드는 감정과 현실적인 문제를 보여줍니다.";
        }

        combinedReadingText.textContent =
            spreadName
            + " 전체 흐름을 보면, "
            + firstCard.positionName
            + "에서 시작된 상황이 "
            + lastCard.positionName
            + "으로 이어지고 있어요."
            + middleText
            + " 이번 결과는 무조건 된다거나 안 된다는 결론보다, 현재 반복되고 있는 문제를 먼저 정리해야 다음 흐름이 달라질 수 있다는 의미에 가깝습니다.";

        const adviceItems =
            createPracticalAdviceItems(
                cards
            );

        practicalAdviceList.innerHTML =
            adviceItems
                .map(
                    function (advice) {
                        return `
                            <div class="practical-advice-item">
                                ${escapeHtml(advice)}
                            </div>
                        `;
                    }
                )
                .join("");
    }

    function createPracticalAdviceItems(
        cards
    ) {
        const positionNames =
            cards
                .map(
                    function (card) {
                        return card.positionName
                            || "";
                    }
                )
                .join(" ");

        const adviceItems =
            [];

        if (
            includesAny(
                positionNames,
                [
                    "상대방",
                    "관계",
                    "마음"
                ]
            )
        ) {
            adviceItems.push(
                "상대방의 마음을 혼자 추측하기보다 실제 말과 행동을 천천히 확인해보세요."
            );

            adviceItems.push(
                "답을 재촉하거나 감정을 한꺼번에 쏟아내기보다 부담 없는 대화부터 시작해보세요."
            );
        }

        if (
            includesAny(
                positionNames,
                [
                    "선택",
                    "A",
                    "B"
                ]
            )
        ) {
            adviceItems.push(
                "두 선택의 장점만 보지 말고 시간, 비용, 책임과 감당해야 할 과정도 함께 비교해보세요."
            );
        }

        if (
            includesAny(
                positionNames,
                [
                    "문제",
                    "가로막",
                    "방해"
                ]
            )
        ) {
            adviceItems.push(
                "현재 반복되고 있는 문제 중 가장 먼저 해결할 수 있는 한 가지부터 정리해보세요."
            );
        }

        if (
            includesAny(
                positionNames,
                [
                    "미래",
                    "결과",
                    "흐름"
                ]
            )
        ) {
            adviceItems.push(
                "결과를 기다리기만 하기보다 지금 바꿀 수 있는 행동을 하나 정해서 시작해보세요."
            );
        }

        if (adviceItems.length === 0) {
            adviceItems.push(
                "카드의 내용을 그대로 믿기보다 현재 상황과 실제로 맞는 부분부터 차분하게 적용해보세요."
            );

            adviceItems.push(
                "감정이 크게 흔들릴 때는 중요한 결정을 바로 내리지 말고 시간을 두고 다시 확인해보세요."
            );
        }

        return Array.from(
            new Set(adviceItems)
        ).slice(0, 4);
    }

    function bindRestartEvent() {
        restartTarotButton.addEventListener(
            "click",
            function () {
                resetSessionState();

                tarotQuestion.value =
                    "";

                questionTextCount.textContent =
                    "0 / 1000";

                tarotState.selectedSpreadId =
                    null;

                tarotState.selectedSpread =
                    null;

                renderSpreads();
                updateSelectedSpreadInfo();

                showScreen("question");
            }
        );
    }

    function includesAny(
        source,
        words
    ) {
        const text =
            String(source || "");

        return words.some(
            function (word) {
                return text.includes(
                    word
                );
            }
        );
    }

    function normalizeSentence(
        value
    ) {
        const text =
            String(value || "")
                .trim();

        if (!text) {
            return "현재 상황을 조금 더 천천히 살펴볼 필요가 있습니다.";
        }

        return text.endsWith(".")
            ? text
            : text + ".";
    }

    function modulo(
        value,
        divisor
    ) {
        if (!divisor) {
            return 0;
        }

        return (
            (
                value % divisor
            )
            + divisor
        ) % divisor;
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

    function showToast(
        message
    ) {
        clearTimeout(
            toastTimer
        );

        tarotToast.textContent =
            message
            || "오류가 발생했습니다.";

        tarotToast.classList.add(
            "is-show"
        );

        toastTimer =
            setTimeout(
                function () {
                    tarotToast.classList.remove(
                        "is-show"
                    );
                },
                2300
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