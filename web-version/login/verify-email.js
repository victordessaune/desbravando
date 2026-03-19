document.addEventListener("DOMContentLoaded", function(){

    let email = localStorage.getItem("emailUser");

    if (!email) {
        document.getElementById("text-email").textContent =
            "Nenhum email encontrado.";
        return;
    }

    function mascararEmail(email){
        let [user, dominio] = email.split("@");

        let inicio = user[0];
        let fim = user[user.length - 1];

        let meio = "*".repeat(user.length - 2);

        return inicio + meio + fim + "@" + dominio;
    }

    let emailMascarado = mascararEmail(email);

    document.getElementById("text-email").textContent =
        "Insira o código enviado para o e-mail " + emailMascarado;

});