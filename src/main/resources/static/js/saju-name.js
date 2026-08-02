document.addEventListener("DOMContentLoaded", function () {

    const backButton =
        document.querySelector("#backButton");

    const homeButton =
        document.querySelector("#homeButton");

    const nameForm =
        document.querySelector("#nameForm");

    const nameInput =
        document.querySelector("#nameInput");

    const clearButton =
        document.querySelector("#clearButton");

    const nextButton =
        document.querySelector("#nextButton");

    const errorMessage =
        document.querySelector("#errorMessage");

    const toast =
        document.querySelector("#toast");

    let toastTimer;

    function getStoredForm() {

        try {

            const storedValue =
                sessionStorage.getItem("sajuForm");

            if (!storedValue) {
                return {};
            }

            return JSON.parse(storedValue);

        } catch (error) {

            console.error(
                "사주 입력 정보를 불러오지 못했습니다.",
                error
            );

            return {};
        }
    }

    function saveName(name) {

        const sajuForm =
            getStoredForm();

        sajuForm.name =
            name;

        sessionStorage.setItem(
            "sajuForm",
            JSON.stringify(sajuForm)
        );
    }

    function showToast(message) {

        if (!toast) {
            return;
        }

        clearTimeout(toastTimer);

        toast.textContent =
            message;

        toast.classList.add("show");

        toastTimer = setTimeout(function () {

            toast.classList.remove("show");

        }, 1600);
    }

    function validateName() {

        const name =
            nameInput.value.trim();

        if (name.length === 0) {

            errorMessage.textContent = "";
            nameInput.classList.remove("input-error");
            nextButton.disabled = true;

            return false;
        }

        if (name.length < 2) {

            errorMessage.textContent =
                "이름은 두 글자 이상 입력해주세요.";

            nameInput.classList.add("input-error");
            nextButton.disabled = true;

            return false;
        }

        if (name.length > 20) {

            errorMessage.textContent =
                "이름은 20자 이하로 입력해주세요.";

            nameInput.classList.add("input-error");
            nextButton.disabled = true;

            return false;
        }

        errorMessage.textContent = "";
        nameInput.classList.remove("input-error");
        nextButton.disabled = false;

        return true;
    }

    function updateClearButton() {

        const hasValue =
            nameInput.value.length > 0;

        clearButton.classList.toggle(
            "show",
            hasValue
        );
    }

    const storedForm =
        getStoredForm();

    if (storedForm.name) {

        nameInput.value =
            storedForm.name;

        validateName();
        updateClearButton();
    }

    nameInput.addEventListener("input", function () {

        validateName();
        updateClearButton();
    });

    clearButton.addEventListener("click", function () {

        nameInput.value = "";

        validateName();
        updateClearButton();

        nameInput.focus();
    });

    backButton.addEventListener("click", function () {

        window.location.href =
            "/saju/start";
    });

    homeButton.addEventListener("click", function () {

        window.location.href =
            "/";
    });

    nameForm.addEventListener("submit", function (event) {

        event.preventDefault();

        if (!validateName()) {

            nameInput.focus();

            return;
        }

        const name =
            nameInput.value.trim();

        saveName(name);

        nextButton.disabled = true;
        nextButton.textContent = "저장 중...";

        showToast(
            name + "님, 다음 정보를 알려주세요."
        );

        setTimeout(function () {

            window.location.href =
                "/saju/birth-date";

        }, 650);
    });
});