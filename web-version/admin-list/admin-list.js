import { db, auth, app } from "../js/api/firebase.js";
import {
  doc,
  getDoc,
  getDocs,
  collection,
  query,
  where,
  setDoc,
  deleteDoc,
} from "https://www.gstatic.com/firebasejs/10.12.2/firebase-firestore.js";
import {
  onAuthStateChanged,
  createUserWithEmailAndPassword,
  signOut,
  getAuth,
} from "https://www.gstatic.com/firebasejs/10.12.2/firebase-auth.js";
import {
  initializeApp,
} from "https://www.gstatic.com/firebasejs/10.12.2/firebase-app.js";

// ── Estado global ──
let currentOrgId = null;
let currentUserUid = null;

// ── POPUP ──
window.openPopup  = () => {
  clearPopupFields();
  document.getElementById("popup-responsavel").classList.add("open");
};
window.closePopup = () =>
  document.getElementById("popup-responsavel").classList.remove("open");
window.closeOnBg  = (e) => {
  if (e.target.id === "popup-responsavel") closePopup();
};

document.addEventListener("keydown", (e) => {
  if (e.key === "Escape") closePopup();
});

window.toggleVis = (id, btn) => {
  const inp = document.getElementById(id);
  const show = inp.type === "password";
  inp.type = show ? "text" : "password";
  btn.innerHTML = show
    ? '<i class="fa-solid fa-eye-slash"></i>'
    : '<i class="fa-solid fa-eye"></i>';
};

// ── Máscara CPF ──
document.getElementById("cpf")?.addEventListener("input", function () {
  let v = this.value.replace(/\D/g, "").slice(0, 11);
  v = v
    .replace(/(\d{3})(\d)/, "$1.$2")
    .replace(/(\d{3}\.\d{3})(\d)/, "$1.$2")
    .replace(/(\d{3}\.\d{3}\.\d{3})(\d)/, "$1-$2");
  this.value = v;
});

// ── Limpa erros ao digitar ──
["nome", "sobrenome", "cpf", "email", "senha", "confirmar"].forEach((id) => {
  document.getElementById(id)?.addEventListener("input", () => {
    document.getElementById("err-" + id)?.classList.remove("show");
    document.getElementById(id)?.classList.remove("err");
  });
});

function clearPopupFields() {
  ["nome", "sobrenome", "cpf", "ocupacao", "email", "senha", "confirmar"].forEach(
    (id) => {
      const el = document.getElementById(id);
      if (el) el.value = "";
    }
  );
  ["nome", "sobrenome", "cpf", "email", "senha", "confirmar"].forEach((id) => {
    document.getElementById("err-" + id)?.classList.remove("show");
    document.getElementById(id)?.classList.remove("err");
  });
}

// ── Validação e salvamento ──
window.validarPopup = async () => {
  let ok = true;

  ["nome", "sobrenome", "cpf", "email", "senha", "confirmar"].forEach((id) => {
    document.getElementById("err-" + id)?.classList.remove("show");
    document.getElementById(id)?.classList.remove("err");
  });

  const req = ["nome", "sobrenome", "cpf", "email"];
  req.forEach((id) => {
    if (!document.getElementById(id).value.trim()) {
      showErr(id, "Campo obrigatório");
      ok = false;
    }
  });

  const s = document.getElementById("senha").value;
  const c = document.getElementById("confirmar").value;
  if (!s)               { showErr("senha", "Campo obrigatório"); ok = false; }
  else if (s.length < 6){ showErr("senha", "Mínimo 6 caracteres"); ok = false; }
  if (!c)               { showErr("confirmar", "Campo obrigatório"); ok = false; }
  else if (s && c && s !== c) { showErr("confirmar", "As senhas não coincidem"); ok = false; }

  if (!ok) return;

  // ── Salvar no Firebase ──
  const saveBtn = document.querySelector(".btn-primary");
  saveBtn.disabled = true;
  saveBtn.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> Salvando...';

  try {
    const firstName  = document.getElementById("nome").value.trim();
    const lastName   = document.getElementById("sobrenome").value.trim();
    const cpf        = document.getElementById("cpf").value.trim();
    const email      = document.getElementById("email").value.trim();
    const occupation = document.getElementById("ocupacao").value.trim();
    const password   = document.getElementById("senha").value;

    // 1. Cria um app Firebase secundário temporário para não alterar a sessão atual
    const secondaryApp  = initializeApp(app.options, "secondary-" + Date.now());
    const secondaryAuth = getAuth(secondaryApp);
    const newCred = await createUserWithEmailAndPassword(secondaryAuth, email, password);
    const newUid  = newCred.user.uid;
    await secondaryAuth.signOut();

    // 2. Salva os dados no Firestore herdando orgId do admin logado
    await setDoc(doc(db, "users", newUid), {
      uid:        newUid,
      firstName,
      lastName,
      cpf,
      email,
      occupation,
      orgId:      currentOrgId,
      role:       "admin",
      createdAt:  new Date(),
    });

    closePopup();
    await loadAdmins(currentOrgId);

  } catch (error) {
    console.error("Erro ao criar admin:", error);

    if (error.code === "auth/email-already-in-use") {
      showErr("email", "Este e-mail já está em uso");
    } else if (error.code === "auth/invalid-email") {
      showErr("email", "E-mail inválido");
    } else {
      showErr("email", "Erro ao criar conta. Tente novamente");
    }
  } finally {
    saveBtn.disabled = false;
    saveBtn.innerHTML = '<i class="fa-solid fa-check"></i> Salvar';
  }
};

