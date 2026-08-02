document.addEventListener("DOMContentLoaded", function () {

    const backButton =
        document.querySelector("#backButton");

    const homeButton =
        document.querySelector("#homeButton");

    const birthDateForm =
        document.querySelector("#birthDateForm");

    const yearSelect =
        document.querySelector("#yearSelect");

    const monthSelect =
        document.querySelector("#monthSelect");

    const daySelect =
        document.querySelector("#daySelect");

    const calendarButtons =
        document.querySelectorAll("[data-calendar]");

    const genderButtons =
        document.querySelectorAll("[data-gender]");

    const nextButton =
        document.querySelector("#nextButton");

    const errorMessage =
        document.querySelector("#errorMessage");

    const characterMessage =
        document.querySelector("#characterMessage");

    const toast =
        document.querySelector("#toast");

    let selectedCalendar = "SOLAR";
    let selectedGender = "";

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

    function saveBirthDate() {

        const sajuForm =
            getStoredForm();

        const year =
            yearSelect.value;

        const month =
            monthSelect.value.padStart(2, "0");

        const day =
            daySelect.value.padStart(2, "0");

        sajuForm.calendarType =
            selectedCalendar;

        sajuForm.gender =
            selectedGender;

        sajuForm.birthDate =
            year + "-" + month + "-" + day;

        sessionStorage.setItem(
            "sajuForm",
            JSON.stringify(sajuForm)
        );
    }

    function showToast(message) {

        clearTimeout(toastTimer);

        toast.textContent =
            message;

        toast.classList.add("show");

        toastTimer = setTimeout(function () {

            toast.classList.remove("show");

        }, 1800);
    }

    function createOption(value, text) {

        const option =
            document.createElement("option");

        option.value =
            String(value);

        option.textContent =
            text;

        return option;
    }

    function initializeYears() {

        const currentYear =
            new Date().getFullYear();

        for (
            let year = currentYear;
            year >= 1900;
            year--
        ) {

            yearSelect.appendChild(
                createOption(year, year + "년")
            );
        }
    }

    function initializeMonths() {

        for (
            let month = 1;
            month <= 12;
            month++
        ) {

            monthSelect.appendChild(
                createOption(month, month + "월")
            );
        }
    }

    function getLastDay(year, month) {

        return new Date(
            Number(year),
            Number(month),
            0
        ).getDate();
    }

    function updateDays() {

        const previousDay =
            daySelect.value;

        daySelect.innerHTML =
            '<option value="">일</option>';

        if (
            !yearSelect.value ||
            !monthSelect.value
        ) {
            return;
        }

        const lastDay =
            getLastDay(
                yearSelect.value,
                monthSelect.value
            );

        for (
            let day = 1;
            day <= lastDay;
            day++
        ) {

            daySelect.appendChild(
                createOption(day, day + "일")
            );
        }

        if (
            previousDay &&
            Number(previousDay) <= lastDay
        ) {

            daySelect.value =
                previousDay;
        }
    }

    function updateSelectedButton(
        buttons,
        selectedButton
    ) {

        buttons.forEach(function (button) {

            button.classList.remove("active");
        });

        selectedButton.classList.add("active");
    }

    function validateForm() {

        if (!selectedGender) {

            errorMessage.textContent =
                "성별을 선택해주세요.";

            nextButton.disabled = true;

            return false;
        }

        if (
            !yearSelect.value ||
            !monthSelect.value ||
            !daySelect.value
        ) {

            errorMessage.textContent =
                "생년월일을 모두 선택해주세요.";

            nextButton.disabled = true;

            return false;
        }

        errorMessage.textContent = "";
        nextButton.disabled = false;

        return true;
    }

    function restoreStoredForm() {

        const storedForm =
            getStoredForm();

        if (storedForm.name && characterMessage) {

            characterMessage.innerHTML =
                storedForm.name +
                "님의 생년월일을 알려주세요.<br>" +
                "양력과 음력도 함께 확인하겠습니다.";
        }

        if (storedForm.calendarType) {

            selectedCalendar =
                storedForm.calendarType;

            calendarButtons.forEach(function (button) {

                button.classList.toggle(
                    "active",
                    button.dataset.calendar ===
                    selectedCalendar
                );
            });
        }

        if (storedForm.gender) {

            selectedGender =
                storedForm.gender;

            genderButtons.forEach(function (button) {

                button.classList.toggle(
                    "active",
                    button.dataset.gender ===
                    selectedGender
                );
            });
        }

        if (storedForm.birthDate) {

            const dateParts =
                storedForm.birthDate.split("-");

            if (dateParts.length === 3) {

                yearSelect.value =
                    String(Number(dateParts[0]));

                monthSelect.value =
                    String(Number(dateParts[1]));

                updateDays();

                daySelect.value =
                    String(Number(dateParts[2]));
            }
        }

        validateForm();
    }

    initializeYears();
    initializeMonths();
    restoreStoredForm();

    calendarButtons.forEach(function (button) {

        button.addEventListener("click", function () {

            selectedCalendar =
                button.dataset.calendar;

            updateSelectedButton(
                calendarButtons,
                button
            );

            validateForm();
        });
    });

    genderButtons.forEach(function (button) {

        button.addEventListener("click", function () {

            selectedGender =
                button.dataset.gender;

            updateSelectedButton(
                genderButtons,
                button
            );

            validateForm();
        });
    });

    yearSelect.addEventListener("change", function () {

        updateDays();
        validateForm();
    });

    monthSelect.addEventListener("change", function () {

        updateDays();
        validateForm();
    });

    daySelect.addEventListener("change", function () {

        validateForm();
    });

    backButton.addEventListener("click", function () {

        window.location.href =
            "/saju/name";
    });

    homeButton.addEventListener("click", function () {

        window.location.href =
            "/";
    });

    birthDateForm.addEventListener("submit", function (event) {

        event.preventDefault();

        if (!validateForm()) {
            return;
        }

        saveBirthDate();

        nextButton.disabled = true;
        nextButton.textContent = "저장 중...";

        showToast(
            "생년월일이 입력되었습니다."
        );

        setTimeout(function () {

            nextButton.disabled = false;
            nextButton.textContent = "다음";

            /*
             * 다음 단계에서 출생시간 화면을 만든 후
             * 아래 주소로 이동하게 연결한다.
             *
             * window.location.href = "/saju/birth-time";
             */

        }, 800);
    });
});