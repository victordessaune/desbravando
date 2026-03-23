import { initializeApp } from "https://www.gstatic.com/firebasejs/10.12.2/firebase-app.js";

import { getAuth } from "https://www.gstatic.com/firebasejs/10.12.2/firebase-auth.js";

import { getFirestore } from "https://www.gstatic.com/firebasejs/10.12.2/firebase-firestore.js";

export { addDoc, collection } from "https://www.gstatic.com/firebasejs/10.12.2/firebase-firestore.js";

export { createUserWithEmailAndPassword, signInWithEmailAndPassword, setPersistence, browserLocalPersistence, browserSessionPersistence } from "https://www.gstatic.com/firebasejs/10.12.2/firebase-auth.js";


const firebaseConfig = {
  apiKey: "AIzaSyDBfUUUBmZZNk0o4uNrIc2OWLJhcXb35Hg",
  authDomain: "desbravando-3488c.firebaseapp.com",
  projectId: "desbravando-3488c",
  storageBucket: "desbravando-3488c.firebasestorage.app",
  messagingSenderId: "424189045363",
  appId: "1:424189045363:web:d0a222d33e3b032e5dd806"
};

const app = initializeApp(firebaseConfig);

export const auth = getAuth(app);
export const db = getFirestore(app);