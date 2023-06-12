package lecture.devcs.co.kr.lecture.security

import lecture.devcs.co.kr.lecture.entity.Member
import lecture.devcs.co.kr.lecture.repository.MemberRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class MemberDetailsService(
    @Autowired private val memberRepository: MemberRepository
): UserDetailsService {
    override fun loadUserByUsername(id: String): MemberDetails {
        val member: Member = memberRepository.findById(id) ?: throw UsernameNotFoundException("인증 불가 유저")
        return MemberDetails(member.id, member.pw, member.authorities)
    }
}