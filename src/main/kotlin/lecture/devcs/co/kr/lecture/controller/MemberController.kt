package lecture.devcs.co.kr.lecture.controller

import lecture.devcs.co.kr.lecture.dto.SigninRequest
import lecture.devcs.co.kr.lecture.dto.SignupRequest
import lecture.devcs.co.kr.lecture.entity.Author
import lecture.devcs.co.kr.lecture.entity.Book
import lecture.devcs.co.kr.lecture.entity.BookInstance
import lecture.devcs.co.kr.lecture.repository.AuthorRepository
import lecture.devcs.co.kr.lecture.repository.BookInstanceRepository
import lecture.devcs.co.kr.lecture.repository.BookRepository
import lecture.devcs.co.kr.lecture.repository.MemberRepository
import lecture.devcs.co.kr.lecture.security.MemberDetails
import lecture.devcs.co.kr.lecture.service.MemberService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.lang.Exception
import java.time.LocalDateTime
import java.util.UUID

@RestController
@RequestMapping("/api/member")
@CrossOrigin("*")
class MemberController(
    @Autowired private val memberService: MemberService,
    @Autowired private val bookRepository: BookRepository,
    @Autowired private val authorRepository: AuthorRepository,
    @Autowired private val bookInstanceRepository: BookInstanceRepository
) {
    @GetMapping("/")
    fun member(): ResponseEntity<Any?> {
        return try {
            val memberDetails: MemberDetails = SecurityContextHolder.getContext().authentication.principal as MemberDetails
            val member = memberService.findById(memberDetails.username) ?: throw UsernameNotFoundException("")
            ResponseEntity.ok().body(mutableMapOf("id" to member.id, "name" to member.name, "createDate" to member.createdDate, "authorities" to member.authorities))
        }catch(e: Exception) {
            ResponseEntity.badRequest().body("인증이 되지 않습니다.")
        }
    }
    @PostMapping("/signup")
    fun signup(@RequestBody @Validated signupRequest: SignupRequest, bindingResult: BindingResult): ResponseEntity<Any?> {
        if(bindingResult.hasErrors()) return fieldErrors(bindingResult)
        if(memberService.checkIdDuplication(signupRequest.id)) return ResponseEntity.badRequest().body("ID: 이미 있는 ID입니다.")
        if(!memberService.confirmPassword(signupRequest.pw1, signupRequest.pw2)) return ResponseEntity.badRequest().body("패스워드: 패스워드와 패스워드 확인 입력이 일치하지 않습니다.")
        memberService.signup(signupRequest)
        return ResponseEntity.ok().body(null)
    }

    @PostMapping("/signin")
    fun signin(@RequestBody @Validated signinRequest: SigninRequest, bindingResult: BindingResult): ResponseEntity<Any?> {
        if(bindingResult.hasErrors()) return fieldErrors(bindingResult)
        if(!memberService.checkIdDuplication(signinRequest.id)) return ResponseEntity.badRequest().body("존재하지 않는 아이디입니다.")
        if(!memberService.checkPassword(signinRequest.id, signinRequest.pw)) return ResponseEntity.badRequest().body("비밀번호가 일치 하지 않습니다.")
        val token = memberService.signin(signinRequest)
        return ResponseEntity.ok().body(token)
    }

    @GetMapping("/book")
    fun bookList(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "id") sort: String,
        @RequestParam(defaultValue = "asc") order: String
    ): Page<Book> {
        val pageable = PageRequest.of(page, 30, Sort.by(if(order == "asc") Sort.Direction.ASC else Sort.Direction.DESC, sort))
        return bookRepository.findAll(pageable)
    }

    @GetMapping("/author")
    fun authorList(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "id") sort: String,
        @RequestParam(defaultValue = "asc") order: String
    ): Page<Author> {
        val pageable = PageRequest.of(page, 30, Sort.by(if(order == "asc") Sort.Direction.ASC else Sort.Direction.DESC, sort))
        return authorRepository.findAll(pageable)
    }

    @GetMapping("/bookinstance")
    fun bookInstanceList(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "id") sort: String,
        @RequestParam(defaultValue = "asc") order: String
    ): Page<BookInstance> {
        val pageable = PageRequest.of(page, 30, Sort.by(if(order == "asc") Sort.Direction.ASC else Sort.Direction.DESC, sort))
        return bookInstanceRepository.findAll(pageable)
    }

    @GetMapping("/bookinstance/{book_id}")
    fun specificBookInstanceList(@PathVariable book_id: Long):ResponseEntity<Any?> {
        if(!bookRepository.existsById(book_id)) return ResponseEntity.badRequest().body("책 정보가 없습니다.")
        val bookInstances = bookInstanceRepository.findAllByBook(bookRepository.findById(book_id).get())
        return ResponseEntity.ok().body(bookInstances)
    }

    @GetMapping("/myloan")
    fun myloan(): ResponseEntity<Any?> {
        val memberDetails: MemberDetails = SecurityContextHolder.getContext().authentication.principal as MemberDetails
        val member = memberService.findById(memberDetails.username) ?: throw UsernameNotFoundException("")
        val instances = bookInstanceRepository.findAllByBorrower(member)
        return ResponseEntity.ok().body(if(instances.size == 0) null else instances)
    }

    @GetMapping("/loan/{uuid}")
    fun loan(@PathVariable uuid: UUID): ResponseEntity<Any?> {
        return try {
            if(!bookInstanceRepository.existsById(uuid)) return ResponseEntity.badRequest().body("책 인스턴스 정보가 없습니다.")
            val memberDetails: MemberDetails = SecurityContextHolder.getContext().authentication.principal as MemberDetails
            val member = memberService.findById(memberDetails.username) ?: throw UsernameNotFoundException("")
            val instances = bookInstanceRepository.findAllByBorrower(member)
            member.authorities.forEach{
                if(it.name == "ROLE_NORMAL" && instances.size >= 3) return ResponseEntity.badRequest().body("일반 유저는 3권이상 빌릴 수 없습니다.")
                else if((it.name == "ROLE_VIP" || it.name == "ROLE_ADMIN") && instances.size >= 5) return ResponseEntity.badRequest().body("특별 유저는 5권이상 빌릴 수 없습니다.")
            }
            val instance = bookInstanceRepository.findById(uuid).get()
            instance.borrower = member
            instance.due_back = LocalDateTime.now().plusDays(14)
            bookInstanceRepository.save(instance)
            ResponseEntity.ok().body(null)
        }catch(e: Exception) {
            ResponseEntity.badRequest().body("인증이 되지 않습니다.")
        }
    }

    @GetMapping("/return/{uuid}")
    fun returning(@PathVariable uuid: UUID): ResponseEntity<Any?> {
        if(!bookInstanceRepository.existsById(uuid)) return ResponseEntity.badRequest().body("책 인스턴스 정보가 없습니다.")
        val memberDetails: MemberDetails = SecurityContextHolder.getContext().authentication.principal as MemberDetails
        val member = memberService.findById(memberDetails.username) ?: throw UsernameNotFoundException("")
        val instance = bookInstanceRepository.findById(uuid).get()
        println(instance)
        if(instance.borrower != member) return ResponseEntity.badRequest().body("권한이 없습니다.")
        instance.due_back = null
        instance.borrower = null
        bookInstanceRepository.save(instance)
        return ResponseEntity.ok().body(null)
    }

    private fun fieldErrors(bindingResult: BindingResult): ResponseEntity<Any?> {
        val errorMessages = bindingResult.allErrors.map { it.defaultMessage }
        return ResponseEntity.badRequest().body(errorMessages.toString())
    }
}