import api from "./api";

export const fetchCart = async () => {
  const { data } = await api.get("/cart");
  return data;
};

export const addToCart = async (payload) => {
  const { data } = await api.post("/cart/add", payload);
  return data;
};

export const removeCartItem = async (id) => {
  await api.delete(`/cart/remove/${id}`);
};
