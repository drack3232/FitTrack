import React from 'react';
import {
  Cell,
  Pie,
  PieChart,
  ResponsiveContainer,
  Tooltip,
} from 'recharts';
import {
  CalendarRange,
  Droplets,
  Flame,
  Footprints,
  Scale,
  TrendingUp,
} from 'lucide-react';

import { useFit } from '../lib/FitContext';
import { fmt, prettyDate } from '../lib/format';
import {
  Card,
  MiniArea,
  MiniBars,
  SectionHead,
  StatCard,
  WeightLine,
} from '../components/UI';

const macroDefinitions = [
  { key: 'totalProteinGrams', label: 'Білки', color: 'var(--purple)' },
  { key: 'totalFatGrams', label: 'Жири', color: 'var(--orange)' },
  { key: 'totalCarbsGrams', label: 'Вуглеводи', color: 'var(--blue)' },
];

export default function Analytics() {
  const { analytics, period, setPeriod, date } = useFit();
  const summary = analytics?.summary || {};
  const daily = analytics?.dailyStats || [];

  return (
    <div className="page">
      <SectionHead
        eyebrow="АНАЛІТИКА ТВОЄЇ ФОРМИ"
        title="Весь прогрес. Наочно."
        subtitle="Різні графіки для десктопа та компактні показники на телефоні."
        action={
          <div className="segmented">
            {[7, 14, 30, 90].map((days) => (
              <button
                key={days}
                type="button"
                className={period === days ? 'selected' : ''}
                onClick={() => setPeriod(days)}
              >
                {days} дн.
              </button>
            ))}
          </div>
        }
      />

      <div className="insight-head">
        <span>
          <CalendarRange size={16} />
          {summary.startDate ? prettyDate(summary.startDate) : '—'} — {prettyDate(date)}
        </span>
        <span><TrendingUp size={16} /> Аналітика за записами користувача</span>
      </div>

      <div className="stats-grid">
        <StatCard
          label="Середньодобово"
          value={fmt(summary.averageDailyCalories)}
          unit="ккал"
          icon={Flame}
          color="orange"
          foot="Калорій на день"
        />
        <StatCard
          label="Випито води"
          value={fmt((summary.totalWaterMl || 0) / 1000, 1)}
          unit="л"
          icon={Droplets}
          color="blue"
          foot="За весь період"
        />
        <StatCard
          label="Тренувань"
          value={fmt(summary.totalActivities)}
          icon={Footprints}
          color="purple"
          foot={`${fmt(summary.totalActivityMinutes)} хв активності`}
        />
        <StatCard
          label="Зміна ваги"
          value={summary.weightChangeKg == null ? '—' : fmt(summary.weightChangeKg, 1)}
          unit="кг"
          icon={Scale}
          color="accent"
          foot="За наявними вимірами"
        />
      </div>

      <div className="analytics-grid chart-only-desktop">
        <Card title="Калорії" subtitle="Плавний графік динаміки за днями">
          <MiniArea data={daily} height={255} />
        </Card>

        <Card title="Споживання води" subtitle="Стовпчикова діаграма у мілілітрах">
          <MiniBars data={daily} dataKey="waterMl" height={255} color="var(--blue)" />
        </Card>

        <Card title="Маса тіла" subtitle="Лінійний графік вимірювань">
          <WeightLine data={daily} height={255} />
        </Card>

        <Card title="Калорії тренувань" subtitle="Порівняння витрат енергії">
          <MiniBars
            data={daily}
            dataKey="caloriesBurned"
            height={255}
            color="var(--purple)"
          />
        </Card>

        <Card title="БЖВ за період" subtitle="Кругова діаграма макронутрієнтів">
          <MacroDonut summary={summary} />
        </Card>
      </div>

      <Card title="Підсумок нутрієнтів" subtitle="Значення за обраний період">
        <div className="summary-macros">
          {macroDefinitions.map(({ key, label, color }) => (
            <div className="summary-row" key={key}>
              <span className="legend">
                <i style={{ background: color }} /> {label}
              </span>
              <strong>{fmt(summary[key], 1)} г</strong>
            </div>
          ))}

          <div className="summary-row">
            <span className="legend">
              <i style={{ background: 'var(--accent)' }} /> Калорії
            </span>
            <strong>{fmt(summary.totalCaloriesConsumed)} ккал</strong>
          </div>
        </div>
        <p className="muted small spaced-top">
          Дні без записів враховуються як нуль у середньому.
          Витрати активності не віднімаються вдруге від TDEE.
        </p>
      </Card>
    </div>
  );
}

function MacroDonut({ summary }) {
  const data = macroDefinitions.map(({ key, label, color }) => ({
    name: label,
    value: Number(summary[key] || 0),
    color,
  }));

  if (data.every((item) => item.value === 0)) {
    return <p className="muted">Додай харчування, щоб побачити діаграму.</p>;
  }

  return (
    <div className="macro-donut">
      <ResponsiveContainer width="100%" height={240}>
        <PieChart>
          <Pie
            data={data}
            cx="50%"
            cy="50%"
            dataKey="value"
            nameKey="name"
            innerRadius={65}
            outerRadius={92}
            paddingAngle={3}
          >
            {data.map((item) => (
              <Cell key={item.name} fill={item.color} />
            ))}
          </Pie>
          <Tooltip formatter={(value) => `${fmt(value, 1)} г`} />
        </PieChart>
      </ResponsiveContainer>
      <div className="donut-legend">
        {data.map((item) => (
          <span className="legend" key={item.name}>
            <i style={{ background: item.color }} /> {item.name}
          </span>
        ))}
      </div>
    </div>
  );
}
