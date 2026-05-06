// 🔥 IMPORTS
import { db, auth } from "/web-version/js/api/firebase.js";
import {
    collection,
    addDoc,
    doc,
    setDoc,
    getDoc,
    serverTimestamp
} from "https://www.gstatic.com/firebasejs/10.12.2/firebase-firestore.js";

import { onAuthStateChanged } 
from "https://www.gstatic.com/firebasejs/10.12.2/firebase-auth.js";

// ☁️ CLOUDINARY
const CLOUDINARY_CLOUD_NAME = "dopr7jbfd";
const CLOUDINARY_UPLOAD_PRESET = "locations_upload";

/* ═══════════════════════
   ESTADO
═══════════════════════ */
let currentStep = 1;
const totalSteps = 4;
let currentUser = null;
let currentOrgId = null;

/* ═══════════════════════
   AUTH
═══════════════════════ */
onAuthStateChanged(auth, async (user) => {
    currentUser = user || null;

    if (user) {
        try {
            const userSnap = await getDoc(doc(db, "users", user.uid));

            if (userSnap.exists()) {
                currentOrgId = userSnap.data().orgId;
                console.log("✅ OrgId:", currentOrgId);
            }
        } catch (error) {
            console.error(error);
        }
    }
});

/* ═══════════════════════
   IMAGENS
═══════════════════════ */
let selectedCoverFile    = null;
let selectedGalleryFiles = [];

// ── CAPA ──
window.previewCover = function(event) {
    const file = event.target.files[0];
    if (!file) return;

    selectedCoverFile = file;

    const preview = document.getElementById("cover-preview");
    preview.innerHTML = "";

    const wrapper = document.createElement("div");
    wrapper.className = "cover-preview-wrapper";

    const img = document.createElement("img");
    img.className = "cover-preview-img";

    const reader = new FileReader();
    reader.onload = (e) => { img.src = e.target.result; };
    reader.readAsDataURL(file);

    const btn = document.createElement("button");
    btn.textContent = "✕ Remover capa";
    btn.type = "button";
    btn.className = "remove-cover-btn";
    btn.onclick = () => {
        selectedCoverFile = null;
        preview.innerHTML = "";
        document.getElementById("cover-upload").value = "";
    };

    wrapper.appendChild(img);
    wrapper.appendChild(btn);
    preview.appendChild(wrapper);

    const err = document.getElementById("cover-error");
    if (err) { err.textContent = ""; err.classList.remove("show"); }
};

// ── GALERIA ──
window.previewGallery = function(event) {
    const files = Array.from(event.target.files);
    const preview  = document.getElementById("gallery-preview");
    const countEl  = document.getElementById("gallery-count");
    const errorEl  = document.getElementById("gallery-error");

    files.forEach(file => {
        if (selectedGalleryFiles.length >= 10) return;
        selectedGalleryFiles.push(file);

        const reader = new FileReader();
        reader.onload = (e) => {
            const wrapper = document.createElement("div");
            wrapper.className = "gallery-thumb";

            const img = document.createElement("img");
            img.src = e.target.result;

            const btn = document.createElement("button");
            btn.textContent = "✕";
            btn.type = "button";
            btn.className = "gallery-thumb-remove";
            btn.onclick = () => {
                const idx = selectedGalleryFiles.indexOf(file);
                if (idx > -1) selectedGalleryFiles.splice(idx, 1);
                wrapper.remove();
                countEl.textContent = selectedGalleryFiles.length;
            };

            wrapper.appendChild(img);
            wrapper.appendChild(btn);
            preview.appendChild(wrapper);
            countEl.textContent = selectedGalleryFiles.length;

            // limpa erro se já tiver 5+
            if (selectedGalleryFiles.length >= 5 && errorEl) {
                errorEl.textContent = "";
                errorEl.classList.remove("show");
            }
        };
        reader.readAsDataURL(file);
    });

    // reset input para permitir re-upload
    event.target.value = "";
};

