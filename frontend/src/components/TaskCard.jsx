import React from 'react';
import { getRoleName } from '../constants/Roles';
import './TaskCard.css';

const TaskCard = ({ task, onComplete, requiresApproval = false }) => {
  const [showModal, setShowModal] = React.useState(false);
  const [approved, setApproved] = React.useState(true);

  const handleComplete = () => {
    if (requiresApproval) {
      setShowModal(true);
    } else {
      onComplete(task.id, {});
    }
  };

  const handleModalSubmit = () => {
    onComplete(task.id, { approved });
    setShowModal(false);
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleString('tr-TR');
  };

  return (
    <>
      <div className="task-card">
        <div className="task-header">
          <h3>{task.name}</h3>
          <span className="task-assignee">{getRoleName(task.assignee)}</span>
        </div>
        <div className="task-body">
          <p><strong>Task ID:</strong> {task.id}</p>
          <p><strong>Oluşturulma:</strong> {formatDate(task.createTime)}</p>
          {task.processInstanceId && (
            <p><strong>Process ID:</strong> {task.processInstanceId.substring(0, 8)}...</p>
          )}
        </div>
        <div className="task-footer">
          <button className="btn-complete" onClick={handleComplete}>
            ✓ Tamamla
          </button>
        </div>
      </div>

      {showModal && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h3>Siparişi Onayla</h3>
            <div className="modal-body">
              <label>
                <input
                  type="radio"
                  checked={approved}
                  onChange={() => setApproved(true)}
                />
                Onayla
              </label>
              <label>
                <input
                  type="radio"
                  checked={!approved}
                  onChange={() => setApproved(false)}
                />
                Reddet
              </label>
            </div>
            <div className="modal-footer">
              <button className="btn-cancel" onClick={() => setShowModal(false)}>
                İptal
              </button>
              <button className="btn-submit" onClick={handleModalSubmit}>
                Gönder
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  );
};

export default TaskCard;
