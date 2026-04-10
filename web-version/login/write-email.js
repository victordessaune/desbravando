import { auth } from "../js/api/firebase.js";
import { sendPasswordResetEmail } from "https://www.gstatic.com/firebasejs/10.12.2/firebase-auth.js";

function setError(input, errorEl, message) {
    input.classList.add("input-error");
    errorEl.innerHTML = `<i class="fa-solid fa-circle-exclamation"></i> ${message}`;
    errorEl.style.visibility = "visible";

    input.addEventListener("focus", function clearError() {
        input.classList.remove("input-error");
        errorEl.style.visibility = "hidden";
        input.removeEventListener("focus", clearError);
    });
}

document.querySelector(".btn-template").addEventListener("click", async () => {
    const emailInput = document.getElementById("email-login");
    const erro = document.getElementById("error-email-login");

    const email = emailInput.value.trim();

    emailInput.classList.remove("input-error");
    erro.style.visibility = "hidden";

    if (email === "") {
        setError(emailInput, erro, "Insira um email.");
        return;
    }

    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
        setError(emailInput, erro, "Informe um email válido.");
        return;
    }

    try {
        await sendPasswordResetEmail(auth, email);

        const popup = document.getElementById("toast");
        popup.textContent = "Email enviado! Verifique sua caixa de entrada.";
        popup.classList.add("toast-show");

        setTimeout(() => {
            window.location.href = "login.html";
        }, 5000);

    } catch (error) {
        if (error.code === "auth/user-not-found") {
            setError(emailInput, erro, "Email não encontrado.");
        } else {
            setError(emailInput, erro, "Algo deu errado. Tente novamente.");
        }
    }
});