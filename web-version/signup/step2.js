const params = new URLSearchParams(window.location.search);
const orgType = params.get("org-type");



const cnpjInput = document.getElementById("org-cnpj");

cnpjInput.addEventListener("input", function () {
    let value = this.value.replace(/\D/g, "");
    value = value.substring(0, 14);

    if (value.length > 12) {
        value = value.replace(/^(\d{2})(\d{3})(\d{3})(\d{4})(\d{0,2})/, "$1.$2.$3/$4-$5");
    } else if (value.length > 8) {
        value = value.replace(/^(\d{2})(\d{3})(\d{3})(\d{0,4})/, "$1.$2.$3/$4");
    } else if (value.length > 5) {
        value = value.replace(/^(\d{2})(\d{3})(\d{0,3})/, "$1.$2.$3");
    } else if (value.length > 2) {
        value = value.replace(/^(\d{2})(\d{0,3})/, "$1.$2");
    }

    this.value = value;
});

const cepInput = document.getElementById("cep");

cepInput.addEventListener("input", function () {
    let value = this.value.replace(/\D/g, "");
    value = value.substring(0, 8);

    if (value.length > 5) {
        value = value.replace(/^(\d{5})(\d{0,3})/, "$1-$2");
    }

    this.value = value;
});

let form = document.getElementById("form-step2");

form.addEventListener("submit", function(e){

    e.preventDefault();

    const regexEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    let orgName = document.getElementById("org-name").value;
    let cnpj = document.getElementById("org-cnpj").value.replace(/\D/g, "");
    let website = document.getElementById("website").value;
    let orgEmail = document.getElementById("org-email").value;
    let cep = document.getElementById("cep").value.replace(/\D/g, "");
    let uf = document.getElementById("uf").value;
    let city = document.getElementById("city").value;
    let number = document.getElementById("number").value;
    let street = document.getElementById("street").value;
    let complement = document.getElementById("complement").value;
    let neighborhood = document.getElementById("neighborhood").value;

    let errorName = document.getElementById("error-name");
    let errorCNPJ = document.getElementById("error-cnpj");
    let errorWebsite = document.getElementById("error-website");
    let errorOrgEmail = document.getElementById("error-email");
    let errorAddress = document.getElementById("error-address");

    let hasError = false;

    errorName.style.display = "none";
    errorCNPJ.style.display = "none";
    errorWebsite.style.display = "none";
    errorOrgEmail.style.display = "none";
    errorAddress.style.display = "none";

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

    if (!hasError){

    const orgData = {
        orgType: orgType,
        orgName,
        cnpj,
        website,
        orgEmail,
        cep,
        uf,
        city,
        number,
        street,
        complement,
        neighborhood
    };

    localStorage.setItem("orgData", JSON.stringify(orgData));

    window.location.href = "step3.html";
}

});

