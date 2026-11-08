# FitTrack — теми інтерфейсу

У застосунку доступні **8 тем**; початковий **Graphite + Lime** залишено темою за замовчуванням.

| ID | Назва | Категорія |
|---|---|---|
| `graphite` | Graphite + Lime | Темна, оригінальна |
| `warm` | Warm Minimal | Світла |
| `midnight` | Midnight Blue | Темна |
| `violet` | Obsidian Violet | Темна |
| `ember` | Carbon Ember | Темна |
| `ocean` | Deep Ocean | Темна |
| `arctic` | Arctic Light | Світла |
| `rose` | Rose Quartz | Світла |

**Як змінити:** верхня панель → **Тема** або Профіль → Візуальний стиль → натисни картку теми. Сторінка не перезавантажується. Вибір зберігається у `localStorage` (`fittrack_theme`) і синхронізується між вкладками, якщо браузер дозволяє локальне сховище.

**Швидке повернення Lime:** Профіль → Graphite + Lime.

**Файли:**
- `src/lib/themes.js` — список тем і збереження вибору.
- `src/lib/FitContext.jsx` — керування темою React.
- `src/components/ThemeSwitcher.jsx` — швидкий перемикач у верхній панелі.
- `src/pages/Profile.jsx` — вісім карток з кольоровими зразками.
- `src/styles/themes.css` — CSS-змінні нових тем.
- `previews/fittrack-*.png` — зображення концептів Dashboard.
- `FitTrack_8_Themes_Preview.html` — автономний HTML-макет із миттєвим перемиканням оформлення (без npm).

Кольорові прев’ю — концептуальні макети Dashboard, а не скриншоти запущеного React у браузері. Для тесту реального перемикання запусти `npm run dev`.
