package com.etonealbert.examenmanejo.domain.usecase

import com.etonealbert.examenmanejo.domain.model.ExamAnswer
import com.etonealbert.examenmanejo.domain.model.ExamConfig
import com.etonealbert.examenmanejo.domain.model.ExamQuestionSnapshot
import com.etonealbert.examenmanejo.domain.model.ExamResult

class CalculateExamResultUseCase {
    fun calculate(
        config: ExamConfig,
        snapshots: List<ExamQuestionSnapshot>,
        answers: List<ExamAnswer>,
        sessionId: Long = answers.firstOrNull()?.sessionId ?: 0L,
    ): ExamResult {
        val answersByQuestionId = answers.associateBy { it.questionId }
        val correctCount = snapshots.count { snapshot ->
            answersByQuestionId[snapshot.questionId]?.selectedOptionId == snapshot.correctOptionId
        }
        val total = snapshots.size
        val score = if (total == 0) 0 else (correctCount * 100) / total
        return ExamResult(
            sessionId = sessionId,
            licenseClassId = config.licenseClassId,
            totalQuestions = total,
            correctCount = correctCount,
            incorrectCount = total - correctCount,
            scorePercentage = score,
            passed = score >= config.passingPercentage,
        )
    }
}
