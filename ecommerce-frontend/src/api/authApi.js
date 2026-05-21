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
export const requestPasswordResetLink = (data) => axiosInstance.post("/auth/password/reset-link", data, { skipAuth: true });
export const requestForgotPasswordLink = (data) => axiosInstance.post("/auth/forgot-password/request-link", data, { skipAuth: true });
export const resetForgotPassword = (data) => axiosInstance.post("/auth/forgot-password/reset", data, { skipAuth: true });
export const logoutUser = () => axiosInstance.post("/auth/logout");
