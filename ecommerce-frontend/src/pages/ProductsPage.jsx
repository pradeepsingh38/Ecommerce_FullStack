import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getAllProducts, searchProducts } from "../api/ProductApi";
import { addToCart } from "../api/cartApi";
import { useAuth } from "../context/useAuth";
import styles from "../styles/products.module.css";

export default function ProductsPage() {
  const [allProducts, setAllProducts] = useState([]);
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [cartMessage, setCartMessage] = useState("");
  const [keyword, setKeyword] = useState("");
  const [category, setCategory] = useState("");
  const { user } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    let active = true;

    const loadInitialProducts = async () => {
      try {
        const res = await getAllProducts();
        if (active) {
          setAllProducts(res.data);
          setProducts(res.data);
        }
      } catch {
        if (active) {
          setError("Failed to load products");
        }
      } finally {
        if (active) {
          setLoading(false);
        }
      }
    };

    loadInitialProducts();

    return () => {
      active = false;
    };
  }, []);

  const handleSearch = async (event) => {
    event.preventDefault();
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
    setProducts(allProducts);
    setCartMessage("");
  };

  const handleCategoryChange = (nextCategory) => {
    setCategory(nextCategory);
    setCartMessage("");

    const normalizedKeyword = keyword.trim().toLowerCase();
    const filteredProducts = allProducts.filter((product) => {
      const matchesCategory = !nextCategory || product.category === nextCategory;
      const matchesKeyword = !normalizedKeyword || product.name.toLowerCase().includes(normalizedKeyword);
      return matchesCategory && matchesKeyword;
    });

    setProducts(filteredProducts);
  };

  const handleAddToCart = async (event, product) => {
    event.stopPropagation();
    setCartMessage("");
    setError("");

    try {
      await addToCart(product.productId, 1);
      setCartMessage(`${product.name} added to cart`);
    } catch (err) {
      setError(err.response?.data?.error || "Product could not be added to cart");
    }
  };

  const handleCardKeyDown = (event, productId) => {
    if (event.key === "Enter" || event.key === " ") {
      event.preventDefault();
      navigate(`/products/${productId}`);
    }
  };

  const categories = [...new Set(allProducts.map((p) => p.category).filter(Boolean))];

  return (
    <div className={styles.page}>
      <div className={styles.topBar}>
        <h1>Explore Products</h1>
        <div className={styles.topActions}>
          {user?.role === "ADMIN" && (
            <button type="button" onClick={() => navigate("/products/new")}>
              Add Product
            </button>
          )}
          <button type="button" onClick={() => navigate("/cart")}>
            Cart
          </button>
          <button type="button" onClick={() => navigate("/dashboard")}>
            Back
          </button>
        </div>
      </div>

      <form onSubmit={handleSearch} className={styles.searchBar}>
        <input
          type="text"
          placeholder="Search anything..."
          value={keyword}
          onChange={(event) => setKeyword(event.target.value)}
          className={styles.searchInput}
        />

        <select
          value={category}
          onChange={(event) => handleCategoryChange(event.target.value)}
          className={styles.select}
        >
          <option value="">All</option>
          {categories.map((cat) => (
            <option key={cat} value={cat}>
              {cat}
            </option>
          ))}
        </select>

        <button type="submit" className={styles.searchBtn}>
          Search
        </button>
        <button type="button" onClick={handleReset} className={styles.resetBtn}>
          Reset
        </button>
      </form>

      {loading && <p className={styles.message}>Loading...</p>}
      {cartMessage && <p className={styles.success}>{cartMessage}</p>}
      {error && <p className={styles.error}>{error}</p>}
      {!loading && !error && products.length === 0 && (
        <p className={styles.message}>No products found</p>
      )}

      {!loading && !error && (
        <div className={styles.grid}>
          {products.map((product) => (
            <article
              key={product.productId}
              role="button"
              tabIndex="0"
              className={styles.card}
              onClick={() => navigate(`/products/${product.productId}`)}
              onKeyDown={(event) => handleCardKeyDown(event, product.productId)}
            >
              <div className={styles.imageWrapper}>
                <img src={product.imageUrl || "https://placehold.co/300x200"} alt={product.name} />
                <div className={styles.overlay}>
                  <span>View Details</span>
                </div>
              </div>

              <div className={styles.cardBody}>
                <h3>{product.name}</h3>
                <p>{product.category}</p>

                <div className={styles.cardFooter}>
                  <span>Rs. {product.price}</span>
                  <span>{product.stock > 0 ? "In Stock" : "Out"}</span>
                </div>

                <button
                  type="button"
                  className={styles.addCartBtn}
                  onClick={(event) => handleAddToCart(event, product)}
                  disabled={product.stock <= 0}
                >
                  Add to Cart
                </button>
              </div>
            </article>
          ))}
        </div>
      )}
    </div>
  );
}
