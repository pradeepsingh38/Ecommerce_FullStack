import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { registerUser } from "../api/authApi";
import { useAuth } from "../context/AuthContext";
import styles from "../styles/auth.module.css";

export default function RegisterPage() {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [form, setForm] = useState({ name: "", email: "", password: "" });
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
    setErrors({ ...errors, [e.target.name]: "" });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const res = await registerUser(form);
      const { token, ...userData } = res.data;
      login(userData, token);
      navigate("/dashboard");
    } catch (err) {
      const data = err.response?.data;
      if (typeof data === "object") setErrors(data);
      else setErrors({ general: data?.error || "Registration failed" });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={styles.authPage}>
      <div className={styles.authCard}>
        <h1 className={styles.title}>Create account</h1>
        <p className={styles.subtitle}>Join us today</p>
        {errors.general && <p className={styles.errorBanner}>{errors.general}</p>}
        <form onSubmit={handleSubmit} className={styles.form}>
          <div className={styles.field}>
            <label>Full Name</label>
            <input name="name" placeholder="John Doe" value={form.name} onChange={handleChange} />
            {errors.name && <span className={styles.error}>{errors.name}</span>}
          </div>
          <div className={styles.field}>
            <label>Email</label>
            <input name="email" type="email" placeholder="you@example.com" value={form.email} onChange={handleChange} />
            {errors.email && <span className={styles.error}>{errors.email}</span>}
          </div>
          <div className={styles.field}>
            <label>Password</label>
            <input name="password" type="password" placeholder="Min 6 characters" value={form.password} onChange={handleChange} />
            {errors.password && <span className={styles.error}>{errors.password}</span>}
          </div>
          <button type="submit" className={styles.btn} disabled={loading}>
            {loading ? "Creating account..." : "Register"}
          </button>
        </form>
        <p className={styles.switchLink}>
          Already have an account? <Link to="/login">Sign in</Link>
        </p>
      </div>
    </div>
  );
}