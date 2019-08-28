# Java try-with-resources

[![License](https://img.shields.io/github/license/mabrarov/java-try-with-resources.svg)](https://github.com/mabrarov/java-try-with-resources/tree/master/LICENSE)
[![Travis CI build status](https://travis-ci.org/mabrarov/java-try-with-resources.svg?branch=master)](https://travis-ci.org/mabrarov/java-try-with-resources)
[![Code coverage status](https://codecov.io/gh/mabrarov/java-try-with-resources/branch/master/graph/badge.svg)](https://codecov.io/gh/mabrarov/java-try-with-resources/branch/master)

Tests and examples with Java try-with-resources statement

## Building

[Maven Wrapper](https://github.com/takari/maven-wrapper) can be used for bulding:

```bash
./mvnw clean install
```

or on Windows

```cmd
mvnw.cmd clean install
```

## Building with JaCoCo code coverage report

```bash
./mvnw clean install -P jacoco
```

or on Windows

```cmd
mvnw.cmd clean install -P jacoco
```

JaCoCo HTML report is generated at `target/site/jacoco/index.html`
