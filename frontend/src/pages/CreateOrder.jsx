import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { createOrder } from '../services/orderService';
import { useToast } from '../components/ToastProvider';
import { getOrderStatusDescription } from '../constants/OrderStatus';
import './CreateOrder.css';

const CreateOrder = () => {
  const navigate = useNavigate();
  const { showToast } = useToast();
  const [formData, setFormData] = useState({
    userId: 1,
    restaurantId: 1,
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    setSuccess(null);

    try {
      const order = await createOrder(formData);
      const statusDescription = getOrderStatusDescription(order.status);
      setSuccess(`Sipariş başarıyla oluşturuldu! Durum: ${statusDescription}`);
      showToast(`Sipariş oluşturuldu! Durum: ${statusDescription}`, 'success', 2000);
      
      // 2 saniye sonra dashboard'a yönlendir
      setTimeout(() => {
        navigate('/');
      }, 2000);
    } catch (err) {
      setError('Sipariş oluşturulurken hata oluştu: ' + err.message);
      showToast('Sipariş oluşturulamadı', 'error', 2000);
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: parseInt(e.target.value),
    });
  };

  return (
    <div className="create-order">
      <div className="form-container">
        <h1>🍕 Yeni Sipariş Oluştur</h1>
        <p className="form-description">
          BPMN sürecini başlatmak için aşağıdaki formu doldurun.
          Ödeme otomatik olarak kontrol edilecektir (%70 başarı oranı).
        </p>

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="userId">Kullanıcı ID</label>
            <input
              type="number"
              id="userId"
              name="userId"
              value={formData.userId}
              onChange={handleChange}
              min="1"
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="restaurantId">Restoran ID</label>
            <input
              type="number"
              id="restaurantId"
              name="restaurantId"
              value={formData.restaurantId}
              onChange={handleChange}
              min="1"
              required
            />
          </div>

          {error && <div className="alert alert-error">{error}</div>}
          {success && <div className="alert alert-success">{success}</div>}

          <button type="submit" className="btn-submit" disabled={loading}>
            {loading ? 'Oluşturuluyor...' : 'Sipariş Oluştur'}
          </button>
        </form>

        <div className="info-box">
          <h3>ℹ️ Bilgi</h3>
          <ul>
            <li>Sipariş oluşturulduğunda otomatik olarak ödeme kontrolü yapılır</li>
            <li>Ödeme %70 ihtimalle onaylanır</li>
            <li>Ödeme onaylanırsa Restoran'a görev düşer</li>
            <li>Ödeme reddedilirse süreç REJECTED olarak sonlanır</li>
          </ul>
        </div>
      </div>
    </div>
  );
};

export default CreateOrder;
