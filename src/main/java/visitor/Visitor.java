package visitor;

import filesystem.DirectoryNode;
import filesystem.FileNode;

/** Visitor contract for the two supported file-system element types. */
public interface Visitor {
    void visit(FileNode file);
    void visit(DirectoryNode directory);
}
