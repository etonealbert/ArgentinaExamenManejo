package com.etonealbert.examenmanejo.domain.usecase

import com.etonealbert.examenmanejo.domain.model.ExamAnswer
import com.etonealbert.examenmanejo.domain.model.ExamQuestionSnapshot
import com.etonealbert.examenmanejo.domain.repository.ExamRepository

class GetReviewAnswersUseCase(
    private val examRepository: ExamRepository,
) {
    suspend operator fun invoke(sessionId: Long): List<Pair<ExamQuestionSnapshot, ExamAnswer?>> {
        val answersByQuestionId = examRepository.getAnswers(sessionId).associateBy { it.questionId }
        return examRepository.getQuestionSnapshots(sessionId).map { snapshot ->
            snapshot to answersByQuestionId[snapshot.questionId]
        }
    }
}
