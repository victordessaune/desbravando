// ══════════════════════════════════════════════
//  place.js  —  Detalhe de Local
// ══════════════════════════════════════════════

import { db } from "../js/api/firebase.js";
import {
    doc,
    getDoc
} from "https://www.gstatic.com/firebasejs/10.12.2/firebase-firestore.js";

// ── Mapa de infraestrutura → ícone FA ──────────
const INFRA_ICONS = {
    "Estacionamento": "fa-square-parking",
    "Lanchonete":     "fa-utensils",
    "Playground":     "fa-children",
    "Bicicletas":     "fa-bicycle",
    "Segurança":      "fa-shield-halved",
    "Toaletes":       "fa-restroom",
    "Monitoramento":  "fa-video",
    "Na Sombra":      "fa-tree",
    "Familiar":       "fa-people-group",
    "Climatizado":    "fa-snowflake",
    "Iluminação":     "fa-lightbulb",
    "Acessível":      "fa-wheelchair"
};

// ── Mapa de tags → tipo de local ───────────────
const TAG_TIPO = {
    "Praia":     "Praia",
    "ECO":       "ECO",
    "Parques":   "Parque",
    "Religioso": "Religioso",
    "GastroBar": "GastroBar",
    "Histórico": "Histórico"
};

// ── Helpers ────────────────────────────────────
function getParam(key) {
    return new URLSearchParams(window.location.search).get(key);
}

function showError() {
    document.getElementById("loading-screen").style.display = "none";
    document.getElementById("error-screen").style.display   = "flex";
}

function showContent() {
    document.getElementById("loading-screen").style.display  = "none";
    document.getElementById("place-content").style.display   = "block";
}

// ── Info list helper ───────────────────────────
function addInfoRow(container, iconClass, colorClass, label, value) {
    if (!value) return;
    const row = document.createElement("div");
    row.className = "info-space";
    row.innerHTML = `
        <div class="bg-icon ${colorClass}">
            <i class="fa-solid ${iconClass}"></i>
        </div>
        <div class="info-content">
            <span class="info-label">${label}</span>
            <span class="info-value">${value}</span>
        </div>
    `;
    container.appendChild(row);
}

