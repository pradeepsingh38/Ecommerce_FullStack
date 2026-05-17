import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { loginUser, updatePassword, updateProfile } from "../api/authApi";
import PanelNavbar from "../components/PanelNavbar";
import { useAuth } from "../context/useAuth";
import styles from "../styles/dashboard.module.css";

export default function ProfilePage() {
  const { user, updateUser } = useAuth();
  const navigate = useNavigate();
  const isAdmin = user?.role === "ADMIN";
  const [profileForm, setProfileForm] = useState({ name: user?.name || "", email: user?.email || "" });
  const [passwordForm, setPasswordForm] = useState({ currentPassword: "", newPassword: "" });
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [passwordDebug, setPasswordDebug] = useState(null);
  const [savingProfile, setSavingProfile] = useState(false);
  const [savingPassword, setSavingPassword] = useState(false);
  const sessionExpired = error.includes("session expired");

  const getErrorMessage = (err, fallback) => {
    const data = err.response?.data;
    const status = err.response?.status;
    if (data?.error) return data.error;
    if (data && typeof data === "object") return Object.values(data)[0];
    if (status === 404 || status === 405) return "Password API is not available. Please restart the backend server.";
    if (status === 401 || status === 403) return "Your session expired. Please login again.";
    if (status) return `${fallback} (HTTP ${status})`;
    return fallback;
  };

  const handleProfileSubmit = async (event) => {
    event.preventDefault();
    setMessage("");
    setError("");
    setPasswordDebug(null);
    setSavingProfile(true);

    try {
      const res = await updateProfile({
        ...profileForm,
        name: profileForm.name.trim(),
        email: profileForm.email.trim().toLowerCase(),
      });
      const { token, ...userData } = res.data;
      updateUser(userData, token);
      setMessage("Profile updated successfully");
    } catch (err) {
      setError(getErrorMessage(err, "Profile could not be updated"));
    } finally {
      setSavingProfile(false);
    }
  };

  const handlePasswordSubmit = async (event) => {
    event.preventDefault();
    setMessage("");
    setError("");

    if (!localStorage.getItem("token")) {
      setError("Your session expired. Please login again.");
      return;
    }

    if (!passwordForm.currentPassword.trim() || !passwordForm.newPassword.trim()) {
      setError("Enter both current password and new password");
      return;
    }

    if (passwordForm.newPassword.length < 6) {
      setError("New password must be at least 6 characters");
      return;
    }

    if (passwordForm.currentPassword === passwordForm.newPassword) {
      setError("New password must be different from current password");
      return;
    }

    setSavingPassword(true);

    try {
      const newPassword = passwordForm.newPassword;
      const email = profileForm.email.trim().toLowerCase();
      const res = await updatePassword({ ...passwordForm, email });
      await loginUser({ email: profileForm.email.trim().toLowerCase(), password: newPassword });
      setPasswordForm({ currentPassword: "", newPassword: "" });
      setMessage(res.data?.message || "Password updated successfully");
      setPasswordDebug(res.data || null);
    } catch (err) {
      const message = getErrorMessage(err, "Password could not be updated");
      setError(message === "Bad credentials" ? "Password update did not apply. New password login test failed." : message);
    } finally {
      setSavingPassword(false);
    }
  };

  return (
    <div className={`${styles.profilePage} ${isAdmin ? styles.adminLayout : ""}`}>
      <PanelNavbar title="Profile" isAdmin={isAdmin} />

      <main className={styles.profileContent}>
        {(message || error) && (
          <div className={error ? styles.errorBanner : styles.successBanner}>
            <span>{error || message}</span>
            {sessionExpired && (
              <button type="button" onClick={() => navigate("/login")}>
                Login
              </button>
            )}
          </div>
        )}

        {passwordDebug && (
          <div className={styles.passwordDebug}>
            <strong>Backend password update check</strong>
            <span>Version: {passwordDebug.backendVersion || "not returned"}</span>
            <span>Updated rows: {passwordDebug.updatedRows}</span>
            <span>New password verified: {String(passwordDebug.newPasswordVerified)}</span>
            <span>Old password rejected: {String(passwordDebug.oldPasswordRejected)}</span>
            <span>User: {passwordDebug.email}</span>
          </div>
        )}

        <section className={styles.profileGrid}>
          <form className={styles.profileCard} onSubmit={handleProfileSubmit}>
            <h2>Profile details</h2>
            <label>
              <span>Name</span>
              <input
                value={profileForm.name}
                onChange={(event) => setProfileForm({ ...profileForm, name: event.target.value })}
              />
            </label>
            <label>
              <span>Email</span>
              <input
                type="email"
                value={profileForm.email}
                onChange={(event) => setProfileForm({ ...profileForm, email: event.target.value })}
              />
            </label>
            <button type="submit" disabled={savingProfile}>
              {savingProfile ? "Saving..." : "Update Profile"}
            </button>
          </form>

          <form className={styles.profileCard} onSubmit={handlePasswordSubmit}>
            <h2>Password</h2>
            <label>
              <span>Current Password</span>
              <input
                type="password"
                required
                autoComplete="current-password"
                value={passwordForm.currentPassword}
                onChange={(event) => {
                  setError("");
                  setPasswordForm({ ...passwordForm, currentPassword: event.target.value });
                }}
              />
            </label>
            <label>
              <span>New Password</span>
              <input
                type="password"
                required
                minLength="6"
                autoComplete="new-password"
                value={passwordForm.newPassword}
                onChange={(event) => {
                  setError("");
                  setPasswordForm({ ...passwordForm, newPassword: event.target.value });
                }}
              />
            </label>
            <button type="submit" disabled={savingPassword}>
              {savingPassword ? "Saving..." : "Update Password"}
            </button>
          </form>
        </section>

        <div className={styles.actions}>
          <button type="button" onClick={() => navigate("/dashboard")}>
            Dashboard
          </button>
        </div>
      </main>
    </div>
  );
}
