package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.exception.NotValidException;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.repository.UserRepository;
import com.edu.ulab.app.service.UserService;
import com.edu.ulab.app.validation.PersonValid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository,
                           UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        if (PersonValid.isValidPerson(userDto)){
            Person user = userMapper.userDtoToPerson(userDto);
            log.info("Mapped user: {}", user);
            Person createUser = userRepository.save(user);
            log.info("Saved user: {}", createUser);
            return userMapper.personToUserDto(createUser);
        } else {
            throw new NotValidException("Not valid:  " + userDto);
        }
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        UserDto existUser = getUserById(userDto.getId());

        if (userDto.getFullName() != null) {
            existUser.setFullName(userDto.getFullName());
        }
        if (userDto.getTitle() != null) {
            existUser.setTitle(userDto.getTitle());
        }
        if (userDto.getAge() != 0) {
            existUser.setAge(userDto.getAge());
        }
        if (PersonValid.isValidPerson(userDto)) {
            existUser = userMapper.personToUserDto(userRepository
                            .save(userMapper.userDtoToPerson(existUser)));
        }
        log.info("UPDATE USER: {}", userDto);
        return existUser;
    }

    @Override
    public UserDto getUserById(Long id) {
        Person person = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Can not fount user with ID: " + id));
        log.info("Find person with ID: " + id);
        return userMapper.personToUserDto(person);
    }

    @Override
    public void deleteUserById(Long id) {
        try {
            userRepository.deleteById(id);
            log.info("deleted user with ID: " + id);
        } catch (EmptyResultDataAccessException exc) {
            throw new NotFoundException("User with ID" + id + "not found");
        }
    }
}
