# 🌐 Spring Boot Security Login System

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green?logo=springboot)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange?logo=java)](https://www.oracle.com/java/)
[![MySQL](https://img.shields.io/badge/MySQL-Database-blue?logo=mysql)](https://www.mysql.com/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Author](https://img.shields.io/badge/Author-Shivansh%20Mishra-blueviolet)](https://github.com/shivanshmishra54)

---

This is a **complete, feature-rich user login and registration system** built with **Spring Boot**.  
It demonstrates a **secure, modern, and high-performance** approach to handling user authentication, authorization, and password management — all from scratch.

The app includes **email OTP verification**, **secure password reset**, and a **beautiful modern UI**.

---

## ✨ Features

- 🔐 **Spring Security:** Full authentication and authorization using a database-backed `UserDetailsService`.
- 🧑‍💼 **Role-Based Access:** Separate access levels for `ROLE_USER` and `ROLE_ADMIN`.
- ⚡ **Dynamic Signup:** Single-page registration with live email OTP verification.
- ✉️ **Email OTP Verification:** Multi-threaded, asynchronous email sending for instant verification.
- 🔑 **Forgot Password Flow:** Secure password reset using time-based tokens and email links.
- 💌 **Professional HTML Emails:** Designed with Thymeleaf templates for OTP and reset emails.
- 🎨 **Modern UI/UX:** Frosted glass effect, smooth animations, and a fully responsive design.
- 🚀 **Performance Optimized:** Indexed tables, efficient caching, and tuned database connections.

---

## 🛠 Tech Stack

| Layer | Technology |
|-------|-------------|
| **Backend** | Spring Boot 3 |
| **Security** | Spring Security 6 |
| **Data** | Spring Data JPA (Hibernate) |
| **Database** | MySQL |
| **Frontend** | Thymeleaf |
| **Styling** | CSS3 |
| **Email** | Spring Boot Starter Mail |
| **Build Tool** | Apache Maven |

---

## 📂 Project Structure
| Directory/File | Location | Description |
| :--- | :--- | :--- |
| **`LoginSystemApplication.java`** | `src/main/java/.../loginsystem/` | Main application entry point. |
| **`SecurityConfig.java`** | `.../config/` | Configures **Spring Security** (access control, password encoder). |
| **`AppController.java`** | `.../controller/` | Handles all primary web routes (`/`, `/login`, `/signup`, `/admin`). |
| **`User.java`, `Roles.java`** | `.../entity/` | JPA database models. |
| **`EmailService.java`** | `.../service/` | Business logic for sending emails. |
| **`application.properties`** | `src/main/resources/` | **Configuration file** for database and email settings. |
| **`templates/`** | `src/main/resources/` | Contains all **Thymeleaf HTML views** (`login.html`, `signup.html`, etc.). |
| **`pom.xml`** | Root Directory | Defines all project dependencies (Maven). |
---

## 🚀 How to Run

### 1. Prerequisites
* Java 21 (or as specified in your `pom.xml`)
* Maven 3.x
* A running MySQL server

### 2. Database Setup
1.  Log in to your MySQL server.
2.  Create a new database (the name `loginsystem_db` is used in the properties file).
    ```sql
    CREATE DATABASE loginsystem_db;
    ```
3.  **This is optional but recommended:** If your database schema is out of sync, the easiest fix is to set `spring.jpa.hibernate.ddl-auto=create` in your properties file *for the first run only*. This will build all tables perfectly. **Remember to set it back to `update`** after.

### 3. Configure the Application
Open `src/main/resources/application.properties` and fill in your details.

````markdown
# 🛠️ Login System Project Setup Guide

## 🗄️ Database Configuration

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/loginsystem_db
spring.datasource.user=YOUR_MYSQL_USERNAME
spring.datasource.password=YOUR_MYSQL_PASSWORD
````

-----

## 📧 Email Configuration (for a Gmail Account)

> **⚠️ Important:** You must generate a **16-digit App Password** from your Google account. **Do not use your normal password.**

### Steps for Generating an App Password:

1.  Go to your [Google Account settings](https://myaccount.google.com)
2.  Navigate to **Security** → **2-Step Verification** (it must be **ON**)
3.  Go to **App Passwords**
4.  Generate a new password for:
      * **App:** Mail
      * **Device:** Other (Custom name)
5.  Use that **16-digit password** in the configuration below:

### ⚙️ Mail Properties

```properties
spring.mail.username=YOUR_GMAIL_ADDRESS@gmail.com
spring.mail.password=YOUR_16_DIGIT_APP_PASSWORD
```

-----

## 🔒 Recommended: Use Environment Variables

For better security, **avoid hardcoding passwords** directly in the configuration files.

```properties
spring.datasource.password=${DB_PASSWORD}
spring.mail.password=${EMAIL_PASSWORD}
```

-----

## ⚙️ Run the Application

You can run the application in two primary ways:

### 🧩 A) From IntelliJ IDEA

1.  Open the main application file: **`LoginSystemApplication.java`**
2.  Right-click the `main` method → Select **Run**
3.  The following property will automatically create/update your database tables:
      * `spring.jpa.hibernate.ddl-auto=update`
      * This will generate necessary tables like `user`, `roles`, and `user_roles`.

-----

### 💻 B) From the Command Line (Maven)

```bash
# Build the project and run all tests
mvn clean install

# Run the application
java -jar target/loginsystem-0.0.1-SNAPSHOT.jar
```

Once started, open your browser and visit:
👉 **`http://localhost:8080`**

-----

## 🆓 Free to Use

This project is completely **free to use, modify, and share** for learning or development purposes.

If you find it helpful, please **⭐ star the repository on GitHub\!**

-----

## 👤 Author

**Shivansh Mishra**

  * **GitHub:** [@shivanshmishra54](https://github.com/shivanshmishra54)
  * **LinkedIn:** [shivansh-mishra54](https://www.linkedin.com/in/shivansh-mishra54/)

<!-- end list -->

```
```
