import api from "./api";

export const fetchProducts = async (params) => {
  const { data } = await api.get("/products", { params });
  return data;
};

export const fetchProductById = async (id) => {
  const { data } = await api.get(`/products/${id}`);
  return data;
};

export const fetchVendorProducts = async (params) => {
  const { data } = await api.get("/vendor/products", { params });
  return data;
};

export const createVendorProduct = async (payload) => {
  const { data } = await api.post("/vendor/products", payload);
  return data;
};

export const fetchAdminProducts = async (params) => {
  const { data } = await api.get("/admin/products", { params });
  return data;
};

export const approveAdminProduct = async (id) => {
  const { data } = await api.put(`/admin/products/${id}/approve`);
  return data;
};
