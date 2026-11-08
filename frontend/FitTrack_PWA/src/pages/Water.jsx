import React, { useState } from 'react';
import { Droplets, GlassWater, Plus, Trash2 } from 'lucide-react';

import { useFit } from '../lib/FitContext';
import { fmt, percent } from '../lib/format';
import {
  Button,
  Card,
  Empty,
  MiniBars,
  Notice,
  Progress,
  SectionHead,
  useTask,
} from '../components/UI';

const DAILY_WATER_TARGET = 2500;
const QUICK_AMOUNTS = [150, 250, 500, 750];

export default function Water() {
  const { data, date, analytics, mutate } = useFit();
  const { busy, error, run } = useTask();
  const [customAmount, setCustomAmount] = useState('350');

  const entries = data.waters.filter((item) => item.entryDate === date);
  const totalMl = entries.reduce((sum, item) => sum + Number(item.amountMl || 0), 0);

  function addWater(amount) {
    return run(() => mutate('water', 'create', {
      entryDate: date,
      amountMl: Number(amount),
    }));
  }

  function deleteWater(id) {
    return run(() => mutate('water', 'delete', null, id));
  }

  return (
    <div className="page">
      <SectionHead
        eyebrow="ВОДНИЙ БАЛАНС"
        title="Пий воду. Відновлюйся."
        subtitle="Трекер гідратації, який допомагає формувати здорову звичку."
      />

      <div className="water-layout">
        <div className="water-column">
          <Card className="water-hero">
            <div className="water-wave">
              <Droplets size={72} strokeWidth={1.15} />
            </div>
            <span className="kicker">ВОДА СЬОГОДНІ</span>
            <div className="water-value">
              {fmt(totalMl / 1000, 2)} <span>л</span>
            </div>
            <span className="muted">
              з {fmt(DAILY_WATER_TARGET / 1000, 1)} л щоденної цілі
            </span>
            <Progress
              value={totalMl}
              max={DAILY_WATER_TARGET}
              color="var(--blue)"
              height={11}
            />
            <div className="water-progress-foot">
              <span>{fmt(percent(totalMl, DAILY_WATER_TARGET))}% виконано</span>
              <span>{fmt(Math.max(0, DAILY_WATER_TARGET - totalMl))} мл залишилось</span>
            </div>
          </Card>

          <Card title="Швидко додати" subtitle="Один дотик — один запис">
            <div className="quick-water">
              {QUICK_AMOUNTS.map((amount) => (
                <button
                  key={amount}
                  type="button"
                  disabled={busy}
                  onClick={() => addWater(amount)}
                >
                  <GlassWater size={21} />
                  <b>+{amount}</b>
                  <small>мл</small>
                </button>
              ))}
            </div>
            <div className="water-custom">
              <input
                aria-label="Кількість води в мілілітрах"
                type="number"
                min="1"
                value={customAmount}
                onChange={(event) => setCustomAmount(event.target.value)}
              />
              <Button
                onClick={() => addWater(customAmount)}
                disabled={busy || Number(customAmount) <= 0}
              >
                <Plus size={17} /> Додати
              </Button>
            </div>
            {error && <Notice type="error">{error}</Notice>}
          </Card>
        </div>

        <div className="water-column">
          <Card
            title="Гідратація за період"
            subtitle="Статистика щоденного споживання"
            className="chart-only-desktop"
          >
            <MiniBars
              data={analytics?.dailyStats || []}
              dataKey="waterMl"
              color="var(--blue)"
              height={250}
            />
          </Card>

          <Card title="Записи за сьогодні" subtitle={`${entries.length} доданих порцій`}>
            <div className="water-history">
              {entries.length > 0 ? entries.map((entry) => (
                <div className="water-row" key={entry.id}>
                  <span className="water-row-icon"><Droplets size={17} /></span>
                  <strong>{fmt(entry.amountMl)} мл</strong>
                  <span className="muted">Порція води</span>
                  <button
                    type="button"
                    className="delete-icon"
                    title="Видалити"
                    disabled={busy}
                    onClick={() => deleteWater(entry.id)}
                  >
                    <Trash2 size={17} />
                  </button>
                </div>
              )) : (
                <Empty
                  title="Тут ще порожньо"
                  detail="Випий склянку води й додай перший запис."
                />
              )}
            </div>
          </Card>
        </div>
      </div>
    </div>
  );
}
