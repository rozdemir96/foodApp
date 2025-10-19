import React from 'react';
import { getRoleName } from '../constants/Roles';
import './ProcessTimeline.css';

const ProcessTimeline = ({ history }) => {
  const formatDuration = (millis) => {
    const seconds = Math.floor(millis / 1000);
    const minutes = Math.floor(seconds / 60);
    const hours = Math.floor(minutes / 60);

    if (hours > 0) {
      return `${hours}s ${minutes % 60}d`;
    } else if (minutes > 0) {
      return `${minutes}d ${seconds % 60}s`;
    } else {
      return `${seconds}s`;
    }
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleString('tr-TR');
  };

  if (!history || history.length === 0) {
    return <p className="no-history">Henüz tamamlanmış görev yok.</p>;
  }

  return (
    <div className="timeline">
      {history.map((task, index) => (
        <div key={index} className="timeline-item">
          <div className="timeline-marker"></div>
          <div className="timeline-content">
            <h4>{task.taskName}</h4>
            <p><strong>Atanan:</strong> {getRoleName(task.assignee)}</p>
            <p><strong>Başlangıç:</strong> {formatDate(task.startTime)}</p>
            <p><strong>Bitiş:</strong> {formatDate(task.endTime)}</p>
            <p className="duration">⏱ Süre: {formatDuration(task.durationInMillis)}</p>
          </div>
        </div>
      ))}
    </div>
  );
};

export default ProcessTimeline;
