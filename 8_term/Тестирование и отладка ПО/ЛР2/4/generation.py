# Тестовое пространство
values = {
    "a": [-1, 1, 2],
    "b": [-20, -5, 5],
    "x": [0, 4]
}

all_tests = list(product(values["a"], values["b"], values["x"]))