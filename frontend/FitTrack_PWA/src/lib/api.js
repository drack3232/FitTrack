
const BASE_URL = (import.meta.env.VITE_API_BASE_URL || '').replace(/\/$/, '');
const API_PREFIX = `${BASE_URL}/api/v1`;
const TOKEN_KEY = 'fittrack_token';

let bearer = sessionStorage.getItem(TOKEN_KEY) || '';

export function setBearer(token) {
  bearer = token || '';

  if (bearer) {
    sessionStorage.setItem(TOKEN_KEY, bearer);
  } else {
    sessionStorage.removeItem(TOKEN_KEY);
  }
}

export function getBearer() {
  return bearer;
}

export class ApiError extends Error {
  constructor(message, status = 0) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
  }
}

async function request(path, options = {}) {
  const {
    method = 'GET',
    body,
    params,
    signal,
  } = options;

  const search = params
    ? new URLSearchParams(
      Object.entries(params).filter(([, value]) => value !== '' && value != null),
    ).toString()
    : '';

  const url = `${API_PREFIX}${path}${search ? `?${search}` : ''}`;
  let response;

  try {
    response = await fetch(url, {
      method,
      headers: {
        ...(body !== undefined ? { 'Content-Type': 'application/json' } : {}),
        ...(bearer ? { Authorization: `Bearer ${bearer}` } : {}),
      },
      ...(body !== undefined ? { body: JSON.stringify(body) } : {}),
      signal,
    });
  } catch {
    throw new ApiError('Немає з’єднання зі Spring Boot. Перевір Backend і Vite Proxy.');
  }

  const isJson = response.headers.get('content-type')?.includes('json');
  const data = isJson
    ? await response.json().catch(() => null)
    : await response.text().catch(() => null);

  if (!response.ok) {
    const message =
      (typeof data === 'object' && (data?.message || data?.detail || data?.error)) ||
      `HTTP ${response.status}: ${path}`;

    throw new ApiError(message, response.status);
  }

  return data;
}

export const api = {
  register: (data) => request('/users/register', { method: 'POST', body: data }),
  login: (data) => request('/users/login', { method: 'POST', body: data }),

  profile: () => request('/users/me'),


  goals: () => request('/goals/me'),
  saveGoals: (data) => request('/goals/me', { method: 'PUT', body: data }),
  calculations: () => request('/goals/me/calculations'),

  products: (query = '', page = 0, size = 100) => request(
    query ? '/products/search' : '/products',
    { params: { ...(query ? { query } : {}), page, size } },
  ),
  productByBarcode: (barcode) => request(
    `/products/barcode/${encodeURIComponent(barcode)}`,
  ),
  createProduct: (data) => request('/products', { method: 'POST', body: data }),
  updateProduct: (id, data) => request(`/products/${id}`, { method: 'PUT', body: data }),
  deleteProduct: (id) => request(`/products/${id}`, { method: 'DELETE' }),

  foods: (date) => request('/nutrition/foods', { params: { date } }),
  addFood: (data) => request('/nutrition/foods', { method: 'POST', body: data }),
  updateFood: (id, data) => request(`/nutrition/foods/${id}`, { method: 'PUT', body: data }),
  deleteFood: (id) => request(`/nutrition/foods/${id}`, { method: 'DELETE' }),

  water: (date) => request('/nutrition/water', { params: { date } }),
  addWater: (data) => request('/nutrition/water', { method: 'POST', body: data }),
  updateWater: (id, data) => request(`/nutrition/water/${id}`, { method: 'PUT', body: data }),
  deleteWater: (id) => request(`/nutrition/water/${id}`, { method: 'DELETE' }),
  nutritionSummary: (date) => request('/nutrition/summary', { params: { date } }),

  activities: (date) => request('/activities', { params: { date } }),
  addActivity: (data) => request('/activities', { method: 'POST', body: data }),
  updateActivity: (id, data) => request(`/activities/${id}`, { method: 'PUT', body: data }),
  deleteActivity: (id) => request(`/activities/${id}`, { method: 'DELETE' }),

  weights: () => request('/weights'),
  weightsPeriod: (startDate, endDate) => request(
    '/weights/period', { params: { startDate, endDate } },
  ),
  weightProgress: () => request('/weights/progress'),
  addWeight: (data) => request('/weights', { method: 'POST', body: data }),
  updateWeight: (id, data) => request(`/weights/${id}`, { method: 'PUT', body: data }),
  deleteWeight: (id) => request(`/weights/${id}`, { method: 'DELETE' }),

  analytics: (startDate, endDate) => request(
    '/analytics', { params: { startDate, endDate } },
  ),
};

export async function fetchAllProducts(pageSize = 100, maxPages = 25) {
  const all = [];

  for (let page = 0; page < maxPages; page += 1) {
    const response = await api.products('', page, pageSize);
    const batch = unwrapProducts(response);
    all.push(...batch);

    if (Array.isArray(response)) break;
    if (response?.last === true || batch.length < pageSize) break;
  }

  return [...new Map(all.map((product) => [product.id, product])).values()];
}

export function unwrapProducts(response) {
  return Array.isArray(response)
    ? response
    : response?.content || response?.products || [];
}

export function unwrapArray(response) {
  return Array.isArray(response) ? response : response?.content || [];
}

export function extractToken(response) {
  return (
    response?.token ||
    response?.jwt ||
    response?.accessToken ||
    response?.access_token ||
    response?.jwtToken ||
    response?.data?.token ||
    null
  );
}
