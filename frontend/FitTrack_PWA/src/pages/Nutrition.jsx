import React, { useMemo, useState } from 'react';
import { Flame, Plus, ScanBarcode, Search, Trash2, Utensils } from 'lucide-react';
import { useFit } from '../lib/FitContext';
import { fmt, mealLabel } from '../lib/format';
import {
  Button,
  Card,
  Empty,
  FormButtons,
  Input,
  Macro,
  Modal,
  Notice,
  SectionHead,
  Select,
  useTask,
} from '../components/UI';
import { api } from '../lib/api';
import BarcodeScanner from '../components/BarcodeScanner';
import ProductForm from '../components/ProductForm';

const mealTypes = ['BREAKFAST', 'LUNCH', 'DINNER', 'SNACK'];

function sumValues(items, field) {
  return items.reduce((total, item) => total + Number(item[field] || 0), 0);
}

export default function Nutrition() {
  const { data, date, mutate } = useFit();
  const [foodModalOpen, setFoodModalOpen] = useState(false);
  const { error, run } = useTask();

  const dailyEntries = data.foods.filter((entry) => entry.entryDate === date);
  const calories = sumValues(dailyEntries, 'calories');
  const protein = sumValues(dailyEntries, 'proteinGrams');
  const fat = sumValues(dailyEntries, 'fatGrams');
  const carbs = sumValues(dailyEntries, 'carbGrams');

  const target = Number(
    data.calculations?.dailyCalorieTarget ??
    data.calculations?.targetCalories ??
    data.calculations?.dailyCalories ??
    2400,
  );

  async function deleteEntry(id) {
    await run(() => mutate('food', 'delete', null, id));
  }

  return (
    <div className="page">
      <SectionHead
        eyebrow="ТВОЄ ПАЛИВО"
        title="Щоденник харчування"
        subtitle="Вибирай продукт, зазначай грамовку та отримуй БЖВ порції."
        action={
          <Button onClick={() => setFoodModalOpen(true)}>
            <Plus size={18} /> Додати їжу
          </Button>
        }
      />

      <div className="nutrition-top">
        <Card className="nutrition-total">
          <div className="nutri-total-head">
            <span><Flame size={19} /> З'їдено сьогодні</span>
            <small>Орієнтовна ціль: {fmt(target)} ккал</small>
          </div>
          <div className="nutri-big">{fmt(calories)}<span> ккал</span></div>
          <div className="daily-balance">
            <div
              className="daily-bar"
              style={{ width: `${Math.min(100, calories / Math.max(1, target) * 100)}%` }}
            />
          </div>
          <p>Залишилося: {fmt(Math.max(0, target - calories))} ккал</p>
        </Card>

        <Card title="БЖВ за сьогодні" subtitle="Макронутрієнти">
          <div className="macro-stack">
            <Macro label="Білки" current={protein} target={160} color="var(--purple)" />
            <Macro label="Жири" current={fat} target={80} color="var(--orange)" />
            <Macro label="Вуглеводи" current={carbs} target={260} color="var(--blue)" />
          </div>
        </Card>
      </div>

      <div className="meal-grid">
        {mealTypes.map((type) => {
          const entries = dailyEntries.filter((entry) => entry.mealType === type);
          const mealCalories = sumValues(entries, 'calories');

          return (
            <Card
              key={type}
              className="meal-card"
              title={mealLabel[type]}
              subtitle={`${entries.length} записів · ${fmt(mealCalories)} ккал`}
              action={
                <button
                  type="button"
                  className="icon-link"
                  aria-label={`Додати: ${mealLabel[type]}`}
                  onClick={() => setFoodModalOpen(true)}
                >
                  <Plus size={18} />
                </button>
              }
            >
              {entries.length === 0 ? (
                <Empty title="Немає записів" detail="Додай продукт до цього прийому їжі." />
              ) : (
                entries.map((entry) => (
                  <div className="recent-row" key={entry.id}>
                    <div className="food-dot"><Utensils size={16} /></div>
                    <div className="recent-name">
                      <strong>{entry.productName}</strong>
                      <small>{fmt(entry.quantityGrams)} г</small>
                    </div>
                    <div className="recent-cal">
                      <strong>{fmt(entry.calories)} ккал</strong>
                      <small>Б {fmt(entry.proteinGrams, 1)} г</small>
                    </div>
                    <button
                      type="button"
                      className="delete-icon"
                      title="Видалити запис"
                      onClick={() => deleteEntry(entry.id)}
                    >
                      <Trash2 size={16} />
                    </button>
                  </div>
                ))
              )}
            </Card>
          );
        })}
      </div>

      {error && <Notice type="error">{error}</Notice>}
      {foodModalOpen && <AddFoodModal onClose={() => setFoodModalOpen(false)} />}
    </div>
  );
}

