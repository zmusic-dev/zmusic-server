package me.zhenxin.zmusic.dependencies.graph

import java.io.File
import java.io.IOException

/**
 * 依赖图导出工具
 * 支持导出为 GraphViz DOT、JSON、文本树格式
 */
class DependencyGraphExporter(
    private val graph: DependencyGraph,
) {

    @Throws(IOException::class)
    fun exportDot(file: File) {
        file.bufferedWriter().use { writer ->
            writer.write("digraph DependencyGraph {\n")
            writer.write("  rankdir=TB;\n")
            writer.write("  node [shape=box, style=rounded];\n\n")

            val writtenEdges = HashSet<String>()
            for (root in graph.getRoots()) {
                writeDotNode(writer, root, writtenEdges)
            }

            writer.write("}\n")
        }
    }

    @Throws(IOException::class)
    private fun writeDotNode(writer: Appendable, node: DependencyNode, writtenEdges: MutableSet<String>) {
        val nodeId = getNodeId(node)
        writer.append(
            "  \"$nodeId\" [label=\"${getNodeLabel(node)}\"${getNodeStyle(node)}];\n"
        )

        for (child in node.getChildren()) {
            val childId = getNodeId(child)
            val edgeId = "$nodeId->$childId"
            if (writtenEdges.add(edgeId)) {
                writer.append("  \"$nodeId\" -> \"$childId\"${getEdgeStyle(child)};\n")
                writeDotNode(writer, child, writtenEdges)
            }
        }
    }

    private fun getNodeId(node: DependencyNode): String {
        return node.getCoordinate().replace(":", "_").replace(".", "_")
    }

    private fun getNodeLabel(node: DependencyNode): String {
        return buildString {
            append(node.getArtifactId()).append("\\n").append(node.getVersion())
            node.getScope()?.let { append("\\n(").append(it).append(")") }
        }
    }

    private fun getNodeStyle(node: DependencyNode): String {
        return when {
            node.isOptional() -> ", color=gray, style=\"rounded,dashed\""
            node.isRoot() -> ", color=blue, style=\"rounded,filled\", fillcolor=lightblue"
            else -> ""
        }
    }

    private fun getEdgeStyle(child: DependencyNode): String {
        return if (child.isOptional()) " [style=dashed, color=gray]" else ""
    }

    @Throws(IOException::class)
    fun exportJson(file: File) {
        file.bufferedWriter().use { writer ->
            writer.write("{\n")
            writer.write("  \"totalNodes\": ${graph.getTotalNodes()},\n")
            writer.write("  \"maxDepth\": ${graph.getMaxDepth()},\n")
            writer.write("  \"roots\": [\n")

            val roots = graph.getRoots()
            for ((index, root) in roots.withIndex()) {
                writeJsonNode(writer, root, 2)
                writer.write(if (index < roots.lastIndex) ",\n" else "\n")
            }

            writer.write("  ]\n")
            writer.write("}\n")
        }
    }

    @Throws(IOException::class)
    private fun writeJsonNode(writer: Appendable, node: DependencyNode, indent: Int) {
        val indentStr = "  ".repeat(indent)
        writer.append(indentStr).append("{\n")
        writer.append(indentStr).append("  \"coordinate\": \"").append(node.getCoordinate()).append("\",\n")
        writer.append(indentStr).append("  \"groupId\": \"").append(node.getGroupId()).append("\",\n")
        writer.append(indentStr).append("  \"artifactId\": \"").append(node.getArtifactId()).append("\",\n")
        writer.append(indentStr).append("  \"version\": \"").append(node.getVersion()).append("\",\n")
        writer.append(indentStr).append("  \"scope\": \"").append(node.getScope()?.toString() ?: "UNKNOWN").append("\",\n")
        writer.append(indentStr).append("  \"optional\": ").append(node.isOptional().toString()).append(",\n")
        writer.append(indentStr).append("  \"depth\": ").append(node.getDepth().toString())

        val children = node.getChildren()
        if (children.isNotEmpty()) {
            writer.append(",\n")
            writer.append(indentStr).append("  \"children\": [\n")
            for ((index, child) in children.withIndex()) {
                writeJsonNode(writer, child, indent + 2)
                writer.append(if (index < children.lastIndex) ",\n" else "\n")
            }
            writer.append(indentStr).append("  ]\n")
        } else {
            writer.append("\n")
        }

        writer.append(indentStr).append("}")
    }

    @Throws(IOException::class)
    fun exportTextTree(file: File) {
        file.bufferedWriter().use { writer ->
            writer.write("Dependency Tree\n")
            writer.write("===============\n\n")

            for (root in graph.getRoots()) {
                writeTextTreeNode(writer, root, "", true)
            }

            writer.write("\n")
            writer.write("Statistics\n")
            writer.write("==========\n")
            writer.write("Total nodes: ${graph.getTotalNodes()}\n")
            writer.write("Max depth: ${graph.getMaxDepth()}\n")
            writer.write("Leaf nodes: ${graph.getLeafNodes().size}\n")

            writer.write("\nScope Statistics:\n")
            for ((scope, count) in graph.getScopeStatistics()) {
                writer.write("  $scope: $count\n")
            }

            val conflicts = graph.findVersionConflicts()
            if (conflicts.isNotEmpty()) {
                writer.write("\nVersion Conflicts:\n")
                for ((coordinate, versions) in conflicts) {
                    writer.write("  $coordinate: $versions\n")
                }
            }

            val optionals = graph.findOptionalDependencies()
            if (optionals.isNotEmpty()) {
                writer.write("\nOptional Dependencies:\n")
                for (node in optionals) {
                    writer.write("  ${node.getCoordinate()}\n")
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun writeTextTreeNode(writer: Appendable, node: DependencyNode, prefix: String, isLast: Boolean) {
        writer.append(prefix)
        writer.append(if (isLast) "└── " else "├── ")
        writer.append(node.toString())
        writer.append('\n')

        val children = node.getChildren()
        for ((index, child) in children.withIndex()) {
            val childPrefix = prefix + if (isLast) "    " else "│   "
            writeTextTreeNode(writer, child, childPrefix, index == children.lastIndex)
        }
    }

    @Throws(IOException::class)
    fun exportAll(baseFileName: String) {
        exportDot(File("$baseFileName.dot"))
        exportJson(File("$baseFileName.json"))
        exportTextTree(File("$baseFileName.txt"))
    }
}
