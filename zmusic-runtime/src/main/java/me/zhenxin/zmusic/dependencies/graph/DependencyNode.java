package me.zhenxin.zmusic.dependencies.graph;

import me.zhenxin.zmusic.dependencies.DependencyScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 依赖树节点
 * 表示依赖关系树中的一个依赖项
 *
 * @author ZMusic Team
 * @since 4.0.0
 */
public class DependencyNode {

    private final String groupId;
    private final String artifactId;
    private final String version;
    private final DependencyScope scope;
    private final boolean optional;
    private final int depth;

    private final List<DependencyNode> children = new ArrayList<>();
    private DependencyNode parent;

    /**
     * 创建依赖节点
     *
     * @param groupId    组 ID
     * @param artifactId 构件 ID
     * @param version    版本
     * @param scope      作用域
     * @param optional   是否可选
     * @param depth      深度（从根节点开始为 0）
     */
    public DependencyNode(@NotNull String groupId, @NotNull String artifactId, @NotNull String version,
                          @Nullable DependencyScope scope, boolean optional, int depth) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.scope = scope;
        this.optional = optional;
        this.depth = depth;
    }

    /**
     * 从 Maven 坐标创建节点
     *
     * @param coordinate Maven 坐标（格式: groupId:artifactId:version）
     * @param scope      作用域
     * @param optional   是否可选
     * @param depth      深度
     * @return 依赖节点
     */
    @NotNull
    public static DependencyNode fromCoordinate(@NotNull String coordinate, @Nullable DependencyScope scope,
                                                boolean optional, int depth) {
        String[] parts = coordinate.split(":");
        if (parts.length < 3) {
            throw new IllegalArgumentException("Invalid Maven coordinate: " + coordinate);
        }

        return new DependencyNode(parts[0], parts[1], parts[2], scope, optional, depth);
    }

    /**
     * 添加子节点
     *
     * @param child 子节点
     */
    public void addChild(@NotNull DependencyNode child) {
        children.add(child);
        child.parent = this;
    }

    /**
     * 移除子节点
     *
     * @param child 子节点
     */
    public void removeChild(@NotNull DependencyNode child) {
        children.remove(child);
        child.parent = null;
    }

    /**
     * 获取 Maven 坐标
     *
     * @return groupId:artifactId:version
     */
    @NotNull
    public String getCoordinate() {
        return groupId + ":" + artifactId + ":" + version;
    }

    /**
     * 获取简短坐标（不含版本）
     *
     * @return groupId:artifactId
     */
    @NotNull
    public String getShortCoordinate() {
        return groupId + ":" + artifactId;
    }

    /**
     * 检查是否为根节点
     */
    public boolean isRoot() {
        return parent == null;
    }

    /**
     * 检查是否为叶子节点
     */
    public boolean isLeaf() {
        return children.isEmpty();
    }

    /**
     * 获取所有祖先节点（从父节点到根节点）
     */
    @NotNull
    public List<DependencyNode> getAncestors() {
        List<DependencyNode> ancestors = new ArrayList<>();
        DependencyNode current = parent;
        while (current != null) {
            ancestors.add(current);
            current = current.parent;
        }
        return ancestors;
    }

    /**
     * 获取路径（从根节点到当前节点）
     */
    @NotNull
    public List<DependencyNode> getPath() {
        List<DependencyNode> path = new ArrayList<>();
        DependencyNode current = this;
        while (current != null) {
            path.add(0, current);
            current = current.parent;
        }
        return path;
    }

    /**
     * 计算子树大小（包括自己）
     */
    public int getSubtreeSize() {
        int size = 1;
        for (DependencyNode child : children) {
            size += child.getSubtreeSize();
        }
        return size;
    }

    /**
     * 遍历子树（深度优先）
     *
     * @param visitor 访问器
     */
    public void traverse(@NotNull NodeVisitor visitor) {
        visitor.visit(this);
        for (DependencyNode child : children) {
            child.traverse(visitor);
        }
    }

    // Getters

    @NotNull
    public String getGroupId() {
        return groupId;
    }

    @NotNull
    public String getArtifactId() {
        return artifactId;
    }

    @NotNull
    public String getVersion() {
        return version;
    }

    @Nullable
    public DependencyScope getScope() {
        return scope;
    }

    public boolean isOptional() {
        return optional;
    }

    public int getDepth() {
        return depth;
    }

    @NotNull
    public List<DependencyNode> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @Nullable
    public DependencyNode getParent() {
        return parent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DependencyNode that = (DependencyNode) o;
        return Objects.equals(groupId, that.groupId) &&
                Objects.equals(artifactId, that.artifactId) &&
                Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, artifactId, version);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getCoordinate());
        if (scope != null) {
            sb.append(" (").append(scope).append(")");
        }
        if (optional) {
            sb.append(" [optional]");
        }
        return sb.toString();
    }

    /**
     * 节点访问器接口
     */
    public interface NodeVisitor {
        void visit(DependencyNode node);
    }
}
