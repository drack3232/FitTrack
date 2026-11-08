import React, { useState } from 'react';
import { Clock3, Dumbbell, Flame, Footprints, Plus, Trash2 } from 'lucide-react';

import { useFit } from '../lib/FitContext';
import { activityLabel, fmt } from '../lib/format';
import {
  Button,
  Card,
  Empty,
  FormButtons,
  Input,
  MiniBars,
  Modal,
  Notice,
  SectionHead,
  Select,
  StatCard,
  useTask,
} from '../components/UI';

export default function Activity() {
  const { data, date, analytics, mutate } = useFit();
  const [formOpen, setFormOpen] = useState(false);
  const { busy, error, run } = useTask();

  const entries = data.activities.filter((item) => item.entryDate === date);
  const totalMinutes = entries.reduce(
    (sum, item) => sum + Number(item.durationMinutes || 0), 0,
  );
  const caloriesBurned = entries.reduce(
    (sum, item) => sum + Number(item.caloriesBurned || 0), 0,
  );

  return (
    <div className="page">
      <SectionHead
        eyebrow="РУХ — ЦЕ ПРОГРЕС"
        title="Фізична активність"
        subtitle="Фіксуй тренування та відстежуй свою послідовність."
        action={
          <Button onClick={() => setFormOpen(true)}>
            <Plus size={18} /> Додати тренування
          </Button>
        }
      />

      <div className="stats-grid">
        <StatCard label="Тренувань сьогодні" value={entries.length} icon={Dumbbell} color="purple" foot="Записаних активностей" />
        <StatCard label="Тривалість" value={fmt(totalMinutes)} unit="хв" icon={Clock3} color="blue" foot="Час у русі" />
        <StatCard label="Витрачено" value={fmt(caloriesBurned)} unit="ккал" icon={Flame} color="orange" foot="За ручними записами" />
        <StatCard label="За період" value={fmt(analytics?.summary?.totalActivities)} icon={Footprints} color="accent" foot="Активностей у статистиці" />
      </div>

      <div className="analytics-grid">
        <Card
          title="Динаміка активності"
          subtitle="Витрати енергії за період"
          className="chart-only-desktop"
        >
          <MiniBars
            data={analytics?.dailyStats || []}
            dataKey="caloriesBurned"
            color="var(--purple)"
            height={270}
          />
        </Card>

        <Card title="Тренування сьогодні" subtitle="Список твоїх активностей">
          {entries.length > 0 ? entries.map((entry) => (
            <div className="activity-row" key={entry.id}>
              <span className="activity-round"><Dumbbell size={19} /></span>
              <div>
                <strong>{activityLabel[entry.activityType] || entry.activityType}</strong>
                <small>{fmt(entry.durationMinutes)} хв · {fmt(entry.caloriesBurned)} ккал</small>
              </div>
              <button
                type="button"
                className="delete-icon"
                title="Видалити"
                disabled={busy}
                onClick={() => run(() => mutate('activity', 'delete', null, entry.id))}
              >
                <Trash2 size={17} />
              </button>
            </div>
          )) : (
            <Empty title="Поки немає тренувань" detail="Запиши першу активність за сьогодні." />
          )}
          {error && <Notice type="error">{error}</Notice>}
        </Card>
      </div>

      {formOpen && <ActivityForm onClose={() => setFormOpen(false)} />}
    </div>
  );
}

function ActivityForm({ onClose }) {
  const { date, mutate } = useFit();
  const { busy, error, run } = useTask();
  const [type, setType] = useState('STRENGTH_TRAINING');
  const [duration, setDuration] = useState('60');
  const [calories, setCalories] = useState('350');

  async function submit(event) {
    event.preventDefault();
    const success = await run(() => mutate('activity', 'create', {
      activityType: type,
      entryDate: date,
      durationMinutes: Number(duration),
      caloriesBurned: Number(calories),
    }));
    if (success) onClose();
  }

  return (
    <Modal title="Нове тренування" onClose={onClose}>
      <form onSubmit={submit}>
        <Select
          label="Тип активності"
          value={type}
          onChange={(event) => setType(event.target.value)}
          options={Object.entries(activityLabel).map(([value, label]) => ({ value, label }))}
        />
        <div className="form-grid">
          <Input label="Тривалість, хв" type="number" min="1" required value={duration} onChange={(event) => setDuration(event.target.value)} />
          <Input label="Витрачено, ккал" type="number" min="0" step="0.1" required value={calories} onChange={(event) => setCalories(event.target.value)} />
        </div>
        <p className="form-hint">Калорії — орієнтовне значення. Не віднімай їх удруге від TDEE.</p>
        {error && <Notice type="error">{error}</Notice>}
        <FormButtons busy={busy} onCancel={onClose} submitText="Зберегти активність" />
      </form>
    </Modal>
  );
}
