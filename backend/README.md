# Foodly API - BPMN Öğrenme Projesi

**Flowable BPMN Engine** kullanarak sipariş yönetimi workflow'unu simüle eden Spring Boot uygulaması.

## Proje Hakkında

Bu proje, **BPMN 2.0 (Business Process Model and Notation)** konseptlerini öğrenmek için geliştirilmiş basit bir online yemek sipariş sistemidir.

### Öğrenme Hedefleri

- **BPMN Process Design** - XML tabanlı süreç modelleme
- **ServiceTask & UserTask** - Otomatik ve manuel görevler
- **ExclusiveGateway** - Koşullu dallanma mantığı
- **JavaDelegate** - Özel iş mantığı entegrasyonu
- **ExecutionListener** - Süreç olaylarını dinleme
- **Process Variables** - Süreç boyunca veri paylaşımı
- **Task Management** - Görev atama ve tamamlama

---

## BPMN Süreç Akışı

```
┌─────────────────────────────────────────────────────────────────────┐
│                        FOOD ORDER PROCESS                            │
└─────────────────────────────────────────────────────────────────────┘

     START
       │
       ▼
 ┌──────────────────┐
 │  Ödemeyi Onayla  │  ◄── ServiceTask (paymentApprovalDelegate)
 │  (Payment Task)  │      %70 başarı, %30 red
 └────────┬─────────┘
          │
          ▼
     ┌─────────┐
     │ Ödeme   │
     │ Onaylı? │  ◄── ExclusiveGateway
     └────┬────┘
          │
    ┌─────┴──────┐
  Evet          Hayır
    │             │
    ▼             ▼
┌─────────┐   [REJECTED]
│Siparişi │
│ Onayla  │  ◄── UserTask (chef)
│ (Chef)  │      approved: true/false
└────┬────┘
     │
     ▼
 ┌─────────┐
 │Sipariş  │
 │Onaylı?  │  ◄── ExclusiveGateway
 └────┬────┘
      │
 ┌────┴─────┐
Evet      Hayır
 │          │
 ▼          ▼
┌─────────┐  [REJECTED]
│Hazırla  │
│(Kitchen)│  ◄── UserTask (kitchen)
└────┬────┘
     │
     ▼
┌──────────┐
│Kuryeye   │
│   Ver    │  ◄── UserTask (courier)
│(Courier) │
└────┬─────┘
     │
     ▼
  [DELIVERED]
```

---

### Uygulamayı Başlatın

```text
./gradlew clean build
./gradlew bootRun
```

Uygulama `http://localhost:8080` adresinde çalışacaktır.

---

## API Kullanım Rehberi

### Swagger UI

Tüm API endpoint'lerini keşfetmek için:

`http://localhost:8080/swagger-ui.html`

---

## Adım Adım Test Senaryosu

### ADIM 1: Yeni Sipariş Oluştur

BPMN sürecini başlatır ve ödeme kontrolü yapar.

```text
POST /api/orders
Content-Type: application/json

{
  "userId": 1,
  "restaurantId": 1
}
```

**Yanıt:**
```text
{
  "id": 1,
  "userId": 1,
  "restaurantId": 1,
  "status": "PAYMENT_APPROVED",
  "orderDate": "2025-10-18T14:30:00",
  "processInstanceId": "44bf4bea-ac4a-11f0-bd47-4e936128c6c4"
}
```

> Not: Ödeme %70 ihtimalle onaylanır. REJECTED olursa süreç biter!

---

### ADIM 2: Chef Görevlerini Listele

Ödeme onaylanan siparişler için chef'e atanan görevleri görüntüleyin.

```text
GET /api/orders/tasks/chef
```

**Yanıt:**
```text
[
  {
    "id": "task-12345",
    "name": "Siparişi Onayla",
    "assignee": "chef",
    "createTime": "2025-10-18T14:30:05",
    "processInstanceId": "44bf4bea-ac4a-11f0-bd47-4e936128c6c4"
  }
]
```

