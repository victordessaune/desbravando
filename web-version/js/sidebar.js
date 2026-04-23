class AppSidebar extends HTMLElement {
  static get observedAttributes() {
    return ['page'];
  }

  connectedCallback() {
    this.render();
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
          { id: 'dashboard',      icon: 'fa-chart-column', label: 'Dashboard',       href: '../dashboard/dashboard.html' },
          { id: 'organizacao',    icon: 'fa-building',     label: 'Organização',     href: '../profile/profile.html' },
          { id: 'administradores',icon: 'fa-user',         label: 'Administradores', href: '../admin-list/admin-list.html' },
          { id: 'locais',         icon: 'fa-map-pin',      label: 'Locais',          href: '../form-locations/form-locations.html' },
        ],
      },
      {
        section: 'Sistema',
        items: [
          { id: 'configuracoes', icon: 'fa-gear', label: 'Configurações', href: '../configuracoes/configuracoes.html' },
          { id: 'seguranca',     icon: 'fa-lock', label: 'Segurança',     href: '../seguranca/seguranca.html' },
        ],
      },
    ];

    const sectionsHTML = navItems.map(({ section, items }) => {
      const itemsHTML = items.map(({ id, icon, label, href }) => {
        const isActive = id === this.currentPage;
        const cls = isActive ? 'principal-icon-page active' : 'principal-icons';
        return `
          <div class="${cls}">
            <a href="${href}" style="text-decoration:none;color:#FFFFFF;">
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
            <p class="name-admin">Victor Ramos</p>
            <p class="class-admin">Super admin</p>
          </div>
        </div>
      </aside>`;
  }
}

customElements.define('app-sidebar', AppSidebar);