// ── CLOUDINARY ──
async function uploadToCloudinary(file) {
    const formData = new FormData();
    formData.append("file", file);
    formData.append("upload_preset", CLOUDINARY_UPLOAD_PRESET);
    formData.append("folder", `locations/${currentOrgId}`);

    const res = await fetch(`https://api.cloudinary.com/v1_1/${CLOUDINARY_CLOUD_NAME}/image/upload`, {
        method: "POST",
        body: formData
    });

    const data = await res.json();
    return data.secure_url;
}

/* ═══════════════════════
   STEPS
═══════════════════════ */
window.goToStep = function(step) {
    document.querySelectorAll(".panel").forEach(p => p.classList.remove("active"));

    const painel = document.getElementById(`panel-${step}`);
    if (painel) painel.classList.add("active");

    document.querySelectorAll(".step-item").forEach((el, i) => {
        el.classList.remove("active", "done");
        if (i + 1 < step) el.classList.add("done");
        if (i + 1 === step) el.classList.add("active");
    });

    currentStep = step;
};

window.goToStepSafe = function(step) {
    if (step > currentStep + 1) return;

    if (step > currentStep && !validateStep(currentStep)) {
        console.log("❌ bloqueado");
        return;
    }

    goToStep(step);
};

window.nextStep = function(step) {
    if (validateStep(step)) goToStep(step + 1);
};

window.prevStep = function(from) {
    if (from > 1) goToStep(from - 1);
};

/* ═══════════════════════
   VALIDAÇÃO
═══════════════════════ */
function validateStep(step) {
    const panel = document.getElementById(`panel-${step}`);
    const required = panel.querySelectorAll("input[required], select[required], textarea[required]");

    let valid = true;

    // 🔹 CAMPOS OBRIGATÓRIOS
    required.forEach(field => {
        const errorMsg = field.parentElement.querySelector(".error-msg");

        if (!field.value.trim()) {
            field.classList.add("input-error");
            if (errorMsg) { errorMsg.textContent = "Campo obrigatório"; errorMsg.classList.add("show"); }
            valid = false;
        } else {
            field.classList.remove("input-error");
            if (errorMsg) { errorMsg.textContent = ""; errorMsg.classList.remove("show"); }
        }

        if (!field.dataset.listener) {
            field.addEventListener("input", () => {
                field.classList.remove("input-error");
                if (errorMsg) { errorMsg.textContent = ""; errorMsg.classList.remove("show"); }
            });
            field.dataset.listener = "true";
        }
    });

    /* ═══════════════════════
       STEP 2 — HORÁRIO
    ════════════════════════ */
    if (step === 2) {
        const rows     = document.querySelectorAll("#hours-list .hours-grid[data-day]");
        const errorMsg = document.getElementById("hours-error");
        let hasValid   = false;

        rows.forEach(row => {
            const isClosed = row.querySelector("input[type=checkbox]")?.checked;
            if (isClosed) { hasValid = true; return; }

            const inputs    = row.querySelectorAll(".hour-input");
            const abertura  = inputs[0]?.value.trim();
            const fechamento = inputs[1]?.value.trim();
            if (abertura && fechamento) hasValid = true;
        });

        if (!hasValid) {
            if (errorMsg) { errorMsg.textContent = "Preencha pelo menos um horário ou marque como fechado"; errorMsg.classList.add("show"); }
            valid = false;
        } else {
            if (errorMsg) { errorMsg.textContent = ""; errorMsg.classList.remove("show"); }
        }
    }

    /* ═══════════════════════
       STEP 3 — TAGS
    ════════════════════════ */
    if (step === 3) {
        if (!validateTags("tags-pill", "tags-error")) valid = false;
    }

    /* ═══════════════════════
       STEP 4 — PREÇO + IMAGENS
    ════════════════════════ */
    if (step === 4) {
        // preço
        const selected = document.querySelector(".price-opt.selected");
        const priceErr = document.getElementById("price-error");

        if (!selected) {
            if (priceErr) { priceErr.textContent = "Selecione se é grátis ou pago"; priceErr.classList.add("show"); }
            valid = false;
        } else {
            const isPago = selected.textContent.trim() === "Pago";
            if (isPago) {
                const value = document.getElementById("price-value").value;
                if (!value || Number(value) <= 0) {
                    if (priceErr) { priceErr.textContent = "Informe um valor válido"; priceErr.classList.add("show"); }
                    valid = false;
                } else {
                    if (priceErr) { priceErr.textContent = ""; priceErr.classList.remove("show"); }
                }
            } else {
                if (priceErr) { priceErr.textContent = ""; priceErr.classList.remove("show"); }
            }
        }

        // capa obrigatória
        const coverErr = document.getElementById("cover-error");
        if (!selectedCoverFile) {
            if (coverErr) { coverErr.textContent = "Adicione uma foto de capa"; coverErr.classList.add("show"); }
            valid = false;
        } else {
            if (coverErr) { coverErr.textContent = ""; coverErr.classList.remove("show"); }
        }

        // galeria mínimo 1
        const galleryErr = document.getElementById("gallery-error");
        if (selectedGalleryFiles.length < 1) {
            if (galleryErr) { galleryErr.textContent = `Adicione pelo menos 1 foto na galeria (${selectedGalleryFiles.length}/5)`; galleryErr.classList.add("show"); }
            valid = false;
        } else {
            if (galleryErr) { galleryErr.textContent = ""; galleryErr.classList.remove("show"); }
        }
    }

    if (!valid) {
        const first = panel.querySelector(".input-error");
        if (first) first.scrollIntoView({ behavior: "smooth", block: "center" });
    }

    return valid;
}

