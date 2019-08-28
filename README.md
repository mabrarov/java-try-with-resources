# Java try-with-resources

[![License](https://img.shields.io/github/license/mabrarov/java-try-with-resources.svg)](https://github.com/mabrarov/java-try-with-resources/tree/master/LICENSE)
[![Travis CI build status](https://travis-ci.org/mabrarov/java-try-with-resources.svg?branch=master)](https://travis-ci.org/mabrarov/java-try-with-resources)
[![AppVeyor CI build status](https://ci.appveyor.com/api/projects/status/e9gghbqg9knp0cfw/branch/master?svg=true)](https://ci.appveyor.com/project/mabrarov/java-try-with-resources/branch/master)
[![Code coverage status](https://codecov.io/gh/mabrarov/java-try-with-resources/branch/master/graph/badge.svg)](https://codecov.io/gh/mabrarov/java-try-with-resources/branch/master)

Tests and examples with Java try-with-resources statement

## Building

Prerequisites

* [JDK](https://openjdk.java.net/) 1.7+

Build steps

* [Maven Wrapper](https://github.com/takari/maven-wrapper) can be used for building:

   ```bash
   ./mvnw clean package
   ```

   or on Windows:

   ```cmd
   mvnw.cmd clean package
   ```

## Coverage report

To build, run unit tests and generate [JaCoCo](https://www.eclemma.org/jacoco/) report:

```bash
./mvnw clean install -P jacoco
```

or on Windows:

```cmd
mvnw.cmd clean install -P jacoco
```

JaCoCo HTML report is generated at `target/site/jacoco/index.html`
