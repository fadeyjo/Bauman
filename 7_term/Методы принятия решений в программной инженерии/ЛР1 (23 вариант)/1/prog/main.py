import math
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt

class FuncCounter:
    def __init__(self, f):
        self._f = f
        self.count = 0
    def __call__(self, x):
        self.count += 1
        return self._f(x)
    def reset(self):
        self.count = 0

def f_raw(x):
    return x**3 - math.exp(x)

def history_to_df(history):
    df = pd.DataFrame(history)
    lengths = df['length'].values
    ratios = [np.nan]
    for i in range(1, len(lengths)):
        prev = lengths[i-1]
        curr = lengths[i]
        ratios.append(prev / curr if curr != 0 else np.nan)
    df['ratio_prev_to_curr'] = ratios
    cols = ['k', 'a', 'b', 'length', 'ratio_prev_to_curr',
            'x_test1', 'f_x_test1', 'x_test2', 'f_x_test2', 'cumulative_f_calls']
    cols_present = [c for c in cols if c in df.columns]
    return df[cols_present]

def dichotomy_with_history(a, b, eps, f, delta=None):
    if delta is None:
        delta = eps / 4.0
    history = []
    while (b - a) > eps:
        xm = (a + b) / 2.0
        x1 = xm - delta
        x2 = xm + delta
        f1 = f(x1)
        f2 = f(x2)
        if f1 < f2:
            b = x2
        else:
            a = x1
        length = b - a
        history.append({
            'k': len(history) + 1,
            'a': a, 'b': b, 'length': length,
            'x_test1': x1, 'f_x_test1': f1,
            'x_test2': x2, 'f_x_test2': f2,
            'cumulative_f_calls': f.count
        })
    x_min = (a + b) / 2.0
    return x_min, f(x_min), history

def golden_with_history(a, b, eps, f):
    phi = (math.sqrt(5) - 1) / 2.0
    x1 = b - phi * (b - a)
    x2 = a + phi * (b - a)
    f1 = f(x1)
    f2 = f(x2)
    history = []
    while (b - a) > eps:
        if f1 < f2:
            b = x2
            x2 = x1
            f2 = f1
            x1 = b - phi * (b - a)
            f1 = f(x1)
        else:
            a = x1
            x1 = x2
            f1 = f2
            x2 = a + phi * (b - a)
            f2 = f(x2)
        length = b - a
        history.append({
            'k': len(history) + 1,
            'a': a, 'b': b, 'length': length,
            'x_test1': x1, 'f_x_test1': f1,
            'x_test2': x2, 'f_x_test2': f2,
            'cumulative_f_calls': f.count
        })
    x_min = (a + b) / 2.0
    return x_min, f(x_min), history

def fibonacci_with_history(a, b, eps, f):
    L0 = b - a
    F = [1, 1]
    while F[-1] < L0 / eps:
        F.append(F[-1] + F[-2])
    n = len(F)
    history = []
    if n < 3:
        x_mid = (a + b) / 2.0
        val = f(x_mid)
        history.append({
            'k': 1, 'a': a, 'b': b, 'length': b-a,
            'x_test1': x_mid, 'f_x_test1': val,
            'x_test2': np.nan, 'f_x_test2': np.nan,
            'cumulative_f_calls': f.count
        })
        return x_mid, val, history
    x1 = a + (F[-3] / F[-1]) * (b - a)
    x2 = a + (F[-2] / F[-1]) * (b - a)
    f1 = f(x1)
    f2 = f(x2)
    k = 1
    while k <= n - 2 and (b - a) > eps:
        if f1 < f2:
            b = x2
            x2 = x1
            f2 = f1
            idx = n - k - 3
            if idx >= 0:
                x1 = a + (F[idx] / F[n - k - 1]) * (b - a)
                f1 = f(x1)
            else:
                x1 = (a + b) / 2.0
                f1 = f(x1)
        else:
            a = x1
            x1 = x2
            f1 = f2
            idx = n - k - 2
            if idx >= 0:
                x2 = a + (F[idx] / F[n - k - 1]) * (b - a)
                f2 = f(x2)
            else:
                x2 = (a + b) / 2.0
                f2 = f(x2)
        length = b - a
        history.append({
            'k': k,
            'a': a, 'b': b, 'length': length,
            'x_test1': x1, 'f_x_test1': f1,
            'x_test2': x2, 'f_x_test2': f2,
            'cumulative_f_calls': f.count
        })
        k += 1
    x_min = (a + b) / 2.0
    return x_min, f(x_min), history

def main():
    a0 = -1.0
    b0 = 0.0
    eps0 = 1e-6
    fc = FuncCounter(f_raw)
    x_d, fx_d, hist_d = dichotomy_with_history(a0, b0, eps0, fc)
    df_d = history_to_df(hist_d)
    df_d.to_csv("dichotomy_history.csv", sep=";", index=False)
    print(f"Дихотомия: x_min = {x_d:.12f}, f = {fx_d:.12e}, f-evals = {fc.count}")
    print(f"Таблица итераций сохранена: dichotomy_history.csv (строк {len(df_d)})")
    fc = FuncCounter(f_raw)
    x_g, fx_g, hist_g = golden_with_history(a0, b0, eps0, fc)
    df_g = history_to_df(hist_g)
    df_g.to_csv("golden_history.csv", sep=";", index=False)
    print(f"Золотое сечение: x_min = {x_g:.12f}, f = {fx_g:.12e}, f-evals = {fc.count}")
    print(f"Таблица итераций сохранена: golden_history.csv (строк {len(df_g)})")
    fc = FuncCounter(f_raw)
    x_f, fx_f, hist_f = fibonacci_with_history(a0, b0, eps0, fc)
    df_f = history_to_df(hist_f)
    df_f.to_csv("fibonacci_history.csv", sep=";", index=False)
    print(f"Фибоначчи: x_min = {x_f:.12f}, f = {fx_f:.12e}, f-evals = {fc.count}")
    print(f"Таблица итераций сохранена: fibonacci_history.csv (строк {len(df_f)})")
    print("\nПервые 6 итераций (Дихотомия):")
    print(df_d.head(6).to_string(index=False))
    print("\nПервые 6 итераций (Золотое сечение):")
    print(df_g.head(6).to_string(index=False))
    print("\nПервые 6 итераций (Фибоначчи):")
    print(df_f.head(6).to_string(index=False))
    plt.figure(figsize=(8,5))
    plt.plot(df_d['k'], df_d['length'], marker='o', label='Дихотомия')
    plt.plot(df_g['k'], df_g['length'], marker='s', label='Золотое сечение')
    plt.plot(df_f['k'], df_f['length'], marker='^', label='Фибоначчи')
    plt.yscale('log')
    plt.xlabel('k (итерация)')
    plt.ylabel('Длина интервала (log scale)')
    plt.title('Длина интервала по итерациям (eps = {})'.format(eps0))
    plt.grid(True)
    plt.legend()
    plt.tight_layout()
    plt.savefig("interval_lengths_by_iteration.png")
    print("\nГрафик длины интервала сохранён: interval_lengths_by_iteration.png")

if __name__ == "__main__":
    main()
