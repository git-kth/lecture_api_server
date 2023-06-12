package lecture.devcs.co.kr.lecture.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtUtils(
    @Autowired private val memberDetailsService: MemberDetailsService
) {
    companion object {
        private val EXP_TIME = 1000L * 60 * 60
        private val SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512)
    }
    fun createToken(id: String): String {
        val claims: Claims = Jwts.claims()
        claims["id"] = id
        return Jwts.builder()
            .setClaims(claims)
            .setExpiration(Date(System.currentTimeMillis() + EXP_TIME))
            .signWith(SECRET_KEY, SignatureAlgorithm.HS512)
            .compact()
    }

    fun validateToken(token: String): Boolean {
        val claims: Claims = getClaims(token)
        val exp: Date = claims.expiration
        return exp.after(Date())
    }

    fun getClaims(token: String): Claims {
        try{
            return Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).body
        }catch(e: Exception){
            throw Exception("인증 토큰 만료 및 허가 불가")
        }

    }

    fun getAuthentication(id: String): UsernamePasswordAuthenticationToken {
        val memberDetails = memberDetailsService.loadUserByUsername(id)
        val authentication = UsernamePasswordAuthenticationToken(memberDetails, "", memberDetails.authorities)
        return authentication
    }
}