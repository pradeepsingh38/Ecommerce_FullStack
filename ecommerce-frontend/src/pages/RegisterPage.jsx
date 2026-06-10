import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { registerUser } from "../api/authApi";
import { useAuth } from "../context/useAuth";
import styles from "../styles/register.module.css";

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

  const getAuthErrorMessage = (err) => {
    const data = err.response?.data;
    if (data?.error) return data.error;
    if (data && typeof data === "object") {
      return Object.values(data).find(Boolean) || "Bad credentials";
    }
    return "Bad credentials";
  };

  const validateForm = () => {
    const nextErrors = {};
    const email = form.email.trim();

    if (!form.name.trim()) {
      nextErrors.name = "Name is required";
    }
    if (!email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      nextErrors.email = "Enter a valid email address";
    }
    if (!form.password.trim()) {
      nextErrors.password = "Password is required";
    }

    if (Object.keys(nextErrors).length > 0) {
      setErrors({ ...nextErrors, general: "Bad credentials" });
      return false;
    }

    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validateForm()) {
      return;
    }
    setLoading(true);
    setErrors({});
    try {
      const res = await registerUser(form);
      const { token, ...userData } = res.data;
      if (!token) {
        setErrors({ general: "Registration succeeded, but no JWT token was returned." });
        return;
      }
      login(userData, token);
      navigate("/dashboard");
    } catch (err) {
      const data = err.response?.data;
      if (data?.error) {
        setErrors({ general: data.error });
      } else if (data && typeof data === "object") {
        setErrors({ ...data, general: getAuthErrorMessage(err) });
      } else {
        setErrors({ general: "Bad credentials" });
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={styles.container}>
      <section className={styles.card}>
        <span className={styles.formEyebrow}>ShopEase Account</span>
        <h1>Sign up</h1>
        <p className={styles.subtitle}>Fill in your details to create a new customer account.</p>

        {errors.general && <p className={styles.errorBanner} role="alert">{errors.general}</p>}

        <form id="signup-form" onSubmit={handleSubmit} noValidate>
          <label>
            <span>Full Name</span>
            <input
              id="signup-name"
              name="name"
              placeholder="Enter your name"
              value={form.name}
              onChange={handleChange}
            />
            {errors.name && <small>{errors.name}</small>}
          </label>

          <label>
            <span>Email</span>
            <input
              id="signup-email"
              name="email"
              type="email"
              placeholder="you@example.com"
              value={form.email}
              onChange={handleChange}
            />
            {errors.email && <small>{errors.email}</small>}
          </label>

          <label>
            <span>Password</span>
            <input
              id="signup-password"
              name="password"
              type="password"
              placeholder="Create password"
              value={form.password}
              onChange={handleChange}
            />
            {errors.password && <small>{errors.password}</small>}
          </label>

          <button id="signup-submit" type="submit" disabled={loading}>
            {loading ? "Creating..." : "Create account"}
          </button>
        </form>

        <p>
          Already have an account? <Link to="/login">Sign in</Link>
        </p>
      </section>
    </div>
  );
}
