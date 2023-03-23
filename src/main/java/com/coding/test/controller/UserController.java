package com.coding.test.controller;

import com.coding.test.dto.UserRequest;
import com.coding.test.exception.DateFormatException;
import com.coding.test.exception.ResourceNotFoundException;
import com.coding.test.exception.UpdateNotAllowedException;
import com.coding.test.model.User;
import com.coding.test.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody @Valid UserRequest user) throws DateFormatException {
        User savedUser = userService.save(user);
        return new ResponseEntity<>(savedUser,HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody @Valid UserRequest user) throws ResourceNotFoundException, UpdateNotAllowedException {
        User savedUser = userService.updateUser(id,user);
        return new ResponseEntity<>(savedUser,HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) throws ResourceNotFoundException {
        User user = userService.getUserById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/{id}/getAddress")
    public ResponseEntity<User> getUserWithAddressById(@PathVariable Long id) throws ResourceNotFoundException {
        User user = userService.getUserWithAddressById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }


    // exception handler for validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    @ExceptionHandler({ResourceNotFoundException.class, UpdateNotAllowedException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleExceptions(Exception ex) {
        return ex.getMessage();
    }

    @ExceptionHandler({DateFormatException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleDateExceptions(DateFormatException ex) {
        return ex.getMessage();
    }


}
