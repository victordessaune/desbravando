import { auth, db } from "../js/api/firebase.js";
import { onAuthStateChanged } from "https://www.gstatic.com/firebasejs/10.12.2/firebase-auth.js";
import { doc, getDoc, getDocs, collection, query, where } from "https://www.gstatic.com/firebasejs/10.12.2/firebase-firestore.js";

function getInitials(name) {
    const ignore = ["de", "da", "do", "dos", "das"];
    const words = name.split(" ").filter(p => p.trim() !== "");
    if (words.length === 1) return words[0].substring(0, 2).toUpperCase();
    return words
        .filter(p => !ignore.includes(p.toLowerCase()))
        .map(p => p[0].toUpperCase())
        .slice(0, 2)
        .join("");
}

const GRADIENT_COLORS = ["", "green", "pink", "blue"];
function getIconColor(index) {
    return GRADIENT_COLORS[index % GRADIENT_COLORS.length];
}

function getTagClass(tag) {
    const eco = ["parque", "praia", "natureza", "trilha"];
    const pub = ["museu", "cultura", "arte", "monumento", "praça"];
    const t = (tag || "").toLowerCase();
    if (eco.some(k => t.includes(k))) return "eco";
    if (pub.some(k => t.includes(k))) return "pub";
    return "priv";
}

let allPlaces = [];
let activeFilter = "all";
let searchTerm = "";

function renderTable() {
    const body = document.getElementById("places-table-body");
    const emptyState = document.getElementById("empty-state");
    const countEl = document.getElementById("places-count");
    const labelEl = document.getElementById("places-label");

    const filtered = allPlaces.filter(p => {
        const matchFilter = activeFilter === "all" || (p.tags || []).some(t => t.toLowerCase() === activeFilter.toLowerCase());
        const q = searchTerm.toLowerCase();
        const matchSearch = !q
            || p.name.toLowerCase().includes(q)
            || (p.city || "").toLowerCase().includes(q)
            || (p.tags || []).some(t => t.toLowerCase().includes(q));
        return matchFilter && matchSearch;
    });

    countEl.textContent = filtered.length;
    labelEl.textContent = filtered.length === 1 ? "local" : "locais";

    if (!filtered.length) {
        body.innerHTML = "";
        emptyState.style.display = "flex";
        return;
    }
    emptyState.style.display = "none";

    body.innerHTML = filtered.map((p, i) => {
        const firstTag = Array.isArray(p.tags) ? p.tags[0] || "—" : p.tags || "—";
        const tagClass = getTagClass(firstTag);
        const isActive = p.status !== "pendent";
        const dateStr = p.createdAt
            ? p.createdAt.toDate().toLocaleDateString("pt-BR")
            : "—";

        return `
        <div class="places-row" onclick="window.location.href='../place-teste-ruim/place.html?id=${p.id}'">
            <div class="col-name">
                <div class="icon-name ${getIconColor(i)}">${p.initials}</div>
                <div>
                    <p class="name-org">${p.name}</p>
                    <p class="email-org">${p.city || ""}</p>
                </div>
            </div>
            <div>
                <span class="tag-place ${tagClass}">
                    <i class="fa-solid fa-location-dot"></i>
                    ${firstTag}
                </span>
            </div>
            <div>
                <span class="status-place ${isActive ? "active" : "pendent"}">
                    <i class="fa-solid fa-circle"></i>
                    ${isActive ? "Publicado" : "Pendente"}
                </span>
            </div>
            <div class="date-place">${dateStr}</div>
            <div class="actions-place">
                <button class="btn-ver" onclick="event.stopPropagation(); window.location.href='../place-teste-ruim/place.html?id=${p.id}'">
                    Ver Local
                </button>
            </div>
        </div>`;
    }).join("");
}

function buildFilters() {
    const tags = new Set();
    allPlaces.forEach(p => (p.tags || []).forEach(t => tags.add(t)));

    const row = document.getElementById("filter-row");
    row.innerHTML = `<button class="filter-btn active" data-filter="all">Todos</button>`;

    tags.forEach(tag => {
        const btn = document.createElement("button");
        btn.className = "filter-btn";
        btn.dataset.filter = tag;
        btn.textContent = tag;
        row.appendChild(btn);
    });

    row.addEventListener("click", e => {
        const btn = e.target.closest(".filter-btn");
        if (!btn) return;
        row.querySelectorAll(".filter-btn").forEach(b => b.classList.remove("active"));
        btn.classList.add("active");
        activeFilter = btn.dataset.filter;
        renderTable();
    });
}

document.getElementById("search-input").addEventListener("input", e => {
    searchTerm = e.target.value;
    renderTable();
});

onAuthStateChanged(auth, async (user) => {
    if (!user) {
        window.location.href = "../login/login.html";
        return;
    }

    document.getElementById("btn-new-local").addEventListener("click", () => {
        window.location.href = "../form-locations/form-locations.html";
    });

    try {
        const userSnap = await getDoc(doc(db, "users", user.uid));
        const usuario = userSnap.data();

        const locationsRef = query(
            collection(db, "locations"),
            where("orgId", "==", usuario.orgId)
        );
        const snapshot = await getDocs(locationsRef);

        allPlaces = [];
        snapshot.forEach((docSnap, i) => {
            const data = docSnap.data();
            allPlaces.push({
                id: docSnap.id,
                name: data.name || "Sem nome",
                city: data.city || "",
                uf: data.uf || "XX",
                tags: Array.isArray(data.tags) ? data.tags : [data.tags].filter(Boolean),
                status: data.status || "active",
                createdAt: data.createdAt,
                initials: getInitials(data.name || "??"),
            });
        });

        document.getElementById("loading-state").style.display = "none";
        buildFilters();
        renderTable();

    } catch (err) {
        console.error("Erro ao carregar locais:", err);
        document.getElementById("loading-state").style.display = "none";
        document.getElementById("empty-state").style.display = "flex";
    }
});