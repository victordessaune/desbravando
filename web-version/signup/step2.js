const params = new URLSearchParams(window.location.search);
const orgType = params.get("org-type");

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

form.addEventListener("submit", function (e) {

    e.preventDefault();

    const regexEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    const fields = {
        orgName:      document.getElementById("org-name"),
        cnpj:         document.getElementById("org-cnpj"),
        website:      document.getElementById("website"),
        orgEmail:     document.getElementById("org-email"),
        cep:          document.getElementById("cep"),
        street:       document.getElementById("street"),
        number:       document.getElementById("number"),
        neighborhood: document.getElementById("neighborhood"),
        city:         document.getElementById("city"),
        uf:           document.getElementById("uf"),
    };

    const errors = {
        orgName:      document.getElementById("error-name"),
        cnpj:         document.getElementById("error-cnpj"),
        website:      document.getElementById("error-website"),
        orgEmail:     document.getElementById("error-email"),
        cep:          document.getElementById("error-cep"),
        street:       document.getElementById("error-street"),
        number:       document.getElementById("error-number"),
        neighborhood: document.getElementById("error-neighborhood"),
        city:         document.getElementById("error-city"),
        uf:           document.getElementById("error-uf"),
    };

    // Limpa todos os erros
    Object.keys(fields).forEach(key => {
        fields[key].classList.remove("input-error");
        errors[key].style.visibility = "hidden";
    });

    let hasError = false;

    if (fields.orgName.value.trim() === "") {
        setError(fields.orgName, errors.orgName, "Preencha o nome da organização");
        hasError = true;
    }

    if (fields.cnpj.value.trim() === "") {
        setError(fields.cnpj, errors.cnpj, "Informe o CNPJ");
        hasError = true;
    } else if (fields.cnpj.value.replace(/\D/g, "").length < 14) {
        setError(fields.cnpj, errors.cnpj, "CNPJ inválido");
        hasError = true;
    }

    if (fields.website.value.trim() === "") {
        setError(fields.website, errors.website, "Informe um website");
        hasError = true;
    }

    if (fields.orgEmail.value.trim() === "") {
        setError(fields.orgEmail, errors.orgEmail, "Informe um email institucional");
        hasError = true;
    } else if (!regexEmail.test(fields.orgEmail.value.trim())) {
        setError(fields.orgEmail, errors.orgEmail, "Email institucional inválido");
        hasError = true;
    }

    if (fields.cep.value.trim() === "") {
        setError(fields.cep, errors.cep, "Informe um CEP");
        hasError = true;
    } else if (fields.cep.value.replace(/\D/g, "").length < 8) {
        setError(fields.cep, errors.cep, "CEP inválido");
        hasError = true;
    }

    if (fields.street.value.trim() === "") {
        setError(fields.street, errors.street, "Informe um logradouro");
        hasError = true;
    }

    if (fields.number.value.trim() === "") {
        setError(fields.number, errors.number, "Informe um número");
        hasError = true;
    }

    if (fields.neighborhood.value.trim() === "") {
        setError(fields.neighborhood, errors.neighborhood, "Informe um bairro");
        hasError = true;
    }

    if (fields.city.value.trim() === "") {
        setError(fields.city, errors.city, "Informe uma cidade");
        hasError = true;
    }

    if (fields.uf.value === "" || fields.uf.value === "—") {
        setError(fields.uf, errors.uf, "Selecione um estado");
        hasError = true;
    }

    if (!hasError) {
        const orgData = {
            orgType,
            orgName:      fields.orgName.value,
            cnpj:         fields.cnpj.value,
            website:      fields.website.value,
            orgEmail:     fields.orgEmail.value,
            cep:          fields.cep.value,
            uf:           fields.uf.value,
            city:         fields.city.value,
            number:       fields.number.value,
            street:       fields.street.value,
            complement:   document.getElementById("complement").value,
            neighborhood: fields.neighborhood.value,
        };

        localStorage.setItem("orgData", JSON.stringify(orgData));
        window.location.href = "step3.html";
    }

});