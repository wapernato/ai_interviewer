package config

object ApiConfig {
    val baseUrl: String = System.getProperty("baseUrl", "http://localhost:8080") // адресс моего backend API
}
