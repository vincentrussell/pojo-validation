package com.github.vincentrussell.validation.tree;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.LinkedList;
import java.util.List;

/**
 * Class that is used to represent a tree structure.
 *
 * @param <T> the type of the leaf elements on this tree.
 */
public final class TreeNode<T> {

    private final T data;
    private TreeNode<T> parent;
    private final List<TreeNode<T>> children;
    private long depth;

    /**
     * Default constructor that takes the data.
     *
     * @param data trunk node for this tree
     */
    public TreeNode(final T data) {
        this.data = data;
        this.children = new LinkedList<>();
        this.depth = 0;
    }

    /**
     * Get the depth.
     *
     * @return get the depth.
     */
    public long getDepth() {
        return depth;
    }

    private long getChildrenDepth() {
        if (children.isEmpty()) {
            return 0;
        }

        return children.stream().reduce(Long.valueOf(0), (integer, tTreeNode) -> integer + tTreeNode.getDepth(),
                (integer, integer2) -> integer + integer2);
    }

    private void incrementDepth() {
        depth = depth + (parent != null ? parent.getDepth() : 0) + getChildrenDepth() + 1;
    }

    /**
     * Gets the parent node of this node.  Will return null if this is the root node.
     *
     * @return the parent {@link com.github.vincentrussell.validation.tree.TreeNode}.
     */
    public TreeNode<T> getParent() {
        return parent;
    }

    /**
     * Get the data associated with this {@link com.github.vincentrussell.validation.tree.TreeNode}.
     *
     * @return the data that was passed in.
     */
    public T getData() {
        return data;
    }

    /**
     * get the children for this {@link com.github.vincentrussell.validation.tree.TreeNode}.
     *
     * @return a list of {@link com.github.vincentrussell.validation.tree.TreeNode}s.
     */
    public List<TreeNode<T>> getChildren() {
        return children;
    }

    /**
     * add a child to this tree structure.
     *
     * @param childNode the child {@link com.github.vincentrussell.validation.tree.TreeNode}
     * @return the child node that is passed in.
     */
    public TreeNode<T> addChild(final TreeNode<T> childNode) {
        childNode.parent = this;
        childNode.parent.incrementDepth();
        this.children.add(childNode);
        return childNode;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE).
                append("data", data.toString()).
                toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        TreeNode rhs = (TreeNode) obj;
        return new EqualsBuilder()
                .appendSuper(super.equals(obj))
                .append(data, rhs.data)
                .isEquals();
    }

    @Override
    @SuppressWarnings("checkstyle:magicnumber")
    public int hashCode() {
        return new HashCodeBuilder(3, 49).
                append(data).
                toHashCode();
    }

}
