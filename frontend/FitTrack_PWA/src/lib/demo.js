import { isoToday, shiftDate, round } from './format';
import catalog from '../data/catalog.json';
export const seededProducts = catalog;
const today = isoToday();
let serial = 100;
const portion = (pid, quantityGrams, mealType, entryDate) => {
    const p = seededProducts.find(v => v.id === pid);
    const ratio = quantityGrams / 100;
    return { id: serial++, productId: pid, productName: p.name, mealType, entryDate, quantityGrams,
        calories: round(p.calories * ratio), proteinGrams: round(p.protein * ratio), fatGrams: round(p.fat * ratio), carbGrams: round(p.carbs * ratio) };
};
const foods = [];
for (let i = 13; i >= 0; i--) {
    const date = shiftDate(today, -i);
    foods.push(portion(4, 65, 'BREAKFAST', date));
    foods.push(portion(5, 170, 'BREAKFAST', date));
    foods.push(portion(1, 190 + (i % 3) * 20, 'LUNCH', date));
    foods.push(portion(2, 120 + (i % 4) * 10, 'LUNCH', date));
    foods.push(portion(i % 2 ? 8 : 7, 140, 'DINNER', date));
    foods.push(portion(i % 3 ? 3 : 6, 110, 'SNACK', date));
}
export const initialDemo = {
    user: { id: 1, email: 'demo@fittrack.app', gender: 'MALE', age: 22, height: 181, weight: 79.4, displayName: 'Артем' },
    goal: { goalType: 'MAINTAIN', activityLevel: 'MODERATELY_ACTIVE', targetWeight: 78 },
    products: seededProducts,
    foods,
    waters: Array.from({ length: 14 }, (_, i) => ({ id: i + 1, entryDate: shiftDate(today, -i), amountMl: 1600 + (i % 4) * 250 })),
    activities: Array.from({ length: 8 }, (_, i) => ({ id: i + 1, entryDate: shiftDate(today, -i * 2), activityType: i % 3 === 0 ? 'STRENGTH_TRAINING' : i % 2 ? 'WALKING' : 'RUNNING', durationMinutes: 45 + (i % 4) * 10, caloriesBurned: 250 + (i % 4) * 80 })),
    weights: Array.from({ length: 10 }, (_, i) => ({ id: i + 1, entryDate: shiftDate(today, -(9 - i) * 2), weightKg: round(80.8 - i * 0.16) })),
};
export function makeDemoAnalytics(demo, start, end) {
    const rows = [];
    let d = start;
    while (d <= end) {
        const fs = demo.foods.filter(x => x.entryDate === d);
        const wa = demo.waters.filter(x => x.entryDate === d);
        const act = demo.activities.filter(x => x.entryDate === d);
        const ws = demo.weights.filter(x => x.entryDate === d);
        rows.push({ date: d, caloriesConsumed: round(fs.reduce((n, x) => n + x.calories, 0)), proteinGrams: round(fs.reduce((n, x) => n + x.proteinGrams, 0)), fatGrams: round(fs.reduce((n, x) => n + x.fatGrams, 0)), carbsGrams: round(fs.reduce((n, x) => n + x.carbGrams, 0)), waterMl: wa.reduce((n, x) => n + x.amountMl, 0), activityCount: act.length, activityMinutes: act.reduce((n, x) => n + x.durationMinutes, 0), caloriesBurned: act.reduce((n, x) => n + x.caloriesBurned, 0), weightKg: ws.length ? ws[ws.length - 1].weightKg : null });
        d = shiftDate(d, 1);
    }
    const sum = k => rows.reduce((n, x) => n + Number(x[k] || 0), 0);
    const ws = rows.filter(r => r.weightKg !== null);
    return { dailyStats: rows, summary: { startDate: start, endDate: end, totalCaloriesConsumed: round(sum('caloriesConsumed')), averageDailyCalories: round(sum('caloriesConsumed') / rows.length), totalProteinGrams: round(sum('proteinGrams')), totalFatGrams: round(sum('fatGrams')), totalCarbsGrams: round(sum('carbsGrams')), totalWaterMl: sum('waterMl'), totalActivities: sum('activityCount'), totalActivityMinutes: sum('activityMinutes'), totalCaloriesBurned: round(sum('caloriesBurned')), startingWeightKg: ws[0]?.weightKg ?? null, endingWeightKg: ws.at(-1)?.weightKg ?? null, weightChangeKg: ws.length ? round(ws.at(-1).weightKg - ws[0].weightKg) : null } };
}
