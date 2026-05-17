import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/useAuth";
import styles from "../styles/dashboard.module.css";

export default function PanelNavbar({ title, isAdmin }) {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const initial = user?.name?.trim()?.charAt(0)?.toUpperCase() || "U";

  const handleLogout = async () => {
    await logout();
    navigate("/login");
  };

  return (
    <header className={styles.panelNavbar}>
      <div>
        <span className={isAdmin ? styles.adminBadge : styles.userBadge}>
          {isAdmin ? "Admin Panel" : "User Panel"}
        </span>
        <h1 className={styles.title}>{title}</h1>
      </div>

      <div className={styles.navProfile}>
        <button type="button" className={styles.profileButton} onClick={() => navigate("/profile")}>
          <span className={styles.avatar}>{initial}</span>
          <span className={styles.profileName}>{user?.name || "User"}</span>
        </button>
        <button type="button" className={styles.logoutButton} onClick={handleLogout}>
          Logout
        </button>
      </div>
    </header>
  );
}
