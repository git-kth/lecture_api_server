package lecture.devcs.co.kr.lecture.dto

import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.NotBlank

data class SigninRequest(
    @field:NotBlank(message = "ID: 필수 항목입니다.")
    val id: String,
    @field:NotBlank(message = "패스워드: 필수 항목입니다.")
    var pw: String
)