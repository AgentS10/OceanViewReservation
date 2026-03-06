/**
 * reservation.js — Handles add-reservation form and reservations list page.
 */

requireAuth();
renderNavUser();

/* ─── ADD RESERVATION PAGE ─────────────────────────── */
if (document.getElementById('add-reservation-form')) {

    // Room type dropdown + date min logic handled in add-reservation.html inline script

    document.getElementById('add-reservation-form').addEventListener('submit', async function (e) {
        e.preventDefault();
        clearErrors();

        const data = {
            guestName:       getValue('guestName'),
            address:         getValue('address'),
            contactNumber:   getValue('contactNumber'),
            roomTypeName:    getValue('roomTypeName'),
            checkInDate:     getValue('checkInDate'),
            checkOutDate:    getValue('checkOutDate'),
            specialRequests: getValue('specialRequests')
        };

        let valid = true;
        if (!data.guestName)     { setError('guestName',     'Guest name is required.');    valid = false; }
        if (!data.contactNumber) { setError('contactNumber', 'Contact number is required.'); valid = false; }
        if (!data.roomTypeName)  { setError('roomTypeName',  'Please select a room type.'); valid = false; }
        if (!data.checkInDate)   { setError('checkInDate',   'Check-in date is required.');  valid = false; }
        if (!data.checkOutDate)  { setError('checkOutDate',  'Check-out date is required.'); valid = false; }
        if (data.checkInDate && data.checkOutDate && data.checkOutDate <= data.checkInDate) {
            setError('checkOutDate', 'Check-out must be after check-in.'); valid = false;
        }
        if (!valid) return;

        const btn = document.getElementById('btn-save');
        btn.disabled = true; btn.textContent = 'Saving…';

        try {
            const res = await ReservationAPI.create(data);
            if (res.success) {
                showAlert('alert-box', '✔ Reservation created: ' + res.reservation.reservationNumber, 'success');
                document.getElementById('add-reservation-form').reset();
            } else {
                showAlert('alert-box', res.message || 'Failed to create reservation.', 'danger');
            }
        } catch (err) {
            showAlert('alert-box', 'Server error. Please try again.', 'danger');
        } finally {
            btn.disabled = false; btn.textContent = 'Save Reservation';
        }
    });
}

/* ─── RESERVATIONS LIST PAGE ────────────────────────── */
if (document.getElementById('reservations-tbody')) {
    loadReservations();

    document.getElementById('btn-search').addEventListener('click', () => {
        loadReservations(document.getElementById('search-input').value.trim());
    });
    document.getElementById('search-input').addEventListener('keydown', e => {
        if (e.key === 'Enter') loadReservations(document.getElementById('search-input').value.trim());
    });
    document.getElementById('btn-clear').addEventListener('click', () => {
        document.getElementById('search-input').value = '';
        loadReservations();
    });
}

async function loadReservations(search = '') {
    const tbody = document.getElementById('reservations-tbody');
    tbody.innerHTML = '<tr><td colspan="7" style="text-align:center"><span class="spinner"></span></td></tr>';
    try {
        const list = await ReservationAPI.getAll(search);
        tbody.innerHTML = '';
        if (list.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" style="text-align:center;color:#6c757d;">No reservations found.</td></tr>';
            return;
        }
        list.forEach(r => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td><strong>${r.reservationNumber}</strong></td>
                <td>${r.guestName}</td>
                <td>${r.contactNumber}</td>
                <td>${r.roomTypeName}</td>
                <td>${r.checkInDate}</td>
                <td>${r.checkOutDate}</td>
                <td>${badgeHtml(r.status)}</td>
                <td>
                  <a class="btn btn-sm btn-primary" href="view-reservation.html?num=${r.reservationNumber}">View</a>
                </td>
            `;
            tbody.appendChild(tr);
        });
    } catch (err) {
        tbody.innerHTML = '<tr><td colspan="7" style="color:red;">Failed to load reservations.</td></tr>';
    }
}

/* ─── Helpers ──────────────────────────────────────── */
function getValue(id) {
    const el = document.getElementById(id);
    return el ? el.value.trim() : '';
}
function setError(id, msg) {
    const el = document.getElementById(id);
    if (!el) return;
    el.classList.add('is-invalid');
    let fb = el.nextElementSibling;
    if (!fb || !fb.classList.contains('invalid-feedback')) {
        fb = document.createElement('div');
        fb.className = 'invalid-feedback';
        el.parentNode.insertBefore(fb, el.nextSibling);
    }
    fb.textContent = msg;
    fb.style.display = 'block';
}
function clearErrors() {
    document.querySelectorAll('.is-invalid').forEach(el => el.classList.remove('is-invalid'));
    document.querySelectorAll('.invalid-feedback').forEach(el => el.style.display = 'none');
}
function badgeHtml(status) {
    const map = { 'CONFIRMED':'badge-success','CHECKED_IN':'badge-info','CHECKED_OUT':'badge-secondary','CANCELLED':'badge-danger' };
    return `<span class="badge ${map[status] || 'badge-secondary'}">${status}</span>`;
}
