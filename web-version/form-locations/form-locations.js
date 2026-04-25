// 🔥 IMPORTS
import { db, auth } from "/web-version/js/api/firebase.js";
import {
    collection,
    addDoc,
    getDocs,
    query,
    where,
    serverTimestamp
} from "https://www.gstatic.com/firebasejs/10.12.2/firebase-firestore.js";

import { onAuthStateChanged } 
from "https://www.gstatic.com/firebasejs/10.12.2/firebase-auth.js";

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
            const userSnap = await getDocs(query(
                collection(db, "users"),
                where("uid", "==", user.uid)
            ));

            if (!userSnap.empty) {
                const userData = userSnap.docs[0].data();
                currentOrgId = userData.orgId;
                console.log("✅ OrgId:", currentOrgId);
            }
        } catch (error) {
            console.error(error);
        }
    }
});

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

            if (errorMsg) {
                errorMsg.textContent = "Campo obrigatório";
                errorMsg.classList.add("show");
            }

            valid = false;
        } else {
            field.classList.remove("input-error");

            if (errorMsg) {
                errorMsg.textContent = "";
                errorMsg.classList.remove("show");
            }
        }

        // evita duplicar listener
        if (!field.dataset.listener) {
            field.addEventListener("input", () => {
                field.classList.remove("input-error");
                if (errorMsg) {
                    errorMsg.textContent = "";
                    errorMsg.classList.remove("show");
                }
            });
            field.dataset.listener = "true";
        }
    });

    /* ═══════════════════════
       STEP 2 — HORÁRIO
    ════════════════════════ */
    if (step === 2) {
        const rows = document.querySelectorAll("#hours-list .hours-grid");
        const errorMsg = document.getElementById("hours-error");

        let hasValid = false;

        rows.forEach(row => {
            const inputs = row.querySelectorAll("input");

            const abertura = inputs[0].value.trim();
            const fechamento = inputs[1].value.trim();

            if (abertura && fechamento) {
                hasValid = true;
            }
        });

        if (!hasValid) {
            if (errorMsg) {
                errorMsg.textContent = "Preencha pelo menos um horário";
                errorMsg.classList.add("show");
            }
            valid = false;
        } else {
            if (errorMsg) {
                errorMsg.textContent = "";
                errorMsg.classList.remove("show");
            }
        }
    }

    /* ═══════════════════════
       STEP 3 — TAGS
    ════════════════════════ */
    if (step === 3) {
        if (!validateTags("tags-pill", "tags-error")) valid = false;
    }

    /* ═══════════════════════
       STEP 4 — PREÇO
    ════════════════════════ */
    if (step === 4) {
        const selected = document.querySelector(".price-opt.selected");
        const errorMsg = document.getElementById("price-error");

        if (!selected) {
            if (errorMsg) {
                errorMsg.textContent = "Selecione se é grátis ou pago";
                errorMsg.classList.add("show");
            }
            valid = false;
        } else {
            const isPago = selected.textContent.trim() === "Pago";

            if (isPago) {
                const value = document.getElementById("price-value").value;

                if (!value || Number(value) <= 0) {
                    if (errorMsg) {
                        errorMsg.textContent = "Informe um valor válido";
                        errorMsg.classList.add("show");
                    }
                    valid = false;
                } else {
                    if (errorMsg) {
                        errorMsg.textContent = "";
                        errorMsg.classList.remove("show");
                    }
                }
            } else {
                if (errorMsg) {
                    errorMsg.textContent = "";
                    errorMsg.classList.remove("show");
                }
            }
        }
    }

    /* ═══════════════════════ */

    if (!valid) {
        const first = panel.querySelector(".input-error");
        if (first) {
            first.scrollIntoView({ behavior: "smooth", block: "center" });
        }
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
        if (errorMsg) {
            errorMsg.textContent = "Selecione pelo menos uma tag";
            errorMsg.classList.add("show"); // 🔥 ESSENCIAL
        }
        return false;
    } else {
        if (errorMsg) {
            errorMsg.textContent = "";
            errorMsg.classList.remove("show"); // 🔥 ESSENCIAL
        }
        return true;
    }
}

window.toggleTag = function(el) {
    el.classList.toggle("selected");

    // remove erro se tiver pelo menos 1 selecionada
    const container = document.getElementById("tags-pill");
    const selected = container.querySelectorAll(".tag.selected");
    const errorMsg = document.getElementById("tags-error");

    if (selected.length > 0 && errorMsg) {
        errorMsg.textContent = "";
        errorMsg.classList.remove("show");
    }
};

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

window.clearQuickBtns = function() {
    document.querySelectorAll('.price-quick-btn').forEach(b => b.classList.remove('active'));
};

// ===============================
// ⏰ HORÁRIOS - SISTEMA COMPLETO
// ===============================

// ===============================
// ➕ ADICIONAR DIA (EDITÁVEL)
// ===============================
window.addHourRow = function () {
    const list = document.getElementById("hours-list");

    const row = document.createElement("div");
    row.className = "hours-grid dynamic";

    row.innerHTML = `
        <input type="text" class="day-input" placeholder="Nome do dia">

        <input type="text" class="hour-input" maxlength="5" placeholder="Abertura">
        <input type="text" class="hour-input" maxlength="5" placeholder="Fechamento">

        <button type="button" onclick="removeHourRow(this)">✕</button>
    `;

    list.appendChild(row);
};

