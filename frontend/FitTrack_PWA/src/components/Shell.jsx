import React, { useEffect, useState } from 'react';
import { NavLink, Outlet, useLocation, useNavigate } from 'react-router-dom';
import {
  Activity,
  ArrowRight,
  BarChart3,
  CalendarDays,
  ChevronRight,
  ClipboardList,
  Droplets,
  Dumbbell,
  LayoutDashboard,
  Menu,
  Package,
  PanelLeftClose,
  PanelLeftOpen,
  Scale,
  Target,
  UserRound,
  Wifi,
  WifiOff,
  X,
} from 'lucide-react';

import { useFit } from '../lib/FitContext';
import { DateSelect } from './UI';
import ThemeSwitcher from './ThemeSwitcher';
import { initials } from '../lib/format';

const SIDEBAR_STORAGE_KEY = 'fittrack_sidebar_collapsed';

const navigation = [
  {
    title: 'ГОЛОВНЕ',
    links: [
      { to: '/', label: 'Огляд', icon: LayoutDashboard },
      { to: '/analytics', label: 'Аналітика', icon: BarChart3 },
    ],
  },
  {
    title: 'ЩОДЕННИК',
    links: [
      { to: '/nutrition', label: 'Харчування', icon: ClipboardList },
      { to: '/water', label: 'Вода', icon: Droplets },
      { to: '/activity', label: 'Активність', icon: Dumbbell },
      { to: '/weight', label: 'Моя вага', icon: Scale },
    ],
  },
  {
    title: 'НАЛАШТУВАННЯ',
    links: [
      { to: '/products', label: 'Продукти', icon: Package },
      { to: '/goals', label: 'Мої цілі', icon: Target },
      { to: '/profile', label: 'Профіль', icon: UserRound },
    ],
  },
];

const pageTitles = {
  '/': 'Панель огляду',
  '/analytics': 'Аналітика',
  '/nutrition': 'Харчування',
  '/water': 'Гідратація',
  '/activity': 'Активність',
  '/weight': 'Прогрес ваги',
  '/products': 'Продукти',
  '/goals': 'Мої цілі',
  '/profile': 'Профіль',
};

function readCollapsedState() {
  try {
    return localStorage.getItem(SIDEBAR_STORAGE_KEY) === 'true';
  } catch {
    return false;
  }
}

export function Logo({ compact = false }) {
  return (
    <div className="logo">
      <div className="logo-mark">
        <Activity size={25} strokeWidth={2.8} />
      </div>
      {!compact && (
        <span className="logo-wordmark">
          fit<span className="logo-accent">track</span>
          <small>YOUR DAILY EDGE</small>
        </span>
      )}
    </div>
  );
}

