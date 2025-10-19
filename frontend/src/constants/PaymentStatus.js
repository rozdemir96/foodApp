export const PaymentStatus = {
  PENDING: {
    value: 'PENDING',
    description: 'Beklemede'
  },
  APPROVED: {
    value: 'APPROVED',
    description: 'Onaylandı'
  },
  REJECTED: {
    value: 'REJECTED',
    description: 'Reddedildi'
  }
};

export const getPaymentStatusDescription = (status) => {
  return PaymentStatus[status]?.description || status;
};