// ===============================
// ❌ REMOVER (só dinâmicos)
// ===============================
window.removeHourRow = function (btn) {
    const row = btn.closest(".hours-grid");

    if (row.classList.contains("fixed")) return;

    row.remove();
};

// ===============================
// 🧠 MÁSCARA TIPO CEP (HH:MM)
// ===============================
document.addEventListener("input", function (e) {
    if (!e.target.classList.contains("hour-input")) return;

    let value = e.target.value.replace(/\D/g, "");

    if (value.length > 4) value = value.slice(0, 4);

    if (value.length >= 3) {
        value = value.replace(/(\d{2})(\d{1,2})/, "$1:$2");
    }

    e.target.value = value;
});

// ===============================
// 📦 PEGAR HORÁRIOS (FIREBASE)
// ===============================
window.getHorarios = function () {
        
    const horarios = {};

    document.querySelectorAll("#hours-list .hours-grid").forEach(row => {

        const inputs = row.querySelectorAll(".hour-input");
        const open = inputs[0]?.value;
        const close = inputs[1]?.value;

        const fixedLabel = row.querySelector(".day-label")?.textContent;
        const dynamicLabel = row.querySelector(".day-input")?.value;

        if (fixedLabel && open && close) {
            horarios[fixedLabel] = { abertura: open, fechamento: close };
        }

        if (dynamicLabel && open && close) {
            horarios[dynamicLabel] = { abertura: open, fechamento: close };
        }
    });

    document.querySelectorAll("#hours-list .hours-grid").forEach(row => {

        const inputs = row.querySelectorAll(".hour-input");
        const open = inputs[0]?.value;
        const close = inputs[1]?.value;

        // 🔵 FIXOS (não editáveis)
        if (row.classList.contains("fixed")) {
            const label = row.querySelector(".day-label")?.textContent;

            if (label && open && close) {
                horarios[label] = { abertura: open, fechamento: close };
            }
        }

        // 🟡 DINÂMICOS (editáveis)
        if (row.classList.contains("dynamic")) {
            const label = row.querySelector(".day-input")?.value?.trim();

            if (label && open && close) {
                horarios[label] = { abertura: open, fechamento: close };
            }
        }
    });

    return horarios;
};

/* ═══════════════════════
   CEP
═══════════════════════ */
document.getElementById("cep")?.addEventListener("blur", async function() {
    const cep = this.value.replace(/\D/g, "");
    if (cep.length !== 8) return;

    try {
        const res = await fetch(`https://viacep.com.br/ws/${cep}/json/`);
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

window.publishLocal = async function () {

    // 🔴 1. validar STEP 4 primeiro
    const valid = validateStep(4);

    if (!valid) {
        console.log("❌ formulário inválido");
        return;
    }

    // 🔐 2. checar login
    if (!currentUser) {
        alert("Você precisa estar logado.");
        return;
    }

    if (!currentOrgId) {
        alert("Nenhuma organização encontrada.");
        return;
    }

    try {

        // ⏰ HORÁRIOS
        
            const horarios = {};

            document.querySelectorAll("#hours-list .hours-grid").forEach(row => {

                const open = row.querySelectorAll(".hour-input")[0]?.value;
                const close = row.querySelectorAll(".hour-input")[1]?.value;

                // 🔵 FIXOS
                const fixed = row.querySelector(".day-label")?.textContent?.trim();

                if (fixed && open && close) {
                    horarios[fixed] = { abertura: open, fechamento: close };
                }

                // 🟡 DINÂMICOS
                const dynamic = row.querySelector(".day-input")?.value?.trim();

                if (dynamic && open && close) {
                    horarios[dynamic] = { abertura: open, fechamento: close };
                }
            });

        // 📦 DADOS COMPLETOS
        const data = {
            orgId: currentOrgId,
            createdBy: currentUser.uid,
            createdAt: serverTimestamp(),

            nome: document.getElementById("nome-local")?.value || "",
            descricao: document.getElementById("descricao-local")?.value || "",

            cep: document.getElementById("cep")?.value || "",
            rua: document.getElementById("rua")?.value || "",
            bairro: document.getElementById("bairro")?.value || "",
            cidade: document.getElementById("cidade")?.value || "",
            uf: document.getElementById("uf")?.value || "",

            // ⏰ horários
            horarios,

            // 💰 preço (IMPORTANTE)
            preco: {
                tipo: document.querySelector(".price-opt.selected")?.textContent || "",
                valor: document.getElementById("price-value")?.value || null,
                por: document.getElementById("price-per")?.value || ""
            },

            // 🏷️ tags
            tags: [...document.querySelectorAll("#tags-pill .tag.selected")]
                .map(t => t.textContent.replace("#", "").trim()),

            // 🎯 serviços
            servicos: [...document.querySelectorAll("#services-tags .tag.selected")]
                .map(t => t.textContent.trim()),

            // 🏗️ infraestrutura
            infraestrutura: [...document.querySelectorAll("#infraestrutura input:checked")]
                .map(i => i.closest(".check-item")?.textContent?.trim())
        };

        // 💾 SALVAR
        await addDoc(collection(db, "locals"), data);

        alert("✅ Publicado com sucesso!");

    } catch (e) {
        alert("Erro: " + e.message);
    }
};