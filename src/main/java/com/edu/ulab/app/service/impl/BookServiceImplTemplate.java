package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.exception.NotValidException;
import com.edu.ulab.app.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class BookServiceImplTemplate implements BookService {

    private final JdbcTemplate jdbcTemplate;

    private final static String INSERT_SQL = """
                                             INSERT INTO BOOK(TITLE, AUTHOR, PAGE_COUNT, USER_ID) VALUES (?,?,?,?)
                                             """;

    private final static String UPDATE_SQL = """
                                             UPDATE BOOK SET 
                                             TITLE = ?, AUTHOR = ?, PAGE_COUNT = ?, USER_ID = ? 
                                             WHERE ID = ?
                                             """;

    private final static String GET_BY_ID = """
                                            SELECT * FROM BOOK WHERE ID = ?
                                            """;

    private final static String DELETE_SQL = """
                                             DELETE FROM BOOK WHERE ID = ?
                                             """;
    private final static String GET_BOOKS_SQL = """
                                                SELECT * FROM BOOK WHERE USER_ID = ?
                                                """;
    private final static String DELETE_SQL_BY_USER_ID = """
                                                        DELETE FROM BOOK WHERE USER_ID = ?
                                                        """;

    public BookServiceImplTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public BookDto createBook(BookDto bookDto) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                new PreparedStatementCreator() {
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        PreparedStatement ps =
                                connection.prepareStatement(INSERT_SQL, new String[]{"id"});
                        ps.setString(1, bookDto.getTitle());
                        ps.setString(2, bookDto.getAuthor());
                        ps.setLong(3, bookDto.getPageCount());
                        ps.setLong(4, bookDto.getUserId());
                        return ps;
                    }
                },
                keyHolder);

        bookDto.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return bookDto;
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {
        // реализовать недостающие методы
        validateID(bookDto.getId());
        if (getBookById(bookDto.getId()) != null) {
            jdbcTemplate.update(UPDATE_SQL,
                    bookDto.getTitle(),
                    bookDto.getAuthor(),
                    bookDto.getPageCount(),
                    bookDto.getUserId(),
                    bookDto.getId());
            log.info("Book with ID{} update", bookDto.getId());
            return bookDto;
        } else {
            throw new NotFoundException("Book with ID: " + bookDto.getId() + "not found");
        }
    }

    @Override
    public BookDto getBookById(Long id) {
        // реализовать недстающие методы
        validateID(id);
        SqlRowSet bookRow = jdbcTemplate.queryForRowSet(GET_BY_ID, id);
        BookDto bookDto;
        if (bookRow.first()) {
            bookDto = new BookDto(
                    bookRow.getLong("ID"),
                    bookRow.getLong("USER_ID"),
                    bookRow.getString("TITLE"),
                    bookRow.getString("AUTHOR"),
                    bookRow.getInt("PAGE_COUNT")
            );
            log.info("Book with ID{} found", id);
        } else {
            throw new NotFoundException("Book with ID: " + id + "not found");
        }
        return bookDto;
    }

    @Override
    public void deleteBookById(Long id) {
        // реализовать недстающие методы
        validateID(id);
        int numberRowAffected = jdbcTemplate.update(DELETE_SQL, id);
        log.info("Book with ID{} deleted", id);
        if (numberRowAffected == 0) {
            throw new NotFoundException("Book with ID:" + id + "not found");
        }
    }

    @Override
    public List<BookDto> getBookByUserId(Long id) {
        return jdbcTemplate.query(GET_BOOKS_SQL, (rs, rowNum) -> new BookDto(
                        rs.getLong("ID"),
                        rs.getLong("USER_ID"),
                        rs.getString("TITLE"),
                        rs.getString("AUTHOR"),
                        rs.getInt("PAGE_COUNT")), id);
    }

    @Override
    public void deleteBooksByUserId(Long id) {
        validateID(id);
        if (jdbcTemplate.update(DELETE_SQL_BY_USER_ID, id) == 0) {
            throw new NotFoundException("User with ID=" + id + " not found!");
        }
        log.info("delete Books By User Id {}", id);
    }

    private void validateID(Long id) {
        if (id == null) {
            throw new NotValidException("ID can not be null");
        }
    }
}
