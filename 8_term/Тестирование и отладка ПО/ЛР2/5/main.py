from collections import defaultdict

class FlowGraph:
    def __init__(self, edges):
        self.graph = defaultdict(list)
        self.rev_graph = defaultdict(list)
        self.nodes = set()

        for u, v in edges:
            self.graph[u].append(v)
            self.rev_graph[v].append(u)
            self.nodes.add(u)
            self.nodes.add(v)

    # Поиск всех простых циклов (DFS)
    def find_cycles(self):
        visited = set()
        stack = []
        cycles = []

        def dfs(node, start):
            visited.add(node)
            stack.append(node)

            for neighbor in self.graph[node]:
                if neighbor == start:
                    cycles.append(stack.copy())
                elif neighbor not in stack:
                    dfs(neighbor, start)

            stack.pop()

        for node in self.nodes:
            dfs(node, node)

        # Убираем дубликаты циклов
        unique = []
        for c in cycles:
            c_sorted = sorted(c)
            if c_sorted not in unique:
                unique.append(c_sorted)

        return unique

    # Классификация цикла
    def classify_cycle(self, cycle):
        entry_points = 0
        cycle_set = set(cycle)

        for node in cycle:
            for pred in self.rev_graph[node]:
                if pred not in cycle_set:
                    entry_points += 1

        if entry_points == 1:
            return "Естественный цикл (один вход)"
        elif entry_points > 1:
            return "Неструктурированный цикл (несколько входов)"
        else:
            return "Вложенный или внутренний цикл"

    def analyze(self):
        cycles = self.find_cycles()

        if not cycles:
            print("Циклы отсутствуют")
            return

        for i, cycle in enumerate(cycles, 1):
            print(f"\nЦикл {i}: {cycle}")
            print("Тип:", self.classify_cycle(cycle))


# ===========================
# ВЫБЕРИТЕ НУЖНЫЙ ГРАФ
# ===========================

edges_5 = [
    (1,2),(2,3),(3,4),(4,5),
    (5,6),(6,7),(7,8),(8,9),(9,10),
    (4,2),
    (8,6),
    (9,5)
]

graph = FlowGraph(edges_5)
graph.analyze()