/* ═══════════════════════
   TAGS
═══════════════════════ */
function validateTags(containerId, errorId) {
    const container = document.getElementById(containerId);
    if (!container) return true;

    const selected = container.querySelectorAll(".tag.selected");
    const errorMsg = document.getElementById(errorId);

    if (selected.length === 0) {
        if (errorMsg) { errorMsg.textContent = "Selecione pelo menos uma tag"; errorMsg.classList.add("show"); }
        return false;
    } else {
        if (errorMsg) { errorMsg.textContent = ""; errorMsg.classList.remove("show"); }
        return true;
    }
}

window.toggleTag = function(el) {
    const container = el.parentElement;

    if (container.id === "tags-pill") {
        if (el.classList.contains("selected")) {
            el.classList.remove("selected");
        } else {
            container.querySelectorAll(".tag.selected").forEach(t => t.classList.remove("selected"));
            el.classList.add("selected");
        }
    } else {
        el.classList.toggle("selected");
    }

    const errorMsg = document.getElementById("tags-error");
    const selected = document.querySelectorAll("#tags-pill .tag.selected");
    if (selected.length > 0 && errorMsg) { errorMsg.textContent = ""; errorMsg.classList.remove("show"); }
};

/* ═══════════════════════
   HORÁRIOS
═══════════════════════ */
window.toggleClosed = function(checkbox) {
    const grid     = checkbox.closest(".hours-grid");
    const isClosed = checkbox.checked;

    grid.classList.toggle("is-closed", isClosed);
    grid.querySelectorAll(".hour-input").forEach(i => {
        i.disabled = isClosed;
        i.value    = "";
    });
};

window.getHorarios = function() {
    const horarios = {};

    document.querySelectorAll("#hours-list .hours-grid[data-day]").forEach(row => {
        const day      = row.dataset.day;
        const isClosed = row.querySelector("input[type=checkbox]")?.checked;

        if (isClosed) { horarios[day] = { fechado: true }; return; }

        const inputs = row.querySelectorAll(".hour-input");
        const open   = inputs[0]?.value.trim();
        const close  = inputs[1]?.value.trim();

        if (open && close) horarios[day] = { abertura: open, fechamento: close };
    });

    return horarios;
};

