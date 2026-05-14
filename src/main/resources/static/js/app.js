/* ═══════════ MediCare Frontend ═══════════ */
const API = '';  // same-origin; Spring Boot serves on /v1/...

/* ── State ── */
let doctors = [], patients = [], appointments = [];

/* ═══ API helpers ═══ */
async function api(method, path, body) {
  try {
    const res = await fetch(path, {
      method,
      headers: body ? { 'Content-Type': 'application/json' } : {},
      body: body ? JSON.stringify(body) : undefined,
    });
    const json = await res.json().catch(() => ({}));
    if (!res.ok) throw new Error(json.message || `HTTP ${res.status}`);
    return json.data ?? json;
  } catch (e) { throw e; }
}

const GET  = (p)      => api('GET',   p);
const POST = (p, b)   => api('POST',  p, b);
const PATCH= (p, b)   => api('PATCH', p, b);
const DEL  = (p)      => api('DELETE', p);

/* ═══ Toast ═══ */
function toast(type, title, msg) {
  const c = document.getElementById('toast-container');
  const icons = { success:'✅', error:'❌', info:'ℹ️' };
  const el = document.createElement('div');
  el.className = `toast toast-${type}`;
  el.innerHTML = `<span class="toast-icon">${icons[type]||'•'}</span>
    <div class="toast-content"><div class="toast-title">${title}</div>${msg?`<div class="toast-msg">${msg}</div>`:''}
    </div>`;
  c.appendChild(el);
  setTimeout(() => { el.classList.add('exit'); setTimeout(() => el.remove(), 300); }, 3500);
}

/* ═══ Modal ═══ */
function openModal(id)  { document.getElementById(id).classList.add('open'); }
function closeModal(id) { document.getElementById(id).classList.remove('open'); }

document.querySelectorAll('.modal-close,[data-modal]').forEach(btn => {
  btn.addEventListener('click', () => closeModal(btn.dataset.modal));
});
document.querySelectorAll('.modal-overlay').forEach(ov => {
  ov.addEventListener('click', e => { if (e.target === ov) closeModal(ov.id); });
});

/* ═══ Navigation ═══ */
const pages = { dashboard:'Dashboard', doctors:'Doctors', patients:'Patients', appointments:'Appointments', book:'Book Appointment' };
const subtitles = {
  dashboard:'Clinic overview and key metrics',
  doctors:'Manage clinic doctors and their specialties',
  patients:'Manage registered patients',
  appointments:'View and manage all appointments',
  book:'Schedule a new appointment',
};

function navigateTo(name) {
  document.querySelectorAll('.page').forEach(p => p.classList.remove('active'));
  document.querySelectorAll('.nav-item').forEach(n => n.classList.remove('active'));
  const page = document.getElementById(`page-${name}`);
  const nav  = document.getElementById(`nav-${name}`);
  if (page) page.classList.add('active');
  if (nav)  nav.classList.add('active');
  document.getElementById('page-title').textContent    = pages[name] || name;
  document.getElementById('page-subtitle').textContent = subtitles[name] || '';
  if (name === 'doctors')      renderDoctors();
  if (name === 'patients')     renderPatients();
  if (name === 'appointments') renderAppointments();
  if (name === 'book')         populateBookSelects();
}

document.querySelectorAll('.nav-item').forEach(item => {
  item.addEventListener('click', e => {
    e.preventDefault();
    const pg = item.dataset.page;
    if (pg) navigateTo(pg);
    if (window.innerWidth < 768) document.getElementById('sidebar').classList.remove('open');
  });
});

document.querySelectorAll('.view-all-link').forEach(a => {
  a.addEventListener('click', e => { e.preventDefault(); navigateTo(a.dataset.page); });
});

document.getElementById('hamburger').addEventListener('click', () => {
  document.getElementById('sidebar').classList.toggle('open');
});

/* ═══ API Status Check ═══ */
async function checkStatus() {
  const dot  = document.getElementById('status-dot');
  const txt  = document.getElementById('status-text');
  try {
    await fetch('/v1/appointments?status=SCHEDULED');
    dot.className = 'status-dot online';
    txt.textContent = 'API Online';
  } catch {
    dot.className = 'status-dot offline';
    txt.textContent = 'API Offline';
  }
}

/* ═══ Load Data ═══ */
async function loadAllAppointments(params = '') {
  try { appointments = await GET(`/v1/appointments${params}`); }
  catch { appointments = []; }
}

