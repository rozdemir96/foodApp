import apiClient from './apiClient';

// Yeni sipariş oluştur
export const createOrder = async (orderData) => {
  const response = await apiClient.post('/orders', orderData);
  return response.data;
};

// Tüm siparişleri listele
export const getAllOrders = async () => {
  const response = await apiClient.get('/orders');
  return response.data;
};

// Belirli bir role ait görevleri listele
export const getTasksByRole = async (role) => {
  const response = await apiClient.get(`/orders/tasks/${role}`);
  return response.data;
};

// Görevi tamamla
export const completeTask = async (taskId, variables = {}) => {
  const response = await apiClient.post(`/orders/tasks/${taskId}/complete`, variables);
  return response.data;
};

export default {
  createOrder,
  getAllOrders,
  getTasksByRole,
  completeTask,
};
