import axiosInstance from "./axiosInstance";

export const registerUser = (data) => axiosInstance.post("/auth/register", data);
export const loginUser = (data) => axiosInstance.post("/auth/login", data);
export const getCurrentUser = () => axiosInstance.get("/auth/me");
export const updateProfile = (data) => axiosInstance.put("/auth/profile", data);
export const updateAddress = (data) => axiosInstance.put("/auth/address", data);
export const getAddresses = () => axiosInstance.get("/auth/addresses");
export const addAddress = (data) => axiosInstance.post("/auth/addresses", data);
export const updateSavedAddress = (addressId, data) => axiosInstance.put(`/auth/addresses/${addressId}`, data);
export const updatePassword = (data) => axiosInstance.post("/auth/change-password", data, { skipAuth: true });
export const requestPasswordOtp = (data) => axiosInstance.post("/auth/password/otp", data, { skipAuth: true });
export const requestForgotPasswordOtp = (data) => axiosInstance.post("/auth/forgot-password/request-otp", data, { skipAuth: true });
export const resetForgotPassword = (data) => axiosInstance.post("/auth/forgot-password/reset", data, { skipAuth: true });
export const logoutUser = () => axiosInstance.post("/auth/logout");
