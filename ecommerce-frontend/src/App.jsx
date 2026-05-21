import { BrowserRouter } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import SiteFooter from "./components/SiteFooter";
import AppRoutes from "./routes/AppRoutes";
import "./styles/global.css";

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <AppRoutes />
        <SiteFooter />
      </AuthProvider>
    </BrowserRouter>
  );
}
