import axios from "axios";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8081/api";

const axiosInstance = axios.create({
  baseURL: API_BASE_URL,
});

axiosInstance.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token && !config.skipAuth) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

axiosInstance.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401 || error.response?.status === 403) {
      const hadToken = Boolean(localStorage.getItem("token"));
      localStorage.removeItem("token");
      localStorage.removeItem("user");
      if (hadToken) {
        window.dispatchEvent(new CustomEvent("auth:session-expired"));
      }
    }
    return Promise.reject(error);
  }
);

export default axiosInstance;
