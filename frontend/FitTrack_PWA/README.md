# FitTrack PWA v2 — читабельний React Frontend

React 19 + Vite 7 + PWA + Recharts + ZXing, для Backend Java/Spring Boot.

**Спершу прочитай [`INTEGRATION_UA.md`](INTEGRATION_UA.md).** Там є команда для
безпечного об'єднання з існуючим Frontend, backup, імпорт 280 продуктів,
запуск Frontend + Backend однією командою та перевірка на телефоні.

## Що реалізовано

- 8 тем, фірмова Graphite + Lime за замовчуванням;
- анімоване згортання лівого меню до іконок на ПК (стан зберігається);
- окреме мобільне висувне меню; діаграми приховано на вузьких екранах;
- Dashboard, Nutrition, Water, Activity, Weight, Analytics, Goals, Profile;
- 280 **довідкових** продуктів з пошуком, категоріями і пагінацією;
- створення власного продукту, ручний штрихкод або сканування через ZXing;
- додавання порції продукту в щоденник з калоріями та БЖВ;
- Demo Mode без Backend та Live Mode через `/api/v1/*`;
- Auth (login/register) із JWT у sessionStorage;
- installable PWA shell (після HTTPS production deployment).

## Запустити окремо

```bash
npm install
cp .env.example .env
npm run dev
```

Відкрий http://localhost:5173.

У `.env` `VITE_DEMO_MODE=false` вмикає Live API. Для localhost на `8080`
використовується Vite Proxy; `VITE_API_BASE_URL` залишай порожнім.

## Редагування

Код сторінок — у `src/pages/`, контекст стану — у `src/lib/FitContext.jsx`,
API — у `src/lib/api.js`, сканер — у `src/components/BarcodeScanner.jsx`,
форма продукту — у `src/components/ProductForm.jsx`.

Всі ключові компоненти відформатовано з відступами і винесено у функції,
щоб було зручно змінювати код під час курсової. Тема змінюється в Профілі
і у верхній панелі, `fittrack_theme` зберігається в localStorage.

## Сумісність із Backend

- Реальні продукти в PostgreSQL потрібно додати через `sql/seed_280_products.sql`.
- Для продукту із заводським штрихкодом створюй власний продукт на основі етикетки.
- Перевір відповідність DTO у `src/lib/api.js`. Не всі контролери доступні у файлах,
  тому live-інтеграцію не можна гарантувати без перевірки запитів у Network.
- Запити з камери працюють тільки у захищеному контексті (HTTPS/localhost).

## Збірка

```bash
npm run build
npm run preview
```

Це Frontend-проєкт; Java Backend сюди не копіюється. Архів не містить
`node_modules`, `.env` із секретами або вигаданих товарних штрихкодів.
