import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { addToCart } from "../api/cartApi";
import { getProductById } from "../api/ProductApi";
import { useAuth } from "../context/useAuth";
import { handleProductImageError, productImageFallback } from "../utils/productImage";
import styles from "../styles/products.module.css";

export default function ProductDetailsPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [toast, setToast] = useState(null);
  const [saving, setSaving] = useState(false);
  const [quantity, setQuantity] = useState(1);

  const isAdmin = user?.role === "ADMIN";

  useEffect(() => {
    if (!toast) {
      return;
    }

    const timer = window.setTimeout(() => setToast(null), 3200);
    return () => window.clearTimeout(timer);
  }, [toast]);

  useEffect(() => {
    let active = true;

    const loadProduct = async () => {
      try {
        const res = await getProductById(id);
        if (active) {
          setProduct(res.data);
        }
      } catch {
        if (active) {
          setError("Product details not found");
        }
      } finally {
        if (active) {
          setLoading(false);
        }
      }
    };

    loadProduct();

    return () => {
      active = false;
    };
  }, [id]);

  const handleQuantityChange = (event) => {
    const nextQuantity = Number(event.target.value);
    const maxQuantity = product?.stock || 1;
    setQuantity(Math.min(Math.max(nextQuantity, 1), maxQuantity));
    setToast(null);
  };

  const handleAddToCart = async () => {
    setSaving(true);
    setToast(null);
    setError("");

    try {
      await addToCart(product.productId, quantity);
      setToast({
        type: "success",
        message: `${quantity} item${quantity > 1 ? "s" : ""} added to cart`,
      });
    } catch (err) {
      setToast({ type: "error", message: err.response?.data?.error || "Product could not be added to cart" });
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className={styles.page}>
      {toast && (
        <div className={`${styles.toast} ${toast.type === "error" ? styles.toastError : styles.toastSuccess}`}>
          <strong>{toast.type === "error" ? "Action failed" : "Success"}</strong>
          <span>{toast.message}</span>
        </div>
      )}

      <div className={styles.topBar}>
        <h1>Product Details</h1>
        <div className={styles.topActions}>
          {isAdmin && (
            <button type="button" onClick={() => navigate("/products/new")}>
              Add Product
            </button>
          )}
          <button type="button" onClick={() => navigate("/products")}>
            Products
          </button>
          <button type="button" onClick={() => navigate("/cart")}>
            Cart
          </button>
        </div>
      </div>

      {loading && <p className={styles.message}>Loading...</p>}
      {error && <p className={styles.error}>{error}</p>}

      {!loading && !error && product && (
        <section className={styles.detailLayout}>
          <div className={styles.detailMedia}>
            <img
              className={styles.detailImage}
              src={product.imageUrl || productImageFallback(product.name)}
              alt={product.name}
              onError={(event) => handleProductImageError(event, product.name)}
            />
            <span className={product.stock > 0 ? styles.stockBadge : styles.stockBadgeEmpty}>
              {product.stock > 0 ? "In Stock" : "Out of Stock"}
            </span>
          </div>

          <div className={styles.detailInfo}>
            <span className={styles.category}>{product.category}</span>
            <h2>{product.name}</h2>
            <p>{product.description || "No description available."}</p>

            <div className={styles.detailMeta}>
              <div>
                <span className={styles.metaLabel}>Price</span>
                <strong>Rs. {product.price}</strong>
              </div>
              <div>
                <span className={styles.metaLabel}>Available</span>
                <span>{product.stock > 0 ? `${product.stock} units` : "No stock"}</span>
              </div>
            </div>

            <label className={styles.quantityField}>
              Quantity
              <input
                type="number"
                min="1"
                max={product.stock || 1}
                value={quantity}
                onChange={handleQuantityChange}
                disabled={product.stock <= 0}
              />
            </label>

            <div className={styles.detailActions}>
              <button
                type="button"
                className={styles.searchBtn}
                onClick={handleAddToCart}
                disabled={saving || product.stock <= 0}
              >
                {saving ? "Adding..." : "Add to Cart"}
              </button>
              <button type="button" className={styles.resetBtn} onClick={() => navigate("/cart")}>
                View Cart
              </button>
            </div>
          </div>
        </section>
      )}
    </div>
  );
}
