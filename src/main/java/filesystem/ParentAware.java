package filesystem;

/** Package-private tree-maintenance contract; not exposed to application clients. */
interface ParentAware extends Element {
    void setParent(DirectoryNode parent);
    boolean containsDirectory(DirectoryNode target);
}
