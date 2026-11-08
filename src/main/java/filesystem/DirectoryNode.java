package filesystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import visitor.Visitor;

/** A directory owns zero or more named child elements. */
public final class DirectoryNode implements ParentAware {
    private final String name;
    private final List<Element> children = new ArrayList<>();
    private DirectoryNode parent;

    public DirectoryNode(String name) { this.name = FileNode.validateName(name); }

    @Override public String getName() { return name; }
    @Override public DirectoryNode getParent() { return parent; }
    @Override public void setParent(DirectoryNode parent) { this.parent = parent; }

    /** Adds a child while preserving unique names, one-parent ownership, and an acyclic tree. */
    public void add(Element element) {
        Objects.requireNonNull(element, "Element cannot be null");
        if (element == this) throw new IllegalArgumentException("A directory cannot contain itself");
        if (element.getParent() != null) throw new IllegalStateException("Element already belongs to a directory");
        for (Element child : children) {
            if (child.getName().equals(element.getName())) {
                throw new IllegalArgumentException("Duplicate sibling name: " + element.getName());
            }
        }
        ParentAware child = (ParentAware) element;
        if (child.containsDirectory(this)) throw new IllegalArgumentException("Adding this element would create a cycle");
        children.add(element);
        child.setParent(this);
    }

    /** Removes a direct child and releases its parent link. */
    public boolean remove(String childName) {
        for (int i = 0; i < children.size(); i++) {
            Element child = children.get(i);
            if (child.getName().equals(childName)) {
                children.remove(i);
                ((ParentAware) child).setParent(null);
                return true;
            }
        }
        return false;
    }

    @Override public boolean containsDirectory(DirectoryNode target) {
        if (this == target) return true;
        for (Element child : children) {
            if (((ParentAware) child).containsDirectory(target)) return true;
        }
        return false;
    }

    /** Returns an unmodifiable snapshot, never the writable internal collection. */
    public List<Element> getChildren() {
        return Collections.unmodifiableList(new ArrayList<>(children));
    }

    @Override public void accept(Visitor visitor) { Objects.requireNonNull(visitor).visit(this); }
    @Override public String getPath() { return parent == null ? name : parent.getPath() + "/" + name; }
}
