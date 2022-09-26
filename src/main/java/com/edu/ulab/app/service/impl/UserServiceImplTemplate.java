package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.exception.NotValidException;
import com.edu.ulab.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.util.Objects;

@Slf4j
@Service
public class UserServiceImplTemplate implements UserService {
    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT_SQL = """
                                             INSERT INTO PERSON(FULL_NAME, TITLE, AGE) VALUES (?,?,?)
                                             """;
    private static final String UPDATE_SQL = """
                                             UPDATE PERSON SET 
                                             FULL_NAME = ?, TITLE = ?, AGE = ? 
                                             WHERE ID = ?
                                             """;
    private final static String GET_BY_ID = """
                                            SELECT * FROM PERSON WHERE ID = ?
                                            """;
    private final static String DELETE_SQL = """
                                             DELETE FROM PERSON WHERE ID = ?
                                             """;


    public UserServiceImplTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(INSERT_SQL, new String[]{"id"});
                    ps.setString(1, userDto.getFullName());
                    ps.setString(2, userDto.getTitle());
                    ps.setLong(3, userDto.getAge());
                    return ps;
                }, keyHolder);

        userDto.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return userDto;
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        // реализовать недстающие методы
        validateID(userDto.getId());
        if (getUserById(userDto.getId()) != null) {
            jdbcTemplate.update(UPDATE_SQL,
                    userDto.getFullName(),
                    userDto.getTitle(),
                    userDto.getAge(),
                    userDto.getId());
            log.info("User with ID{} update", userDto.getId());
            return userDto;
        } else {
            throw new NotFoundException("User with ID: " + userDto.getId() + "not found");
        }
    }

    @Override
    public UserDto getUserById(Long id) {
        // реализовать недстающие методы
        validateID(id);
        SqlRowSet personRow = jdbcTemplate.queryForRowSet(GET_BY_ID, id);
        UserDto userDto;
        if (personRow.first()) {
            userDto = new UserDto(
                    personRow.getLong("ID"),
                    personRow.getString("FULL_NAME"),
                    personRow.getString("TITLE"),
                    personRow.getInt("AGE")
            );
            log.info("Person with ID{} found", id);
        } else {
            throw new NotFoundException("Person with ID: " + id + "not found");
        }
        return userDto;
    }

    @Override
    public void deleteUserById(Long id) {
        // реализовать недстающие методы
        validateID(id);
        int numberRowAffected = jdbcTemplate.update(DELETE_SQL, id);
        log.info("Book with ID{} deleted", id);
        if (numberRowAffected == 0) {
            throw new NotFoundException("Person with ID:" + id + "not found");
        }
    }

    private void validateID(Long id) {
        if (id == null) {
            throw new NotValidException("ID can not be null");
        }
    }
}