export default function Shell() {
  const {
    demoMode,
    switchMode,
    user,
    toast,
    error,
  } = useFit();
  const [mobileOpen, setMobileOpen] = useState(false);
  const [collapsed, setCollapsed] = useState(readCollapsedState);
  const location = useLocation();
  const navigate = useNavigate();

  useEffect(() => {
    setMobileOpen(false);
  }, [location.pathname]);

  useEffect(() => {
    try {
      localStorage.setItem(SIDEBAR_STORAGE_KEY, String(collapsed));
    } catch {
    }
  }, [collapsed]);

  useEffect(() => {
    const closeOnEscape = (event) => {
      if (event.key === 'Escape') {
        setMobileOpen(false);
      }
    };

    window.addEventListener('keydown', closeOnEscape);
    return () => window.removeEventListener('keydown', closeOnEscape);
  }, []);

  useEffect(() => {
    const handleResize = () => {
      if (window.innerWidth > 850) {
        setMobileOpen(false);
      }
    };

    window.addEventListener('resize', handleResize);

    return () => {
      window.removeEventListener('resize', handleResize);
    };
  }, []);

  function toggleNavigation() {
    const isMobile = window.matchMedia(
        '(max-width: 850px)'
    ).matches;

    if (isMobile) {
      setMobileOpen((previous) => !previous);
      return;
    }

    setCollapsed((previous) => !previous);

  }

  const appClass = [
    'app-shell',
    collapsed ? 'sidebar-collapsed' : '',
    mobileOpen ? 'mobile-menu-open' : '',
  ]
      .filter(Boolean)
      .join(' ');

  return (
    <div className={appClass}>
      {mobileOpen && (
        <button
          className="scrim"
          aria-label="Закрити навігацію"
          onClick={() => setMobileOpen(false)}
        />
      )}

      <aside
          id="fittrack-sidebar"
          className={`sidebar ${mobileOpen ? 'open' : ''}`}
        aria-label="Основна навігація"
      >
        <div className="sidebar-top">
          <Logo />

          <button
              type="button"
              className="icon-btn mobile-close"
              aria-label="Закрити мобільне меню"
              onClick={() => setMobileOpen(false)}
          >
            <X size={22} />
          </button>
        </div>

        <nav className="sidebar-scroll">
          {navigation.map((section) => (
            <div className="nav-section" key={section.title}>
              <div className="nav-label">{section.title}</div>

              {section.links.map(({ to, label, icon: Icon }) => (
                <NavLink
                  key={to}
                  to={to}
                  title={collapsed ? label : undefined}
                  aria-label={label}
                  end={to === '/'}
                  className={({ isActive }) =>
                    `nav-link ${isActive ? 'active' : ''}`
                  }
                >
                  <Icon size={20} />
                  <span className="nav-text">{label}</span>
                </NavLink>
              ))}
            </div>
          ))}
        </nav>

        <div className="sidebar-bottom">
          <div className="help-tile">
            <div className="help-icon"><Activity size={19} /></div>
            <b>Маленькі кроки. Великий прогрес.</b>
            <p>Твоя щоденна форма — це сума звичок.</p>
          </div>

          <NavLink to="/profile" className="account-tile" title="Мій профіль">
            <span className="avatar">
              {initials(user?.displayName || user?.email || 'FT')}
            </span>
            <span className="account-names">
              <strong>{user?.displayName || user?.email?.split('@')[0] || 'Атлет'}</strong>
              <small>{demoMode ? 'Демоакаунт' : 'Мій акаунт'}</small>
            </span>
            <ChevronRight size={16} className="account-chevron" />
          </NavLink>
        </div>
      </aside>

      <div className="main-shell">
        <header className="topbar">
          <div className="top-left">
            <button
                type="button"
                className="icon-btn menu-toggle"
                onClick={toggleNavigation}
                aria-label={
                  mobileOpen
                      ? 'Закрити меню'
                      : collapsed
                          ? 'Відкрити меню'
                          : 'Закрити меню'
                }
                aria-controls="fittrack-sidebar"
            >
  <span className="desktop-menu-icon">
    {collapsed ? (
        <PanelLeftOpen size={22} />
    ) : (
        <PanelLeftClose size={22} />
    )}
  </span>

              <Menu className="mobile-menu-icon" size={22} />
            </button>
            <span className="breadcrumb">
              FITTRACK <ChevronRight size={13} />
              <strong>{pageTitles[location.pathname] || 'FitTrack'}</strong>
            </span>
          </div>

          <div className="top-actions">
            <span className={`mode-label ${demoMode ? 'demo-label' : ''}`}>
              {demoMode ? <WifiOff size={14} /> : <Wifi size={14} />}
              {demoMode ? 'DEMO' : 'LIVE API'}
            </span>
            <ThemeSwitcher />
            <DateSelect />
            <button
              className="top-avatar"
              type="button"
              aria-label="Відкрити профіль"
              onClick={() => navigate('/profile')}
            >
              {initials(user?.displayName || user?.email || 'FT')}
            </button>
          </div>
        </header>

        <main className="main-content">
          {demoMode && (
            <div className="demo-banner">
              <span>Деморежим: записи зберігаються лише в цьому браузері.</span>
              <button
                type="button"
                onClick={() => {
                  switchMode(false);
                  navigate('/login');
                }}
              >
                Підключити Backend <ArrowRight size={15} />
              </button>
            </div>
          )}

          {error && <div className="error-strip">{error}</div>}
          <Outlet />
        </main>

        <footer className="app-footer">
          <span>FITTRACK © 2026</span>

        </footer>
      </div>

      {toast && (
        <div className="toast" role="status">
          <span className="toast-icon">✓</span> {toast}
        </div>
      )}
    </div>
  );
}
