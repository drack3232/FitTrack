import React, { useEffect, useRef, useState } from 'react';
import { Camera, Keyboard, ScanBarcode } from 'lucide-react';

import { Button, Modal, Notice } from './UI';

/**
 * Scans EAN / UPC / Code 128 / other 1D and 2D symbols with ZXing.
 * Camera permission requires HTTPS or localhost in modern browsers.
 * Never sends camera frames to the backend.
 */
export default function BarcodeScanner({ onDetected, onClose }) {
  const videoRef = useRef(null);
  const controlsRef = useRef(null);
  const detectedRef = useRef(onDetected);
  const closedRef = useRef(false);
  const [manualCode, setManualCode] = useState('');
  const [error, setError] = useState('');
  const [status, setStatus] = useState('Запускаємо камеру…');

  useEffect(() => {
    detectedRef.current = onDetected;
  }, [onDetected]);

  useEffect(() => {
    closedRef.current = false;

    async function startCamera() {
      try {
        if (!navigator.mediaDevices?.getUserMedia) {
          throw new Error('Камера доступна лише через HTTPS або localhost.');
        }

        // Dynamic import: the ZXing code is downloaded only when scanning.
        const { BrowserMultiFormatReader } = await import('@zxing/browser');
        if (closedRef.current) return;

        const reader = new BrowserMultiFormatReader();
        const controls = await reader.decodeFromVideoDevice(
          undefined,
          videoRef.current,
          (result, _error, scanControls) => {
            if (!result || closedRef.current) return;

            closedRef.current = true;
            scanControls.stop();
            detectedRef.current(result.getText().trim());
          },
        );

        if (closedRef.current) {
          controls.stop();
        } else {
          controlsRef.current = controls;
          setStatus('Наведи камеру на штрихкод товару');
        }
      } catch (scanError) {
        setError(scanError.message || 'Не вдалося відкрити камеру.');
        setStatus('Можеш ввести код вручну нижче.');
      }
    }

    startCamera();

    return () => {
      closedRef.current = true;
      controlsRef.current?.stop();
      controlsRef.current = null;
    };
  }, []);

  function submitManual(event) {
    event.preventDefault();
    const code = manualCode.trim();

    if (!code) {
      setError('Введи штрихкод.');
      return;
    }

    closedRef.current = true;
    controlsRef.current?.stop();
    detectedRef.current(code);
  }

  return (
    <Modal title="Сканувати штрихкод" onClose={onClose}>
      <div className="scanner-shell">
        <div className="scanner-preview">
          <video
            ref={videoRef}
            autoPlay
            muted
            playsInline
            aria-label="Зображення з камери для сканування штрихкоду"
          />
          <span className="scanner-target" aria-hidden="true" />
        </div>
        <p className="scanner-status"><Camera size={16} /> {status}</p>

        {error && <Notice type="error">{error}</Notice>}

        <form onSubmit={submitManual}>
          <label className="field">
            <span><Keyboard size={16} /> Ввести штрихкод вручну</span>
            <input
              inputMode="numeric"
              value={manualCode}
              onChange={(event) => setManualCode(event.target.value)}
              placeholder="Наприклад, EAN-13 або UPC-A"
            />
          </label>

          <Button type="submit">
            <ScanBarcode size={17} /> Знайти продукт
          </Button>
        </form>
        <p className="form-hint">
          Код використовується тільки для пошуку збереженого продукту.
          Якщо такого немає, його можна додати зі своїми БЖВ.
        </p>
      </div>
    </Modal>
  );
}
