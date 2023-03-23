package com.coding.test.service;

import com.coding.test.dto.UserRequest;
import com.coding.test.exception.DateFormatException;
import com.coding.test.exception.ResourceNotFoundException;
import com.coding.test.exception.UpdateNotAllowedException;
import com.coding.test.model.User;

public interface UserService {
    User save(UserRequest user) throws DateFormatException;
    User getUserById(Long id) throws ResourceNotFoundException;
    User updateUser(Long id, UserRequest user) throws ResourceNotFoundException, UpdateNotAllowedException;
    User getUserWithAddressById(Long id);
}
