# Foodly Frontend - React BPMN Order Management

React ile geliştirilmiş, Foodly API'nin BPMN süreçlerini yönetmek için basit ve modern bir kullanıcı arayüzü.

## 🎯 Proje Hakkında

Bu frontend uygulaması, BPMN (Business Process Model and Notation) öğrenmek için geliştirilmiş Foodly API'sini kullanır. Kullanıcılar sipariş oluşturabilir, görevleri yönetebilir ve süreç geçmişini görüntüleyebilir.

### Özellikler

- ✅ **Dashboard** - Aktif ve tamamlanmış süreçlerin özeti
- ✅ **Sipariş Oluşturma** - Yeni BPMN süreci başlatma
- ✅ **Görev Yönetimi** - Restoran, Mutfak ve Kurye görevlerini tamamlama
- ✅ **Süreç Geçmişi** - Tamamlanmış görevlerin timeline görünümü
- ✅ **Modern UI** - Temiz ve kullanıcı dostu arayüz

## 🚀 Kurulum

### Ön Gereksinimler

- **Node.js** (v18 veya üzeri)
- **npm** veya **yarn**
- **Backend API** (http://localhost:8080'de çalışıyor olmalı)

### Node.js Kurulumu (macOS)

```bash
# Homebrew ile Node.js kurulumu
brew install node

# Kurulumu doğrula
node --version
npm --version
```

### Projeyi Başlatma

1. **Bağımlılıkları yükleyin:**

```bash
cd frontend
npm install
```

2. **Backend'in çalıştığından emin olun:**

Backend API'nin `http://localhost:8080` adresinde çalışıyor olması gerekir.

```bash
# Backend klasöründe
cd ../backend
./gradlew bootRun
```

3. **Frontend'i başlatın:**

```bash
# Frontend klasöründe
npm run dev
```

Uygulama `http://localhost:3000` adresinde açılacaktır.

## 📁 Proje Yapısı

```
frontend/
├── src/
│   ├── components/          # Yeniden kullanılabilir bileşenler
│   │   ├── Navbar.jsx       # Navigation bar
│   │   ├── TaskCard.jsx     # Görev kartı
│   │   └── ProcessTimeline.jsx  # Süreç zaman çizelgesi
│   ├── pages/               # Sayfa bileşenleri
│   │   ├── Dashboard.jsx    # Ana dashboard
│   │   ├── CreateOrder.jsx  # Sipariş oluşturma formu
│   │   ├── TaskList.jsx     # Görev listesi
│   │   └── ProcessHistory.jsx  # Süreç geçmişi
│   ├── services/            # API servis katmanı
│   │   ├── apiClient.js     # Axios yapılandırması
│   │   ├── orderService.js  # Sipariş API çağrıları
│   │   └── processService.js  # Süreç API çağrıları
│   ├── App.jsx              # Ana uygulama ve routing
│   ├── App.css              # Global stiller
│   └── main.jsx             # React entry point
├── index.html
├── package.json
├── vite.config.js
└── README.md
```

## 🎨 Kullanım Rehberi

### 1. Dashboard

Ana sayfada şunları görebilirsiniz:
- Aktif süreç sayısı
- Tamamlanmış süreç sayısı
- Aktif ve tamamlanmış süreçlerin detayları

### 2. Yeni Sipariş Oluşturma

1. "Yeni Sipariş" menüsüne tıklayın
2. Kullanıcı ID ve Restoran ID girin
3. "Sipariş Oluştur" butonuna tıklayın
4. Ödeme otomatik olarak kontrol edilir (%70 başarı oranı)

### 3. Görev Yönetimi

1. "Görevler" menüsüne tıklayın
2. Rol seçin (Restoran, Mutfak, Kurye)
3. Bekleyen görevleri görüntüleyin
4. "Tamamla" butonu ile görevi tamamlayın

**Restoran için özel:** Siparişi onaylama/reddetme seçeneği vardır.

### 4. Süreç Geçmişi

1. "Geçmiş" menüsüne tıklayın
2. Process Instance ID girin
3. Görevlerin ne kadar sürdüğünü görüntüleyin

## 🔧 Teknolojiler

- **React 18** - UI kütüphanesi
- **React Router 6** - Client-side routing
- **Axios** - HTTP istemcisi
- **Vite** - Build tool ve dev server
- **CSS3** - Styling

## 🌐 API Entegrasyonu

Frontend, aşağıdaki backend endpoint'lerini kullanır:

### Sipariş API'leri
- `POST /api/orders` - Yeni sipariş oluştur
- `GET /api/orders/tasks/{role}` - Role göre görevleri listele
- `POST /api/orders/tasks/{taskId}/complete` - Görevi tamamla

### Süreç API'leri
- `GET /api/processes/active` - Aktif süreçler
- `GET /api/processes/completed` - Tamamlanmış süreçler
- `GET /api/processes/{processInstanceId}/history` - Süreç geçmişi

API çağrıları `src/services/` klasöründe organize edilmiştir.

## 🎓 React Kavramları

Bu projede kullanılan React kavramları:

### 1. Components (Bileşenler)
```jsx
// Fonksiyonel bileşen
function TaskCard({ task, onComplete }) {
  return <div>...</div>;
}
```

### 2. State Management
```jsx
// useState hook
const [tasks, setTasks] = useState([]);
```

### 3. Effects (Yan Etkiler)
```jsx
// useEffect hook - component mount olduğunda çalışır
useEffect(() => {
  fetchTasks();
}, []);
```

### 4. Props (Özellikler)
```jsx
// Parent'tan child'a veri aktarımı
<TaskCard task={task} onComplete={handleComplete} />
```

### 5. Routing
```jsx
// React Router ile sayfa yönlendirme
<Route path="/" element={<Dashboard />} />
```

### 6. Event Handling
```jsx
// Form submit
const handleSubmit = async (e) => {
  e.preventDefault();
  // ...
};
```

## 🐛 Sorun Giderme

### Backend'e bağlanamıyor

Backend'in çalıştığından emin olun:
```bash
curl http://localhost:8080/api/processes/active
```

### CORS hatası

Backend'de CORS yapılandırması yapılmış olmalı. `vite.config.js` dosyasında proxy ayarları yapılmıştır.

### Port zaten kullanımda

Farklı bir port kullanmak için `vite.config.js` dosyasını düzenleyin:
```js
export default defineConfig({
  server: {
    port: 3001, // Farklı port
  }
})
```

## 📝 Geliştirme Notları

### Build (Production)

```bash
npm run build
```

Build çıktısı `dist/` klasöründe oluşturulur.

### Preview (Production Build)

```bash
npm run preview
```

## 🎯 Öğrenme Hedefleri

Bu proje ile şunları öğrenebilirsiniz:

- ✅ React bileşenleri ve props
- ✅ State yönetimi (useState)
- ✅ Effect hook (useEffect)
- ✅ React Router ile routing
- ✅ Axios ile API entegrasyonu
- ✅ Form yönetimi
- ✅ Asenkron işlemler (async/await)
- ✅ CSS ile styling
- ✅ Component composition

## 📚 Kaynaklar

- [React Dokümantasyonu](https://react.dev/)
- [React Router](https://reactrouter.com/)
- [Axios](https://axios-http.com/)
- [Vite](https://vitejs.dev/)

## 🤝 Katkıda Bulunma

Bu bir öğrenme projesidir. Geliştirmeler ve iyileştirmeler için pull request gönderebilirsiniz.

## 📄 Lisans

Bu proje eğitim amaçlıdır.

---

**Geliştirici:** Recep Özdemir  
**Tarih:** Ekim 2025  
**Amaç:** React ve BPMN öğrenimi
