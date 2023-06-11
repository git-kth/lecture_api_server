package lecture.devcs.co.kr.lecture.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
class Language(
    @Id
    @Column(unique = true)
    val name: String
)