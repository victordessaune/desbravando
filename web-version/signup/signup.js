let form = document.getElementById("form-step1");

form.addEventListener("submit", function(e){

    e.preventDefault();

    let orgName = document.getElementById("org-name").value;
    let cnpj = document.getElementById("org-cnpj").value;

    let errorName = document.getElementById("error-name");
    let errorCNPJ = document.getElementById("error-cnpj");

    let hasError = false;

    errorName.style.display = "none";
    errorCNPJ.style.display = "none";

    if (orgName === ""){
        errorName.textContent = "Preencha o nome da organização";
        errorName.style.display = "block";
        hasError = true;
    }
    if (cnpj === ""){
        errorCNPJ.textContent = "Informe o CNPJ";
        errorCNPJ.style.display = "block";
        hasError = true;
    } else if (cnpj.length < 14){
        errorCNPJ.textContent = "CNPJ inválido";
        errorCNPJ.style.display = "block";
        hasError = true;
    }
    if (!hasError){
        form.submit();
    }

});