import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { loginUser, requestForgotPasswordOtp, resetForgotPassword } from "../api/authApi";
import { useAuth } from "../context/useAuth";
import styles from "../styles/auth.module.css";

export default function LoginPage() {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [form, setForm] = useState({ email: "", password: "" });
  const [forgotForm, setForgotForm] = useState({ email: "", otp: "", newPassword: "" });
  const [forgotOpen, setForgotOpen] = useState(false);
  const [forgotMessage, setForgotMessage] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);
  const [sendingOtp, setSendingOtp] = useState(false);
  const [resetting, setResetting] = useState(false);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
    setErrors({ ...errors, [e.target.name]: "" });
    setSuccessMessage("");
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
        setErrors(data);
      } else {
        setErrors({ general: "Invalid email or password" });
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
    setForgotForm({ email: form.email.trim().toLowerCase(), otp: "", newPassword: "" });
  };

  const sendForgotOtp = async () => {
    setErrors({});
    setForgotMessage("");
    if (!forgotForm.email.trim()) {
      setErrors({ forgot: "Enter your email first" });
      return;
    }

    setSendingOtp(true);
    try {
      const res = await requestForgotPasswordOtp({ email: forgotForm.email.trim().toLowerCase() });
      setForgotMessage(
        `${res.data?.message || "Verification OTP sent to your email"}. It expires in ${
          res.data?.expiresInMinutes || 5
        } minutes.`
      );
    } catch (err) {
      setErrors({ forgot: getErrorMessage(err, "OTP could not be sent") });
    } finally {
      setSendingOtp(false);
    }
  };

  const handleForgotSubmit = async (e) => {
    e.preventDefault();
    setErrors({});
    setForgotMessage("");

    if (!forgotForm.email.trim() || !forgotForm.otp.trim() || !forgotForm.newPassword.trim()) {
      setErrors({ forgot: "Enter email, OTP, and new password" });
      return;
    }

    if (forgotForm.newPassword.length < 6) {
      setErrors({ forgot: "New password must be at least 6 characters" });
      return;
    }

    setResetting(true);
    try {
      await resetForgotPassword({
        email: forgotForm.email.trim().toLowerCase(),
        otp: forgotForm.otp.trim(),
        newPassword: forgotForm.newPassword,
      });
      setForm({ email: forgotForm.email.trim().toLowerCase(), password: "" });
      setForgotOpen(false);
      setSuccessMessage("Password reset successfully. Please sign in with your new password.");
    } catch (err) {
      setErrors({ forgot: getErrorMessage(err, "Password could not be reset") });
    } finally {
      setResetting(false);
    }
  };

  return (
    <div className={styles.authPage}>
      <section className={styles.authCard}>
        <span className={styles.formEyebrow}>ShopEase Account</span>
        <h1 className={styles.title}>Sign in</h1>
        <p className={styles.subtitle}>Enter your details to continue shopping.</p>
        {successMessage && <p className={styles.successBanner}>{successMessage}</p>}
        {errors.general && <p className={styles.errorBanner}>{errors.general}</p>}
        <form onSubmit={handleSubmit} className={styles.form}>
          <div className={styles.field}>
            <label>Email</label>
            <input name="email" type="email" placeholder="you@example.com" value={form.email} onChange={handleChange} />
            {errors.email && <span className={styles.error}>{errors.email}</span>}
          </div>
          <div className={styles.field}>
            <label>Password</label>
            <input name="password" type="password" placeholder="Your password" value={form.password} onChange={handleChange} />
            {errors.password && <span className={styles.error}>{errors.password}</span>}
          </div>
          <button type="button" className={styles.textButton} onClick={openForgotPassword}>
            Forgot password?
          </button>
          <button type="submit" className={styles.btn} disabled={loading}>
            {loading ? "Signing in..." : "Sign in"}
          </button>
        </form>
        <p className={styles.switchLink}>
          No account yet? <Link to="/register">Create account</Link>
        </p>
      </section>

      {forgotOpen && (
        <div className={styles.modalOverlay} role="presentation">
          <form className={styles.modalCard} onSubmit={handleForgotSubmit} autoComplete="off">
            <div className={styles.modalHeader}>
              <h2>Reset password</h2>
              <button type="button" onClick={() => setForgotOpen(false)} aria-label="Close">
                x
              </button>
            </div>
            {errors.forgot && <p className={styles.errorBanner}>{errors.forgot}</p>}
            {forgotMessage && <p className={styles.successBanner}>{forgotMessage}</p>}
            <div className={styles.field}>
              <label>Email</label>
              <input
                type="email"
                value={forgotForm.email}
                onChange={(event) => setForgotForm({ ...forgotForm, email: event.target.value })}
              />
            </div>
            <button type="button" className={styles.secondaryButton} onClick={sendForgotOtp} disabled={sendingOtp}>
              {sendingOtp ? "Sending OTP..." : "Send Verification OTP"}
            </button>
            <div className={styles.field}>
              <label>OTP</label>
              <input
                name="forgotOtp"
                type="text"
                inputMode="numeric"
                autoComplete="one-time-code"
                placeholder="Enter OTP"
                value={forgotForm.otp}
                onChange={(event) => setForgotForm({ ...forgotForm, otp: event.target.value })}
              />
            </div>
            <div className={styles.field}>
              <label>New Password</label>
              <input
                type="password"
                minLength="6"
                value={forgotForm.newPassword}
                onChange={(event) => setForgotForm({ ...forgotForm, newPassword: event.target.value })}
              />
            </div>
            <button type="submit" className={styles.btn} disabled={resetting}>
              {resetting ? "Resetting..." : "Reset Password"}
            </button>
          </form>
        </div>
      )}
    </div>
  );
}
