package lecture.devcs.co.kr.lecture.dto

import jakarta.validation.constraints.NotNull
import org.hibernate.validator.constraints.NotBlank
import java.time.LocalDate

data class AuthorRequest(
    @field:NotBlank(message = "이름: 필수 항목입니다.")
    val name: String,
    @field:NotNull(message = "생일: 필수 항목입니다.")
    val birthDate: LocalDate,
    val deathDate: LocalDate?
)
