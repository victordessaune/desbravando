import { auth, db } from "../js/api/firebase.js";
import { onAuthStateChanged } from "https://www.gstatic.com/firebasejs/10.12.2/firebase-auth.js";
import { doc, getDoc } from "https://www.gstatic.com/firebasejs/10.12.2/firebase-firestore.js";

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

    } catch (error) {
        console.error("Erro ao carregar dados:", error);
    }

    document.querySelector(".btn-primary").addEventListener("click", () => {
        window.location.href = "../form-locations/form-locations.html";
    });
});