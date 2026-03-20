document.addEventListener("DOMContentLoaded", function() {
    let email = localStorage.getItem("emailUser");
    if(!email) return;

    function mascararEmail(email){
        let [user, domain] = email.split("@");
        return user[0] + "*".repeat(user.length-2) + user[user.length-1] + "@" + domain;
    }

    const emailText = document.getElementById("text-email");
    if(emailText){
        emailText.textContent = "Insira o código enviado para o e-mail " + mascararEmail(email);
    }

    const inputs = document.querySelectorAll(".input-code input");
    if(inputs.length){
        inputs.forEach((input, idx) => {
            input.addEventListener("input", () => {
                if(input.value.length === 1 && idx < inputs.length-1){
                    inputs[idx+1].focus();
                }
            });
        });
    }

    const btnVerify = document.querySelector(".btn-template-verification");
    if(btnVerify){
        btnVerify.addEventListener("click", () => {
            alert("Código verificado com sucesso!");
            if(window.location.pathname.includes("forgot-password.html")){
                window.location.href = "change-password.html";
            } 
        });
    }

    const resend = document.querySelector(".send-code a");
    if(resend){
        resend.addEventListener("click", () => {
            alert("Código reenviado para " + email);
        });
    }
});