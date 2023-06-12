package lecture.devcs.co.kr.lecture.controller

import lecture.devcs.co.kr.lecture.dto.SigninRequest
import lecture.devcs.co.kr.lecture.dto.SignupRequest
import lecture.devcs.co.kr.lecture.security.MemberDetails
import lecture.devcs.co.kr.lecture.service.MemberService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.lang.Exception

@RestController
@RequestMapping("/api/member")
@CrossOrigin("*")
class MemberController(
    @Autowired private val memberService: MemberService
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

    private fun fieldErrors(bindingResult: BindingResult): ResponseEntity<Any?> {
        val errorMessages = bindingResult.allErrors.map { it.defaultMessage }
        return ResponseEntity.badRequest().body(errorMessages.toString())
    }
}