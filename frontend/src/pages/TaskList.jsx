import React, { useState, useEffect } from 'react';
import { getTasksByRole, completeTask } from '../services/orderService';
import TaskCard from '../components/TaskCard';
import { useToast } from '../components/ToastProvider';
import { Roles } from '../constants/Roles';
import './TaskList.css';

const TaskList = () => {
  const { showToast } = useToast();
  const [selectedRole, setSelectedRole] = useState('restaurant');
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchTasks();
  }, [selectedRole]);

  const fetchTasks = async () => {
    try {
      setLoading(true);
      const data = await getTasksByRole(selectedRole);
      setTasks(data);
      setError(null);
    } catch (err) {
      setError('Görevler yüklenirken hata oluştu: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleCompleteTask = async (taskId, variables) => {
    try {
      await completeTask(taskId, variables);
      // Görevi listeden kaldır
      setTasks(tasks.filter(task => task.id !== taskId));
      showToast('Görev başarıyla tamamlandı!', 'success', 1500);
    } catch (err) {
      showToast('Görev tamamlanırken hata oluştu', 'error', 2000);
    }
  };

  return (
    <div className="task-list">
      <h1>📋 Görevler</h1>

      <div className="role-selector">
        {Roles.map((role) => (
          <button
            key={role.value}
            className={`role-btn ${selectedRole === role.value ? 'active' : ''}`}
            style={{
              borderColor: selectedRole === role.value ? role.color : '#ddd',
              background: selectedRole === role.value ? role.color : 'white',
              color: selectedRole === role.value ? 'white' : '#333'
            }}
            onClick={() => setSelectedRole(role.value)}
          >
            {role.label}
          </button>
        ))}
      </div>

      {loading && <div className="loading">Yükleniyor...</div>}

      {error && (
        <div className="error-container">
          <p className="error">{error}</p>
          <button onClick={fetchTasks} className="btn-retry">Tekrar Dene</button>
        </div>
      )}

      {!loading && !error && (
        <div className="tasks-container">
          <div className="tasks-header">
            <h2>{Roles.find(r => r.value === selectedRole)?.label} Görevleri</h2>
            <button onClick={fetchTasks} className="btn-refresh-small">🔄</button>
          </div>

          {tasks.length === 0 ? (
            <div className="no-tasks">
              <p>✅ Tüm görevler tamamlandı!</p>
              <p className="subtitle">Şu anda bekleyen görev bulunmamaktadır.</p>
            </div>
          ) : (
            <div className="tasks-grid">
              {tasks.map((task) => (
                <TaskCard
                  key={task.id}
                  task={task}
                  onComplete={handleCompleteTask}
                  requiresApproval={selectedRole === 'restaurant'}
                />
              ))}
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default TaskList;
