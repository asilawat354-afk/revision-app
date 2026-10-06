package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.BuildEntity
import com.example.data.local.ProjectEntity
import com.example.data.model.BuildRecord
import com.example.data.model.BuildStatus
import com.example.data.model.BuildWorkerStatus
import com.example.data.model.UrlValidationResult
import com.example.data.model.WebProject
import com.example.data.remote.BuildWorkerClient
import com.example.data.remote.UrlValidator
import com.example.generator.ProjectGenerator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File

class Web2ApkRepository(
    private val database: AppDatabase,
    private val appContext: Context
) {
    val allProjects: Flow<List<WebProject>> = database.projectDao().getAllProjects()
        .map { list -> list.map { it.toDomain() } }

    val allBuilds: Flow<List<BuildRecord>> = database.buildDao().getAllBuilds()
        .map { list -> list.map { it.toDomain() } }

    val totalBuildsCount: Flow<Int> = database.buildDao().getTotalBuildsCount()
    val successfulBuildsCount: Flow<Int> = database.buildDao().getSuccessfulBuildsCount()
    val failedBuildsCount: Flow<Int> = database.buildDao().getFailedBuildsCount()
    val projectCount: Flow<Int> = database.projectDao().getProjectCount()

    suspend fun getProjectById(id: String): WebProject? {
        return database.projectDao().getProjectById(id)?.toDomain()
    }

    suspend fun saveProject(project: WebProject) {
        database.projectDao().insertProject(ProjectEntity.fromDomain(project))
    }

    suspend fun deleteProject(id: String) {
        database.projectDao().deleteProjectById(id)
    }

    suspend fun saveBuild(build: BuildRecord) {
        database.buildDao().insertBuild(BuildEntity.fromDomain(build))
    }

    suspend fun updateBuild(build: BuildRecord) {
        database.buildDao().updateBuild(BuildEntity.fromDomain(build))
    }

    suspend fun deleteBuild(id: String) {
        database.buildDao().deleteBuildById(id)
    }

    suspend fun clearBuilds() {
        database.buildDao().clearAllBuilds()
    }

    suspend fun validateUrl(url: String): UrlValidationResult {
        return UrlValidator.validate(url)
    }

    suspend fun pingWorker(workerUrl: String): BuildWorkerStatus {
        return BuildWorkerClient.checkHealth(workerUrl)
    }

    suspend fun submitBuildJob(
        workerUrl: String,
        project: WebProject,
        keyPass: String,
        storePass: String
    ): Result<String> {
        return BuildWorkerClient.submitBuildJob(workerUrl, project, keyPass, storePass)
    }

    suspend fun pollBuildStatus(workerUrl: String, buildId: String): Result<BuildRecord> {
        return BuildWorkerClient.pollJobStatus(workerUrl, buildId)
    }

    suspend fun cancelBuild(workerUrl: String, buildId: String): Result<Boolean> {
        return BuildWorkerClient.cancelJob(workerUrl, buildId)
    }

    fun exportProjectZip(project: WebProject): File {
        return ProjectGenerator.exportProjectAsZip(appContext, project)
    }
}
