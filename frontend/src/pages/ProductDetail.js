import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { addToCart } from "../services/cartService";
import { fetchProductById } from "../services/productService";
import { formatINR } from "../utils/formatCurrency";

function ProductDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [product, setProduct] = useState(null);
  const [quantity, setQuantity] = useState(1);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  useEffect(() => {
    const load = async () => {
      try {
        setError("");
        const data = await fetchProductById(id);
        setProduct(data);
      } catch (err) {
        setError("Unable to load product details.");
      }
    };
    load();
  }, [id]);

  if (!product) {
    return <p>{error || "Loading..."}</p>;
  }

  const addItem = async () => {
    try {
      setMessage("");
      await addToCart({ productId: product.id, quantity });
      setMessage("Added to cart.");
    } catch (err) {
      setError("Could not add to cart.");
    }
  };

  return (
    <div className="card">
      <button onClick={() => navigate(-1)}>Back</button>
      <h2>{product.name}</h2>
      <p>{product.description}</p>
      <strong>{formatINR(product.price)}</strong>
      <p className="muted">Stock: {product.stock}</p>
      <div className="row-actions">
        <label htmlFor="qty">Qty</label>
        <input
          id="qty"
          type="number"
          min="1"
          max={product.stock || 1}
          value={quantity}
          onChange={(e) => setQuantity(Math.max(1, Number(e.target.value) || 1))}
        />
        <button onClick={addItem}>Add to cart</button>
      </div>
      {message && <p className="success-msg">{message}</p>}
      {error && <p className="error-msg">{error}</p>}
    </div>
  );
}

export default ProductDetail;
