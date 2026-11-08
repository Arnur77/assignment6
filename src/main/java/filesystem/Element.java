package filesystem;

import visitor.Visitor;

/** Public read-only abstraction shared by files and directories. */
public interface Element {
    String getName();
    DirectoryNode getParent();
    void accept(Visitor visitor);
    String getPath();
}
