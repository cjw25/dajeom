document.addEventListener(
    "DOMContentLoaded",
    function () {

        "use strict";


        /* =====================================================
           타로 세션

           현재 페이지에서만 저장
           DB / localStorage 저장 안 함
           ===================================================== */

        const tarotSession = {

            name: "",

            lovePreference: "",

            relationshipStatus: "",

            jobStatus: "",

            question: "",

            readingMode: "",

            spread: null

        };



        /* =====================================================
           배열법 데이터
           ===================================================== */

        const spreads = [

            {
                id: "one_card",
                name: "원카드",
                description: "1장 · 오늘의 카드",
                cardCount: 1
            },

            {
                id: "three_card",
                name: "쓰리카드",
                description: "3장 · 과거 / 현재 / 미래",
                cardCount: 3
            },

            {
                id: "five_card",
                name: "5카드",
                description:
                    "현재 상황 / 도전 과제 / 내면의 힘 / 조언 / 결과",
                cardCount: 5
            },

            {
                id: "choice",
                name: "양자택일",
                description:
                    "7장 · 현재 / A선택 / B선택 / 조언 / 결과",
                cardCount: 7
            },

            {
                id: "timing",
                name: "시기 보기",
                description:
                    "3~6장 · 시기별 흐름 확인",
                cardCount: 6
            },

            {
                id: "partner_mind",
                name: "상대방 속마음",
                description:
                    "5장 · 상대가 보는 나 / 겉모습 / 속마음 / 바라는 것 / 결론",
                cardCount: 5
            },

            {
                id: "six_card",
                name: "6카드",
                description:
                    "현재 / 외부상황 / 가까운 미래 / 조언 / 숨은 영향 / 최종결론",
                cardCount: 6
            },

            {
                id: "mini_celtic",
                name: "미니 켈틱",
                description:
                    "6장 · 현재 / 장애물 / 과거 원인 / 현재 흐름 / 미래 모습",
                cardCount: 6
            },

            {
                id: "celtic_cross",
                name: "켈틱크로스",
                description:
                    "10장 · 현재부터 근원, 내면·외부, 미래, 최종 결과까지",
                cardCount: 10
            }

        ];



        /* =====================================================
           ELEMENT
           ===================================================== */

        const introScene =
            document.getElementById(
                "introScene"
            );


        const profileScene =
            document.getElementById(
                "profileScene"
            );


        const spreadScene =
            document.getElementById(
                "spreadScene"
            );


        const comingSoonScene =
            document.getElementById(
                "comingSoonScene"
            );


        const allScenes = [

            introScene,
            profileScene,
            spreadScene,
            comingSoonScene

        ];



        const profileSteps =
            Array.from(
                document.querySelectorAll(
                    ".profile-step"
                )
            );



        const openingVideoSection =
            document.getElementById(
                "openingVideoSection"
            );


        const openingVideo =
            document.getElementById(
                "openingVideo"
            );


        const soundButton =
            document.getElementById(
                "soundButton"
            );


        const skipVideoButton =
            document.getElementById(
                "skipVideoButton"
            );



        const nameInput =
            document.getElementById(
                "nameInput"
            );


        const nameNextButton =
            document.getElementById(
                "nameNextButton"
            );



        const preferenceChoices =
            Array.from(
                document.querySelectorAll(
                    ".preference-choice"
                )
            );


        const preferenceNextButton =
            document.getElementById(
                "preferenceNextButton"
            );



        const relationshipChoices =
            Array.from(
                document.querySelectorAll(
                    ".relationship-choice"
                )
            );


        const relationshipNextButton =
            document.getElementById(
                "relationshipNextButton"
            );



        const jobChoices =
            Array.from(
                document.querySelectorAll(
                    ".job-choice"
                )
            );


        const jobNextButton =
            document.getElementById(
                "jobNextButton"
            );



        const questionInput =
            document.getElementById(
                "questionInput"
            );


        const questionCount =
            document.getElementById(
                "questionCount"
            );


        const questionNextButton =
            document.getElementById(
                "questionNextButton"
            );


        const questionSkipButton =
            document.getElementById(
                "questionSkipButton"
            );



        const spreadList =
            document.getElementById(
                "spreadList"
            );


        const spreadNextButton =
            document.getElementById(
                "spreadNextButton"
            );


        const spreadBackButton =
            document.getElementById(
                "spreadBackButton"
            );



        const comingBackButton =
            document.getElementById(
                "comingBackButton"
            );


        const comingSpreadName =
            document.getElementById(
                "comingSpreadName"
            );


        const comingSpreadDescription =
            document.getElementById(
                "comingSpreadDescription"
            );



        const toast =
            document.getElementById(
                "tarotToast"
            );


        let toastTimer =
            null;


        let videoStarted =
            false;



        /* =====================================================
           SCENE
           ===================================================== */

        function showScene(
            target
        ) {

            allScenes.forEach(
                function (
                    scene
                ) {

                    if (
                        scene
                    ) {

                        scene.classList.remove(
                            "is-active"
                        );

                    }

                }
            );


            target.classList.add(
                "is-active"
            );


            window.scrollTo(
                0,
                0
            );

        }



        /* =====================================================
           PROFILE STEP
           ===================================================== */

        function showProfileStep(
            name
        ) {

            showScene(
                profileScene
            );


            profileSteps.forEach(
                function (
                    step
                ) {

                    step.classList.toggle(
                        "is-active",
                        step.dataset.step === name
                    );

                }
            );

        }



        /* =====================================================
           웹툰 아래까지 내려오면 영상 자동재생
           ===================================================== */

        if (
            "IntersectionObserver"
            in window
        ) {

            const videoObserver =
                new IntersectionObserver(

                    function (
                        entries
                    ) {

                        entries.forEach(
                            function (
                                entry
                            ) {

                                if (
                                    entry.isIntersecting
                                    &&
                                    entry.intersectionRatio >= 0.45
                                    &&
                                    !videoStarted
                                ) {

                                    videoStarted = true;


                                    openingVideo
                                        .play()
                                        .catch(
                                            function () {

                                                videoStarted = false;

                                            }
                                        );

                                }

                            }
                        );

                    },

                    {
                        threshold: [
                            0.45,
                            0.70
                        ]
                    }

                );


            videoObserver.observe(
                openingVideoSection
            );

        }



        /* =====================================================
           영상 끝 → 이름 화면
           ===================================================== */

        openingVideo.addEventListener(
            "ended",
            function () {

                showProfileStep(
                    "name"
                );

            }
        );



        /* =====================================================
           영상 건너뛰기
           ===================================================== */

        skipVideoButton.addEventListener(
            "click",
            function () {

                openingVideo.pause();


                showProfileStep(
                    "name"
                );

            }
        );



        /* =====================================================
           영상 소리
           ===================================================== */

        soundButton.addEventListener(
            "click",
            function () {

                openingVideo.muted =
                    !openingVideo.muted;


                soundButton.textContent =
                    openingVideo.muted
                        ? "소리 켜기"
                        : "소리 끄기";


                if (
                    openingVideo.paused
                ) {

                    openingVideo
                        .play()
                        .catch(
                            function () {
                            }
                        );

                }

            }
        );



        /* =====================================================
           BACK
           ===================================================== */

        document.addEventListener(
            "click",
            function (
                event
            ) {

                const backButton =
                    event.target.closest(
                        "[data-back]"
                    );


                if (
                    !backButton
                ) {

                    return;
                }


                const target =
                    backButton.dataset.back;


                if (
                    target === "intro"
                ) {

                    showScene(
                        introScene
                    );


                    setTimeout(
                        function () {

                            openingVideoSection
                                .scrollIntoView(
                                    {
                                        behavior: "smooth"
                                    }
                                );

                        },
                        50
                    );


                    return;
                }


                showProfileStep(
                    target
                );

            }
        );



        /* =====================================================
           NAME
           ===================================================== */

        function confirmName() {

            const value =
                nameInput
                    .value
                    .trim();


            if (
                !value
            ) {

                showToast(
                    "이름을 입력해줘."
                );


                nameInput.focus();


                return;
            }


            tarotSession.name =
                value;


            showProfileStep(
                "preference"
            );

        }



        nameNextButton.addEventListener(
            "click",
            confirmName
        );


        nameInput.addEventListener(
            "keydown",
            function (
                event
            ) {

                if (
                    event.key === "Enter"
                ) {

                    event.preventDefault();


                    confirmName();

                }

            }
        );



        /* =====================================================
           SINGLE CHOICE
           ===================================================== */

        function setupChoice(

            buttons,

            callback

        ) {

            buttons.forEach(
                function (
                    button
                ) {

                    button.addEventListener(
                        "click",
                        function () {

                            buttons.forEach(
                                function (
                                    item
                                ) {

                                    item.classList.remove(
                                        "is-selected"
                                    );

                                }
                            );


                            button.classList.add(
                                "is-selected"
                            );


                            callback(
                                button.dataset.value
                            );

                        }
                    );

                }
            );

        }



        /* =====================================================
           취향
           ===================================================== */

        setupChoice(

            preferenceChoices,

            function (
                value
            ) {

                tarotSession.lovePreference =
                    value;


                preferenceNextButton.disabled =
                    false;

            }

        );


        preferenceNextButton.addEventListener(
            "click",
            function () {

                if (
                    !tarotSession.lovePreference
                ) {

                    return;
                }


                showProfileStep(
                    "relationship"
                );

            }
        );



        /* =====================================================
           연애 상태
           ===================================================== */

        setupChoice(

            relationshipChoices,

            function (
                value
            ) {

                tarotSession.relationshipStatus =
                    value;


                relationshipNextButton.disabled =
                    false;

            }

        );


        relationshipNextButton.addEventListener(
            "click",
            function () {

                if (
                    !tarotSession.relationshipStatus
                ) {

                    return;
                }


                showProfileStep(
                    "job"
                );

            }
        );



        /* =====================================================
           직업
           ===================================================== */

        setupChoice(

            jobChoices,

            function (
                value
            ) {

                tarotSession.jobStatus =
                    value;


                jobNextButton.disabled =
                    false;

            }

        );


        jobNextButton.addEventListener(
            "click",
            function () {

                if (
                    !tarotSession.jobStatus
                ) {

                    return;
                }


                showProfileStep(
                    "question"
                );

            }
        );



        /* =====================================================
           QUESTION
           ===================================================== */

        questionInput.addEventListener(
            "input",
            function () {

                const value =
                    questionInput.value;


                questionCount.textContent =
                    value.length;


                questionNextButton.disabled =
                    !value.trim();

            }
        );



        questionNextButton.addEventListener(
            "click",
            function () {

                const value =
                    questionInput
                        .value
                        .trim();


                if (
                    !value
                ) {

                    return;
                }


                tarotSession.question =
                    value;


                tarotSession.readingMode =
                    "question";


                openSpreadScene();

            }
        );



        questionSkipButton.addEventListener(
            "click",
            function () {

                tarotSession.question =
                    "오늘 나에게 필요한 메시지는 무엇인가요?";


                tarotSession.readingMode =
                    "daily";


                openSpreadScene();

            }
        );



        /* =====================================================
           SPREAD
           ===================================================== */

        function openSpreadScene() {

            renderSpreads();


            showScene(
                spreadScene
            );

        }



        function renderSpreads() {

            spreadList.innerHTML =
                "";


            spreads.forEach(
                function (
                    spread
                ) {

                    const button =
                        document.createElement(
                            "button"
                        );


                    button.type =
                        "button";


                    button.className =
                        "spread-option";


                    if (
                        tarotSession.spread
                        &&
                        tarotSession.spread.id === spread.id
                    ) {

                        button.classList.add(
                            "is-selected"
                        );

                    }


                    const title =
                        document.createElement(
                            "strong"
                        );


                    title.textContent =
                        spread.name;


                    const description =
                        document.createElement(
                            "span"
                        );


                    description.textContent =
                        spread.description;


                    button.appendChild(
                        title
                    );


                    button.appendChild(
                        description
                    );



                    button.addEventListener(
                        "click",
                        function () {

                            tarotSession.spread =
                                spread;


                            document
                                .querySelectorAll(
                                    ".spread-option"
                                )
                                .forEach(
                                    function (
                                        item
                                    ) {

                                        item.classList.remove(
                                            "is-selected"
                                        );

                                    }
                                );


                            button.classList.add(
                                "is-selected"
                            );


                            spreadNextButton.disabled =
                                false;


                            spreadNextButton.textContent =
                                "이 배열법으로 볼게요";

                        }
                    );


                    spreadList.appendChild(
                        button
                    );

                }
            );


            if (
                tarotSession.spread
            ) {

                spreadNextButton.disabled =
                    false;


                spreadNextButton.textContent =
                    "이 배열법으로 볼게요";

            }

            else {

                spreadNextButton.disabled =
                    true;


                spreadNextButton.textContent =
                    "배열법을 선택해주세요";

            }

        }



        /* =====================================================
           SPREAD BACK
           ===================================================== */

        spreadBackButton.addEventListener(
            "click",
            function () {

                showProfileStep(
                    "question"
                );

            }
        );



        /* =====================================================
           카드 선택 → COMING SOON
           ===================================================== */

        spreadNextButton.addEventListener(
            "click",
            function () {

                if (
                    !tarotSession.spread
                ) {

                    return;
                }


                comingSpreadName.textContent =
                    tarotSession.spread.name;


                comingSpreadDescription.textContent =
                    tarotSession.spread.description;


                showScene(
                    comingSoonScene
                );


                console.log(
                    "[TAROT SESSION]",
                    tarotSession
                );

            }
        );



        /* =====================================================
           COMING SOON BACK
           ===================================================== */

        comingBackButton.addEventListener(
            "click",
            function () {

                openSpreadScene();

            }
        );



        /* =====================================================
           TOAST
           ===================================================== */

        function showToast(
            message
        ) {

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
                    2200
                );

        }



        /* =====================================================
           INIT
           ===================================================== */

        showScene(
            introScene
        );


        console.log(
            "[TAROT] AI 타로 초기화 완료"
        );

    }
);