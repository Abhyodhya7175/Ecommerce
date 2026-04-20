import { NavLink } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

function Navbar() {
  const { user, logout } = useAuth();

  const navClassName = ({ isActive }) =>
    isActive ? "nav-pill nav-pill-active" : "nav-pill";

  return (
    <nav className="navbar">
      <h1 className="brand">Marketplace</h1>
      <div className="nav-links">
        <NavLink to="/" className={navClassName}>
          Home
        </NavLink>
        {!user && (
          <NavLink to="/login" className={navClassName}>
            Login
          </NavLink>
        )}
        {!user && (
          <NavLink to="/register" className={navClassName}>
            Register
          </NavLink>
        )}
        {user?.role === "CUSTOMER" && (
          <NavLink to="/cart" className={navClassName}>
            Cart
          </NavLink>
        )}
        {user?.role === "CUSTOMER" && (
          <NavLink to="/orders" className={navClassName}>
            Orders
          </NavLink>
        )}
        {user?.role === "VENDOR" && (
          <NavLink to="/vendor" className={navClassName}>
            Vendor
          </NavLink>
        )}
        {user?.role === "ADMIN" && (
          <NavLink to="/admin/orders" className={navClassName}>
            Admin
          </NavLink>
        )}
        {user && <span className="role-chip">{user.role}</span>}
        {user && (
          <button className="nav-pill" onClick={logout}>
            Logout
          </button>
        )}
      </div>
    </nav>
  );
}

export default Navbar;
