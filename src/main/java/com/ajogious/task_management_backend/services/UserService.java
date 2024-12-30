package com.ajogious.task_management_backend.services;

import com.ajogious.task_management_backend.dtos.UserDTO;
import com.ajogious.task_management_backend.entities.User;
import com.ajogious.task_management_backend.global_exception.ResourceNotFoundException;
import com.ajogious.task_management_backend.repositories.UserRepository;
import com.ajogious.task_management_backend.security.JWTUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTUtil jwtUtil;

    @Value("${file.upload-dir}")
    private String uploadDir;

    // User Registration Method
    public User saveUser(User user, MultipartFile imageFile) throws IOException {
        // Check for duplicate username, email, and phone number
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (userRepository.existsByPhoneNo(user.getPhoneNo())) {
            throw new IllegalArgumentException("Phone number already exists");
        }

        String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        // Save the image to the server
        String userFolder = uploadDir + "/users/" + user.getUsername();
        Path uploadPath = Paths.get(userFolder);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = imageFile.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(imageFile.getInputStream(), filePath);

        user.setImage(filePath.toString());

        return userRepository.save(user);
    }

    // User login
    public String userLogin(User user) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid email or password.");
        }

        // Fetch user details after successful authentication
        User savedUser = userRepository.findUserByEmail(user.getEmail());
        if (savedUser == null) {
            throw new UsernameNotFoundException("User not found.");
        }

        return jwtUtil.generateToken(savedUser.getEmail());
    }

    // Get all users
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserDTO(
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
                        user.getCreated()))
                .collect(Collectors.toList());
    }

    public Page<UserDTO> getAllUsersInfo(String search, String role, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        // Fetch users with search and role filtering
        Page<User> userPage = userRepository.findAllWithFilters(search, role, pageable);

        // Convert to UserDTO
        return userPage.map(user -> new UserDTO(
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
                user.getCreated()));
    }

    public UserDTO getUserDetailsDTO(String email) {
        User user = userRepository.findUserByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found.");
        }
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

    public UserDTO getUserDetailsDTO(Long userId) {
        return userRepository.findById(userId)
                .map(user -> new UserDTO(
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
                        user.getCreated(),
                        user.getUpdated()))
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
    }

    public User updateUser(UserDTO userDTO, MultipartFile imageFile) throws IOException {
        // Check for duplicate username, email, and phone number, excluding the current
        // user
        if (userRepository.existsByUsername(userDTO.getUsername()) &&
                !userRepository.findById(userDTO.getId()).get().getUsername().equals(userDTO.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(userDTO.getEmail()) &&
                !userRepository.findById(userDTO.getId()).get().getEmail().equals(userDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (userRepository.existsByPhoneNo(userDTO.getPhoneNo()) &&
                !userRepository.findById(userDTO.getId()).get().getPhoneNo().equals(userDTO.getPhoneNo())) {
            throw new IllegalArgumentException("Phone number already exists");
        }

        User existingUser = userRepository.findById(userDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        existingUser.setUsername(userDTO.getUsername());
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setFullName(userDTO.getFullName());

        // Handle image update
        if (imageFile != null && !imageFile.isEmpty()) {
            // Save the image to the server
            String userFolder = uploadDir + "/users/" + existingUser.getUsername();
            Path uploadPath = Paths.get(userFolder);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Handle file name conflicts
            String fileName = imageFile.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            existingUser.setImage(filePath.toString());
        }

        existingUser.setGender(userDTO.getGender());
        existingUser.setAddress(userDTO.getAddress());
        existingUser.setPhoneNo(userDTO.getPhoneNo());

        return userRepository.save(existingUser);
    }

    // public User getUserInfo(Long userId) {
    // Optional<User> user = userRepository.findUserWithTasks(userId);
    // return user.orElseThrow(() -> new IllegalArgumentException("User not found
    // with ID: " + userId));
    // }

    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }
        userRepository.deleteById(userId);
    }

    @Transactional
    public void suspendUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }
        userRepository.suspendUserById(userId);
    }

    @Transactional
    public void activateUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }
        userRepository.activateUserById(userId);
    }

}
