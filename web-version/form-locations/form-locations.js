// 🔥 IMPORTA DO SEU FIREBASE (SEM MEXER NO firebase.js)
import { db, auth } from "../js/api/firebase.js";

// 🔥 IMPORTS NECESSÁRIOS
import { collection, addDoc, serverTimestamp } 
from "https://www.gstatic.com/firebasejs/10.12.2/firebase-firestore.js";

import { onAuthStateChanged } 
from "https://www.gstatic.com/firebasejs/10.12.2/firebase-auth.js";

/* ═══════════════════════
   ESTADO
═══════════════════════ */
let currentStep = 1;
const totalSteps = 4;
const tags = [];
let currentUser = null;

/* ═══════════════════════
   AUTH
═══════════════════════ */
onAuthStateChanged(auth, (user) => {
    currentUser = user || null;
});

/* ═══════════════════════
   STEPS (FUNCIONA COM HTML ATUAL)
═══════════════════════ */
window.goToStep = function(step) {

    document.querySelectorAll(".panel").forEach(p =>
        p.classList.remove("active")
    );

    // verifica se o painel existe antes de ativar
    const painel = document.getElementById(`panel-${step}`);
    if (painel) painel.classList.add("active");

    // verifica se a barra existe antes de mexer
    const barra = document.getElementById("progress-fill");
    if (barra) barra.style.width = `${(step / totalSteps) * 100}%`;

    // atualiza o stepper
    document.querySelectorAll(".step-item").forEach((el, i) => {
        el.classList.remove("active", "done");
        if (i + 1 < step)  el.classList.add("done");
        if (i + 1 === step) el.classList.add("active");
    });

    currentStep = step;
};

window.nextStep = function(from) {
    if (from < totalSteps) {
        window.goToStep(from + 1);
    }
};

window.prevStep = function(from) {
    if (from > 1) {
        window.goToStep(from - 1);
    }
};

/* ═══════════════════════
   MAPA (CORRIGE ERRO)
═══════════════════════ */
window.handleMapClick = function() {

    const cidade = document.getElementById("cidade").value;
    const rua = document.getElementById("rua").value;

    const query = encodeURIComponent(
        [rua, cidade].filter(Boolean).join(", ") || "Brasil"
    );

    window.open(`https://maps.google.com/?q=${query}`, "_blank");
};

/* ═══════════════════════
   HORÁRIOS
═══════════════════════ */
window.addHourRow = function() {

    const list = document.getElementById("hours-list");

    const row = document.createElement("div");
    row.className = "hours-grid";

    row.innerHTML = `
        <span class="day-label">Outro dia</span>
        <input type="text" placeholder="Abertura">
        <input type="text" placeholder="Fechamento">
    `;

    list.appendChild(row);
};

/* ═══════════════════════
   TAGS
═══════════════════════ */
window.addTag = function() {

    const input = document.getElementById("tag-input");
    const val = input.value.trim();

    if (!val) return;

    tags.push(val);
    renderTags();
    input.value = "";
};

function renderTags() {

    document.getElementById("tags-preview").innerHTML =
        tags.map((t, i) =>
            `<span class="tag-pill">${t}
                <button onclick="removeTag(${i})">×</button>
            </span>`
        ).join("");
}

window.removeTag = function(i) {
    tags.splice(i, 1);
    renderTags();
};

/* ═══════════════════════
   UI HELPERS
═══════════════════════ */
window.toggleTag = el => el.classList.toggle("selected");

window.selectPrice = function(el, isPago) {
  document.querySelectorAll('.price-opt').forEach(o => o.classList.remove('selected'));
  el.classList.add('selected');
  const box = document.getElementById('price-value-box');
  if (isPago) {
    box.classList.add('visible');
  } else {
    box.classList.remove('visible');
    document.getElementById('price-value').value = '';
    clearQuickBtns();
  }
};

window.setQuick = function(btn, val) {
  document.querySelectorAll('.price-quick-btn').forEach(b => b.classList.remove('active'));
  btn.classList.add('active');
  document.getElementById('price-value').value = val.toFixed(2);
};

window.clearQuickBtns = function() {
  document.querySelectorAll('.price-quick-btn').forEach(b => b.classList.remove('active'));
};

/* ═══════════════════════
   PREVIEW IMAGENS (OPCIONAL)
═══════════════════════ */
window.previewImages = function(e) {

    const preview = document.getElementById("image-preview");
    preview.innerHTML = "";

    Array.from(e.target.files).forEach(file => {

        const img = document.createElement("img");

        img.src = URL.createObjectURL(file);
        img.style = "width:80px;height:80px;object-fit:cover;border-radius:10px";

        preview.appendChild(img);
    });
};

/* ═══════════════════════
   PUBLICAR (SEM IMAGEM POR ENQUANTO)
═══════════════════════ */
window.publishLocal = async function() {

    if (!currentUser) {
        alert("Você precisa estar logado.");
        return;
    }

    try {

        const orgData = {
            nome: document.getElementById("nome-local").value,
            descricao: document.getElementById("descricao-local").value,
            cidade: document.getElementById("cidade").value,
            rua: document.getElementById("rua").value,
            createdAt: serverTimestamp(),
            createdBy: currentUser.uid
        };

        await addDoc(collection(db, "organizations"), orgData);

        alert("✅ Local publicado com sucesso!");

    } catch (e) {
        alert("Erro: " + e.message);
    }
};

/* ═══════════════════════
   INIT
═══════════════════════ */
document.addEventListener("DOMContentLoaded", () => {

    document.getElementById("tag-input")
        ?.addEventListener("keydown", e => {
            if (e.key === "Enter") {
                e.preventDefault();
                addTag();
            }
        });

    document.getElementById("file-upload")
        ?.addEventListener("change", previewImages);
});
// ⭐ Clique (seleciona nota)
window.setStars = function(tipo, valor) {

    const container = document.getElementById(`stars-${tipo}`);
    const stars = container.querySelectorAll(".star");

    // salva valor
    container.dataset.val = valor;

    // pinta estrelas fixas
    stars.forEach((star, index) => {
        star.classList.toggle("lit", index < valor);
    });
};


// ⭐ Hover (efeito visual bonito)
document.querySelectorAll(".stars").forEach(container => {

    const stars = container.querySelectorAll(".star");

    stars.forEach((star, i) => {

        // passa o mouse
        star.addEventListener("mouseover", () => {
            stars.forEach((s, j) => {
                s.classList.toggle("lit", j <= i);
            });
        });

        // tira o mouse
        star.addEventListener("mouseout", () => {
            const val = container.dataset.val || 0;

            stars.forEach((s, j) => {
                s.classList.toggle("lit", j < val);
            });
        });

    });

});