/* Dashboard loads from appointments only (no list-all for doctors/patients) */
async function loadDashboard() {
  await loadAllAppointments();
  // stats
  const scheduled = appointments.filter(a => a.status === 'SCHEDULED').length;
  const completed  = appointments.filter(a => a.status === 'COMPLETED').length;
  document.getElementById('stat-scheduled').textContent = scheduled;
  document.getElementById('stat-completed').textContent = completed;

  document.getElementById('stat-doctors').textContent  = doctors.length || '—';
  document.getElementById('stat-patients').textContent = patients.length || '—';
  document.getElementById('badge-appointments').textContent = appointments.length;

  renderDashboardAppointments();
  renderSpecialtyChart();
}

function renderDashboardAppointments() {
  const el = document.getElementById('dash-appointments-list');
  const recent = [...appointments].sort((a,b) => new Date(b.appointmentTime) - new Date(a.appointmentTime)).slice(0,6);
  if (!recent.length) { el.innerHTML = emptyState('No appointments yet'); return; }
  el.innerHTML = `<table class="data-table"><thead><tr><th>Patient</th><th>Doctor</th><th>Time</th><th>Status</th></tr></thead>
    <tbody>${recent.map(a => `<tr>
      <td class="cell-name">${a.patient?.fullName||'—'}</td>
      <td>${a.doctor?.fullName||'—'}</td>
      <td style="font-size:.78rem;color:var(--text-muted)">${fmtDt(a.appointmentTime)}</td>
      <td><span class="status-badge status-${a.status}">${a.status}</span></td>
    </tr>`).join('')}</tbody></table>`;
}

function renderSpecialtyChart() {
  const el = document.getElementById('specialty-chart');
  const specs = { CARDIOLOGY: 0, DENTIST: 0, THERAPIST: 0 };
  appointments.forEach(a => { if (a.doctor?.specialty && specs[a.doctor.specialty] !== undefined) specs[a.doctor.specialty]++; });
  const total = Object.values(specs).reduce((s,v) => s+v, 0) || 1;
  const labels = { CARDIOLOGY:'❤️ Cardiology', DENTIST:'🦷 Dentist', THERAPIST:'🧠 Therapist' };
  el.innerHTML = Object.entries(specs).map(([k,v]) => `
    <div class="specialty-bar-item">
      <div class="specialty-bar-label"><span>${labels[k]}</span><span>${v}</span></div>
      <div class="specialty-bar-track"><div class="specialty-bar-fill bar-${k}" style="width:${Math.round(v/total*100)}%"></div></div>
    </div>`).join('');
}

/* ═══ Doctors ═══ */
async function loadDoctors() {
  try { doctors = await GET('/v1/doctors'); }
  catch { doctors = []; }
}

function renderDoctors(filter='') {
  const grid = document.getElementById('doctors-grid');
  const fl = filter.toLowerCase();
  const list = doctors.filter(d =>
    !fl || d.fullName?.toLowerCase().includes(fl) || (d.specialty||'').toLowerCase().includes(fl)
  );
  document.getElementById('badge-doctors').textContent = doctors.length;

  if (!list.length) { grid.innerHTML = `<div style="grid-column:1/-1">${emptyState('No doctors found')}</div>`; return; }

  grid.innerHTML = list.map(d => {
    const initials = (d.fullName||'?').split(' ').map(w=>w[0]).slice(0,2).join('').toUpperCase();
    const sp = d.specialty || null;
    return `<div class="doctor-card">
      <div class="doctor-card-top">
        <div class="doctor-avatar">${initials}</div>
        <div class="doctor-info">
          <div class="doctor-name">${d.fullName}</div>
          <div class="doctor-specialty">${sp ? `<span class="specialty-badge badge-${sp}">${specialtyLabel(sp)}</span>` : '<span class="specialty-badge badge-none">No specialty</span>'}</div>
        </div>
      </div>
      <div class="doctor-id">ID: ${d.id}</div>
      <div class="doctor-actions">
        <button class="btn btn-secondary btn-sm" onclick="showDoctorApts('${d.id}','${d.fullName}')">📅 Schedule</button>
        <button class="btn btn-ghost btn-sm" onclick="openAssignSpecialty('${d.id}','${d.fullName}','${sp||''}')">✏️ Specialty</button>
        <button class="btn btn-danger btn-sm" onclick="promptDeleteDoctor('${d.id}')">🗑 Delete</button>
      </div>
    </div>`;
  }).join('');
}

document.getElementById('doctor-search').addEventListener('input', e => renderDoctors(e.target.value));

