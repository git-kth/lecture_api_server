package lecture.devcs.co.kr.lecture.repository

import lecture.devcs.co.kr.lecture.entity.Author
import lecture.devcs.co.kr.lecture.entity.Book
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BookRepository: JpaRepository<Book, Long> {
    fun existsByAuthor(author: Author): Boolean
}