import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import styles from "../styles/dashboard.module.css";
export default function DashboardPage() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <div className={styles.layout}>
      
      {/* ===== SIDEBAR ===== */}
      <aside className={styles.sidebar}>
        <h2 className={styles.logo}>Ecoomerce Web</h2>

        <nav className={styles.nav}>
          <button onClick={() => navigate("/products")}>
            Products
          </button>

          <button onClick={handleLogout}>
            Logout
          </button>
        </nav>
      </aside>

      {/* ===== MAIN CONTENT ===== */}
      <main className={styles.content}>
        <h1 className={styles.title}>Dashboard</h1>

        <div className={styles.card}>
          <h3>Welcome back 👋</h3>
          <p><strong>Name:</strong> {user?.name}</p>
          <p><strong>Role:</strong> {user?.role}</p>
        </div>

        <div className={styles.actions}>
          <button onClick={() => navigate("/products")}>
            Browse Products
          </button>
        </div>
      </main>
    </div>
  );
}