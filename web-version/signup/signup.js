let form = document.getElementById("form-step1");

form.addEventListener("submit", function(e){

    e.preventDefault();

    let orgName = document.getElementById("org-name").value;
    let cnpj = document.getElementById("org-cnpj").value;
    let website = document.getElementById("website").value;
    let orgEmail = document.getElementById("org-email").value;
    let cep = document.getElementById("cep").value;
    let uf = document.getElementById("uf").value;
    let city = document.getElementById("city").value;
    let number = document.getElementById("number").value;
    let street = document.getElementById("street").value
    let password = document.getElementById("password").value;
    let confirmPassword = document.getElementById("confirm-password").value;
    const regexSenha = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{6,}$/;
    const regexEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    let errorName = document.getElementById("error-name");
    let errorCNPJ = document.getElementById("error-cnpj");
    let errorWebsite = document.getElementById("error-website");
    let errorOrgEmail = document.getElementById("error-email");
    let errorAddress = document.getElementById("error-address")
    let errorPassword = document.getElementById("error-password")
    let errorConfirmPassword = document.getElementById("error-confirm-password")

    let hasError = false;

    errorName.style.display = "none";
    errorCNPJ.style.display = "none";
    errorWebsite.style.display = "none";
    errorOrgEmail.style.display = "none";
    errorAddress.style.display = "none";
    errorPassword.style.display = "none";
    errorConfirmPassword.style.display = "none";

    if (orgName.trim() === ""){
        errorName.textContent = "Preencha o nome da organização";
        errorName.style.display = "block";
        hasError = true;
    }

    if (cnpj.trim() === ""){
        errorCNPJ.textContent = "Informe o CNPJ";
        errorCNPJ.style.display = "block";
        hasError = true;
    } else if (cnpj.length < 14){
        errorCNPJ.textContent = "CNPJ inválido";
        errorCNPJ.style.display = "block";
        hasError = true;
    }

    if (website.trim() === ""){
        errorWebsite.textContent = "Informe um website.";
        errorWebsite.style.display = "block";
        hasError = true;
    }

    if (orgEmail.trim() === ""){
        errorOrgEmail.textContent = "Informe um email institucional"
        errorOrgEmail.style.display = "block";
        hasError = true;
    } else if(!regexEmail.test(orgEmail.trim())){
        errorOrgEmail.textContent = "Email institucional inválido"
        errorOrgEmail.style.display = "block";
        hasError = true;
    }

    if(cep === "" || uf === "" || city === "" || number === "" || street === ""){
        errorAddress.textContent = "O endereço está incompleto";
        errorAddress.style.display = "block";
        hasError = true;
    }

    if (password.trim() === ""){
        errorPassword.textContent = "Este campo é obrigatório."
        errorPassword.style.display = "block";
        hasError = true;
    }else if (!regexSenha.test(password)) {
        errorPassword.textContent = "A senha deve ter no mínimo 6 caracteres, com letra maiúscula, minúscula e número.";
        errorPassword.style.display = "block";
        hasError = true;
    }

    if (confirmPassword.trim() === ""){
        errorConfirmPassword.textContent = "Este campo é obrigatório."
        errorConfirmPassword.style.display = "block";
        hasError = true;
    } else if (password !== confirmPassword){
        errorConfirmPassword.textContent = "As senhas não coincidem.";
        errorConfirmPassword.style.display = "block";
        hasError = true;
    }

    if (!hasError){
        form.submit();
    }

});