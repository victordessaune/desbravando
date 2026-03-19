// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app";
import { getAnalytics } from "firebase/analytics";
// TODO: Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries

// Your web app's Firebase configuration
// For Firebase JS SDK v7.20.0 and later, measurementId is optional
const firebaseConfig = {
  apiKey: "AIzaSyDBfUUUBmZZNk0o4uNrIc2OWLJhcXb35Hg",
  authDomain: "desbravando-3488c.firebaseapp.com",
  projectId: "desbravando-3488c",
  storageBucket: "desbravando-3488c.firebasestorage.app",
  messagingSenderId: "424189045363",
  appId: "1:424189045363:web:a98c1d37e74ecae55dd806",
  measurementId: "G-VSLHHBW2SZ"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const analytics = getAnalytics(app);
