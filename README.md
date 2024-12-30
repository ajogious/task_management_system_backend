# Task Management System

## Overview
The Task Management System is a web application designed to help users efficiently manage and organize their tasks. 
The system supports user authentication, role-based access control, task tracking, and provides a dashboard for statistical insights into tasks.

## Features
- **User Management**:
  - Role-based access control (Admin, User).
  - Account activation, suspension, and deletion.
  - User statistics (e.g., total tasks, completed tasks).
- **Task Management**:
  - Create, update, and delete tasks.
  - View task status (pending, completed).
  - Assign tasks to users.
- **Dashboard**:
  - View summary statistics (e.g., total tasks, completed tasks).
  - Filter tasks by user or status.
- **Search and Filters**:
  - Search users by email or username.
  - Pagination and configurable rows per page.

## Technologies Used
### Frontend
- **React** (JavaScript library for building user interfaces)
- **Axios** (HTTP client for API requests)
- **Bootstrap** (CSS framework for responsive design)

### Backend
- **Spring Boot** (Java framework for building RESTful APIs)
- **JPA/Hibernate** (Object-relational mapping)
- **MySQL** (Database for data storage)

### Security
- **Spring Security** (User authentication and authorization)
- **JWT** (JSON Web Tokens for secure user sessions)

## Installation

### Prerequisites
1. **Backend**:
   - Ensure Java 17+ is installed.
   - Set up a MySQL database.
2. **Frontend**:
   - Ensure Node.js and npm are installed.

### Backend Setup
1. Clone the repository:
   ```bash
   git clone https://github.com/ajogious/task_management_system_backend.git
   ```
2. Navigate to the project directory:
   ```bash
   cd task_management_system_backend
   ```
3. Update `application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/task_management
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```
4. Build and run the project:
   ```bash
   ./mvnw spring-boot:run
   ```

### Frontend Setup
1. Clone the repository:
   ```bash
   git clone https://github.com/ajogious/task_management_system_frontend.git
   ```
2. Navigate to the project directory:
   ```bash
   cd task_management_system_frontend
   ```
3. Install dependencies:
   ```bash
   npm install
   ```
4. Start the development server:
   ```bash
   npm start
   ```

## Usage
1. Open the application in your browser:
   - Frontend: `http://localhost:5173`
   - Backend API: `http://localhost:8080`
2. Register or log in to access the dashboard.
3. Use the admin panel for user management.
4. Create, assign, and track tasks.

## API Endpoints

### User Management
- `GET /api/auth/users` - Fetch users with pagination and search.
- `PUT /api/auth/{userId}/suspend` - Suspend a user.
- `PUT /api/auth/{userId}/activate` - Activate a user.
- `DELETE /api/auth/user/{userId}` - Delete a user.

### Task Management
- `GET /api/tasks` - Fetch all tasks.
- `POST /api/tasks` - Create a new task.
- `PUT /api/tasks/{taskId}` - Update a task.
- `DELETE /api/tasks/{taskId}` - Delete a task.

### Dashboard
- `GET /api/dashboard/stats/{userId}` - Fetch task statistics for a user.

## Contributing
1. Fork the repository.
2. Create a new branch for your feature:
   ```bash
   git checkout -b feature-name
   ```
3. Commit your changes:
   ```bash
   git commit -m "Add feature"
   ```
4. Push to your branch:
   ```bash
   git push origin feature-name
   ```
5. Submit a pull request.

## License
This project belong to me. You can use it in your resume and your other activities but don't 
claim as yours.

## Contact
For questions or support, please contact:
- **Name**: Abdulmumuni Ajoge
- **Email**: ajogious@gmail.com
- **GitHub**: https://github.com/ajogious

