import { db } from "../js/api/firebase.js";
import { doc, getDoc, getDocs, collection, query, where } from "https://www.gstatic.com/firebasejs/10.12.2/firebase-firestore.js";

// ─── Paleta de cores para as categorias ───────────────────
const PALETTE = [
  '#7F77DD', '#1D9E75', '#D4537E', '#EF9F27',
  '#378ADD', '#639922', '#D85A30', '#888780',
];

// ─── Data atual ───────────────────────────────────────────
function atualizarData() {
  const data = new Date();
  const dias  = ["Domingo","Segunda-feira","Terça-feira","Quarta-feira","Quinta-feira","Sexta-feira","Sábado"];
  const meses = ["janeiro","fevereiro","março","abril","maio","junho","julho","agosto","setembro","outubro","novembro","dezembro"];
  document.getElementById("data-atual").innerText =
    `${dias[data.getDay()]}, ${data.getDate()} de ${meses[data.getMonth()]} de ${data.getFullYear()}`;
}
atualizarData();

// ─── Gráfico (instância global para poder destruir e recriar) ──
let donutChart = null;

function buildDonut(categoryCounts, total) {
  const labels = Object.keys(categoryCounts);
  const values = Object.values(categoryCounts);
  const colors = labels.map((_, i) => PALETTE[i % PALETTE.length]);

  // Atualiza número central
  document.querySelector('.donut-number').textContent = total;
  document.querySelector('.donut-label').textContent  = total === 1 ? 'local' : 'locais';

  // Destrói gráfico anterior se existir
  if (donutChart) donutChart.destroy();

  donutChart = new Chart(document.getElementById('donutChart'), {
    type: 'doughnut',
    data: {
      labels,
      datasets: [{
        data: values,
        backgroundColor: colors,
        borderWidth: 0,
      }]
    },
    options: {
      cutout: '72%',
      plugins: { legend: { display: false }, tooltip: { enabled: false } }
    }
  });

  // Monta legenda dinamicamente
  const legend = document.querySelector('.legend');
  legend.innerHTML = labels.map((label, i) => {
    const pct = total > 0 ? Math.round((values[i] / total) * 100) : 0;
    return `
      <div class="legend-item">
        <span class="dot" style="background:${colors[i]};"></span>
        <span class="legend-label">${label}</span>
        <span class="legend-count">${values[i]}</span>
        <span class="legend-pct">${pct}%</span>
      </div>`;
  }).join('');
}

// ─── Locais ───────────────────────────────────────────────
async function loadLocations(orgId) {
  const snapshot = await getDocs(query(
    collection(db, "locations"),
    where("orgId", "==", orgId)
  ));

  const container = document.querySelector(".body");
  container.innerHTML = "";

  const count = snapshot.size;
  document.getElementById("total-places-number").textContent = count;
  document.getElementById("total-places-text").textContent   = count === 1 ? "Local Cadastrado" : "Locais Cadastrados";

  // Conta categorias pela primeira tag de cada local
  const categoryCounts = {};

  snapshot.forEach((docSnap) => {
    const d        = docSnap.data();
    const placeId  = docSnap.id;
    const initials = getInitials(d.name);
    const firstTag = Array.isArray(d.tags) ? d.tags[0] || "Outros" : d.tags || "Outros";
    const date     = d.createdAt.toDate().toLocaleDateString("pt-BR");

    // Acumula para o gráfico
    categoryCounts[firstTag] = (categoryCounts[firstTag] || 0) + 1;

    container.innerHTML += `
      <div class="table-body" style="cursor:pointer"
           onclick="window.location.href='../place-teste-ruim/place.html?id=${placeId}'">
        <div class="box-name">
          <div class="table-org">
            <div class="icon-name">${initials}</div>
            <div class="informations">
              <p class="name-org">${d.name}</p>
              <p class="email-org">${d.city || ""}</p>
            </div>
          </div>
        </div>
        <div class="type-org priv">
          <div class="icon-type priv"><i class="fa-solid fa-building"></i></div>
          <p>${firstTag}</p>
        </div>
        <div class="mood-org active">
          <i class="fa-solid fa-circle"></i>
          <p>Publicado</p>
        </div>
        <div class="date-org"><p>${date}</p></div>
        <div class="actions-org aprove">
          <button onclick="event.stopPropagation(); window.location.href='../place-teste-ruim/place.html?id=${placeId}'">
            Ver Local
          </button>
        </div>
      </div>`;
  });

  // Constrói o gráfico com os dados reais
  buildDonut(categoryCounts, count);
}

function getInitials(name) {
  const ignore = ["de","da","do","dos","das"];
  const words  = name.split(" ").filter(p => p.trim());
  if (words.length === 1) return words[0].substring(0, 2).toUpperCase();
  return words
    .filter(p => !ignore.includes(p.toLowerCase()))
    .map(p => p[0].toUpperCase())
    .slice(0, 2)
    .join("");
}

// ─── Escuta dados do usuário vindos da sidebar ────────────
window.addEventListener("userLoaded", async (e) => {
  const { usuario } = e.detail;

  try {
    const orgSnap = await getDoc(doc(db, "organizations", usuario.orgId));
    const empresa = orgSnap.exists() ? orgSnap.data() : { orgName: "Organização não encontrada" };
    document.querySelector(".top-left h1").textContent = empresa.orgName;

    await loadLocations(usuario.orgId);
  } catch (err) {
    console.error("Erro ao carregar dados do dashboard:", err);
  }

  document.getElementById("btn-new-local")?.addEventListener("click", () => {
    window.location.href = "../form-locations/form-locations.html";
  });
});