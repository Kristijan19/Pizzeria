# Pizzeria Android App

Андроид апликација за нарачување пици со Firebase интеграција.

## Технологии

- **Android** (Java, минимум SDK 24)
- **Firebase Authentication** (Anonymous, Email/Password, Google, Facebook)
- **Firebase Firestore** (Cloud база)
- **Firebase Messaging** (Push нотификации)
- **Firebase Analytics** (Аналитика)
- **Room** (Локална база)
- **MVVM** архитектура (ViewModel, LiveData, Repository)

## Функционалности

-  Најава со 4 методи
-  Мени со 16 пици
-  Кошница и нарачување
-  Историја на нарачки
-  Двојазичност (МК/EN)
-  Push нотификации
-  Firebase Analytics
-  Локално и cloud снимање
-  Admin панел (нарачки + менување статус + цени на пици)
-  Email известувања преку EmailJS (бесплатен tier)

## Поставување

1. Клонирај го репото
2. Додади `google-services.json` во `app/`
3. Постави Firebase credentials во `strings.xml`
4. Стартувај во Android Studio

## Admin (Firestore)

Колекција `admins`, документ ID = Firebase UID, поле `isAdmin: true`.

## Цени на пици (Firestore)

Колекција `menu_prices`, документ `current`, полиња: `margherita`, `pepperoni`, ... (број MKD).
Admin ги менува од таб **Pizza prices** во Admin Panel.

## EmailJS (бесплатно)

1. Направи account на [emailjs.com](https://www.emailjs.com)
2. Додади Email Service (Gmail и сл.)
3. Направи 2 templates со променливи: `{{to_email}}`, `{{to_name}}`, `{{subject}}`, `{{message}}`
4. Во `app/src/main/res/values/strings.xml` замени:
   - `emailjs_service_id`
   - `emailjs_public_key`
   - `emailjs_template_order` (при нова нарачка)
   - `emailjs_template_status` (при промена на статус од admin)

Пример template body: `{{message}}`  
Пример To field во template: `{{to_email}}`

Mail се праќа кога:
- корисникот ќе потврди нарачка (ако има e-mail)
- admin ќе смени статус на Preparing / On the way / Delivered