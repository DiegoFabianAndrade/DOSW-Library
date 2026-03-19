package edu.eci.dosw.tdd.controller;

import edu.eci.dosw.tdd.controller.dto.BookDTO;
import edu.eci.dosw.tdd.controller.mapper.BookMapper;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.service.BookService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    public BookDTO addBook(@RequestBody BookDTO bookDTO) {
        Book created = bookService.addBook(BookMapper.toModel(bookDTO), bookDTO.getQuantity());
        return BookMapper.toDTO(created, bookService.getAvailableCopies(created.getId()));
    }

    @GetMapping
    public List<BookDTO> getAllBooks() {
        return bookService.getAllBooks().stream()
                .map(book -> BookMapper.toDTO(book, bookService.getAvailableCopies(book.getId())))
                .toList();
    }

    @GetMapping("/{id}")
    public BookDTO getBookById(@PathVariable Integer id) {
        Book book = bookService.getBookById(id);
        return BookMapper.toDTO(book, bookService.getAvailableCopies(id));
    }

    @PatchMapping("/{id}/availability")
    public BookDTO updateAvailability(@PathVariable Integer id, @RequestParam boolean available) {
        Book updated = bookService.updateAvailability(id, available);
        return BookMapper.toDTO(updated, bookService.getAvailableCopies(id));
    }
}
