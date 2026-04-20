import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { registerUser } from "../services/authService";
import { useAuth } from "../context/AuthContext";

function Register() {
  const [form, setForm] = useState({ name: "", email: "", password: "", role: "CUSTOMER" });
  const [error, setError] = useState("");
  const { login } = useAuth();
  const navigate = useNavigate();

  const onSubmit = async (e) => {
    e.preventDefault();
    try {
      const data = await registerUser(form);
      login(data);
      navigate("/");
    } catch (err) {
      setError("Registration failed");
    }
  };

  return (
    <form className="form" onSubmit={onSubmit}>
      <h2>Register</h2>
      <input placeholder="Name" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} />
      <input placeholder="Email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} />
      <input
        type="password"
        placeholder="Password"
        value={form.password}
        onChange={(e) => setForm({ ...form, password: e.target.value })}
      />
      <select value={form.role} onChange={(e) => setForm({ ...form, role: e.target.value })}>
        <option value="CUSTOMER">Customer</option>
        <option value="VENDOR">Vendor</option>
      </select>
      {error && <p>{error}</p>}
      <button type="submit">Register</button>
    </form>
  );
}

export default Register;
