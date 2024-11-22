package com.zekademirli.laresmain.service;

import com.zekademirli.laresmain.dto.BookDTO;
import com.zekademirli.laresmain.entities.Book;
import com.zekademirli.laresmain.repository.BookRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final ModelMapper modelMapper;


    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
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
    public BookDTO updateBook(Long id, BookDTO bookDTO) {    //BookDTO == inputBookDTO
        Book book = bookRepository.findById(id).orElseThrow(); //existing
        modelMapper.map(bookDTO, book); //map
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

    public BookDTO convertToDTO(Book book) {
        return modelMapper.map(book, BookDTO.class);
    }

    public Book convertToEntity(BookDTO bookDTO) {
        return modelMapper.map(bookDTO, Book.class);
    }
}
