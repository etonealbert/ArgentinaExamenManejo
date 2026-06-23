package com.etonealbert.examenmanejo.domain.usecase

import com.etonealbert.examenmanejo.domain.model.ExamConfig

fun classBDemoExamConfig(questionCount: Int) = ExamConfig(
    licenseClassId = "B",
    questionCount = questionCount,
    passingPercentage = 70,
    timeLimitMinutes = null,
)
