# Password Generator (Commons CLI)

A command-line password generator built with **Java** and **Maven**.  
It supports custom length, batch generation, optional special characters, and a **Strong mode** enhancement that enforces basic password rules.

## Features

- **Length**
  - `-l, --length <N>` sets password length (default: `10`)
- **Batch generation**
  - `-c, --count <N>` generates `N` passwords (default: `1`)
- **Special characters**
  - `-s, --special` allows symbols: `@#$%!&*`
- **Strong mode (Enhancement)**
  - `-x, --strong` guarantees the password contains:
    - **at least 1 uppercase letter**
    - **at least 1 digit**
    - **at least 1 special character**
  - Strong mode **implies `--special`**
  - Note: `--strong` requires `--length >= 8`
- **Help**
  - `-h, --help` shows usage and examples

## Enhancement implemented

**Strong password rules (`--strong`)**

This enhancement adds a “policy” mode that *guarantees* the generated password includes required character categories (uppercase, digit, symbol).  
Implementation approach:

- Uses a **construct + shuffle** strategy:
  1. Randomly choose distinct positions in the output password.
  2. Place the required uppercase/digit/symbol characters in those random positions.
  3. Fill remaining positions using the allowed character pool.
- Ensures the final password still has the exact requested length.

## Tech stack

- Java + Maven
- Apache Commons CLI (argument parsing)
- JUnit 5 (tests)
- Maven Shade Plugin (runnable “fat” jar)
- Exec Maven Plugin (dev run via `mvn exec:java`)

## Project structure

```text
src/
  main/
    java/
      uk/ac/gold/ade/passwordapp/
        PasswordApp.java
        utils/
          Generator.java
          CharacterSet.java
  test/
    java/
      uk/ac/gold/ade/passwordapp/utils/
        GeneratorTest.java
pom.xml
```

* `PasswordApp.java` — CLI entry point (parses options + prints output)
* `Generator.java` — generates password strings (supports strong rules)
* `CharacterSet.java` — character sets (letters / digits / symbols)
* `GeneratorTest.java` — unit tests (length boundaries + strong mode rules)


## Build

From the project root (where `pom.xml` is):

```bash
mvn clean package
```

## Run

### Option A: Run via Maven (recommended while developing)

```bash
# Show help
mvn -q exec:java -Dexec.args="-h"

# Default (one password, default length)
mvn -q exec:java

# Custom length
mvn -q exec:java -Dexec.args="-l 16"

# Multiple passwords + special characters
mvn -q exec:java -Dexec.args="-l 16 -s -c 15"

# Strong mode (must include uppercase + digit + symbol)
mvn -q exec:java -Dexec.args="-l 16 -x"

# Same using long options
mvn -q exec:java -Dexec.args="--length 24 --strong --count 3"
```

### Option B: Run the runnable jar

```bash
# Show help
java -jar target/password-generator-1.0-SNAPSHOT-shaded.jar --help

# Default (one password, default length)
java -jar target/password-generator-1.0-SNAPSHOT-shaded.jar

# Custom length
java -jar target/password-generator-1.0-SNAPSHOT-shaded.jar -l 16

# Multiple passwords + special characters
java -jar target/password-generator-1.0-SNAPSHOT-shaded.jar -l 16 -s -c 5

# Strong mode (must include uppercase + digit + symbol)
java -jar target/password-generator-1.0-SNAPSHOT-shaded.jar -l 16 -x

# Same using long options
java -jar target/password-generator-1.0-SNAPSHOT-shaded.jar --length 24 --strong --count 3
```

## Testing

Run all tests:

```bash
mvn test
```

Run a single test class:

```bash
mvn -Dtest=GeneratorTest test
```

### What is tested

`GeneratorTest` includes:

* **Length tests**

  * correct output length for `generate`, `generateWithSymbols`, `generateStrong`
* **Boundary tests**

  * reject lengths below 8
  * accept length 8
  * reject non-positive lengths
* **Strong mode rule tests**

  * generated password contains at least one uppercase letter, one digit, and one symbol
* **Allowed character tests**

  * `generateWithSymbols` output uses only characters from the configured allowed set

## CLI quick reference

* `-h, --help` — show help
* `-l, --length <N>` — set length (default: 10)
* `-c, --count <N>` — generate N passwords (default: 1)
* `-s, --special` — allow symbols `@#$%!&*`
* `-x, --strong` — strong mode (requires uppercase + digit + symbol; implies `--special`)

