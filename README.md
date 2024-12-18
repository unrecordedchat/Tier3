# Unrecorded - Tier 3 Database Interface

**Unrecorded** is a third tier of Unrecorded.
Its primary role is to securely **store and manage data** that has already been encrypted by the second-tier logic.
This tier acts as the **interface to the database**.
Built on a robust and modern tech stack, it
ensures smooth integration and secure data flows.

## 🛠️ Features

- **Reliable Data Storage**:
    - Manages and stores Tier 2 encrypted data within a PostgreSQL database.
- **API-Driven Design**:
    - Built with **Ktor**, a lightweight Kotlin framework for modular and efficient web server development.
    - Supports REST endpoints and ensures data integrity in transit (with planned TLS 1.3 support).
- **Secure Data Management**:
    - Uses **Hibernate ORM** for seamless interaction with the PostgreSQL database.
    - Database queries are secured with partially verified SSL (full TLS support planned).
- **Scalable Architecture**:
    - Fully equipped to integrate with the other tiers of the Unrecorded project, acting as the backbone of
      database-related operations.

### 🌐 API

- Built with **Ktor**, a lightweight Kotlin framework for creating server applications.
- Database handling in **Java**, powered by **Hibernate ORM**.
- **REST** endpoints and **HTTPS** are under development.

### 🔐 Security

This tier ensures the **secure handling and storage** of encrypted data passed from tier 2. Key practices include:

#### 1. **Data at Rest**

- Tier 3 stores data that has already been encrypted using **ECDSA/EdDSA + ECDH** or **AES-256** in Tier 2.
- This approach isolates encryption responsibilities to tier 2 for simplicity and security.
- No encryption operations are performed at this tier.

#### 2. **Data in Transit**

- Communication between components is planned to be secured with **TLS 1.3**, ensuring data integrity and protection
  during REST API interactions.
- TLS will prevent interception and tampering of data during transport.

#### 3. **Password Security**

- Password authentication is secured using **Argon2** hashing, ensuring strong resistance to brute force attacks.
- Additional measures, such as salting and multiple hashing iterations, are implemented following industry best
  practices.

#### 4. **Database Security**

- PostgreSQL is configured with **SSL** connections to ensure secure queries between the server and client.
- Developers are encouraged to configure **strong SSL certificates** for production environments to eliminate partially
  verified SSL setups.

### 📊 Database

- Database logic powered by **PostgreSQL**, ensuring reliability and scalability.

## 🏗️ Architecture

Unrecorded's third tier represents the **backbone interface for database operations**:

1. **Frontend (1st Tier)**: Client-facing interface for end users, built on **Blazor**.
2. **Backend Logic (2nd Tier)**: Responsible for the core business logic, including **data encryption** and other
   features.
3. **Database Interface (3rd Tier)**: Provides connectivity, persistent data handling, and secure database
   interactions (**this tier**).

## 📋 Tech Stack

| **Component**            | **Technology**            |
|--------------------------|---------------------------|
| **App Framework**        | [Ktor]() (Kotlin)         |
| **ORM**                  | [Hibernate ORM]() (Java)  |
| **Database**             | [PostgreSQL]()            |
| **Security**             | [Argon2]()                |
| **Serialization**        | [KotlinX Serialization]() |
| **Dependency Injection** | [Koin]()                  |
| **Logging**              | [Logback]()               |

## 🚀 How To Run

### Prerequisites

1. Linux based OS (optimized for Linux AWS environments).
2. Install JDK **17**.
3. Install **PostgreSQL 16** (or configure it via the included scripts).
4. Validate that your environment supports running **Bash scripts** for automated setup.

### Run With Automated Scripts

This project includes **fully automated scripts** to simplify the deployment process.
The `run.sh` script sets up the necessary environment, configures SSL,
installs required PostgreSQL extensions, applies database schemas, and performs
all setup steps.

1. Navigate to the `Setup` directory:

``` bash
   cd src/main/resources/com/unrecorded/database/setup
```

2. Make all `.sh` scripts executable:

``` bash
   chmod +x *.sh
```

3. Execute the `run.sh` script:

``` bash
   ./run.sh
```

- **What this does**:
    - Installs and configures **PostgreSQL 16**.
    - Sets up **SSL** for encrypted connections.
    - Installs all required dependencies, including `pg_cron`.
    - Applies the database schema using `setup.sql`.

Once completed, your environment and database will be fully configured and ready to run the application.

### Steps

1. Clone the repository:

``` bash
   git clone https://github.com/your-repository/unrecorded
   cd unrecorded/Database
```

