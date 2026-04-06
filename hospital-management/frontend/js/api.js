// API Configuration
const API_BASE = 'http://localhost:8080/api';

const api = {
    getToken: () => localStorage.getItem('hms_token'),

    getHeaders() {
        const token = this.getToken();
        return {
            'Content-Type': 'application/json',
            ...(token ? { 'Authorization': `Bearer ${token}` } : {})
        };
    },

    async request(method, endpoint, data = null) {
        const options = {
            method,
            headers: this.getHeaders()
        };
        if (data) options.body = JSON.stringify(data);

        const res = await fetch(`${API_BASE}${endpoint}`, options);

        if (res.status === 401) {
            localStorage.clear();
            window.location.href = 'login.html';
            return;
        }

        const text = await res.text();
        try {
            return { ok: res.ok, data: JSON.parse(text), status: res.status };
        } catch {
            return { ok: res.ok, data: text, status: res.status };
        }
    },

    get: (ep) => api.request('GET', ep),
    post: (ep, data) => api.request('POST', ep, data),
    put: (ep, data) => api.request('PUT', ep, data),
    patch: (ep, data) => api.request('PATCH', ep, data),
    delete: (ep) => api.request('DELETE', ep),

    // Auth
    login: (creds) => api.post('/auth/login', creds),

    // Dashboard
    dashboardStats: () => api.get('/dashboard/stats'),

    // Patients
    getPatients: () => api.get('/patients'),
    getPatient: (id) => api.get(`/patients/${id}`),
    createPatient: (p) => api.post('/patients', p),
    updatePatient: (id, p) => api.put(`/patients/${id}`, p),
    deletePatient: (id) => api.delete(`/patients/${id}`),
    searchPatients: (q) => api.get(`/patients/search?q=${q}`),
    admitPatient: (id, data) => api.post(`/patients/${id}/admit`, data),
    dischargePatient: (id) => api.post(`/patients/${id}/discharge`, {}),

    // Doctors
    getDoctors: () => api.get('/doctors'),
    getDoctor: (id) => api.get(`/doctors/${id}`),
    createDoctor: (d) => api.post('/doctors', d),
    updateDoctor: (id, d) => api.put(`/doctors/${id}`, d),
    deleteDoctor: (id) => api.delete(`/doctors/${id}`),
    searchDoctors: (q) => api.get(`/doctors/search?q=${q}`),
    getActiveDoctors: () => api.get('/doctors/active'),

    // Appointments
    getAppointments: () => api.get('/appointments'),
    getTodayAppointments: () => api.get('/appointments/today'),
    createAppointment: (a) => api.post('/appointments', a),
    updateAppointment: (id, a) => api.put(`/appointments/${id}`, a),
    deleteAppointment: (id) => api.delete(`/appointments/${id}`),
    updateAppointmentStatus: (id, status) => api.patch(`/appointments/${id}/status`, { status }),

    // Bills
    getBills: () => api.get('/bills'),
    createBill: (b) => api.post('/bills', b),
    updateBill: (id, b) => api.put(`/bills/${id}`, b),
    payBill: (id, data) => api.post(`/bills/${id}/pay`, data),
    deleteBill: (id) => api.delete(`/bills/${id}`),
};

// Toast notification
function showToast(msg, type = 'success') {
    const container = document.getElementById('toast-container') || (() => {
        const el = document.createElement('div');
        el.id = 'toast-container';
        el.style.cssText = 'position:fixed;top:20px;right:20px;z-index:9999;display:flex;flex-direction:column;gap:10px';
        document.body.appendChild(el);
        return el;
    })();

    const toast = document.createElement('div');
    toast.style.cssText = `
        background: ${type === 'success' ? '#0f7c7c' : type === 'error' ? '#c94f6d' : '#e8a040'};
        color: #fff; padding: 14px 20px; border-radius: 10px; font-family: 'DM Sans', sans-serif;
        font-size: 14px; font-weight: 500; box-shadow: 0 8px 24px rgba(0,0,0,0.15);
        animation: slideIn 0.3s ease; display: flex; align-items: center; gap: 10px; min-width: 250px;
    `;
    toast.innerHTML = `<span>${type === 'success' ? '✅' : type === 'error' ? '❌' : '⚠️'}</span> ${msg}`;
    container.appendChild(toast);
    setTimeout(() => { toast.style.opacity = '0'; toast.style.transition = 'opacity 0.4s'; setTimeout(() => toast.remove(), 400); }, 3000);
}

// Auth check
function requireAuth() {
    const token = localStorage.getItem('hms_token');
    if (!token) {
        window.location.href = 'login.html';
        return false;
    }
    return true;
}

// Format date
function formatDate(dateStr) {
    if (!dateStr) return '—';
    return new Date(dateStr).toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' });
}

// Format currency
function formatCurrency(amount) {
    if (amount == null) return '₹0';
    return '₹' + Number(amount).toLocaleString('en-IN', { minimumFractionDigits: 2 });
}

// Status badge
function statusBadge(status) {
    const map = {
        ADMITTED: 'admitted', CRITICAL: 'critical', DISCHARGED: 'discharged',
        OBSERVATION: 'observation', OUTPATIENT: 'outpatient',
        SCHEDULED: 'scheduled', CONFIRMED: 'confirmed', COMPLETED: 'completed',
        CANCELLED: 'cancelled', PAID: 'paid', PENDING: 'pending', PARTIAL: 'partial',
        ACTIVE: 'active', INACTIVE: 'inactive', ON_LEAVE: 'on_leave',
    };
    return `<span class="badge badge-${(map[status] || 'default')}">${status?.replace('_', ' ')}</span>`;
}

// Sidebar active state
function setSidebarActive() {
    const page = window.location.pathname.split('/').pop();
    document.querySelectorAll('.nav-item').forEach(item => {
        item.classList.remove('active');
        if (item.getAttribute('href') === page) item.classList.add('active');
    });
}

// Load user info in sidebar
function loadUserInfo() {
    const user = JSON.parse(localStorage.getItem('hms_user') || '{}');
    const nameEl = document.getElementById('user-name');
    const roleEl = document.getElementById('user-role');
    const avatarEl = document.getElementById('user-avatar');
    if (nameEl) nameEl.textContent = user.fullName || 'User';
    if (roleEl) roleEl.textContent = user.role || '';
    if (avatarEl) avatarEl.textContent = (user.fullName || 'U').split(' ').map(n => n[0]).join('').slice(0, 2).toUpperCase();
}

function logout() {
    localStorage.clear();
    window.location.href = 'home.html';
}
