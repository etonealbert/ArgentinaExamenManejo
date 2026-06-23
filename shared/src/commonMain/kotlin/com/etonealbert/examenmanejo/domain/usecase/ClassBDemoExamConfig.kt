package com.etonealbert.examenmanejo.domain.usecase

import com.etonealbert.examenmanejo.domain.model.ExamConfig

const val CLASS_B_DEMO_QUESTION_COUNT = 3

fun classBDemoExamConfig(questionCount: Int) = ExamConfig(
    licenseClassId = "B",
    questionCount = questionCount,
    passingPercentage = 70,
    timeLimitMinutes = null,
)
