package com.coding.test.service;

import com.coding.test.dto.UserRequest;
import com.coding.test.exception.DateFormatException;
import com.coding.test.exception.ResourceNotFoundException;
import com.coding.test.exception.UpdateNotAllowedException;
import com.coding.test.model.Address;
import com.coding.test.model.User;
import com.coding.test.repository.AddressRepository;
import com.coding.test.repository.UserRepository;
import junit.framework.TestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest extends TestCase {
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AddressRepository addressRepository;

    @Test
    public void testSave() throws DateFormatException {
        UserRequest user = new UserRequest();
        user.setFirstName("test");
        user.setLastName("user");
        user.setAge(20);
        user.setGender("M");
        user.setDob("12-Apr-2003");
        when(userRepository.save(any())).thenReturn(new User());
        User response = userService.save(user);
        assertNotNull(response);
    }

    @Test
    public void testSaveWhenDateFormatException() {
        UserRequest user = new UserRequest();
        user.setFirstName("test");
        user.setLastName("user");
        user.setAge(20);
        user.setGender("M");
        user.setDob("12-04-2003");
        User response = null;
        try {
            response = userService.save(user);
        } catch (DateFormatException ex) {
            assertNotNull(ex);
            assertEquals("Date of birth must be in dd-MMM-yyyy format", ex.getMessage());
        }
    }

    @Test
    public void testGetUserById() throws ResourceNotFoundException {
        User user = new User();
        user.setFirstName("test");
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        User response = userService.getUserById(1L);
        assertNotNull(response);
        assertEquals("test", response.getFirstName());
    }

    @Test
    public void testGetUserByIdWhenResourceNotFoundException() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());
        User response = null;
        try {
            response = userService.getUserById(1L);
        } catch (ResourceNotFoundException ex) {
            assertNotNull(ex);
            assertEquals("No user found with given id", ex.getMessage());
        }
    }

    @Test
    public void testUpdateUser() throws UpdateNotAllowedException, ResourceNotFoundException {
        UserRequest userRequest = new UserRequest();
        userRequest.setFirstName("test");
        userRequest.setLastName("user");
        userRequest.setAge(20);
        userRequest.setGender("M");
        userRequest.setDob("12-Apr-2003");

        User user = new User();
        user.setFirstName("test");
        user.setLastName("user");
        user.setAge(20);
        user.setGender("M");
        user.setDob(LocalDate.parse("12-Apr-2003", DateTimeFormatter.ofPattern("d-MMM-yyyy")));

        when(userRepository.getById(any())).thenReturn(user);
        when(userRepository.save(any())).thenReturn(user);

        User response = userService.updateUser(1L, userRequest);
        assertNotNull(response);
        assertEquals("test", response.getFirstName());
    }

    @Test
    public void testUpdateUserWhenResourceNotFoundException(){
        UserRequest userRequest = new UserRequest();
        userRequest.setFirstName("test");
        userRequest.setLastName("user");
        userRequest.setAge(20);
        userRequest.setGender("M");
        userRequest.setDob("12-Apr-2003");

        when(userRepository.getById(any())).thenReturn(null);

        User response = null;
        try {
            response = userService.updateUser(1L, userRequest);
        } catch (ResourceNotFoundException ex) {
            assertNotNull(ex);
            assertEquals("No user found with given id", ex.getMessage());
        } catch (UpdateNotAllowedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testUpdateUserWhenUpdateNotAllowedException(){
        UserRequest userRequest = new UserRequest();
        userRequest.setFirstName("test");
        userRequest.setLastName("user");
        userRequest.setAge(20);
        userRequest.setGender("M");
        userRequest.setDob("13-Apr-2003");

        User user = new User();
        user.setFirstName("test");
        user.setLastName("user");
        user.setAge(20);
        user.setGender("M");
        user.setDob(LocalDate.parse("12-Apr-2003", DateTimeFormatter.ofPattern("d-MMM-yyyy")));

        when(userRepository.getById(any())).thenReturn(user);

        User response = null;
        try {
            response = userService.updateUser(1L, userRequest);
        } catch (ResourceNotFoundException ex) {
            throw new RuntimeException(ex);
        } catch (UpdateNotAllowedException ex) {
            assertNotNull(ex);
            assertEquals("Date of birth update not allowed", ex.getMessage());
        }
    }

    @Test
    public void testGetUserWithAddressById() {
        User user = new User();
        user.setFirstName("test");

        List<Address> addresses = new ArrayList<>();
        Address address = new Address();
        addresses.add(address);

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(addressRepository.findByUserId(any())).thenReturn(addresses);
        User response = userService.getUserWithAddressById(1L);
        assertNotNull(response);
        assertEquals("test", response.getFirstName());
        assertEquals(1, response.getAddressList().size());
    }
}