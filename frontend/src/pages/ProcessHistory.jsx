import React, { useState } from 'react';
import { getProcessHistory } from '../services/processService';
import ProcessTimeline from '../components/ProcessTimeline';
import './ProcessHistory.css';

const ProcessHistory = () => {
  const [processId, setProcessId] = useState('');
  const [history, setHistory] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!processId.trim()) {
      setError('Lütfen bir Process Instance ID girin');
      return;
    }

    try {
      setLoading(true);
      setError(null);
      const data = await getProcessHistory(processId.trim());
      setHistory(data);
    } catch (err) {
      setError('Süreç geçmişi yüklenirken hata oluştu: ' + err.message);
      setHistory(null);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="process-history">
      <h1>📜 Süreç Geçmişi</h1>

      <div className="search-container">
        <form onSubmit={handleSubmit}>
          <div className="search-box">
            <input
              type="text"
              placeholder="Process Instance ID girin..."
              value={processId}
              onChange={(e) => setProcessId(e.target.value)}
              className="search-input"
            />
            <button type="submit" className="btn-search" disabled={loading}>
              {loading ? '🔍 Aranıyor...' : '🔍 Ara'}
            </button>
          </div>
        </form>

        <div className="info-hint">
          <p>💡 <strong>İpucu:</strong> Process Instance ID'yi Dashboard'dan veya sipariş oluşturduktan sonra alabilirsiniz.</p>
        </div>
      </div>

      {error && (
        <div className="error-box">
          <p>{error}</p>
        </div>
      )}

      {history && (
        <div className="history-container">
          <h2>⏱ Görev Geçmişi</h2>
          {history.length === 0 ? (
            <div className="no-history-message">
              <p className="warning-icon">⚠️</p>
              <h3>Bu süreçte tamamlanan görev bulunamadı</h3>
              <p>Bu süreç muhtemelen <strong>erken aşamada sonlandı</strong> (ödeme reddi veya restoran reddi).</p>
              <p>Detaylı bilgi için Dashboard'dan ilgili süreci görüntüleyin.</p>
            </div>
          ) : (
            <ProcessTimeline history={history} />
          )}
        </div>
      )}

      {!history && !error && !loading && (
        <div className="placeholder">
          <p>🔍 Bir süreç geçmişini görüntülemek için yukarıdaki arama kutusuna Process Instance ID girin.</p>
        </div>
      )}
    </div>
  );
};

export default ProcessHistory;
