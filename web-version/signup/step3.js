console.log("JS carregou");

// step3.js — importa tudo de um lugar só
import { db, auth, createUserWithEmailAndPassword, addDoc, collection, sendEmailVerification } from "../js/api/firebase.js";

console.log("Firebase importou");

let form = document.getElementById("form-step3");

console.log("✅ Form encontrado:", form);

form.addEventListener("submit", async function(e){

    console.log("Submit disparou");

    e.preventDefault();

    // 🔥 PEGAR DADOS DO FORM
    let firstName = document.getElementById("first-name").value;
    let lastName = document.getElementById("last-name").value;
    let cpf = document.getElementById("cpf").value;
    let email = document.getElementById("email").value;
    let occupation = document.getElementById("occupation").value;

    let password = document.getElementById("password").value;
    let confirmPassword = document.getElementById("confirm-password").value;

    console.log("📦 Dados do usuário:", {
        firstName,
        email,
        password
    });

    // 🔐 VALIDAÇÃO
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
        console.log("🔥 Tentando criar usuário...");
        // 🔐 1. CRIA USUÁRIO
        const userCredential = await createUserWithEmailAndPassword(
            auth,
            email,
            password
        );

        const user = userCredential.user;
        console.log("✅ Usuário criado:", user.uid);

        // 📥 2. PEGA DADOS DA EMPRESA
        const orgData = JSON.parse(localStorage.getItem("orgData"));
        console.log("orgData:", orgData); // ← o que aparece aqui?

        // 🏢 3. SALVA EMPRESA
        console.log("🏢 Salvando empresa...", orgData);
        const orgRef = await addDoc(collection(db, "organizations"), {
            ...orgData,
            createdAt: new Date()
        });
        console.log("✅ Empresa salva:", orgRef.id);

        // 👤 4. SALVA RESPONSÁVEL
        await addDoc(collection(db, "users"), {
            uid: user.uid,
            firstName,
            lastName,
            cpf,
            email,
            occupation,
            orgId: orgRef.id
        });

        console.log("✅ Responsável salvo");

        // 🧹 limpa storage
        localStorage.removeItem("orgData");

        console.log("🔥 TUDO SALVO!");

        // 🚀 REDIRECIONA
        window.location.href = "../home/dashboard.html";

    } catch (error){
        console.error("❌ ERRO COMPLETO:", error);
        alert(error.message);
    }
});