/* ═══ Create Doctor ═══ */
document.getElementById('open-create-doctor').addEventListener('click', () => openModal('modal-create-doctor'));

document.getElementById('form-create-doctor').addEventListener('submit', async e => {
  e.preventDefault();
  clearErrors(['dr-name-err','dr-specialty-err']);
  const fullName  = document.getElementById('dr-name').value.trim();
  const specialty = document.getElementById('dr-specialty').value;
  let ok = true;
  if (!fullName || fullName.length < 3) { setErr('dr-name-err','Full name must be at least 3 characters'); ok=false; }
  if (!specialty) { setErr('dr-specialty-err','Please select a specialty'); ok=false; }
  if (!ok) return;
  try {
    await POST('/v1/doctors', { fullName, specialty });
    toast('success','Doctor Created',`${fullName} registered successfully`);
    closeModal('modal-create-doctor');
    e.target.reset();
    await loadAllAppointments();
    await loadDoctors();
    renderDoctors();
    updateBadges();
  } catch(err) { toast('error','Failed', err.message); }
});

/* ═══ Assign Specialty ═══ */
function openAssignSpecialty(id, name, current) {
  document.getElementById('assign-doctor-id').value = id;
  document.getElementById('assign-doctor-name').textContent = name;
  document.getElementById('assign-specialty').value = current || '';
  openModal('modal-assign-specialty');
}

document.getElementById('form-assign-specialty').addEventListener('submit', async e => {
  e.preventDefault();
  clearErrors(['assign-specialty-err']);
  const id       = document.getElementById('assign-doctor-id').value;
  const specialty= document.getElementById('assign-specialty').value;
  if (!specialty) { setErr('assign-specialty-err','Please select a specialty'); return; }
  try {
    await PATCH(`/v1/doctors/${id}/specialty`, { specialty });
    toast('success','Specialty Updated','Doctor specialty assigned successfully');
    closeModal('modal-assign-specialty');
    await loadAllAppointments();
    await loadDoctors();
    renderDoctors();
  } catch(err) { toast('error','Failed', err.message); }
});

/* ═══ Doctor Appointments Modal ═══ */
async function showDoctorApts(id, name) {
  document.getElementById('modal-doctor-apts-name').textContent = name;
  const body = document.getElementById('modal-doctor-apts-body');
  body.innerHTML = `<div class="skeleton-loader"><div class="sk-row"></div><div class="sk-row"></div></div>`;
  openModal('modal-doctor-apts');
  try {
    const list = await GET(`/v1/doctors/${id}/appointments`);
    if (!list.length) { body.innerHTML = emptyState('No appointments'); return; }
    body.innerHTML = aptTable(list, ['patient','time','status','actions']);
  } catch(err) { body.innerHTML = `<p style="padding:20px;color:var(--accent-red)">${err.message}</p>`; }
}

/* ═══ Patients ═══ */
async function loadPatients() {
  try { patients = await GET('/v1/patients'); }
  catch { patients = []; }
}

function renderPatients(filter='') {
  const tbody = document.getElementById('patients-tbody');
  const fl = filter.toLowerCase();
  const list = patients.filter(p =>
    !fl || p.fullName?.toLowerCase().includes(fl) || (p.phoneNumber||'').includes(fl)
  );
  document.getElementById('badge-patients').textContent = patients.length;
  if (!list.length) {
    tbody.innerHTML = `<tr><td colspan="4" style="text-align:center;padding:32px;color:var(--text-muted)">No patients found</td></tr>`;
    return;
  }
  tbody.innerHTML = list.map(p => `<tr>
    <td><div class="cell-name">${p.fullName}</div></td>
    <td><span style="font-family:monospace;font-size:.82rem">${p.phoneNumber||'—'}</span></td>
    <td class="cell-id">${p.id}</td>
    <td class="cell-actions">
      <button class="btn btn-secondary btn-sm" onclick="showPatientApts('${p.id}','${p.fullName}')">📅 History</button>
      <button class="btn btn-danger btn-sm" onclick="promptDeletePatient('${p.id}')">🗑 Delete</button>
    </td>
  </tr>`).join('');
}

document.getElementById('patient-search').addEventListener('input', e => renderPatients(e.target.value));

/* ═══ Create Patient ═══ */
document.getElementById('open-create-patient').addEventListener('click', () => openModal('modal-create-patient'));

