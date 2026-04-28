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

  // Load all products on first render
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

  // Get unique categories from loaded products for the dropdown
  const categories = [...new Set(products.map((p) => p.category))];

  return (
    <div className={styles.page}>

      {/* Header */}
      <div className={styles.header}>
        <h1 className={styles.title}>Products</h1>
      </div>

      {/* Search + Filter Bar */}
      <form onSubmit={handleSearch} className={styles.searchBar}>
        <input
          type="text"
          placeholder="Search products..."
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
          className={styles.searchInput}
        />
        <select
          value={category}
          onChange={(e) => setCategory(e.target.value)}
          className={styles.select}
        >
          <option value="">All Categories</option>
          {categories.map((cat) => (
            <option key={cat} value={cat}>{cat}</option>
          ))}
        </select>
        <button type="submit" className={styles.searchBtn}>Search</button>
        <button type="button" onClick={handleReset} className={styles.resetBtn}>Reset</button>
      </form>

      {/* States */}
      {loading && <p className={styles.message}>Loading products...</p>}
      {error && <p className={styles.error}>{error}</p>}
      {!loading && !error && products.length === 0 && (
        <p className={styles.message}>No products found.</p>
      )}

      {/* Product Grid */}
      {!loading && !error && (
        <div className={styles.grid}>
          {products.map((product) => (
            <div
              key={product.productId}
              className={styles.card}
              onClick={() => navigate(`/products/${product.productId}`)}
            >
              <img
                src={product.imageUrl || "https://placehold.co/300x200"}
                alt={product.name}
                className={styles.image}
              />
              <div className={styles.cardBody}>
                <span className={styles.category}>{product.category}</span>
                <h3 className={styles.productName}>{product.name}</h3>
                <p className={styles.description}>
                  {product.description?.slice(0, 80)}
                  {product.description?.length > 80 ? "..." : ""}
                </p>
                <div className={styles.cardFooter}>
                  <span className={styles.price}>₹{product.price}</span>
                  <span className={styles.stock}>
                    {product.stock > 0 ? `${product.stock} in stock` : "Out of stock"}
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