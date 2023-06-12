package lecture.devcs.co.kr.lecture.dto

import jakarta.validation.constraints.NotNull
import org.hibernate.validator.constraints.NotBlank

data class BookInstanceRequest(
    @field:NotBlank(message = "출판사: 필수 항목입니다.")
    val imprint: String,
    @field:NotNull(message = "출판사: 필수 항목입니다.")
    val book_id: Long,
)
