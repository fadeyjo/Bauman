from collections import defaultdict, deque

class ControlFlowGraph:
    def __init__(self, edges):
        self.edges = edges
        self.nodes = self._get_nodes()
        self.adj = self._build_adj_list()

    def _get_nodes(self):
        nodes = set()
        for u, v in self.edges:
            nodes.add(u)
            nodes.add(v)
        return nodes

    def _build_adj_list(self):
        adj = defaultdict(list)
        for u, v in self.edges:
            adj[u].append(v)
            adj[v].append(u)  # для поиска компонент (неориентированный обход)
        return adj

    def count_components(self):
        visited = set()
        components = 0

        for node in self.nodes:
            if node not in visited:
                components += 1
                queue = deque([node])
                while queue:
                    current = queue.popleft()
                    if current not in visited:
                        visited.add(current)
                        queue.extend(self.adj[current])
        return components

    def cyclomatic_complexity(self):
        E = len(self.edges)
        N = len(self.nodes)
        P = self.count_components()
        V = E - N + 2 * P
        return V, E, N, P


# =========================
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
    "A": edges_A,
    "Б": edges_B,
    "В": edges_C
}

for name, edges in graphs.items():
    cfg = ControlFlowGraph(edges)
    V, E, N, P = cfg.cyclomatic_complexity()
    print(f"Граф {name}:")
    print(f"  Вершины (N) = {N}")
    print(f"  Рёбра (E) = {E}")
    print(f"  Компоненты (P) = {P}")
    print(f"  Цикломатическая сложность V(G) = {V}")
    print()