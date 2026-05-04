import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { addToCart } from "../api/cartApi";
import { getProductById } from "../api/ProductApi";
import styles from "../styles/products.module.css";

export default function ProductDetailsPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [cartMessage, setCartMessage] = useState("");
  const [saving, setSaving] = useState(false);

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

  const handleAddToCart = async () => {
    setSaving(true);
    setCartMessage("");
    setError("");

    try {
      await addToCart(product.productId, 1);
      setCartMessage("Product added to cart");
    } catch (err) {
      setError(err.response?.data?.error || "Product could not be added to cart");
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className={styles.page}>
      <div className={styles.topBar}>
        <h1>Product Details</h1>
        <button type="button" onClick={() => navigate("/products")}>
          Back
        </button>
      </div>

      {loading && <p className={styles.message}>Loading...</p>}
      {cartMessage && <p className={styles.success}>{cartMessage}</p>}
      {error && <p className={styles.error}>{error}</p>}

      {!loading && !error && product && (
        <section className={styles.detailLayout}>
          <img
            className={styles.detailImage}
            src={product.imageUrl || "https://placehold.co/640x420"}
            alt={product.name}
          />

          <div className={styles.detailInfo}>
            <span className={styles.category}>{product.category}</span>
            <h2>{product.name}</h2>
            <p>{product.description || "No description available."}</p>

            <div className={styles.detailMeta}>
              <strong>Rs. {product.price}</strong>
              <span>{product.stock > 0 ? `${product.stock} in stock` : "Out of stock"}</span>
            </div>

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
