# DBFX Framework — JavaFX Database Utility

DBFX is a lightweight, thread-safe utility for managing **JDBC connections and queries** in JavaFX applications using **HikariCP** and **JavaFX Task** for async operations.

---

## 🚀 Features

* Simple JDBC abstraction for JavaFX apps
* Built-in async query support with `Task`
* HikariCP connection pooling
* Works with any JDBC-compatible database (PostgreSQL by default)

---

## 📦 Installation

### Add via [JitPack](https://jitpack.io)

> Add this to your `pom.xml`: 

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.WaqarAhmad321</groupId>
        <artifactId>dbfx-framework</artifactId>
        <version>Tag</version>
    </dependency>
</dependencies>
```

### Option 2: Use as a Local JAR

1. Clone this repo.
2. Run `mvn package`
3. Copy the JAR from `target/dbfx-framework-1.0-SNAPSHOT.jar`
4. Add it to your JavaFX project classpath

---

## 🛠 JavaFX Project Setup (Sample `pom.xml`)

Make sure your JavaFX app includes:

```xml
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-controls</artifactId>
    <version>17.0.10</version>
    <classifier>win</classifier> <!-- or 'mac', 'linux' -->
</dependency>
```

---

## 💡 Usage

### 1. Initialize the Database

Call this **once** (e.g., in your `start()` method):

```java
DBConnectionPool.initializeDB(
    "jdbc:postgresql://localhost:5432/mydb",
    "postgres",
    "password"
);
```

### 2. Execute a Query (Synchronous)

```java
String sql = "SELECT name FROM users WHERE id = ?";
String name = DBUtils.executeQuery(sql, stmt -> {
    stmt.setInt(1, 1);
}, rs -> {
    return rs.next() ? rs.getString("name") : null;
});
System.out.println(name);
```

### 3. Execute an Update (Synchronous)

```java
boolean success = DBUtils.executeUpdate(
    "INSERT INTO users (name) VALUES (?)",
    stmt -> stmt.setString(1, "Alice")
);
```

### 4. Execute a Query Asynchronously (JavaFX Thread-Safe)

```java
DBUtils.executeQueryAsync(
    "SELECT name FROM users WHERE id = ?",
    stmt -> stmt.setInt(1, 1),
    rs -> rs.next() ? rs.getString("name") : null,
    result -> {
        System.out.println("Got: " + result); // runs on UI thread
    },
    error -> {
        error.printStackTrace(); // error handling
    }
);
```

### 5. Execute an Update Asynchronously

```java
DBUtils.executeUpdateAsync(
    "DELETE FROM users WHERE id = ?",
    stmt -> stmt.setInt(1, 2),
    success -> {
        if (success) System.out.println("User deleted");
    },
    error -> error.printStackTrace()
);
```

### 6. Close Connection Pool on App Exit

```java
@Override
public void stop() {
    DBConnectionPool.closeConnection();
}
```

---

## 📁 Project Structure

```
src/
└── com.dbfx
    ├── database
    │   ├── DBConnectionPool.java
    │   └── DBUtils.java
```

---

## 🤝 Contributing

Pull requests welcome! Feel free to submit improvements, bugfixes, or new helper methods.