2. Build and run the application:

``` bash
   ./gradlew run
```

🚨 Use the `-Pdevelopment` flag to enable development mode when needed.

1. Check application logs for the assigned server port and API endpoints.

### Manual Build and Execution

For environments where automated scripts cannot be used, follow these steps:

1. **Set Up the Database**:

- Manually install **PostgreSQL 16** and [optional] set up **SSL** configuration.

2. **Delete `pg_cron` extension from `setup.sql`**:

- `pg_cron` must be deleted:

``` postgresql
CREATE EXTENSION IF NOT EXISTS pg_cron;
```

3. **Delete `pg_cron` related logic from `setup.sql`**:

- `notification_housekeeping` and the scheduler must be deleted:

``` postgresql
-- Housekeeping: Delete notifications that are read or related to deleted entities.
CREATE OR REPLACE FUNCTION postgres.unrecorded.notification_housekeeping() RETURNS VOID AS
$$
BEGIN
    -- Delete notifications marked as read or linked to non-existent users.
    DELETE
    FROM postgres.unrecorded.notifications
    WHERE (is_read = TRUE) -- Remove read notifications.
       OR NOT EXISTS (SELECT 1
                      FROM postgres.unrecorded.users
                      WHERE users.user_id = notifications.user_id); -- Remove those without a valid user.
END;
$$ LANGUAGE plpgsql;

-- Schedule the notification cleanup task to run daily at 1 AM.
SELECT cron.schedule('notification_cleanup', '0 1 * * *',
                     'SELECT postgres.unrecorded.notification_housekeeping();'); -- Automate cleanup task.
```

4. **Execute `setup.sql`**:

- Manually set environments within your OS or hardcode the inputs within the `setup.sql`.
- Execute, either from any IDE or from Bash (required if using environments):

``` bash
psql -U postgres -d postgres -v ON_ERROR_STOP=1 -f setup.sql --set unServer="$UnDB_USER" --set unPass="$UnDB_PASSWORD"
```

**Note:** It is highly recommended to use environments,
it may not be now implemented within the code, but it is planned to.
Therefore, we recommend using the provided scripts as they will be updated.

## ⚙️ Configuration

Update the following configuration files based on your environment:

- `build.gradle.kts`: Modify dependencies or versions if required.
- `application.yaml`: Adjust server properties, environment flags, etc.
- `hibernate.cfg.xml`: Configure HibernateORM library.
- `logback.xml`: Personalize system logging.

## 🔍 Complete API Endpoints List

Below is a consolidated list of all REST API endpoints for the `Unrecorded` backend project.
Each module contains a description of the relevant functionality and associated endpoints.

