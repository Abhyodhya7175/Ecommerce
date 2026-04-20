import { useEffect, useState } from "react";
import { createVendorProduct, fetchVendorProducts } from "../services/productService";
import { formatINR } from "../utils/formatCurrency";

const initialForm = {
  name: "",
  description: "",
  price: "",
  stock: "",
  categoryName: "",
};

function VendorDashboard() {
  const [form, setForm] = useState(initialForm);
  const [products, setProducts] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");

  const loadVendorProducts = async () => {
    try {
      setIsLoading(true);
      setError("");
      const data = await fetchVendorProducts({ page: 0, size: 50, sortBy: "id" });
      setProducts(data.content || []);
    } catch (err) {
      setError("Unable to load your products.");
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadVendorProducts();
  }, []);

  const onSubmit = async (e) => {
    e.preventDefault();
    try {
      setError("");
      setMessage("");
      await createVendorProduct({
        name: form.name.trim(),
        description: form.description.trim(),
        price: Number(form.price),
        stock: Number(form.stock),
        categoryName: form.categoryName.trim(),
      });
      setMessage("Product submitted to pool. It is pending admin approval.");
      setForm(initialForm);
      loadVendorProducts();
    } catch (err) {
      setError("Could not submit product. Check all fields and try again.");
    }
  };

  return (
    <div>
      <h2>Vendor Dashboard</h2>
      <p className="muted">Add products to the marketplace pool. New submissions require admin approval.</p>

      <form className="form" onSubmit={onSubmit}>
        <h3>Add New Product</h3>
        <input
          placeholder="Product name"
          value={form.name}
          required
          onChange={(e) => setForm({ ...form, name: e.target.value })}
        />
        <input
          placeholder="Description"
          value={form.description}
          required
          onChange={(e) => setForm({ ...form, description: e.target.value })}
        />
        <input
          type="number"
          min="1"
          step="0.01"
          placeholder="Price (INR)"
          value={form.price}
          required
          onChange={(e) => setForm({ ...form, price: e.target.value })}
        />
        <input
          type="number"
          min="1"
          step="1"
          placeholder="Stock"
          value={form.stock}
          required
          onChange={(e) => setForm({ ...form, stock: e.target.value })}
        />
        <input
          placeholder="Category (e.g. Electronics)"
          value={form.categoryName}
          required
          onChange={(e) => setForm({ ...form, categoryName: e.target.value })}
        />
        <button type="submit">Submit Product</button>
      </form>

      {error && <p className="error-msg">{error}</p>}
      {message && <p className="success-msg">{message}</p>}

      <h3 style={{ marginTop: "1.4rem" }}>Your Product Pool</h3>
      {isLoading && <p className="muted">Loading your products...</p>}
      {!isLoading && products.length === 0 && <p className="muted">No products yet. Add your first product above.</p>}
      <div className="grid">
        {products.map((product) => (
          <div className="card" key={product.id}>
            <h4>{product.name}</h4>
            <p>{product.description}</p>
            <p className="muted">{product.categoryName || "Uncategorized"}</p>
            <p>{formatINR(product.price)}</p>
            <p className="muted">Stock: {product.stock}</p>
            <span className={product.approved ? "status-chip status-approved" : "status-chip status-pending"}>
              {product.approved ? "Approved" : "Pending approval"}
            </span>
          </div>
        ))}
      </div>
    </div>
  );
}

export default VendorDashboard;
