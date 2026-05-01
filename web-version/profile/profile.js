import { db, auth } from "../js/api/firebase.js";
import { collection, doc, getDoc, getDocs, query, where } from "https://www.gstatic.com/firebasejs/10.12.2/firebase-firestore.js";
import { onAuthStateChanged } from "https://www.gstatic.com/firebasejs/10.12.2/firebase-auth.js";
import { updateDoc } from "https://www.gstatic.com/firebasejs/10.12.2/firebase-firestore.js";

// Variável global para guardar os dados do endereço
let orgAddress = {};

onAuthStateChanged(auth, async (user) => {
  if (user) {
    loadData(user.uid);
  } else {
    console.log("Usuário não logado.")
  }
});

async function loadData(uid) {
  console.log("UID logado:", uid);

  const docRef = doc(db, "users", uid);
  const docSnap = await getDoc(docRef);

  if (docSnap.exists()) {
    const data = docSnap.data();
    const personName = `${data.firstName || ""} ${data.lastName || ""}`;
    const userLetters = getInitials(personName);

    document.getElementById("email-resp").textContent = data.email;
    document.getElementById("occupation").textContent = data.occupation;
    document.getElementById("person-name").textContent = personName;
    document.getElementById("person-user").textContent = userLetters;

    const orgId = data.orgId;
    const orgRef = doc(db, "organizations", orgId);
    const orgSnap = await getDoc(orgRef);

    if (orgSnap.exists()) {
      const orgData = orgSnap.data();

      // Guarda os campos do endereço na variável global
      orgAddress = {
        street: orgData.street || "",
        number: orgData.number || "",
        neighborhood: orgData.neighborhood || "",
        city: orgData.city || "",
        uf: orgData.uf || "",
        cep: orgData.cep || "",
      };

      const address = `${orgAddress.street}, ${orgAddress.number}, ${orgAddress.neighborhood}, ${orgAddress.city} - ${orgAddress.uf}, CEP ${orgAddress.cep}`;
      const orgLetters = getInitials(orgData.orgName);
      const createdDate = orgData.createdAt.toDate();
      const signinDate = createdDate.toLocaleDateString("pt-BR");

      document.getElementById("org-name").textContent = orgData.orgName;
      document.getElementById("org-type").textContent = orgData.orgType;
      document.getElementById("signin-date").textContent = signinDate;
      document.getElementById("org-city").textContent = orgData.city;
      document.getElementById("org-email").textContent = orgData.orgEmail;
      document.getElementById("website").textContent = orgData.website;
      document.getElementById("cnpj").textContent = orgData.cnpj;
      document.getElementById("address").textContent = address;
      document.getElementById("user-icon").textContent = orgLetters;
      document.getElementById("description-text").textContent = orgData.bio;

      await loadLocations(orgId);
    }

  } else {
    console.log("Documento não encontrado");
  }
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

async function loadLocations(orgId) {
  const locationsRef = query(
    collection(db, "locations"),
    where("orgId", "==", orgId)
  );
  const snapshot = await getDocs(locationsRef);

  const container = document.querySelector(".places-area");
  container.innerHTML = "";

  const countPlaces = snapshot.size;
  document.getElementById("value-total-places").textContent = `${countPlaces}`;
  document.getElementById("card-text").textContent = countPlaces === 1 ? "Local Cadastrado" : "Locais Cadastrados";

  snapshot.forEach((doc) => {
    const placeData = doc.data();
    const placeName = placeData.name;
    const placeAdress = `${placeData.neighborhood || ""}, ${placeData.city || ""}`;
    const placeImageUrl = placeData.images;
    const placeCard = `
      <div class="place-card">
        <div class="banner-place" style="background-image: url('${placeImageUrl}'); background-size: cover; background-position: center;"></div>
        <div class="place-name">${placeName}</div>
        <div class="place-location"><i class="fa-solid fa-location-dot"></i>${placeAdress}</div>
      </div>
    `;
    container.innerHTML += placeCard;
  });
}

/* Código para ativar o modo de edição */
function edit() {
  // Email
  document.getElementById("org-email").style.display = "none";
  document.getElementById("org-email-input").style.display = "block";
  document.getElementById("org-email-input").value = document.getElementById("org-email").innerText;

  // Website
  document.getElementById("website").style.display = "none";
  document.getElementById("website-input").style.display = "block";
  document.getElementById("website-input").value = document.getElementById("website").innerText;

  // CNPJ
  document.getElementById("cnpj").style.display = "none";
  document.getElementById("cnpj-input").style.display = "block";
  document.getElementById("cnpj-input").value = document.getElementById("cnpj").innerText;

  // Endereço — esconde o texto e mostra os inputs com os valores já preenchidos
  document.getElementById("address").style.display = "none";
  document.getElementById("address-inputs").style.display = "grid";
  document.getElementById("street-input").value = orgAddress.street;
  document.getElementById("number-input").value = orgAddress.number;
  document.getElementById("neighborhood-input").value = orgAddress.neighborhood;
  document.getElementById("city-input").value = orgAddress.city;
  document.getElementById("uf-input").value = orgAddress.uf;
  document.getElementById("cep-input").value = orgAddress.cep;

  document.getElementById("btn-save").style.display = "inline-block";
  document.getElementById("btn-cancel").style.display = "inline-block";
}

function cancel() {
  document.getElementById("org-email").style.display = "block";
  document.getElementById("org-email-input").style.display = "none";
  document.getElementById("website").style.display = "block";
  document.getElementById("website-input").style.display = "none";
  document.getElementById("cnpj").style.display = "block";
  document.getElementById("cnpj-input").style.display = "none";

  document.getElementById("address").style.display = "block";
  document.getElementById("address-inputs").style.display = "none";

  document.getElementById("btn-save").style.display = "none";
  document.getElementById("btn-cancel").style.display = "none";
}

async function save() {
  const user = auth.currentUser;
  if (!user) return;

  const newOrgEmail = document.getElementById("org-email-input").value;
  const newWebsite = document.getElementById("website-input").value;
  const newCnpj = document.getElementById("cnpj-input").value;
  const newStreet = document.getElementById("street-input").value;
  const newNumber = document.getElementById("number-input").value;
  const newNeighborhood = document.getElementById("neighborhood-input").value;
  const newCity = document.getElementById("city-input").value;
  const newUf = document.getElementById("uf-input").value;
  const newCep = document.getElementById("cep-input").value;

  try {
    const userRef = doc(db, "users", user.uid);
    const userSnap = await getDoc(userRef);
    const orgId = userSnap.data().orgId;

    const orgRef = doc(db, "organizations", orgId);
    await updateDoc(orgRef, {
      orgEmail: newOrgEmail,
      website: newWebsite,
      cnpj: newCnpj,
      street: newStreet,
      number: newNumber,
      neighborhood: newNeighborhood,
      city: newCity,
      uf: newUf,
      cep: newCep,
    });

    // Atualiza os textos exibidos
    document.getElementById("org-email").innerText = newOrgEmail;
    document.getElementById("website").innerText = newWebsite;
    document.getElementById("cnpj").innerText = newCnpj;

    const newAddress = `${newStreet}, ${newNumber}, ${newNeighborhood}, ${newCity} - ${newUf}, CEP ${newCep}`;
    document.getElementById("address").innerText = newAddress;
    document.getElementById("org-city").textContent = newCity;

    // Atualiza a variável global para a próxima edição
    orgAddress = { street: newStreet, number: newNumber, neighborhood: newNeighborhood, city: newCity, uf: newUf, cep: newCep };

    cancel();
  } catch (e) {
    console.error("Erro ao salvar:", e);
    alert("Erro ao salvar: " + e.message);
  }
}

window.edit = edit;
window.save = save;
window.cancel = cancel;

/* JS para editar a descrição */
function editDescription() {
  document.getElementById("description-text").style.display = "none";
  document.getElementById("input-description-text").style.display = "block";
  document.getElementById("input-description-text").value =
    document.getElementById("description-text").innerText;

  document.getElementById("btn-save-description").style.display = "inline-block";
  document.getElementById("btn-cancel-description").style.display = "inline-block";
}

function cancelDescription() {
  document.getElementById("description-text").style.display = "block";
  document.getElementById("input-description-text").style.display = "none";

  document.getElementById("btn-save-description").style.display = "none";
  document.getElementById("btn-cancel-description").style.display = "none";
}

async function saveDescription() {
  const user = auth.currentUser;
  if (!user) return;

  const newDescription = document.getElementById("input-description-text").value;
  try {
    const userRef = doc(db, "users", user.uid);
    const userSnap = await getDoc(userRef);
    const orgId = userSnap.data().orgId;
    const orgRef = doc(db, "organizations", orgId);

    await updateDoc(orgRef, { bio: newDescription });

    document.getElementById("description-text").innerText = newDescription;
    cancelDescription();
  } catch (e) {
    console.error("Erro ao salvar descrição:", e);
    alert("Erro ao salvar descrição: " + e.message);
  }
}

window.editDescription = editDescription;
window.saveDescription = saveDescription;
window.cancelDescription = cancelDescription;