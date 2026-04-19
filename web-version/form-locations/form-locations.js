let currentStep = 1;
const totalSteps = 4;

const stepTitles = [
    'Informações Básicas',
    'Estrutura & Horários',
    'Detalhes & Avaliações',
    'Publicação'
];
const stepSubtitles = [
    'Preencha as informações básicas do local.',
    'Configure a infraestrutura e horários de funcionamento.',
    'Adicione detalhes, avaliações e serviços disponíveis.',
    'Finalize as configurações e publique o local na plataforma.'
];

const tags = [];

function goToStep(step) {
    document.querySelectorAll('.panel').forEach(p => p.classList.remove('active'));
    document.getElementById('panel-' + step).classList.add('active');
    document.getElementById('success-screen').style.display = 'none';

    // hero
    document.getElementById('hero-title').textContent    = stepTitles[step - 1];
    document.getElementById('hero-subtitle').textContent = stepSubtitles[step - 1];

    // progresso
    document.getElementById('progress-fill').style.width   = ((step / totalSteps) * 100) + '%';

    // stepper
    document.querySelectorAll('.step-item').forEach((el, i) => {
        el.classList.remove('active', 'done');
        if (i + 1 < step)   el.classList.add('done');
        if (i + 1 === step) el.classList.add('active');
    });

    currentStep = step;
    window.scrollTo({ top: document.querySelector('.form-hero').offsetTop - 72, behavior: 'smooth' });
}

function nextStep(from) {
    if (from === 1) {
        const nome   = document.getElementById('nome-local').value.trim();
        const errNome = document.getElementById('err-nome');
        if (!nome) { errNome.classList.add('show'); return; }
        errNome.classList.remove('show');
    }
    if (from < totalSteps) goToStep(from + 1);
}

function prevStep(from) {
    if (from > 1) goToStep(from - 1);
}

function addHourRow() {
    const days    = ['Terça-feira', 'Quarta-feira', 'Quinta-feira', 'Sexta-feira'];
    const list    = document.getElementById('hours-list');
    const row     = document.createElement('div');
    row.className = 'hours-grid';
    const usedDays = list.querySelectorAll('.day-label').length;
    const dayName  = days[usedDays - 3] || 'Outro dia';
    row.innerHTML  = `
        <span class="day-label">${dayName}</span>
        <input type="text" placeholder="Abertura" style="font-size:12px;">
        <input type="text" placeholder="Fechamento" style="font-size:12px;">
    `;
    list.appendChild(row);
}

function setStars(group, val) {
    const container = document.getElementById('stars-' + group);
    container.dataset.val = val;
    container.querySelectorAll('.star').forEach((s, i) => s.classList.toggle('lit', i < val));
}

function toggleTag(el)   { el.classList.toggle('selected'); }

function selectPrice(el) {
    document.querySelectorAll('.price-opt').forEach(o => o.classList.remove('selected'));
    el.classList.add('selected');
}

function addTag() {
    const input = document.getElementById('tag-input');
    const val   = input.value.trim();
    if (!val) return;
    tags.push(val);
    renderTags();
    input.value = '';
}

function renderTags() {
    document.getElementById('tags-preview').innerHTML = tags.map((t, i) =>
        `<span class="tag-pill">${t}<button onclick="removeTag(${i})">×</button></span>`
    ).join('');
}

function removeTag(i) { tags.splice(i, 1); renderTags(); }

function previewImages(e) {
    const preview = document.getElementById('image-preview');
    preview.innerHTML = '';
    Array.from(e.target.files).slice(0, 8).forEach(file => {
        const img = document.createElement('img');
        img.src   = URL.createObjectURL(file);
        img.style.cssText = 'width:80px;height:80px;object-fit:cover;border-radius:10px;border:2px solid #dde1f5';
        preview.appendChild(img);
    });
}

function publishLocal() {
    document.querySelectorAll('.panel').forEach(p => p.classList.remove('active'));
    document.getElementById('success-screen').style.display = 'block';
    document.getElementById('hero-title').textContent    = 'Publicado com sucesso!';
    document.getElementById('hero-subtitle').textContent = 'O local já está disponível na plataforma Desbravando.';
    document.getElementById('progress-fill').style.width = '100%';
    document.querySelectorAll('.step-item').forEach(el => el.classList.add('done'));
}

function resetForm() {
    document.getElementById('success-screen').style.display = 'none';
    document.getElementById('nome-local').value = '';
    goToStep(1);
}

function handleMapClick() {
    const cidade = document.getElementById('cidade').value;
    const rua    = document.getElementById('rua').value;
    const query  = encodeURIComponent([rua, cidade].filter(Boolean).join(', ') || 'Brasil');
    window.open('https://maps.google.com/?q=' + query, '_blank');
}

/* ── scroll navbar ── */
window.addEventListener('scroll', () => {
    document.querySelector('.navbar').classList.toggle('scrolled', window.scrollY > 10);
});

document.addEventListener('DOMContentLoaded', () => {
    // tag input — Enter
    document.getElementById('tag-input').addEventListener('keydown', e => {
        if (e.key === 'Enter') { e.preventDefault(); addTag(); }
    });

    // CEP mask
    document.getElementById('cep').addEventListener('input', function () {
        let v = this.value.replace(/\D/g, '');
        if (v.length > 5) v = v.slice(0, 5) + '-' + v.slice(5, 8);
        this.value = v;
    });

    // Phone mask
    document.getElementById('telefone').addEventListener('input', function () {
        let v = this.value.replace(/\D/g, '');
        if (v.length > 2)  v = '(' + v.slice(0, 2) + ') ' + v.slice(2);
        if (v.length > 10) v = v.slice(0, 10) + '-' + v.slice(10, 14);
        this.value = v;
    });
});