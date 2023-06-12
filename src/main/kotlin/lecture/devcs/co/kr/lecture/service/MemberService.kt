package lecture.devcs.co.kr.lecture.service

import jakarta.transaction.Transactional
import lecture.devcs.co.kr.lecture.dto.SigninRequest
import lecture.devcs.co.kr.lecture.dto.SignupRequest
import lecture.devcs.co.kr.lecture.entity.Member
import lecture.devcs.co.kr.lecture.repository.AuthorityRepository
import lecture.devcs.co.kr.lecture.repository.MemberRepository
import lecture.devcs.co.kr.lecture.security.JwtUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class MemberService(
    @Autowired private val memberRepository: MemberRepository,
    @Autowired private val authorityRepository: AuthorityRepository,
    @Autowired private val jwtUtils: JwtUtils,
    @Autowired private val passwordEncoder: PasswordEncoder
) {
    fun checkIdDuplication(id: String) = memberRepository.existsById(id)
    fun confirmPassword(pw1: String, pw2: String) = pw1 == pw2
    fun checkPassword(id: String, pw: String) = passwordEncoder.matches(pw, memberRepository.findById(id)?.pw ?: "")
    fun findById(id: String) = memberRepository.findById(id)
    @Transactional
    fun signup(signupRequest: SignupRequest) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val authority = authorityRepository.findByName("ROLE_NORMAL") ?: throw Exception("ROLE_NORMAL 테이블에 있는지 확인해보세요.")
        memberRepository.save(Member(
            id = signupRequest.id,
            pw = passwordEncoder.encode(signupRequest.pw1),
            name = signupRequest.name,
            birthDate = LocalDate.parse(signupRequest.birthDate, formatter),
            authorities = mutableSetOf(authority)
        ))
    }

    fun signin(signinRequest: SigninRequest): String {
        return jwtUtils.createToken(signinRequest.id)
    }
}