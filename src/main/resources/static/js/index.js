document.addEventListener(
    "DOMContentLoaded",
    function () {

        "use strict";


        /* =================================================
         * BASIC
         * =================================================
         */

        const toast =
            document.getElementById(
                "toast"
            );


        const noticeButton =
            document.getElementById(
                "noticeButton"
            );


        let toastTimer =
            null;



        /* =================================================
         * AI BANNER
         * =================================================
         */

        const bannerSlider =
            document.getElementById(
                "aiConsultSlider"
            );


        const bannerTrack =
            document.getElementById(
                "aiConsultTrack"
            );


        const bannerSlides =
            Array.from(
                document.querySelectorAll(
                    ".ai-banner-slide"
                )
            );


        const bannerDots =
            Array.from(
                document.querySelectorAll(
                    ".ai-banner-dot"
                )
            );


        let bannerIndex =
            0;


        let bannerTimer =
            null;


        let touchStartX =
            0;


        let touchEndX =
            0;



        /* =================================================
         * BANNER MOVE
         * =================================================
         */

        function showBanner(
            index
        ) {

            if (
                !bannerTrack
                ||
                bannerSlides.length === 0
            ) {

                return;
            }


            /*
             * 마지막에서 다음으로
             */

            if (
                index >=
                bannerSlides.length
            ) {

                index =
                    0;

            }


            /*
             * 첫 번째에서 이전으로
             */

            if (
                index < 0
            ) {

                index =
                    bannerSlides.length
                    -
                    1;

            }


            bannerIndex =
                index;


            bannerTrack.style.transform =
                "translateX(-"
                +
                bannerIndex * 100
                +
                "%)";


            /*
             * DOT
             */

            bannerDots.forEach(
                function (
                    dot,
                    dotIndex
                ) {

                    dot.classList.toggle(
                        "is-active",
                        dotIndex === bannerIndex
                    );

                }
            );


            /*
             * 접근성 + 이미지 모션
             */

            bannerSlides.forEach(
                function (
                    slide,
                    slideIndex
                ) {

                    slide.setAttribute(
                        "aria-hidden",
                        slideIndex === bannerIndex
                            ? "false"
                            : "true"
                    );

                }
            );

        }



        /* =================================================
         * NEXT
         * =================================================
         */

        function nextBanner() {

            showBanner(
                bannerIndex + 1
            );

        }



        /* =================================================
         * PREVIOUS
         * =================================================
         */

        function previousBanner() {

            showBanner(
                bannerIndex - 1
            );

        }



        /* =================================================
         * AUTO PLAY
         * =================================================
         */

        function startBannerAuto() {

            stopBannerAuto();


            if (
                bannerSlides.length <= 1
            ) {

                return;
            }


            bannerTimer =
                window.setInterval(
                    function () {

                        nextBanner();

                    },
                    5000
                );

        }



        /* =================================================
         * STOP AUTO
         * =================================================
         */

        function stopBannerAuto() {

            if (
                bannerTimer !== null
            ) {

                window.clearInterval(
                    bannerTimer
                );


                bannerTimer =
                    null;

            }

        }



        /* =================================================
         * DOT CLICK
         * =================================================
         */

        bannerDots.forEach(
            function (
                dot
            ) {

                dot.addEventListener(
                    "click",
                    function () {

                        const index =
                            Number(
                                dot.dataset.slide
                            );


                        if (
                            Number.isNaN(
                                index
                            )
                        ) {

                            return;
                        }


                        showBanner(
                            index
                        );


                        startBannerAuto();

                    }
                );

            }
        );



        /* =================================================
         * MOBILE SWIPE
         * =================================================
         */

        if (
            bannerSlider
        ) {

            /*
             * 손가락 터치 시작
             */

            bannerSlider.addEventListener(
                "touchstart",
                function (
                    event
                ) {

                    if (
                        event.changedTouches.length === 0
                    ) {

                        return;
                    }


                    touchStartX =
                        event
                            .changedTouches[0]
                            .clientX;


                    touchEndX =
                        touchStartX;


                    stopBannerAuto();

                },
                {
                    passive: true
                }
            );


            /*
             * 이동 중
             */

            bannerSlider.addEventListener(
                "touchmove",
                function (
                    event
                ) {

                    if (
                        event.changedTouches.length === 0
                    ) {

                        return;
                    }


                    touchEndX =
                        event
                            .changedTouches[0]
                            .clientX;

                },
                {
                    passive: true
                }
            );


            /*
             * 손가락 터치 종료
             */

            bannerSlider.addEventListener(
                "touchend",
                function (
                    event
                ) {

                    if (
                        event.changedTouches.length > 0
                    ) {

                        touchEndX =
                            event
                                .changedTouches[0]
                                .clientX;

                    }


                    const difference =
                        touchStartX
                        -
                        touchEndX;


                    /*
                     * 너무 조금 움직이면
                     * 슬라이드 변경하지 않음
                     */

                    if (
                        Math.abs(
                            difference
                        )
                        >=
                        45
                    ) {

                        /*
                         * 왼쪽으로 넘김
                         */

                        if (
                            difference > 0
                        ) {

                            nextBanner();

                        }


                        /*
                         * 오른쪽으로 넘김
                         */

                        else {

                            previousBanner();

                        }

                    }


                    startBannerAuto();

                },
                {
                    passive: true
                }
            );


            /*
             * PC에서는 마우스를 올려두면
             * 자동 슬라이드 잠시 멈춤
             */

            bannerSlider.addEventListener(
                "mouseenter",
                function () {

                    stopBannerAuto();

                }
            );


            bannerSlider.addEventListener(
                "mouseleave",
                function () {

                    startBannerAuto();

                }
            );

        }



        /* =================================================
         * BROWSER VISIBILITY
         * =================================================
         */

        document.addEventListener(
            "visibilitychange",
            function () {

                if (
                    document.hidden
                ) {

                    stopBannerAuto();

                }

                else {

                    startBannerAuto();

                }

            }
        );



        /* =================================================
         * 아직 연결하지 않은 홈 메뉴
         * =================================================
         */

        document.addEventListener(
            "click",
            function (
                event
            ) {

                const button =
                    event.target.closest(
                        "[data-message]"
                    );


                if (
                    !button
                ) {

                    return;
                }


                const message =
                    button.dataset.message
                    ||
                    "해당";


                showToast(
                    message
                    +
                    " 기능은 이후 연결합니다."
                );

            }
        );



        /* =================================================
         * 알림
         * =================================================
         */

        if (
            noticeButton
        ) {

            noticeButton.addEventListener(
                "click",
                function () {

                    showToast(
                        "알림 기능은 이후 연결합니다."
                    );

                }
            );

        }



        /* =================================================
         * TOAST
         * =================================================
         */

        function showToast(
            message
        ) {

            if (
                !toast
            ) {

                return;
            }


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



        /* =================================================
         * INITIALIZE
         * =================================================
         */

        if (
            bannerSlides.length > 0
        ) {

            /*
             * 첫 화면은 기존 3번
             */

            showBanner(
                0
            );


            startBannerAuto();

        }



        console.log(
            "[HOME] index.js 초기화 완료"
        );

    }
);