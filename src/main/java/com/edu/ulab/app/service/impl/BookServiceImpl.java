package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.exception.NotValidException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.repository.BookRepository;
import com.edu.ulab.app.service.BookService;
import com.edu.ulab.app.validation.BookValid;
import com.edu.ulab.app.validation.PersonValid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    public BookServiceImpl(BookRepository bookRepository,
                           BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    @Override
    public BookDto createBook(BookDto bookDto) {
            Book book = bookMapper.bookDtoToBook(bookDto);
            log.info("Mapped book: {}", book);
            Book createBook = bookRepository.save(book);
            log.info("createBook book: {}", createBook);
            return bookMapper.bookToBookDto(createBook);
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {
        BookDto existBook = getBookById(bookDto.getId());

        if (bookDto.getTitle() != null) {
            existBook.setTitle(bookDto.getTitle());
        }

        if (bookDto.getAuthor() != null) {
            existBook.setAuthor(bookDto.getAuthor());
        }
        if (bookDto.getPageCount() != 0) {
            existBook.setPageCount(bookDto.getPageCount());
        }
        if (BookValid.isValidBook(bookDto)) {
            existBook = bookMapper.bookToBookDto(bookRepository
                            .save(bookMapper.bookDtoToBook(existBook)));
            log.info("Update book: {} ", bookDto);
        }
        return existBook;
    }

    @Override
    public BookDto getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Can not fount book with ID: " + id));
        log.info("Find book with ID: " + id);
        return bookMapper.bookToBookDto(book);
    }

    @Override
    public void deleteBookById(Long id) {
        try {
            bookRepository.deleteById(id);
            log.info("deleted book with ID: " + id);
        } catch (EmptyResultDataAccessException exc) {
            throw new NotFoundException("BOOK with ID" + id + "not found");
        }
    }

    @Override
    public List<BookDto> getBookByUserId(Long id) {
        log.info("Get BOOKS by user ID{}", id);
        return bookRepository
                .findAllByUserId(id)
                .stream()
                .map(bookMapper::bookToBookDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void deleteBooksByUserId(Long id) {
        bookRepository.deleteByUserId(id);
    }
}
