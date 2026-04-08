import { db, auth } from "./SEU_ARQUIVO_FIREBASE.js";
import { doc, getDoc } from "https://www.gstatic.com/firebasejs/10.12.2/firebase-firestore.js";
import { onAuthStateChanged } from "https://www.gstatic.com/firebasejs/10.12.2/firebase-auth.js";

onAuthStateChanged(auth, async (user) => {
  if (user) {
    
    const userId = user.uid;

    const docRef = doc(db, "usuarios", userId);
    const docSnap = await getDoc(docRef);

    if (docSnap.exists()) {
  const data = docSnap.data();

  document.getElementById("nome").textContent = data.nome;
  document.getElementById("org-email").textContent = data.email;
  document.getElementById("cnpj").textContent = data.cnpj;
  document.getElementById("website").textContent = data.website;

  const endereco = data.endereco || {};

  const enderecoFormatado = `${endereco.street || ""}, ${endereco.number || ""} - 
  ${endereco.neighborhood || ""}, ${endereco.city || ""} - ${endereco.uf || ""}`;

  document.getElementById("endereco").textContent = enderecoFormatado;

    } else {
      console.log("Usuário não encontrado");
    }

  } else {
    console.log("Usuário não está logado");
  }
});