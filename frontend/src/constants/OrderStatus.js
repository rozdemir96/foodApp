export const OrderStatus = {
  PENDING: {
    value: 'PENDING',
    description: 'Beklemede'
  },
  PAYMENT_APPROVED: {
    value: 'PAYMENT_APPROVED',
    description: 'Ödeme Onaylandı'
  },
  ORDER_APPROVED: {
    value: 'ORDER_APPROVED',
    description: 'Sipariş Onaylandı'
  },
  PREPARING: {
    value: 'PREPARING',
    description: 'Hazırlanıyor'
  },
  READY_FOR_DELIVERY: {
    value: 'READY_FOR_DELIVERY',
    description: 'Teslimata Hazır'
  },
  DELIVERED: {
    value: 'DELIVERED',
    description: 'Teslim Edildi'
  },
  REJECTED: {
    value: 'REJECTED',
    description: 'Reddedildi'
  }
};

// Helper function to get description
export const getOrderStatusDescription = (status) => {
  return OrderStatus[status]?.description || status;
};
