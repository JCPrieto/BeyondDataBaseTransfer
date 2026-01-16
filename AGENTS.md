# Repository Guidelines

## Project Structure & Module Organization

- `src/main/java` contains the Swing desktop app (`es.jklabs.*`), UI panels, and utility classes.
- `src/main/resources` holds i18n bundles, icons, and JSON assets (e.g., Firebase config under
  `src/main/resources/json`).
- Build output and distributions are managed by Gradle (`build/` is generated).

## Build, Test, and Development Commands

- `gradle build` compiles the app and produces artifacts in `build/`.
- `gradle run` launches the desktop application using `mainClass` from `build.gradle`.
- `gradle test` runs JUnit 4 tests (if present).
- `gradle distZip` creates a distributable ZIP with the application and README.

## Coding Style & Naming Conventions

- Use 4-space indentation for Java code and align with existing formatting.
- Packages follow `es.jklabs.*`; classes use `PascalCase`, methods/fields use `camelCase`.
- Keep UI strings in resource bundles under `src/main/resources/i18n`.
- Prefer `final` for constants in `es.jklabs.utilidades.Constantes` and similar utility classes.

## Testing Guidelines

- JUnit 4 is the test framework (see Gradle dependencies).
- If adding tests, place them in `src/test/java` and name classes `*Test`.
- Run `gradle test` before submitting PRs; note any manual UI verification in the PR.

## Commit & Pull Request Guidelines

- Commit messages are short and imperative; Spanish or English is acceptable (e.g., "Nueva URL de la web", "Update
  README").
- PRs should include: purpose, key changes, how to run/verify, and any UI screenshots for Swing changes.
- Link related issues or tickets when applicable.

## Configuration & Security Notes

- The app depends on Java 21, MySQL client tools, and LibNotify on Linux.
- Avoid committing real credentials in `src/main/resources/json`; use placeholders or sanitized configs when possible.
