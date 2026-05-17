import { useNavigate } from "react-router-dom";
import PanelNavbar from "../components/PanelNavbar";
import { useAuth } from "../context/useAuth";
import styles from "../styles/dashboard.module.css";

export default function DashboardPage() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const isAdmin = user?.role === "ADMIN";
  const dashboardItems = isAdmin
    ? [
        { label: "Products", value: "Manage catalog", path: "/products" },
        { label: "Users", value: "Customer accounts", path: "/users" },
        { label: "Orders", value: "Order history", path: "/orders" },
      ]
    : [
        { label: "Products", value: "Browse catalog", path: "/products" },
        { label: "Cart", value: "View cart items", path: "/cart" },
        { label: "Orders", value: "Track purchases", path: "/my-orders" },
      ];

  return (
    <div className={`${styles.layout} ${isAdmin ? styles.adminLayout : ""}`}>
      <aside className={styles.sidebar}>
        <h2 className={styles.logo}>{isAdmin ? "Admin Panel" : "Ecommerce Web"}</h2>
        {isAdmin && <span className={styles.adminBadge}>Admin Management</span>}

        <nav className={styles.nav}>
          <button onClick={() => navigate("/products")}>
            {isAdmin ? "Manage Products" : "Products"}
          </button>

          {!isAdmin && (
            <button onClick={() => navigate("/cart")}>
              Cart
            </button>
          )}

          {!isAdmin && (
            <button onClick={() => navigate("/my-orders")}>
              My Orders
            </button>
          )}

          {isAdmin && (
            <button onClick={() => navigate("/products/new")}>
              Add Product
            </button>
          )}

          {isAdmin && (
            <button onClick={() => navigate("/users")}>
              Users
            </button>
          )}

          {isAdmin && (
            <button onClick={() => navigate("/orders")}>
              Order History
            </button>
          )}

          <button onClick={() => navigate("/profile")}>
            Profile
          </button>
        </nav>
      </aside>

      <main className={styles.content}>
        <PanelNavbar title={isAdmin ? "Admin Dashboard" : "Dashboard"} isAdmin={isAdmin} />

        <section className={styles.summaryGrid}>
          <article className={styles.welcomePanel}>
            <div>
              <span className={styles.heroEyebrow}>{isAdmin ? "Admin overview" : "User overview"}</span>
              <h2>{isAdmin ? "Welcome back, Admin" : `Welcome back, ${user?.name || "User"}`}</h2>
              <p>
                {isAdmin
                  ? "Review store activity and manage the catalog, users, and orders."
                  : "Continue shopping, review your cart, and check your recent orders."}
              </p>
            </div>
            <button type="button" onClick={() => navigate("/products")}>
              {isAdmin ? "Manage Products" : "Shop Products"}
            </button>
          </article>
        </section>

        <section className={styles.dashboardCards}>
          {dashboardItems.map((item) => (
            <button type="button" key={item.label} onClick={() => navigate(item.path)}>
              <span>{item.label}</span>
              <strong>{item.value}</strong>
            </button>
          ))}
        </section>
      </main>
    </div>
  );
}
