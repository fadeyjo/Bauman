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