package me.zhenxin.zmusic.dependencies.graph;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 依赖关系图
 * 管理和分析依赖树结构
 *
 * @author ZMusic Team
 * @since 4.0.0
 */
public class DependencyGraph {

    private final List<DependencyNode> roots = new ArrayList<>();
    private final Map<String, DependencyNode> nodeMap = new HashMap<>();
    private final Set<String> visited = new HashSet<>();

    /**
     * 添加根节点
     *
     * @param root 根节点
     */
    public void addRoot(@NotNull DependencyNode root) {
        roots.add(root);
        indexNode(root);
    }

    /**
     * 索引节点（递归）
     */
    private void indexNode(@NotNull DependencyNode node) {
        String coordinate = node.getCoordinate();
        nodeMap.put(coordinate, node);

        for (DependencyNode child : node.getChildren()) {
            indexNode(child);
        }
    }

    /**
     * 查找节点
     *
     * @param coordinate Maven 坐标
     * @return 节点，不存在返回 null
     */
    @Nullable
    public DependencyNode findNode(@NotNull String coordinate) {
        return nodeMap.get(coordinate);
    }

    /**
     * 检查是否存在循环依赖
     *
     * @return 是否存在循环依赖
     */
    public boolean hasCycle() {
        visited.clear();
        Set<String> recursionStack = new HashSet<>();

        for (DependencyNode root : roots) {
            if (hasCycleDFS(root, recursionStack)) {
                return true;
            }
        }

        return false;
    }

    /**
     * DFS 检测循环依赖
     */
    private boolean hasCycleDFS(@NotNull DependencyNode node, @NotNull Set<String> recursionStack) {
        String coordinate = node.getCoordinate();

        if (recursionStack.contains(coordinate)) {
            return true; // 发现循环
        }

        if (visited.contains(coordinate)) {
            return false; // 已经访问过且无循环
        }

        visited.add(coordinate);
        recursionStack.add(coordinate);

        for (DependencyNode child : node.getChildren()) {
            if (hasCycleDFS(child, recursionStack)) {
                return true;
            }
        }

        recursionStack.remove(coordinate);
        return false;
    }

    /**
     * 获取所有叶子节点
     */
    @NotNull
    public List<DependencyNode> getLeafNodes() {
        List<DependencyNode> leaves = new ArrayList<>();

        for (DependencyNode root : roots) {
            root.traverse(node -> {
                if (node.isLeaf()) {
                    leaves.add(node);
                }
            });
        }

        return leaves;
    }

    /**
     * 获取最大深度
     */
    public int getMaxDepth() {
        int maxDepth = 0;

        for (DependencyNode root : roots) {
            maxDepth = Math.max(maxDepth, getMaxDepthRecursive(root));
        }

        return maxDepth;
    }

    private int getMaxDepthRecursive(@NotNull DependencyNode node) {
        if (node.isLeaf()) {
            return node.getDepth();
        }

        int maxDepth = node.getDepth();
        for (DependencyNode child : node.getChildren()) {
            maxDepth = Math.max(maxDepth, getMaxDepthRecursive(child));
        }

        return maxDepth;
    }

    /**
     * 获取总节点数
     */
    public int getTotalNodes() {
        return nodeMap.size();
    }

    /**
     * 统计各作用域的依赖数量
     */
    @NotNull
    public Map<String, Integer> getScopeStatistics() {
        Map<String, Integer> stats = new HashMap<>();

        for (DependencyNode node : nodeMap.values()) {
            String scope = node.getScope() != null ? node.getScope().toString() : "UNKNOWN";
            stats.put(scope, stats.getOrDefault(scope, 0) + 1);
        }

        return stats;
    }

    /**
     * 查找所有版本冲突
     * 返回 groupId:artifactId -> List<version>
     */
    @NotNull
    public Map<String, List<String>> findVersionConflicts() {
        Map<String, Set<String>> versionMap = new HashMap<>();

        for (DependencyNode node : nodeMap.values()) {
            String key = node.getShortCoordinate();
            versionMap.computeIfAbsent(key, k -> new HashSet<>()).add(node.getVersion());
        }

        Map<String, List<String>> conflicts = new HashMap<>();
        for (Map.Entry<String, Set<String>> entry : versionMap.entrySet()) {
            if (entry.getValue().size() > 1) {
                conflicts.put(entry.getKey(), new ArrayList<>(entry.getValue()));
            }
        }

        return conflicts;
    }

    /**
     * 查找可选依赖
     */
    @NotNull
    public List<DependencyNode> findOptionalDependencies() {
        List<DependencyNode> optionals = new ArrayList<>();

        for (DependencyNode node : nodeMap.values()) {
            if (node.isOptional()) {
                optionals.add(node);
            }
        }

        return optionals;
    }

    /**
     * 遍历所有节点
     */
    public void traverse(@NotNull DependencyNode.NodeVisitor visitor) {
        for (DependencyNode root : roots) {
            root.traverse(visitor);
        }
    }

    // Getters

    @NotNull
    public List<DependencyNode> getRoots() {
        return new ArrayList<>(roots);
    }

    @NotNull
    public Collection<DependencyNode> getAllNodes() {
        return new ArrayList<>(nodeMap.values());
    }

    /**
     * 清空图
     */
    public void clear() {
        roots.clear();
        nodeMap.clear();
        visited.clear();
    }

    @Override
    public String toString() {
        return "DependencyGraph{" +
                "roots=" + roots.size() +
                ", totalNodes=" + getTotalNodes() +
                ", maxDepth=" + getMaxDepth() +
                '}';
    }
}
