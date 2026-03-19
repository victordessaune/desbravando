document.addEventListener("DOMContentLoaded", function(){

    let form = document.getElementById("form-login");
    let btn = document.querySelector(".btn-template");

    btn.addEventListener("click", function(e){

        e.preventDefault();

        let email = document.getElementById("email-login").value;
        let password = document.getElementById("password-login").value;

        const regexEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

        let errorEmail = document.getElementById("error-email-login");
        let errorPassword = document.getElementById("error-password-login");

        let hasError = false;

        errorEmail.style.display = "none";
        errorPassword.style.display = "none";

        if (email.trim() === ""){
            errorEmail.textContent = "Informe o email.";
            errorEmail.style.display = "block";
            hasError = true;
        } else if (!regexEmail.test(email)){
            errorEmail.textContent = "Email inválido.";
            errorEmail.style.display = "block";
            hasError = true;
        }

        if (password.trim() === ""){
            errorPassword.textContent = "Informe a senha.";
            errorPassword.style.display = "block";
            hasError = true;
        }

        if (!hasError){
            localStorage.setItem("emailUser", email);
            window.location.href = "verify-email.html";

        }

    });

});