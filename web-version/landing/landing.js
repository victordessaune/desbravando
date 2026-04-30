/* ─── NAVBAR SCROLL ─────────────────────────────────────────── */
window.addEventListener("scroll", () => {
    const navbar = document.querySelector(".navbar");
    navbar.classList.toggle("scrolled", window.scrollY > 50);
});

/* ─── SCROLL REVEAL ─────────────────────────────────────────── */
const revealObserver = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
        if (entry.isIntersecting) {
            entry.target.classList.add("revealed");
            revealObserver.unobserve(entry.target);
        }
    });
}, { threshold: 0.15, rootMargin: "0px 0px -60px 0px" });

document.querySelectorAll(
    ".box-individual-feature, .info-top, .info-middle, .info-bottom"
).forEach(el => {
    el.classList.add("reveal-on-scroll");
    revealObserver.observe(el);
});

/* final section entra como um bloco só */
const finalSection = document.querySelector(".final-section");
if (finalSection) revealObserver.observe(finalSection);

/* stagger nas features */
document.querySelectorAll(".box-individual-feature").forEach((el, i) => {
    el.style.transitionDelay = `${i * 0.12}s`;
});

/* stagger nas etapas how it works */
document.querySelectorAll(".info-top, .info-middle, .info-bottom").forEach((el, i) => {
    el.style.transitionDelay = `${i * 0.15}s`;
});

/* ─── ANIMATED COUNTERS ──────────────────────────────────────── */
function animateCounter(el, target, duration = 1800) {
    const suffix = el.textContent.replace(/[0-9]/g, "");
    let start = null;
    const step = (ts) => {
        if (!start) start = ts;
        const progress = Math.min((ts - start) / duration, 1);
        const ease = 1 - Math.pow(1 - progress, 3);
        el.textContent = Math.floor(ease * target).toLocaleString("pt-BR") + suffix;
        if (progress < 1) requestAnimationFrame(step);
    };
    requestAnimationFrame(step);
}

const statsObserver = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
        if (entry.isIntersecting) {
            entry.target.querySelectorAll("h3[data-count]").forEach(h3 => {
                animateCounter(h3, parseInt(h3.dataset.count), 2000);
            });
            statsObserver.unobserve(entry.target);
        }
    });
}, { threshold: 0.4 });

/* prepara os elementos de estatística */
document.querySelectorAll(".statistics-left h3, .statistics-middle h3, .statistics-right h3").forEach(h3 => {
    const raw = h3.textContent.trim();
    const num = parseInt(raw.replace(/\D/g, ""));
    const suffix = raw.replace(/[0-9]/g, "");
    h3.dataset.count = num;
    h3.textContent = "0" + suffix;
});

const statsSection = document.querySelector(".statistics");
if (statsSection) statsObserver.observe(statsSection);

/* ─── PARALLAX SUAVE NO HERO ────────────────────────────────── */
const woman = document.querySelector(".woman");
const shape = document.querySelector(".shape");

window.addEventListener("scroll", () => {
    const scrollY = window.scrollY;
    if (woman)  woman.style.transform  = `translateY(${scrollY * 0.06}px)`;
    if (shape)  shape.style.transform  = `translateY(${scrollY * 0.03}px)`;
}, { passive: true });

/* ─── MAGNETIC BUTTONS ──────────────────────────────────────── */
document.querySelectorAll(".button-primary, .button-secondary, .final-button, .button-navbar").forEach(btn => {
    btn.addEventListener("mousemove", e => {
        const rect = btn.getBoundingClientRect();
        const cx = rect.left + rect.width  / 2;
        const cy = rect.top  + rect.height / 2;
        const dx = (e.clientX - cx) * 0.25;
        const dy = (e.clientY - cy) * 0.25;
        btn.style.transform = `translate(${dx}px, ${dy}px) scale(1.05)`;
    });
    btn.addEventListener("mouseleave", () => {
        btn.style.transform = "";
    });
});

/* ─── CURSOR HIGHLIGHT NAS FEATURE CARDS ───────────────────── */
document.querySelectorAll(".box-individual-feature").forEach(card => {
    card.addEventListener("mousemove", e => {
        const rect = card.getBoundingClientRect();
        const x = e.clientX - rect.left;
        const y = e.clientY - rect.top;
        card.style.background = `radial-gradient(circle at ${x}px ${y}px, rgba(106,89,246,0.12) 0%, rgba(214,214,214,0.15) 60%)`;
    });
    card.addEventListener("mouseleave", () => {
        card.style.background = "#D6D6D625";
    });
});

/* ─── TILT 3D NAS INFO CARDS ────────────────────────────────── */
document.querySelectorAll(".info-top, .info-middle, .info-bottom").forEach(card => {
    card.addEventListener("mousemove", e => {
        const rect = card.getBoundingClientRect();
        const cx = rect.left + rect.width  / 2;
        const cy = rect.top  + rect.height / 2;
        const rx = ((e.clientY - cy) / (rect.height / 2)) * 4;
        const ry = ((e.clientX - cx) / (rect.width  / 2)) * -4;
        card.style.transform = `perspective(600px) rotateX(${rx}deg) rotateY(${ry}deg) scale(1.02)`;
    });
    card.addEventListener("mouseleave", () => {
        card.style.transform = "";
    });
});

/* ─── RIPPLE NOS BOTÕES ──────────────────────────────────────── */
document.querySelectorAll(".button-primary, .final-button").forEach(btn => {
    btn.style.position   = "relative";
    btn.style.overflow   = "hidden";
    btn.addEventListener("click", e => {
        const rect = btn.getBoundingClientRect();
        const ripple = document.createElement("span");
        const size = Math.max(rect.width, rect.height) * 2;
        Object.assign(ripple.style, {
            position:     "absolute",
            width:        size + "px",
            height:       size + "px",
            borderRadius: "50%",
            background:   "rgba(255,255,255,0.35)",
            transform:    "scale(0)",
            animation:    "rippleAnim 0.55s linear",
            left:         (e.clientX - rect.left - size / 2) + "px",
            top:          (e.clientY - rect.top  - size / 2) + "px",
            pointerEvents:"none",
        });
        btn.appendChild(ripple);
        ripple.addEventListener("animationend", () => ripple.remove());
    });
});

/* injeta o keyframe do ripple dinamicamente */
const style = document.createElement("style");
style.textContent = `
@keyframes rippleAnim {
    to { transform: scale(1); opacity: 0; }
}

/* ── reveal-on-scroll base ── */
.reveal-on-scroll {
    opacity: 0;
    transform: translateY(32px);
    transition: opacity 0.65s cubic-bezier(0.22,1,0.36,1),
                transform 0.65s cubic-bezier(0.22,1,0.36,1);
}
.reveal-on-scroll.revealed {
    opacity: 1;
    transform: translateY(0);
}

/* ── info cards transition suave ── */
.info-top, .info-middle, .info-bottom {
    transition: transform 0.2s ease, box-shadow 0.2s ease;
}

/* ── feature cards transition suave ── */
.box-individual-feature {
    transition: background 0.3s ease, transform 0.3s ease, box-shadow 0.3s ease, opacity 0.65s cubic-bezier(0.22,1,0.36,1);
}
.box-individual-feature:hover {
    box-shadow: 0 8px 32px rgba(106,89,246,0.13);
    transform: translateY(-4px) scale(1.02);
}

/* ── smooth transition nos botões magnéticos ── */
.button-primary, .button-secondary, .final-button, .button-navbar {
    transition: transform 0.15s ease !important;
}
`;
document.head.appendChild(style);