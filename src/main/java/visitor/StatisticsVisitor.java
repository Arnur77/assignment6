package visitor;

import filesystem.DirectoryNode;
import filesystem.Element;
import filesystem.FileNode;

/** Counts files/directories and records the greatest depth; selected root is depth zero. */
public final class StatisticsVisitor implements Visitor {
    public record Statistics(int fileCount, int directoryCount, int maximumDepth) { }

    private int fileCount;
    private int directoryCount;
    private int maximumDepth;
    private int currentDepth;

    public Statistics collect(Element root) {
        fileCount = 0;
        directoryCount = 0;
        maximumDepth = 0;
        currentDepth = 0;
        root.accept(this);
        return new Statistics(fileCount, directoryCount, maximumDepth);
    }

    @Override public void visit(FileNode file) {
        fileCount++;
        maximumDepth = Math.max(maximumDepth, currentDepth);
    }

    @Override public void visit(DirectoryNode directory) {
        directoryCount++;
        maximumDepth = Math.max(maximumDepth, currentDepth);
        currentDepth++;
        for (Element child : directory.getChildren()) child.accept(this);
        currentDepth--;
    }
}
