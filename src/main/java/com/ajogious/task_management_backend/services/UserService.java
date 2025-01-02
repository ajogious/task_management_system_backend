package com.ajogious.task_management_backend.services;

import com.ajogious.task_management_backend.dtos.UserDTO;
import com.ajogious.task_management_backend.entities.User;
import com.ajogious.task_management_backend.global_exception.ResourceNotFoundException;
import com.ajogious.task_management_backend.repositories.AuthRepository;
import com.ajogious.task_management_backend.repositories.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final AuthRepository authRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
    }

    private void validateUserDetails(UserDTO userDTO) {
        userRepository.findById(userDTO.getId()).ifPresent(existingUser -> {
            if (!existingUser.getUsername().equals(userDTO.getUsername()) &&
                    authRepository.existsByUsername(userDTO.getUsername())) {
                throw new IllegalArgumentException("Username already exists");
            }
            if (!existingUser.getEmail().equals(userDTO.getEmail()) &&
                    authRepository.existsByEmail(userDTO.getEmail())) {
                throw new IllegalArgumentException("Email already exists");
            }
            if (!existingUser.getPhoneNo().equals(userDTO.getPhoneNo()) &&
                    authRepository.existsByPhoneNo(userDTO.getPhoneNo())) {
                throw new IllegalArgumentException("Phone number already exists");
            }
        });
    }

    private String handleFileUpload(MultipartFile imageFile, String username) throws IOException {
        if (imageFile == null || imageFile.isEmpty())
            return null;

        String userFolder = uploadDir + "/users/" + username;
        Path uploadPath = Paths.get(userFolder);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = imageFile.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return filePath.toString();
    }

    public UserDTO getUserDetailsDTO(Long userId) {
        User user = findUserById(userId);
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
                user.getCreated(),
                user.getUpdated());
    }

    public User updateUser(UserDTO userDTO, MultipartFile imageFile) throws IOException {
        validateUserDetails(userDTO);
        User existingUser = findUserById(userDTO.getId());

        existingUser.setUsername(userDTO.getUsername());
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setFullName(userDTO.getFullName());
        existingUser.setGender(userDTO.getGender());
        existingUser.setAddress(userDTO.getAddress());
        existingUser.setPhoneNo(userDTO.getPhoneNo());

        String imagePath = handleFileUpload(imageFile, existingUser.getUsername());
        if (imagePath != null) {
            existingUser.setImage(imagePath);
        }

        return userRepository.save(existingUser);
    }

    public void deleteUser(Long userId) {
        User user = findUserById(userId);

        String userFolder = uploadDir + "/users/" + user.getUsername();
        Path userFolderPath = Paths.get(userFolder);

        try {
            if (Files.exists(userFolderPath)) {
                Files.walk(userFolderPath)
                        .sorted(Comparator.reverseOrder())
                        .forEach(path -> {
                            try {
                                Files.delete(path);
                            } catch (IOException e) {
                                throw new RuntimeException("Failed to delete file: " + path, e);
                            }
                        });
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete user folder: " + userFolder, e);
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
