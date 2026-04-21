import { db, auth } from "../js/api/firebase.js";
import { doc, getDoc } from "https://www.gstatic.com/firebasejs/10.12.2/firebase-firestore.js";
import { onAuthStateChanged } from "https://www.gstatic.com/firebasejs/10.12.2/firebase-auth.js";

onAuthStateChanged(auth, async (user) => {
  if (user) {
    const userId = user.uid;
    loadData(userId);
  } else{
    console.log("Usuário não logado.")
  }
});

async function loadData(uid){
  console.log("UID logado:", uid);
  
  const docRef = doc(db,"users", uid);
  const docSnap = await getDoc(docRef);

  if (docSnap.exists()){
    const data = docSnap.data();
    console.log("Nome:", data.firstName);
    const personName = `${data.firstName || ""} ${data.lastName || ""}`;
    const userLetters = getInitials(personName);

    document.getElementById("email").textContent = data.email;
    document.getElementById("occupation").textContent = data.occupation;
    document.getElementById("person-name").textContent = personName;
    document.getElementById("person-user").textContent = userLetters;

    const orgId = data.orgId;

    const orgRef = doc(db, "organizations", orgId);
    const orgSnap = await getDoc(orgRef);

    if (orgSnap.exists()){
      const orgData = orgSnap.data();
      const address = `${orgData.street || ""}, ${orgData.number || ""}, ${orgData.neighborhood || ""}, ${orgData.city || ""} - ${orgData.uf || ""}, CEP ${orgData.cep || ""}`;
      const userLetters = getInitials(orgData.orgName);
      const createdDate = orgData.createdAt.toDate();
      const signinDate = createdDate.toLocaleDateString("pt-BR");

      

      console.log("Organização:", orgData.orgName);
      
      document.getElementById("org-name").textContent = orgData.orgName;
      document.getElementById("org-type").textContent = orgData.orgType;
      document.getElementById("signin-date").textContent = signinDate;
      document.getElementById("org-city").textContent = orgData.city;      
      document.getElementById("org-email").textContent = orgData.orgEmail;
      document.getElementById("website").textContent = orgData.website;
      document.getElementById("cnpj").textContent = orgData.cnpj;
      document.getElementById("address").textContent = address;
      document.getElementById("user-icon").textContent = userLetters;

    }
  } else{
    console.log("Documento não encontrado");
  }

  function getInitials(name) {
  const ignore = ["de", "da", "do", "dos", "das"];

  const words = name
    .split(" ")
    .filter(p => p.trim() !== "");

  if (words.length === 1) {
    return words[0].substring(0, 2).toUpperCase();
  }

  return words
    .filter(p => !ignore.includes(p.toLowerCase()))
    .map(p => p[0].toUpperCase())
    .slice(0, 2)
    .join("");
}


}






/*onAuthStateChanged(auth, async (user) => {
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
});*/