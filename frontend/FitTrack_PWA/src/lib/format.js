export const isoToday = () => {
    const d = new Date();
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
};
export const shiftDate = (date, days) => {
    const d = new Date(`${date}T12:00:00`);
    d.setDate(d.getDate() + days);
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
};
export const prettyDate = (date, opts = { day: 'numeric', month: 'long' }) => new Date(`${date}T12:00:00`).toLocaleDateString('uk-UA', opts);
export const shortDate = date => new Date(`${date}T12:00:00`).toLocaleDateString('uk-UA', { day: '2-digit', month: '2-digit' });
export const fmt = (num, digits = 0) => Number(num || 0).toLocaleString('uk-UA', { maximumFractionDigits: digits });
export const round = (n, places = 1) => Math.round(Number(n || 0) * 10 ** places) / 10 ** places;
export const percent = (value, max) => max > 0 ? Math.min(100, Math.max(0, value / max * 100)) : 0;
export const initials = (s = 'FT') => s.split(/[\s@._-]+/).filter(Boolean).slice(0, 2).map(v => v[0].toUpperCase()).join('');
export const mealLabel = { BREAKFAST: 'Сніданок', LUNCH: 'Обід', DINNER: 'Вечеря', SNACK: 'Перекус' };
export const activityLabel = { WALKING: 'Ходьба', RUNNING: 'Біг', CYCLING: 'Велосипед', SWIMMING: 'Плавання', STRENGTH_TRAINING: 'Силове тренування', OTHER: 'Інша активність' };
export const goalLabel = { LOSE: 'Схуднення', MAINTAIN: 'Підтримання', GAIN: 'Набір маси' };
export const activityLevelLabel = { SEDENTARY: 'Мінімальна', LIGHTLY_ACTIVE: 'Легка', MODERATELY_ACTIVE: 'Середня', VERY_ACTIVE: 'Висока', EXTRA_ACTIVE: 'Дуже висока' };
