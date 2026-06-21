# Подпись APK для публикации

## Способ 1: Android Studio (простой)

1. Откройте проект в Android Studio
2. Меню: **Build → Generate Signed Bundle / APK**
3. Выберите **APK**
4. Нажмите **Create new...** и заполните:
   - Key store path: `C:\Users\bekha\Desktop\CarRentalApp\keystore.jks`
   - Password: придумайте
   - Alias: `carrental`
   - Validity: `25` лет
5. Заполните сертификат (можно любые данные)
6. Нажмите OK, затем Next
7. Выберите **release** и **V2 (Full APK Signature)**
8. Нажмите Finish

Готовый APK будет в `app/release/app-release.apk`

## Способ 2: Через Gradle (без Android Studio)

1. Создайте файл `keystore.properties` в корне проекта:
```
storePassword=ваш_пароль
keyPassword=ваш_пароль
keyAlias=carrental
storeFile=keystore.jks
```

2. Добавьте в `app/build.gradle.kts`:
```kotlin
android {
    signingConfigs {
        create("release") {
            val props = Properties().apply {
                load(file("keystore.properties").inputStream())
            }
            storeFile = file(props.getProperty("storeFile"))
            storePassword = props.getProperty("storePassword")
            keyAlias = props.getProperty("keyAlias")
            keyPassword = props.getProperty("keyPassword")
        }
    }
    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
```

3. Соберите подписанный APK:
```bash
cd app
./gradlew assembleRelease
```
