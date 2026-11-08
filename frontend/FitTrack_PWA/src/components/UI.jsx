import React, { useState } from 'react';
import {
  Area,
  AreaChart,
  Bar,
  BarChart,
  CartesianGrid,
  Line,
  LineChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts';
import {
  AlertCircle,
  ArrowUpRight,
  CalendarDays,
  Check,
  Inbox,
  LoaderCircle,
  X,
} from 'lucide-react';

import { fmt, percent, prettyDate, shortDate } from '../lib/format';
import { useFit } from '../lib/FitContext';

export function Card({ children, className = '', title, subtitle, action }) {
  return (
    <section className={`card ${className}`}>
      {title && (
        <div className="card-heading">
          <div>
            <h3>{title}</h3>
            {subtitle && <p>{subtitle}</p>}
          </div>
          {action}
        </div>
      )}
      {children}
    </section>
  );
}

export function Kicker({ children }) {
  return <span className="kicker">{children}</span>;
}

export function SectionHead({ eyebrow, title, subtitle, action }) {
  return (
    <div className="section-head">
      <div>
        {eyebrow && <Kicker>{eyebrow}</Kicker>}
        <h1>{title}</h1>
        {subtitle && <p>{subtitle}</p>}
      </div>
      {action && <div className="section-actions">{action}</div>}
    </div>
  );
}

export function Button({ children, variant = 'primary', className = '', ...props }) {
  return (
    <button type="button" className={`btn btn-${variant} ${className}`} {...props}>
      {children}
    </button>
  );
}

export function DateSelect() {
  const { date, setDate } = useFit();
  return (
    <label className="date-select">
      <CalendarDays size={17} />
      <input
        type="date"
        value={date}
        onChange={(event) => setDate(event.target.value)}
        aria-label="Дата щоденника"
      />
    </label>
  );
}

export function Progress({ value, max, color = 'var(--accent)', height = 8 }) {
  return (
    <div className="progress-track" style={{ height }}>
      <div
        className="progress-fill"
        style={{ width: `${percent(value, max)}%`, background: color }}
      />
    </div>
  );
}

export function Ring({ value, max, label, unit = 'ккал', size = 184 }) {
  const radius = 77;
  const circumference = 2 * Math.PI * radius;
  const completion = percent(value, max);

  return (
    <div className="ring-wrap" style={{ width: size, height: size }}>
      <svg className="ring-svg" viewBox="0 0 180 180" aria-hidden="true">
        <circle cx="90" cy="90" r={radius} className="ring-base" strokeWidth="11" fill="none" />
        <circle
          cx="90"
          cy="90"
          r={radius}
          className="ring-active"
          strokeWidth="11"
          strokeDasharray={`${(circumference * completion) / 100} ${circumference}`}
          strokeLinecap="round"
          transform="rotate(-90 90 90)"
          fill="none"
        />
      </svg>
      <div className="ring-text">
        <b>{fmt(value)}</b>
        <small>{label || `із ${fmt(max)} ${unit}`}</small>
      </div>
    </div>
  );
}

export function StatCard({ label, value, unit, icon: Icon, color = 'accent', foot, trend }) {
  return (
    <Card className="stat-card">
      <div className="stat-head">
        <span>{label}</span>
        {Icon && (
          <span className={`stat-icon ${color}`}>
            <Icon size={18} />
          </span>
        )}
      </div>
      <div className="stat-value">
        {value}
        <small>{unit}</small>
      </div>
      {foot && (
        <div className="stat-foot">
          {trend && <ArrowUpRight size={14} />}
          {foot}
        </div>
      )}
    </Card>
  );
}

export function Macro({ label, current, target, color }) {
  return (
    <div className="macro">
      <div className="macro-top">
        <span>{label}</span>
        <span>
          <strong>{fmt(current)}</strong> / {fmt(target)} г
        </span>
      </div>
      <Progress value={current} max={target} color={color} />
    </div>
  );
}

const axisTick = { fill: 'var(--muted)', fontSize: 11 };

function ChartTooltip({ active, payload, label, suffix = '' }) {
  if (!active || !payload?.length) return null;
  const title = typeof label === 'string' && label.includes('-')
    ? prettyDate(label)
    : label;

  return (
    <div className="chart-tip">
      <span>{title}</span>
      <strong>{fmt(payload[0].value, 1)}{suffix}</strong>
    </div>
  );
}

export function MiniArea({
  data,
  dataKey = 'caloriesConsumed',
  height = 210,
  color = 'var(--accent)',
  tickFormatter,
}) {
  const gradientId = `area-${dataKey}`;

  return (
    <div style={{ width: '100%', height }}>
      <ResponsiveContainer width="100%" height="100%">
        <AreaChart data={data} margin={{ top: 10, right: 2, left: -28, bottom: 0 }}>
          <defs>
            <linearGradient id={gradientId} x1="0" y1="0" x2="0" y2="1">
              <stop offset="0%" stopColor={color} stopOpacity={0.28} />
              <stop offset="100%" stopColor={color} stopOpacity={0} />
            </linearGradient>
          </defs>
          <CartesianGrid vertical={false} stroke="var(--line)" strokeDasharray="4 5" />
          <XAxis
            dataKey="date"
            tickFormatter={shortDate}
            tick={axisTick}
            axisLine={false}
            tickLine={false}
            tickMargin={12}
          />
          <YAxis
            tick={axisTick}
            axisLine={false}
            tickLine={false}
            tickFormatter={tickFormatter || ((value) => `${value}`)}
          />
          <Tooltip content={<ChartTooltip suffix={dataKey === 'waterMl' ? ' мл' : ' ккал'} />} />
          <Area
            type="monotone"
            dataKey={dataKey}
            stroke={color}
            strokeWidth={3}
            fill={`url(#${gradientId})`}
            activeDot={{ r: 5 }}
            connectNulls
          />
        </AreaChart>
      </ResponsiveContainer>
    </div>
  );
}

export function MiniBars({ data, dataKey = 'caloriesConsumed', height = 220, color = 'var(--accent)' }) {
  return (
    <div style={{ width: '100%', height }}>
      <ResponsiveContainer width="100%" height="100%">
        <BarChart data={data} margin={{ top: 10, right: 2, left: -28, bottom: 0 }}>
          <CartesianGrid vertical={false} stroke="var(--line)" strokeDasharray="4 5" />
          <XAxis
            dataKey="date"
            tickFormatter={shortDate}
            tick={axisTick}
            axisLine={false}
            tickLine={false}
            tickMargin={10}
          />
          <YAxis tick={axisTick} axisLine={false} tickLine={false} />
          <Tooltip content={<ChartTooltip suffix={dataKey === 'waterMl' ? ' мл' : ' ккал'} />} />
          <Bar dataKey={dataKey} fill={color} radius={[6, 6, 3, 3]} maxBarSize={38} />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
}

export function WeightLine({ data, height = 245 }) {
  const rows = data.filter((row) => row.weightKg != null);

  return (
    <div style={{ width: '100%', height }}>
      <ResponsiveContainer width="100%" height="100%">
        <LineChart data={rows} margin={{ top: 12, right: 12, left: -15, bottom: 0 }}>
          <CartesianGrid vertical={false} stroke="var(--line)" strokeDasharray="4 5" />
          <XAxis
            dataKey="date"
            tickFormatter={shortDate}
            tick={axisTick}
            axisLine={false}
            tickLine={false}
            tickMargin={10}
          />
          <YAxis
            domain={['dataMin - 1', 'dataMax + 1']}
            tick={axisTick}
            axisLine={false}
            tickLine={false}
          />
          <Tooltip content={<ChartTooltip suffix=" кг" />} />
          <Line
            type="monotone"
            dataKey="weightKg"
            stroke="var(--accent)"
            strokeWidth={3}
            dot={{ r: 4, fill: 'var(--accent)', stroke: 'var(--surface)', strokeWidth: 2 }}
            activeDot={{ r: 6 }}
          />
        </LineChart>
      </ResponsiveContainer>
    </div>
  );
}

export function Empty({
  title = 'Поки немає записів',
  detail = 'Додай перший запис, щоб побачити статистику.',
  icon: Icon = Inbox,
}) {
  return (
    <div className="empty">
      <Icon size={31} />
      <strong>{title}</strong>
      <p>{detail}</p>
    </div>
  );
}

export function Notice({ children, type = 'info' }) {
  return (
    <div className={`notice notice-${type}`}>
      <AlertCircle size={17} />
      <span>{children}</span>
    </div>
  );
}

export function Modal({ title, children, onClose, wide = false }) {
  return (
    <div
      className="modal-backdrop"
      onMouseDown={(event) => {
        if (event.target === event.currentTarget) onClose?.();
      }}
    >
      <section
        className={`modal ${wide ? 'modal-wide' : ''}`}
        role="dialog"
        aria-modal="true"
        aria-label={title}
      >
        <div className="modal-head">
          <h2>{title}</h2>
          <button className="icon-btn" type="button" aria-label="Закрити" onClick={onClose}>
            <X size={21} />
          </button>
        </div>
        {children}
      </section>
    </div>
  );
}

export function Input({ label, ...props }) {
  return (
    <label className="field">
      <span>{label}</span>
      <input {...props} />
    </label>
  );
}

export function Select({ label, options, ...props }) {
  return (
    <label className="field">
      <span>{label}</span>
      <select {...props}>
        {options.map((item) => (
          <option key={item.value} value={item.value}>{item.label}</option>
        ))}
      </select>
    </label>
  );
}

export function FormButtons({ busy, onCancel, submitText = 'Зберегти' }) {
  return (
    <div className="form-actions">
      <Button type="button" variant="ghost" onClick={onCancel}>
        Скасувати
      </Button>
      <Button type="submit" disabled={busy}>
        {busy ? <LoaderCircle size={17} className="spin" /> : <Check size={17} />}
        {submitText}
      </Button>
    </div>
  );
}

/** Common async action helper: disables the button and shows the actual error. */
export function useTask() {
  const { notify } = useFit();
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState('');

  async function run(action) {
    setBusy(true);
    setError('');

    try {
      await action();
      return true;
    } catch (caught) {
      setError(caught.message || 'Не вдалося виконати дію');
      return false;
    } finally {
      setBusy(false);
    }
  }

  return { busy, error, setError, run };
}
