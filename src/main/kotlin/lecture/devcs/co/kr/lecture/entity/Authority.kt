package lecture.devcs.co.kr.lecture.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class Authority(
    @Id
    val name: String
)