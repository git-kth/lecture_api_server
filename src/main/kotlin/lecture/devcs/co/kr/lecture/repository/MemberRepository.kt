package lecture.devcs.co.kr.lecture.repository

import lecture.devcs.co.kr.lecture.entity.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberRepository: JpaRepository<Member, Long> {
    fun findById(id: String): Member?
    fun existsById(id: String): Boolean
}