// máscara HH:MM
document.addEventListener("input", function(e) {
    if (!e.target.classList.contains("hour-input")) return;

    let value = e.target.value.replace(/\D/g, "");
    if (value.length > 4) value = value.slice(0, 4);
    if (value.length >= 3) value = value.replace(/(\d{2})(\d{1,2})/, "$1:$2");
    e.target.value = value;
});

/* ═══════════════════════
   PRICE
═══════════════════════ */
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
   CEP
═══════════════════════ */
document.getElementById("cep")?.addEventListener("blur", async function() {
    const cep = this.value.replace(/\D/g, "");
    if (cep.length !== 8) return;

    try {
        const res  = await fetch(`https://viacep.com.br/ws/${cep}/json/`);
        const data = await res.json();
        if (data.erro) return;

        document.getElementById("rua").value    = data.logradouro || "";
        document.getElementById("bairro").value = data.bairro     || "";
        document.getElementById("cidade").value = data.localidade || "";
        document.getElementById("uf").value     = data.uf         || "";
    } catch (e) {
        console.error("Erro CEP:", e);
    }
});

/* ═══════════════════════
   PUBLICAR
═══════════════════════ */
window.publishLocal = async function() {

    // 1. validar step 4
    if (!validateStep(4)) {
        console.log("❌ formulário inválido");
        return;
    }

    // 2. checar login
    if (!currentUser) { alert("Você precisa estar logado."); return; }
    if (!currentOrgId) { alert("Nenhuma organização encontrada."); return; }

    try {
        // ⏰ horários
        const horarios = getHorarios();

        // 🖼️ upload capa
        const coverUrl = await uploadToCloudinary(selectedCoverFile);

        // 🖼️ upload galeria
        const galleryUrls = await Promise.all(selectedGalleryFiles.map(f => uploadToCloudinary(f)));

        // 📦 dados completos
        const data = {
            orgId:     currentOrgId,
            createdBy: currentUser.uid,
            createdAt: serverTimestamp(),

            name:        document.getElementById("nome-local")?.value      || "",
            description: document.getElementById("descricao-local")?.value || "",

            cep:          document.getElementById("cep")?.value    || "",
            street:       document.getElementById("rua")?.value    || "",
            numero:       document.getElementById("numero")?.value || "",
            complemento:  document.getElementById("complemento")?.value || "",
            neighborhood: document.getElementById("bairro")?.value  || "",
            city:         document.getElementById("cidade")?.value  || "",
            uf:           document.getElementById("uf")?.value      || "",
            telefone:     document.getElementById("telefone")?.value || "",
            website:      document.getElementById("website")?.value  || "",

            // ⏰ horários
            horarios,

            // 🖼️ imagens
            cover:  coverUrl,
            images: galleryUrls,

            // 💰 preço
            price: {
                tipo:  document.querySelector(".price-opt.selected")?.textContent || "",
                valor: document.getElementById("price-value")?.value             || null,
                por:   document.getElementById("price-per")?.value               || ""
            },

            // 🏷️ tags
            tags: [...document.querySelectorAll("#tags-pill .tag.selected")]
                .map(t => t.textContent.replace("#", "").trim()),

            // 🎯 serviços
            services: [...document.querySelectorAll("#services-tags .tag.selected")]
                .map(t => t.textContent.trim()),

            // 🏗️ infraestrutura
            infrastructure: [...document.querySelectorAll("#infraestrutura input:checked")]
                .map(i => i.closest(".check-item")?.textContent?.trim())
        };

        // 💾 salvar
        await addDoc(collection(db, "locations"), data);

        // ✅ sucesso
        document.querySelectorAll(".panel").forEach(p => p.classList.remove("active"));
        const success = document.getElementById("success-screen");
        if (success) success.style.display = "block";

    } catch (e) {
        alert("Erro: " + e.message);
    }
};

window.resetForm = function() {
    location.reload();
};