document.getElementById('form-create-patient').addEventListener('submit', async e => {
  e.preventDefault();
  clearErrors(['pt-name-err','pt-phone-err']);
  const fullName    = document.getElementById('pt-name').value.trim();
  const phoneNumber = document.getElementById('pt-phone').value.trim();
  let ok = true;
  if (!fullName) { setErr('pt-name-err','Full name is required'); ok=false; }
  if (!phoneNumber || !/^[+]?[0-9]{10,}$/.test(phoneNumber)) { setErr('pt-phone-err','Enter a valid phone number (min 10 digits)'); ok=false; }
  if (!ok) return;
  try {
    await POST('/v1/patients', { fullName, phoneNumber });
    toast('success','Patient Registered',`${fullName} added successfully`);
    closeModal('modal-create-patient');
    e.target.reset();
    await loadAllAppointments();
    await loadPatients();
    renderPatients();
    updateBadges();
  } catch(err) { toast('error','Failed', err.message); }
});

/* ═══ Patient Appointments ═══ */
async function showPatientApts(id, name) {
  document.getElementById('modal-patient-apts-name').textContent = name;
  const body = document.getElementById('modal-patient-apts-body');
  body.innerHTML = `<div class="skeleton-loader"><div class="sk-row"></div><div class="sk-row"></div></div>`;
  openModal('modal-patient-apts');
  try {
    const list = await GET(`/v1/patients/${id}/appointments`);
    if (!list.length) { body.innerHTML = emptyState('No appointments'); return; }
    body.innerHTML = aptTable(list, ['doctor','time','status','actions']);
  } catch(err) { body.innerHTML = `<p style="padding:20px;color:var(--accent-red)">${err.message}</p>`; }
}

/* ═══ Appointments Page ═══ */
function renderAppointments() {
  const tbody = document.getElementById('appointments-tbody');
  document.getElementById('badge-appointments').textContent = appointments.length;
  if (!appointments.length) {
    tbody.innerHTML = `<tr><td colspan="6" style="text-align:center;padding:32px;color:var(--text-muted)">No appointments found</td></tr>`;
    return;
  }
  tbody.innerHTML = appointments.map(a => `<tr>
    <td><div class="cell-name">${a.patient?.fullName||'—'}</div></td>
    <td>${a.doctor?.fullName||'—'}</td>
    <td>${a.doctor?.specialty ? `<span class="specialty-badge badge-${a.doctor.specialty}">${specialtyLabel(a.doctor.specialty)}</span>` : '—'}</td>
    <td><div style="font-size:.82rem">${fmtDt(a.appointmentTime)}</div></td>
    <td><span class="status-badge status-${a.status}">${a.status}</span></td>
    <td class="cell-actions">
      ${a.status==='SCHEDULED' ? `<button class="btn btn-secondary btn-sm" onclick="promptComplete('${a.id}')">✓ Complete</button> <button class="btn btn-danger btn-sm" onclick="promptCancel('${a.id}')">✕ Cancel</button>` : ''}
    </td>
  </tr>`).join('');
}

/* Filters */
document.getElementById('apt-filter-btn').addEventListener('click', async () => {
  const status = document.getElementById('apt-status-filter').value;
  const date   = document.getElementById('apt-date-filter').value;
  let q = [];
  if (status) q.push(`status=${status}`);
  if (date)   q.push(`date=${date}`);
  await loadAllAppointments(q.length ? '?'+q.join('&') : '');
  renderAppointments();
});

document.getElementById('apt-clear-filter').addEventListener('click', async () => {
  document.getElementById('apt-status-filter').value = '';
  document.getElementById('apt-date-filter').value   = '';
  await loadAllAppointments();
  renderAppointments();
});

/* ═══ Book Appointment ═══ */
document.getElementById('open-book-apt').addEventListener('click', () => navigateTo('book'));
document.getElementById('nav-book').addEventListener('click', e => { e.preventDefault(); navigateTo('book'); });

function populateBookSelects() {
  const ps = document.getElementById('book-patient');
  const ds = document.getElementById('book-doctor');
  const cur_p = ps.value, cur_d = ds.value;
  ps.innerHTML = '<option value="">Select a patient...</option>' +
    patients.map(p => `<option value="${p.id}">${p.fullName} — ${p.phoneNumber||''}</option>`).join('');
  ds.innerHTML = '<option value="">Select a doctor...</option>' +
    doctors.map(d => `<option value="${d.id}">${d.fullName} (${specialtyLabel(d.specialty||'')})</option>`).join('');
  if (cur_p) ps.value = cur_p;
  if (cur_d) ds.value = cur_d;
  // default datetime = now+1h
  const min = new Date(); min.setHours(min.getHours()+1);
  const dtInput = document.getElementById('book-time');
  if (!dtInput.value) dtInput.value = min.toISOString().slice(0,16);
}

