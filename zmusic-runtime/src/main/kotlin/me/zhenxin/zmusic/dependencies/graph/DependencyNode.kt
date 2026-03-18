package me.zhenxin.zmusic.dependencies.graph

import me.zhenxin.zmusic.dependencies.DependencyScope
import java.util.Collections
import java.util.Objects

/**
 * 依赖树节点
 * 表示依赖关系树中的一个依赖项
 */
class DependencyNode(
    groupId: String,
    artifactId: String,
    version: String,
    scope: DependencyScope?,
    optional: Boolean,
    depth: Int,
) {

    private val groupId = groupId
    private val artifactId = artifactId
    private val version = version
    private val scope = scope
    private val optional = optional
    private val depth = depth

    private val children = ArrayList<DependencyNode>()
    private var parent: DependencyNode? = null

    fun addChild(child: DependencyNode) {
        children.add(child)
        child.parent = this
    }

    fun removeChild(child: DependencyNode) {
        children.remove(child)
        child.parent = null
    }

    fun getCoordinate(): String {
        return "$groupId:$artifactId:$version"
    }

    fun getShortCoordinate(): String {
        return "$groupId:$artifactId"
    }

    fun isRoot(): Boolean {
        return parent == null
    }

    fun isLeaf(): Boolean {
        return children.isEmpty()
    }

    fun getAncestors(): List<DependencyNode> {
        val ancestors = ArrayList<DependencyNode>()
        var current = parent
        while (current != null) {
            ancestors.add(current)
            current = current.parent
        }
        return ancestors
    }

    fun getPath(): List<DependencyNode> {
        val path = ArrayList<DependencyNode>()
        var current: DependencyNode? = this
        while (current != null) {
            path.add(0, current)
            current = current.parent
        }
        return path
    }

    fun getSubtreeSize(): Int {
        var size = 1
        for (child in children) {
            size += child.getSubtreeSize()
        }
        return size
    }

    fun traverse(visitor: NodeVisitor) {
        visitor.visit(this)
        for (child in children) {
            child.traverse(visitor)
        }
    }

    fun getGroupId(): String {
        return groupId
    }

    fun getArtifactId(): String {
        return artifactId
    }

    fun getVersion(): String {
        return version
    }

    fun getScope(): DependencyScope? {
        return scope
    }

    fun isOptional(): Boolean {
        return optional
    }

    fun getDepth(): Int {
        return depth
    }

    fun getChildren(): List<DependencyNode> {
        return Collections.unmodifiableList(children)
    }

    fun getParent(): DependencyNode? {
        return parent
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is DependencyNode) {
            return false
        }
        return groupId == other.groupId &&
            artifactId == other.artifactId &&
            version == other.version
    }

    override fun hashCode(): Int {
        return Objects.hash(groupId, artifactId, version)
    }

    override fun toString(): String {
        return buildString {
            append(getCoordinate())
            if (scope != null) {
                append(" (").append(scope).append(")")
            }
            if (optional) {
                append(" [optional]")
            }
        }
    }

    fun interface NodeVisitor {
        fun visit(node: DependencyNode)
    }

    companion object {
        @JvmStatic
        fun fromCoordinate(
            coordinate: String,
            scope: DependencyScope?,
            optional: Boolean,
            depth: Int,
        ): DependencyNode {
            val parts = coordinate.split(":")
            require(parts.size >= 3) {
                "Invalid Maven coordinate: $coordinate"
            }
            return DependencyNode(parts[0], parts[1], parts[2], scope, optional, depth)
        }
    }
}
