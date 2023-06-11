package lecture.devcs.co.kr.lecture.repository

import lecture.devcs.co.kr.lecture.entity.Authority
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AuthorityRepository: JpaRepository<Authority, String> {
    fun findByName(name: String): Authority?
}