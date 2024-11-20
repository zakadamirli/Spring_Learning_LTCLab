package com.zekademirli.laresmain.service;

import com.zekademirli.laresmain.dto.BookDTO;
import com.zekademirli.laresmain.entities.Book;
import com.zekademirli.laresmain.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private BookService bookService;

    @Test
    public void testGetBookById() {

        Book mockBook = new Book();
        mockBook.setId(1L);
        mockBook.setTitle("Test Title");

        BookDTO mockBookDTO = new BookDTO();
        mockBookDTO.setId(1L);
        mockBookDTO.setTitle("Test Title");

        when(bookRepository.findById(1L)).thenReturn(Optional.of(mockBook));
        when(modelMapper.map(mockBook, BookDTO.class)).thenReturn(mockBookDTO);

        BookDTO result = bookService.getBookById(1L);

        assertEquals(mockBookDTO, result);
        verify(bookRepository, times(1)).findById(1L);
        verify(modelMapper, times(1)).map(mockBook, BookDTO.class);
    }

    @Test
    public void testGetAllBooks() {

        List<Book> bookList = IntStream.range(1, 5)
                .mapToObj(i -> Book.builder()
                        .id((long) i)
                        .title("Title " + i)
                        .author("Author " + i)
                        .build())
                .toList();

        when(bookRepository.findAll()).thenReturn(bookList);

        List<BookDTO> books = IntStream.range(1, 5)
                .mapToObj(i -> BookDTO.builder()
                        .id((long) i)
                        .title("Title " + i)
                        .author("Author " + i)
                        .build())
                .toList();

        for (int i = 0; i < bookList.size(); i++) {
            when(modelMapper.map(bookList.get(i), BookDTO.class)).thenReturn(books.get(i));
        }

        List<BookDTO> result = bookService.getAllBooks();

        assertEquals(4, result.size());
        for (int i = 0; i < result.size(); i++) {
            assertEquals(books.get(i).getId(), result.get(i).getId());
            assertEquals(books.get(i).getTitle(), result.get(i).getTitle());
            assertEquals(books.get(i).getAuthor(), result.get(i).getAuthor());
        }

        verify(bookRepository, times(1)).findAll();
        verify(modelMapper, times(4)).map(any(Book.class), eq(BookDTO.class));
    }

    @Test
    public void testAddBook() {

        BookDTO inputBookDTO = BookDTO.builder()
                .id(1L)
                .title("Test Title")
                .author("Author 1")
                .build();

        Book bookEntity = Book.builder()
                .id(1L)
                .title("Test Title")
                .author("Author 1")
                .build();

        Book savedBook = Book.builder()
                .id(1L)
                .title("Test Title")
                .author("Author 1")
                .build();

        BookDTO outputBookDTO = BookDTO.builder()
                .id(1L)
                .title("Test Title")
                .author("Author 1")
                .build();

        when(modelMapper.map(inputBookDTO, Book.class)).thenReturn(bookEntity);
        when(bookRepository.save(bookEntity)).thenReturn(savedBook);
        when(modelMapper.map(savedBook, BookDTO.class)).thenReturn(outputBookDTO);

        BookDTO result = bookService.addBook(inputBookDTO);

        assertNotNull(result);
        assertEquals(outputBookDTO.getId(), result.getId());
        assertEquals(outputBookDTO.getTitle(), result.getTitle());
        assertEquals(outputBookDTO.getAuthor(), result.getAuthor());

        verify(modelMapper, times(1)).map(inputBookDTO, Book.class);
        verify(bookRepository, times(1)).save(bookEntity);
        verify(modelMapper, times(1)).map(savedBook, BookDTO.class);

    }

    @Test
    public void testUpdateBook() {

        Long bookId = 1L;

        Book existingBook = Book.builder()
                .id(1L)
                .title("Old Title")
                .author("Old Author")
                .build();

        BookDTO inputBookDTO = BookDTO.builder()
                .id(1L)
                .title("New Title")
                .author("New Author")
                .build();

        Book updatedBook = Book.builder()
                .id(1L)
                .title("New Title")
                .author("New Author")
                .build();

        BookDTO updatedBookDTO = BookDTO.builder()
                .id(1L)
                .title("New Title")
                .author("New Author")
                .build();

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));

        doAnswer(ilanQur -> {
            BookDTO source = ilanQur.getArgument(0);
            Book target = ilanQur.getArgument(1);
            target.setTitle(source.getTitle());
            target.setAuthor(source.getAuthor());
            return null;
        }).when(modelMapper).map(inputBookDTO, existingBook);
        when(bookRepository.save(existingBook)).thenReturn(updatedBook);
        when(modelMapper.map(updatedBook, BookDTO.class)).thenReturn(updatedBookDTO);

        BookDTO result = bookService.updateBook(bookId, inputBookDTO);

        assertNotNull(result);
        assertEquals(updatedBookDTO.getId(), result.getId());
        assertEquals(updatedBookDTO.getTitle(), result.getTitle());
        assertEquals(updatedBookDTO.getAuthor(), result.getAuthor());

        verify(bookRepository, times(1)).findById(bookId);
        verify(modelMapper, times(1)).map(inputBookDTO, existingBook);
        verify(bookRepository, times(1)).save(existingBook);
        verify(modelMapper, times(1)).map(updatedBook, BookDTO.class);
    }

    @Test
    public void testDeleteBook() {
        Long bookId = 1L;

        bookService.deleteBook(bookId);
        verify(bookRepository, times(1)).deleteById(bookId);
    }

    @Test
    public void testClearAllBooksCache() {
        //TODO: Add functionality to clear books cache
    }

    @Test
    public void testConvertToDTO() {
        ModelMapper modelMapper = new ModelMapper();

        BookService bookService = new BookService(bookRepository, modelMapper);

        Book book = new Book(1L, "Test Title", "Test Author");

        BookDTO bookDTO = bookService.convertToDTO(book);

        assertNotNull(bookDTO);
        assertEquals(book.getId(), bookDTO.getId());
        assertEquals(book.getTitle(), bookDTO.getTitle());
        assertEquals(book.getAuthor(), bookDTO.getAuthor());
    }

    @Test
    public void testConvertToEntity() {
        ModelMapper modelMapper = new ModelMapper();
        BookService bookService = new BookService(bookRepository, modelMapper);

        BookDTO bookDTO = new BookDTO(1L, "Test Title", "Test Author");
        Book book = bookService.convertToEntity(bookDTO);

        assertNotNull(book);
        assertEquals(book.getId(), bookDTO.getId());
        assertEquals(book.getTitle(), bookDTO.getTitle());
        assertEquals(book.getAuthor(), bookDTO.getAuthor());
    }


}