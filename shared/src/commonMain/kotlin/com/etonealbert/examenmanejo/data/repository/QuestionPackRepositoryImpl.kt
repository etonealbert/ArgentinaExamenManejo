package com.etonealbert.examenmanejo.data.repository

import com.etonealbert.examenmanejo.data.local.QuestionPackContentStore
import com.etonealbert.examenmanejo.data.local.seed.SeedQuestionPackProvider
import com.etonealbert.examenmanejo.domain.model.ImportResult
import com.etonealbert.examenmanejo.domain.repository.QuestionPackRepository
import kotlin.time.Clock

class QuestionPackRepositoryImpl(
    private val seedQuestionPackProvider: SeedQuestionPackProvider,
    private val localDataSource: QuestionPackContentStore,
    private val nowEpochMillis: () -> Long = { Clock.System.now().toEpochMilliseconds() },
) : QuestionPackRepository {
    override suspend fun importBundledSeedIfNeeded(): ImportResult {
        var importLogId: String? = null
        return try {
            val pack = seedQuestionPackProvider.readBundledClassBDemoPack()
            val storedHash = localDataSource.selectQuestionPackHash(pack.packId, pack.version)
            if (storedHash == pack.hash) {
                return ImportResult.AlreadyImported
            }

            val startedAt = nowEpochMillis()
            importLogId = "${pack.packId}-${pack.version}-$startedAt"
            localDataSource.insertImportLog(
                importLogId = importLogId,
                packId = pack.packId,
                version = pack.version,
                startedAtEpochMillis = startedAt,
                status = "STARTED",
                message = "",
            )

            val questionCount = localDataSource.importContent(pack, installedAtEpochMillis = nowEpochMillis())
            localDataSource.finishImportLog(
                importLogId = importLogId,
                finishedAtEpochMillis = nowEpochMillis(),
                status = "IMPORTED",
                message = "Imported $questionCount questions",
            )
            ImportResult.Imported(questionCount)
        } catch (exception: Exception) {
            val reason = exception.message ?: exception::class.simpleName ?: "Unknown import failure"
            importLogId?.let { failedImportLogId ->
                runCatching {
                    localDataSource.finishImportLog(
                        importLogId = failedImportLogId,
                        finishedAtEpochMillis = nowEpochMillis(),
                        status = "FAILED",
                        message = reason,
                    )
                }
            }
            ImportResult.Failed(reason)
        }
    }
}
