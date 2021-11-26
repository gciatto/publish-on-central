package org.danilopianini.gradle.mavencentral

import io.github.gradlenexus.publishplugin.internal.ActionRetrier
import io.github.gradlenexus.publishplugin.internal.BasicActionRetrier
import io.github.gradlenexus.publishplugin.internal.NexusClient
import io.github.gradlenexus.publishplugin.internal.StagingRepository
import io.github.gradlenexus.publishplugin.internal.StagingRepositoryDescriptor
import io.github.gradlenexus.publishplugin.internal.StagingRepositoryTransitioner
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import java.net.URI
import java.time.Duration

/**
 * Lazy class acting as a container for stateful operations on Nexus.
 */
data class NexusStatefulOperation(
    private val project: Project,
    private val nexusUrl: String,
    private val user: Provider<String>,
    private val password: Provider<String>,
    private val timeOut: Duration,
    private val connectionTimeOut: Duration,
    private val group: String,
) {

    /**
     * Repository description.
     */
    val description by lazy { project.run { "$group:$name:$version" } }

    /**
     * The NexusClient.
     */
    val client: NexusClient by lazy {
        NexusClient(
            project.uri(nexusUrl),
            user.get(),
            password.get(),
            timeOut,
            connectionTimeOut,
        )
    }

    /**
     * Lazily computed staging profile id.
     */
    val stagingProfile: String by lazy {
        requireNotNull(client.findStagingProfileId(group)) {
            "Invalid group id '$group': could not find an appropriate staging profile"
        }
    }

    /**
     * Lazily computed staging repository descriptor.
     */
    val stagingRepository: StagingRepositoryDescriptor by lazy {
        println("Creating repo for $stagingProfile")
        client.createStagingRepository(
            stagingProfile,
            description,
        )
    }

    /**
     * Lazily computed staging repository url.
     */
    val repoUrl: URI by lazy { stagingRepository.stagingRepositoryUrl }

    /**
     * Lazily computed staging repository it.
     */
    val repoId: String by lazy { stagingRepository.stagingRepositoryId }

    private val transitioner by lazy {
        StagingRepositoryTransitioner(
            client,
            BasicActionRetrier(Int.MAX_VALUE, Duration.ofSeconds(retryInterval), StagingRepository::transitioning),
        )
    }

    /**
     * Closes the repository.
     */
    fun close() = transitioner.effectivelyClose(repoId, description)

    /**
     * Releases the repository. Must be called after close().
     */
    fun release() {
        transitioner.effectivelyRelease(repoId, description)
    }

    companion object {
        const val retryInterval: Long = 10
    }
}
