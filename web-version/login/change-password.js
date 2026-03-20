document.addEventListener("DOMContentLoaded", function(){

    const btn = document.querySelector(".btn-template");

    if(!btn) return;

    btn.addEventListener("click", function(e){
        e.preventDefault();

        const novaSenha = document.getElementById("password-new").value.trim();
        const confirmarSenha = document.getElementById("password-confirm").value.trim();

        const errorNew = document.getElementById("error-password-new");
        const errorConfirm = document.getElementById("error-password-confirm");

        let hasError = false;

        // reset erros
        errorNew.style.display = "none";
        errorConfirm.style.display = "none";

        // valida nova senha
        if(novaSenha === ""){
            errorNew.textContent = "Informe a nova senha.";
            errorNew.style.display = "block";
            hasError = true;
        } else if(novaSenha.length < 6){
            errorNew.textContent = "A senha deve ter pelo menos 6 caracteres.";
            errorNew.style.display = "block";
            hasError = true;
        }

        // valida confirmação
        if(confirmarSenha === ""){
            errorConfirm.textContent = "Confirme a senha.";
            errorConfirm.style.display = "block";
            hasError = true;
        } else if(novaSenha !== confirmarSenha){
            errorConfirm.textContent = "As senhas não coincidem.";
            errorConfirm.style.display = "block";
            hasError = true;
        }

        // se tudo ok
        if(!hasError){
            alert("Senha redefinida com sucesso!");
            window.location.href = "login.html";
        }

    });

});