import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getAddresses } from "../api/authApi";
import { getCart } from "../api/cartApi";
import { placeOrder } from "../api/orderApi";
import { useAuth } from "../context/useAuth";
import { handleProductImageError, productImageFallback } from "../utils/productImage";
import styles from "../styles/products.module.css";

const initialForm = {
  houseNo: "",
  street: "",
  city: "",
  pincode: "",
  state: "",
  paymentMethod: "COD",
  contactNumber: "",
};

export default function CheckoutPage() {
  const navigate = useNavigate();
  const { user } = useAuth();
  const savedAddress = user?.address || [user?.houseNo, user?.street, user?.city, user?.pincode, user?.state].filter(Boolean).join(", ");
  const [cart, setCart] = useState({ items: [], totalItems: 0, totalAmount: 0 });
  const [savedAddresses, setSavedAddresses] = useState([]);
  const [selectedAddressKey, setSelectedAddressKey] = useState(savedAddress ? "default" : "new");
  const [form, setForm] = useState(initialForm);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");
  const [fieldErrors, setFieldErrors] = useState({});

  const addressOptions = useMemo(() => {
    const options = (savedAddresses.length ? savedAddresses : user?.addresses || [])
      .filter((address) => address?.fullAddress)
      .map((address) => ({
        key: address.addressId ? `saved-${address.addressId}` : `address-${address.fullAddress}`,
        label: address.defaultAddress ? "Default address" : address.source === "order" ? "Used before" : "Saved address",
        fullAddress: address.fullAddress,
        houseNo: address.houseNo,
        street: address.street,
        city: address.city,
        pincode: address.pincode,
        state: address.state,
      }));

    if (savedAddress && !options.some((address) => address.fullAddress === savedAddress)) {
      options.unshift({
        key: "default",
        label: "Default address",
        fullAddress: savedAddress,
        houseNo: user?.houseNo,
        street: user?.street,
        city: user?.city,
        pincode: user?.pincode,
        state: user?.state,
      });
    }

    return options;
  }, [savedAddress, savedAddresses, user]);

  const selectedAddress = addressOptions.find((address) => address.key === selectedAddressKey);
  const useManualAddress = selectedAddressKey === "new" || !selectedAddress;
  const isPincodeValid = !useManualAddress || /^[0-9]{6}$/.test(form.pincode.trim());
  const isContactNumberValid = !form.contactNumber.trim() || /^[0-9]{1,11}$/.test(form.contactNumber.trim());

  const validateForm = (nextForm = form) => {
    const errors = {};
    const pincode = nextForm.pincode.trim();
    const contactNumber = nextForm.contactNumber.trim();

    if (useManualAddress && pincode && !/^[0-9]{6}$/.test(pincode)) {
      errors.pincode = "Pincode must be exactly 6 digits";
    }

    if (contactNumber && !/^[0-9]{1,11}$/.test(contactNumber)) {
      errors.contactNumber = "Contact number must not be more than 11 digits";
    }

    return errors;
  };

  const buildShippingAddress = () => {
    if (!useManualAddress && selectedAddress?.fullAddress) {
      return selectedAddress.fullAddress.trim();
    }

    return [
      form.houseNo.trim(),
      form.street.trim(),
      form.city.trim(),
      form.pincode.trim(),
      form.state.trim(),
    ]
      .filter(Boolean)
      .join(", ");
  };

  useEffect(() => {
    let active = true;

    const loadCart = async () => {
      try {
        const [cartRes, addressesRes] = await Promise.all([getCart(), getAddresses().catch(() => ({ data: [] }))]);
        if (active) {
          setCart(cartRes.data);
          setSavedAddresses(addressesRes.data || []);
          const firstAddress = (addressesRes.data || [])[0];
          setSelectedAddressKey(
            firstAddress?.addressId
              ? `saved-${firstAddress.addressId}`
              : firstAddress?.fullAddress
                ? `address-${firstAddress.fullAddress}`
                : savedAddress
                  ? "default"
                  : "new"
          );
        }
      } catch {
        if (active) {
          setError("Checkout could not be loaded");
        }
      } finally {
        if (active) {
          setLoading(false);
        }
      }
    };

    loadCart();

    return () => {
      active = false;
    };
  }, [savedAddress]);

  const canSubmit = useMemo(() => {
    return (
      cart.items.length > 0 &&
      ((!useManualAddress && selectedAddress?.fullAddress) ||
        (useManualAddress && form.houseNo.trim() && form.city.trim() && form.pincode.trim() && form.state.trim())) &&
      !submitting
    );
  }, [
    cart.items.length,
    form.city,
    form.houseNo,
    form.pincode,
    form.state,
    selectedAddress,
    submitting,
    useManualAddress,
  ]);

  const handleChange = (event) => {
    const { name, value } = event.target;
    const nextForm = { ...form, [name]: value };
    setForm(nextForm);
    if (name === "pincode" || name === "contactNumber") {
      setFieldErrors(validateForm(nextForm));
      setError("");
    }
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError("");

    const shippingAddress = buildShippingAddress();

    if (!shippingAddress) {
      setError("Shipping address is required");
      return;
    }

    const validationErrors = validateForm();
    setFieldErrors(validationErrors);
    if (Object.keys(validationErrors).length > 0 || !isPincodeValid || !isContactNumberValid) {
      setError("Please correct the highlighted checkout fields before placing your order");
      return;
    }

    setSubmitting(true);

    try {
      await placeOrder({
        shippingAddress,
        paymentMethod: form.paymentMethod,
        contactNumber: form.contactNumber.trim(),
      });

      navigate("/my-orders", {
        state: {
          toast: { type: "success", message: "Order placed successfully" },
        },
      });
    } catch (err) {
      setError(err.response?.data?.error || "Order could not be placed");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className={styles.page}>
      <div className={styles.topBar}>
        <h1>Checkout</h1>
        <div className={styles.topActions}>
          <button type="button" onClick={() => navigate("/cart")}>
            Cart
          </button>
          <button type="button" onClick={() => navigate("/products")}>
            Products
          </button>
        </div>
      </div>

      {loading && <p className={styles.message}>Loading checkout...</p>}
      {!loading && error && (
        <p className={styles.errorCompact} role="alert">
          {error}
        </p>
      )}

      {!loading && cart.items.length === 0 && (
        <div className={styles.emptyState}>
          <p>Your cart is empty</p>
          <button type="button" className={styles.searchBtn} onClick={() => navigate("/products")}>
            Browse Products
          </button>
        </div>
      )}

      {!loading && cart.items.length > 0 && (
        <div className={styles.checkoutLayout}>
          <form className={styles.checkoutForm} onSubmit={handleSubmit} noValidate>
            <section className={styles.deliveryAddressPanel}>
              <div className={styles.checkoutSectionHeader}>
                <span>1</span>
                <div>
                  <h2>Select a delivery address</h2>
                  <p>Choose where this order should be delivered.</p>
                </div>
              </div>

              {addressOptions.length > 0 && (
                <div className={styles.addressChoiceList}>
                  {addressOptions.map((address) => (
                    <label
                      className={`${styles.addressChoice} ${selectedAddressKey === address.key ? styles.addressChoiceActive : ""}`}
                      key={address.key}
                    >
                      <input
                        type="radio"
                        name="selectedAddress"
                        value={address.key}
                        checked={selectedAddressKey === address.key}
                        onChange={(event) => setSelectedAddressKey(event.target.value)}
                      />
                      <span>
                        <strong>{address.label}</strong>
                        <b>{user?.name || "Delivery address"}</b>
                        <p>{address.fullAddress}</p>
                      </span>
                    </label>
                  ))}
                </div>
              )}

              <button
                type="button"
                className={styles.addAddressOption}
                onClick={() => setSelectedAddressKey("new")}
              >
                + Add a new delivery address
              </button>
            </section>

            {useManualAddress && (
              <div className={styles.addressGrid}>
                <label>
                  <span>House / Flat No.</span>
                  <input
                    type="text"
                    name="houseNo"
                    value={form.houseNo}
                    onChange={handleChange}
                    maxLength="120"
                    required={useManualAddress}
                  />
                </label>

                <label>
                  <span>Street / Area</span>
                  <input
                    type="text"
                    name="street"
                    value={form.street}
                    onChange={handleChange}
                    maxLength="160"
                  />
                </label>

                <label>
                  <span>City</span>
                  <input
                    type="text"
                    name="city"
                    value={form.city}
                    onChange={handleChange}
                    maxLength="80"
                    required={useManualAddress}
                  />
                </label>

                <label>
                  <span>Pincode</span>
                  <input
                    type="text"
                    name="pincode"
                    value={form.pincode}
                    onChange={handleChange}
                    inputMode="numeric"
                    pattern="[0-9]{6}"
                    title="Pincode must be exactly 6 digits"
                    aria-invalid={Boolean(fieldErrors.pincode)}
                    aria-describedby={fieldErrors.pincode ? "pincode-error" : undefined}
                    required={useManualAddress}
                  />
                  {fieldErrors.pincode && (
                    <small id="pincode-error" className={styles.fieldError}>
                      {fieldErrors.pincode}
                    </small>
                  )}
                </label>

                <label>
                  <span>State</span>
                  <input
                    type="text"
                    name="state"
                    value={form.state}
                    onChange={handleChange}
                    maxLength="80"
                    required={useManualAddress}
                  />
                </label>
              </div>
            )}

            <label>
              <span>Contact Number</span>
              <input
                type="tel"
                name="contactNumber"
                value={form.contactNumber}
                onChange={handleChange}
                inputMode="numeric"
                pattern="[0-9]{1,11}"
                title="Contact number must not be more than 11 digits"
                aria-invalid={Boolean(fieldErrors.contactNumber)}
                aria-describedby={fieldErrors.contactNumber ? "contact-number-error" : undefined}
              />
              {fieldErrors.contactNumber && (
                <small id="contact-number-error" className={styles.fieldError}>
                  {fieldErrors.contactNumber}
                </small>
              )}
            </label>

            <fieldset className={styles.paymentOptions}>
              <legend>Payment Method</legend>
              <label>
                <input
                  type="radio"
                  name="paymentMethod"
                  value="COD"
                  checked={form.paymentMethod === "COD"}
                  onChange={handleChange}
                />
                <span>Cash on Delivery</span>
              </label>
              <label>
                <input
                  type="radio"
                  name="paymentMethod"
                  value="UPI"
                  checked={form.paymentMethod === "UPI"}
                  onChange={handleChange}
                />
                <span>UPI</span>
              </label>
              <label>
                <input
                  type="radio"
                  name="paymentMethod"
                  value="CARD"
                  checked={form.paymentMethod === "CARD"}
                  onChange={handleChange}
                />
                <span>Card</span>
              </label>
            </fieldset>

            <button type="submit" className={styles.searchBtn} disabled={!canSubmit}>
              {submitting ? "Placing Order..." : "Place Order"}
            </button>
          </form>

          <aside className={styles.checkoutSummary}>
            <h2>Order Summary</h2>
            <div className={styles.checkoutItems}>
              {cart.items.map((item) => (
                <div className={styles.checkoutItem} key={item.cartItemId}>
                  <img
                    src={item.imageUrl || productImageFallback(item.name)}
                    alt={item.name}
                    onError={(event) => handleProductImageError(event, item.name)}
                  />
                  <div>
                    <strong>{item.name}</strong>
                    <span>
                      {item.quantity} x Rs. {item.price}
                    </span>
                  </div>
                  <b>Rs. {item.subtotal}</b>
                </div>
              ))}
            </div>
            <p>
              <span>Items</span>
              <strong>{cart.totalItems}</strong>
            </p>
            <p>
              <span>Total</span>
              <strong>Rs. {cart.totalAmount}</strong>
            </p>
          </aside>
        </div>
      )}
    </div>
  );
}
