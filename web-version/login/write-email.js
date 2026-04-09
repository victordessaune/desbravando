import { auth } from "../js/api/firebase.js";
import { sendPasswordResetEmail } from "https://www.gstatic.com/firebasejs/10.12.2/firebase-auth.js";

document.querySelector('.btn-template').addEventListener('click', async () => {
    const email = document.getElementById('email-login').value.trim();
    const erro = document.getElementById('error-email-login');

    if (!email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
        erro.textContent = 'Insira um email válido.';
        erro.style.display = 'block';
        return;
    }

    erro.style.display = 'none';

    try {
        await sendPasswordResetEmail(auth, email);

        const popup = document.getElementById('toast');
        popup.textContent = 'Email enviado! Verifique sua caixa de entrada.';
        popup.classList.add('toast-show');

        setTimeout(() => {
            window.location.href = 'login.html';
        }, 5000);

    } catch (error) {
        // O Firebase retorna erro se o email não existir — mas por segurança
        // mostramos a mesma mensagem de sucesso pra não expor quais emails estão cadastrados
        if (error.code === 'auth/user-not-found') {
            const popup = document.getElementById('toast');
            popup.textContent = 'Email enviado! Verifique sua caixa de entrada.';
            popup.classList.add('toast-show');

            setTimeout(() => {
                window.location.href = 'login.html';
            }, 5000);
        } else {
            erro.textContent = 'Algo deu errado. Tente novamente.';
            erro.style.display = 'block';
        }
    }
});