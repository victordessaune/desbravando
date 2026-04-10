import { auth, db } from "../js/api/firebase.js";
import { onAuthStateChanged } from "https://www.gstatic.com/firebasejs/10.12.2/firebase-auth.js";
import { collection, query, where, getDocs, doc, getDoc } from "https://www.gstatic.com/firebasejs/10.12.2/firebase-firestore.js";

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
        let usuarioNome = sessionStorage.getItem("usuarioNome");
        let empresaNome = sessionStorage.getItem("empresaNome");
        let usuarioCargo = sessionStorage.getItem("usuarioCargo");

        if (!usuarioNome || !empresaNome) {
            // Busca pelo campo uid dentro da collection
            const q = query(collection(db, "users"), where("uid", "==", user.uid));
            const querySnap = await getDocs(q);

            if (querySnap.empty) {
                window.location.href = "../login/login.html";
                return;
            }

            const usuario = querySnap.docs[0].data();

            const empresaSnap = await getDoc(doc(db, "organizations", usuario.orgId));
            const empresa = empresaSnap.exists() ? empresaSnap.data() : { orgName: "Organização" };

            usuarioNome = `${usuario.firstName} ${usuario.lastName}`;
            empresaNome = empresa.orgName;

            sessionStorage.setItem("usuarioNome", usuarioNome);
            sessionStorage.setItem("empresaNome", empresaNome);
        }

        document.querySelector(".top-left h1").textContent = empresaNome;
        document.querySelector(".name-admin").textContent = usuarioNome;
        document.querySelector(".class-admin").textContent = usuarioCargo;

    } catch (error) {
        console.error("Erro ao carregar dados:", error);
    }
});