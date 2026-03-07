import pandas as pd
import networkx as nx
import matplotlib.pyplot as plt

# Создание потокового графа
G = nx.DiGraph()

# Узлы графа
nodes = {
    "Start": "Начало",
    "A": "a > 0 ?",
    "B": "b < 0 ?",
    "C": "x := x + 1",
    "D": "x := x * 2",
    "E": "(a > 2) or (x = 0) ?",
    "F": "x := x + 1",
    "End": "Конец"
}

G.add_nodes_from(nodes.keys())

# Рёбра
edges = [
    ("Start", "A"),
    ("A", "B"),
    ("A", "E"),
    ("B", "C"),
    ("B", "D"),
    ("C", "E"),
    ("D", "E"),
    ("E", "F"),
    ("E", "End"),
    ("F", "End")
]

G.add_edges_from(edges)

# Позиции узлов
pos = {
    "Start": (0, 4),
    "A": (0, 3),
    "B": (-1.5, 2),
    "C": (-1.5, 1),
    "D": (1.5, 1),
    "E": (0, 0),
    "F": (0, -1),
    "End": (0, -2)
}

# Рисуем граф
plt.figure(figsize=(12, 10))
nx.draw(G, pos, with_labels=True, labels=nodes,
        node_size=4000, node_color='lightgreen',
        font_size=10, font_weight='bold', arrows=True)
plt.title("Потоковый граф процедуры m(a, b: real; var x: real)")
plt.show()

# ────────────────────────────────────────────────
# Цикломатическая сложность

E = G.number_of_edges()   # 10
N = G.number_of_nodes()   # 8
p = 3                     # A, B, E

V1 = 4                    # регионы
V2 = E - N + 2
V3 = p + 1

results = {
    "Метод 1 (регионы)": V1,
    "Метод 2 (E - N + 2)": V2,
    "Метод 3 (p + 1)": V3
}

df_results = pd.DataFrame([results])

# ────────────────────────────────────────────────
# Тестовые наборы 

test_cases = [
    {"a": 5,   "b": -3,  "x": 10, "Ожидаемый результат": "x = x + 1 + 1"},
    {"a": 5,   "b": -3,  "x": 0,  "Ожидаемый результат": "x = x + 1 + 1"},
    {"a": 1,   "b": 10,  "x": 8,  "Ожидаемый результат": "x = x * 2 + 1"},
    {"a": 4,   "b": 1,   "x": 7,  "Ожидаемый результат": "x = x * 2"},
    {"a": -1,  "b": 5,   "x": 3,  "Ожидаемый результат": "x без изменений"},
    {"a": 0,   "b": 0,   "x": 0,  "Ожидаемый результат": "x = x + 1"}
]

df_tests = pd.DataFrame(test_cases)

# ────────────────────────────────────────────────
# Вывод 

print("Цикломатическая сложность процедуры\n", df_results)
print("Тестовые наборы по критерию покрытия маршрутов\n", df_tests)