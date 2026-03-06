/**
 * auth.js — Auth guard, role-aware navbar, shared alert helper.
 */

function getUser() {
    try { return JSON.parse(localStorage.getItem('user') || '{}'); } catch { return {}; }
}

function getRole() { return (getUser().role || '').toUpperCase(); }

function requireAuth() {
    if (!localStorage.getItem('token')) {
        window.location.href = '/index.html';
    }
}

function logout() {
    AuthAPI.logout().finally(() => {
        localStorage.clear();
        window.location.href = '/index.html';
    });
}

function renderNavUser() {
    const user = getUser();

    const nameEl = document.getElementById('nav-username');
    if (nameEl) nameEl.textContent = user.fullName || user.username || '';

    const roleEl = document.getElementById('nav-role');
    if (roleEl) {
        const r = (user.role || '').toUpperCase();
        const cls = r === 'ADMIN' ? 'role-admin' : r === 'MANAGER' ? 'role-manager' : 'role-staff';
        roleEl.innerHTML = `<span class="role-badge-nav ${cls}">${r}</span>`;
    }

    const avatarEl = document.querySelector('.sidebar-user-avatar');
    if (avatarEl && user.avatarUrl) {
        avatarEl.innerHTML = `<img src="${user.avatarUrl}" alt="avatar" style="width:100%;height:100%;object-fit:cover;border-radius:50%">`;
    }

    const btn = document.getElementById('btn-logout');
    if (btn) btn.addEventListener('click', logout);

    injectRoleNav();
}

function injectRoleNav() {
    const role = getRole();
    const adminNavEl = document.getElementById('admin-nav-links');
    if (!adminNavEl) return;
    if (role === 'ADMIN') {
        adminNavEl.innerHTML =
            '<a href="/users.html"><span class="nav-icon">👥</span> Users</a>' +
            '<a href="/admin.html"><span class="nav-icon">🛡️</span> Admin Panel</a>';
    } else if (role === 'MANAGER') {
        adminNavEl.innerHTML =
            '<a href="/admin.html"><span class="nav-icon">📊</span> Reports</a>';
    }
    highlightActiveNav();
}

function highlightActiveNav() {
    const path = window.location.pathname.split('/').pop() || 'dashboard.html';
    document.querySelectorAll('.sidebar-nav a').forEach(a => {
        const href = a.getAttribute('href') || '';
        if (href.endsWith(path)) a.classList.add('active');
        else a.classList.remove('active');
    });
}

// Background slideshow for inner pages
function initBgSlideshow() {
    const container = document.getElementById('bg-slideshow');
    if (!container) return;
    const images = [
        '/images/beach_view.jpg', '/images/hotel_front.jpg', '/images/pool.jpg',
        '/images/aerial_view.jpg', '/images/Dining_area.jpg', '/images/relaxing_area.jpg',
        '/images/night time_pool.jpg', '/images/presidential_suite.jpg'
    ];
    images.forEach((src, i) => {
        const div = document.createElement('div');
        div.className = 'bg-slide' + (i === 0 ? ' active' : '');
        div.style.backgroundImage = 'url(' + src + ')';
        container.appendChild(div);
    });
    let current = 0;
    setInterval(() => {
        const slides = container.querySelectorAll('.bg-slide');
        slides[current].classList.remove('active');
        current = (current + 1) % slides.length;
        slides[current].classList.add('active');
    }, 6000);
}

// Styled confirm dialog (replaces browser confirm())
function showConfirm(title, message, icon, onConfirm, confirmText = 'Confirm', confirmClass = 'btn-danger') {
    let overlay = document.getElementById('confirm-overlay');
    if (!overlay) {
        overlay = document.createElement('div');
        overlay.id = 'confirm-overlay';
        overlay.className = 'confirm-overlay';
        overlay.innerHTML = `<div class="confirm-dialog">
            <div class="confirm-icon" id="confirm-icon"></div>
            <div class="confirm-title" id="confirm-title"></div>
            <div class="confirm-msg" id="confirm-msg"></div>
            <div class="confirm-actions">
                <button class="btn btn-secondary" id="confirm-cancel">Cancel</button>
                <button class="btn" id="confirm-ok"></button>
            </div>
        </div>`;
        document.body.appendChild(overlay);
        overlay.addEventListener('click', e => { if (e.target === overlay) overlay.classList.remove('open'); });
        document.getElementById('confirm-cancel').addEventListener('click', () => overlay.classList.remove('open'));
    }
    document.getElementById('confirm-icon').textContent = icon || '⚠️';
    document.getElementById('confirm-title').textContent = title;
    document.getElementById('confirm-msg').textContent = message;
    const okBtn = document.getElementById('confirm-ok');
    okBtn.textContent = confirmText;
    okBtn.className = 'btn ' + confirmClass;
    okBtn.onclick = () => { overlay.classList.remove('open'); onConfirm(); };
    overlay.classList.add('open');
}

function showAlert(containerId, message, type = 'danger') {
    const el = document.getElementById(containerId);
    if (!el) return;
    el.className = 'alert alert-' + type;
    el.textContent = message;
    el.classList.remove('hidden');
    setTimeout(() => el.classList.add('hidden'), 6000);
}

// ── Login page handler ───────────────────────────────────────────
if (document.getElementById('login-form')) {
    document.getElementById('login-form').addEventListener('submit', async function (e) {
        e.preventDefault();
        const username = document.getElementById('username').value.trim();
        const password = document.getElementById('password').value;
        const btn      = document.getElementById('btn-login');

        if (!username || !password) {
            showAlert('alert-box', 'Please enter username and password.', 'danger'); return;
        }

        btn.disabled = true;
        btn.innerHTML = '<span class="spinner"></span> Signing in…';

        try {
            const data = await AuthAPI.login(username, password);
            if (data.success) {
                localStorage.setItem('token', data.token);
                localStorage.setItem('user',  JSON.stringify(data.user));
                window.location.href = '/dashboard.html';
            } else {
                showAlert('alert-box', data.message || 'Invalid credentials.', 'danger');
                btn.disabled = false; btn.textContent = 'Sign In';
            }
        } catch {
            showAlert('alert-box', 'Cannot reach server. Is the Java app running?', 'danger');
            btn.disabled = false; btn.textContent = 'Sign In';
        }
    });
}
