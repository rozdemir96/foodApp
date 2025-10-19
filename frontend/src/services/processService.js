import apiClient from './apiClient';

// Aktif süreçleri getir
export const getActiveProcesses = async () => {
  const response = await apiClient.get('/processes/active');
  return response.data;
};

// Tamamlanmış süreçleri getir
export const getCompletedProcesses = async () => {
  const response = await apiClient.get('/processes/completed');
  return response.data;
};

// Sipariş detayını getir
export const getOrderProcess = async (orderId) => {
  const response = await apiClient.get(`/processes/order/${orderId}`);
  return response.data;
};

// Süreç geçmişini getir
export const getProcessHistory = async (processInstanceId) => {
  const response = await apiClient.get(`/processes/${processInstanceId}/history`);
  return response.data;
};

export default {
  getActiveProcesses,
  getCompletedProcesses,
  getOrderProcess,
  getProcessHistory,
};
