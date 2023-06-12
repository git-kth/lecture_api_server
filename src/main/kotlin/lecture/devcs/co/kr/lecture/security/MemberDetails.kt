package lecture.devcs.co.kr.lecture.security

import lecture.devcs.co.kr.lecture.entity.Authority
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class MemberDetails(
    private val id: String,
    private val password: String,
    private val authorities: MutableSet<Authority>
): UserDetails {
    override fun getAuthorities(): Collection<GrantedAuthority> = authorities.map { SimpleGrantedAuthority(it.name) }

    override fun getPassword() = password

    override fun getUsername() = id

    override fun isAccountNonExpired() = true

    override fun isAccountNonLocked() = true

    override fun isCredentialsNonExpired() = true

    override fun isEnabled() = true
}