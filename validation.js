document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("login-form");
    const emailInput = document.getElementById("email");
    const passwordInput = document.getElementById("password");
    const emailError = document.getElementById("email-error");
    const passwordError = document.getElementById("password-error");

    form.addEventListener("submit", (e) => {
        let isValid = true;

        // Validate email
        if (!validateEmail(emailInput.value)) {
            emailError.classList.remove("d-none");
            isValid = false;
        } else {
            emailError.classList.add("d-none");
        }

        // Validate password
        if (passwordInput.value.length < 8) {
            passwordError.classList.remove("d-none");
            isValid = false;
        } else {
            passwordError.classList.add("d-none");
        }

        if (!isValid) e.preventDefault();
    });

    function validateEmail(email) {
        const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return re.test(email);
    }
});
