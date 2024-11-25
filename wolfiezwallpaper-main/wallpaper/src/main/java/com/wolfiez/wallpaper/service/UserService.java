package com.wolfiez.wallpaper.service;

import com.wolfiez.wallpaper.DTO.LoginDTO;
import com.wolfiez.wallpaper.DTO.UserDto;
import com.wolfiez.wallpaper.DTO.UserRegistrationDTO;
import com.wolfiez.wallpaper.entity.User;
import com.wolfiez.wallpaper.exception.AuthenticationException;
import com.wolfiez.wallpaper.exception.DuplicateResourceException;
import com.wolfiez.wallpaper.exception.UserNotFoundException;
import com.wolfiez.wallpaper.repository.RoleRepository;
import com.wolfiez.wallpaper.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@Service
@Transactional
public class UserService {
    @Autowired
    private ImageService imageService;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;


    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }
    public User updateProfileImage(Long userId, MultipartFile imageFile) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String encodedImage = imageService.processImageForStorage(imageFile);
        user.setProfileImage(encodedImage);

        return userRepository.save(user);
    }

    public byte[] getProfileImage(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getProfileImage() == null) {
            return null;
        }

        return imageService.decodeImage(user.getProfileImage());
    }
    public void removeProfileImage(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setProfileImage(null);
        userRepository.save(user);
    }
    public User authenticateUser(LoginDTO loginDTO) {
        User user = userRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new AuthenticationException("Invalid email or password"));

        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new AuthenticationException("Invalid email or password");
        }

        if (!user.isActive()) {
            throw new AuthenticationException("Account is disabled");
        }

        return user;
    }

    public User registerUser(UserRegistrationDTO registrationDTO) {
        if (userRepository.findByEmail(registrationDTO.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setName(registrationDTO.getName());
        user.setEmail(registrationDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));

        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    public User updateUser(Long userId, UserDto userDto, MultipartFile profileImage, String newPassword) {
        User user = getUserById(userId);

        // Validate email uniqueness
        if (!user.getEmail().equals(userDto.getEmail()) && userRepository.existsByEmail(userDto.getEmail())) {
            throw new DuplicateResourceException("Email already registered: " + userDto.getEmail());
        }

        // Update basic user info
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());

        // Handle profile image update
        if (profileImage != null && !profileImage.isEmpty()) {
            try {
                String encodedImage = imageService.processImageForStorage(profileImage);
                user.setProfileImage(encodedImage);
            } catch (IOException e) {
                throw new RuntimeException("Failed to process profile image", e);
            }
        }

        // Handle password update
        if (newPassword != null && !newPassword.isEmpty()) {
            user.setPassword(passwordEncoder.encode(newPassword));
        }

        return userRepository.save(user);
    }
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }

    public List<User> searchUsers(String keyword) {
        return userRepository.searchUsers(keyword);
    }

    public List<User> getUsersByRole(String roleName) {
        return userRepository.findByRoleName(roleName);
    }
}