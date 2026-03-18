package me.zhenxin.zmusic.dependencies

import me.zhenxin.zmusic.dependencies.common.ClassAppender
import java.io.File

/**
 * Kotlin 侧依赖注解解析与编排。
 * 仅在 Kotlin runtime 已经就绪后调用，避免污染 Java bootstrap 阶段。
 */
object RuntimeDependencyProcessor {

    @JvmStatic
    @Throws(Throwable::class)
    fun loadAnnotatedDependencies(dependency: RuntimeEnvDependency, clazz: Class<*>) {
        val baseDirectory = File(dependency.defaultLibrary)
        for (parsedDependency in collectDependencies(clazz)) {
            val tests = parseTests(parsedDependency.test())
            if (tests.isNotEmpty() && tests.all(::isClassAvailable)) {
                continue
            }

            dependency.loadDependency(
                normalizeValue(parsedDependency.value()),
                baseDirectory,
                parseRelocations(parsedDependency.relocate()),
                parsedDependency.repository(),
                parsedDependency.ignoreOptional(),
                parsedDependency.ignoreException(),
                parsedDependency.transitive(),
                parsedDependency.scopes(),
                parsedDependency.external(),
            )
        }
    }

    private fun collectDependencies(clazz: Class<*>): List<ParsedDependency> {
        return clazz.getAnnotationsByType(RuntimeDependency::class.java)
            .map(::parseDependency)
    }

    private fun parseDependency(dependency: RuntimeDependency): ParsedDependency {
        return ParsedDependency(
            dependency.value,
            dependency.test,
            dependency.repository,
            dependency.transitive,
            dependency.ignoreOptional,
            dependency.ignoreException,
            dependency.scopes.toList(),
            dependency.relocate.toList(),
            dependency.external,
        )
    }

    private fun parseTests(testSpec: String): List<String> {
        if (testSpec.isBlank()) {
            return emptyList()
        }
        return testSpec.split(',')
            .map(String::trim)
            .filter(String::isNotEmpty)
    }

    private fun parseRelocations(relocate: List<String>): List<JarRelocation> {
        require(relocate.size % 2 == 0) {
            "invalid relocate format"
        }

        return relocate.chunked(2).map { (from, to) ->
            JarRelocation(normalizeValue(from), normalizeValue(to))
        }
    }

    private fun isClassAvailable(path: String): Boolean {
        val normalizedPath = normalizeValue(path)
        return normalizedPath.isNotEmpty() && ClassAppender.isExists(normalizedPath)
    }

    private fun normalizeValue(value: String): String {
        return if (value.startsWith("!")) value.substring(1) else value
    }
}
