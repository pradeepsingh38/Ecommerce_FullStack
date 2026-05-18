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
        { label: "Products", value: "Manage catalog", path: "/products", tone: "primary" },
        { label: "Users", value: "Customer accounts", path: "/users", tone: "blue" },
        { label: "Orders", value: "Order history", path: "/orders", tone: "green" },
      ]
    : [
        { label: "Products", value: "Browse catalog", path: "/products", tone: "primary" },
        { label: "Cart", value: "View cart items", path: "/cart", tone: "blue" },
        { label: "Orders", value: "Track purchases", path: "/my-orders", tone: "green" },
      ];
  const navItems = isAdmin
    ? [
        { label: "Manage Products", path: "/products" },
        { label: "Add Product", path: "/products/new" },
        { label: "Users", path: "/users" },
        { label: "Order History", path: "/orders" },
        { label: "Profile", path: "/profile" },
      ]
    : [
        { label: "Products", path: "/products" },
        { label: "Cart", path: "/cart" },
        { label: "My Orders", path: "/my-orders" },
        { label: "Profile", path: "/profile" },
      ];

  return (
    <div className={`${styles.layout} ${isAdmin ? styles.adminLayout : ""}`}>
      <aside className={styles.sidebar}>
        <div className={styles.brandBlock}>
          <div>
            <h2 className={styles.logo}>{isAdmin ? "Shop Admin" : "ShopEase"}</h2>
            <p>{isAdmin ? "Store control center" : "Online shopping"}</p>
          </div>
        </div>
        {isAdmin && <span className={styles.adminBadge}>Admin Management</span>}

        <nav className={styles.nav}>
          {navItems.map((item) => (
            <button key={item.path} onClick={() => navigate(item.path)}>
              <span>{item.label}</span>
            </button>
          ))}
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

        <section className={styles.quickStats}>
          <div>
            <span>Catalog</span>
            <strong>{isAdmin ? "Live inventory" : "Fresh picks"}</strong>
          </div>
          <div>
            <span>Orders</span>
            <strong>{isAdmin ? "Track fulfillment" : "Easy tracking"}</strong>
          </div>
          <div>
            <span>Checkout</span>
            <strong>{isAdmin ? "Customer ready" : "Fast cart flow"}</strong>
          </div>
        </section>

        <section className={styles.dashboardCards}>
          {dashboardItems.map((item) => (
            <button type="button" key={item.label} className={styles[item.tone]} onClick={() => navigate(item.path)}>
              <span>{item.label}</span>
              <strong>{item.value}</strong>
            </button>
          ))}
        </section>
      </main>
    </div>
  );
}
