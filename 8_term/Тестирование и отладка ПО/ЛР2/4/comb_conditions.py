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