package me.zhenxin.zmusic.dependencies.graph;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 依赖图导出工具
 * 支持导出为 GraphViz DOT、JSON、文本树格式
 *
 * @author ZMusic Team
 * @since 4.0.0
 */
public class DependencyGraphExporter {

    private final DependencyGraph graph;

    public DependencyGraphExporter(@NotNull DependencyGraph graph) {
        this.graph = graph;
    }

    /**
     * 导出为 GraphViz DOT 格式
     *
     * @param file 输出文件
     * @throws IOException 如果写入失败
     */
    public void exportDot(@NotNull File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("digraph DependencyGraph {\n");
            writer.write("  rankdir=TB;\n");
            writer.write("  node [shape=box, style=rounded];\n\n");

            Set<String> writtenEdges = new HashSet<>();

            for (DependencyNode root : graph.getRoots()) {
                writeDotNode(writer, root, writtenEdges);
            }

            writer.write("}\n");
        }
    }

    /**
     * 递归写入 DOT 节点
     */
    private void writeDotNode(@NotNull BufferedWriter writer, @NotNull DependencyNode node,
                              @NotNull Set<String> writtenEdges) throws IOException {
        String nodeId = getNodeId(node);

        // 写入节点定义
        writer.write(String.format("  \"%s\" [label=\"%s\"%s];\n",
                nodeId,
                getNodeLabel(node),
                getNodeStyle(node)));

        // 写入边
        for (DependencyNode child : node.getChildren()) {
            String edgeId = nodeId + "->" + getNodeId(child);
            if (!writtenEdges.contains(edgeId)) {
                writtenEdges.add(edgeId);
                writer.write(String.format("  \"%s\" -> \"%s\"%s;\n",
                        nodeId,
                        getNodeId(child),
                        getEdgeStyle(child)));

                // 递归处理子节点
                writeDotNode(writer, child, writtenEdges);
            }
        }
    }

    /**
     * 获取节点 ID
     */
    private String getNodeId(@NotNull DependencyNode node) {
        return node.getCoordinate().replace(":", "_").replace(".", "_");
    }

    /**
     * 获取节点标签
     */
    private String getNodeLabel(@NotNull DependencyNode node) {
        StringBuilder label = new StringBuilder();
        label.append(node.getArtifactId()).append("\\n").append(node.getVersion());

        if (node.getScope() != null) {
            label.append("\\n(").append(node.getScope()).append(")");
        }

        return label.toString();
    }

    /**
     * 获取节点样式
     */
    private String getNodeStyle(@NotNull DependencyNode node) {
        if (node.isOptional()) {
            return ", color=gray, style=\"rounded,dashed\"";
        } else if (node.isRoot()) {
            return ", color=blue, style=\"rounded,filled\", fillcolor=lightblue";
        }
        return "";
    }

    /**
     * 获取边样式
     */
    private String getEdgeStyle(@NotNull DependencyNode child) {
        if (child.isOptional()) {
            return " [style=dashed, color=gray]";
        }
        return "";
    }

    /**
     * 导出为 JSON 格式
     *
     * @param file 输出文件
     * @throws IOException 如果写入失败
     */
    public void exportJson(@NotNull File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("{\n");
            writer.write("  \"totalNodes\": " + graph.getTotalNodes() + ",\n");
            writer.write("  \"maxDepth\": " + graph.getMaxDepth() + ",\n");
            writer.write("  \"roots\": [\n");

            List<DependencyNode> roots = graph.getRoots();
            for (int i = 0; i < roots.size(); i++) {
                writeJsonNode(writer, roots.get(i), 2);
                if (i < roots.size() - 1) {
                    writer.write(",\n");
                } else {
                    writer.write("\n");
                }
            }

            writer.write("  ]\n");
            writer.write("}\n");
        }
    }

    /**
     * 递归写入 JSON 节点
     */
    private void writeJsonNode(@NotNull BufferedWriter writer, @NotNull DependencyNode node,
                               int indent) throws IOException {
        String indentStr = "  ".repeat(indent);

        writer.write(indentStr + "{\n");
        writer.write(indentStr + "  \"coordinate\": \"" + node.getCoordinate() + "\",\n");
        writer.write(indentStr + "  \"groupId\": \"" + node.getGroupId() + "\",\n");
        writer.write(indentStr + "  \"artifactId\": \"" + node.getArtifactId() + "\",\n");
        writer.write(indentStr + "  \"version\": \"" + node.getVersion() + "\",\n");
        writer.write(indentStr + "  \"scope\": \"" + (node.getScope() != null ? node.getScope() : "UNKNOWN") + "\",\n");
        writer.write(indentStr + "  \"optional\": " + node.isOptional() + ",\n");
        writer.write(indentStr + "  \"depth\": " + node.getDepth());

        List<DependencyNode> children = node.getChildren();
        if (!children.isEmpty()) {
            writer.write(",\n");
            writer.write(indentStr + "  \"children\": [\n");

            for (int i = 0; i < children.size(); i++) {
                writeJsonNode(writer, children.get(i), indent + 2);
                if (i < children.size() - 1) {
                    writer.write(",\n");
                } else {
                    writer.write("\n");
                }
            }

            writer.write(indentStr + "  ]\n");
        } else {
            writer.write("\n");
        }

        writer.write(indentStr + "}");
    }

    /**
     * 导出为文本树格式
     *
     * @param file 输出文件
     * @throws IOException 如果写入失败
     */
    public void exportTextTree(@NotNull File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("Dependency Tree\n");
            writer.write("===============\n\n");

            for (DependencyNode root : graph.getRoots()) {
                writeTextTreeNode(writer, root, "", true);
            }

            // 写入统计信息
            writer.write("\n");
            writer.write("Statistics\n");
            writer.write("==========\n");
            writer.write("Total nodes: " + graph.getTotalNodes() + "\n");
            writer.write("Max depth: " + graph.getMaxDepth() + "\n");
            writer.write("Leaf nodes: " + graph.getLeafNodes().size() + "\n");

            // 作用域统计
            writer.write("\nScope Statistics:\n");
            Map<String, Integer> scopeStats = graph.getScopeStatistics();
            for (Map.Entry<String, Integer> entry : scopeStats.entrySet()) {
                writer.write(String.format("  %s: %d\n", entry.getKey(), entry.getValue()));
            }

            // 版本冲突
            Map<String, List<String>> conflicts = graph.findVersionConflicts();
            if (!conflicts.isEmpty()) {
                writer.write("\nVersion Conflicts:\n");
                for (Map.Entry<String, List<String>> entry : conflicts.entrySet()) {
                    writer.write(String.format("  %s: %s\n", entry.getKey(), entry.getValue()));
                }
            }

            // 可选依赖
            List<DependencyNode> optionals = graph.findOptionalDependencies();
            if (!optionals.isEmpty()) {
                writer.write("\nOptional Dependencies:\n");
                for (DependencyNode node : optionals) {
                    writer.write("  " + node.getCoordinate() + "\n");
                }
            }
        }
    }

    /**
     * 递归写入文本树节点
     */
    private void writeTextTreeNode(@NotNull BufferedWriter writer, @NotNull DependencyNode node,
                                   @NotNull String prefix, boolean isLast) throws IOException {
        // 写入当前节点
        writer.write(prefix);
        writer.write(isLast ? "└── " : "├── ");
        writer.write(node.toString());
        writer.write("\n");

        // 写入子节点
        List<DependencyNode> children = node.getChildren();
        for (int i = 0; i < children.size(); i++) {
            boolean childIsLast = (i == children.size() - 1);
            String childPrefix = prefix + (isLast ? "    " : "│   ");
            writeTextTreeNode(writer, children.get(i), childPrefix, childIsLast);
        }
    }

    /**
     * 导出为所有格式
     *
     * @param baseFileName 基础文件名（不含扩展名）
     * @throws IOException 如果写入失败
     */
    public void exportAll(@NotNull String baseFileName) throws IOException {
        exportDot(new File(baseFileName + ".dot"));
        exportJson(new File(baseFileName + ".json"));
        exportTextTree(new File(baseFileName + ".txt"));
    }
}
