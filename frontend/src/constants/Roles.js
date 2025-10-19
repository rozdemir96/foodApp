export const RoleNames = {
  restaurant: 'Restoran',
  kitchen: 'Şef',
  courier: 'Kurye'
};

export const getRoleName = (role) => {
  return RoleNames[role] || role;
};

export const Roles = [
  { value: 'restaurant', label: '🏪 Restoran', color: '#e74c3c' },
  { value: 'kitchen', label: '👨‍🍳 Şef', color: '#f39c12' },
  { value: 'courier', label: '🚴 Kurye', color: '#3498db' }
];
