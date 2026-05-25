package com.albabot.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.albabot.dao.UserDao;
import com.albabot.model.User;

@Service
public class UserService {
	private final UserDao userDao;
	private final PasswordEncoder passwordEncoder;
	
	public UserService(UserDao userDao, PasswordEncoder passwordEncoder) {
		this.userDao = userDao;
		this.passwordEncoder = passwordEncoder;
	}
	
	public User login(String email, String password) {
		User user = userDao.findByEmail(email);
		
		if (user == null) {
			return null;
		}
		
		if(!passwordEncoder.matches(password, user.getPasswordHash())) return null;
		
		return user;
	}
}