// ── Populate ───────────────────────────────────
function populate(id, data) {

    // ── Banner (capa) ────────────────────────────
    const banner = document.getElementById("banner-place");
    if (data.cover) {
        banner.style.backgroundImage = `url('${data.cover}')`;
        document.querySelector(".banner-overlay").style.background =
            "linear-gradient(to bottom, rgba(0,0,0,0.15), rgba(0,0,0,0.55))";
        document.getElementById("no-photo-msg").style.display = "none";
    }

    // ── Tipo (derivado das tags) ─────────────────
    const tipoTexto = data.tags && data.tags.length > 0
        ? (TAG_TIPO[data.tags[0]] || data.tags[0])
        : "Local";
    document.getElementById("place-tipo").textContent = tipoTexto;

    // ── Nome ────────────────────────────────────
    document.getElementById("place-nome").textContent = data.name || "—";
    document.title = `Desbravando — ${data.name || "Local"}`;

    // ── Cidade e Bairro ─────────────────────────
    const cidade = [data.city, data.uf].filter(Boolean).join(" / ");
    document.getElementById("place-cidade").textContent = cidade            || "—";
    document.getElementById("place-rua").textContent    = data.neighborhood || "—";

    // ── Badge de preço ──────────────────────────
    const badge = document.getElementById("place-preco-badge");
    if (data.price) {
        const isGratis = (data.price.tipo || "").toLowerCase().includes("grát");
        badge.textContent = isGratis ? "Grátis" : "Pago";
        if (!isGratis) badge.classList.add("pago");
    } else {
        badge.style.display = "none";
    }

    // ── Descrição ───────────────────────────────
    const descEl = document.getElementById("place-descricao");
    descEl.textContent = data.description || "Nenhuma descrição cadastrada.";

    // ── Galeria ──────────────────────────────────
    const galleryGrid = document.getElementById("gallery-grid");
    const boxGaleria  = document.getElementById("box-galeria");

    if (data.images && data.images.length > 0) {
        boxGaleria.style.display = "block";
        data.images.forEach(url => {
            const item = document.createElement("div");
            item.className = "gallery-item";
            item.innerHTML = `<img src="${url}" alt="Foto do local" loading="lazy">`;
            item.onclick   = () => openModal(url);
            galleryGrid.appendChild(item);
        });
    } else {
        galleryGrid.innerHTML = `<span class="empty-msg">Nenhuma foto cadastrada.</span>`;
        boxGaleria.style.display = "block";
    }

    // ── Infraestrutura ──────────────────────────
    const infraList = document.getElementById("infra-list");
    if (data.infrastructure && data.infrastructure.length > 0) {
        data.infrastructure.forEach(item => {
            if (!item) return;
            const icon = INFRA_ICONS[item] || "fa-circle-check";
            const tag  = document.createElement("div");
            tag.className = "box-tag";
            tag.innerHTML = `
                <i class="fa-solid ${icon}"></i>
                <span class="tag-text">${item}</span>
            `;
            infraList.appendChild(tag);
        });
    } else {
        document.getElementById("box-infraestrutura").style.display = "none";
    }

    // ── Serviços ────────────────────────────────
    const servicosList = document.getElementById("servicos-list");
    if (data.services && data.services.length > 0) {
        data.services.forEach(s => {
            if (!s) return;
            const chip = document.createElement("span");
            chip.className   = "tag-chip service";
            chip.textContent = s;
            servicosList.appendChild(chip);
        });
    } else {
        document.getElementById("box-servicos").style.display = "none";
    }

    // ── Info Geral ──────────────────────────────
    const infoList = document.getElementById("info-list");
    addInfoRow(infoList, "fa-phone",    "purple", "Telefone",     data.telefone || data.phone || null);
    addInfoRow(infoList, "fa-globe",    "blue",   "Website",      data.website  || null);

    if (infoList.children.length === 0) {
        infoList.innerHTML = `<span class="empty-msg">Nenhuma informação adicional.</span>`;
    }

    // ── Horários ────────────────────────────────
    
    const horariosEl = document.getElementById("horarios-list");
    if (data.horarios && Object.keys(data.horarios).length > 0) {
    horariosEl.innerHTML = "";

    const ORDEM_DIAS = ["Segunda", "Terça", "Quarta", "Quinta", "Sexta", "Sábado", "Domingo", "Feriado"];

    Object.entries(data.horarios)
        .sort(([a], [b]) => {
            const oa = ORDEM_DIAS.indexOf(a) === -1 ? 999 : ORDEM_DIAS.indexOf(a);
            const ob = ORDEM_DIAS.indexOf(b) === -1 ? 999 : ORDEM_DIAS.indexOf(b);
            return oa - ob;
        })
        .forEach(([dia, horas]) => {
            const row = document.createElement("div");
            row.className = "horario-row";

            const timeHtml = horas.fechado
                ? `<span class="horario-fechado">Fechado</span>`
                : `<span class="horario-time">
                       <i class="fa-solid fa-clock"></i>
                       ${horas.abertura} – ${horas.fechamento}
                   </span>`;

            row.innerHTML = `<span class="horario-dia">${dia}</span>${timeHtml}`;
            horariosEl.appendChild(row);
        });
    }

    // ── Tags ────────────────────────────────────
    const tagsList = document.getElementById("tags-list");
    if (data.tags && data.tags.length > 0) {
        data.tags.forEach(t => {
            const chip = document.createElement("span");
            chip.className   = "tag-chip pill";
            chip.textContent = `# ${t}`;
            tagsList.appendChild(chip);
        });
    } else {
        document.getElementById("box-tags").style.display = "none";
    }

    // ── Endereço completo ───────────────────────
    const addressEl = document.getElementById("address-block");
    const partes = [
        data.street && data.numero
            ? `${data.street}, ${data.numero}`
            : data.street,
        data.complemento,
        data.neighborhood,
        cidade,
        data.cep ? `CEP ${data.cep}` : null
    ].filter(Boolean);
    addressEl.innerHTML = partes.join("<br>") || "—";

    // ── Preço ───────────────────────────────────
    const precoContent = document.getElementById("preco-content");
    if (data.price) {
        const isGratis = (data.price.tipo || "").toLowerCase().includes("grát");

        if (isGratis) {
            precoContent.innerHTML = `
                <div class="preco-gratis">
                    <i class="fa-solid fa-circle-check"></i>
                    Entrada gratuita
                </div>`;
        } else {
            const valor = data.price.valor
                ? parseFloat(data.price.valor).toLocaleString("pt-BR", {
                      style: "currency", currency: "BRL"
                  })
                : "—";
            const por = data.price.por || "";
            precoContent.innerHTML = `
                <div class="preco-pago">
                    <span class="preco-valor">${valor}</span>
                    ${por ? `<span class="preco-por">por ${por.toLowerCase()}</span>` : ""}
                </div>`;
        }
    } else {
        document.getElementById("box-preco").style.display = "none";
    }
}

// ── Modal ──────────────────────────────────────
window.openModal = function(src) {
    document.getElementById("modal-img").src = src;
    document.getElementById("photo-modal").classList.add("active");
    document.body.style.overflow = "hidden";
};

window.closeModal = function(e) {
    // fecha só se clicar no fundo ou no botão, não na imagem
    if (e && e.target === document.getElementById("modal-img")) return;
    document.getElementById("photo-modal").classList.remove("active");
    document.getElementById("modal-img").src = "";
    document.body.style.overflow = "";
};

document.addEventListener("keydown", e => {
    if (e.key === "Escape") closeModal();
});

// ── Init ───────────────────────────────────────
(async function init() {
    const placeId = getParam("id");

    if (!placeId) { showError(); return; }

    try {
        const snap = await getDoc(doc(db, "locations", placeId));

        if (!snap.exists()) { showError(); return; }

        populate(placeId, snap.data());
        showContent();

    } catch (err) {
        console.error("Erro ao carregar local:", err);
        showError();
    }
})();