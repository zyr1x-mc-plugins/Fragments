# Fragments

A Paper plugin that adds a recurring in-world "Fragments" event: fragments economy,
event shop, schematic placement, NPC and WorldGuard region handling.

## Modules

| Module        | Path        | Purpose                                                        |
|---------------|-------------|----------------------------------------------------------------|
| Fragments     | root        | The main plugin (JAR: `build/dist/Fragments.jar`)              |
| Api           | `Api/`      | Public API contract for third-party plugins (JAR: `build/dist/Fragments-api.jar`) |
| Integration   | `Integration/` | Demo consumer proving the API can be connected (not shipped) |

## Build

```bash
./gradlew build
```

After a successful build the following artifacts are produced automatically:

```text
build/dist/
├── Fragments.jar      # plugin (already bundles the API classes)
└── Fragments-api.jar  # standalone API for developers
```

The Api module also produces `Api/build/libs/Fragments-api-1.0-SNAPSHOT.jar`
(plus `-sources` and `-javadoc` jars).

---

# API

The Fragments API exposes a stable, versioned contract over the plugin's internal
implementation. Third-party plugins use it without touching internal classes.

## 1. Подключение API (Gradle)

The API is not (yet) published to a public Maven repository. Two options:

**Option A — project dependency (monorepo):**

```kotlin
dependencies {
    compileOnly(project(":Api"))
}
```

**Option B — local JAR:**

1. Build the project: `./gradlew build`
2. Add `build/dist/Fragments-api.jar` to your plugin's classpath as `compileOnly`.

If a Maven repository is configured later, it can be referenced as:

```kotlin
compileOnly("ru.lewis.fragments:api:1.0.1")
```

## 2. Получение API

**Вариант 1 — статический метод (рекомендуется, работает где угодно, без DI):**

```kotlin
val api: FragmentsApi? = FragmentsApi.get()

if (api != null) {
    // работа с API
}
```

**Вариант 2 — Bukkit ServicesManager:**

```kotlin
val api: FragmentsApi? = Bukkit.getServicesManager().load(FragmentsApi::class.java)
```

- Оба варианта эквивалентны: `FragmentsApi.get()` внутри резолвит экземпляр через ServicesManager.
- API регистрируется после полной инициализации плагина и снимается при shutdown.
- Если плагин Fragments не установлен/выключен — возвращается `null`.

## 3. Пример использования

```kotlin
import org.bukkit.entity.Player
import ru.lewis.fragments.api.FragmentsApi

fun showBalance(player: Player): Int {
    val api = FragmentsApi.get() ?: return -1
    return api.economy.getFragments(player)
}
```

## 4. Основные возможности

`FragmentsApi` exposes three sub-APIs:

- **`economy: FragmentsEconomy`** — фрагменты-баланс игроков:
  `getFragments`, `addFragments`, `removeFragments`, `setFragments`, `getTop`.
- **`event: FragmentEventController`** — управление событием:
  `isRunning()`, `getLocation()`, `start()`, `stop()`.
- **`purchases: FragmentPurchaseController`** — состояние магазина события:
  `isPurchaseEnabled()`, `openShop(player)`.

### API события

- `FragmentBalanceUpdateEvent` — баланс игрока изменился
  (свойства: `player`, `uniqueId`, `oldBalance`, `newBalance`, `delta`).
- `FragmentEventStartedEvent` — событие полностью запущено (`location`).
- `FragmentEventStoppedEvent` — событие остановлено.

```kotlin
@EventHandler
fun onBalance(event: FragmentBalanceUpdateEvent) {
    println("${event.uniqueId} change by ${event.delta}")
}
```

## 5. Ограничения по потокам

Все методы API и события вызываются/обрабатываются на **main server thread**.
Не вызывайте их из фоновых потоков.

## 6. Версия API

`ru.lewis.fragments.api.Fragments.API_VERSION` (SemVer). Обновляйте проверку версии,
если используете особенности, добавленные в более новых версиях контракта.
