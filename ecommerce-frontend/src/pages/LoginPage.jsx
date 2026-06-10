import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { loginUser, requestForgotPasswordLink } from "../api/authApi";
import { useAuth } from "../context/useAuth";
import styles from "../styles/auth.module.css";

export default function LoginPage() {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [form, setForm] = useState({ email: "", password: "" });
  const [forgotForm, setForgotForm] = useState({ email: "" });
  const [forgotOpen, setForgotOpen] = useState(false);
  const [forgotMessage, setForgotMessage] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
  const [errors, setErrors] = useState(() => {
    const authMessage = sessionStorage.getItem("authMessage");
    if (authMessage) {
      sessionStorage.removeItem("authMessage");
      return { general: authMessage };
    }
    return {};
  });
  const [loading, setLoading] = useState(false);
  const [sendingLink, setSendingLink] = useState(false);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
    setErrors({ ...errors, [e.target.name]: "" });
    setSuccessMessage("");
  };

  const getAuthErrorMessage = (err) => {
    const data = err.response?.data;
    if (data?.error) return data.error;
    if (data && typeof data === "object") {
      return Object.values(data).find(Boolean) || "Bad credentials";
    }
    return "Bad credentials";
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setErrors({});
    try {
      const res = await loginUser({ ...form, email: form.email.trim().toLowerCase() });
      const { token, ...userData } = res.data;
      if (!token) {
        setErrors({ general: "Login succeeded, but no JWT token was returned." });
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

  const getErrorMessage = (err, fallback) => {
    const data = err.response?.data;
    if (data?.error) return data.error;
    if (data && typeof data === "object") return Object.values(data)[0];
    return fallback;
  };

  const openForgotPassword = () => {
    setForgotOpen(true);
    setForgotMessage("");
    setSuccessMessage("");
    setErrors({});
    setForgotForm({ email: form.email.trim().toLowerCase() });
  };

  const sendForgotLink = async (event) => {
    event.preventDefault();
    setErrors({});
    setForgotMessage("");
    if (!forgotForm.email.trim()) {
      setErrors({ forgot: "Enter your email first" });
      return;
    }

    setSendingLink(true);
    try {
      const res = await requestForgotPasswordLink({ email: forgotForm.email.trim().toLowerCase() });
      setForgotMessage(
        `${res.data?.message || "Password reset link sent to your email"}. It expires in ${
          res.data?.expiresInMinutes || 15
        } minutes.`
      );
      setForm({ email: forgotForm.email.trim().toLowerCase(), password: "" });
    } catch (err) {
      setErrors({ forgot: getErrorMessage(err, "Reset link could not be sent") });
    } finally {
      setSendingLink(false);
    }
  };

  return (
    <div className={styles.authPage}>
      <section className={styles.authCard}>
        <span className={styles.formEyebrow}>ShopEase Account</span>
        <h1 className={styles.title}>Sign in</h1>
        <p className={styles.subtitle}>Enter your details to continue shopping.</p>
        {successMessage && <p className={styles.successBanner}>{successMessage}</p>}
        {errors.general && <p className={styles.errorBanner} role="alert">{errors.general}</p>}
        <form id="login-form" onSubmit={handleSubmit} className={styles.form}>
          <div className={styles.field}>
            <label>Email</label>
            <input id="login-email" name="email" type="email" placeholder="you@example.com" value={form.email} onChange={handleChange} />
            {errors.email && <span className={styles.error}>{errors.email}</span>}
          </div>
          <div className={styles.field}>
            <label>Password</label>
            <input id="login-password" name="password" type="password" placeholder="Your password" value={form.password} onChange={handleChange} />
            {errors.password && <span className={styles.error}>{errors.password}</span>}
          </div>
          <button type="button" className={styles.textButton} onClick={openForgotPassword}>
            Forgot password?
          </button>
          <button id="login-submit" type="submit" className={styles.btn} disabled={loading}>
            {loading ? "Signing in..." : "Sign in"}
          </button>
        </form>
        <p className={styles.switchLink}>
          No account yet? <Link to="/register">Create account</Link>
        </p>
      </section>

      {forgotOpen && (
        <div className={styles.modalOverlay} role="presentation">
          <form className={styles.modalCard} onSubmit={sendForgotLink} autoComplete="off">
            <div className={styles.modalHeader}>
              <h2>Reset password</h2>
              <button type="button" onClick={() => setForgotOpen(false)} aria-label="Close">
                x
              </button>
            </div>
            {errors.forgot && <p className={styles.errorBanner} role="alert">{errors.forgot}</p>}
            {forgotMessage && <p className={styles.successBanner}>{forgotMessage}</p>}
            <div className={styles.field}>
              <label>Email</label>
              <input
                type="email"
                value={forgotForm.email}
                onChange={(event) => setForgotForm({ ...forgotForm, email: event.target.value })}
              />
            </div>
            <button type="submit" className={styles.btn} disabled={sendingLink}>
              {sendingLink ? "Sending link..." : "Send reset link"}
            </button>
          </form>
        </div>
      )}
    </div>
  );
}
