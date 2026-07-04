# Repository Guidelines

## Project Structure & Module Organization

- `src/main/java` contains the Swing desktop app (`es.jklabs.*`), UI panels, and utility classes.
- `src/main/resources` holds i18n bundles, icons, and JSON assets (e.g., Firebase config under
  `src/main/resources/json`).
- Build output and distributions are managed by Gradle (`build/` is generated).

## Build, Test, and Development Commands

- `./gradlew build` compiles the app and produces artifacts in `build/`.
- `./gradlew run` launches the desktop application using `mainClass` from `build.gradle`.
- `./gradlew test` runs JUnit 4 tests.
- `./gradlew distZip` creates a distributable ZIP with the application and README.
- `./gradlew installLinuxDesktopEntry` registers a local GNOME desktop entry for IDE/manual Linux testing.
- `./gradlew sonar` runs the SonarQube analysis when `SONAR_TOKEN` is available.

## Coding Style & Naming Conventions

- Use 4-space indentation for Java code and align with existing formatting.
- Packages follow `es.jklabs.*`; classes use `PascalCase`, methods/fields use `camelCase`.
- Keep UI strings in resource bundles under `src/main/resources/i18n`.
- Prefer `final` for constants in `es.jklabs.utilidades.Constantes` and similar utility classes.
- Swing workers that execute `mysql` or `mysqldump` should extend `AbstractMysqlWorker`, reuse its common connection
  argument helper, and publish progress descriptions for the main progress bar.

## Testing Guidelines

- JUnit 4 is the test framework (see Gradle dependencies).
- If adding tests, place them in `src/test/java` and name classes `*Test`.
- Run `./gradlew test` before submitting PRs; note any manual UI verification in the PR.
- The release workflow runs tests before SonarQube and release creation; tests are blocking, SonarQube is informational.
- SonarQube coverage comes from JaCoCo XML (`jacocoTestReport`); keep XML reporting enabled when changing Gradle or CI.
- Gradle tests set `beyond.database.transfer.config.dir` and `beyond.database.transfer.logs.dir` to folders under
  `build/`; tests must not read from or write to the user's real app configuration or logs.

## Commit & Pull Request Guidelines

- Commit messages are short and imperative; Spanish or English is acceptable (e.g., "Nueva URL de la web", "Update
  README").
- PRs should include: purpose, key changes, how to run/verify, and any UI screenshots for Swing changes.
- Link related issues or tickets when applicable.

## Configuration & Security Notes

- The app depends on Java 21, MySQL client tools, and D-Bus on Linux for native notifications.
- Avoid committing real credentials in `src/main/resources/json`; use placeholders or sanitized configs when possible.
- User configuration is stored under the app config directory, while logs use OS-specific application data folders.
  Use the `beyond.database.transfer.config.dir` and `beyond.database.transfer.logs.dir` system properties when tests or
  tools need isolated paths.
- Stored server passwords use the versioned encryption format in `UtilidadesEncryptacion`; keep legacy decryption
  support
  when rotating keys or changing the cipher.
