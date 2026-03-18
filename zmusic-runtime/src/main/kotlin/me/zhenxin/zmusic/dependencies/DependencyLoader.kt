package me.zhenxin.zmusic.dependencies

import me.zhenxin.zmusic.dependencies.exception.DependencyException
import java.io.File

/**
 * 依赖加载器 - Builder 模式
 */
class DependencyLoader @JvmOverloads constructor(
    private val dependency: RuntimeEnvDependency = RuntimeEnv.ENV_DEPENDENCY,
) {

    private var coordinate: String? = null
    private var baseDirectory: File = File(dependency.defaultLibrary)
    private val relocations = mutableListOf<JarRelocation>()
    private var repository: String? = null
    private var ignoreOptional = true
    private var ignoreException = false
    private var transitive = true
    private var scopes: List<DependencyScope> = listOf(DependencyScope.RUNTIME, DependencyScope.COMPILE)
    private var external = true

    fun coordinate(coordinate: String): DependencyLoader {
        this.coordinate = coordinate
        return this
    }

    fun baseDirectory(baseDirectory: File): DependencyLoader {
        this.baseDirectory = baseDirectory
        return this
    }

    fun baseDirectory(baseDirectory: String): DependencyLoader {
        this.baseDirectory = File(baseDirectory)
        return this
    }

    fun relocate(from: String, to: String): DependencyLoader {
        relocations.add(JarRelocation(from, to))
        return this
    }

    fun relocations(relocations: List<JarRelocation>): DependencyLoader {
        this.relocations.addAll(relocations)
        return this
    }

    fun repository(repository: String): DependencyLoader {
        this.repository = repository
        return this
    }

    fun ignoreOptional(ignoreOptional: Boolean): DependencyLoader {
        this.ignoreOptional = ignoreOptional
        return this
    }

    fun ignoreException(ignoreException: Boolean): DependencyLoader {
        this.ignoreException = ignoreException
        return this
    }

    fun transitive(transitive: Boolean): DependencyLoader {
        this.transitive = transitive
        return this
    }

    fun scopes(vararg scopes: DependencyScope): DependencyLoader {
        this.scopes = scopes.toList()
        return this
    }

    fun scopes(scopes: List<DependencyScope>): DependencyLoader {
        this.scopes = scopes
        return this
    }

    fun external(external: Boolean): DependencyLoader {
        this.external = external
        return this
    }

    @Throws(DependencyException::class)
    fun load() {
        validate()
        val currentCoordinate = checkNotNull(coordinate)
        try {
            dependency.loadDependency(
                currentCoordinate,
                baseDirectory,
                relocations,
                repository,
                ignoreOptional,
                ignoreException,
                transitive,
                scopes,
                external,
            )
        } catch (exception: DependencyException) {
            throw exception
        } catch (throwable: Throwable) {
            throw DependencyException("Failed to load dependency: $currentCoordinate", throwable)
        }
    }

    private fun validate() {
        check(!coordinate.isNullOrEmpty()) {
            "Maven coordinate is required. Use .coordinate(\"groupId:artifactId:version\") to set it."
        }
        check(scopes.isNotEmpty()) {
            "Dependency scopes cannot be empty"
        }
    }

    override fun toString(): String {
        return "DependencyLoader(" +
            "coordinate=$coordinate, " +
            "baseDirectory=$baseDirectory, " +
            "relocations=${relocations.size}, " +
            "repository=${repository ?: "default"}, " +
            "ignoreOptional=$ignoreOptional, " +
            "ignoreException=$ignoreException, " +
            "transitive=$transitive, " +
            "scopes=$scopes, " +
            "external=$external" +
            ")"
    }
}
