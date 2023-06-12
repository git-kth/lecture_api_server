package lecture.devcs.co.kr.lecture.repository

import lecture.devcs.co.kr.lecture.entity.Author
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AuthorRepository: JpaRepository<Author, Long> {
    fun existsByName(name: String): Boolean
    override fun findAll(): MutableList<Author>
}