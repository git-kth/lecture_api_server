package lecture.devcs.co.kr.lecture.dto

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.NotBlank

data class BookRequest(
    @field:NotBlank(message = "제목: 필수 항목입니다.")
    val title: String,
    val summary: String?,
    @field:NotNull(message = "저자: 필수 항목입니다.")
    val author_id: Long,
)