| **Module**        | **Method** | **Endpoint**                                      | **Description**                                                          |
|-------------------|------------|---------------------------------------------------|--------------------------------------------------------------------------|
| **Users**         | POST       | `/api/users`                                      | Create a new user.                                                       |
|                   | GET        | `/api/users/{id}`                                 | Retrieve a user by their unique ID.                                      |
|                   | GET        | `/api/users/username/{username}`                  | Retrieve a user by their username.                                       |
|                   | PATCH      | `/api/users/{id}/username`                        | Update a user's username.                                                |
|                   | PATCH      | `/api/users/{id}/email`                           | Update a user's email address.                                           |
|                   | DELETE     | `/api/users/{id}`                                 | Delete a user by their ID.                                               |
|                   | POST       | `/api/users/verifyPassword`                       | Verify user's credentials (username and password).                       |
| **Friendships**   | POST       | `/api/friendships`                                | Create a new friendship.                                                 |
|                   | GET        | `/api/friendships/{userId1}/{userId2}`            | Retrieve a friendship by two user IDs.                                   |
|                   | GET        | `/api/friendships/{userId}`                       | Retrieve all friendships for a given user.                               |
|                   | PATCH      | `/api/friendships/{userId1}/{userId2}/status`     | Update the status of a friendship.                                       |
|                   | DELETE     | `/api/friendships/{userId1}/{userId2}`            | Delete a friendship by two user IDs.                                     |
| **Groups**        | POST       | `/api/groups`                                     | Create a new group.                                                      |
|                   | GET        | `/api/groups/{id}`                                | Retrieve a group by its unique ID.                                       |
|                   | GET        | `/api/groups/owner/{ownerId}`                     | Retrieve all groups owned by a specific user.                            |
|                   | PATCH      | `/api/groups/{id}/name`                           | Update a group's name.                                                   |
|                   | PATCH      | `/api/groups/{id}/owner`                          | Transfer ownership of a group to a new user.                             |
|                   | DELETE     | `/api/groups/{id}`                                | Soft delete a group (mark it as inactive).                               |
| **Group Members** | POST       | `/api/group-members`                              | Add a new member to a group.                                             |
|                   | GET        | `/api/group-members/group/{groupId}`              | List all members of a specific group.                                    |
|                   | GET        | `/api/group-members/user/{userId}`                | List all groups a user belongs to.                                       |
|                   | PATCH      | `/api/group-members/{groupId}/user/{userId}/role` | Update a member's role in a group.                                       |
|                   | DELETE     | `/api/group-members/{groupId}/user/{userId}`      | Remove a user from a group.                                              |
| **Messages**      | POST       | `/api/messages`                                   | Create a new message (direct or group).                                  |
|                   | GET        | `/api/messages/{id}`                              | Retrieve a message by its unique ID.                                     |
|                   | GET        | `/api/messages/users/{senderId}/{recipientId}`    | List direct messages exchanged between two users.                        |
|                   | GET        | `/api/messages/groups/{groupId}`                  | List all messages sent to a specific group.                              |
|                   | PATCH      | `/api/messages/{id}`                              | Update the content of an existing message.                               |
|                   | DELETE     | `/api/messages/{id}`                              | Permanently delete a message.                                            |
|                   | POST       | `/api/messages/{id}/softDelete`                   | Mark a message as soft-deleted.                                          |
| **Reactions**     | POST       | `/api/reactions`                                  | Create a new reaction to a message.                                      |
|                   | GET        | `/api/reactions/{messageId}`                      | Retrieve all reactions for a given message.                              |
|                   | DELETE     | `/api/reactions`                                  | Remove a specific reaction (requires userId, messageId, and emoji keys). |
| **Sessions**      | POST       | `/api/sessions`                                   | Create a new session.                                                    |
|                   | GET        | `/api/sessions/{id}`                              | Retrieve a session by its unique ID.                                     |
|                   | GET        | `/api/sessions/user/{userId}`                     | Retrieve all sessions for a specific user.                               |
|                   | GET        | `/api/sessions/token/{token}`                     | Retrieve a session by its token value.                                   |
|                   | DELETE     | `/api/sessions/{id}`                              | Delete a session by its unique ID.                                       |
|                   | DELETE     | `/api/sessions/expired`                           | Delete all expired sessions to maintain performance.                     |
| **Notifications** | POST       | `/api/notifications`                              | Create a new notification.                                               |
|                   | GET        | `/api/notifications/{id}`                         | Retrieve a notification by its ID.                                       |
|                   | GET        | `/api/notifications/user/{userId}`                | Retrieve all notifications for a specific user.                          |
|                   | GET        | `/api/notifications/user/{userId}/unread`         | Retrieve all unread notifications for a specific user.                   |
|                   | PATCH      | `/api/notifications/{id}/readStatus`              | Update the read/unread status of a notification.                         |
|                   | DELETE     | `/api/notifications/{id}`                         | Delete a specific notification by its ID.                                |
|                   | DELETE     | `/api/notifications/user/{userId}`                | Delete all notifications for a specific user.                            |

---

### 📘 Endpoint Details and Features

- **Core Functionality**: Each module represents distinct functionality essential to the project, including user
  management, group management, message reactions, session handling, and notification tracking.
- **RESTful Design**: All endpoints follow RESTful principles ensuring predictable interactions and modular scalability.
- **Security Recommendations**: Authentication mechanisms like **JWT tokens** should be used for sensitive endpoints.  
  Hashing (e.g., for passwords) must comply with best practices such as **bcrypt** or **Argon2** plus consider the
  peppering.  
  Encryption should be done using either AES-256 or ECDSA/EdDSA + ECDH.

## 📂 Directory Structure

``` 
Database/
├── src/main/                         # Source code for this tier
│      │
│      └── resources/
│              ├── application.yaml   # App-level configurations
│              ├── hibernate.cfg.xml  # HibernateORM configurations
│              └── logback.xml        # Logging configurations
└── build.gradle.kts                  # Build configuration
```

🚨 **Design Note**:  
By isolating encryption responsibilities to Tier 2,
the system minimizes the risk of security vulnerabilities and unauthorized decryption.
Tier 3 ensures secure storage and retrieval of already encrypted data,
allowing for a modular, scalable, and secure architecture.

## 📜 License

This project is developed as part of the **VIA University College** educational program.
**2024 - Sergiu Chirap**.
All rights reserved.
Unauthorized reproduction or use is prohibited.