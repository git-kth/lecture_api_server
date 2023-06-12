package lecture.devcs.co.kr.lecture.entity

import jakarta.persistence.*

@Entity
class Book(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    var title: String,
    var summary: String?,
    @ManyToOne
    var author: Author,
)