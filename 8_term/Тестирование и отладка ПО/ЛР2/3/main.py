from itertools import product

# -----------------------------------------
# 1. Модель процедуры
# -----------------------------------------

def m(a, b, x):
    path = []

    # D1
    if (a > 0) and (b < 0):
        path.append("D1_T")
        x = x + 1
    else:
        path.append("D1_F")

    # D2
    if ((a == 2) or (x > 3)) and (b > -10):
        path.append("D2_T")
        x = x - 1
    else:
        path.append("D2_F")

    return x, tuple(path)


# -----------------------------------------
# 2. Цикломатическая сложность
# -----------------------------------------

def cyclomatic_complexity_edges_nodes():
    E = 7
    N = 6
    P = 1
    return E - N + 2 * P


def cyclomatic_complexity_predicates():
    D = 2
    return D + 1


def cyclomatic_complexity_regions():
    return 3


# -----------------------------------------
# 3. Поиск независимых путей
# -----------------------------------------

def find_independent_paths():
    test_values = {
        "a": [-1, 1, 2],
        "b": [-20, -5, 5],
        "x": [0, 4]
    }

    paths = {}

    for a, b, x in product(test_values["a"],
                            test_values["b"],
                            test_values["x"]):
        _, path = m(a, b, x)
        if path not in paths:
            paths[path] = (a, b, x)

    return paths


# -----------------------------------------
# 4. Запуск
# -----------------------------------------

print("Цикломатическая сложность:")
print("По формуле E-N+2P:", cyclomatic_complexity_edges_nodes())
print("По формуле D+1:", cyclomatic_complexity_predicates())
print("По числу областей:", cyclomatic_complexity_regions())

print("\nНезависимые пути и тесты:")

paths = find_independent_paths()

for i, (path, values) in enumerate(paths.items(), 1):
    print(f"P{i}: {path}")
    print(f"   Тест: a={values[0]}, b={values[1]}, x={values[2]}")