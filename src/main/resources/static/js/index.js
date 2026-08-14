document.addEventListener(
    "DOMContentLoaded",
    function () {

        "use strict";


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


        /*
         * =================================================
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
                    || "해당";


                showToast(
                    message
                    + " 기능은 이후 연결합니다."
                );
            }
        );


        /*
         * =================================================
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


        /*
         * =================================================
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


        console.log(
            "[HOME] index.js 초기화 완료"
        );

    }
);