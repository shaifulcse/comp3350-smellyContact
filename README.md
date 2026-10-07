# Contacts – three-tier Android app (Java + SQLite)

## Layers
| Tier | Package | Knows about |
|---|---|---|
| Presentation | `presentation` | Activities, layouts, `ContactManager` |
| Logic | `logic` | `ContactManager` (validation, rules), the `ContactPersistence` interface |
| Persistence | `persistence` | `ContactPersistence` interface, `fake/` (in-memory), `sqlite/` (SQLite) |
| Domain | `objects` | `Contact`, shared by all tiers |

`ContactsApp` (the `Application` class) is the only place that picks the database
and wires persistence -> logic. Activities get the logic tier with
`((ContactsApp) getApplication()).getContactManager()`.

## Choosing the database
Build > Select Build Variant:
- `realDebug`  -> SQLite (`contacts.db`, survives restarts)
- `fakeDebug`  -> in-memory, preloaded with 3 sample contacts

Both variants can be installed side by side (the fake one has the `.fake` id suffix).

## Run
Open the folder in Android Studio (Koala or newer), let Gradle sync, press Run.
Unit tests: `./gradlew testFakeDebugUnitTest` (or right-click `ContactManagerTest`).
