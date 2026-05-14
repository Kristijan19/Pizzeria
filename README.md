# 🍕 Pizzeria Android App - Водич за поставување

## Структура на проектот

```
app/src/main/
├── java/com/anas/pizzeria/
│   ├── data/
│   │   ├── local/
│   │   │   ├── dao/OrderDao.java          ← Room DAO
│   │   │   ├── entity/OrderEntity.java    ← Room Entity
│   │   │   └── PizzeriaDatabase.java      ← Room Database
│   │   └── OrderRepository.java           ← Room + Firestore
│   ├── model/
│   │   └── Pizza.java
│   ├── service/
│   │   └── PizzeriaMessagingService.java  ← FCM
│   └── ui/
│       ├── auth/
│       │   ├── LoginActivity.java         ← Anon, Email, Google, FB
│       │   └── RegisterActivity.java
│       ├── cart/
│       │   ├── CartActivity.java
│       │   └── CartViewModel.java
│       ├── history/
│       │   ├── HistoryActivity.java
│       │   ├── HistoryAdapter.java
│       │   ├── HistoryFragment.java
│       │   └── HistoryViewModel.java
│       ├── menu/
│       │   ├── MenuFragment.java
│       │   └── PizzaAdapter.java
│       ├── profile/
│       │   └── ProfileFragment.java
│       ├── MainActivity.java
│       └── SplashActivity.java
└── res/
    ├── layout/                            ← Telefon Portrait
    ├── layout-land/                       ← Telefon Landscape
    ├── layout-sw600dp/                    ← Tablet Portrait
    ├── values/strings.xml                 ← Англиски (default)
    ├── values-mk/strings.xml              ← Македонски
    └── values-sw600dp/bools.xml           ← is_tablet=true
```

---

## ✅ ЧЕКОР 1 — Firebase Console

1. Отиди на https://console.firebase.google.com
2. Креирај нов проект (или отвори постоечки)
3. **Додади Android апп:**
   - Package name: `com.anas.pizzeria`
   - Преземи `google-services.json`
   - Копирај го во `app/` папката (замени го placeholder-от)

4. **Овозможи Authentication методи:**
   - Firebase Console → Authentication → Sign-in method
   - ✅ Anonymous
   - ✅ Email/Password
   - ✅ Google
   - ✅ Facebook

5. **Креирај Firestore Database:**
   - Firebase Console → Firestore Database → Create database
   - Избери `Start in test mode` (за развој)

6. **Овозможи Firebase Messaging:**
   - Автоматски е вклучен со додавање на firebase-messaging зависноста

---

## ✅ ЧЕКОР 2 — Google Sign-In

1. Во Firebase Console → Authentication → Sign-in method → Google → Enable
2. Копирај го **Web client ID**
3. Во `res/values/strings.xml` замени:
   ```xml
   <string name="default_web_client_id">ТВОЈОТ_WEB_CLIENT_ID</string>
   ```
   > Истата вредност се наоѓа и во `google-services.json` под `oauth_client.client_id`

---

## ✅ ЧЕКОР 3 — Facebook Login

1. Отиди на https://developers.facebook.com
2. Креирај нова апликација → Consumer
3. Додади **Facebook Login** продукт
4. Под Settings → Basic земи го **App ID** и **Client Token**
5. Во `res/values/strings.xml` замени:
   ```xml
   <string name="facebook_app_id">ТВОЈОТ_FB_APP_ID</string>
   <string name="facebook_client_token">ТВОЈОТ_CLIENT_TOKEN</string>
   <string name="fb_login_protocol_scheme">fbТВОЈОТ_FB_APP_ID</string>
   ```
6. Во Facebook Developer Console → Facebook Login → Settings:
   - Додади Key Hash за debug:
     ```bash
     keytool -exportcert -alias androiddebugkey -keystore ~/.android/debug.keystore | openssl sha1 -binary | openssl base64
     ```

---

## ✅ ЧЕКОР 4 — Firestore Security Rules (за production)

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /orders/{orderId} {
      allow read, write: if request.auth != null;
    }
  }
}
```

---

## 📱 Поддржани конфигурации

| Уред         | Ориентација | Layout                    |
|-------------|-------------|---------------------------|
| Телефон     | Portrait    | `layout/`                 |
| Телефон     | Landscape   | `layout-land/`            |
| Таблет      | Portrait    | `layout-sw600dp/`         |
| Таблет      | Landscape   | `layout-sw600dp-land/`*   |

*Опционално — може да се додаде подоцна.

---

## 🔥 Firebase функционалности

| Модул                  | Употреба во апп                              |
|-----------------------|---------------------------------------------|
| Authentication        | Анонимен, Email/Pass, Google, Facebook       |
| Firestore             | Cloud синхронизација на нарачки              |
| Firebase Messaging    | Push нотификации за промоции/статус          |
| Firebase Analytics    | Login events, screen views, purchase events |

---

## 🗄️ Room (Локална база)

| Класа               | Улога                         |
|--------------------|-------------------------------|
| `OrderEntity`      | Табела `orders`               |
| `OrderDao`         | Insert, query по user         |
| `PizzeriaDatabase` | Singleton Room database       |
| `OrderRepository`  | Bridge: Room ↔ Firestore      |

---

## 🌍 Интернационализација

- `res/values/strings.xml` → Англиски (default)
- `res/values-mk/strings.xml` → Македонски

За да го тестираш македонскиот јазик:
- Телефон → Settings → Language → Македонски
- Или во Android Studio → Run/Debug → Edit configuration → Language: mk
