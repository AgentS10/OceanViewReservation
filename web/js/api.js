/**
 * api.js — Centralised REST API client.
 * All fetch calls go through here so auth headers are applied uniformly.
 */

const API_BASE = '';   // same origin

function getToken() {
    return localStorage.getItem('token');
}

function authHeaders() {
    return {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + (getToken() || '')
    };
}

async function apiGet(path) {
    const res = await fetch(API_BASE + path, {
        method: 'GET',
        headers: authHeaders()
    });
    return res.json();
}

async function apiPost(path, body) {
    const res = await fetch(API_BASE + path, {
        method: 'POST',
        headers: authHeaders(),
        body: JSON.stringify(body)
    });
    return res.json();
}

async function apiPut(path, body) {
    const res = await fetch(API_BASE + path, {
        method: 'PUT',
        headers: authHeaders(),
        body: JSON.stringify(body)
    });
    return res.json();
}

async function apiDelete(path) {
    const res = await fetch(API_BASE + path, {
        method: 'DELETE',
        headers: authHeaders()
    });
    return res.json();
}

// Auth API
const AuthAPI = {
    login:  (username, password) => apiPost('/api/auth/login',  { username, password }),
    logout: ()                   => apiPost('/api/auth/logout', {}),
    status: ()                   => apiGet('/api/auth/status')
};

// Reservations API
const ReservationAPI = {
    getAll:    (search = '')  => apiGet('/api/reservations' + (search ? '?search=' + encodeURIComponent(search) : '')),
    getOne:    (num)          => apiGet('/api/reservations/' + num),
    create:    (data)         => apiPost('/api/reservations', data),
    update:    (num, data)    => apiPut('/api/reservations/' + num, data),
    cancel:    (num)          => apiDelete('/api/reservations/' + num)
};

// Bill API
const BillAPI = {
    get: (num) => apiGet('/api/bill/' + num)
};

// Rooms API
const RoomAPI = {
    getAll: () => apiGet('/api/rooms')
};

// Help API
const HelpAPI = {
    get: () => apiGet('/api/help')
};

// Users API (ADMIN only)
const UserAPI = {
    getAll:          ()           => apiGet('/api/users'),
    getOne:          (id)         => apiGet('/api/users/' + id),
    create:          (data)       => apiPost('/api/users', data),
    update:          (id, data)   => apiPut('/api/users/' + id, data),
    delete:          (id)         => apiDelete('/api/users/' + id),
    toggleActive:    (id)         => apiPost('/api/users/' + id + '/toggle-active', {}),
    changePassword:  (id, newPwd) => apiPost('/api/users/' + id + '/change-password', { newPassword: newPwd })
};

// Admin API (ADMIN / MANAGER)
const AdminAPI = {
    getSessions:     ()      => apiGet('/api/admin/sessions'),
    terminateSession:(token) => apiDelete('/api/admin/sessions/' + encodeURIComponent(token)),
    getStats:        ()      => apiGet('/api/admin/stats')
};

// Reservation status actions
const StatusAPI = {
    checkIn:  (num) => apiPost('/api/reservations/' + num + '/checkin',  {}),
    checkOut: (num) => apiPost('/api/reservations/' + num + '/checkout', {})
};
