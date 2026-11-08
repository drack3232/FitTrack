import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ArrowRight, Check, ShieldCheck, Sparkles } from 'lucide-react';

import { useFit } from '../lib/FitContext';
import { Logo } from '../components/Shell';
import { Button, Input, Notice } from '../components/UI';

const INITIAL_FORM = {
  email: '',
  password: '',
  gender: 'MALE',
  age: '22',
  height: '180',
  weight: '80',
};

export default function Auth() {
  const { login, register, switchMode } = useFit();
  const navigate = useNavigate();
  const [registering, setRegistering] = useState(false);
  const [form, setForm] = useState(INITIAL_FORM);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState('');

  function bindField(name) {
    return {
      value: form[name],
      onChange: (event) => setForm((previous) => ({
        ...previous,
        [name]: event.target.value,
      })),
    };
  }

  async function submit(event) {
    event.preventDefault();
    setBusy(true);
    setError('');

    try {
      if (registering) {
        await register({
          email: form.email,
          password: form.password,
          gender: form.gender,
          age: Number(form.age),
          height: Number(form.height),
          weight: Number(form.weight),
        });
      } else {
        await login({ email: form.email, password: form.password });
      }
      navigate('/');
    } catch (caught) {
      setError(caught.message || 'Не вдалося увійти');
    } finally {
      setBusy(false);
    }
  }

  function toggleRegistration() {
    setRegistering((value) => !value);
    setError('');
  }

  function openDemo() {
    switchMode(true);
    navigate('/');
  }

  return (
    <div className="auth-layout">
      <div className="auth-art">
        <Logo />
        <div className="auth-copy">
          <div className="hero-pill">
            <Sparkles size={15} /> КОЖЕН ДЕНЬ — НОВИЙ РІВЕНЬ
          </div>
          <h1>
            Сильніші
            <br />
            <em>звички.</em>
            <br />
            Кращий ти.
          </h1>
          <p>Харчування, тренування, вода й аналітика — в одному особистому просторі.</p>
          <div className="auth-features">
            <span><Check size={17} /> Простий облік</span>
            <span><Check size={17} /> Прозора аналітика</span>
            <span><Check size={17} /> Твій темп</span>
          </div>
        </div>
        <div className="auth-art-footer">FITTRACK © 2026</div>
      </div>

      <div className="auth-panel">
        <div className="auth-panel-in">
          <div className="auth-mobile-logo"><Logo /></div>
          <div className="kicker">ЛАСКАВО ПРОСИМО</div>
          <h2>{registering ? 'Створи свій акаунт' : 'Повернись до прогресу'}</h2>
          <p className="muted">
            {registering
              ? 'Перший крок до щоденного результату.'
              : 'Увійди, щоб продовжити свій шлях.'}
          </p>

          <form onSubmit={submit}>
            <Input
              label="Email"
              type="email"
              autoComplete="email"
              required
              placeholder="you@example.com"
              {...bindField('email')}
            />
            <Input
              label="Пароль"
              type="password"
              minLength="6"
              autoComplete={registering ? 'new-password' : 'current-password'}
              required
              placeholder="••••••••"
              {...bindField('password')}
            />

            {registering && (
              <>
                <label className="field">
                  <span>Стать</span>
                  <select {...bindField('gender')}>
                    <option value="MALE">Чоловіча</option>
                    <option value="FEMALE">Жіноча</option>
                  </select>
                </label>
                <div className="form-grid">
                  <Input label="Вік" type="number" min="13" max="100" required {...bindField('age')} />
                  <Input label="Зріст, см" type="number" min="100" max="250" required {...bindField('height')} />
                </div>
                <Input label="Вага, кг" type="number" min="20" max="350" step="0.1" required {...bindField('weight')} />
              </>
            )}

            {error && <Notice type="error">{error}</Notice>}
            <Button type="submit" className="auth-submit" disabled={busy}>
              {busy ? 'Зачекай...' : registering ? 'Зареєструватись' : 'Увійти'}
              <ArrowRight size={18} />
            </Button>
          </form>

          <p className="auth-switch">
            {registering ? 'Вже маєш акаунт?' : 'Ще не маєш акаунту?'}{' '}
            <button type="button" onClick={toggleRegistration}>
              {registering ? 'Увійти' : 'Створити акаунт'}
            </button>
          </p>
          <div className="auth-divider">або</div>
          <Button variant="outline" className="demo-start" onClick={openDemo}>
            <Sparkles size={17} /> Переглянути демо
          </Button>
          <p className="auth-security">
            <ShieldCheck size={16} /> Твої записи доступні лише після авторизації.
          </p>
        </div>
      </div>
    </div>
  );
}
