package com.coding.test.controller;

import com.coding.test.dto.UserRequest;
import com.coding.test.exception.DateFormatException;
import com.coding.test.exception.ResourceNotFoundException;
import com.coding.test.exception.UpdateNotAllowedException;
import com.coding.test.model.User;
import com.coding.test.service.UserService;
import junit.framework.TestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest extends TestCase {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Test
    public void testCreateUser() throws DateFormatException {
        UserRequest userRequest = new UserRequest();
        User savedUser = new User();
        when(userService.save(userRequest)).thenReturn(savedUser);
        ResponseEntity<User> response = userController.createUser(userRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testCreateUserWhenException(){
        UserRequest userRequest = new UserRequest();
        ResponseEntity<User> response = null;
        try {
            doThrow(new DateFormatException("error while saving user details")).when(userService).save(userRequest);
            response = userController.createUser(userRequest);
        } catch (DateFormatException ex) {
            assertNotNull(ex);
            assertEquals("error while saving user details", ex.getMessage());
        }
    }

    @Test
    public void testUpdateUser() throws UpdateNotAllowedException, ResourceNotFoundException {
        UserRequest userRequest = new UserRequest();
        User savedUser = new User();
        when(userService.updateUser(1L, userRequest)).thenReturn(savedUser);
        ResponseEntity<User> response = userController.updateUser(1L, userRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testUpdateUserWhenUpdateNotAllowedException(){
        UserRequest userRequest = new UserRequest();
        ResponseEntity<User> response = null;
        try {
            doThrow(new UpdateNotAllowedException("error while updating user details")).when(userService).updateUser(1L, userRequest);
            response = userController.updateUser(1L, userRequest);
        } catch (ResourceNotFoundException e) {
            throw new RuntimeException(e);
        } catch (UpdateNotAllowedException ex) {
            assertNotNull(ex);
            assertEquals("error while updating user details", ex.getMessage());
        }
    }

    @Test
    public void testUpdateUserWhenResourceNotFoundException(){
        UserRequest userRequest = new UserRequest();
        ResponseEntity<User> response = null;
        try {
            doThrow(new ResourceNotFoundException("error while updating user details")).when(userService).updateUser(1L, userRequest);
            response = userController.updateUser(1L, userRequest);
        } catch (ResourceNotFoundException ex) {
            assertNotNull(ex);
            assertEquals("error while updating user details", ex.getMessage());
        } catch (UpdateNotAllowedException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Test
    public void testGetUserById() throws ResourceNotFoundException {
        when(userService.getUserById(any())).thenReturn(new User());
        ResponseEntity<User> response = userController.getUserById(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testGetUserByIdWhenResourceNotFoundException(){
        ResponseEntity<User> response = null;
        try {
            doThrow(new ResourceNotFoundException("user not found")).when(userService).getUserById(1L);
            response = userController.getUserById(1L);
        } catch (ResourceNotFoundException ex) {
            assertNotNull(ex);
            assertEquals("user not found", ex.getMessage());
        }
    }

    @Test
    public void testGetUserWithAddressById() throws ResourceNotFoundException {
        when(userService.getUserWithAddressById(any())).thenReturn(new User());
        ResponseEntity<User> response = userController.getUserWithAddressById(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

}