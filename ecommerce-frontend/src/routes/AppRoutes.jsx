import { Routes, Route, Navigate } from "react-router-dom";
import LoginPage from "../pages/LoginPage";
import RegisterPage from "../pages/RegisterPage";
import DashboardPage from "../pages/DashboardPage";
import ProductsPage from "../pages/ProductsPage";
import AddProductPage from "../pages/AddProductPage";
import ProductDetailsPage from "../pages/ProductDetailsPage";
import CartPage from "../pages/CartPage";
import ProtectedRoute from "../components/ProtectedRoute";
import AdminRoute from "../components/AdminRoute";

export default function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/login" replace />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route path="/dashboard" element={
        <ProtectedRoute><DashboardPage /></ProtectedRoute>
      }/>
      <Route path="/products" element={
        <ProtectedRoute><ProductsPage /></ProtectedRoute>
      }/>
      <Route path="/products/new" element={
        <ProtectedRoute><AdminRoute><AddProductPage /></AdminRoute></ProtectedRoute>
      }/>
      <Route path="/products/:id" element={
        <ProtectedRoute><ProductDetailsPage /></ProtectedRoute>
      }/>
      <Route path="/cart" element={
        <ProtectedRoute><CartPage /></ProtectedRoute>
      }/>
    </Routes>
  );
}
