package dto.auth

data class RegisterRequest(
    val username: String?, // знак ? ~ Optional
    val email: String?,
    val password: String?
)