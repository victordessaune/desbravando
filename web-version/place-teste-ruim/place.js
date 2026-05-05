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
    "Praia":      "Praia",
    "ECO":        "ECO",
    "Parques":    "Parque",
    "Religioso":  "Religioso",
    "GastroBar":  "GastroBar",
    "Histórico":  "Histórico"
};

// ── Helpers ────────────────────────────────────
function getParam(key) {
    return new URLSearchParams(window.location.search).get(key);
}

function showError() {
    document.getElementById("loading-screen").style.display = "none";
    document.getElementById("error-screen").style.display = "flex";
}

function showContent() {
    document.getElementById("loading-screen").style.display = "none";
    document.getElementById("place-content").style.display = "block";
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

    // ── Banner ──────────────────────────────────
    const banner = document.getElementById("banner-place");
    if (data.images && data.images.length > 0) {
        banner.style.backgroundImage = `url('${data.images[0]}')`;
        document.querySelector(".banner-overlay").style.background =
            "linear-gradient(to bottom, rgba(0,0,0,0.1), rgba(0,0,0,0.45))";
        document.getElementById("no-photo-msg").style.display = "none";
    }

    // ── Tipo (derivado das tags) ─────────────────
    const tipoEl = document.getElementById("place-tipo");
    const tipoTexto = data.tags && data.tags.length > 0
        ? (TAG_TIPO[data.tags[0]] || data.tags[0])
        : "Local";
    tipoEl.textContent = tipoTexto;

    // ── Nome ────────────────────────────────────
    document.getElementById("place-nome").textContent = data.name || "—";
    document.title = `Desbravando — ${data.name || "Local"}`;

    // ── Cidade e Rua ────────────────────────────
    const cidade = [data.city, data.uf].filter(Boolean).join(" / ");
    document.getElementById("place-cidade").textContent = cidade || "—";
    document.getElementById("place-rua").textContent    = data.street || "—";

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
    descEl.textContent = data.description
        ? data.description
        : "Nenhuma descrição cadastrada.";

    // ── Infraestrutura ──────────────────────────
    const infraList = document.getElementById("infra-list");
    if (data.infrastructure && data.infrastructure.length > 0) {
        data.infrastructure.forEach(item => {
            if (!item) return;
            const icon = INFRA_ICONS[item] || "fa-circle-check";
            const tag = document.createElement("div");
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
            chip.className = "tag-chip service";
            chip.textContent = s;
            servicosList.appendChild(chip);
        });
    } else {
        document.getElementById("box-servicos").style.display = "none";
    }

    // ── Info Geral ──────────────────────────────
    const infoList = document.getElementById("info-list");
    addInfoRow(infoList, "fa-phone",   "purple", "Telefone", data.telefone || data.phone || null);
    addInfoRow(infoList, "fa-globe",   "blue",   "Website",  data.website  || null);
    addInfoRow(infoList, "fa-building","green",  "Organização", data.orgId || null);

    if (infoList.children.length === 0) {
        infoList.innerHTML = `<span class="empty-msg">Nenhuma informação adicional.</span>`;
    }

    // ── Horários ────────────────────────────────
    const horariosEl = document.getElementById("horarios-list");
    if (data.horarios && Object.keys(data.horarios).length > 0) {
        horariosEl.innerHTML = "";
        Object.entries(data.horarios).forEach(([dia, horas]) => {
            const row = document.createElement("div");
            row.className = "horario-row";
            row.innerHTML = `
                <span class="horario-dia">${dia}</span>
                <span class="horario-time">
                    <i class="fa-solid fa-clock"></i>
                    ${horas.abertura} – ${horas.fechamento}
                </span>
            `;
            horariosEl.appendChild(row);
        });
    }

    // ── Tags ────────────────────────────────────
    const tagsList = document.getElementById("tags-list");
    if (data.tags && data.tags.length > 0) {
        data.tags.forEach(t => {
            const chip = document.createElement("span");
            chip.className = "tag-chip pill";
            chip.textContent = `# ${t}`;
            tagsList.appendChild(chip);
        });
    } else {
        document.getElementById("box-tags").style.display = "none";
    }

    // ── Endereço completo ───────────────────────
    const addressEl = document.getElementById("address-block");
    const partes = [
        data.street && data.numero ? `${data.street}, ${data.numero}` : data.street,
        data.complemento,
        data.neighborhood,
        cidade,
        data.cep ? `CEP ${data.cep}` : null
    ].filter(Boolean);
    addressEl.innerHTML = partes.join("<br>") || "—";

    // ── Preço ───────────────────────────────────
    const precoContent = document.getElementById("preco-content");
    if (data.price) {
        const tipo = (data.price.tipo || "").toLowerCase();
        const isGratis = tipo.includes("grát");

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

// ── Init ───────────────────────────────────────
(async function init() {
    const placeId = getParam("id");

    if (!placeId) {
        showError();
        return;
    }

    try {
        const snap = await getDoc(doc(db, "locations", placeId));

        if (!snap.exists()) {
            showError();
            return;
        }

        populate(placeId, snap.data());
        showContent();

    } catch (err) {
        console.error("Erro ao carregar local:", err);
        showError();
    }
})();