import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { ToastProvider } from './components/ToastProvider';
import Navbar from './components/Navbar';
import Dashboard from './pages/Dashboard';
import CreateOrder from './pages/CreateOrder';
import TaskList from './pages/TaskList';
import ProcessHistory from './pages/ProcessHistory';
import './App.css';

function App() {
  return (
    <ToastProvider>
      <Router>
        <div className="app">
          <Navbar />
          <main className="main-content">
            <Routes>
              <Route path="/" element={<Dashboard />} />
              <Route path="/create-order" element={<CreateOrder />} />
              <Route path="/tasks" element={<TaskList />} />
              <Route path="/history" element={<ProcessHistory />} />
            </Routes>
          </main>
        </div>
      </Router>
    </ToastProvider>
  );
}

export default App;
