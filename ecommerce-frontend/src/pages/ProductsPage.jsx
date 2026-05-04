import { useState, useEffect } from "react";
import { getAllProducts, searchProducts } from "../api/productApi";
import { useNavigate } from "react-router-dom";
import styles from "../styles/products.module.css";

export default function ProductsPage() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [keyword, setKeyword] = useState("");
  const [category, setCategory] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    fetchProducts();
  }, []);

  const fetchProducts = async () => {
    setLoading(true);
    setError("");
    try {
      const res = await getAllProducts();
      setProducts(res.data);
    } catch {
      setError("Failed to load products");
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    try {
      const res = await searchProducts(keyword, category);
      setProducts(res.data);
    } catch {
      setError("Search failed");
    } finally {
      setLoading(false);
    }
  };

  const handleReset = () => {
    setKeyword("");
    setCategory("");
    fetchProducts();
  };

  const categories = [...new Set(products.map((p) => p.category))];

  return (
    <div className={styles.page}>

      {/* 🔝 TOP BAR */}
      <div className={styles.topBar}>
        <h1>Explore Products</h1>
        <button onClick={() => navigate("/dashboard")}>← Back</button>
      </div>

      {/* 🔍 SEARCH */}
      <form onSubmit={handleSearch} className={styles.searchBar}>
        <input
          type="text"
          placeholder="Search anything..."
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
          className={styles.searchInput}
        />

        <select
          value={category}
          onChange={(e) => setCategory(e.target.value)}
          className={styles.select}
        >
          <option value="">All</option>
          {categories.map((cat) => (
            <option key={cat} value={cat}>{cat}</option>
          ))}
        </select>

        <button type="submit" className={styles.searchBtn}>Search</button>
        <button type="button" onClick={handleReset} className={styles.resetBtn}>Reset</button>
      </form>

      {/* STATES */}
      {loading && <p className={styles.message}>Loading...</p>}
      {error && <p className={styles.error}>{error}</p>}
      {!loading && !error && products.length === 0 && (
        <p className={styles.message}>No products found 🚫</p>
      )}

      {/* 🧱 GRID */}
      {!loading && !error && (
        <div className={styles.grid}>
          {products.map((product) => (
            <div
              key={product.productId}
              className={styles.card}
              onClick={() => navigate(`/products/${product.productId}`)}
            >
              <div className={styles.imageWrapper}>
                <img
                  src={product.imageUrl || "https://placehold.co/300x200"}
                  alt={product.name}
                />
                <div className={styles.overlay}>
                  <span>View Details</span>
                </div>
              </div>

              <div className={styles.cardBody}>
                <h3>{product.name}</h3>
                <p>{product.category}</p>

                <div className={styles.cardFooter}>
                  <span>₹{product.price}</span>
                  <span>
                    {product.stock > 0 ? "In Stock" : "Out"}
                  </span>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}