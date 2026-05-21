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
        { icon: "PR", label: "Products", value: "Manage catalog", path: "/products", tone: "primary" },
        { icon: "US", label: "Users", value: "Customer accounts", path: "/users", tone: "blue" },
        { icon: "OR", label: "Orders", value: "Order history", path: "/orders", tone: "green" },
      ]
    : [
        { icon: "PR", label: "Products", value: "Browse catalog", path: "/products", tone: "primary" },
        { icon: "CA", label: "Cart", value: "View cart items", path: "/cart", tone: "blue" },
        { icon: "OR", label: "Orders", value: "Track purchases", path: "/my-orders", tone: "green" },
      ];
  const navItems = isAdmin
    ? [
        { icon: "PR", label: "Manage Products", path: "/products" },
        { icon: "+", label: "Add Product", path: "/products/new" },
        { icon: "US", label: "Users", path: "/users" },
        { icon: "OR", label: "Order History", path: "/orders" },
        { icon: "ME", label: "Profile", path: "/profile" },
      ]
    : [
        { icon: "PR", label: "Products", path: "/products" },
        { icon: "CA", label: "Cart", path: "/cart" },
        { icon: "OR", label: "My Orders", path: "/my-orders" },
        { icon: "ME", label: "Profile", path: "/profile" },
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
            <span className={`${styles.statBoxIcon} ${styles.catalogIcon}`} aria-hidden="true"></span>
            <small>Catalog</small>
            <strong>{isAdmin ? "Live inventory" : "Fresh picks"}</strong>
          </div>
          <div>
            <span className={`${styles.statBoxIcon} ${styles.orderIcon}`} aria-hidden="true"></span>
            <small>Orders</small>
            <strong>{isAdmin ? "Track fulfillment" : "Easy tracking"}</strong>
          </div>
          <div>
            <span className={`${styles.statBoxIcon} ${styles.checkoutIcon}`} aria-hidden="true"></span>
            <small>Checkout</small>
            <strong>{isAdmin ? "Customer ready" : "Fast cart flow"}</strong>
          </div>
        </section>

        <section className={styles.dashboardCards}>
          {dashboardItems.map((item) => (
            <button type="button" key={item.label} className={styles[item.tone]} onClick={() => navigate(item.path)}>
              <span className={styles.homeCardIcon} aria-hidden="true">{item.icon}</span>
              <span>{item.label}</span>
              <strong>{item.value}</strong>
            </button>
          ))}
        </section>

      </main>
    </div>
  );
}
