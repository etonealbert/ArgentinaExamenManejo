package com.etonealbert.examenmanejo.core.di

import com.etonealbert.examenmanejo.core.time.Clock
import com.etonealbert.examenmanejo.core.time.SystemClock
import com.etonealbert.examenmanejo.core.navigation.AppNavigator
import com.etonealbert.examenmanejo.core.navigation.InMemoryAppNavigator
import com.etonealbert.examenmanejo.core.navigation.RootCoordinator
import com.etonealbert.examenmanejo.data.local.DatabaseProvider
import com.etonealbert.examenmanejo.data.local.DriverFactory
import com.etonealbert.examenmanejo.data.local.ExamLocalDataSource
import com.etonealbert.examenmanejo.data.local.LicenseClassLocalDataSource
import com.etonealbert.examenmanejo.data.local.QuestionLocalDataSource
import com.etonealbert.examenmanejo.data.local.QuestionPackContentStore
import com.etonealbert.examenmanejo.data.local.QuestionPackLocalDataSource
import com.etonealbert.examenmanejo.data.local.SettingsLocalDataSource
import com.etonealbert.examenmanejo.data.local.seed.SeedQuestionPackProvider
import com.etonealbert.examenmanejo.data.repository.ExamRepositoryImpl
import com.etonealbert.examenmanejo.data.repository.FakeSubscriptionRepository
import com.etonealbert.examenmanejo.data.repository.LicenseClassRepositoryImpl
import com.etonealbert.examenmanejo.data.repository.QuestionPackRepositoryImpl
import com.etonealbert.examenmanejo.data.repository.QuestionRepositoryImpl
import com.etonealbert.examenmanejo.data.repository.SettingsRepositoryImpl
import com.etonealbert.examenmanejo.data.repository.StatsRepositoryImpl
import com.etonealbert.examenmanejo.db.ExamenManejoDatabase
import com.etonealbert.examenmanejo.domain.repository.ExamRepository
import com.etonealbert.examenmanejo.domain.repository.LicenseClassRepository
import com.etonealbert.examenmanejo.domain.repository.QuestionPackRepository
import com.etonealbert.examenmanejo.domain.repository.QuestionRepository
import com.etonealbert.examenmanejo.domain.repository.SettingsRepository
import com.etonealbert.examenmanejo.domain.repository.StatsRepository
import com.etonealbert.examenmanejo.domain.repository.SubscriptionRepository
import com.etonealbert.examenmanejo.domain.usecase.CalculateExamResultUseCase
import com.etonealbert.examenmanejo.domain.usecase.CheckFirstLaunchSeedImportUseCase
import com.etonealbert.examenmanejo.domain.usecase.FinishExamUseCase
import com.etonealbert.examenmanejo.domain.usecase.GetExamHistoryUseCase
import com.etonealbert.examenmanejo.domain.usecase.GetExamResultUseCase
import com.etonealbert.examenmanejo.domain.usecase.GetLicenseClassesUseCase
import com.etonealbert.examenmanejo.domain.usecase.GetQuestionsByLicenseClassUseCase
import com.etonealbert.examenmanejo.domain.usecase.GetReviewAnswersUseCase
import com.etonealbert.examenmanejo.domain.usecase.GetUserStatsUseCase
import com.etonealbert.examenmanejo.domain.usecase.ImportSeedQuestionsUseCase
import com.etonealbert.examenmanejo.domain.usecase.StartExamUseCase
import com.etonealbert.examenmanejo.domain.usecase.SubmitExamAnswerUseCase
import com.etonealbert.examenmanejo.feature.exam.ExamCoordinator
import com.etonealbert.examenmanejo.feature.exam.ExamViewModel
import com.etonealbert.examenmanejo.feature.history.HistoryViewModel
import com.etonealbert.examenmanejo.feature.home.HomeViewModel
import com.etonealbert.examenmanejo.feature.onboarding.OnboardingViewModel
import com.etonealbert.examenmanejo.feature.result.ResultViewModel
import com.etonealbert.examenmanejo.feature.review.ReviewViewModel
import com.etonealbert.examenmanejo.feature.settings.SettingsViewModel
import com.etonealbert.examenmanejo.feature.study.StudyCoordinator
import com.etonealbert.examenmanejo.feature.study.StudyViewModel
import com.etonealbert.examenmanejo.feature.subscription.SubscriptionCoordinator
import com.etonealbert.examenmanejo.network.NoOpQuestionPackApi
import com.etonealbert.examenmanejo.network.QuestionPackApi
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

typealias KoinAppDeclaration = KoinApplication.() -> Unit

expect val platformModule: Module

val appModule = module {
    single { DatabaseProvider(get<DriverFactory>()) }
    single<ExamenManejoDatabase> { get<DatabaseProvider>().database }

    single<Clock> { SystemClock() }
    single { SeedQuestionPackProvider() }

    single { LicenseClassLocalDataSource(get()) }
    single { QuestionLocalDataSource(get()) }
    single { QuestionPackLocalDataSource(get()) }
    single<QuestionPackContentStore> { get<QuestionPackLocalDataSource>() }
    single { ExamLocalDataSource(get()) }
    single { SettingsLocalDataSource(get()) }

    single<LicenseClassRepository> { LicenseClassRepositoryImpl(get()) }
    single<QuestionRepository> { QuestionRepositoryImpl(get()) }
    single<QuestionPackRepository> { QuestionPackRepositoryImpl(get(), get()) }
    single<ExamRepository> { ExamRepositoryImpl(get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
    single<StatsRepository> { StatsRepositoryImpl(get()) }
    single<SubscriptionRepository> { FakeSubscriptionRepository() }

    single { CalculateExamResultUseCase() }
    single { CheckFirstLaunchSeedImportUseCase(get()) }
    single { FinishExamUseCase(get(), get(), get()) }
    single { GetExamHistoryUseCase(get()) }
    single { GetExamResultUseCase(get()) }
    single { GetLicenseClassesUseCase(get()) }
    single { GetQuestionsByLicenseClassUseCase(get()) }
    single { GetReviewAnswersUseCase(get()) }
    single { GetUserStatsUseCase(get()) }
    single { ImportSeedQuestionsUseCase(get()) }
    single { StartExamUseCase(get(), get(), get()) }
    single { SubmitExamAnswerUseCase(get(), get()) }

    single<QuestionPackApi> { NoOpQuestionPackApi() }

    single<AppNavigator> { InMemoryAppNavigator() }
    single { RootCoordinator(get(), get(), get()) }
    single { ExamCoordinator(get()) }
    single { StudyCoordinator(get()) }
    single { SubscriptionCoordinator(get()) }

    viewModel { OnboardingViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { (licenseClassId: String) -> StudyViewModel(licenseClassId, get()) }
    viewModel { (licenseClassId: String) -> ExamViewModel(licenseClassId, get(), get(), get(), get()) }
    viewModel { (examSessionId: Long) -> ResultViewModel(examSessionId, get()) }
    viewModel { (examSessionId: Long) -> ReviewViewModel(examSessionId, get()) }
    viewModel { HistoryViewModel(get()) }
    viewModel { SettingsViewModel() }
}

fun initKoin(config: KoinAppDeclaration? = null): KoinApplication = startKoin {
    config?.invoke(this)
    modules(appModule, platformModule)
}
