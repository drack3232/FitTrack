import React from 'react';
import { Navigate, Route, Routes } from 'react-router-dom';

import { useFit } from './lib/FitContext';
import Shell from './components/Shell';

import Dashboard from './pages/Dashboard';
import Analytics from './pages/Analytics';
import Nutrition from './pages/Nutrition';
import Water from './pages/Water';
import Activity from './pages/Activity';
import Weight from './pages/Weight';
import Products from './pages/Products';
import Goals from './pages/Goals';
import Profile from './pages/Profile';
import Auth from './pages/Auth';

function ProtectedPage({ children }) {
  const { demoMode, hasToken } = useFit();
  return demoMode || hasToken ? children : <Navigate to="/login" replace />;
}

export default function App() {
  const { demoMode, hasToken } = useFit();

  return (
    <Routes>
      <Route
        path="/login"
        element={demoMode || hasToken ? <Navigate to="/" replace /> : <Auth />}
      />

      <Route
        element={
          <ProtectedPage>
            <Shell />
          </ProtectedPage>
        }
      >
        <Route index element={<Dashboard />} />
        <Route path="analytics" element={<Analytics />} />
        <Route path="nutrition" element={<Nutrition />} />
        <Route path="water" element={<Water />} />
        <Route path="activity" element={<Activity />} />
        <Route path="weight" element={<Weight />} />
        <Route path="products" element={<Products />} />
        <Route path="goals" element={<Goals />} />
        <Route path="profile" element={<Profile />} />
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
