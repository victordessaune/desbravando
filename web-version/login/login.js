document.addEventListener("DOMContentLoaded", function(){

    let btn = document.querySelector(".btn-template");

    if(!btn) return;

    btn.addEventListener("click", function(e){

        e.preventDefault();

        let emailInput = document.getElementById("email-login");
        let passwordInput = document.getElementById("password-login");

        let email = emailInput ? emailInput.value : "";
        let password = passwordInput ? passwordInput.value : "";

        const regexEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

        let errorEmail = document.getElementById("error-email-login");
        let errorPassword = document.getElementById("error-password-login");

        let hasError = false;

        // reset erros
        if(errorEmail) errorEmail.style.display = "none";
        if(errorPassword) errorPassword.style.display = "none";

        // valida email
        if(email.trim() === ""){
            if(errorEmail){
                errorEmail.textContent = "Informe o email.";
                errorEmail.style.display = "block";
            }
            hasError = true;
        } 
        else if(!regexEmail.test(email)){
            if(errorEmail){
                errorEmail.textContent = "Email inválido.";
                errorEmail.style.display = "block";
            }
            hasError = true;
        }

        // valida senha (só se existir no HTML)
        if(passwordInput){
            if(password.trim() === ""){
                if(errorPassword){
                    errorPassword.textContent = "Informe a senha.";
                    errorPassword.style.display = "block";
                }
                hasError = true;
            }
        }

        // se tudo ok
        if(!hasError){
            localStorage.setItem("emailUser", email);

            // 🔥 DIFERENCIAÇÃO DAS PÁGINAS
            if(window.location.pathname.includes("write-email.html")){
                window.location.href = "forgot-password.html";
            } else {
                window.location.href = "verify-email.html";
            }
        }

    });

});