import api from "./api";

export const placeOrder = async () => {
  const { data } = await api.post("/orders");
  return data;
};

export const fetchOrders = async (params) => {
  const { data } = await api.get("/orders", { params });
  return data;
};

export const fetchAdminOrders = async (params) => {
  const { data } = await api.get("/admin/orders", { params });
  return data;
};
