package dto.interview

data class AnswerRequest(
    val userId: Long?,
    val questionId: Long?,
    val textAnswer: String?
)