import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  addAddress,
  getAddresses,
  requestPasswordResetLink,
  updateSavedAddress,
  updateProfile,
} from "../api/authApi";
import PanelNavbar from "../components/PanelNavbar";
import { useAuth } from "../context/useAuth";
import styles from "../styles/dashboard.module.css";

const getAddressForm = (user) => {
  if (user?.addressId || user?.houseNo || user?.city || user?.pincode || user?.state) {
    return {
      addressId: user?.addressId || "",
      houseNo: user?.houseNo || "",
      street: user?.street || "",
      city: user?.city || "",
      pincode: user?.pincode || "",
      state: user?.state || "",
    };
  }

  const parts = (user?.address || "").split(",").map((part) => part.trim()).filter(Boolean);
  if (parts.length >= 4) {
    return {
      houseNo: parts[0] || "",
      street: parts.length > 4 ? parts.slice(1, -3).join(", ") : "",
      city: parts.at(-3) || "",
      pincode: parts.at(-2) || "",
      state: parts.at(-1) || "",
    };
  }

  return { addressId: "", houseNo: "", street: "", city: "", pincode: "", state: "" };
};

export default function ProfilePage() {
  const { user, updateUser } = useAuth();
  const navigate = useNavigate();
  const isAdmin = user?.role === "ADMIN";
  const [profileForm, setProfileForm] = useState({ name: user?.name || "", email: user?.email || "" });
  const [addressForm, setAddressForm] = useState(() => getAddressForm(user));
  const [addresses, setAddresses] = useState(user?.addresses || []);
  const [addressMode, setAddressMode] = useState("add");
  const [activeModal, setActiveModal] = useState("");
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [passwordResetMessage, setPasswordResetMessage] = useState("");
  const [savingProfile, setSavingProfile] = useState(false);
  const [savingAddress, setSavingAddress] = useState(false);
  const [sendingResetLink, setSendingResetLink] = useState(false);
  const sessionExpired = error.includes("session expired");
  const savedAddress = user?.address || [user?.houseNo, user?.street, user?.city, user?.pincode, user?.state].filter(Boolean).join(", ");

  useEffect(() => {
    let active = true;

    getAddresses()
      .then((res) => {
        if (active) {
          setAddresses(res.data || []);
        }
      })
      .catch(() => {
        if (active) {
          setAddresses(user?.addresses || []);
        }
      });

    return () => {
      active = false;
    };
  }, [user?.addresses]);

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

  const closeModal = () => {
    setActiveModal("");
    setError("");
    setPasswordResetMessage("");
  };

  const closeAddressForm = () => {
    setActiveModal("addressBook");
    setError("");
  };

  const handleProfileSubmit = async (event) => {
    event.preventDefault();
    setMessage("");
    setError("");

    if (!profileForm.name.trim()) {
      setError("Name is required");
      return;
    }

    setSavingProfile(true);
    try {
      const res = await updateProfile({
        name: profileForm.name.trim(),
        email: profileForm.email.trim().toLowerCase(),
      });
      const { token, ...userData } = res.data;
      updateUser(userData, token);
      setMessage("Name updated successfully");
      closeModal();
    } catch (err) {
      setError(getErrorMessage(err, "Name could not be updated"));
    } finally {
      setSavingProfile(false);
    }
  };

  const handleSendResetLink = async () => {
    setMessage("");
    setError("");
    setPasswordResetMessage("");
    setSendingResetLink(true);

    try {
      const email = profileForm.email.trim().toLowerCase();
      const res = await requestPasswordResetLink({ email });
      setPasswordResetMessage(
        `${res.data?.message || "Password reset link sent to your email"}. It expires in ${
          res.data?.expiresInMinutes || 15
        } minutes.`
      );
    } catch (err) {
      setError(getErrorMessage(err, "Reset link could not be sent"));
    } finally {
      setSendingResetLink(false);
    }
  };

  const openAddAddress = () => {
    setAddressMode("add");
    setAddressForm(getAddressForm(null));
    setError("");
    setActiveModal("addressForm");
  };

  const openUpdateAddress = (address) => {
    setAddressMode(address.addressId ? "update" : "add");
    setAddressForm(getAddressForm(address));
    setError("");
    setActiveModal("addressForm");
  };

  const handleAddressSubmit = async (event) => {
    event.preventDefault();
    setMessage("");
    setError("");

    if (!addressForm.houseNo.trim() || !addressForm.city.trim() || !addressForm.pincode.trim() || !addressForm.state.trim()) {
      setError("House / flat no, city, pincode, and state are required");
      return;
    }

    if (!/^[0-9]{6}$/.test(addressForm.pincode.trim())) {
      setError("Pincode must be 6 digits");
      return;
    }

    setSavingAddress(true);
    try {
      const payload = {
        houseNo: addressForm.houseNo.trim(),
        street: addressForm.street.trim(),
        city: addressForm.city.trim(),
        pincode: addressForm.pincode.trim(),
        state: addressForm.state.trim(),
      };
      const res =
        addressMode === "update" && addressForm.addressId
          ? await updateSavedAddress(addressForm.addressId, payload)
          : await addAddress(payload);
      const nextAddresses =
        addressMode === "update"
          ? addresses.map((address) => (address.addressId === res.data.addressId ? res.data : address))
          : [res.data, ...addresses];
      setAddresses(nextAddresses);
      const defaultAddress = nextAddresses.find((address) => address.defaultAddress) || nextAddresses[0];
      updateUser({ ...user, address: defaultAddress?.fullAddress || user?.address, addresses: nextAddresses });
      setMessage(addressMode === "update" ? "Address updated successfully" : "Address added successfully");
      setActiveModal("addressBook");
    } catch (err) {
      setError(getErrorMessage(err, "Address could not be saved"));
    } finally {
      setSavingAddress(false);
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

        <section className={styles.accountShell}>
          <div className={styles.accountHero}>
            <div className={styles.accountAvatarLarge}>{(profileForm.name || "U").charAt(0).toUpperCase()}</div>
            <div>
              <span>Account Center</span>
              <h2>{profileForm.name || "Your account"}</h2>
              <p>{profileForm.email}</p>
            </div>
          </div>

          <div className={styles.accountRows}>
            <div className={styles.accountRow}>
              <div>
                <span>Profile name</span>
                <strong>{profileForm.name || "Not set"}</strong>
              </div>
              <button type="button" onClick={() => setActiveModal("name")}>
                Edit
              </button>
            </div>
            <div className={styles.accountRow}>
              <div>
                <span>Email address</span>
                <strong>{profileForm.email}</strong>
              </div>
              <small>Used for login and order updates</small>
            </div>
            <div className={styles.accountRow}>
              <div>
                <span>Saved addresses</span>
                <strong>{addresses.length ? `${addresses.length} saved address${addresses.length > 1 ? "es" : ""}` : savedAddress || "No saved address"}</strong>
              </div>
              <button type="button" onClick={() => setActiveModal("addressBook")}>
                Manage
              </button>
            </div>
            <div className={styles.accountRow}>
              <div>
                <span>Password</span>
                <strong>Email link verification</strong>
              </div>
              <button type="button" onClick={() => setActiveModal("password")}>
                Update
              </button>
            </div>
          </div>
        </section>

        <div className={styles.actions}>
          <button type="button" onClick={() => navigate("/dashboard")}>
            Dashboard
          </button>
        </div>
      </main>

      {activeModal === "name" && (
        <div className={styles.modalOverlay} role="presentation">
          <form className={styles.modalCard} onSubmit={handleProfileSubmit}>
            <div className={styles.modalHeader}>
              <h2>Update name</h2>
              <button type="button" onClick={closeModal} aria-label="Close">
                x
              </button>
            </div>
            {error && <p className={styles.errorBanner}>{error}</p>}
            <label>
              <span>Name</span>
              <input
                value={profileForm.name}
                onChange={(event) => setProfileForm({ ...profileForm, name: event.target.value })}
              />
            </label>
            <button type="submit" disabled={savingProfile}>
              {savingProfile ? "Saving..." : "Save Name"}
            </button>
          </form>
        </div>
      )}

      {activeModal === "addressBook" && (
        <div className={styles.modalOverlay} role="presentation">
          <section className={`${styles.modalCard} ${styles.addressBookModal}`}>
            <div className={styles.modalHeader}>
              <h2>Your Addresses</h2>
              <button type="button" onClick={closeModal} aria-label="Close">
                x
              </button>
            </div>

            <div className={styles.addressCards}>
              <button type="button" className={styles.addAddressTile} onClick={openAddAddress}>
                <span>+</span>
                <strong>Add address</strong>
              </button>

              {addresses.map((address) => (
                <article className={styles.addressCard} key={address.addressId || address.fullAddress}>
                  {address.defaultAddress && <span className={styles.defaultBadge}>Default</span>}
                  {address.source === "order" && <span className={styles.orderAddressBadge}>From order</span>}
                  <strong>{address.houseNo}</strong>
                  {address.street && <p>{address.street}</p>}
                  <p>
                    {address.city}, {address.pincode}
                  </p>
                  <p>{address.state}</p>
                  <button type="button" onClick={() => openUpdateAddress(address)}>
                    {address.addressId ? "Edit" : "Save & Edit"}
                  </button>
                </article>
              ))}
            </div>
          </section>
        </div>
      )}

      {activeModal === "addressForm" && (
        <div className={styles.modalOverlay} role="presentation">
          <form className={styles.modalCard} onSubmit={handleAddressSubmit}>
            <div className={styles.modalHeader}>
              <h2>{addressMode === "update" ? "Update address" : "Add address"}</h2>
              <button type="button" onClick={closeAddressForm} aria-label="Close">
                x
              </button>
            </div>
            {error && <p className={styles.errorBanner}>{error}</p>}
            <div className={styles.addressFields}>
              <label>
                <span>House / Flat No.</span>
                <input
                  value={addressForm.houseNo}
                  onChange={(event) => setAddressForm({ ...addressForm, houseNo: event.target.value })}
                  maxLength="120"
                />
              </label>
              <label>
                <span>Street / Area</span>
                <input
                  value={addressForm.street}
                  onChange={(event) => setAddressForm({ ...addressForm, street: event.target.value })}
                  maxLength="160"
                />
              </label>
              <label>
                <span>City</span>
                <input
                  value={addressForm.city}
                  onChange={(event) => setAddressForm({ ...addressForm, city: event.target.value })}
                  maxLength="80"
                />
              </label>
              <label>
                <span>Pincode</span>
                <input
                  value={addressForm.pincode}
                  onChange={(event) => setAddressForm({ ...addressForm, pincode: event.target.value })}
                  inputMode="numeric"
                  maxLength="6"
                />
              </label>
              <label>
                <span>State</span>
                <input
                  value={addressForm.state}
                  onChange={(event) => setAddressForm({ ...addressForm, state: event.target.value })}
                  maxLength="80"
                />
              </label>
            </div>
            <button type="submit" disabled={savingAddress}>
              {savingAddress ? "Saving..." : addressMode === "update" ? "Update Address" : "Add Address"}
            </button>
          </form>
        </div>
      )}

      {activeModal === "password" && (
        <div className={styles.modalOverlay} role="presentation">
          <section className={styles.modalCard}>
            <div className={styles.modalHeader}>
              <h2>Update password</h2>
              <button type="button" onClick={closeModal} aria-label="Close">
                x
              </button>
            </div>
            {error && <p className={styles.errorBanner}>{error}</p>}
            {passwordResetMessage && <p className={styles.passwordResetNote}>{passwordResetMessage}</p>}
            <p className={styles.passwordResetNote}>
              We will send a secure link to {profileForm.email}. Open it to set a new password.
            </p>
            <button type="button" onClick={handleSendResetLink} disabled={sendingResetLink}>
              {sendingResetLink ? "Sending link..." : "Send reset link"}
            </button>
          </section>
        </div>
      )}
    </div>
  );
}
