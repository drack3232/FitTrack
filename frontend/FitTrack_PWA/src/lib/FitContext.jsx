import React, {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
} from 'react';

import {
  api,
  extractToken,
  fetchAllProducts,
  getBearer,
  setBearer,
  unwrapArray,
} from './api';
import { initialDemo, makeDemoAnalytics, seededProducts } from './demo';
import { isoToday, round, shiftDate } from './format';
import { applyTheme, getStoredTheme, normalizeTheme } from './themes';

const FitContext = createContext(null);
const DEMO_KEY = 'fittrack_demo_v2';
const MODE_KEY = 'fittrack_mode';


const categoryByName = new Map(
  seededProducts.map((item) => [item.name.trim().toLocaleLowerCase('uk'), item.category]),
);

function withDisplayCategory(product) {
  if (!product || product.category) return product;

  const name = String(product.name || '').trim().toLocaleLowerCase('uk');
  return {
    ...product,
    category: categoryByName.get(name) || (product.ownerId != null ? 'Власні продукти' : 'Інші'),
  };
}

const emptyState = {
  user: null,
  goal: null,
  calculations: null,
  products: [],
  foods: [],
  waters: [],
  activities: [],
  weights: [],
};

function readDemoData() {
  try {
    const stored = localStorage.getItem(DEMO_KEY);
    if (!stored) return initialDemo;

    const saved = JSON.parse(stored);
    const own = saved.products?.filter((product) => product.ownerId != null) || [];

    return {
      ...initialDemo,
      ...saved,
      products: [...seededProducts, ...own.filter((product) =>
        !seededProducts.some((reference) => reference.id === product.id)
      )],
    };
  } catch {
    return initialDemo;
  }
}

function readSavedUser() {
  try {
    return JSON.parse(sessionStorage.getItem('fittrack_user') || 'null');
  } catch {
    return null;
  }
}

function combineByDate(previous, incoming, date) {
  return [
    ...previous.filter((item) => item.entryDate !== date),
    ...incoming,
  ];
}

function getDefaultMode() {
  const selected = localStorage.getItem(MODE_KEY);
  if (selected) return selected === 'demo';
  return import.meta.env.VITE_DEMO_MODE !== 'false';
}

