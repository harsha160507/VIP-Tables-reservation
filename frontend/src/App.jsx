import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import Shell from './components/Shell';
import LandingPage from './pages/LandingPage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import RestaurantsPage from './pages/RestaurantsPage';
import RestaurantDetailsPage from './pages/RestaurantDetailsPage';
import CustomerDashboardPage from './pages/CustomerDashboardPage';
import MyReservationsPage from './pages/MyReservationsPage';
import ManagerDashboardPage from './pages/ManagerDashboardPage';
import ManagerRestaurantsPage from './pages/ManagerRestaurantsPage';
import ManagerTablesPage from './pages/ManagerTablesPage';
import ManagerReservationsPage from './pages/ManagerReservationsPage';

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route element={<Shell />}>
            <Route path="/" element={<LandingPage />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            <Route path="/restaurants" element={<RestaurantsPage />} />
            <Route path="/restaurants/:id" element={<RestaurantDetailsPage />} />
            <Route element={<ProtectedRoute roles={['CUSTOMER']} />}>
              <Route path="/customer" element={<CustomerDashboardPage />} />
              <Route path="/my-reservations" element={<MyReservationsPage />} />
            </Route>
            <Route element={<ProtectedRoute roles={['MANAGER']} />}>
              <Route path="/manager" element={<ManagerDashboardPage />} />
              <Route path="/manager/restaurants" element={<ManagerRestaurantsPage />} />
              <Route path="/manager/tables" element={<ManagerTablesPage />} />
              <Route path="/manager/reservations" element={<ManagerReservationsPage />} />
            </Route>
            <Route path="*" element={<Navigate to="/" replace />} />
          </Route>
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;
