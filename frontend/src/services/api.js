const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '';

export class ApiError extends Error {
  constructor(status, message) {
    super(message);
    this.status = status;
  }
}

async function request(path, options = {}) {
  const headers = {
    Accept: 'application/json',
    ...(options.body ? { 'Content-Type': 'application/json' } : {}),
    ...(options.headers || {}),
  };
  if (!headers.Authorization) delete headers.Authorization;

  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    headers,
  });

  if (response.status === 204) return null;
  const contentType = response.headers.get('content-type') || '';
  const payload = contentType.includes('application/json') ? await response.json() : null;

  if (!response.ok) {
    const message = payload?.message || (
      response.status === 401 ? 'Your session or credentials are invalid. Please log in again.' :
      response.status === 403 ? 'You do not have permission to access this page.' :
      response.status === 409 ? 'The selected resource is no longer available.' :
      'The request could not be completed.'
    );
    throw new ApiError(response.status, message);
  }
  return payload;
}

export const api = {
  health: () => request('/api/health'),
  me: (auth) => request('/api/auth/me', { headers: { Authorization: auth } }),
  register: (body) => request('/api/auth/register', { method: 'POST', body: JSON.stringify(body) }),
  restaurants: () => request('/api/restaurants'),
  restaurant: (id) => request(`/api/restaurants/${id}`),
  tables: (restaurantId) => request(`/api/restaurants/${restaurantId}/tables`),
  availability: (restaurantId, params) => request(
    `/api/restaurants/${restaurantId}/availability?${new URLSearchParams(params)}`,
  ),
  reservations: (customerId, auth) => request(`/api/reservations?customerId=${customerId}`, { headers: { Authorization: auth } }),
  createReservation: (body, auth) => request('/api/reservations', { method: 'POST', headers: { Authorization: auth }, body: JSON.stringify(body) }),
  cancelReservation: (id, customerId, auth) => request(`/api/reservations/${id}/cancel?customerId=${customerId}`, { method: 'PATCH', headers: { Authorization: auth } }),
  managerDashboard: (auth) => request('/api/manager/dashboard', { headers: { Authorization: auth } }),
  managerRestaurants: (auth) => request('/api/manager/restaurants', { headers: { Authorization: auth } }),
  createRestaurant: (body, auth) => request('/api/manager/restaurants', { method: 'POST', headers: { Authorization: auth }, body: JSON.stringify(body) }),
  updateRestaurant: (id, body, auth) => request(`/api/manager/restaurants/${id}`, { method: 'PUT', headers: { Authorization: auth }, body: JSON.stringify(body) }),
  deleteRestaurant: (id, auth) => request(`/api/manager/restaurants/${id}`, { method: 'DELETE', headers: { Authorization: auth } }),
  managerTables: (restaurantId, auth) => request(`/api/manager/restaurants/${restaurantId}/tables`, { headers: { Authorization: auth } }),
  createTable: (restaurantId, body, auth) => request(`/api/manager/restaurants/${restaurantId}/tables`, { method: 'POST', headers: { Authorization: auth }, body: JSON.stringify(body) }),
  updateTable: (id, body, auth) => request(`/api/manager/tables/${id}`, { method: 'PUT', headers: { Authorization: auth }, body: JSON.stringify(body) }),
  deactivateTable: (id, auth) => request(`/api/manager/tables/${id}/deactivate`, { method: 'PATCH', headers: { Authorization: auth } }),
  managerReservations: (auth) => request('/api/manager/reservations', { headers: { Authorization: auth } }),
  updateReservationStatus: (id, status, auth) => request(`/api/manager/reservations/${id}/status`, { method: 'PATCH', headers: { Authorization: auth }, body: JSON.stringify({ status }) }),
};
