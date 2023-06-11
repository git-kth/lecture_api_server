package lecture.devcs.co.kr.lecture.controller

import lecture.devcs.co.kr.lecture.dto.SignupRequest
import lecture.devcs.co.kr.lecture.service.MemberService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/member")
@CrossOrigin("*")
class MemberController(
    @Autowired private val memberService: MemberService
) {
    @PostMapping("/signup")
    fun signup(@RequestBody @Validated signupRequest: SignupRequest, bindingResult: BindingResult): ResponseEntity<Any?> {
        if(bindingResult.hasErrors()) return fieldErrors(bindingResult)
        if(memberService.checkIdDuplication(signupRequest.id)) return ResponseEntity.badRequest().body("ID: 이미 있는 ID입니다.")
        if(!memberService.confirmPassword(signupRequest.pw1, signupRequest.pw2)) return ResponseEntity.badRequest().body("패스워드: 패스워드와 패스워드 확인 입력이 일치하지 않습니다.")
        memberService.signup(signupRequest)
        return ResponseEntity.ok().body(null)
    }

    private fun fieldErrors(bindingResult: BindingResult): ResponseEntity<Any?> {
        val errorMessages = bindingResult.allErrors.map { it.defaultMessage }
        return ResponseEntity.badRequest().body(errorMessages.toString())
    }
}