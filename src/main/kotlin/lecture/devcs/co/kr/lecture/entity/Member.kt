package lecture.devcs.co.kr.lecture.entity

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
class Member(
    @Id
    val id: String,
    val pw: String,
    var name: String,
    var birthDate: LocalDate,
    val createdDate: LocalDateTime = LocalDateTime.now(),
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "member_authority",
            joinColumns = [JoinColumn(name = "member_id")],
            inverseJoinColumns = [JoinColumn(name = "authority_name")]
    )
    val authorities: MutableSet<Authority> = mutableSetOf()
)