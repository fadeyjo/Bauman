from collections import defaultdict

class FlowGraph:
    def __init__(self, edges):
        self.edges = edges
        self.nodes = set()
        
        for u, v in edges:
            self.nodes.add(u)
            self.nodes.add(v)

    def cyclomatic_complexity(self, components=1):
        E = len(self.edges)
        N = len(self.nodes)
        P = components
        
        V = E - N + 2 * P
        return V

# =====================
# Граф из рисунка 5
# =====================

edges_5 = [
    (1,2),(2,3),(3,4),(4,5),
    (5,6),(6,7),(7,8),(8,9),(9,10),
    (4,2),
    (8,6),
    (9,5)
]

graph = FlowGraph(edges_5)

V = graph.cyclomatic_complexity()

print("Количество вершин:", len(graph.nodes))
print("Количество рёбер:", len(edges_5))
print("Цикломатическая сложность:", V)
print("Необходимое количество наборов тестов:", V)