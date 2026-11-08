import React, { useEffect, useRef, useState } from 'react';
import { Check, ChevronDown, Palette } from 'lucide-react';
import { useFit } from '../lib/FitContext';
import { THEMES } from '../lib/themes';

// Accessible quick switching anywhere in the application; also available in Profile.
export default function ThemeSwitcher() {
  const { theme, setTheme } = useFit();
  const [open, setOpen] = useState(false);
  const container = useRef(null);
  const selected = THEMES.find(item => item.id === theme) || THEMES[0];

  useEffect(() => {
    if (!open) return;
    const onPointer = event => {
      if (!container.current?.contains(event.target)) setOpen(false);
    };
    const onKey = event => {
      if (event.key === 'Escape') setOpen(false);
    };
    document.addEventListener('pointerdown', onPointer);
    document.addEventListener('keydown', onKey);
    return () => {
      document.removeEventListener('pointerdown', onPointer);
      document.removeEventListener('keydown', onKey);
    };
  }, [open]);

  return (
    <div className="theme-switcher" ref={container}>
      <button
        type="button" className="theme-switch-trigger"
        aria-label={`Змінити тему. Поточна: ${selected.name}`}
        aria-expanded={open} aria-haspopup="menu"
        onClick={() => setOpen(value => !value)}
        title="Змінити тему оформлення"
      >
        <Palette size={15} aria-hidden="true" />
        <span className="theme-trigger-label">Тема</span>
        <span className="theme-trigger-dot" aria-hidden="true" />
        <ChevronDown size={13} aria-hidden="true" />
      </button>
      {open && (
        <div className="theme-switch-popover" role="menu" aria-label="Оберіть оформлення">
          <div className="theme-pop-title">Оформлення FitTrack</div>
          <div className="theme-pop-grid">
            {THEMES.map(option => (
              <button key={option.id} type="button" role="menuitemradio"
                aria-checked={theme === option.id}
                className="theme-pop-option"
                onClick={() => { setTheme(option.id); setOpen(false); }}
              >
                <span className="theme-pop-colors" aria-hidden="true">
                  <i style={{ background: option.colors[0] }} />
                  <i style={{ background: option.colors[1] }} />
                  <i style={{ background: option.colors[2] }} />
                </span>
                <span className="theme-pop-option-name">{option.name}</span>
                {theme === option.id && <Check size={14} className="theme-pop-option-check" />}
              </button>
            ))}
          </div>
          <div className="theme-pop-footer">Зміни зберігаються автоматично · Усі 8 тем доступні в профілі</div>
        </div>
      )}
    </div>
  );
}
