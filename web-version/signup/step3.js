import { db, auth, createUserWithEmailAndPassword, addDoc, collection } from "../js/api/firebase.js";
import { getDoc, doc } from "https://www.gstatic.com/firebasejs/10.12.2/firebase-firestore.js";

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

    let errorFirstName = document.getElementById("error-first-name");
    let errorLastName = document.getElementById("error-last-name");
    let errorCPF = document.getElementById("error-cpf");
    let errorEmailInst = document.getElementById("error-email-inst");
    let errorOccupation = document.getElementById("error-occupation");
    let errorPassword = document.getElementById("error-password");
    let errorConfirmPassword = document.getElementById("error-confirm-password");

    let hasError = false;

    errorPassword.style.display = "none";
    errorConfirmPassword.style.display = "none";
    errorFirstName.style.display = "none";
    errorLastName.style.display = "none";
    errorCPF.style.display = "none";
    errorEmailInst.style.display = "none";
    errorOccupation.style.display = "none";

    if (firstName.trim() === ""){
        errorFirstName.textContent = "Informe seu nome";
        errorFirstName.style.display = "block";
        hasError = true;
    }
    if (lastName.trim() === ""){
        errorLastName.textContent = "Informe seu sobrenome";
        errorLastName.style.display = "block";
        hasError = true;
    }
    if (cpf.trim() === ""){
        errorCPF.textContent = "Informe um CPF";
        errorCPF.style.display = "block";
        hasError = true;
    }
    if (email.trim() === ""){
        errorEmailInst.textContent = "Informe um e-mail institucional";
        errorEmailInst.style.display = "block";
        hasError = true;
    }
    if (occupation.trim() === ""){
        errorOccupation.textContent = "Informe a ocupação";
        errorOccupation.style.display = "block";
        hasError = true;
    }

    if (password.trim() === ""){
        errorPassword.textContent = "Informe uma senha";
        errorPassword.style.display = "block";
        hasError = true;
    } else if(!regexSenha.test(password)){
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
        const userCredential = await createUserWithEmailAndPassword(auth, email, password);
        const user = userCredential.user;

        // Buscar dados da empresa do localStorage
        const orgData = JSON.parse(localStorage.getItem("orgData"));

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

        // Busca o orgName da empresa recém criada
        const empresaSnap = await getDoc(doc(db, "organizations", orgRef.id));
        const empresa = empresaSnap.exists() ? empresaSnap.data() : { orgName: "Organização" };

        // 💾 Salva na sessionStorage pra usar no dashboard
        sessionStorage.setItem("usuarioNome", firstName);
        sessionStorage.setItem("usuarioCargo", occupation);
        sessionStorage.setItem("empresaNome", empresa.orgName);

        // Limpa localStorage
        localStorage.removeItem("orgData");

        window.location.href = "../dashboard/dashboard.html";

    } catch (error){
        console.error("Erro:", error);
        alert(error.message);
    }
});