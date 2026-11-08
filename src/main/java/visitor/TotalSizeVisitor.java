package visitor;

import filesystem.DirectoryNode;
import filesystem.Element;
import filesystem.FileNode;

/** Sums file sizes. Directories contribute no size of their own. */
public final class TotalSizeVisitor implements Visitor {
    private long total;

    public long calculate(Element root) {
        total = 0;
        root.accept(this);
        return total;
    }

    @Override public void visit(FileNode file) { total += file.getSize(); }

    @Override public void visit(DirectoryNode directory) {
        for (Element child : directory.getChildren()) child.accept(this);
    }
}
