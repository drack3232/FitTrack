import React, { useEffect, useState } from 'react';
import { Save, Target } from 'lucide-react';

import { useFit } from '../lib/FitContext';
import { api } from '../lib/api';
import { activityLevelLabel, fmt, goalLabel } from '../lib/format';
import {
  Button,
  Card,
  Input,
  Notice,
  SectionHead,
  Select,
  useTask,
} from '../components/UI';

const activityMultipliers = {
  SEDENTARY: 1.2,
  LIGHTLY_ACTIVE: 1.375,
  MODERATELY_ACTIVE: 1.55,
  VERY_ACTIVE: 1.725,
  EXTRA_ACTIVE: 1.9,
};

const goalAdjustments = {
  LOSE: -300,
  MAINTAIN: 0,
  GAIN: 300,
};

/** Values shown before Backend calculations are approximate preview values. */
function estimateDailyEnergy(user, goal) {
  if (!user?.weight || !user?.height || !user?.age || !user?.gender) {
    return null;
  }

  const sexCoefficient = user.gender === 'FEMALE' ? -161 : 5;
  const bmr =
    10 * Number(user.weight) +
    6.25 * Number(user.height) -
    5 * Number(user.age) +
    sexCoefficient;

  const tdee = bmr * (activityMultipliers[goal.activityLevel] || 1.2);
  const dailyCalories = tdee + (goalAdjustments[goal.goalType] || 0);
  const bmi = Number(user.weight) / (Number(user.height) / 100) ** 2;

  return {
    bmr: Math.round(bmr),
    tdee: Math.round(tdee),
    dailyCalories: Math.round(dailyCalories),
    bmi: Math.round(bmi * 10) / 10,
  };
}

export default function Goals() {
  const { data, demoMode, mutate } = useFit();
  const { busy, error, run } = useTask();
  const [goal, setGoal] = useState({
    goalType: 'MAINTAIN',
    activityLevel: 'MODERATELY_ACTIVE',
    targetWeight: 78,
  });
  const [calculations, setCalculations] = useState(null);

  useEffect(() => {
    setGoal({
      goalType: data.goal?.goalType || 'MAINTAIN',
      activityLevel: data.goal?.activityLevel || 'MODERATELY_ACTIVE',
      targetWeight: data.goal?.targetWeight ?? 78,
    });
  }, [data.goal]);

  useEffect(() => {
    if (demoMode) {
      setCalculations(null);
      return;
    }

    let cancelled = false;
    api.calculations()
      .then((response) => {
        if (!cancelled) setCalculations(response);
      })
      .catch(() => {
        if (!cancelled) setCalculations(null);
      });

    return () => {
      cancelled = true;
    };
  }, [demoMode, data.goal]);

  const fallback = estimateDailyEnergy(data.user, goal);
  const values = calculations || fallback;

  async function submit(event) {
    event.preventDefault();
    await run(() => mutate('goal', 'create', {
      ...goal,
      targetWeight: Number(goal.targetWeight),
    }));
  }

  return (
    <div className="page">
      <SectionHead
        eyebrow="ПОБУДУЙ СВІЙ РЕЗУЛЬТАТ"
        title="Цілі та розрахунки"
        subtitle="Налаштовуй свою мету та рівень фізичної активності."
      />

      <div className="goals-grid">
        <Card title="Твоя мета" subtitle="Персональні налаштування" className="goals-form-card">
          <form onSubmit={submit}>
            <div className="goal-choices">
              {Object.entries(goalLabel).map(([key, label]) => (
                <button
                  key={key}
                  type="button"
                  className={`goal-choice ${goal.goalType === key ? 'is-selected' : ''}`}
                  onClick={() => setGoal((previous) => ({ ...previous, goalType: key }))}
                >
                  <Target size={18} />
                  <span>{label}</span>
                </button>
              ))}
            </div>

            <Select
              label="Фізична активність"
              value={goal.activityLevel}
              onChange={(event) => setGoal((previous) => ({
                ...previous,
                activityLevel: event.target.value,
              }))}
              options={Object.entries(activityLevelLabel).map(([value, label]) => ({
                value,
                label,
              }))}
            />

            <Input
              label="Бажана вага, кг"
              type="number"
              min="20"
              max="400"
              step="0.1"
              required
              value={goal.targetWeight}
              onChange={(event) => setGoal((previous) => ({
                ...previous,
                targetWeight: event.target.value,
              }))}
            />

            {error && <Notice type="error">{error}</Notice>}
            <Button type="submit" disabled={busy}>
              <Save size={18} /> Зберегти ціль
            </Button>
          </form>
        </Card>

        <Card
          className="goal-calcs"
          title="Орієнтовні показники"
          subtitle="На основі формули Mifflin–St Jeor"
        >
          <div className="calcs-hero">
            <span className="kicker">ДОБОВА КАЛОРІЙНІСТЬ</span>
            <strong>
              {values ? fmt(values.dailyCalorieTarget ?? values.dailyCalories) : '—'}
              <small> ккал</small>
            </strong>
            <p>Орієнтовне значення, а не медична рекомендація.</p>
          </div>

          <div className="calc-row">
            <span>BMR <small>Базовий обмін</small></span>
            <b>{values ? fmt(values.bmr) : '—'} ккал</b>
          </div>
          <div className="calc-row">
            <span>TDEE <small>З урахуванням активності</small></span>
            <b>{values ? fmt(values.tdee) : '—'} ккал</b>
          </div>
          <div className="calc-row">
            <span>BMI <small>Індекс маси тіла</small></span>
            <b>{values ? fmt(values.bmi, 1) : '—'}</b>
          </div>
        </Card>
      </div>

      <Notice>
        Заповни зріст, вагу, вік і стать у профілі для розрахунків.
        Калорії тренувань не потрібно автоматично віднімати вдруге від TDEE.
      </Notice>
    </div>
  );
}