document.getElementById('book-form').addEventListener('submit', async e => {
  e.preventDefault();
  clearErrors(['book-patient-err','book-doctor-err','book-time-err']);
  const patientId       = document.getElementById('book-patient').value;
  const doctorId        = document.getElementById('book-doctor').value;
  const appointmentTime = document.getElementById('book-time').value;
  let ok = true;
  if (!patientId) { setErr('book-patient-err','Please select a patient'); ok=false; }
  if (!doctorId)  { setErr('book-doctor-err','Please select a doctor'); ok=false; }
  if (!appointmentTime) { setErr('book-time-err','Please select a date and time'); ok=false; }
  else if (new Date(appointmentTime) <= new Date()) { setErr('book-time-err','Appointment must be in the future'); ok=false; }
  if (!ok) return;

  const btn = document.getElementById('book-submit');
  btn.disabled = true; btn.textContent = 'Booking…';
  try {
    await POST('/v1/appointments', { patientId, doctorId, appointmentTime });
    toast('success','Appointment Booked','Visit scheduled successfully');
    document.getElementById('book-form').reset();
    await loadAllAppointments();
    renderDashboardAppointments();
    renderSpecialtyChart();
    renderAppointments();
    navigateTo('appointments');
  } catch(err) { toast('error','Booking Failed', err.message); }
  finally { btn.disabled=false; btn.innerHTML='<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg> Confirm Booking'; }
});

/* ═══ Cancel Appointment ═══ */
function promptCancel(id) {
  document.getElementById('cancel-appointment-id').value = id;
  openModal('modal-cancel-confirm');
}

document.getElementById('confirm-cancel-btn').addEventListener('click', async () => {
  const id = document.getElementById('cancel-appointment-id').value;
  try {
    await POST(`/v1/appointments/${id}/cancel`);
    toast('success','Cancelled','Appointment cancelled');
    closeModal('modal-cancel-confirm');
    await refreshAfterAppointmentChange();
  } catch(err) { toast('error','Failed', err.message); }
});

function promptComplete(id) {
  document.getElementById('complete-appointment-id').value = id;
  openModal('modal-complete-confirm');
}

document.getElementById('confirm-complete-btn').addEventListener('click', async () => {
  const id = document.getElementById('complete-appointment-id').value;
  try {
    await POST(`/v1/appointments/${id}/complete`);
    toast('success','Completed','Appointment marked as completed');
    closeModal('modal-complete-confirm');
    await refreshAfterAppointmentChange();
  } catch(err) { toast('error','Failed', err.message); }
});

function promptDeletePatient(id) {
  const p = patients.find(x => x.id === id);
  document.getElementById('delete-patient-id').value = id;
  document.getElementById('delete-patient-display-name').textContent = p?.fullName || id;
  openModal('modal-delete-patient-confirm');
}

document.getElementById('confirm-delete-patient-btn').addEventListener('click', async () => {
  const id = document.getElementById('delete-patient-id').value;
  try {
    await DEL(`/v1/patients/${id}`);
    toast('success','Patient removed','Patient and all of their appointments were deleted.');
    closeModal('modal-delete-patient-confirm');
    closeModal('modal-patient-apts');
    await refreshAfterEntityDelete();
  } catch(err) { toast('error','Failed', err.message); }
});

function promptDeleteDoctor(id) {
  const d = doctors.find(x => x.id === id);
  document.getElementById('delete-doctor-id').value = id;
  document.getElementById('delete-doctor-display-name').textContent = d?.fullName || id;
  openModal('modal-delete-doctor-confirm');
}

document.getElementById('confirm-delete-doctor-btn').addEventListener('click', async () => {
  const id = document.getElementById('delete-doctor-id').value;
  try {
    await DEL(`/v1/doctors/${id}`);
    toast('success','Doctor removed','Doctor and all of their appointments were deleted.');
    closeModal('modal-delete-doctor-confirm');
    closeModal('modal-doctor-apts');
    await refreshAfterEntityDelete();
  } catch(err) { toast('error','Failed', err.message); }
});

async function refreshAfterAppointmentChange() {
  await loadAllAppointments();
  renderAppointments();
  renderDashboardAppointments();
  renderSpecialtyChart();
  await loadDashboard();
}