function showErr(id, msg) {
  const errEl = document.getElementById("err-" + id);
  if (errEl) {
    errEl.textContent = msg;
    errEl.classList.add("show");
  }
  document.getElementById(id)?.classList.add("err");
}

// ── Gera iniciais ──
function getInitials(name) {
  const ignore = ["de", "da", "do", "dos", "das"];
  const words  = name.split(" ").filter((p) => p.trim() !== "");
  if (words.length === 1) return words[0].substring(0, 2).toUpperCase();
  return words
    .filter((p) => !ignore.includes(p.toLowerCase()))
    .map((p)    => p[0].toUpperCase())
    .slice(0, 2)
    .join("");
}

// ── Formata data ──
function formatDate(val) {
  if (!val) return "—";
  if (val.toDate) return val.toDate().toLocaleDateString("pt-BR");
  if (val instanceof Date) return val.toLocaleDateString("pt-BR");
  return "—";
}

// ── Renderiza tabela ──
async function loadAdmins(orgId) {
  const tableBody = document.getElementById("admins-table-body");
  if (!tableBody) return;

  tableBody.innerHTML = `
    <div class="table-body-admin" style="text-align:center;padding:30px;color:var(--color-gray)">
      <i class="fa-solid fa-spinner fa-spin"></i> Carregando...
    </div>`;

  try {
    const q = query(
      collection(db, "users"),
      where("orgId", "==", orgId)
    );
    const snap = await getDocs(q);

    const admins = [];
    snap.forEach((d) => admins.push({ id: d.id, ...d.data() }));

    // Atualiza contador
    const counter = document.getElementById("admin-count");
    if (counter) counter.textContent = admins.length;

    tableBody.innerHTML = "";

    if (admins.length === 0) {
      tableBody.innerHTML = `
        <div class="table-body-admin" style="text-align:center;padding:30px;color:var(--color-gray)">
          Nenhum administrador encontrado.
        </div>`;
      return;
    }

    admins.forEach((admin) => {
      const fullName   = `${admin.firstName || ""} ${admin.lastName || ""}`.trim();
      const initials   = getInitials(fullName || "?");
      const dateStr    = formatDate(admin.createdAt);
      const isActive   = admin.disabled ? "Inativo" : "Ativo";
      const canRemove  = admins.length > 1;
      const isMe       = admin.id === currentUserUid;

      const row = document.createElement("div");
      row.className = "table-body-admin";
      row.dataset.uid = admin.id;

      row.innerHTML = `
        <div class="table-body-content">
          <div class="column-admin-info">
            <div class="icon-user">${initials}</div>
            <div class="informations">
              <p class="admin-name">${fullName || "—"}${isMe ? ' <span style="font-size:11px;color:var(--color-blue)">(você)</span>' : ""}</p>
              <p class="admin-email">${admin.email || "—"}</p>
            </div>
          </div>
          <div class="admin-occupation"><p>${admin.occupation || "—"}</p></div>
          <div class="admin-status"><p>${isActive}</p></div>
          <div class="admin-date"><p>${dateStr}</p></div>
          <div class="admin-action">
            <div class="buttons-action">
              <button class="btn-edit" onclick="editAdmin('${admin.id}')">Editar</button>
              ${canRemove ? `<button class="btn-remove" onclick="removeAdmin('${admin.id}', '${fullName}')">Remover</button>` : ""}
            </div>
          </div>
        </div>`;

      tableBody.appendChild(row);
    });

  } catch (err) {
    console.error("Erro ao carregar admins:", err);
    tableBody.innerHTML = `
      <div class="table-body-admin" style="text-align:center;padding:30px;color:#ef4444">
        Erro ao carregar administradores.
      </div>`;
  }
}

// ── Remover admin ──
window.removeAdmin = async (uid, name) => {
  const confirmMsg = `Tem certeza que deseja remover "${name}"? Esta ação não pode ser desfeita.`;
  if (!confirm(confirmMsg)) return;

  try {
    await deleteDoc(doc(db, "users", uid));

    // Se removeu a própria conta, faz logout e redireciona para login
    if (uid === currentUserUid) {
      await signOut(auth);
      window.location.href = "../login/login.html";
      return;
    }

    await loadAdmins(currentOrgId);
  } catch (err) {
    console.error("Erro ao remover admin:", err);
    alert("Erro ao remover administrador. Tente novamente.");
  }
};

// ── Editar admin (placeholder — implemente conforme sua necessidade) ──
window.editAdmin = (uid) => {
  console.log("Editar admin:", uid);
  alert("Funcionalidade de edição em desenvolvimento.");
};

// ── Busca ──
document.querySelector(".search-input")?.addEventListener("input", function () {
  const term = this.value.toLowerCase();
  document.querySelectorAll("#admins-table-body .table-body-admin").forEach((row) => {
    const text = row.textContent.toLowerCase();
    row.style.display = text.includes(term) ? "" : "none";
  });
});

// ── Auth listener ──
onAuthStateChanged(auth, async (user) => {
  if (user) {
    currentUserUid = user.uid;

    const userDoc = await getDoc(doc(db, "users", user.uid));
    if (userDoc.exists()) {
      const data = userDoc.data();
      currentOrgId = data.orgId;
      await loadAdmins(currentOrgId);
    }
  } else {
    window.location.href = "../login/login.html";
  }
});