package com.zekademirli.laresmain.service;

import com.zekademirli.laresmain.config.ModelMapperConfig;
import com.zekademirli.laresmain.dto.BookDTO;
import com.zekademirli.laresmain.entities.Book;
import com.zekademirli.laresmain.repository.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookService {

    private final BookRepository bookRepository;
    private final ModelMapper modelMapper;

    public BookService(BookRepository bookRepository, ModelMapperConfig modelMapperConfig) {
        this.bookRepository = bookRepository;
        this.modelMapper = modelMapperConfig.modelMapper();
    }

    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "book", key = "#id")
    public BookDTO getBookById(Long id) {
        log.info("called getBookById");
        Book book = bookRepository.findById(id).orElse(null);
        return convertToDTO(book);
    }

    @CachePut(value = "book", key = "#result.id")
    public BookDTO addBook(BookDTO bookDTO) {
        Book book = convertToEntity(bookDTO);
        Book savedBook = bookRepository.save(book);
        return convertToDTO(savedBook);
    }

    @CachePut(value = "book", key = "#id")
    public BookDTO updateBook(Long id, BookDTO bookDTO) {
        Book book = bookRepository.findById(id).orElseThrow();
        modelMapper.map(bookDTO, book);
        Book savedBook = bookRepository.save(book);
        return convertToDTO(savedBook);
    }

    @CacheEvict(value = "book", key = "#id")
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    @CacheEvict(value = "book", allEntries = true)
    public void clearAllBooksCache() {

    }

    private BookDTO convertToDTO(Book book) {
        return modelMapper.map(book, BookDTO.class);
    }

    private Book convertToEntity(BookDTO bookDTO) {
        return modelMapper.map(bookDTO, Book.class);
    }
}
