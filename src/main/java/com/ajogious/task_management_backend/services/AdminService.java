package com.ajogious.task_management_backend.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import com.ajogious.task_management_backend.dtos.AdminDashboardStatsDTO;
import com.ajogious.task_management_backend.dtos.UserDTO;
import com.ajogious.task_management_backend.entities.Task;
import com.ajogious.task_management_backend.entities.User;
import com.ajogious.task_management_backend.repositories.AdminRepository;
import com.ajogious.task_management_backend.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AdminService {

        private final UserRepository userRepository;
        private final AdminRepository adminRepository;

        // Fetch admin dashboard statistics
        public AdminDashboardStatsDTO getDashboardStats() {
                try {
                        // Total users
                        long totalUsers = userRepository.count();

                        // Monthly user registrations
                        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).toLocalDate().atStartOfDay();
                        LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusSeconds(1);
                        long totalMonthlyUsers = adminRepository.countUsersRegisteredBetween(startOfMonth, endOfMonth);

                        // Gender-based user counts
                        long totalMaleUsers = adminRepository.countUsersByGender("Male");
                        long totalFemaleUsers = adminRepository.countUsersByGender("Female");

                        // Task-based statistics
                        long totalTasks = adminRepository.count();
                        long completedTasks = adminRepository.countByStatus(Task.Status.COMPLETED);
                        long pendingTasks = adminRepository.countByStatus(Task.Status.PENDING);

                        // Return aggregated statistics
                        return new AdminDashboardStatsDTO(
                                        totalUsers,
                                        totalMonthlyUsers,
                                        totalMaleUsers + totalFemaleUsers,
                                        totalMaleUsers,
                                        totalFemaleUsers,
                                        totalTasks,
                                        completedTasks,
                                        pendingTasks);

                } catch (Exception e) {
                        throw new RuntimeException("Failed to fetch dashboard statistics", e);
                }
        }

        // Get paginated user info with search and role filters
        public Page<UserDTO> getAllUsersInfo(String search, String role, int page, int size) {
                Pageable pageable = PageRequest.of(page, size);

                try {
                        Page<User> userPage = adminRepository.findAllWithFilters(search, role, pageable);
                        return userPage.map(this::mapToUserDTO);

                } catch (Exception e) {
                        throw new RuntimeException("Failed to fetch user information", e);
                }
        }

        // Fetch all users
        public List<UserDTO> getAllUsers() {
                try {
                        return userRepository.findAll().stream()
                                        .map(this::mapToUserDTO)
                                        .collect(Collectors.toList());
                } catch (Exception e) {
                        throw new RuntimeException("Failed to fetch all users", e);
                }
        }

        // Utility method to map User entity to UserDTO
        private UserDTO mapToUserDTO(User user) {
                return new UserDTO(
                                user.getId(),
                                user.getUsername(),
                                user.getEmail(),
                                user.getFullName(),
                                user.getRole().name(),
                                user.getImage(),
                                user.getGender(),
                                user.getAddress(),
                                user.getPhoneNo(),
                                user.isActive(),
                                user.getUpdated(),
                                user.getCreated());
        }
}
