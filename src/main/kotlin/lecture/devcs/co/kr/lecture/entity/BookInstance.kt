package lecture.devcs.co.kr.lecture.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import org.hibernate.annotations.GenericGenerator
import java.time.LocalDateTime
import java.util.UUID

@Entity
class BookInstance (
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name="uuid2", strategy = "uuid2")
    val id: UUID,
    var imprint: String,
    @ManyToOne
    var book: Book,
    @ManyToOne
    var borrower: Member? = null,
    var due_back: LocalDateTime? = null
)