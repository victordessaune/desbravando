import { auth, signInWithEmailAndPassword, setPersistence, browserLocalPersistence, browserSessionPersistence } from "../js/api/firebase.js";

document.addEventListener("DOMContentLoaded", function () {

    let btn = document.querySelector(".btn-template");
    if (!btn) return;

    btn.addEventListener("click", async function (e) {
        e.preventDefault();

        let email = document.getElementById("email-login").value.trim();
        let password = document.getElementById("password-login").value.trim();
        let remember = document.getElementById("remember-login").checked;

        const regexEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        let errorEmail = document.getElementById("error-email-login");
        let errorPassword = document.getElementById("error-password-login");
        let hasError = false;

        errorEmail.style.display = "none";
        errorPassword.style.display = "none";

        if (email === "") {
            errorEmail.textContent = "Informe o email.";
            errorEmail.style.display = "block";
            hasError = true;
        } else if (!regexEmail.test(email)) {
            errorEmail.textContent = "Email inválido.";
            errorEmail.style.display = "block";
            hasError = true;
        }

        if (password === "") {
            errorPassword.textContent = "Informe a senha.";
            errorPassword.style.display = "block";
            hasError = true;
        }

        if (hasError) return;

        try {
            const persistence = remember
                ? browserLocalPersistence   // fica logado mesmo fechando o browser
                : browserSessionPersistence; // expira ao fechar a aba

            await setPersistence(auth, persistence);

            const userCredential = await signInWithEmailAndPassword(auth, email, password);
            console.log("Logado:", userCredential.user.uid);

            window.location.href = "../home/dashboard.html";

        } catch (error) {
            console.error("Erro:", error.code);

            switch (error.code) {
                case "auth/user-not-found":
                case "auth/wrong-password":
                case "auth/invalid-credential":
                    errorEmail.textContent = "Email ou senha incorretos.";
                    errorEmail.style.display = "block";
                    break;
                case "auth/too-many-requests":
                    errorEmail.textContent = "Muitas tentativas. Tente mais tarde.";
                    errorEmail.style.display = "block";
                    break;
                default:
                    errorEmail.textContent = "Erro ao fazer login. Tente novamente.";
                    errorEmail.style.display = "block";
            }
        }
    });
});
