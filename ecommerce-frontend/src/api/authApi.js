import axiosInstance from "./axiosInstance";

export const registerUser = (data) => axiosInstance.post("/auth/register", data);
export const loginUser = (data) => axiosInstance.post("/auth/login", data);
export const getCurrentUser = () => axiosInstance.get("/auth/me");
export const updateProfile = (data) => axiosInstance.put("/auth/profile", data);
export const updatePassword = (data) => axiosInstance.post("/auth/change-password", data, { skipAuth: true });
export const logoutUser = () => axiosInstance.post("/auth/logout");
