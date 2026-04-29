// ── POPUP ──
window.openPopup  = () => document.getElementById('popup-responsavel').classList.add('open');
window.closePopup = () => document.getElementById('popup-responsavel').classList.remove('open');
window.closeOnBg  = (e) => { if (e.target.id === 'popup-responsavel') closePopup(); };

document.addEventListener('keydown', e => { if (e.key === 'Escape') closePopup(); });

window.toggleVis = (id, btn) => {
    const inp = document.getElementById(id);
    const show = inp.type === 'password';
    inp.type = show ? 'text' : 'password';
    btn.innerHTML = show
        ? '<i class="fa-solid fa-eye-slash"></i>'
        : '<i class="fa-solid fa-eye"></i>';
};

document.getElementById('cpf')?.addEventListener('input', function () {
    let v = this.value.replace(/\D/g, '').slice(0, 11);
    v = v.replace(/(\d{3})(\d)/, '$1.$2')
         .replace(/(\d{3}\.\d{3})(\d)/, '$1.$2')
         .replace(/(\d{3}\.\d{3}\.\d{3})(\d)/, '$1-$2');
    this.value = v;
});

['nome','sobrenome','cpf','email','senha','confirmar'].forEach(id => {
    document.getElementById(id)?.addEventListener('input', () => {
        document.getElementById('err-' + id).classList.remove('show');
        document.getElementById(id).classList.remove('err');
    });
});

window.validarPopup = () => {
    let ok = true;
    ['nome','sobrenome','cpf','email','senha','confirmar'].forEach(id => {
        document.getElementById('err-' + id).classList.remove('show');
        document.getElementById(id)?.classList.remove('err');
    });

    const req = ['nome', 'sobrenome', 'cpf', 'email'];
    req.forEach(id => {
        if (!document.getElementById(id).value.trim()) {
            showErr(id, 'Campo obrigatório'); ok = false;
        }
    });

    const s = document.getElementById('senha').value;
    const c = document.getElementById('confirmar').value;
    if (!s)           { showErr('senha', 'Campo obrigatório'); ok = false; }
    else if (s.length < 6) { showErr('senha', 'Mínimo 6 caracteres'); ok = false; }
    if (!c)           { showErr('confirmar', 'Campo obrigatório'); ok = false; }
    else if (s && c && s !== c) { showErr('confirmar', 'As senhas não coincidem'); ok = false; }

    if (ok) closePopup();
};

function showErr(id, msg) {
    document.getElementById('err-' + id).textContent = msg;
    document.getElementById('err-' + id).classList.add('show');
    document.getElementById(id)?.classList.add('err');
}