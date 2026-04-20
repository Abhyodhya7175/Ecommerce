import { useCallback, useEffect, useState } from "react";
import { Link } from "react-router-dom";
import Pagination from "../components/Pagination";
import { fetchProducts } from "../services/productService";
import { formatINR } from "../utils/formatCurrency";

function Home() {
  const [products, setProducts] = useState([]);
  const [page, setPage] = useState(0);
  const [searchInput, setSearchInput] = useState("");
  const [searchTerm, setSearchTerm] = useState("");
  const [sortBy, setSortBy] = useState("id");
  const [totalPages, setTotalPages] = useState(0);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState("");

  const loadProducts = useCallback(async () => {
    try {
      setIsLoading(true);
      const data = await fetchProducts({ page, size: 10, sortBy, search: searchTerm });
      setProducts(data.content || []);
      setTotalPages(data.totalPages || 0);
      setError("");
    } catch (err) {
      setProducts([]);
      setTotalPages(0);
      setError("Unable to load products. Start backend server on http://localhost:8080.");
    } finally {
      setIsLoading(false);
    }
  }, [page, searchTerm, sortBy]);

  useEffect(() => {
    loadProducts();
  }, [loadProducts]);

  useEffect(() => {
    const timer = setTimeout(() => {
      setSearchTerm(searchInput.trim());
      setPage(0);
    }, 350);

    return () => clearTimeout(timer);
  }, [searchInput]);

  useEffect(() => {
    if (!error) {
      return;
    }

    const retryTimer = setTimeout(() => {
      loadProducts();
    }, 3000);

    return () => clearTimeout(retryTimer);
  }, [error, loadProducts]);

  return (
    <div>
      <h2>Products</h2>
      <div className="toolbar">
        <input
          value={searchInput}
          onChange={(e) => setSearchInput(e.target.value)}
          placeholder="Search products..."
        />
        <select
          value={sortBy}
          onChange={(e) => {
            setSortBy(e.target.value);
            setPage(0);
          }}
        >
          <option value="id">Newest</option>
          <option value="name">Name</option>
          <option value="price">Price</option>
          <option value="stock">Stock</option>
        </select>
      </div>
      {error && (
        <div className="error-box">
          <p>{error}</p>
          <button onClick={loadProducts}>Retry</button>
        </div>
      )}
      {isLoading && <p className="muted">Loading products...</p>}
      {!isLoading && !error && products.length === 0 && <p className="muted">No products found. Try another search.</p>}
      <div className="grid">
        {products.map((product, index) => (
          <div className="card card-animated" style={{ animationDelay: `${index * 45}ms` }} key={product.id}>
            <h3>{product.name}</h3>
            <p>{product.description}</p>
            <strong>{formatINR(product.price)}</strong>
            <Link className="btn-link" to={`/products/${product.id}`}>
              View Details
            </Link>
          </div>
        ))}
      </div>
      <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />
    </div>
  );
}

export default Home;
