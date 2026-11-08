import React from 'react';
import { Check, Database, LogOut, ShieldCheck, Wifi, WifiOff } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

import { useFit } from '../lib/FitContext';
import { THEMES } from '../lib/themes';
import { initials } from '../lib/format';
import { Button, Card, Notice, SectionHead } from '../components/UI';

function ThemeCard({ item, selected, onChoose }) {
  const [background, accent, surface] = item.colors;

  return (
    <button
      type="button"
      className={`theme-choice ${selected ? 'chosen' : ''}`}
      aria-label={`Обрати тему ${item.name}`}
      aria-pressed={selected}
      onClick={() => onChoose(item.id)}
    >
      <div className="theme-swatches" style={{ background }} aria-hidden="true">
        <div className="theme-preview-header">
          <i style={{ background: accent }} />
        </div>
        <div className="theme-preview-main">
          <div className="theme-preview-side" style={{ background: surface }}>
            <i style={{ background: accent }} />
            <i style={{ background: accent, opacity: 0.35 }} />
            <i style={{ background: accent, opacity: 0.2 }} />
          </div>
          <div className="theme-preview-body">
            <div className="theme-preview-hero" style={{ background: surface }}>
              <div className="theme-preview-circle" style={{ borderColor: accent }} />
            </div>
            <div className="theme-preview-panels">
              <i style={{ background: surface }} />
              <i style={{ background: surface }} />
              <i style={{ background: accent, opacity: 0.7 }} />
            </div>
          </div>
        </div>
      </div>

      <span className="theme-text">
        <b>{item.name}</b>
        <small>{item.description}</small>
        <span className="theme-category">
          {item.category}
          {item.id === 'graphite' ? ' · Оригінальна' : ''}
        </span>
      </span>
      <span className="theme-check" aria-hidden="true">
        {selected && <Check size={17} />}
      </span>
    </button>
  );
}

function ProfileFact({ label, value, unit = '' }) {
  return (
    <div>
      <span>{label}</span>
      <b>{value == null ? '—' : `${value}${unit ? ` ${unit}` : ''}`}</b>
    </div>
  );
}

export default function Profile() {
  const { user, data, theme, setTheme, demoMode, switchMode, logout } = useFit();
  const navigate = useNavigate();
  const profile =  data.user || user;
  const gender = profile?.gender || data.user?.gender;
  const genderLabel = gender === 'MALE'
    ? 'Чоловіча'
    : gender === 'FEMALE'
      ? 'Жіноча'
      : 'Не вказано';

  function connectBackend() {
    switchMode(false);
    navigate('/login');
  }

  function signOut() {
    logout();
    navigate('/login');
  }

  return (
    <div className="page">
      <SectionHead
        eyebrow="ТВІЙ FITTRACK"
        title="Профіль та вигляд"
        subtitle="Персональні дані та налаштування інтерфейсу."
      />

      <div className="profile-grid">
        <Card title="Особиста інформація" subtitle="Поточні дані облікового запису">
          <div className="profile-hero">
            <div className="profile-avatar">
              {initials(profile?.displayName || profile?.email || 'FT')}
            </div>
            <div>
              <h2>
                {profile?.displayName || profile?.email?.split('@')[0] || 'Мій профіль'}
              </h2>
              <p>{profile?.email || '—'}</p>
            </div>
          </div>

          <div className="profile-facts">
            <ProfileFact label="Вік" value={profile?.age ?? data.user?.age} unit="років" />
            <ProfileFact label="Зріст" value={profile?.height ?? data.user?.height} unit="см" />
            <ProfileFact label="Вага" value={profile?.weight ?? data.user?.weight} unit="кг" />
            <ProfileFact label="Стать" value={genderLabel} />
          </div>
          <p className="muted small spaced-top">
            Дані профілю синхронізуються з твоїм обліковим записом.
          </p>
        </Card>

        <Card title="Підключення" subtitle="Режим роботи застосунку">
          <div className="mode-select">
            <div className="mode-head">
              <Database size={21} />
              <div>
                <strong>Джерело даних</strong>
              </div>
            </div>
            <div className="segmented full">
              <button
                type="button"
                className={demoMode ? 'selected' : ''}
                onClick={() => switchMode(true)}
              >
                <WifiOff size={15} /> Демо
              </button>
              <button
                type="button"
                className={!demoMode ? 'selected' : ''}
                onClick={connectBackend}
              >
                <Wifi size={15} /> Backend
              </button>
            </div>
          </div>

          <div className="connection-note">
            <ShieldCheck size={18} />
            <p>
              {demoMode
                ? 'Дані деморежиму зберігаються локально у браузері.'
                : 'JWT зберігається у sessionStorage лише для цієї вкладки.'}
            </p>
          </div>

          {!demoMode && (
            <Button variant="outline" onClick={signOut}>
              <LogOut size={17} /> Вийти
            </Button>
          )}
        </Card>
      </div>

      <Card title="Візуальний стиль" subtitle="8 тем — миттєва зміна без перезавантаження">
        <p className="theme-head-description">
          Graphite + Lime залишився стандартною темою. Твій вибір зберігатиметься
          на цьому пристрої.te + Lime залишився стандартною темою. Твій вибір зберігатиметься
          на цьому пристрої.
        </p>
        <div className="theme-grid">
          {THEMES.map((item) => (
            <ThemeCard
              key={item.id}
              item={item}
              selected={theme === item.id}
              onChoose={setTheme}
            />
          ))}
        </div>
      </Card>

      <Notice>
        Перемикати теми можна також через кнопку «Тема» у верхній панелі.
        Це налаштування не змінює дані FitTrack.
      </Notice>
    </div>
  );
}
