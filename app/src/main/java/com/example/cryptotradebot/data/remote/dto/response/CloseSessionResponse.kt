data class CloseSessionResponse(
    val message: String,
    val closed_sessions: List<ClosedSession>
)

data class ClosedSession(
    val session_id: Int,
    val symbol: String
) 