package lecture.devcs.co.kr.lecture.controller

import lecture.devcs.co.kr.lecture.dto.AuthorRequest
import lecture.devcs.co.kr.lecture.dto.BookInstanceRequest
import lecture.devcs.co.kr.lecture.dto.BookRequest
import lecture.devcs.co.kr.lecture.entity.Author
import lecture.devcs.co.kr.lecture.entity.Book
import lecture.devcs.co.kr.lecture.entity.BookInstance
import lecture.devcs.co.kr.lecture.repository.AuthorRepository
import lecture.devcs.co.kr.lecture.repository.BookInstanceRepository
import lecture.devcs.co.kr.lecture.repository.BookRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/admin")
@CrossOrigin("*")
class AdminController(
    @Autowired private val bookRepository: BookRepository,
    @Autowired private val authorRepository: AuthorRepository,
    @Autowired private val bookInstanceRepository: BookInstanceRepository
) {
    @PostMapping("/book")
    fun bookRegister(@RequestBody @Validated bookRequest: BookRequest, bindingResult: BindingResult): ResponseEntity<Any?> {
        if(bindingResult.hasErrors()) return ResponseEntity.badRequest().body(fieldErrors(bindingResult))
        if(!authorRepository.existsById(bookRequest.author_id)) return ResponseEntity.badRequest().body("저자 정보가 없습니다.")
        bookRepository.save(Book(title = bookRequest.title, summary = bookRequest.summary, author = authorRepository.findById(bookRequest.author_id).get()))
        return ResponseEntity.ok().body(null)
    }

    @GetMapping("/book")
    fun bookList(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "id") sort: String,
        @RequestParam(defaultValue = "asc") order: String
    ): Page<Book> {
        val pageable = PageRequest.of(page, 10, Sort.by(if(order == "asc") Sort.Direction.ASC else Sort.Direction.DESC, sort))
        return bookRepository.findAll(pageable)
    }

    @GetMapping("/book/{id}")
    fun bookDetail(@PathVariable id: Long):ResponseEntity<Any?> {
        if(!bookRepository.existsById(id)) return ResponseEntity.badRequest().body("책 정보가 없습니다.")
        val book = bookRepository.findById(id).get()
        return ResponseEntity.ok().body(book)
    }

    @PatchMapping("/book/{id}")
    fun bookUpdate(@RequestBody @Validated bookRequest: BookRequest, @PathVariable id: Long, bindingResult: BindingResult): ResponseEntity<Any?> {
        if(bindingResult.hasErrors()) return ResponseEntity.badRequest().body(fieldErrors(bindingResult))
        if(!authorRepository.existsById(bookRequest.author_id)) return ResponseEntity.badRequest().body("저자 정보가 없습니다.")
        val book = bookRepository.findById(id).get()
        book.title = bookRequest.title; book.author = authorRepository.findById(bookRequest.author_id).get(); book.summary = bookRequest.summary
        bookRepository.save(book)
        return ResponseEntity.ok().body(null)
    }

    @DeleteMapping("/book")
    fun bookDelete(
        @RequestParam id: Long
    ): ResponseEntity<Any?> {
        if(!bookRepository.existsById(id)) return ResponseEntity.badRequest().body("책 정보가 없습니다.")
        bookRepository.deleteById(id)
        return ResponseEntity.ok().body(null)
    }

    @PostMapping("/author")
    fun authorRegister(@RequestBody @Validated authorRequest: AuthorRequest, bindingResult: BindingResult): ResponseEntity<Any?> {
        if(bindingResult.hasErrors()) return ResponseEntity.badRequest().body(fieldErrors(bindingResult))
        authorRepository.save(Author(name = authorRequest.name, birthDate = authorRequest.birthDate, deathDate = authorRequest.deathDate))
        return ResponseEntity.ok().body(null)
    }

    @GetMapping("/author")
    fun authorList(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "id") sort: String,
        @RequestParam(defaultValue = "asc") order: String
    ): Page<Author> {
        val pageable = PageRequest.of(page, 10, Sort.by(if(order == "asc") Sort.Direction.ASC else Sort.Direction.DESC, sort))
        return authorRepository.findAll(pageable)
    }

    @GetMapping("/author/{id}")
    fun authorDetail(@PathVariable id: Long):ResponseEntity<Any?> {
        if(!authorRepository.existsById(id)) return ResponseEntity.badRequest().body("저자 정보가 없습니다.")
        val author = authorRepository.findById(id).get()
        return ResponseEntity.ok().body(author)
    }

    @PatchMapping("/author/{id}")
    fun bookUpdate(@RequestBody @Validated authorRequest: AuthorRequest, @PathVariable id: Long, bindingResult: BindingResult): ResponseEntity<Any?> {
        if(bindingResult.hasErrors()) return ResponseEntity.badRequest().body(fieldErrors(bindingResult))
        val author = authorRepository.findById(id).get()
        author.name = authorRequest.name; author.birthDate =authorRequest.birthDate; author.deathDate = authorRequest.deathDate
        authorRepository.save(author)
        return ResponseEntity.ok().body(null)
    }

    @DeleteMapping("/author")
    fun authorDelete(
        @RequestParam id: Long
    ): ResponseEntity<Any?> {
        if(!authorRepository.existsById(id)) return ResponseEntity.badRequest().body("저자 정보가 없습니다.")
        authorRepository.deleteById(id)
        return ResponseEntity.ok().body(null)
    }

    @PostMapping("/bookinstance")
    fun bookInstanceRegister(@RequestBody @Validated bookInstanceRequest: BookInstanceRequest, bindingResult: BindingResult): ResponseEntity<Any?> {
        if(bindingResult.hasErrors()) return ResponseEntity.badRequest().body(fieldErrors(bindingResult))
        if(!bookRepository.existsById(bookInstanceRequest.book_id)) return ResponseEntity.badRequest().body("책 정보가 없습니다.")
        val book = bookRepository.findById(bookInstanceRequest.book_id).get()
        bookInstanceRepository.save(BookInstance(id = UUID.randomUUID(), imprint = bookInstanceRequest.imprint, book = book))
        return ResponseEntity.ok().body(null)
    }

    @GetMapping("/bookinstance")
    fun bookInstanceList(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "id") sort: String,
        @RequestParam(defaultValue = "asc") order: String
    ): Page<BookInstance> {
        val pageable = PageRequest.of(page, 10, Sort.by(if(order == "asc") Sort.Direction.ASC else Sort.Direction.DESC, sort))
        return bookInstanceRepository.findAll(pageable)
    }

    @GetMapping("/bookinstance/{uuid}")
    fun bookInstanceDetail(@PathVariable uuid: UUID):ResponseEntity<Any?> {
        if(!bookInstanceRepository.existsById(uuid)) return ResponseEntity.badRequest().body("책 인스턴스 정보가 없습니다.")
        val bookInstances = bookInstanceRepository.findById(uuid)
        return ResponseEntity.ok().body(bookInstances)
    }

    @PatchMapping("/bookinstance/{uuid}")
    fun bookInstanceUpdate(@RequestBody @Validated bookInstanceRequest: BookInstanceRequest, @PathVariable uuid: UUID, bindingResult: BindingResult): ResponseEntity<Any?> {
        if(bindingResult.hasErrors()) return ResponseEntity.badRequest().body(fieldErrors(bindingResult))
        if(!bookRepository.existsById(bookInstanceRequest.book_id)) return ResponseEntity.badRequest().body("책 정보가 없습니다.")
        if(!bookInstanceRepository.existsById(uuid)) return ResponseEntity.badRequest().body("책 인스턴스 정보가 없습니다.")
        val bookInstance = bookInstanceRepository.findById(uuid).get()
        bookInstance.imprint = bookInstanceRequest.imprint; bookInstance.book = bookRepository.findById(bookInstanceRequest.book_id).get()
        bookInstanceRepository.save(bookInstance)
        return ResponseEntity.ok().body(null)
    }

    @DeleteMapping("/bookinstance")
    fun bookInstanceDelete(
        @RequestParam uuid: UUID
    ): ResponseEntity<Any?> {
        if(!bookInstanceRepository.existsById(uuid)) return ResponseEntity.badRequest().body("책 인스턴스 정보가 없습니다.")
        bookInstanceRepository.deleteById(uuid)
        return ResponseEntity.ok().body(null)
    }

    private fun fieldErrors(bindingResult: BindingResult): ResponseEntity<Any?> {
        val errorMessages = bindingResult.allErrors.map { it.defaultMessage }
        return ResponseEntity.badRequest().body(errorMessages.toString())
    }
}