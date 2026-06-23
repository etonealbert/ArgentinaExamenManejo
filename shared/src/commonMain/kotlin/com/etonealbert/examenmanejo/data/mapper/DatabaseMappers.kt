package com.etonealbert.examenmanejo.data.mapper

import com.etonealbert.examenmanejo.db.Content_source
import com.etonealbert.examenmanejo.db.Exam_session
import com.etonealbert.examenmanejo.db.Exam_session_answer
import com.etonealbert.examenmanejo.db.Exam_session_question_snapshot
import com.etonealbert.examenmanejo.db.License_class
import com.etonealbert.examenmanejo.db.Question as QuestionRow
import com.etonealbert.examenmanejo.db.Question_category
import com.etonealbert.examenmanejo.db.Question_option
import com.etonealbert.examenmanejo.domain.model.AnswerOption
import com.etonealbert.examenmanejo.domain.model.ContentStatus
import com.etonealbert.examenmanejo.domain.model.ExamAnswer
import com.etonealbert.examenmanejo.domain.model.ExamOptionSnapshot
import com.etonealbert.examenmanejo.domain.model.ExamQuestionSnapshot
import com.etonealbert.examenmanejo.domain.model.ExamResult
import com.etonealbert.examenmanejo.domain.model.LicenseClass
import com.etonealbert.examenmanejo.domain.model.Question
import com.etonealbert.examenmanejo.domain.model.QuestionCategory
import com.etonealbert.examenmanejo.domain.model.QuestionSource
import com.etonealbert.examenmanejo.domain.model.QuestionStatus
import com.etonealbert.examenmanejo.domain.model.ReviewStatus
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val snapshotJson = Json { ignoreUnknownKeys = true }

@Serializable
private data class ExamOptionSnapshotJson(
    val optionId: String,
    val text: String,
    val position: Int,
)

fun License_class.toDomain(): LicenseClass = LicenseClass(
    id = license_class_id,
    displayName = display_name,
    description = description,
    isEnabled = is_enabled == 1L,
)

fun Question_option.toDomain(): AnswerOption = AnswerOption(
    id = option_id,
    questionId = question_id,
    text = text,
    isCorrect = is_correct == 1L,
    position = position.toInt(),
)

fun Question_category.toDomain(): QuestionCategory = QuestionCategory(
    id = category_id,
    displayName = display_name,
)

fun Content_source.toDomain(): QuestionSource = QuestionSource(
    id = source_id,
    title = title,
    url = url,
    accessedAt = accessed_at,
    attribution = attribution,
)

fun QuestionRow.toDomain(
    options: List<Question_option>,
    category: Question_category,
    source: Content_source,
    licenseClassIds: List<String>,
): Question = Question(
    id = question_id,
    licenseClassIds = licenseClassIds,
    jurisdictionId = jurisdiction_id,
    category = category.toDomain(),
    source = source.toDomain(),
    text = text,
    explanation = explanation,
    options = options.map { it.toDomain() },
    contentStatus = ContentStatus.valueOf(content_status),
    reviewStatus = ReviewStatus.valueOf(review_status),
    status = QuestionStatus.valueOf(question_status),
)

fun Question.toExamQuestionSnapshot(order: Int): ExamQuestionSnapshot = ExamQuestionSnapshot(
    questionId = id,
    questionText = text,
    category = category.displayName,
    explanation = explanation,
    options = options.map { option ->
        ExamOptionSnapshot(optionId = option.id, text = option.text, position = option.position)
    },
    correctOptionId = options.firstOrNull { it.isCorrect }?.id.orEmpty(),
    order = order,
)

fun List<ExamOptionSnapshot>.toSnapshotJson(): String = snapshotJson.encodeToString(
    map { option ->
        ExamOptionSnapshotJson(
            optionId = option.optionId,
            text = option.text,
            position = option.position,
        )
    },
)

fun String.toExamOptionSnapshots(): List<ExamOptionSnapshot> = snapshotJson
    .decodeFromString<List<ExamOptionSnapshotJson>>(this)
    .map { option ->
        ExamOptionSnapshot(optionId = option.optionId, text = option.text, position = option.position)
    }

fun Exam_session_question_snapshot.toDomain(): ExamQuestionSnapshot = ExamQuestionSnapshot(
    questionId = question_id,
    questionText = question_text_snapshot,
    category = category_snapshot,
    explanation = explanation_snapshot,
    options = options_snapshot_json.toExamOptionSnapshots(),
    correctOptionId = correct_option_id_snapshot,
    order = question_order.toInt(),
)

fun Exam_session_answer.toDomain(): ExamAnswer = ExamAnswer(
    sessionId = session_id,
    questionId = question_id,
    selectedOptionId = selected_option_id_snapshot,
    answeredAtEpochMillis = answered_at_epoch_ms,
)

fun Exam_session.toDomainResult(
    snapshots: List<ExamQuestionSnapshot>,
    answers: List<ExamAnswer>,
): ExamResult {
    val answersByQuestionId = answers.associateBy { it.questionId }
    val correctCount = snapshots.count { snapshot ->
        answersByQuestionId[snapshot.questionId]?.selectedOptionId == snapshot.correctOptionId
    }

    return ExamResult(
        sessionId = session_id,
        licenseClassId = license_class_id,
        totalQuestions = question_count.toInt(),
        correctCount = correctCount,
        incorrectCount = question_count.toInt() - correctCount,
        scorePercentage = score_percentage?.toInt() ?: 0,
        passed = passed == 1L,
    )
}

fun Boolean.toDatabaseLong(): Long = if (this) 1L else 0L
