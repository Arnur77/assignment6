import filesystem.DirectoryNode;
import filesystem.Element;
import filesystem.FileNode;
import org.junit.jupiter.api.Test;
import visitor.SearchVisitor;
import visitor.StatisticsVisitor;
import visitor.TotalSizeVisitor;
import visitor.Visitor;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class VisitorTest {
    private DirectoryNode sampleTree() {
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode docs = new DirectoryNode("docs");
        DirectoryNode deep = new DirectoryNode("deep");
        root.add(docs);
        docs.add(new FileNode("a.txt", 100));
        docs.add(deep);
        deep.add(new FileNode("b.pdf", 500));
        root.add(new FileNode("c.pdf", 500));
        return root;
    }

    @Test void bothElementTypesDispatchToTheirMatchingVisitMethods() {
        int[] calls = {0, 0};
        Visitor visitor = new Visitor() {
            @Override public void visit(FileNode file) { calls[0]++; }
            @Override public void visit(DirectoryNode directory) { calls[1]++; }
        };
        new FileNode("x.txt", 1).accept(visitor);
        new DirectoryNode("folder").accept(visitor);
        assertArrayEquals(new int[]{1, 1}, calls);
    }

    @Test void totalSizeIncludesNestedFilesExactlyOnce() {
        assertEquals(1100, new TotalSizeVisitor().calculate(sampleTree()));
    }

    @Test void searchCombinesFilenameAndInclusiveSizeConditions() {
        assertEquals(List.of("c.pdf", "docs/deep/b.pdf"),
                new SearchVisitor(name -> name.endsWith(".pdf"), 500).search(sampleTree()));
    }

    @Test void searchPathsAreRootRelativeAndLexicographicallySorted() {
        DirectoryNode root = new DirectoryNode("base");
        DirectoryNode nested = new DirectoryNode("nested");
        root.add(new FileNode("z.txt", 5));
        root.add(nested);
        nested.add(new FileNode("a.txt", 5));
        assertEquals(List.of("nested/a.txt", "z.txt"), new SearchVisitor(name -> true, 0).search(root));
    }

    @Test void statisticsCountFilesDirectoriesAndMaximumDepth() {
        assertEquals(new StatisticsVisitor.Statistics(3, 3, 3),
                new StatisticsVisitor().collect(sampleTree()));
    }

    @Test void emptyDirectoryAndNoMatchSearchAreHandled() {
        DirectoryNode empty = new DirectoryNode("empty");
        assertEquals(0, new TotalSizeVisitor().calculate(empty));
        assertEquals(List.of(), new SearchVisitor(name -> true, 0).search(empty));
        assertEquals(new StatisticsVisitor.Statistics(0, 1, 0), new StatisticsVisitor().collect(empty));
        assertEquals(List.of(), new SearchVisitor(name -> name.endsWith(".docx"), 0).search(sampleTree()));
    }

    @Test void repeatedExecutionsDoNotLeakVisitorState() {
        DirectoryNode root = sampleTree();
        TotalSizeVisitor sizeVisitor = new TotalSizeVisitor();
        assertEquals(1100, sizeVisitor.calculate(root));
        assertEquals(1100, sizeVisitor.calculate(root));
        SearchVisitor searchVisitor = new SearchVisitor(name -> true, 0);
        assertEquals(3, searchVisitor.search(root).size());
        assertEquals(3, searchVisitor.search(root).size());
        StatisticsVisitor statisticsVisitor = new StatisticsVisitor();
        assertEquals(new StatisticsVisitor.Statistics(3, 3, 3), statisticsVisitor.collect(root));
        assertEquals(new StatisticsVisitor.Statistics(3, 3, 3), statisticsVisitor.collect(root));
    }

    @Test void invalidMutationsAreRejectedAndValidNestingIsSupported() {
        assertThrows(IllegalArgumentException.class, () -> new FileNode("bad.txt", -1));
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode child = new DirectoryNode("child");
        root.add(child);
        assertThrows(IllegalArgumentException.class, () -> root.add(new FileNode("child", 1)));
        assertThrows(IllegalStateException.class, () -> new DirectoryNode("other").add(child));
        assertThrows(IllegalArgumentException.class, () -> child.add(root));
        assertTrue(root.remove("child"));
        DirectoryNode other = new DirectoryNode("other");
        other.add(child);
        assertSame(other, child.getParent());
    }
}
