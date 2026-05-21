import { useState } from "react";
import { Link, useNavigate, useSearchParams } from "react-router-dom";
import { resetForgotPassword } from "../api/authApi";
import styles from "../styles/auth.module.css";

export default function ResetPasswordPage() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const token = searchParams.get("token") || "";
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [error, setError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
  const [resetting, setResetting] = useState(false);

  const getErrorMessage = (err, fallback) => {
    const data = err.response?.data;
    if (data?.error) return data.error;
    if (data && typeof data === "object") return Object.values(data)[0];
    return fallback;
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError("");
    setSuccessMessage("");

    if (!token) {
      setError("Reset link is missing or invalid. Please request a new password reset link.");
      return;
    }

    if (newPassword.length < 6) {
      setError("New password must be at least 6 characters");
      return;
    }

    if (newPassword !== confirmPassword) {
      setError("Passwords do not match");
      return;
    }

    setResetting(true);
    try {
      await resetForgotPassword({ token, newPassword });
      setSuccessMessage("Password reset successfully. Redirecting to sign in...");
      setNewPassword("");
      setConfirmPassword("");
      window.setTimeout(() => navigate("/login"), 1200);
    } catch (err) {
      setError(getErrorMessage(err, "Password could not be reset"));
    } finally {
      setResetting(false);
    }
  };

  return (
    <div className={styles.authPage}>
      <section className={styles.authCard}>
        <span className={styles.formEyebrow}>ShopEase Account</span>
        <h1 className={styles.title}>Reset password</h1>
        <p className={styles.subtitle}>Create a new password from your email verification link.</p>
        {error && <p className={styles.errorBanner}>{error}</p>}
        {successMessage && <p className={styles.successBanner}>{successMessage}</p>}
        <form onSubmit={handleSubmit} className={styles.form}>
          <div className={styles.field}>
            <label>New Password</label>
            <input
              type="password"
              minLength="6"
              autoComplete="new-password"
              value={newPassword}
              onChange={(event) => setNewPassword(event.target.value)}
            />
          </div>
          <div className={styles.field}>
            <label>Confirm Password</label>
            <input
              type="password"
              minLength="6"
              autoComplete="new-password"
              value={confirmPassword}
              onChange={(event) => setConfirmPassword(event.target.value)}
            />
          </div>
          <button type="submit" className={styles.btn} disabled={resetting}>
            {resetting ? "Resetting..." : "Reset Password"}
          </button>
        </form>
        <p className={styles.switchLink}>
          Back to <Link to="/login">sign in</Link>
        </p>
      </section>
    </div>
  );
}
