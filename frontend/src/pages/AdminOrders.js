import { useEffect, useState } from "react";
import { fetchAdminOrders } from "../services/orderService";
import { approveAdminProduct, fetchAdminProducts } from "../services/productService";
import { formatINR } from "../utils/formatCurrency";

const getRequestErrorMessage = (err, fallbackMessage) => {
  if (!err?.response) {
    return "Backend is not reachable on http://localhost:8080. Please start the backend server and try again.";
  }

  if (err.response.status === 401 || err.response.status === 403) {
    return "Your admin session is invalid or expired. Please log in again.";
  }

  const apiMessage = err.response.data?.message;
  if (typeof apiMessage === "string" && apiMessage.trim()) {
    return apiMessage;
  }

  return fallbackMessage;
};

function AdminOrders() {
  const [pendingProducts, setPendingProducts] = useState([]);
  const [orders, setOrders] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");

  const loadData = async () => {
    try {
      setIsLoading(true);
      setError("");
      const results = await Promise.allSettled([
        fetchAdminProducts({ approved: false, page: 0, size: 50, sortBy: "id", sortDir: "desc" }),
        fetchAdminOrders({ page: 0, size: 50, sortBy: "createdAt" }),
      ]);

      const [productsResult, ordersResult] = results;
      const errors = [];

      if (productsResult.status === "fulfilled") {
        setPendingProducts(productsResult.value.content || []);
      } else {
        setPendingProducts([]);
        errors.push(
          `Pending products failed to load: ${getRequestErrorMessage(
            productsResult.reason,
            "Unable to load pending products."
          )}`
        );
      }

      if (ordersResult.status === "fulfilled") {
        setOrders(ordersResult.value.content || []);
      } else {
        setOrders([]);
        errors.push(
          `Orders failed to load: ${getRequestErrorMessage(
            ordersResult.reason,
            "Unable to load orders."
          )}`
        );
      }

      if (errors.length > 0) {
        setError(errors.join(" "));
      }
    } catch (err) {
      setError(getRequestErrorMessage(err, "Unable to load admin data."));
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadData();
    const refreshInterval = setInterval(() => {
      loadData();
    }, 15000);

    return () => clearInterval(refreshInterval);
  }, []);

  const onApprove = async (id) => {
    try {
      setMessage("");
      setError("");
      await approveAdminProduct(id);
      setMessage("Product approved successfully.");
      await loadData();
    } catch (err) {
      setError(getRequestErrorMessage(err, "Unable to approve product."));
    }
  };

  return (
    <div>
      <h2>Admin Panel</h2>
      <p className="muted">Approve products added by vendors and review all customer orders.</p>
      <button type="button" onClick={loadData}>Refresh Admin Data</button>

      {isLoading && <p className="muted">Loading admin data...</p>}
      {error && <p className="error-msg">{error}</p>}
      {message && <p className="success-msg">{message}</p>}

      <h3 style={{ marginTop: "1rem" }}>Pending Product Approvals</h3>
      {!isLoading && pendingProducts.length === 0 && <p className="muted">No pending products.</p>}
      <div className="grid">
        {pendingProducts.map((product) => (
          <div className="card" key={product.id}>
            <h4>{product.name}</h4>
            <p>{product.description}</p>
            <p className="muted">Category: {product.categoryName || "Uncategorized"}</p>
            <p>{formatINR(product.price)}</p>
            <p className="muted">Stock: {product.stock}</p>
            <button onClick={() => onApprove(product.id)}>Approve Product</button>
          </div>
        ))}
      </div>

      <h3 style={{ marginTop: "1.2rem" }}>All Orders</h3>
      {!isLoading && orders.length === 0 && <p className="muted">No orders found.</p>}
      {orders.map((order) => (
        <div className="card" key={order.id}>
          <h4>Order #{order.id}</h4>
          <p className="muted">{new Date(order.createdAt).toLocaleString()}</p>
          {order.items?.map((item) => (
            <p key={`${order.id}-${item.productId}`}>
              {item.productName} x {item.quantity} - {formatINR(item.price)}
            </p>
          ))}
        </div>
      ))}
    </div>
  );
}

export default AdminOrders;
