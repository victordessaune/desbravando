import { db, auth } from "../js/api/firebase.js";
import { doc, getDoc } from "https://www.gstatic.com/firebasejs/10.12.2/firebase-firestore.js";
import { onAuthStateChanged } from "https://www.gstatic.com/firebasejs/10.12.2/firebase-auth.js";

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

onAuthStateChanged(auth, async (user) => {
  if (user) {
    const userId = user.uid;
    loadData(userId);
  } else{
    console.log("Usuário não logado.")
  }
});

async function loadData(uid){
  console.log("UID logado:", uid);
  
  const docRef = doc(db,"users", uid);
  const docSnap = await getDoc(docRef);

  if (docSnap.exists()){
    const data = docSnap.data();
    console.log("Nome:", data.firstName);
    const personName = `${data.firstName || ""} ${data.lastName || ""}`;
    const userLetters = getInitials(personName);
    /*const createdDate = data.createdAt.toDate();
    const signinDate = createdDate.toLocaleDateString("pt-BR");*/

    document.getElementById("admin-email").textContent = data.email;
    document.getElementById("admin-occupation").textContent = data.occupation;
    document.getElementById("admin-name").textContent = personName;
    document.getElementById("icon-user").textContent = userLetters;
    /*document.getElementById("admin-date").textContent = signinDate;*/
  
  function getInitials(name) {
  const ignore = ["de", "da", "do", "dos", "das"];

  const words = name
    .split(" ")
    .filter(p => p.trim() !== "");

  if (words.length === 1) {
    return words[0].substring(0, 2).toUpperCase();
  }

  return words
    .filter(p => !ignore.includes(p.toLowerCase()))
    .map(p => p[0].toUpperCase())
    .slice(0, 2)
    .join("");
}
}
}