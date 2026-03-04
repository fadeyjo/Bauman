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