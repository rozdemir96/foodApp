import React, { useState, useEffect } from 'react';
import { getActiveProcesses, getCompletedProcesses, getProcessHistory } from '../services/processService';
import ProcessTimeline from '../components/ProcessTimeline';
import { useToast } from '../components/ToastProvider';
import './Dashboard.css';

const Dashboard = () => {
  const { showToast } = useToast();
  const [activeProcesses, setActiveProcesses] = useState([]);
  const [completedProcesses, setCompletedProcesses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedProcess, setSelectedProcess] = useState(null);
  const [processHistory, setProcessHistory] = useState(null);
  const [loadingHistory, setLoadingHistory] = useState(false);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      setLoading(true);
      const [active, completed] = await Promise.all([
        getActiveProcesses(),
        getCompletedProcesses()
      ]);
      setActiveProcesses(active);
      setCompletedProcesses(completed);
      setError(null);
    } catch (err) {
      setError('Veriler yüklenirken hata oluştu: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  const copyToClipboard = (text) => {
    navigator.clipboard.writeText(text);
    showToast('Process ID kopyalandı!', 'success', 1500);
  };

  const handleProcessClick = async (process) => {
    setSelectedProcess(process);
    setLoadingHistory(true);
    try {
      const history = await getProcessHistory(process.id);
      setProcessHistory(history);
    } catch (err) {
      setProcessHistory([]);
      showToast('Süreç geçmişi yüklenemedi', 'error', 2000);
    } finally {
      setLoadingHistory(false);
    }
  };

  const closeModal = () => {
    setSelectedProcess(null);
    setProcessHistory(null);
  };

  if (loading) {
    return <div className="loading">Yükleniyor...</div>;
  }

  if (error) {
    return (
      <div className="error-container">
        <p className="error">{error}</p>
        <button onClick={fetchData} className="btn-retry">Tekrar Dene</button>
      </div>
    );
  }

  return (
    <div className="dashboard">
      <h1>📊 Dashboard</h1>
      
      <div className="stats-grid">
        <div className="stat-card active">
          <h3>Aktif Süreçler</h3>
          <p className="stat-number">{activeProcesses.length}</p>
        </div>
        <div className="stat-card completed">
          <h3>Tamamlanan Süreçler</h3>
          <p className="stat-number">{completedProcesses.length}</p>
        </div>
        <div className="stat-card total">
          <h3>Toplam Süreçler</h3>
          <p className="stat-number">{activeProcesses.length + completedProcesses.length}</p>
        </div>
      </div>

      <div className="process-sections">
        <div className="process-section">
          <h2>🔄 Aktif Süreçler</h2>
          {activeProcesses.length === 0 ? (
            <p className="no-data">Aktif süreç bulunmamaktadır.</p>
          ) : (
            <div className="process-list">
              {activeProcesses.map((process) => (
                <div key={process.id} className="process-card">
                  <div className="process-id-header">
                    <h3 
                      className="process-id-clickable" 
                      onClick={() => handleProcessClick(process)}
                      title="Detayları görmek için tıklayın"
                    >
                      Process Instance: {process.id?.substring(0, 12)}...
                    </h3>
                    <button 
                      className="btn-copy"
                      onClick={(e) => {
                        e.stopPropagation();
                        copyToClipboard(process.id);
                      }}
                      title="Process ID'yi kopyala"
                    >
                      📋
                    </button>
                  </div>
                  <p><strong>Process Definition:</strong> {process.processDefinitionName || process.processDefinitionId}</p>
                  <p><strong>Başlangıç:</strong> {new Date(process.startTime).toLocaleString('tr-TR')}</p>
                  {process.businessKey && <p><strong>Business Key:</strong> {process.businessKey}</p>}
                </div>
              ))}
            </div>
          )}
        </div>

        <div className="process-section">
          <h2>✅ Tamamlanan Süreçler</h2>
          {completedProcesses.length === 0 ? (
            <p className="no-data">Tamamlanmış süreç bulunmamaktadır.</p>
          ) : (
            <div className="process-list">
              {completedProcesses.map((process) => (
                <div key={process.id} className="process-card completed">
                  <div className="process-id-header">
                    <h3 
                      className="process-id-clickable" 
                      onClick={() => handleProcessClick(process)}
                      title="Detayları görmek için tıklayın"
                    >
                      Process Instance: {process.id?.substring(0, 12)}...
                    </h3>
                    <button 
                      className="btn-copy"
                      onClick={(e) => {
                        e.stopPropagation();
                        copyToClipboard(process.id);
                      }}
                      title="Process ID'yi kopyala"
                    >
                      📋
                    </button>
                  </div>
                  <p><strong>Process Definition:</strong> {process.processDefinitionName || process.processDefinitionId}</p>
                  <p><strong>Başlangıç:</strong> {new Date(process.startTime).toLocaleString('tr-TR')}</p>
                  {process.endTime && (
                    <p><strong>Bitiş:</strong> {new Date(process.endTime).toLocaleString('tr-TR')}</p>
                  )}
                  {process.deleteReason && process.deleteReason.includes('Reddedildi') && (
                    <div className="process-status-badge rejected">
                      ❌ {process.deleteReason}
                    </div>
                  )}
                  {process.deleteReason && process.deleteReason.includes('Tamamlandı') && (
                    <div className="process-status-badge success">
                      ✅ {process.deleteReason}
                    </div>
                  )}
                  {!process.deleteReason && process.endTime && (
                    <div className="process-status-badge success">
                      ✅ Başarıyla Tamamlandı
                    </div>
                  )}
                </div>
              ))}
            </div>
          )}
        </div>
      </div>

      <button onClick={fetchData} className="btn-refresh">🔄 Yenile</button>

      {/* Modal for Process Details */}
      {selectedProcess && (
        <div className="modal-overlay" onClick={closeModal}>
          <div className="modal-large" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>📋 Süreç Detayları</h2>
              <button className="btn-close" onClick={closeModal}>✕</button>
            </div>
            
            <div className="modal-body">
              <div className="process-info">
                <div className="info-row">
                  <strong>Process Instance ID:</strong>
                  <div className="id-with-copy">
                    <code>{selectedProcess.id}</code>
                    <button 
                      className="btn-copy-inline"
                      onClick={() => copyToClipboard(selectedProcess.id)}
                    >
                      📋 Kopyala
                    </button>
                  </div>
                </div>
                <div className="info-row">
                  <strong>Process Definition:</strong>
                  <span>{selectedProcess.processDefinitionName || selectedProcess.processDefinitionId}</span>
                </div>
                <div className="info-row">
                  <strong>Başlangıç:</strong>
                  <span>{new Date(selectedProcess.startTime).toLocaleString('tr-TR')}</span>
                </div>
                {selectedProcess.endTime && (
                  <div className="info-row">
                    <strong>Bitiş:</strong>
                    <span>{new Date(selectedProcess.endTime).toLocaleString('tr-TR')}</span>
                  </div>
                )}
                {selectedProcess.businessKey && (
                  <div className="info-row">
                    <strong>Business Key:</strong>
                    <span>{selectedProcess.businessKey}</span>
                  </div>
                )}
              </div>

              <h3 className="history-title">⏱ Görev Geçmişi</h3>
              {loadingHistory ? (
                <div className="loading-small">Yükleniyor...</div>
              ) : (
                <ProcessTimeline history={processHistory} />
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Dashboard;
