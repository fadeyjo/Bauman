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