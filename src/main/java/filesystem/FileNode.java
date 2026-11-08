package filesystem;

import java.util.Objects;
import visitor.Visitor;

/** A file stores its name and a non-negative size in bytes. */
public final class FileNode implements ParentAware {
    private final String name;
    private final long size;
    private DirectoryNode parent;

    public FileNode(String name, long size) {
        this.name = validateName(name);
        if (size < 0) throw new IllegalArgumentException("File size cannot be negative");
        this.size = size;
    }

    static String validateName(String name) {
        Objects.requireNonNull(name, "Name cannot be null");
        if (name.isBlank() || name.contains("/") || name.equals(".") || name.equals("..")) {
            throw new IllegalArgumentException("Name must be non-empty and cannot contain '/'");
        }
        return name;
    }

    @Override public String getName() { return name; }
    public long getSize() { return size; }
    @Override public DirectoryNode getParent() { return parent; }
    @Override public void setParent(DirectoryNode parent) { this.parent = parent; }
    @Override public boolean containsDirectory(DirectoryNode target) { return false; }
    @Override public void accept(Visitor visitor) { Objects.requireNonNull(visitor).visit(this); }
    @Override public String getPath() { return parent == null ? name : parent.getPath() + "/" + name; }
}
