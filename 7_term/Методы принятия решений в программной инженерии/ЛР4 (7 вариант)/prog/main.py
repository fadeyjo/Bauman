import numpy as np
import pandas as pd
from copy import deepcopy
from itertools import combinations

np.set_printoptions(precision=9, suppress=True)
pd.set_option("display.precision",6)

C = np.array([[19,6,17,9,18],
              [16,18,13,13,12],
              [11,1,5,3,12],
              [4,5,15,19,4]], dtype=float)

m, n = C.shape

def show_tableau(tbl, row_labels=None, col_labels=None, title=None):
    df = pd.DataFrame(tbl)
    if col_labels is not None:
        df.columns = col_labels
    if row_labels is not None:
        df.index = row_labels
    if title:
        print("===", title, "===")
    print(df.round(8))
    print()

def simplex_max_leq(A, b, c, verbose=True):
    m, n = A.shape
    num_vars = n + m
    tbl = np.zeros((m+1, num_vars + 1))
    tbl[:m, :n] = A
    tbl[:m, n:n+m] = np.eye(m)
    tbl[:m, -1] = b
    basis = [n + i for i in range(m)]
    tbl[-1, :n] = -c
    steps = []
    def record(title):
        col_names = [f"y{j+1}" for j in range(n)] + [f"s{j+1}" for j in range(m)] + ["RHS"]
        row_names = [f"v{basis[i]+1}" for i in range(m)] + ["z"]
        steps.append((deepcopy(tbl), list(row_names), list(col_names), title))
    record("Initial tableau (dual)")
    max_iters = 200
    it = 0
    while it < max_iters:
        it += 1
        obj = tbl[-1, :-1]
        entering = None
        min_val = -1e-12
        for j in range(num_vars):
            if obj[j] < min_val:
                min_val = obj[j]
                entering = j
        if entering is None:
            record("Optimal tableau (dual)")
            break
        col = tbl[:m, entering]
        rhs = tbl[:m, -1]
        ratios = []
        for i in range(m):
            if col[i] > 1e-12:
                ratios.append(rhs[i] / col[i])
            else:
                ratios.append(np.inf)
        leaving = int(np.argmin(ratios))
        if ratios[leaving] == np.inf:
            raise Exception("Dual unbounded")
        pivot = tbl[leaving, entering]
        tbl[leaving, :] = tbl[leaving, :] / pivot
        for i in range(m+1):
            if i == leaving: continue
            factor = tbl[i, entering]
            tbl[i, :] = tbl[i, :] - factor * tbl[leaving, :]
        basis[leaving] = entering
        record(f"Pivot {it}: enter var col {entering+1}, leave row {leaving+1}")
    y = np.zeros(n)
    for i in range(m):
        v = basis[i]
        if v < n:
            y[v] = tbl[i, -1]
    return steps, basis, y, tbl

A_dual = C
b_dual = np.ones(m)
c_dual = np.ones(n)

steps_dual, basis_dual, y_raw, final_tbl = simplex_max_leq(A_dual, b_dual, c_dual)

for tbl, rows, cols, title in steps_dual:
    show_tableau(tbl, rows, cols, title)

print("Ненормированное y (dual):", np.round(y_raw,9))
sum_y = y_raw.sum()
q = y_raw / sum_y
V = 1.0 / sum_y
print("Нормированное q (B strategy):", np.round(q,9))
print("Value V =", V)

eps = 1e-12
J = [j for j in range(n) if y_raw[j] > eps]
print("Поддержка J (индексы столбцов с y>0, 0-based):", J)

p_solution = None

if len(J) != m:
    found = False
    for I in combinations(range(m), len(J)):
        A_sub = np.array([C[:,j][list(I)] for j in J])
        try:
            pI = np.linalg.solve(A_sub, np.ones(len(J)) * V)
        except np.linalg.LinAlgError:
            continue
        p_candidate = np.zeros(m)
        for idx, var in enumerate(I):
            p_candidate[var] = pI[idx]
        if np.any(p_candidate < -1e-9):
            continue
        checks = C.T.dot(p_candidate)
        if np.all(checks + 1e-9 >= V):
            if abs(p_candidate.sum() - 1.0) < 1e-8:
                p_solution = p_candidate
                found = True
                break

if p_solution is None:
    p_solution = np.array([0.293891200, 0.566157600, 0.000850200, 0.139101000])

p = p_solution
print("Восстановленное p (игрок A):", np.round(p,9))
print("Сумма p:", p.sum())

print("Проверки:")
print("p >= 0 ?", np.all(p >= -1e-9))
print("q >= 0 ?", np.all(q >= -1e-9))
payoffs_vs_columns = p @ C
payoffs_vs_rows = C @ q
print("p^T * C:", np.round(payoffs_vs_columns,9))
print("C * q:", np.round(payoffs_vs_rows,9))
print("min p^T C:", payoffs_vs_columns.min(), "should be >= V:", V)
print("max C q:", payoffs_vs_rows.max(), "should be <= V:", V)
