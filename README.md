# Assignment 06 — Visitor Pattern: Advanced File System

**Language:** Java 17  
**Build:** Maven  
**Tests:** JUnit 5

## Project structure

```text
Assignment6/
├── pom.xml
├── README.md
└── src
    ├── main/java
    │   ├── Main.java
    │   ├── filesystem
    │   │   ├── Element.java
    │   │   ├── FileNode.java
    │   │   └── DirectoryNode.java
    │   └── visitor
    │       ├── Visitor.java
    │       ├── TotalSizeVisitor.java
    │       ├── SearchVisitor.java
    │       └── StatisticsVisitor.java
    └── test/java/VisitorTest.java
```

## Run from the project root

```bash
mvn clean test
mvn package
java -cp target/classes Main
```

## Demonstration output

```text
Total bytes: 1710
Search results (pdf, >= 400 bytes): [documents/report.pdf, projects/archive/old-report.pdf]
Statistics: Statistics[fileCount=4, directoryCount=4, maximumDepth=3]
```

## Design decisions

- `Element` is the common abstraction. `FileNode` and `DirectoryNode` are concrete elements.
- Each element's `accept(Visitor)` calls the matching overloaded `visit` method. This is double dispatch: the call first enters `accept`, then Java selects the overload using the concrete element type.
- Visitors own traversal. `visit(DirectoryNode)` visits each child by calling `accept(this)`. Thus each reachable node is visited once per operation.
- Search paths are relative to the selected root; the root directory name is omitted. `/` is the separator and results are sorted lexicographically.
- Statistics count the selected root directory and define its depth as zero. A file directly inside the root has depth one.
- Public visitor operations reset their accumulated state before each run, so visitor objects can be reused.
- Directory children are returned as an unmodifiable snapshot. `add` and `remove` maintain unique sibling names, one-parent ownership, and cycle prevention. Parent mutation is kept in the package-private `ParentAware` contract rather than the public `Element` API.

## Automated tests

`VisitorTest` contains eight JUnit 5 tests for: overloaded dispatch, nested size accumulation, combined search conditions, root-relative sorted paths, statistics and depth, empty/no-match cases, repeat execution, and invalid tree mutations.

## Five Clean Code practices

1. **Meaningful names:** `TotalSizeVisitor.calculate`, `SearchVisitor.search`, and `StatisticsVisitor.collect` describe their actions.
2. **Single Responsibility:** file nodes store file data; each visitor implements one independent operation.
3. **Small methods:** `DirectoryNode.remove` only removes one direct child and releases its parent link.
4. **Encapsulation:** `getChildren` returns an unmodifiable copy instead of the internal writable list.
5. **Avoiding type-checking chains:** visitor operation selection uses overloaded `accept`/`visit` methods instead of `instanceof` checks in visitor classes.

## Extension trade-off

A new operation can be added as another `Visitor` without modifying `FileNode`, `DirectoryNode`, or their `accept` methods. However, adding a new element type requires a new overload in `Visitor` and an implementation of that overload in every concrete visitor.
