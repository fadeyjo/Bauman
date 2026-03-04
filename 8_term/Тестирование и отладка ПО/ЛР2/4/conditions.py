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