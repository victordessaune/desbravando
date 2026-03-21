let form = document.getElementById("form-step3");

form.addEventListener("submit", function(e){

    e.preventDefault();

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

    if (!hasError){
        form.submit();
    }
});