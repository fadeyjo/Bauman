from itertools import product

# -----------------------------------------
# Модель процедуры с трассировкой
# -----------------------------------------

def m(a, b, x):
    trace = {
        "D1": None,
        "D2": None,
        "C1": a > 0,
        "C2": b < 0,
        "C3": a == 2,
        "C4": x > 3,
        "C5": b > -10,
        "S1": False,
        "S2": False
    }

    # D1
    if (trace["C1"]) and (trace["C2"]):
        trace["D1"] = True
        x = x + 1
        trace["S1"] = True
    else:
        trace["D1"] = False

    # D2
    if ((trace["C3"]) or (trace["C4"])) and (trace["C5"]):
        trace["D2"] = True
        x = x - 1
        trace["S2"] = True
    else:
        trace["D2"] = False

    return x, trace

# Тестовое пространство
values = {
    "a": [-1, 1, 2],
    "b": [-20, -5, 5],
    "x": [0, 4]
}

all_tests = list(product(values["a"], values["b"], values["x"]))

def statement_coverage():
    covered_S1 = False
    covered_S2 = False
    tests = []

    for a,b,x in all_tests:
        _, trace = m(a,b,x)

        if trace["S1"] and not covered_S1:
            covered_S1 = True
            tests.append((a,b,x))

        if trace["S2"] and not covered_S2:
            covered_S2 = True
            tests.append((a,b,x))

        if covered_S1 and covered_S2:
            break

    return tests

def decision_coverage():
    outcomes = set()
    tests = []

    for a,b,x in all_tests:
        _, trace = m(a,b,x)
        key = (trace["D1"], trace["D2"])

        if key not in outcomes:
            outcomes.add(key)
            tests.append((a,b,x))

        if len(outcomes) == 4:
            break

    return tests

def condition_coverage():
    coverage = {c:set() for c in ["C1","C2","C3","C4","C5"]}
    tests = []

    for a,b,x in all_tests:
        _, trace = m(a,b,x)

        new = False
        for c in coverage:
            if trace[c] not in coverage[c]:
                coverage[c].add(trace[c])
                new = True

        if new:
            tests.append((a,b,x))

        if all(len(v)==2 for v in coverage.values()):
            break

    return tests

def decision_condition_coverage():
    tests = set(statement_coverage())
    tests.update(decision_coverage())
    tests.update(condition_coverage())
    return list(tests)

def combinatorial_coverage():
    combos_D1 = set()
    combos_D2 = set()
    tests = []

    for a,b,x in all_tests:
        _, trace = m(a,b,x)

        c1 = (trace["C1"], trace["C2"])
        c2 = (trace["C3"], trace["C4"], trace["C5"])

        new = False
        if c1 not in combos_D1:
            combos_D1.add(c1)
            new = True

        if c2 not in combos_D2:
            combos_D2.add(c2)
            new = True

        if new:
            tests.append((a,b,x))

        if len(combos_D1)==4 and len(combos_D2)==8:
            break

    return tests

print("Statement coverage:", statement_coverage())
print("Decision coverage:", decision_coverage())
print("Condition coverage:", condition_coverage())
print("Decision/Condition coverage:", decision_condition_coverage())
print("Combinatorial coverage:", combinatorial_coverage())