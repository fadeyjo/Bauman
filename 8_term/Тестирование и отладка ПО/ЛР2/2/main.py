from collections import defaultdict

class ControlFlowGraph:
    def __init__(self, edges, start, end):
        self.edges = edges
        self.start = start
        self.end = end
        self.graph = self.build_graph()
        self.nodes = self.get_nodes()

    def build_graph(self):
        graph = defaultdict(list)
        for u, v in self.edges:
            graph[u].append(v)
        return graph

    def get_nodes(self):
        nodes = set()
        for u, v in self.edges:
            nodes.add(u)
            nodes.add(v)
        return nodes

    def cyclomatic_complexity(self):
        E = len(self.edges)
        N = len(self.nodes)
        P = 1  # один связный компонент
        return E - N + 2 * P

    # Поиск всех простых путей DFS
    def find_all_paths(self):
        paths = []

        def dfs(node, path):
            if node == self.end:
                paths.append(path[:])
                return
            for neighbor in self.graph[node]:
                if neighbor not in path:
                    dfs(neighbor, path + [neighbor])

        dfs(self.start, [self.start])
        return paths

    # Формирование независимых путей
    def find_independent_paths(self):
        all_paths = self.find_all_paths()
        independent = []
        used_edges = set()

        for path in all_paths:
            path_edges = set(zip(path, path[1:]))
            if not path_edges.issubset(used_edges):
                independent.append(path)
                used_edges.update(path_edges)

        return independent


# =============================
# Граф A
edges_A = [
    (1,2),(2,3),(3,4),(4,2),
    (2,5),(5,6),(6,7),(4,7),
    (5,8),(7,9),(8,9)
]

# Граф Б
edges_B = [
    (1,2),(2,3),(3,4),
    (2,5),(5,8),
    (3,6),(6,8),
    (4,7),(7,8),
    (7,9),(9,10),
    (8,10)
]

# Граф В
edges_C = [
    (1,2),(2,3),(3,4),
    (2,5),(5,6),(6,7),
    (7,8),(4,8),
    (8,9),(9,11),
    (10,3),(10,9)
]

graphs = {
    "A": (edges_A, 1, 9),
    "Б": (edges_B, 1, 10),
    "В": (edges_C, 1, 11)
}

for name, (edges, start, end) in graphs.items():
    cfg = ControlFlowGraph(edges, start, end)

    V = cfg.cyclomatic_complexity()
    independent_paths = cfg.find_independent_paths()

    print(f"\nГраф {name}")
    print(f"Цикломатическая сложность V(G) = {V}")
    print(f"Независимые пути ({len(independent_paths)}):")

    for i, path in enumerate(independent_paths, 1):
        print(f"P{i}: {' -> '.join(map(str, path))}")