import { db, auth, createUserWithEmailAndPassword, addDoc, collection, sendEmailVerification } from "../js/api/firebase.js";

let form = document.getElementById("form-step3");

form.addEventListener("submit", async function(e){

    e.preventDefault();

    let firstName = document.getElementById("first-name").value;
    let lastName = document.getElementById("last-name").value;
    let cpf = document.getElementById("cpf").value;
    let email = document.getElementById("email").value;
    let occupation = document.getElementById("occupation").value;

    let password = document.getElementById("password").value;
    let confirmPassword = document.getElementById("confirm-password").value;

    const regexSenha = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{6,}$/;

    let errorPassword = document.getElementById("error-password");
    let errorConfirmPassword = document.getElementById("error-confirm-password");

    let hasError = false;

    errorPassword.style.display = "none";
    errorConfirmPassword.style.display = "none";

    if (!regexSenha.test(password)) {
        errorPassword.textContent = "Senha fraca";
        errorPassword.style.display = "block";
        hasError = true;
    }

    if (password !== confirmPassword){
        errorConfirmPassword.textContent = "Senhas não coincidem";
        errorConfirmPassword.style.display = "block";
        hasError = true;
    }

    if (hasError) return;

    try {

        // Criar usuário no Firebase Authentication
        const userCredential = await createUserWithEmailAndPassword(
            auth,
            email,
            password
        );

        const user = userCredential.user;

        // Buscar dados da empresa do localStorage
        const orgData = JSON.parse(localStorage.getItem("orgData"));
        console.log("orgData:", orgData); 

        // Salva empresa no Firestore
        const orgRef = await addDoc(collection(db, "organizations"), {
            ...orgData,
            createdAt: new Date()
        });

        // Salva responsável no Firestore
        await addDoc(collection(db, "users"), {
            uid: user.uid,
            firstName,
            lastName,
            cpf,
            email,
            occupation,
            orgId: orgRef.id
        });

        // Limpa localStorage
        localStorage.removeItem("orgData");

        // Redireciona para dashboard
        window.location.href = "../home/dashboard.html";

    } catch (error){
        console.error("Erro:", error);
        alert(error.message);
    }
});