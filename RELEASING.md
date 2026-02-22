# Releasing

This project publishes the following modules:
- `graphql-mock-matchers-core`
- `graphql-mock-matchers-wiremock`

`examples-wiremock-junit5` is not published.

## Prerequisites
- Push access to `main`.
- GitHub repository secrets configured:
  - `OSSRH_USERNAME`
  - `OSSRH_PASSWORD`
  - `SIGNING_KEY` (ASCII-armored private key)
  - `SIGNING_PASSWORD`
- Java 21 installed locally.

## 1. Prepare a release version
Set a release version (no `-SNAPSHOT`):

```bash
./gradlew setReleaseVersion -PreleaseVersion=1.0.0
```

Commit the version change:

```bash
git add gradle.properties
git commit -m "Release 1.0.0"
```

## 2. Verify locally

```bash
./gradlew clean test
./gradlew :graphql-mock-matchers-core:publishToMavenLocal :graphql-mock-matchers-wiremock:publishToMavenLocal
```

## 3. Tag and push

```bash
git tag v1.0.0
git push origin main --tags
```

Pushing tag `v*` triggers `.github/workflows/publish.yml`.

## 4. GitHub publish workflow
The workflow runs:

```bash
./gradlew --no-daemon publish
```

and uses the repository secrets for OSSRH credentials and artifact signing.

## 5. Post-release: move to next snapshot
After release succeeds:

```bash
./gradlew setNextSnapshotVersion
```

This infers the next patch snapshot (for example `1.0.0` -> `1.0.1-SNAPSHOT`).

Commit and push:

```bash
git add gradle.properties
git commit -m "Start next snapshot"
git push origin main
```

## Notes
- If needed, you can set snapshot version explicitly:

```bash
./gradlew setNextSnapshotVersion -PnextVersion=1.1.0-SNAPSHOT
```

- CI for pushes/PRs is in `.github/workflows/ci.yml`.
- Dependency update PRs are managed by `.github/dependabot.yml`.
