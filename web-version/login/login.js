import { auth, db, signInWithEmailAndPassword, setPersistence, browserLocalPersistence, browserSessionPersistence } from "../js/api/firebase.js";
import { collection, query, where, getDocs, doc, getDoc } from "https://www.gstatic.com/firebasejs/10.12.2/firebase-firestore.js";

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

document.addEventListener("DOMContentLoaded", function () {

    let btn = document.querySelector(".btn-template");
    if (!btn) return;

    btn.addEventListener("click", async function (e) {
        e.preventDefault();

        let emailInput = document.getElementById("email-login");
        let passwordInput = document.getElementById("password-login");
        let remember = document.getElementById("remember-login").checked;

        let email = emailInput.value.trim();
        let password = passwordInput.value.trim();

        const regexEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        let errorEmail = document.getElementById("error-email-login");
        let errorPassword = document.getElementById("error-password-login");
        let hasError = false;

        errorEmail.style.visibility = "hidden";
        errorPassword.style.visibility = "hidden";
        emailInput.classList.remove("input-error");
        passwordInput.classList.remove("input-error");

        if (email === "") {
            setError(emailInput, errorEmail, "Informe o email.");
            hasError = true;
        } else if (!regexEmail.test(email)) {
            setError(emailInput, errorEmail, "Email inválido.");
            hasError = true;
        }

        if (password === "") {
            setError(passwordInput, errorPassword, "Informe a senha.");
            hasError = true;
        }

        if (hasError) return;

        try {
            const persistence = remember
                ? browserLocalPersistence
                : browserSessionPersistence;

            await setPersistence(auth, persistence);

            const userCredential = await signInWithEmailAndPassword(auth, email, password);
            const uid = userCredential.user.uid;

            const q = query(collection(db, "users"), where("uid", "==", uid));
            const querySnap = await getDocs(q);

            if (querySnap.empty) {
                setError(emailInput, errorEmail, "Usuário não encontrado no sistema.");
                return;
            }

            const usuario = querySnap.docs[0].data();

            const empresaSnap = await getDoc(doc(db, "organizations", usuario.orgId));
            const empresa = empresaSnap.exists() ? empresaSnap.data() : { orgName: "Organização não encontrada" };

            sessionStorage.setItem("usuarioNome", usuario.firstName);
            sessionStorage.setItem("usuarioCargo", usuario.occupation);
            sessionStorage.setItem("empresaNome", empresa.orgName);

            window.location.href = "../dashboard/dashboard.html";

        } catch (error) {
            console.error("Erro:", error.code);

            switch (error.code) {
                case "auth/user-not-found":
                case "auth/wrong-password":
                case "auth/invalid-credential":
                    setError(emailInput, errorEmail, "Email ou senha incorretos.");
                    setError(passwordInput, errorPassword, "Email ou senha incorretos.");
                    break;
                case "auth/too-many-requests":
                    setError(emailInput, errorEmail, "Muitas tentativas. Tente mais tarde.");
                    break;
                default:
                    setError(emailInput, errorEmail, "Erro ao fazer login. Tente novamente.");
            }
        }
    });
});