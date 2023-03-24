package com.coding.test.service;

import com.coding.test.dto.UserRequest;
import com.coding.test.exception.DateFormatException;
import com.coding.test.exception.ResourceNotFoundException;
import com.coding.test.exception.UpdateNotAllowedException;
import com.coding.test.model.Address;
import com.coding.test.model.User;
import com.coding.test.repository.AddressRepository;
import com.coding.test.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.BindingResultUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Component
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;
    @Override
    public User save(UserRequest user) throws DateFormatException {
        User userEntity = new User();
        userEntity.setGender(user.getGender());
        userEntity.setAge(user.getAge());
        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());
        try {
            userEntity.setDob(LocalDate.parse(user.getDob(), DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        }catch(Exception e){
            throw new DateFormatException("Date of birth must be in dd-MM-yyyy format");
        }
        userEntity.setAddressList(user.getAddressList());
        return userRepository.save(userEntity);
    }

    @Override
    public User getUserById(Long id) throws ResourceNotFoundException {
        Optional<User> user = userRepository.findById(id);
        if(user.isPresent()){
            return user.get();
        }else{
            throw new ResourceNotFoundException("No user found with given id");
        }
    }

    @Override
    public User updateUser(Long id, UserRequest user) throws ResourceNotFoundException, UpdateNotAllowedException {
        User existingUser = userRepository.getById(id);
        if(existingUser==null){
            throw new ResourceNotFoundException("No user found with given id");
        }
        if (!existingUser.getDob().isEqual(LocalDate.parse(user.getDob(), DateTimeFormatter.ofPattern("dd-MM-yyyy")))) {
            throw new UpdateNotAllowedException("Date of birth update not allowed");
        }
        existingUser.setLastName(user.getLastName());
        existingUser.setAge(user.getAge());
        existingUser.setFirstName(user.getFirstName());
        existingUser.setGender(user.getGender());
        return userRepository.save(existingUser);
    }

    @Override
    @Transactional
    public User getUserWithAddressById(Long id) {
       User user = userRepository.findById(id).get();
       List<Address> result = addressRepository.findByUserId(id);
       user.setAddressList(result);
       return user;
    }
}
