import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { addProduct } from "../api/ProductApi";
import styles from "../styles/products.module.css";

const initialForm = {
  name: "",
  description: "",
  price: "",
  stock: "",
  category: "",
  imageUrl: "",
};

export default function AddProductPage() {
  const navigate = useNavigate();
  const [form, setForm] = useState(initialForm);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  const handleChange = (event) => {
    const { name, value } = event.target;
    setForm((current) => ({ ...current, [name]: value }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setSaving(true);
    setError("");

    try {
      const payload = {
        ...form,
        price: Number(form.price),
        stock: Number(form.stock),
      };
      await addProduct(payload);
      navigate("/products");
    } catch (err) {
      setError(err.response?.data?.error || "Product could not be added");
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className={styles.page}>
      <div className={styles.topBar}>
        <h1>Add Product</h1>
        <button type="button" onClick={() => navigate("/products")}>
          Back
        </button>
      </div>

      <form className={styles.productForm} onSubmit={handleSubmit}>
        <input name="name" placeholder="Product name" value={form.name} onChange={handleChange} required />
        <input name="category" placeholder="Category" value={form.category} onChange={handleChange} required />
        <input name="price" type="number" min="1" step="0.01" placeholder="Price" value={form.price} onChange={handleChange} required />
        <input name="stock" type="number" min="0" placeholder="Stock" value={form.stock} onChange={handleChange} required />
        <input name="imageUrl" placeholder="Image URL" value={form.imageUrl} onChange={handleChange} />
        <textarea name="description" placeholder="Description" value={form.description} onChange={handleChange} rows="5" />

        {error && <p className={styles.errorCompact}>{error}</p>}

        <button className={styles.searchBtn} type="submit" disabled={saving}>
          {saving ? "Adding..." : "Add Product"}
        </button>
      </form>
    </div>
  );
}
