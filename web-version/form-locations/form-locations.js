// 🔥 IMPORTS
import { db, auth } from "../js/api/firebase.js";
import { collection, addDoc, serverTimestamp } 
from "https://www.gstatic.com/firebasejs/10.12.2/firebase-firestore.js";
import { onAuthStateChanged } 
from "https://www.gstatic.com/firebasejs/10.12.2/firebase-auth.js";

/* ═══════════════════════
   ESTADO
═══════════════════════ */
let currentStep = 1;
const totalSteps = 4;
let currentUser = null;

/* ═══════════════════════
   AUTH
═══════════════════════ */
onAuthStateChanged(auth, (user) => {
    currentUser = user || null;
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
    if (step <= currentStep + 1) window.goToStep(step);
};

window.prevStep = function(from) {
    if (from > 1) window.goToStep(from - 1);
};

/* ═══════════════════════
   VALIDAÇÃO GERAL
═══════════════════════ */
/* ═══════════════════════
   VALIDAÇÃO GERAL
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

/* ═══════════════════════
   CEP (ViaCEP)
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
window.goToStepSafe = function(step) {

    // se está tentando avançar
    if (step > currentStep) {

        const valid = validateStep(currentStep);

        if (!valid) {
            console.log("❌ bloqueado pelo stepper");
            return;
        }
    }

    // pode ir (voltar ou avançar validado)
    goToStep(step);
};

window.nextStep = function(step) {
    if (validateStep(step)) {
        goToStep(step + 1);
    }
};
/* ═══════════════════════
   PUBLICAR
═══════════════════════ */
window.publishLocal = async function() {

    // 🔴 PRIMEIRO valida o último step
    const valid = validateStep(4);

    if (!valid) {
        console.log("❌ formulário inválido");
        return;
    }

    // 🔐 DEPOIS verifica login
    if (!currentUser) {
        alert("Você precisa estar logado.");
        return;
    }

    try {

        const data = {
            nome: document.getElementById("nome-local").value,
            descricao: document.getElementById("descricao-local").value,
            cidade: document.getElementById("cidade").value,
            rua: document.getElementById("rua").value,
            createdAt: serverTimestamp(),
            createdBy: currentUser.uid
        };

        await addDoc(collection(db, "organizations"), data);

        alert("✅ Publicado!");

    } catch (e) {
        alert("Erro: " + e.message);
    }
};