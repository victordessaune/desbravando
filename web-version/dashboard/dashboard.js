import { auth, db } from "../js/api/firebase.js";
import { onAuthStateChanged } from "https://www.gstatic.com/firebasejs/10.12.2/firebase-auth.js";
import { doc, getDoc, getDocs, collection, query, where } from "https://www.gstatic.com/firebasejs/10.12.2/firebase-firestore.js";

// ─── Data atual ───────────────────────────────────────────
function atualizarData() {
    const data = new Date();

    const dias = ["Domingo", "Segunda-feira", "Terça-feira", "Quarta-feira", "Quinta-feira", "Sexta-feira", "Sábado"];
    const meses = ["janeiro", "fevereiro", "março", "abril", "maio", "junho",
                   "julho", "agosto", "setembro", "outubro", "novembro", "dezembro"];

    const diaSemana = dias[data.getDay()];
    const dia = data.getDate();
    const mes = meses[data.getMonth()];
    const ano = data.getFullYear();

    document.getElementById("data-atual").innerText = `${diaSemana}, ${dia} de ${mes} de ${ano}`;
}

atualizarData();

// ─── Gráfico de rosca ─────────────────────────────────────
new Chart(document.getElementById('donutChart'), {
    type: 'doughnut',
    data: {
        datasets: [{
            data: [49, 32, 28, 19],
            backgroundColor: ['#7F77DD', '#1D9E75', '#D4537E', '#EF9F27'],
            borderWidth: 0
        }]
    },
    options: {
        cutout: '72%',
        plugins: { legend: { display: false }, tooltip: { enabled: false } }
    }
});

// ─── Proteção de rota + dados do usuário ──────────────────
onAuthStateChanged(auth, async (user) => {
    if (!user) {
        window.location.href = "../login/login.html";
        return;
    }

    try {
        const userSnap = await getDoc(doc(db, "users", user.uid));
        const usuario = userSnap.data();

        const orgSnap = await getDoc(doc(db, "organizations", usuario.orgId));
        const empresa = orgSnap.exists() ? orgSnap.data() : { orgName: "Organização não encontrada" };

        document.querySelector(".top-left h1").textContent = empresa.orgName;
        document.querySelector(".name-admin").textContent = usuario.firstName;
        document.querySelector(".class-admin").textContent = usuario.occupation;

        await loadLocations(usuario.orgId);

    } catch (error) {
        console.error("Erro ao carregar dados:", error);
    }

    document.querySelector(".btn-primary").addEventListener("click", () => {
        window.location.href = "../form-locations/form-locations.html";
    });
});
async function loadLocations(orgId) {
  const locationsRef = query(
    collection(db, "locations"),
    where("orgId", "==", orgId)
  );
  const snapshot = await getDocs(locationsRef);

  const container = document.querySelector(".body");
  container.innerHTML = "";

  const countPlaces = snapshot.size;
  document.getElementById("total-places-number").textContent = `${countPlaces}`;
  document.getElementById("total-places-text").textContent = countPlaces === 1 ? "Local Cadastrado" : "Locais Cadastrados";

  snapshot.forEach((doc) => {
    const placeData = doc.data();
    const iconLetters = getInitials(placeData.name);
    const placeName = placeData.name;
    const placeType = placeData.tags;
    const placeSignIn = placeData.createdAt.toDate();
    const placeSignInDate = placeSignIn.toLocaleDateString("pt-BR");
    const placeCard = `
    <div class="body">
        <div class="table-body">
            <div class="box-name">
                <div class="table-org">
                    <div class="icon-name">${iconLetters}</div>
                        <div class="informations">
                            <p class="name-org">${placeName}</p>
                            <p class="email-org"></p>
                        </div>
                    </div>
                </div>
                <div class="type-org priv">
                    <div class="icon-type priv"><i class="fa-solid fa-building"></i></div>
                    <p>${placeType}</p>
                </div>
                <div class="mood-org active">
                    <i class="fa-solid fa-circle"></i>
                    <p>Publicado</p>
                </div>
                <div class="date-org">
                    <p>${placeSignInDate}</p>
                </div>
                <div class="actions-org aprove">
                    <button>Aprovar</button>
                </div>
            </div>
        </div>
    </div>
    `;
    container.innerHTML += placeCard;
  });
}
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