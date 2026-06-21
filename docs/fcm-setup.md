# Настройка Push-уведомлений (FCM)

Для добавления push-уведомлений потребуется:

## 1. Firebase Console

1. Перейдите на https://console.firebase.google.com
2. Нажмите **Add project** → выберите ваш проект
3. Добавьте **Android app**: `com.example.carrentalapp`
4. Скачайте `google-services.json` и положите в `app/google-services.json`
5. Включите **Cloud Messaging** (FCM)

## 2. Android (app/build.gradle.kts)

```kotlin
// В конец build.gradle.kts:
plugins {
    id("com.google.gms.google-services")
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))
    implementation("com.google.firebase:firebase-messaging")
}
```

## 3. Firebase Service (Android)

Создайте файл `app/src/main/java/com/example/carrentalapp/push/FCMPushService.kt`:

```kotlin
package com.example.carrentalapp.push

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.carrentalapp.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMPushService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        createNotificationChannel()
        showNotification(message.notification?.title ?: "CarRental",
            message.notification?.body ?: "")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "bookings", "Бронирования",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }

    private fun showNotification(title: String, body: String) {
        val notification = NotificationCompat.Builder(this, "bookings")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .build()
        getSystemService(NotificationManager::class.java)
            .notify(System.currentTimeMillis().toInt(), notification)
    }
}
```

## 4. Backend (NotificationServiceImpl.java)

Добавьте Firebase Admin SDK в `backend/pom.xml`:

```xml
<dependency>
    <groupId>com.google.firebase</groupId>
    <artifactId>firebase-admin</artifactId>
    <version>9.3.0</version>
</dependency>
```

Обновите `NotificationServiceImpl.java` для реальной отправки:

```java
// Вместо log.info() — реальная отправка
Message message = Message.builder()
    .setToken(fcmToken)
    .setNotification(Notification.builder()
        .setTitle("Бронирование подтверждено")
        .setBody("Ваше бронирование " + carModel + " подтверждено!")
        .build())
    .build();
FirebaseMessaging.getInstance().send(message);
```

## 5. AndroidManifest.xml

```xml
<service
    android:name=".push.FCMPushService"
    android:exported="false">
    <intent-filter>
        <action android:name="com.google.firebase.MESSAGING_EVENT" />
    </intent-filter>
</service>
```
