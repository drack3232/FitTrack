import React from 'react';
import { Link } from 'react-router-dom';
import {
  ArrowRight,
  ArrowUpRight,
  CheckCircle2,
  Droplets,
  Flame,
  Footprints,
  HeartPulse,
  Scale,
  Sparkles,
  Utensils,
} from 'lucide-react';

import { useFit } from '../lib/FitContext';
import { fmt, mealLabel, percent, prettyDate } from '../lib/format';
import {
  Card,
  Macro,
  MiniArea,
  Ring,
  SectionHead,
  StatCard,
} from '../components/UI';

function QuickLink({ to, icon: Icon, title, note, color }) {
  return (
    <Link to={to} className="quick-link">
      <span className={`quick-icon ${color}`}>
        <Icon size={21} />
      </span>
      <span>
        <b>{title}</b>
        <small>{note}</small>
      </span>
      <ArrowUpRight size={17} className="quick-arrow" />
    </Link>
  );
}

export default function Dashboard() {
  const { data, date, analytics, user } = useFit();
  const today = analytics?.dailyStats?.find((item) => item.date === date) || {};
  const calories = Number(today.caloriesConsumed || 0);
  const calorieTarget = Number(
    data.calculations?.dailyCalorieTarget ??
    data.calculations?.targetCalories ??
    data.calculations?.dailyCalories ??
    2400,
  );

  const week = analytics?.dailyStats || [];
  const foods = data.foods.filter((entry) => entry.entryDate === date);
  const currentWeight = [...data.weights]
    .filter((entry) => entry.entryDate <= date)
    .sort((a, b) => a.entryDate.localeCompare(b.entryDate))
    .at(-1)?.weightKg;

  return (
    <div className="page dashboard-page">
      <SectionHead
        eyebrow="ТВІЙ ПРОГРЕС ПОЧИНАЄТЬСЯ ТУТ"
        title={
          <>
            Привіт, {user?.displayName || data.user?.displayName || 'атлете'}{' '}
            <span className="hello-wave">✳</span>
          </>
        }
        subtitle={`${prettyDate(date, {
          weekday: 'long',
          day: 'numeric',
          month: 'long',
        })}. Час ставати сильнішою версією себе.`}
        action={
          <Link to="/analytics" className="btn btn-outline">
            Вся аналітика <ArrowUpRight size={17} />
          </Link>
        }
      />

      <div className="hero-grid">
        <section className="hero-card">
          <div className="hero-copy">
            <div className="hero-pill">
              <Sparkles size={14} /> ТВОЯ ЩОДЕННА ЦІЛЬ
            </div>
            <h2>
              Кожен день
              <br />
              <em>має значення.</em>
            </h2>
            <p>Слідкуй за балансом енергії та поступово досягай своєї форми.</p>
            <Link to="/nutrition" className="hero-cta">
              Додати прийом їжі <ArrowRight size={18} />
            </Link>
          </div>
          <div className="hero-ring">
            <Ring
              value={calories}
              max={calorieTarget}
              label={`із ${fmt(calorieTarget)} ккал`}
              size={212}
            />
            <span className="hero-ring-note">
              {Math.max(0, Math.round(calorieTarget - calories))} ккал залишилось
            </span>
          </div>
          <div className="hero-glow" />
        </section>

        <Card
          className="macro-card"
          title="Баланс нутрієнтів"
          subtitle="Показники за сьогодні"
          action={
            <Link to="/nutrition" className="icon-link" aria-label="Перейти до харчування">
              <ArrowUpRight size={18} />
            </Link>
          }
        >
          <div className="macro-stack">
            <Macro label="Білки" current={today.proteinGrams || 0} target={160} color="var(--purple)" />
            <Macro label="Жири" current={today.fatGrams || 0} target={80} color="var(--orange)" />
            <Macro label="Вуглеводи" current={today.carbsGrams || 0} target={260} color="var(--blue)" />
          </div>
          <div className="macro-callout">
            <CheckCircle2 size={16} /> Дані оновлюються з твого щоденника
          </div>
        </Card>
      </div>

      <div className="stats-grid">
        <StatCard
          icon={Flame}
          label="З'їдено калорій"
          value={fmt(calories)}
          unit="ккал"
          foot={`${Math.round(percent(calories, calorieTarget))}% від денної цілі`}
          color="orange"
        />
        <StatCard
          icon={Droplets}
          label="Випито води"
          value={fmt(Number(today.waterMl || 0) / 1000, 2)}
          unit="л"
          foot={`${Math.round(percent(today.waterMl || 0, 2500))}% від 2,5 л`}
          color="blue"
        />
        <StatCard
          icon={HeartPulse}
          label="Активність"
          value={fmt(today.activityMinutes || 0)}
          unit="хв"
          foot={`${today.activityCount || 0} тренувань за день`}
          color="purple"
        />
        <StatCard
          icon={Scale}
          label="Поточна вага"
          value={currentWeight != null ? fmt(currentWeight, 1) : '—'}
          unit="кг"
          foot="Останнє вимірювання"
          color="accent"
        />
      </div>

      <div className="dashboard-lower">
        <Card
          className="chart-card"
          title="Динаміка калорій"
          subtitle={`Споживання за останні ${week.length} днів`}
          action={
            <Link to="/analytics" className="text-link">
              Докладніше <ArrowRight size={15} />
            </Link>
          }
        >
          <div className="chart-spacer"><MiniArea data={week} /></div>
          <div className="chart-footer">
            <span className="legend"><i /> Спожиті калорії</span>
            <span>
              Середнє <b>{fmt(analytics?.summary?.averageDailyCalories)} ккал</b>
            </span>
          </div>
        </Card>

        <Card className="quick-card" title="Швидкі дії" subtitle="Все необхідне під рукою">
          <div className="quick-grid">
            <QuickLink to="/nutrition" icon={Utensils} title="Записати їжу" note="Калорії та БЖВ" color="accent" />
            <QuickLink to="/water" icon={Droplets} title="Додати воду" note="Підтримуй баланс" color="blue" />
            <QuickLink to="/activity" icon={Footprints} title="Тренування" note="Рухайся щодня" color="purple" />
            <QuickLink to="/weight" icon={Scale} title="Нова вага" note="Фіксуй прогрес" color="orange" />
          </div>
        </Card>
      </div>

      <Card
        className="recent-card"
        title="Сьогодні у щоденнику"
        subtitle="Останні записи"
        action={
          <Link to="/nutrition" className="text-link">
            Усі записи <ArrowRight size={15} />
          </Link>
        }
      >
        <div className="recent-list">
          {foods.slice(0, 5).map((entry) => (
            <div className="recent-row" key={entry.id}>
              <div className="food-dot"><Utensils size={17} /></div>
              <div className="recent-name">
                <strong>{entry.productName}</strong>
                <small>
                  {mealLabel[entry.mealType] || entry.mealType} · {fmt(entry.quantityGrams)} г
                </small>
              </div>
              <div className="recent-cal">
                <strong>{fmt(entry.calories)} ккал</strong>
                <small>{fmt(entry.proteinGrams, 1)} г білка</small>
              </div>
            </div>
          ))}
          {foods.length === 0 && (
            <p className="muted">Сьогодні ще не було записів. Почни зі сніданку!</p>
          )}
        </div>
      </Card>
    </div>
  );
}