---

### ADIM 3: Chef Siparişi Onaylasın

```text
POST /api/orders/tasks/task-12345/complete
Content-Type: application/json

{
  "approved": true
}
```

> approved: false gönderirseniz sipariş REJECTED olur ve süreç biter.

---

### ADIM 4: Mutfak Görevlerini Listele

Chef onayladıysa, mutfağa hazırlama görevi düşer.

```text
GET /api/orders/tasks/kitchen
```

---

### ADIM 5: Mutfak Siparişi Hazırlasın

```text
POST /api/orders/tasks/task-67890/complete
Content-Type: application/json

{}
```

> Mutfak görevi için değişken gerekmez, boş obje gönderin.

---

### ADIM 6: Kurye Görevlerini Listele

```text
GET /api/orders/tasks/courier
```

---

### ADIM 7: Kurye Teslim Etsin

```text
POST /api/orders/tasks/task-99999/complete
Content-Type: application/json

{}
```

Sipariş DELIVERED durumuna geçer ve süreç tamamlanır.

---

## Süreç Takip Endpoint'leri

### Aktif Süreçleri Görüntüle

```text
GET /api/processes/active
```

Henüz tamamlanmamış (bekleyen) süreçleri listeler.

---

### Tamamlanmış Süreçleri Görüntüle

```text
GET /api/processes/completed
```

Başarıyla teslim edilen veya reddedilen siparişleri gösterir.

---

### Sipariş Detayını Sorgula

```text
GET /api/processes/order/1
```

Belirli bir siparişin BPMN süreç bilgilerini getirir.

---

### Süreç Geçmişini İncele

```text
GET /api/processes/44bf4bea-ac4a-11f0-bd47-4e936128c6c4/history
```

Sürecin hangi adımlardan geçtiğini, ne kadar sürdüğünü gösterir.

**Örnek Yanıt:**
```text
[
  {
    "taskName": "Siparişi Onayla",
    "assignee": "chef",
    "startTime": "2025-10-18T14:30:05",
    "endTime": "2025-10-18T14:32:10",
    "durationInMillis": 125000
  },
  {
    "taskName": "Hazırla",
    "assignee": "kitchen",
    "startTime": "2025-10-18T14:32:10",
    "endTime": "2025-10-18T14:45:00",
    "durationInMillis": 770000
  }
]
```

---

## BPMN Kavramları ve Kod Örnekleri

### ServiceTask - Otomatik Görev

BPMN XML:
```text
<serviceTask id="paymentTask"
             name="Ödemeyi Onayla"
             flowable:delegateExpression="${paymentApprovalDelegate}"/>
```

Java Implementation:
```text
@Service("paymentApprovalDelegate")
public class PaymentApprovalDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) {
        // %70 ihtimalle ödeme başarılı
        boolean paymentApproved = random.nextInt(100) < 70;
        execution.setVariable("paymentApproved", paymentApproved);
    }
}
```

**Öğrenilen:** ServiceTask otomatik çalışır, kullanıcı müdahalesi gerektirmez.

---

### UserTask - Manuel Görev

BPMN XML:
```text
<userTask id="reviewOrder"
          name="Siparişi Onayla"
          flowable:assignee="chef"/>
```

Java Kullanımı:
```text
// Görevi listele
List<Task> tasks = taskService.createTaskQuery()
    .taskAssignee("chef")
    .list();

// Görevi tamamla
taskService.complete(taskId, variables);
```

**Öğrenilen:** UserTask bir kullanıcının aksiyonu bekler.

---

### ExclusiveGateway - Koşullu Dallanma

BPMN XML:
```text
<exclusiveGateway id="paymentApprovedGateway" name="Ödeme Onaylı mı?"/>

<sequenceFlow sourceRef="paymentApprovedGateway" targetRef="reviewOrder">
  <conditionExpression>${paymentApproved}</conditionExpression>
</sequenceFlow>

<sequenceFlow sourceRef="paymentApprovedGateway" targetRef="endRejected">
  <conditionExpression>${!paymentApproved}</conditionExpression>
</sequenceFlow>
```

