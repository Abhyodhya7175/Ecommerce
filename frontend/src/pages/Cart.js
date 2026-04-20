import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { fetchCart, removeCartItem } from "../services/cartService";
import { placeOrder } from "../services/orderService";
import { formatINR } from "../utils/formatCurrency";

function Cart() {
  const [cart, setCart] = useState({ items: [] });
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");

  const loadCart = async () => {
    try {
      setIsLoading(true);
      setError("");
      const data = await fetchCart();
      setCart(data);
    } catch (err) {
      setError("Unable to load cart.");
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadCart();
  }, []);

  const onRemove = async (id) => {
    try {
      setMessage("");
      await removeCartItem(id);
      setMessage("Item removed.");
      loadCart();
    } catch (err) {
      setError("Unable to remove item.");
    }
  };

  const onPlaceOrder = async () => {
    try {
      setMessage("");
      await placeOrder();
      setMessage("Order placed successfully.");
      loadCart();
    } catch (err) {
      setError("Unable to place order.");
    }
  };

  const subtotal = (cart.items || []).reduce(
    (sum, item) => sum + Number(item.price || 0) * Number(item.quantity || 0),
    0
  );

  return (
    <div>
      <h2>Cart</h2>
      <Link className="btn-link" to="/">
        Back to Home
      </Link>
      {isLoading && <p className="muted">Loading cart...</p>}
      {error && <p className="error-msg">{error}</p>}
      {message && <p className="success-msg">{message}</p>}
      {!isLoading && cart.items?.length === 0 && <p className="muted">Your cart is empty.</p>}
      {cart.items?.map((item) => (
        <div className="card" key={item.itemId}>
          <p>
            {item.productName} x {item.quantity}
          </p>
          <p className="muted">{formatINR(item.price)} each</p>
          <button onClick={() => onRemove(item.itemId)}>Remove</button>
        </div>
      ))}
      {cart.items?.length > 0 && (
        <div className="row-between">
          <strong>Subtotal: {formatINR(subtotal)}</strong>
          <button onClick={onPlaceOrder}>Place Order</button>
        </div>
      )}
    </div>
  );
}

export default Cart;
