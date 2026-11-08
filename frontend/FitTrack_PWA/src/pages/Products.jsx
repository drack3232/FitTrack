import React, { useEffect, useMemo, useState } from 'react';
import { Plus, Search, Package, ScanBarcode, Trash2, Utensils } from 'lucide-react';

import { useFit } from '../lib/FitContext';
import { api, unwrapProducts } from '../lib/api';
import { fmt } from '../lib/format';
import {
  Button,
  Card,
  Empty,
  FormButtons,
  Input,
  Modal,
  Notice,
  SectionHead,
  Select,
  useTask,
} from '../components/UI';
import BarcodeScanner from '../components/BarcodeScanner';
import ProductForm from '../components/ProductForm';

const PAGE_SIZE = 30;
const mealTypes = [
  { value: 'BREAKFAST', label: 'Сніданок' },
  { value: 'LUNCH', label: 'Обід' },
  { value: 'DINNER', label: 'Вечеря' },
  { value: 'SNACK', label: 'Перекус' },
];

function findByBarcode(products, barcode) {
  return products.find((product) =>
    String(product.barcode || '') === String(barcode).trim()
  );
}

export default function Products() {
  const { data, demoMode, mutate, reload } = useFit();
  const { error, run } = useTask();

  const [query, setQuery] = useState('');
  const [category, setCategory] = useState('Усі');
  const [currentPage, setCurrentPage] = useState(1);
  const [remoteResults, setRemoteResults] = useState(null);
  const [searchError, setSearchError] = useState('');
  const [scannerOpen, setScannerOpen] = useState(false);
  const [createBarcode, setCreateBarcode] = useState(null);
  const [recordProduct, setRecordProduct] = useState(null);

  const categories = useMemo(() => [
    'Усі',
    ...new Set(data.products.map((product) => product.category).filter(Boolean)),
  ], [data.products]);

  useEffect(() => {
    setCurrentPage(1);
  }, [query, category]);

  // The server search can return products not loaded in the initial list.
  useEffect(() => {
    if (demoMode || !query.trim()) {
      setRemoteResults(null);
      setSearchError('');
      return;
    }

    let cancelled = false;
    const timer = setTimeout(async () => {
      try {
        const response = await api.products(query.trim(), 0, 100);
        if (!cancelled) {
          const fetched = unwrapProducts(response);
          // Preserve the category shown for items already loaded from the catalog.
          const categoriesByName = new Map(data.products.map((item) => [
            String(item.name || '').toLocaleLowerCase('uk'),
            item.category,
          ]));
          setRemoteResults(fetched.map((item) => ({
            ...item,
            category: item.category || categoriesByName.get(
              String(item.name || '').toLocaleLowerCase('uk'),
            ) || 'Інші',
          })));
          setSearchError('');
        }
      } catch (requestError) {
        if (!cancelled) setSearchError(requestError.message);
      }
    }, 300);

    return () => {
      cancelled = true;
      clearTimeout(timer);
    };
  }, [query, demoMode, data.products]);

  const visibleProducts = useMemo(() => {
    const source = remoteResults || data.products;
    const normalizedQuery = query.trim().toLocaleLowerCase('uk');

    return source.filter((product) => {
      const matchesQuery = [product.name, product.brand, product.barcode]
        .filter(Boolean)
        .join(' ')
        .toLocaleLowerCase('uk')
        .includes(normalizedQuery);

      const matchesCategory = category === 'Усі' || product.category === category;
      return matchesQuery && matchesCategory;
    });
  }, [data.products, remoteResults, query, category]);

  const pagesCount = Math.max(1, Math.ceil(visibleProducts.length / PAGE_SIZE));
  const productsOnPage = visibleProducts.slice(
    (currentPage - 1) * PAGE_SIZE,
    currentPage * PAGE_SIZE,
  );

  async function handleBarcode(barcode) {
    setScannerOpen(false);
    setSearchError('');

    const known = findByBarcode(data.products, barcode);
    if (known) {
      setRecordProduct(known);
      return;
    }

    if (!demoMode) {
      try {
        const remoteProduct = await api.productByBarcode(barcode);
        if (remoteProduct?.id) {
          setRecordProduct(remoteProduct);
          return;
        }
      } catch (requestError) {
        if (![404, 405].includes(requestError.status)) {
          setSearchError(`Пошук за кодом: ${requestError.message}`);
        }
      }
    }

    setCreateBarcode(barcode);
  }

  async function deleteProduct(product) {
    const accepted = window.confirm(`Видалити власний продукт «${product.name}»?`);
    if (!accepted) return;
    await run(() => mutate('product', 'delete', null, product.id));
  }

  return (
    <div className="page">
      <SectionHead
        eyebrow="БІБЛІОТЕКА ПРОДУКТІВ"
        title="Каталог харчування"
        subtitle="Продукти на 100 г, власні записи та сканер штрихкодів."
        action={
          <div className="catalog-actions">
            <Button variant="outline" onClick={() => setScannerOpen(true)}>
              <ScanBarcode size={18} /> Сканувати
            </Button>
            <Button onClick={() => setCreateBarcode('')}>
              <Plus size={18} /> Додати продукт
            </Button>
          </div>
        }
      />

      <Card className="products-card">
        <div className="products-toolbar">
          <div className="search-wrap products-search">
            <Search size={19} />
            <input
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Пошук: куряче філе, гречка, EAN..."
              aria-label="Пошук продукту"
            />
          </div>

          {categories.length > 1 && (
            <select
              className="catalog-category"
              value={category}
              onChange={(event) => setCategory(event.target.value)}
              aria-label="Категорія продукту"
            >
              {categories.map((name) => <option key={name}>{name}</option>)}
            </select>
          )}

          <span className="muted small">
            Знайдено: {visibleProducts.length}
          </span>
        </div>

        {!demoMode && (
          <p className="form-hint catalog-hint">
            У режимі Backend каталог завантажується з PostgreSQL.
            Щоб додати всі 280 довідкових продуктів, виконай SQL-імпорт
            із папки <code>sql/</code> один раз.
          </p>
        )}

        {searchError && <Notice type="error">{searchError}</Notice>}
        {error && <Notice type="error">{error}</Notice>}

        <div className="product-table-wrap">
          <table className="product-table">
            <thead>
              <tr>
                <th>Продукт</th>
                <th>Калорії</th>
                <th>Білки</th>
                <th>Жири</th>
                <th>Вуглеводи</th>
                <th>Дія</th>
              </tr>
            </thead>
            <tbody>
              {productsOnPage.map((product) => (
                <tr key={product.id}>
                  <td>
                    <span className="prod-symbol"><Package size={17} /></span>
                    <span>
                      <b>{product.name}</b>
                      <small>
                        {product.category || product.brand || 'Продукт'}
                        {product.barcode ? ` · ${product.barcode}` : ''}
                      </small>
                    </span>
                  </td>
                  <td><strong>{fmt(product.calories)}</strong><small>ккал / 100 г</small></td>
                  <td>{fmt(product.protein, 1)} г</td>
                  <td>{fmt(product.fat, 1)} г</td>
                  <td>{fmt(product.carbs, 1)} г</td>
                  <td>
                    <div className="catalog-row-actions">
                      <button
                        type="button"
                        className="catalog-action-btn"
                        title="Записати в щоденник"
                        onClick={() => setRecordProduct(product)}
                      >
                        <Utensils size={16} />
                      </button>
                      {product.ownerId != null && (
                        <button
                          type="button"
                          className="catalog-action-btn danger"
                          title="Видалити власний продукт"
                          onClick={() => deleteProduct(product)}
                        >
                          <Trash2 size={16} />
                        </button>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>

          {productsOnPage.length === 0 && (
            <Empty
              title="Нічого не знайдено"
              detail="Спробуй іншу назву або додай власний продукт."
            />
          )}
        </div>

        {pagesCount > 1 && (
          <div className="catalog-pagination">
            <Button
              variant="outline"
              disabled={currentPage === 1}
              onClick={() => setCurrentPage((page) => page - 1)}
            >
              Назад
            </Button>
            <span>{currentPage} / {pagesCount}</span>
            <Button
              variant="outline"
              disabled={currentPage >= pagesCount}
              onClick={() => setCurrentPage((page) => page + 1)}
            >
              Далі
            </Button>
          </div>
        )}
      </Card>

      <p className="form-hint spaced-top">
        БЖВ довідкових продуктів приблизні. Для фабричної упаковки
        використовуй значення з етикетки.
      </p>

      {scannerOpen && (
        <BarcodeScanner
          onDetected={handleBarcode}
          onClose={() => setScannerOpen(false)}
        />
      )}

      {createBarcode !== null && (
        <ProductForm
          initialBarcode={createBarcode}
          onClose={() => setCreateBarcode(null)}
        />
      )}

      {recordProduct && (
        <RecordProductModal
          product={recordProduct}
          onClose={() => setRecordProduct(null)}
        />
      )}
    </div>
  );
}

function RecordProductModal({ product, onClose }) {
  const { date, mutate } = useFit();
  const { busy, error, run } = useTask();
  const [mealType, setMealType] = useState('LUNCH');
  const [grams, setGrams] = useState('100');
  const calories = Number(product.calories || 0) * Number(grams || 0) / 100;

  async function submit(event) {
    event.preventDefault();

    const success = await run(() => mutate('food', 'create', {
      productId: product.id,
      entryDate: date,
      mealType,
      quantityGrams: Number(grams),
    }));

    if (success) onClose();
  }

  return (
    <Modal title={`Записати: ${product.name}`} onClose={onClose}>
      <form onSubmit={submit}>
        <Select
          label="Прийом їжі"
          value={mealType}
          onChange={(event) => setMealType(event.target.value)}
          options={mealTypes}
        />
        <Input
          label="Маса порції, г"
          type="number"
          min="1"
          step="1"
          value={grams}
          required
          onChange={(event) => setGrams(event.target.value)}
        />
        <p className="selected-cal">Порція: <b>{fmt(calories)} ккал</b></p>
        {error && <Notice type="error">{error}</Notice>}
        <FormButtons busy={busy} onCancel={onClose} submitText="Додати в щоденник" />
      </form>
    </Modal>
  );
}
