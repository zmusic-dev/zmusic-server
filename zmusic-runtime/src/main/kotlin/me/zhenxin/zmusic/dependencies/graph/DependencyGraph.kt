package me.zhenxin.zmusic.dependencies.graph

/**
 * 依赖关系图
 * 管理和分析依赖树结构
 */
class DependencyGraph {

    private val roots = ArrayList<DependencyNode>()
    private val nodeMap = HashMap<String, DependencyNode>()
    private val visited = HashSet<String>()

    fun addRoot(root: DependencyNode) {
        roots.add(root)
        indexNode(root)
    }

    private fun indexNode(node: DependencyNode) {
        nodeMap[node.getCoordinate()] = node
        for (child in node.getChildren()) {
            indexNode(child)
        }
    }

    fun findNode(coordinate: String): DependencyNode? {
        return nodeMap[coordinate]
    }

    fun hasCycle(): Boolean {
        visited.clear()
        val recursionStack = HashSet<String>()
        for (root in roots) {
            if (hasCycleDfs(root, recursionStack)) {
                return true
            }
        }
        return false
    }

    private fun hasCycleDfs(node: DependencyNode, recursionStack: MutableSet<String>): Boolean {
        val coordinate = node.getCoordinate()
        if (coordinate in recursionStack) {
            return true
        }
        if (coordinate in visited) {
            return false
        }

        visited.add(coordinate)
        recursionStack.add(coordinate)
        for (child in node.getChildren()) {
            if (hasCycleDfs(child, recursionStack)) {
                return true
            }
        }
        recursionStack.remove(coordinate)
        return false
    }

    fun getLeafNodes(): List<DependencyNode> {
        val leaves = ArrayList<DependencyNode>()
        for (root in roots) {
            root.traverse { node ->
                if (node.isLeaf()) {
                    leaves.add(node)
                }
            }
        }
        return leaves
    }

    fun getMaxDepth(): Int {
        var maxDepth = 0
        for (root in roots) {
            maxDepth = maxOf(maxDepth, getMaxDepthRecursive(root))
        }
        return maxDepth
    }

    private fun getMaxDepthRecursive(node: DependencyNode): Int {
        if (node.isLeaf()) {
            return node.getDepth()
        }

        var maxDepth = node.getDepth()
        for (child in node.getChildren()) {
            maxDepth = maxOf(maxDepth, getMaxDepthRecursive(child))
        }
        return maxDepth
    }

    fun getTotalNodes(): Int {
        return nodeMap.size
    }

    fun getScopeStatistics(): Map<String, Int> {
        val stats = HashMap<String, Int>()
        for (node in nodeMap.values) {
            val scope = node.getScope()?.toString() ?: "UNKNOWN"
            stats[scope] = (stats[scope] ?: 0) + 1
        }
        return stats
    }

    fun findVersionConflicts(): Map<String, List<String>> {
        val versionMap = HashMap<String, MutableSet<String>>()
        for (node in nodeMap.values) {
            versionMap.computeIfAbsent(node.getShortCoordinate()) { HashSet() }.add(node.getVersion())
        }

        val conflicts = HashMap<String, List<String>>()
        for ((key, versions) in versionMap) {
            if (versions.size > 1) {
                conflicts[key] = ArrayList(versions)
            }
        }
        return conflicts
    }

    fun findOptionalDependencies(): List<DependencyNode> {
        val optionals = ArrayList<DependencyNode>()
        for (node in nodeMap.values) {
            if (node.isOptional()) {
                optionals.add(node)
            }
        }
        return optionals
    }

    fun traverse(visitor: DependencyNode.NodeVisitor) {
        for (root in roots) {
            root.traverse(visitor)
        }
    }

    fun getRoots(): List<DependencyNode> {
        return ArrayList(roots)
    }

    fun getAllNodes(): Collection<DependencyNode> {
        return ArrayList(nodeMap.values)
    }

    fun clear() {
        roots.clear()
        nodeMap.clear()
        visited.clear()
    }

    override fun toString(): String {
        return "DependencyGraph(" +
            "roots=${roots.size}, " +
            "totalNodes=${getTotalNodes()}, " +
            "maxDepth=${getMaxDepth()}" +
            ")"
    }
}
