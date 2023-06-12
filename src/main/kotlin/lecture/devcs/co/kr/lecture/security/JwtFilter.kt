package lecture.devcs.co.kr.lecture.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtFilter(
    @Autowired private val jwtUtils: JwtUtils
): OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authorization = request.getHeader("Authorization") ?: return filterChain.doFilter(request, response)
        val token = authorization.substring("Bearer ".length)
        if (jwtUtils.validateToken(token)) {
            val id = jwtUtils.getClaims(token)["id"] as String
            val authentication = jwtUtils.getAuthentication(id)
            SecurityContextHolder.getContext().authentication = authentication
        }
        return filterChain.doFilter(request, response)
    }
}