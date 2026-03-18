package me.zhenxin.zmusic.dependencies

class ParsedDependency(
    value: String,
    test: String,
    repository: String,
    transitive: Boolean,
    ignoreOptional: Boolean,
    ignoreException: Boolean,
    scopes: List<DependencyScope>,
    relocate: List<String>,
    external: Boolean,
) {

    private val value = value
    private val test = test
    private val repository = repository
    private val transitive = transitive
    private val ignoreOptional = ignoreOptional
    private val ignoreException = ignoreException
    private val scopes = scopes.toList()
    private val relocate = relocate.toList()
    private val external = external

    fun value(): String {
        return value
    }

    fun test(): String {
        return test
    }

    fun repository(): String {
        return repository
    }

    fun transitive(): Boolean {
        return transitive
    }

    fun ignoreOptional(): Boolean {
        return ignoreOptional
    }

    fun ignoreException(): Boolean {
        return ignoreException
    }

    fun scopes(): List<DependencyScope> {
        return scopes
    }

    fun relocate(): List<String> {
        return relocate
    }

    fun external(): Boolean {
        return external
    }

    override fun toString(): String {
        return "ParsedDependency(" +
            "value='$value', " +
            "test='$test', " +
            "repository='$repository', " +
            "transitive=$transitive, " +
            "ignoreOptional=$ignoreOptional, " +
            "ignoreException=$ignoreException, " +
            "scopes=$scopes, " +
            "relocate=$relocate, " +
            "external=$external" +
            ")"
    }
}
