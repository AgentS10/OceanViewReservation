/**
 * dashboard.js — Loads stats from /api/admin/stats + recent reservations.
 */

requireAuth();
renderNavUser();
initBgSlideshow();

// Welcome banner
(function() {
    const user = getUser();
    const h = new Date().getHours();
    const greet = h < 12 ? 'Good morning' : h < 17 ? 'Good afternoon' : 'Good evening';
    const el = document.getElementById('wb-greeting');
    if (el) el.textContent = greet + ', ' + (user.fullName || user.username || 'User');
    const timeEl = document.getElementById('wb-time');
    if (timeEl) timeEl.textContent = new Date().toLocaleDateString('en-LK', { weekday:'long', year:'numeric', month:'long', day:'numeric' });
})();

async function loadDashboard() {
    try {
        const [stats, reservations] = await Promise.all([
            AdminAPI.getStats(),
            ReservationAPI.getAll()
        ]);

        document.getElementById('stat-total').textContent      = stats.totalReservations ?? 0;
        document.getElementById('stat-confirmed').textContent  = stats.confirmed  ?? 0;
        document.getElementById('stat-checkedin').textContent  = stats.checkedIn  ?? 0;
        document.getElementById('stat-cancelled').textContent  = stats.cancelled  ?? 0;

        const sessEl = document.getElementById('stat-sessions');
        if (sessEl) sessEl.textContent = stats.activeSessions ?? 0;

        const revEl = document.getElementById('stat-revenue');
        if (revEl) revEl.textContent = 'LKR ' + Number(stats.totalRevenue ?? 0).toLocaleString('en-LK', { maximumFractionDigits: 0 });

        renderRecentTable(reservations.slice(0, 8));
    } catch (err) {
        document.getElementById('recent-tbody').innerHTML =
            '<tr><td colspan="7" style="color:red;padding:1rem">Failed to load data. Is the server running?</td></tr>';
    }
}

function renderRecentTable(list) {
    const tbody = document.getElementById('recent-tbody');
    tbody.innerHTML = '';
    if (!list.length) {
        tbody.innerHTML = '<tr><td colspan="7"><div class="empty-state"><div class="empty-icon">📋</div><p>No reservations yet.</p></div></td></tr>';
        return;
    }
    list.forEach(r => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td><strong>${r.reservationNumber}</strong></td>
            <td>${r.guestName}</td>
            <td>${r.roomTypeName}</td>
            <td>${r.checkInDate}</td>
            <td>${r.checkOutDate}</td>
            <td>${badgeHtml(r.status)}</td>
            <td><a class="btn btn-sm btn-outline" href="/view-reservation.html?num=${r.reservationNumber}">View</a></td>
        `;
        tbody.appendChild(tr);
    });
}

function badgeHtml(status) {
    const map = { CONFIRMED:'badge-success', CHECKED_IN:'badge-info', CHECKED_OUT:'badge-secondary', CANCELLED:'badge-danger' };
    return `<span class="badge ${map[status] || 'badge-secondary'}">${status.replace('_',' ')}</span>`;
}

// Hide admin-only quick cards for STAFF
(function() {
    const role = getRole();
    if (role === 'STAFF') {
        const qcAdmin = document.getElementById('qc-admin');
        const qcUsers = document.getElementById('qc-users');
        if (qcAdmin) qcAdmin.style.display = 'none';
        if (qcUsers) qcUsers.style.display = 'none';
    } else if (role === 'MANAGER') {
        const qcUsers = document.getElementById('qc-users');
        if (qcUsers) qcUsers.style.display = 'none';
    }
})();

loadDashboard();
