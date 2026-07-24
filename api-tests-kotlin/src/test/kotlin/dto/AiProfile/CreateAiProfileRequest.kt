package dto.AiProfile

data class CreateAiProfileRequest(
    val mode: String?,
    val descriptionMode: String?,
    val instructionMode: String?,
    val modelName: String?,
    val language: String?,
    val answerStyle: String?,
    val difficulty: String?,
    val feedbackMode: String?,
    val hintMode: Boolean?,
    val active: Boolean?,
    val temperature: Double?,
    val maxTokens: Int?
)