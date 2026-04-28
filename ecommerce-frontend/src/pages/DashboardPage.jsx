import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import styles from "../styles/auth.module.css";

export default function DashboardPage() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <div className={styles.authPage}>
      <div className={styles.authCard}>
        <h1 className={styles.title}>Dashboard</h1>
        <p className={styles.subtitle}>Welcome, sir {user?.name}!</p>
        <p style={{ color: "#888", marginBottom: "24px" }}>Role: {user?.role}</p>
        <button
          className={styles.btn}
          onClick={() => navigate("/products")}
          style={{ marginBottom: "12px" }}
        >
          Browse Products
        </button>
        <button className={styles.btn} onClick={handleLogout}>
          Logout
        </button>
      </div>
    </div>
  );
}