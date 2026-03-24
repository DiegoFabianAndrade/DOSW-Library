package edu.eci.dosw.tdd.controller;

import edu.eci.dosw.tdd.controller.dto.BookDTO;
import edu.eci.dosw.tdd.controller.dto.ErrorResponse;
import edu.eci.dosw.tdd.controller.mapper.BookMapper;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Libros", description = "Alta, consulta y disponibilidad de libros")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    @Operation(summary = "Agregar libro", description = "Registra un libro con la cantidad de ejemplares inicial.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Libro creado",
                    content = @Content(schema = @Schema(implementation = BookDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public BookDTO addBook(@RequestBody BookDTO bookDTO) {
        Book created = bookService.addBook(BookMapper.toModel(bookDTO), bookDTO.getQuantity());
        return BookMapper.toDTO(created, bookService.getAvailableCopies(created.getId()));
    }

    @GetMapping
    @Operation(summary = "Listar libros", description = "Devuelve todos los libros con stock disponible.")
    @ApiResponse(responseCode = "200", description = "Lista obtenida",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = BookDTO.class))))
    public List<BookDTO> getAllBooks() {
        return bookService.getAllBooks().stream()
                .map(book -> BookMapper.toDTO(book, bookService.getAvailableCopies(book.getId())))
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener libro por id", description = "Busca un libro por su codigo de identificacion.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Libro encontrado",
                    content = @Content(schema = @Schema(implementation = BookDTO.class))),
            @ApiResponse(responseCode = "404", description = "Libro no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public BookDTO getBookById(
            @Parameter(description = "Identificador del libro", example = "1")
            @PathVariable Integer id) {
        Book book = bookService.getBookById(id);
        return BookMapper.toDTO(book, bookService.getAvailableCopies(id));
    }

    @PatchMapping("/{id}/availability")
    @Operation(summary = "Actualizar disponibilidad", description = "Actualiza si el libro se considera disponible para prestamo.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Actualizado",
                    content = @Content(schema = @Schema(implementation = BookDTO.class))),
            @ApiResponse(responseCode = "404", description = "Libro no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public BookDTO updateAvailability(
            @Parameter(description = "Identificador del libro", example = "1")
            @PathVariable Integer id,
            @Parameter(description = "Nuevo valor de disponibilidad")
            @RequestParam boolean available) {
        Book updated = bookService.updateAvailability(id, available);
        return BookMapper.toDTO(updated, bookService.getAvailableCopies(id));
    }
}