function AddFoodModal({ onClose }) {
  const { data, date, demoMode, mutate } = useFit();
  const { busy, error, setError, run } = useTask();

  const [query, setQuery] = useState('');
  const [selected, setSelected] = useState(null);
  const [mealType, setMealType] = useState('LUNCH');
  const [grams, setGrams] = useState('100');
  const [scannerOpen, setScannerOpen] = useState(false);
  const [createBarcode, setCreateBarcode] = useState(null);

  const matchingProducts = useMemo(() => {
    const normalized = query.trim().toLocaleLowerCase('uk');
    return data.products.filter((product) =>
      [product.name, product.brand, product.barcode]
        .filter(Boolean)
        .join(' ')
        .toLocaleLowerCase('uk')
        .includes(normalized)
    );
  }, [data.products, query]);

  async function handleBarcode(barcode) {
    setScannerOpen(false);
    setError('');

    const localProduct = data.products.find((product) =>
      String(product.barcode || '') === barcode
    );

    if (localProduct) {
      setSelected(localProduct);
      setQuery(localProduct.name);
      return;
    }

    if (!demoMode) {
      try {
        const remoteProduct = await api.productByBarcode(barcode);
        if (remoteProduct?.id) {
          setSelected(remoteProduct);
          setQuery(remoteProduct.name);
          return;
        }
      } catch (requestError) {
        if (![404, 405].includes(requestError.status)) {
          setError(requestError.message);
        }
      }
    }

    setCreateBarcode(barcode);
  }

  async function submit(event) {
    event.preventDefault();

    if (!selected) {
      setError('Спочатку вибери продукт зі списку.');
      return;
    }

    const success = await run(() => mutate('food', 'create', {
      entryDate: date,
      productId: selected.id,
      mealType,
      quantityGrams: Number(grams),
    }));

    if (success) onClose();
  }

  return (
    <>
      <Modal title="Додати продукт до щоденника" onClose={onClose} wide>
        <form onSubmit={submit}>
          <div className="food-search-actions">
            <label className="field food-search-field">
              <span>Назва продукту або код</span>
              <div className="search-wrap">
                <Search size={18} />
                <input
                  autoFocus
                  value={query}
                  onChange={(event) => {
                    setQuery(event.target.value);
                    setSelected(null);
                  }}
                  placeholder="Наприклад, куряче філе"
                />
              </div>
            </label>
            <Button
              type="button"
              variant="outline"
              onClick={() => setScannerOpen(true)}
            >
              <ScanBarcode size={19} /> Сканер
            </Button>
          </div>

          <div className="product-picker">
            {matchingProducts.slice(0, 50).map((product) => (
              <button
                type="button"
                key={product.id}
                className={`product-option ${selected?.id === product.id ? 'is-selected' : ''}`}
                onClick={() => setSelected(product)}
              >
                <span>
                  <b>{product.name}</b>
                  <small>
                    Б {fmt(product.protein, 1)} · Ж {fmt(product.fat, 1)} ·
                    В {fmt(product.carbs, 1)} / 100 г
                  </small>
                </span>
                <strong>{fmt(product.calories)} ккал</strong>
              </button>
            ))}

            {matchingProducts.length === 0 && (
              <p className="muted small picker-empty">
                Продукту немає в каталозі. Створи власний продукт.
              </p>
            )}
          </div>

          <Button type="button" variant="outline" onClick={() => setCreateBarcode('')}>
            <Plus size={16} /> Створити власний продукт
          </Button>

          <div className="form-grid spaced-top">
            <Select
              label="Прийом їжі"
              value={mealType}
              onChange={(event) => setMealType(event.target.value)}
              options={mealTypes.map((value) => ({ value, label: mealLabel[value] }))}
            />
            <Input
              label="Кількість, г"
              type="number"
              min="1"
              step="1"
              required
              value={grams}
              onChange={(event) => setGrams(event.target.value)}
            />
          </div>

          {selected && (
            <div className="selected-cal">
              {selected.name}: <b>{fmt(selected.calories * Number(grams) / 100)} ккал</b>
            </div>
          )}

          {error && <Notice type="error">{error}</Notice>}
          <FormButtons busy={busy} onCancel={onClose} submitText="Записати порцію" />
        </form>
      </Modal>

      {scannerOpen && (
        <BarcodeScanner onDetected={handleBarcode} onClose={() => setScannerOpen(false)} />
      )}

      {createBarcode !== null && (
        <ProductForm
          initialBarcode={createBarcode}
          onClose={() => setCreateBarcode(null)}
          onCreated={(product) => {
            if (product?.id) {
              setSelected(product);
              setQuery(product.name);
            }
          }}
        />
      )}
    </>
  );
}
