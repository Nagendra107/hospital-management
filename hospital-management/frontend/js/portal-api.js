const API_BASE = 'https://hospital-backend.onrender.com';

const portalApi = {
    getToken: () => localStorage.getItem('portal_token'),

    getHeaders() {
        const token = this.getToken();
        return {
            'Content-Type': 'application/json',
            ...(token ? { 'Authorization': `Bearer ${token}` } : {})
        };
    },

    async request(method, endpoint, data = null) {
        const options = { method, headers: this.getHeaders() };
        if (data) options.body = JSON.stringify(data);
        try {
            const res = await fetch(`${API_BASE}${endpoint}`, options);
            if (res.status === 401) {
                localStorage.clear();
                window.location.href = 'home.html';
                return;
            }
            const text = await res.text();
            try { return { ok: res.ok, data: JSON.parse(text), status: res.status }; }
            catch { return { ok: res.ok, data: text, status: res.status }; }
        } catch (e) {
            return { ok: false, data: { message: 'Cannot connect to server' } };
        }
    },

    get: (ep) => portalApi.request('GET', ep),
    post: (ep, d) => portalApi.request('POST', ep, d),
    put: (ep, d) => portalApi.request('PUT', ep, d),

    // Auth
    register: (d) => portalApi.post('/portal/register', d),
    login: (d) => portalApi.post('/portal/login', d),

    // Profile
    getProfile: (id) => portalApi.get(`/portal/profile/${id}`),
    updateProfile: (id, d) => portalApi.put(`/portal/profile/${id}`, d),
    changePassword: (id, d) => portalApi.post(`/portal/profile/${id}/change-password`, d),

    // Appointments
    getMyAppointments: (uid) => portalApi.get(`/portal/appointments/${uid}`),
    bookAppointment: (uid, d) => portalApi.post(`/portal/appointments/${uid}`, d),
    cancelAppointment: (apptId, uid) => portalApi.post(`/portal/appointments/${apptId}/cancel/${uid}`, {}),

    // Bills
    getMyBills: (uid) => portalApi.get(`/portal/bills/${uid}`),

    // Doctors
    getDoctors: () => portalApi.get('/portal/doctors'),
    getSpecializations: () => portalApi.get('/portal/doctors/specializations'),
};

// Auth helpers
function requirePatientAuth() {
    const token = localStorage.getItem('portal_token');
    const user = localStorage.getItem('portal_user');
    if (!token || !user) {
        window.location.href = 'home.html';
        return null;
    }
    return JSON.parse(user);
}

function getPatientUser() {
    const u = localStorage.getItem('portal_user');
    return u ? JSON.parse(u) : null;
}

function portalLogout() {
    localStorage.removeItem('portal_token');
    localStorage.removeItem('portal_user');
    window.location.href = 'home.html';
}

// Toast
function showToast(msg, type = 'success') {
    const container = document.getElementById('toast-container') || (() => {
        const el = document.createElement('div');
        el.id = 'toast-container';
        el.style.cssText = 'position:fixed;top:20px;right:20px;z-index:9999;display:flex;flex-direction:column;gap:10px';
        document.body.appendChild(el);
        return el;
    })();
    const colors = { success: '#10b981', error: '#f43f5e', warning: '#f59e0b' };
    const icons = { success: '✅', error: '❌', warning: '⚠️' };
    const toast = document.createElement('div');
    toast.style.cssText = `background:${colors[type]};color:#fff;padding:14px 20px;border-radius:12px;font-family:'Nunito',sans-serif;font-size:13px;font-weight:700;box-shadow:0 8px 24px rgba(0,0,0,0.15);display:flex;align-items:center;gap:10px;min-width:260px;animation:slideIn 0.3s ease`;
    toast.innerHTML = `<span>${icons[type]}</span> ${msg}`;
    container.appendChild(toast);
    setTimeout(() => { toast.style.opacity = '0'; toast.style.transition = 'opacity 0.4s'; setTimeout(() => toast.remove(), 400); }, 3500);
}

function formatDate(d) {
    if (!d) return '—';
    return new Date(d).toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' });
}

function formatCurrency(a) {
    if (a == null) return '₹0';
    return '₹' + Number(a).toLocaleString('en-IN', { minimumFractionDigits: 2 });
}

function statusBadge(status) {
    const map = { SCHEDULED:'scheduled', CONFIRMED:'confirmed', COMPLETED:'completed', CANCELLED:'cancelled', PAID:'paid', PENDING:'pending', PARTIAL:'partial' };
    return `<span class="badge badge-${map[status]||'scheduled'}">${status?.replace('_',' ')}</span>`;
}

// Load nav user info
function loadNavUser() {
    const user = getPatientUser();
    if (!user) return;
    const nameEl = document.getElementById('nav-user-name');
    const avatarEl = document.getElementById('nav-avatar');
    if (nameEl) nameEl.textContent = user.firstName || user.fullName || 'Patient';
    if (avatarEl) avatarEl.textContent = ((user.firstName||'P')[0] + (user.lastName||'')[0]).toUpperCase();
}