**Öğrenilen:** Process variable'a göre farklı yola gider.

---

### ExecutionListener - Olay Dinleyici

BPMN XML:
```text
<userTask id="prepareOrder" name="Hazırla">
  <extensionElements>
    <flowable:executionListener
        event="start"
        delegateExpression="${orderStatusUpdateListener}"/>
  </extensionElements>
</userTask>
```

Java Implementation:
```text
@Component("orderStatusUpdateListener")
public class OrderStatusUpdateListener implements ExecutionListener {
    @Override
    public void notify(DelegateExecution execution) {
        String activityId = execution.getCurrentActivityId();
        // Activity'ye göre order status güncelle
        if ("prepareOrder".equals(activityId)) {
            order.setStatus(OrderStatus.PREPARING);
        }
    }
}
```

**Öğrenilen:** Task başlamadan/bitmeden önce kod çalıştırabilirsiniz.

---

## Veritabanı Şeması

### Uygulama Tabloları

- **orders** - Sipariş bilgileri
- **payments** - Ödeme kayıtları
- **users** - Kullanıcı bilgileri

### Flowable Tabloları (Otomatik Oluşur)

- **ACT_RU_TASK** - Aktif görevler
- **ACT_RU_EXECUTION** - Aktif süreç instance'ları
- **ACT_HI_PROCINST** - Süreç geçmişi
- **ACT_HI_TASKINST** - Görev geçmişi
- **ACT_RE_PROCDEF** - Süreç tanımları

---

## Sipariş Durumları (OrderStatus)

| Durum | Açıklama |
|-------|----------|
| `PENDING` | Sipariş oluşturuldu, ödeme bekleniyor |
| `PAYMENT_APPROVED` | Ödeme onaylandı, chef onayı bekleniyor |
| `ORDER_APPROVED` | Chef onayladı, hazırlık aşamasında |
| `PREPARING` | Mutfakta hazırlanıyor |
| `READY_FOR_DELIVERY` | Kurye teslim almayı bekliyor |
| `DELIVERED` | Teslim edildi |
| `REJECTED` | Reddedildi (ödeme veya chef reddi) |

---

## Test Önerileri

### Senaryo 1: Başarılı Sipariş Akışı
1. Sipariş oluşturulur → Ödeme onaylanır (paymentApproved = true).
2. Chef görevi düşer; chef onaylar (approved = true) → sipariş `ORDER_APPROVED` durumuna geçer.
3. Mutfak hazırlama görevini tamamlar → sipariş `PREPARING` ve sonrasında `READY_FOR_DELIVERY` durumlarına güncellenir.
4. Kurye teslimat görevini tamamlar → sipariş `DELIVERED` durumuna geçer.

Sürecin başarılı tamamlanmasıyla birlikte şu şeyler olur:
- Flowable süreç instance'ı tamamlanır (process instance completed) ve çalışma zamanı tablolarından (ör. ACT_RU_EXECUTION) kaldırılır.
- Süreç geçmişi (ACT_HI_PROCINST, ACT_HI_TASKINST) kaydedilir; hangi görevlerin hangi sürelerde tamamlandığı history tablosunda bulunur.
- Uygulama veritabanında `orders` tablosunda siparişin `status` alanı `DELIVERED` olarak güncellenir.
- İlgili `payments` kaydı ödeme onayı bilgisini içerir.

### Senaryo 2: Ödeme Reddi
1. Sipariş oluştur → Ödeme reddedilir
2. Süreç REJECTED ile sonlanır

### Senaryo 3: Chef Reddi
1. Sipariş oluştur → Ödeme onaylanır
2. Chef reddeder (`approved: false`)
3. Süreç REJECTED ile sonlanır

---
