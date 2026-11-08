// Centralised presentation themes. Data is entirely client-side and does not affect API calls.
export const DEFAULT_THEME = 'graphite';

export const THEMES = [
  {
    id: 'graphite', name: 'Graphite + Lime',
    description: 'Темний графіт із фірмовим лаймом',
    category: 'Темна', isLight: false,
    colors: ['#101516', '#c5f25d', '#202829'],
    browserColor: '#101516',
  },
  {
    id: 'warm', name: 'Warm Minimal',
    description: 'Спокійний світлий мінімалізм',
    category: 'Світла', isLight: true,
    colors: ['#f4f5f0', '#4a805c', '#e9eee7'],
    browserColor: '#f4f5f0',
  },
  {
    id: 'midnight', name: 'Midnight Blue',
    description: 'Стриманий темно-синій простір',
    category: 'Темна', isLight: false,
    colors: ['#0e1423', '#85b5fa', '#1d2940'],
    browserColor: '#0e1423',
  },
  {
    id: 'violet', name: 'Obsidian Violet',
    description: 'Аметистовий акцент на антрациті',
    category: 'Темна', isLight: false,
    colors: ['#11101d', '#b9a0ff', '#28253b'],
    browserColor: '#11101d',
  },
  {
    id: 'ember', name: 'Carbon Ember',
    description: 'Теплий карбон та помаранчевий',
    category: 'Темна', isLight: false,
    colors: ['#171412', '#ffad70', '#2b2420'],
    browserColor: '#171412',
  },
  {
    id: 'ocean', name: 'Deep Ocean',
    description: 'Насичений океан і м’ятна бірюза',
    category: 'Темна', isLight: false,
    colors: ['#09191e', '#67e0c7', '#17343a'],
    browserColor: '#09191e',
  },
  {
    id: 'arctic', name: 'Arctic Light',
    description: 'Чистий холодний білий та синій',
    category: 'Світла', isLight: true,
    colors: ['#eff5f8', '#277db8', '#dceaf1'],
    browserColor: '#eff5f8',
  },
  {
    id: 'rose', name: 'Rose Quartz',
    description: 'М’яке світле оформлення з рожевим',
    category: 'Світла', isLight: true,
    colors: ['#faf4f6', '#aa527d', '#f4e5ec'],
    browserColor: '#faf4f6',
  },
];

export function normalizeTheme(value) {
  return THEMES.some(item => item.id === value) ? value : DEFAULT_THEME;
}

export function getStoredTheme() {
  try { return normalizeTheme(localStorage.getItem('fittrack_theme')); }
  catch { return DEFAULT_THEME; }
}

export function applyTheme(id) {
  const theme = THEMES.find(item => item.id === normalizeTheme(id)) || THEMES[0];
  document.documentElement.dataset.theme = theme.id;
  document.documentElement.style.colorScheme = theme.isLight ? 'light' : 'dark';
  document.querySelector('meta[name="theme-color"]')?.setAttribute('content', theme.browserColor);
  return theme.id;
}
