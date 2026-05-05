import { auth, db } from "./api/firebase.js";
import { onAuthStateChanged } from "https://www.gstatic.com/firebasejs/10.12.2/firebase-auth.js";
import { doc, getDoc } from "https://www.gstatic.com/firebasejs/10.12.2/firebase-firestore.js";

class AppSidebar extends HTMLElement {
  static get observedAttributes() {
    return ['page'];
  }

  connectedCallback() {
    this.render();
    this._loadUser();
  }

  attributeChangedCallback() {
    this.render();
  }

  get currentPage() {
    return this.getAttribute('page') || 'dashboard';
  }

  render() {
    const navItems = [
      {
        section: 'Principal',
        items: [
          { id: 'dashboard',       icon: 'fa-chart-column', label: 'Dashboard',       href: '../dashboard/dashboard.html' },
          { id: 'organizacao',     icon: 'fa-building',     label: 'Organização',     href: '../profile/profile.html' },
          { id: 'administradores', icon: 'fa-user',         label: 'Administradores', href: '../admin-list/admin-list.html' },
          { id: 'locais',          icon: 'fa-map-pin',      label: 'Locais',          href: '../places-list/places-list.html' },
        ],
      },
      {
        section: 'Sistema',
        items: [
          { id: 'configuracoes', icon: 'fa-gear', label: 'Configurações', href: '#' },
          { id: 'seguranca',     icon: 'fa-lock', label: 'Segurança',     href: '#' },
        ],
      },
    ];

    const sectionsHTML = navItems.map(({ section, items }) => {
      const itemsHTML = items.map(({ id, icon, label, href }) => {
        const isActive = id === this.currentPage;
        const cls = isActive ? 'principal-icon-page active' : 'principal-icons';
        return `
          <div class="${cls}">
            <a href="${href}" style="text-decoration:none; color:#FFFFFF;">
              <p><i class="fa-solid ${icon}"></i> ${label}</p>
            </a>
          </div>`;
      }).join('');

      return `
        <div class="principal"><p>${section}</p></div>
        ${itemsHTML}`;
    }).join('');

    this.innerHTML = `
      <aside class="sidebar">
        <div class="sidebar-logo">
          <div class="first-text">
            <div class="logo-text">Desbravando</div>
            <span class="logo-text-two">Admin Painel</span>
          </div>
        </div>

        <div class="sidebar-rest">
          ${sectionsHTML}
        </div>

        <div class="admin">
          <div class="box-admin">
            <div class="info-admin">
              <div class="icon-admin">
                <span class="text-icon-admin" id="sidebar-initials">--</span>
              </div>
              <div class="text-admin-info">
                <div class="name-admin" id="sidebar-name">Carregando...</div>
                <div class="class-admin" id="sidebar-occupation">—</div>
              </div>
            </div>
          </div>
        </div>
      </aside>`;
  }

  async _loadUser() {
    onAuthStateChanged(auth, async (user) => {
      if (!user) {
        window.location.href = "../login/login.html";
        return;
      }

      try {
        const userSnap = await getDoc(doc(db, "users", user.uid));
        const usuario = userSnap.data();

        // Atualiza sidebar
        const firstName = usuario.firstName || "";
        const lastName  = usuario.lastName  || "";
        const fullName  = [firstName, lastName].filter(Boolean).join(" ") || "Admin";
        const initials  = [firstName[0], lastName[0]].filter(Boolean).join("").toUpperCase() || "AD";

        const elName       = this.querySelector("#sidebar-name");
        const elOccupation = this.querySelector("#sidebar-occupation");
        const elInitials   = this.querySelector("#sidebar-initials");

        if (elName)       elName.textContent       = fullName;
        if (elOccupation) elOccupation.textContent = usuario.occupation || "Administrador";
        if (elInitials)   elInitials.textContent   = initials;

        // Dispara evento global com os dados do usuário
        // Outras telas podem escutar: window.addEventListener('userLoaded', e => { ... e.detail ... })
        window.dispatchEvent(new CustomEvent("userLoaded", {
          detail: { user, usuario }
        }));

      } catch (err) {
        console.error("Erro ao carregar usuário na sidebar:", err);
      }
    });
  }
}

customElements.define('app-sidebar', AppSidebar);