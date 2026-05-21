import { Link } from "react-router-dom";
import styles from "../styles/siteFooter.module.css";

export default function SiteFooter() {
  return (
    <footer className={styles.siteFooter}>
      <div className={styles.footerInner}>
        <section className={styles.footerBrand}>
          <span className={styles.footerMark}>SE</span>
          <div>
            <strong>ShopEase</strong>
            <p>Fresh products, secure checkout, and simple order tracking for everyday shopping.</p>
          </div>
        </section>

        <nav className={styles.footerColumn} aria-label="Shopping links">
          <h2>Shop</h2>
          <Link to="/products">Products</Link>
          <Link to="/cart">Cart</Link>
          <Link to="/my-orders">My Orders</Link>
        </nav>

        <nav className={styles.footerColumn} aria-label="Account links">
          <h2>Account</h2>
          <Link to="/profile">Profile</Link>
          <Link to="/dashboard">Dashboard</Link>
          <Link to="/login">Sign in</Link>
        </nav>

        <section className={styles.footerColumn}>
          <h2>Shop Promise</h2>
          <span>Secure payments</span>
          <span>Fast cart flow</span>
          <span>Email password reset</span>
        </section>
      </div>

      <div className={styles.footerBottom}>
        <span>© 2026 ShopEase</span>
        <span>Built for smooth ecommerce shopping</span>
      </div>
    </footer>
  );
}
