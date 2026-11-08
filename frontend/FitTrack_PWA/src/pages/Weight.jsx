import React, { useState } from 'react';
import { CalendarDays, Plus, Scale, Target, Trash2, TrendingDown } from 'lucide-react';

import { useFit } from '../lib/FitContext';
import { fmt, prettyDate, round } from '../lib/format';
import {
  Button,
  Card,
  Empty,
  FormButtons,
  Input,
  Modal,
  Notice,
  SectionHead,
  StatCard,
  WeightLine,
  useTask,
} from '../components/UI';

export default function Weight() {
  const { data, date, analytics, mutate } = useFit();
  const [formOpen, setFormOpen] = useState(false);
  const { busy, error, run } = useTask();

  const sorted = [...data.weights].sort((a, b) => a.entryDate.localeCompare(b.entryDate));
  const current = sorted.at(-1)?.weightKg;
  const starting = sorted[0]?.weightKg;
  const target = data.goal?.targetWeight;
  const delta = current != null && starting != null
    ? round(current - starting)
    : null;

  return (
    <div className="page">
      <SectionHead
        eyebrow="КРОК ЗА КРОКОМ"
        title="Прогрес ваги"
        subtitle="Бач зміни за тижні та місяці, а не окремі коливання."
        action={
          <Button onClick={() => setFormOpen(true)}>
            <Plus size={18} /> Записати вагу
          </Button>
        }
      />

      <div className="stats-grid">
        <StatCard
          label="Остання вага"
          value={current != null ? fmt(current, 1) : '—'}
          unit="кг"
          icon={Scale}
          color="accent"
          foot="Останній запис"
        />
        <StatCard
          label="Зміна від першого"
          value={delta != null ? `${delta > 0 ? '+' : ''}${fmt(delta, 1)}` : '—'}
          unit="кг"
          icon={TrendingDown}
          color="blue"
          foot="За історією"
        />
        <StatCard
          label="Цільова вага"
          value={target != null ? fmt(target, 1) : '—'}
          unit="кг"
          icon={Target}
          color="purple"
          foot="Налаштування цілей"
        />
        <StatCard
          label="Вимірювань"
          value={sorted.length}
          icon={CalendarDays}
          color="orange"
          foot="Усього записів"
        />
      </div>

      <div className="analytics-grid">
        <Card
          title="Зміна ваги"
          subtitle="Динаміка за обраний період"
          className="chart-only-desktop"
        >
          <WeightLine data={analytics?.dailyStats || []} height={265} />
        </Card>

        <Card title="Історія вимірювань" subtitle="Від найновішого до найстарішого">
          <div className="weight-history">
            {sorted.length > 0 ? [...sorted].reverse().slice(0, 15).map((entry) => (
              <div className="weight-row" key={entry.id}>
                <span>
                  <b>{fmt(entry.weightKg, 1)} кг</b>
                  <small>{prettyDate(entry.entryDate)}</small>
                </span>
                <button
                  type="button"
                  className="delete-icon"
                  title="Видалити"
                  disabled={busy}
                  onClick={() => run(() => mutate('weight', 'delete', null, entry.id))}
                >
                  <Trash2 size={17} />
                </button>
              </div>
            )) : (
              <Empty title="Немає вимірювань" />
            )}
          </div>
          {error && <Notice type="error">{error}</Notice>}
        </Card>
      </div>

      {formOpen && (
        <WeightForm onClose={() => setFormOpen(false)} currentWeight={current} initialDate={date} />
      )}
    </div>
  );
}

function WeightForm({ onClose, currentWeight, initialDate }) {
  const { mutate } = useFit();
  const { busy, error, run } = useTask();
  const [weightKg, setWeightKg] = useState(String(currentWeight || 80));
  const [entryDate, setEntryDate] = useState(initialDate);

  async function submit(event) {
    event.preventDefault();
    const success = await run(() => mutate('weight', 'create', {
      entryDate,
      weightKg: Number(weightKg),
    }));
    if (success) onClose();
  }

  return (
    <Modal title="Записати вагу" onClose={onClose}>
      <form onSubmit={submit}>
        <Input
          label="Вага, кг"
          type="number"
          min="20"
          max="400"
          step="0.1"
          value={weightKg}
          onChange={(event) => setWeightKg(event.target.value)}
          required
        />
        <Input
          label="Дата вимірювання"
          type="date"
          value={entryDate}
          onChange={(event) => setEntryDate(event.target.value)}
          required
        />
        <p className="form-hint">Для точнішого відстеження вимірюй вагу в однакових умовах.</p>
        {error && <Notice type="error">{error}</Notice>}
        <FormButtons busy={busy} onCancel={onClose} />
      </form>
    </Modal>
  );
}