export function FitProvider({ children }) {
  const [theme, setThemeState] = useState(getStoredTheme);
  const [demoMode, setDemoMode] = useState(getDefaultMode);
  const [data, setData] = useState(() =>
    getDefaultMode() ? readDemoData() : emptyState
  );
  const [user, setUser] = useState(readSavedUser);
  const [date, setDate] = useState(isoToday);
  const [period, setPeriod] = useState(7);
  const [analytics, setAnalytics] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [toast, setToast] = useState('');

  const setTheme = useCallback((value) => {
    setThemeState(normalizeTheme(value));
  }, []);

  const notify = useCallback((message) => {
    setToast(message);
  }, []);

  useEffect(() => {
    applyTheme(theme);
    try {
      localStorage.setItem('fittrack_theme', theme);
    } catch {
    }
  }, [theme]);

  useEffect(() => {
    const syncTheme = (event) => {
      if (event.key === 'fittrack_theme') {
        setThemeState(normalizeTheme(event.newValue));
      }
    };

    window.addEventListener('storage', syncTheme);
    return () => window.removeEventListener('storage', syncTheme);
  }, []);

  useEffect(() => {
    if (demoMode) {
      localStorage.setItem(DEMO_KEY, JSON.stringify(data));
    }
  }, [data, demoMode]);

  useEffect(() => {
    if (!toast) return undefined;
    const timeout = setTimeout(() => setToast(''), 4000);
    return () => clearTimeout(timeout);
  }, [toast]);

  function switchMode(useDemo) {
    setData(useDemo ? readDemoData() : emptyState);
    setDemoMode(useDemo);
    localStorage.setItem(MODE_KEY, useDemo ? 'demo' : 'live');
    setError('');
  }

  function logout() {
    setBearer('');
    sessionStorage.removeItem('fittrack_user');

    setData(emptyState);
    setUser(null);
    setAnalytics(null);
  }

  async function login(payload) {
    const response = await api.login(payload);
    const token = extractToken(response);

    if (!token) {
      throw new Error('Login не повернув JWT. Перевір extractToken у src/lib/api.js.');
    }

    setBearer(token);
    const profile = {
      ...response,
      email: response.email || payload.email,
    };

    setUser(profile);
    setData((previous) => ({ ...previous, user: profile }));
    sessionStorage.setItem('fittrack_user', JSON.stringify(profile));
    return profile;
  }

  async function register(payload) {
    await api.register(payload);
    return login({ email: payload.email, password: payload.password });
  }

  const reload = useCallback(async () => {
    if (demoMode) {
      setError('');
      return;
    }

    if (!getBearer()) return;

    setLoading(true);
    setError('');

    const startDate = shiftDate(date, -period + 1);
    const jobs = [
      ['profile', api.profile()],
      ['products', fetchAllProducts()],
      ['foods', api.foods(date)],
      ['waters', api.water(date)],
      ['activities', api.activities(date)],
      ['weights', api.weights()],
      ['goal', api.goals()],
      ['analytics', api.analytics(startDate, date)],
      ['calculations', api.calculations()],
    ];

    try {
      const results = await Promise.allSettled(jobs.map(([, job]) => job));
      const fulfilled = {};
      const failures = [];

      results.forEach((result, index) => {
        const key = jobs[index][0];
        if (result.status === 'fulfilled') {
          fulfilled[key] = result.value;
        } else {
          const status = result.reason?.status;
          if (!(status === 404 && ['goal', 'calculations'].includes(key))) {
            failures.push(`${key}: ${result.reason?.message || 'Помилка'}`);
          }
        }
      });

      setData((previous) => ({
        ...previous,

        ...(fulfilled.profile && {
          user: fulfilled.profile,
        }),
        ...(fulfilled.products && { products: fulfilled.products.map(withDisplayCategory) }),
        ...(fulfilled.foods && {
          foods: combineByDate(previous.foods, unwrapArray(fulfilled.foods), date),
        }),
        ...(fulfilled.waters && {
          waters: combineByDate(previous.waters, unwrapArray(fulfilled.waters), date),
        }),
        ...(fulfilled.activities && {
          activities: combineByDate(previous.activities, unwrapArray(fulfilled.activities), date),
        }),
        ...(fulfilled.weights && { weights: unwrapArray(fulfilled.weights) }),
        ...(fulfilled.goal && { goal: fulfilled.goal }),
        ...(fulfilled.calculations && { calculations: fulfilled.calculations }),
      }));

      if (fulfilled.analytics) setAnalytics(fulfilled.analytics);
      if (failures.length) setError(failures.join(' · '));
    } catch (unexpectedError) {
      setError(unexpectedError.message || 'Помилка завантаження даних');
    } finally {
      setLoading(false);
    }
  }, [date, demoMode, period]);

  useEffect(() => {
    reload();
  }, [reload, user]);

  async function mutate(kind, operation, payload, id) {
    if (demoMode) {
      if (kind === 'goal') {
        setData((current) => ({ ...current, goal: payload }));
        notify('Ціль збережено');
        return payload;
      }

      const listKey = {
        food: 'foods',
        water: 'waters',
        activity: 'activities',
        weight: 'weights',
        product: 'products',
      }[kind];

      if (!listKey) throw new Error(`Невідомий тип запису: ${kind}`);

      if (operation === 'delete') {
        setData((current) => ({
          ...current,
          [listKey]: current[listKey].filter((item) => String(item.id) !== String(id)),
        }));
        notify('Запис видалено');
        return null;
      }

      let entry = payload;
      if (kind === 'food') {
        const product = data.products.find((item) => String(item.id) === String(payload.productId));
        if (!product) throw new Error('Вибери продукт із каталогу');

        const scale = Number(payload.quantityGrams) / 100;
        entry = {
          ...payload,
          productName: product.name,
          calories: round(product.calories * scale),
          proteinGrams: round(product.protein * scale),
          fatGrams: round(product.fat * scale),
          carbGrams: round(product.carbs * scale),
        };
      }

      if (kind === 'product') {
        if (Number(payload.protein) + Number(payload.fat) + Number(payload.carbs) > 100) {
          throw new Error('Сума БЖВ не може перевищувати 100 г');
        }

        const collision = data.products.some((item) =>
          payload.barcode && String(item.barcode) === String(payload.barcode)
        );
        if (collision) throw new Error('Продукт із таким штрихкодом уже існує');
        entry = { ...payload, ownerId: data.user?.id || 1 };
      }

      const saved = {
        ...entry,
        id: id || `custom-${Date.now()}`,
        entryDate: entry.entryDate || date,
      };

      setData((current) => ({
        ...current,
        [listKey]: operation === 'update'
          ? current[listKey].map((item) => item.id === id ? saved : item)
          : [saved, ...current[listKey]],
      }));
      notify('Запис збережено');
      return saved;
    }

    const createCalls = {
      food: api.addFood,
      water: api.addWater,
      activity: api.addActivity,
      weight: api.addWeight,
      product: api.createProduct,
      goal: api.saveGoals,
    };
    const updateCalls = {
      food: api.updateFood,
      water: api.updateWater,
      activity: api.updateActivity,
      weight: api.updateWeight,
      product: api.updateProduct,
    };
    const deleteCalls = {
      food: api.deleteFood,
      water: api.deleteWater,
      activity: api.deleteActivity,
      weight: api.deleteWeight,
      product: api.deleteProduct,
    };

    const result = operation === 'delete'
      ? await deleteCalls[kind](id)
      : operation === 'update'
        ? await updateCalls[kind](id, payload)
        : await createCalls[kind](payload);

    notify('Збережено на сервері');
    await reload();
    return result;
  }

  const currentAnalytics = useMemo(() =>
    demoMode
      ? makeDemoAnalytics(data, shiftDate(date, -period + 1), date)
      : analytics,
  [analytics, data, date, demoMode, period]);

  const value = {
    theme,
    setTheme,
    demoMode,
    switchMode,
    data,
    user,
    date,
    setDate,
    period,
    setPeriod,
    analytics: currentAnalytics,
    loading,
    error,
    setError,
    toast,
    notify,
    mutate,
    reload,
    login,
    register,
    logout,
    hasToken: Boolean(getBearer()),
  };

  return <FitContext.Provider value={value}>{children}</FitContext.Provider>;
}

export function useFit() {
  const context = useContext(FitContext);
  if (!context) throw new Error('FitProvider is missing');
  return context;
}
