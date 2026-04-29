import { db, auth } from "../js/api/firebase.js";
import { doc, getDoc } from "https://www.gstatic.com/firebasejs/10.12.2/firebase-firestore.js";
import { onAuthStateChanged } from "https://www.gstatic.com/firebasejs/10.12.2/firebase-auth.js";
import { updateDoc } from "https://www.gstatic.com/firebasejs/10.12.2/firebase-firestore.js";


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

    document.getElementById("email-resp").textContent = data.email;
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
    const placeRef = doc(db, "locals", orgId);
    const placeSnap = await getDoc(placeRef);

    if (placeSnap.exists()){
      const placeData = placeSnap.data();
      /*const userLetters = getInitials(orgData.orgName);*/
      /*const createdDate = orgData.createdAt.toDate();*/
      /*const signinDate = createdDate.toLocaleDateString("pt-BR");*/

      console.log("Nome:", placeData.nome);
      
      /*document.getElementById("org-name").textContent = orgData.orgName;
      document.getElementById("org-type").textContent = orgData.orgType;
      document.getElementById("signin-date").textContent = signinDate;
      document.getElementById("org-city").textContent = orgData.city;      
      document.getElementById("org-email").textContent = orgData.orgEmail;
      document.getElementById("website").textContent = orgData.website;
      document.getElementById("cnpj").textContent = orgData.cnpj;
      document.getElementById("address").textContent = address;
      document.getElementById("user-icon").textContent = userLetters;*/

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

/*Código para ativar o modo de edição das informações*/
function edit(){
  /*Email*/
  document.getElementById("org-email").style.display = "none";
  document.getElementById("org-email-input").style.display = "block";
  document.getElementById("org-email-input").value =
    document.getElementById("org-email").innerText;

  /*Website*/
  document.getElementById("website").style.display = "none";
  document.getElementById("website-input").style.display = "block";
  document.getElementById("website-input").value =
    document.getElementById("website").innerText;

  /*cnpj*/
  document.getElementById("cnpj").style.display = "none";
  document.getElementById("cnpj-input").style.display = "block";
  document.getElementById("cnpj-input").value =
    document.getElementById("cnpj").innerText;

  /*Nome da Responsável
  document.getElementById("person-name").style.display = "none";
  document.getElementById("person-name-input").style.display = "block";
  document.getElementById("person-name-input").value =
    document.getElementById("person-name").innerText;

  /*Ocupação
  document.getElementById("occupation").style.display = "none";
  document.getElementById("occupation-input").style.display = "block";
  document.getElementById("occupation-input").value =
    document.getElementById("occupation").innerText;

  /*Email do Responsável
  document.getElementById("email-resp").style.display = "none";
  document.getElementById("email-resp-input").style.display = "block";
  document.getElementById("email-resp-input").value =
    document.getElementById("email-resp").innerText;*/

  document.getElementById("btn-save").style.display = "inline-block";
  document.getElementById("btn-cancel").style.display = "inline-block"; 
}

function cancel(){
  document.getElementById("org-email").style.display = "block";
  document.getElementById("org-email-input").style.display = "none";
  document.getElementById("website").style.display = "block";
  document.getElementById("website-input").style.display = "none";
  document.getElementById("cnpj").style.display = "block";
  document.getElementById("cnpj-input").style.display = "none";
  /*document.getElementById("person-name").style.display = "block";
  document.getElementById("person-name-input").style.display = "none";
  document.getElementById("occupation").style.display = "block";
  document.getElementById("occupation-input").style.display = "none";
  document.getElementById("email-resp").style.display = "block";
  document.getElementById("email-resp-input").style.display = "none";*/


  document.getElementById("btn-save").style.display = "none";
  document.getElementById("btn-cancel").style.display = "none";

}

async function save(){
  const user = auth.currentUser;

  if(!user) {
    return;
  }

  const newOrgEmail = document.getElementById("org-email-input").value;
  const newWebsite = document.getElementById("website-input").value;
  const newCnpj = document.getElementById("cnpj-input").value;
  /*const newPersonName= document.getElementById("person-name-input").value;
  const newOccupation = document.getElementById("occupation-input").value;
  const newPersonEmail = document.getElementById("email-resp-input").value;*/

  try{
    const userRef = doc(db, "users", user.uid);
    const userSnap = await getDoc(userRef);
    /*await updateDoc(userRef, {
      occupation: newOccupation,
      email: newPersonEmail
    });*/
    const orgId = userSnap.data().orgId;

    const orgRef = doc(db, "organizations", orgId);

    await updateDoc(orgRef, {
      orgEmail: newOrgEmail,
      website: newWebsite,
      cnpj: newCnpj
      
      
    });
    document.getElementById("org-email").innerText = newOrgEmail;
    document.getElementById("website").innerText = newWebsite;
    document.getElementById("cnpj").innerText = newCnpj;

    /*document.getElementById("occupation").innerText = newOccupation;
    document.getElementById("email-resp").innerText = newPersonEmail;*/


    cancel();
    alert("Atualizado");
  } catch(e){
    console.error(e);
    alert("erro");
  }

}
window.edit = edit;
window.save = save;
window.cancel = cancel;








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