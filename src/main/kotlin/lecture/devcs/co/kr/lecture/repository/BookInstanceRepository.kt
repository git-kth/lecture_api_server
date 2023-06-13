package lecture.devcs.co.kr.lecture.repository

import lecture.devcs.co.kr.lecture.entity.Book
import lecture.devcs.co.kr.lecture.entity.BookInstance
import lecture.devcs.co.kr.lecture.entity.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface BookInstanceRepository: JpaRepository<BookInstance, UUID> {
    fun findAllByBook(book: Book): MutableList<BookInstance>
    fun findAllByBorrower(member: Member): MutableList<BookInstance>
}