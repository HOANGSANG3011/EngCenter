package com.hutech.TrungTamTiengAnh.service;

import com.hutech.TrungTamTiengAnh.entity.User;
import com.hutech.TrungTamTiengAnh.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String register(User user) {

        if (userRepository.findByUsername(user.getUsername()) != null) {
            return "Username already exists!";
        }

        if (userRepository.findByEmail(user.getEmail()) != null) {
            return "Email already exists!";
        }

        userRepository.save(user);
        return "SUCCESS";
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User login(String username, String password) {

        User user = userRepository.findByUsername(username);
        
        // NẾU TÌM THEO USERNAME KHÔNG THẤY THÌ TÌM THỬ BẰNG EMAIL
        if (user == null) {
            user = userRepository.findByEmail(username);
        }

        if (user != null && user.getPassword().equals(password)) {
            return user;
        }

        return null;
    }
}