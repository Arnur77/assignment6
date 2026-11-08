import filesystem.DirectoryNode;
import filesystem.FileNode;
import visitor.SearchVisitor;
import visitor.StatisticsVisitor;
import visitor.TotalSizeVisitor;

/** Deterministic demonstration using a small sample file tree. */
public class Main {
    public static void main(String[] args) {
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode documents = new DirectoryNode("documents");
        DirectoryNode projects = new DirectoryNode("projects");
        DirectoryNode archive = new DirectoryNode("archive");

        root.add(documents);
        root.add(projects);
        documents.add(new FileNode("notes.txt", 120));
        documents.add(new FileNode("report.pdf", 850));
        projects.add(new FileNode("Visitor.java", 240));
        projects.add(archive);
        archive.add(new FileNode("old-report.pdf", 500));

        System.out.println("Total bytes: " + new TotalSizeVisitor().calculate(root));
        System.out.println("Search results (pdf, >= 400 bytes): "
                + new SearchVisitor(name -> name.endsWith(".pdf"), 400).search(root));
        System.out.println("Statistics: " + new StatisticsVisitor().collect(root));
    }
}
