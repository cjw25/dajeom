document.addEventListener("DOMContentLoaded", function () {

    const backButton =
        document.querySelector("#backButton");

    const homeButton =
        document.querySelector("#homeButton");

    const startButton =
        document.querySelector("#startButton");

    const characterMessage =
        document.querySelector("#characterMessage");

    if (backButton) {

        backButton.addEventListener("click", function () {

            if (window.history.length > 1) {
                window.history.back();
                return;
            }

            window.location.href = "/";
        });
    }

    if (homeButton) {

        homeButton.addEventListener("click", function () {
            window.location.href = "/";
        });
    }

    if (startButton) {

        startButton.addEventListener("click", function () {

            startButton.disabled = true;
            startButton.textContent = "상담 준비 중...";

            if (characterMessage) {

                characterMessage.innerHTML =
                    "좋습니다.<br>" +
                    "먼저 상담에서 불러드릴 이름을 알려주세요.";
            }

            setTimeout(function () {

                window.location.href = "/saju/name";

            }, 650);
        });
    }
});