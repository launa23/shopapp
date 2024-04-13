package com.project.shopapp.services;

import com.project.shopapp.dtos.UserDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.User;
import com.project.shopapp.responses.UserResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface IUserService {
    User createUser(UserDTO userDTO) throws Exception;

    String login(String phoneNumber, String password) throws Exception;

    String loginAdmin(String phoneNumber, String password) throws Exception;
    UserResponse getCurrent(HttpServletRequest request) throws Exception;
}
