package com.coding.test.service;

import com.coding.test.dto.UserRequest;
import com.coding.test.exception.DateFormatException;
import com.coding.test.exception.ResourceNotFoundException;
import com.coding.test.exception.UpdateNotAllowedException;
import com.coding.test.model.User;
import com.coding.test.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Override
    public User save(UserRequest user) throws DateFormatException {
        User userEntity = new User();
        userEntity.setGender(user.getGender());
        userEntity.setAge(user.getAge());
        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());
        try {
            userEntity.setDob(LocalDate.parse(user.getDob(), DateTimeFormatter.ofPattern("d-MMM-yyyy")));
        }catch(Exception e){
            throw new DateFormatException("Date of birth must be in dd-MMM-yyyy format");
        }
        user.getAddressList().forEach(address -> address.setUser(userEntity));
        userEntity.setAddressList(user.getAddressList());
        return userRepository.save(userEntity);
    }

    @Override
    @Transactional
    public User getUserById(Long id, boolean isAddressNeeded) throws ResourceNotFoundException {
        Optional<User> user = userRepository.findById(id);
        if(user.isPresent()){
            if(isAddressNeeded) {
                return user.get();
            }else{
                User userWithoutAddress= user.get();
                userWithoutAddress.setAddressList(null);
                return userWithoutAddress;
            }
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
        if (!existingUser.getDob().isEqual(LocalDate.parse(user.getDob(), DateTimeFormatter.ofPattern("d-MMM-yyyy")))) {
            throw new UpdateNotAllowedException("Date of birth update not allowed");
        }
        existingUser.setLastName(user.getLastName());
        existingUser.setAge(user.getAge());
        existingUser.setFirstName(user.getFirstName());
        existingUser.setGender(user.getGender());
        return userRepository.save(existingUser);
    }

}