async function refreshAfterEntityDelete() {
  await loadAllAppointments();
  await loadDoctors();
  await loadPatients();
  updateBadges();
  const active = document.querySelector('.page.active')?.id;
  if (active === 'page-doctors') renderDoctors(document.getElementById('doctor-search')?.value || '');
  if (active === 'page-patients') renderPatients(document.getElementById('patient-search')?.value || '');
  if (active === 'page-appointments') renderAppointments();
  if (active === 'page-dashboard') await loadDashboard();
  if (active === 'page-book') populateBookSelects();
}

/* ═══ Global Search ═══ */
document.getElementById('global-search').addEventListener('input', e => {
  const v = e.target.value.trim();
  if (!v) return;
  // search doctors
  const matchDr = doctors.filter(d => d.fullName?.toLowerCase().includes(v.toLowerCase()));
  const matchPt = patients.filter(p => p.fullName?.toLowerCase().includes(v.toLowerCase()));
  if (matchDr.length) { navigateTo('doctors'); renderDoctors(v); }
  else if (matchPt.length) { navigateTo('patients'); renderPatients(v); }
});

/* ═══ Refresh ═══ */
document.getElementById('refresh-btn').addEventListener('click', async () => {
  const btn = document.getElementById('refresh-btn');
  btn.classList.add('spinning');
  await initData();
  btn.classList.remove('spinning');
  toast('info','Refreshed','Data reloaded');
});

/* ═══ Helpers ═══ */
function fmtDt(dt) {
  if (!dt) return '—';
  return new Date(dt).toLocaleString('en-GB', { day:'2-digit', month:'short', year:'numeric', hour:'2-digit', minute:'2-digit' });
}

function specialtyLabel(s) {
  return { CARDIOLOGY:'❤️ Cardiology', DENTIST:'🦷 Dentist', THERAPIST:'🧠 Therapist' }[s] || s || '—';
}

function emptyState(msg) {
  return `<div class="empty-state">
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1"><circle cx="12" cy="12" r="10"/><line x1="8" y1="12" x2="16" y2="12"/></svg>
    <p>${msg}</p></div>`;
}

function aptTable(list, cols) {
  const headers = { patient:'Patient', doctor:'Doctor', time:'Date & Time', status:'Status', cancel:'Action', actions:'Actions' };
  const actionCell = (a) => a.status === 'SCHEDULED'
    ? `<button class="btn btn-secondary btn-sm" onclick="promptComplete('${a.id}')">✓</button> <button class="btn btn-danger btn-sm" onclick="promptCancel('${a.id}')">✕</button>`
    : '—';
  return `<table class="data-table"><thead><tr>${cols.map(c=>`<th>${headers[c]||c}</th>`).join('')}</tr></thead>
    <tbody>${list.map(a=>`<tr>
      ${cols.includes('patient') ? `<td class="cell-name">${a.patient?.fullName||a.patientId||'—'}</td>` : ''}
      ${cols.includes('doctor')  ? `<td class="cell-name">${a.doctor?.fullName||a.doctorId||'—'}</td>` : ''}
      ${cols.includes('time')    ? `<td style="font-size:.78rem;color:var(--text-muted)">${fmtDt(a.appointmentTime)}</td>` : ''}
      ${cols.includes('status')  ? `<td><span class="status-badge status-${a.status}">${a.status}</span></td>` : ''}
      ${cols.includes('actions') ? `<td>${actionCell(a)}</td>` : ''}
      ${cols.includes('cancel') && !cols.includes('actions') ? `<td>${a.status==='SCHEDULED'?`<button class="btn btn-danger btn-sm" onclick="promptCancel('${a.id}')">✕</button>`:'—'}</td>` : ''}
    </tr>`).join('')}</tbody></table>`;
}

function setErr(id, msg) { const el=document.getElementById(id); if(el) el.textContent=msg; }
function clearErrors(ids) { ids.forEach(id => setErr(id,'')); }

function updateBadges() {
  document.getElementById('badge-doctors').textContent     = doctors.length;
  document.getElementById('badge-patients').textContent    = patients.length;
  document.getElementById('badge-appointments').textContent= appointments.length;
}

/* ═══ Init ═══ */
async function initData() {
  await loadAllAppointments();
  await loadDoctors();
  await loadPatients();
  updateBadges();
  await loadDashboard();
}

(async () => {
  await checkStatus();
  await initData();
  navigateTo('dashboard');
})();
