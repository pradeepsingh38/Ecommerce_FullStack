import axiosInstance from "./axiosInstance";

export const placeOrder = () => axiosInstance.post("/orders");

export const getMyOrders = () => axiosInstance.get("/orders/my");
