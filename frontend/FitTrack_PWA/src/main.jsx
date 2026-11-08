import React from 'react';
import { createRoot } from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';

import App from './App';
import { FitProvider } from './lib/FitContext';

import './styles/index.css';
import './styles/themes.css';
import './styles/improvements.css';

const root = document.getElementById('root');

createRoot(root).render(
  <React.StrictMode>
    <BrowserRouter>
      <FitProvider>
        <App />
      </FitProvider>
    </BrowserRouter>
  </React.StrictMode>,
);
