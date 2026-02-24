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
            '<li><a href="/users.html" id="nav-users">👥 Users</a></li>' +
            '<li><a href="/admin.html" id="nav-admin">🛡 Admin</a></li>';
    } else if (role === 'MANAGER') {
        adminNavEl.innerHTML =
            '<li><a href="/admin.html" id="nav-admin">📊 Reports</a></li>';
    }
    highlightActiveNav();
}

function highlightActiveNav() {
    const path = window.location.pathname.split('/').pop() || 'dashboard.html';
    document.querySelectorAll('.navbar-nav a').forEach(a => {
        const href = a.getAttribute('href') || '';
        if (href.endsWith(path)) a.classList.add('active');
        else a.classList.remove('active');
    });
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
            showAlert('alert-box', 'Cannot reach server. Is the Java app running on port 8080?', 'danger');
            btn.disabled = false; btn.textContent = 'Sign In';
        }
    });
}
