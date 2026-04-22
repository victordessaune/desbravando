import { db, auth, createUserWithEmailAndPassword, addDoc, collection } from "../js/api/firebase.js";
import { doc, setDoc } from "https://www.gstatic.com/firebasejs/10.12.2/firebase-firestore.js";

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

let form = document.getElementById("form-step3");

form.addEventListener("submit", async function (e) {

    e.preventDefault();

    const fields = {
        firstName:       document.getElementById("first-name"),
        lastName:        document.getElementById("last-name"),
        cpf:             document.getElementById("cpf"),
        email:           document.getElementById("email"),
        occupation:      document.getElementById("occupation"),
        password:        document.getElementById("password"),
        confirmPassword: document.getElementById("confirm-password"),
    };

    const errors = {
        firstName:       document.getElementById("error-first-name"),
        lastName:        document.getElementById("error-last-name"),
        cpf:             document.getElementById("error-cpf"),
        email:           document.getElementById("error-email-inst"),
        occupation:      document.getElementById("error-occupation"),
        password:        document.getElementById("error-password"),
        confirmPassword: document.getElementById("error-confirm-password"),
    };

    const regexSenha = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{6,}$/;

    Object.keys(fields).forEach(key => {
        fields[key].classList.remove("input-error");
        errors[key].style.visibility = "hidden";
    });

    let hasError = false;

    if (fields.firstName.value.trim() === "") {
        setError(fields.firstName, errors.firstName, "Informe seu nome");
        hasError = true;
    }
    if (fields.lastName.value.trim() === "") {
        setError(fields.lastName, errors.lastName, "Informe seu sobrenome");
        hasError = true;
    }
    if (fields.cpf.value.trim() === "") {
        setError(fields.cpf, errors.cpf, "Informe um CPF");
        hasError = true;
    } else if (fields.cpf.value.replace(/\D/g, "").length < 11) {
        setError(fields.cpf, errors.cpf, "CPF inválido");
        hasError = true;
    }
    if (fields.email.value.trim() === "") {
        setError(fields.email, errors.email, "Informe um e-mail institucional");
        hasError = true;
    }
    if (fields.occupation.value.trim() === "") {
        setError(fields.occupation, errors.occupation, "Informe a ocupação");
        hasError = true;
    }
    if (fields.password.value.trim() === "") {
        setError(fields.password, errors.password, "Informe uma senha");
        hasError = true;
    } else if (!regexSenha.test(fields.password.value)) {
        setError(fields.password, errors.password, "Use letras maiúsculas, minúsculas e números");
        hasError = true;
    }
    if (fields.confirmPassword.value.trim() === "") {
        setError(fields.confirmPassword, errors.confirmPassword, "Confirme sua senha");
        hasError = true;
    } else if (fields.password.value !== fields.confirmPassword.value) {
        setError(fields.confirmPassword, errors.confirmPassword, "Senhas não coincidem");
        hasError = true;
    }

    if (hasError) return;

    try {
        const userCredential = await createUserWithEmailAndPassword(auth, fields.email.value, fields.password.value);
        const user = userCredential.user;

        const orgData = JSON.parse(localStorage.getItem("orgData"));

        const orgRef = await addDoc(collection(db, "organizations"), {
            ...orgData,
            createdAt: new Date(),
            verified: false
        });

        await setDoc(doc(db, "users", user.uid), {
            uid: user.uid,
            firstName: fields.firstName.value,
            lastName: fields.lastName.value,
            cpf: fields.cpf.value,
            email: fields.email.value,
            occupation: fields.occupation.value,
            orgId: orgRef.id
        });

        localStorage.removeItem("orgData");

        window.location.href = "../dashboard/dashboard.html";

    } catch (error) {
        console.error("Erro:", error);

        switch (error.code) {
            case "auth/email-already-in-use":
                setError(fields.email, errors.email, "Este e-mail já está em uso");
                break;
            case "auth/invalid-email":
                setError(fields.email, errors.email, "E-mail inválido");
                break;
            default:
                setError(fields.email, errors.email, "Erro ao criar conta. Tente novamente");
        }
    }
});