import React from 'react';
import { Link } from 'react-router-dom';
import './Navbar.css';

const Navbar = () => {
  return (
    <nav className="navbar">
      <div className="navbar-brand">
        <h1>🍕 Foodly</h1>
        <span className="navbar-subtitle">BPMN Order Management</span>
      </div>
      <div className="navbar-links">
        <Link to="/" className="nav-link">Dashboard</Link>
        <Link to="/create-order" className="nav-link">Yeni Sipariş</Link>
        <Link to="/tasks" className="nav-link">Görevler</Link>
        <Link to="/history" className="nav-link">Geçmiş</Link>
      </div>
    </nav>
  );
};

export default Navbar;
