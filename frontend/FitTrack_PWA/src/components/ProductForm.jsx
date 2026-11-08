import React, { useState } from 'react';
import { useFit } from '../lib/FitContext';
import { Button, FormButtons, Input, Modal, Notice, useTask } from './UI';

const emptyProduct = {
  name: '',
  brand: '',
  barcode: '',
  calories: '',
  protein: '',
  fat: '',
  carbs: '',
};

export default function ProductForm({ onClose, onCreated, initialBarcode = '' }) {
  const { mutate } = useFit();
  const { busy, error, setError, run } = useTask();
  const [form, setForm] = useState({
    ...emptyProduct,
    barcode: initialBarcode,
  });

  function updateField(key) {
    return {
      value: form[key],
      onChange: (event) => {
        setForm((previous) => ({ ...previous, [key]: event.target.value }));
      },
    };
  }

  async function submit(event) {
    event.preventDefault();

    const nutrients = ['protein', 'fat', 'carbs'].map((key) => Number(form[key]));
    const totalMacros = nutrients.reduce((sum, value) => sum + value, 0);

    if (totalMacros > 100) {
      setError('Сума БЖВ не може перевищувати 100 г на 100 г продукту.');
      return;
    }

    const payload = {
      name: form.name.trim(),
      brand: form.brand.trim() || null,
      barcode: form.barcode.trim() || null,
      calories: Number(form.calories),
      protein: Number(form.protein),
      fat: Number(form.fat),
      carbs: Number(form.carbs),
    };

    let createdProduct = null;
    const success = await run(async () => {
      createdProduct = await mutate('product', 'create', payload);
    });

    if (success) {
      onCreated?.(createdProduct);
      onClose();
    }
  }

  return (
    <Modal title="Створити власний продукт" onClose={onClose}>
      <form onSubmit={submit}>
        <Input
          label="Назва"
          required
          placeholder="Наприклад, йогурт виробника X"
          {...updateField('name')}
        />
        <Input label="Бренд" placeholder="Необов'язково" {...updateField('brand')} />
        <Input
          label="Штрихкод (необов'язково)"
          placeholder="Зі сканера або з упаковки"
          {...updateField('barcode')}
        />

        <p className="form-hint">
          Введи дані на 100 г, як на етикетці продукту.
        </p>

        <div className="form-grid">
          {[
            ['calories', 'Ккал / 100 г'],
            ['protein', 'Білки, г'],
            ['fat', 'Жири, г'],
            ['carbs', 'Вуглеводи, г'],
          ].map(([key, label]) => (
            <Input
              key={key}
              label={label}
              type="number"
              min="0"
              step="0.1"
              required
              {...updateField(key)}
            />
          ))}
        </div>

        {error && <Notice type="error">{error}</Notice>}
        <FormButtons
          busy={busy}
          onCancel={onClose}
          submitText="Зберегти продукт"
        />
      </form>
    </Modal>
  );
}
