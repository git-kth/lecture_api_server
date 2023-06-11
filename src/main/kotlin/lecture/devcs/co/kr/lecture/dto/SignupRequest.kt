package lecture.devcs.co.kr.lecture.dto

import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.NotBlank

data class SignupRequest(
    @field:NotBlank(message = "ID: 필수 항목입니다.")
    @field:Size(min = 6, max = 15, message = "ID는 6자 이상 15자 이하로 입력해주세요.")
    val id: String,
    @field:NotBlank(message = "패스워드: 필수 항목입니다.")
    @field:Size(min = 8, max = 18, message = "패스워드1는 8자 이상 18자 이하로 입력해주세요.")
    val pw1: String,
    @field:NotBlank(message = "패스워드 확인: 필수 항목입니다.")
    @field:Size(min = 8, max = 18, message = "패스워드2는 8자 이상 18자 이하로 입력해주세요.")
    val pw2: String,
    @field:NotBlank(message = "이름: 필수 항목입니다.")
    val name: String,
    @field:NotBlank(message = "생년월일: 필수 항목입니다.")
    val birthDate: String
)