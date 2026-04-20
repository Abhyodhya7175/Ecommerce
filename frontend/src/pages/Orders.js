import { useEffect, useState } from "react";
import { fetchOrders } from "../services/orderService";
import { formatINR } from "../utils/formatCurrency";

function Orders() {
  const [orders, setOrders] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    const loadOrders = async () => {
      try {
        setIsLoading(true);
        setError("");
        const data = await fetchOrders({ page: 0, size: 20, sortBy: "createdAt" });
        setOrders(data.content || []);
      } catch (err) {
        setError("Unable to load orders.");
      } finally {
        setIsLoading(false);
      }
    };

    loadOrders();
  }, []);

  return (
    <div>
      <h2>My Orders</h2>
      {isLoading && <p className="muted">Loading orders...</p>}
      {error && <p className="error-msg">{error}</p>}
      {!isLoading && !error && orders.length === 0 && <p className="muted">No orders yet.</p>}
      {orders.map((order) => (
        <div className="card" key={order.id}>
          <h3>Order #{order.id}</h3>
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

export default Orders;
