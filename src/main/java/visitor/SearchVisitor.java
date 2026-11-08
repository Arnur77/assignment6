package visitor;

import filesystem.DirectoryNode;
import filesystem.Element;
import filesystem.FileNode;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/** Searches files using both a filename criterion and an inclusive minimum size. */
public final class SearchVisitor implements Visitor {
    private final Predicate<String> filenameCriterion;
    private final long minimumSize;
    private final List<String> matches = new ArrayList<>();
    private final Deque<String> directoryPath = new ArrayDeque<>();
    private int directoryDepth;

    public SearchVisitor(Predicate<String> filenameCriterion, long minimumSize) {
        this.filenameCriterion = Objects.requireNonNull(filenameCriterion, "Filename criterion cannot be null");
        if (minimumSize < 0) throw new IllegalArgumentException("Minimum size cannot be negative");
        this.minimumSize = minimumSize;
    }

    /** Convenience constructor: filename must contain the supplied text. */
    public SearchVisitor(String filenameContains, long minimumSize) {
        this(name -> name.contains(Objects.requireNonNull(filenameContains)), minimumSize);
    }

    /** Results are relative to root, sorted lexicographically, and use '/' separators. */
    public List<String> search(Element root) {
        Objects.requireNonNull(root, "Root cannot be null");
        matches.clear();
        directoryPath.clear();
        directoryDepth = 0;
        root.accept(this);
        Collections.sort(matches);
        return List.copyOf(matches);
    }

    @Override public void visit(FileNode file) {
        if (file.getSize() >= minimumSize && filenameCriterion.test(file.getName())) {
            String prefix = String.join("/", directoryPath);
            matches.add(prefix.isEmpty() ? file.getName() : prefix + "/" + file.getName());
        }
    }

    @Override public void visit(DirectoryNode directory) {
        if (directoryDepth > 0) directoryPath.addLast(directory.getName());
        directoryDepth++;
        for (Element child : directory.getChildren()) child.accept(this);
        directoryDepth--;
        if (directoryDepth > 0) directoryPath.removeLast();
    }
}
