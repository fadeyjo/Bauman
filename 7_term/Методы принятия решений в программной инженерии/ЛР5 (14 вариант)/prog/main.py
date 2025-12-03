import numpy as np
import pandas as pd
import matplotlib.pyplot as plt

A = np.array([[12, 9, 18],
              [15, 22, 5],
              [16, 3, 12]], dtype=float)

EPS = 0.01
MAX_ITER = 20000
SAVE_PLOT = "br_convergence.png"
SAVE_CSV = "br_results_summary.csv"

def analytic_solution(A):
    n, m = A.shape
    if n != m:
        raise ValueError("Матрица должна быть квадратной для данного метода.")
    e = np.ones(n)
    det = np.linalg.det(A)
    if abs(det) < 1e-12:
        raise np.linalg.LinAlgError("Матрица вырождена — аналитическое решение невозможно.")
    A_inv = np.linalg.inv(A)
    denom = float(e @ A_inv @ e)
    v = 1.0 / denom
    y = (A_inv @ e) / denom
    A_T_inv = np.linalg.inv(A.T)
    denom2 = float(e @ A_T_inv @ e)
    x = (A_T_inv @ e) / denom2
    return v, x, y

def brown_robinson(A, eps=0.01, max_iter=20000, verbose=False):
    m, n = A.shape
    row_counts = np.zeros(m)
    col_counts = np.zeros(n)
    init_row = int(np.argmax(A @ (np.ones(n) / n)))
    init_col = int(np.argmin((np.ones(m) / m) @ A))
    row_counts[init_row] += 1
    col_counts[init_col] += 1
    values_lower = []
    values_upper = []
    history = []
    for t in range(1, max_iter + 1):
        x_avg = row_counts / t
        y_avg = col_counts / t
        col_pay = x_avg @ A
        v_lower = float(np.min(col_pay))
        row_pay = A @ y_avg
        v_upper = float(np.max(row_pay))
        values_lower.append(v_lower)
        values_upper.append(v_upper)
        history.append({
            'iter': t,
            'x_avg': x_avg.copy(),
            'y_avg': y_avg.copy(),
            'v_lower': v_lower,
            'v_upper': v_upper
        })
        if verbose and (t <= 10 or t % 1000 == 0):
            print(f"итерация {t}: нижн={v_lower:.6f}, верхн={v_upper:.6f}, разница={v_upper - v_lower:.6f}")
        if (v_upper - v_lower) <= eps and t > 1:
            break
        next_row = int(np.argmax(A @ y_avg))
        next_col = int(np.argmin(x_avg @ A))
        row_counts[next_row] += 1
        col_counts[next_col] += 1
    return {
        'iterations': t,
        'x_avg': row_counts / t,
        'y_avg': col_counts / t,
        'v_lower': v_lower,
        'v_upper': v_upper,
        'values_lower': np.array(values_lower),
        'values_upper': np.array(values_upper),
        'history': history
    }

def print_solution(v, x, y, title="Аналитическое решение"):
    print(f"\n--- {title} ---")
    print(f"Цена игры v = {v:.12f}")
    print(f"Стратегия игрока I: {np.round(x, 9).tolist()}")
    print(f"Стратегия игрока II: {np.round(y, 9).tolist()}\n")

def save_summary_csv(filename, analytic_v, analytic_x, analytic_y, br_res):
    rows = []
    rows.append({'name': 'v_analytic', 'value': analytic_v})
    for i, xi in enumerate(analytic_x, start=1):
        rows.append({'name': f'x_analytic_{i}', 'value': xi})
    for j, yj in enumerate(analytic_y, start=1):
        rows.append({'name': f'y_analytic_{j}', 'value': yj})
    rows.append({'name': 'BR_iterations', 'value': br_res['iterations']})
    for i, xi in enumerate(br_res['x_avg'], start=1):
        rows.append({'name': f'BR_x{i}', 'value': xi})
    for j, yj in enumerate(br_res['y_avg'], start=1):
        rows.append({'name': f'BR_y{j}', 'value': yj})
    rows.append({'name': 'BR_v_lower', 'value': br_res['v_lower']})
    rows.append({'name': 'BR_v_upper', 'value': br_res['v_upper']})
    pd.DataFrame(rows).to_csv(filename, index=False)
    print(f"Сводная таблица сохранена в {filename}")

if __name__ == "__main__":
    np.set_printoptions(precision=9, suppress=True)
    try:
        v_a, x_a, y_a = analytic_solution(A)
        print_solution(v_a, x_a, y_a, "Аналитическое решение (метод обратной матрицы)")
    except Exception as e:
        print("Ошибка аналитического метода:", e)
        v_a, x_a, y_a = None, None, None
    print("Запуск метода Брауна–Робинсон")
    br = brown_robinson(A, eps=EPS, max_iter=MAX_ITER, verbose=True)
    print("\n--- Результат метода Брауна–Робинсон ---")
    print(f"Число итераций: {br['iterations']}")
    print(f"Нижняя оценка: {br['v_lower']:.6f}")
    print(f"Верхняя оценка: {br['v_upper']:.6f}")
    print(f"Средняя оценка (mid): {(br['v_lower'] + br['v_upper']) / 2:.6f}")
    print(f"Средняя стратегия игрока I: {np.round(br['x_avg'], 6).tolist()}")
    print(f"Средняя стратегия игрока II: {np.round(br['y_avg'], 6).tolist()}\n")
    if v_a is not None:
        v_mid = (br['v_lower'] + br['v_upper']) / 2
        print("--- Сравнение аналитического и численного решений ---")
        print(f"Разность |v_mid - v_analytic| = {abs(v_mid - v_a):.12f}")
        print(f"Ошибка стратегии игрока I (L1): {np.sum(np.abs(br['x_avg'] - x_a)):.12f}")
        print(f"Ошибка стратегии игрока II (L1): {np.sum(np.abs(br['y_avg'] - y_a)):.12f}\n")
    save_summary_csv(SAVE_CSV, v_a, x_a, y_a, br)
    vals_low = br['values_lower']
    vals_up = br['values_upper']
    iters = np.arange(1, len(vals_low) + 1)
    plt.figure(figsize=(10, 5))
    plt.plot(iters, vals_low, label='Нижняя граница')
    plt.plot(iters, vals_up, label='Верхняя граница')
    plt.fill_between(iters, vals_low, vals_up, alpha=0.2)
    plt.xlabel('Итерация')
    plt.ylabel('Оценка значения игры v')
    plt.title(f'Сходимость метода Брауна–Робинсон (ε = {EPS})')
    plt.legend()
    plt.grid(True)
    plt.tight_layout()
    plt.savefig(SAVE_PLOT, dpi=200)
    print(f"График сохранён в {SAVE_PLOT}")
    last = br['history'][-20:]
    rows = []
    for rec in last:
        rows.append({
            'iter': rec['iter'],
            'v_lower': rec['v_lower'],
            'v_upper': rec['v_upper'],
            'x1': rec['x_avg'][0],
            'x2': rec['x_avg'][1],
            'x3': rec['x_avg'][2],
            'y1': rec['y_avg'][0],
            'y2': rec['y_avg'][1],
            'y3': rec['y_avg'][2],
        })
    pd.DataFrame(rows).to_csv("br_last_iterations.csv", index=False)
    print("Таблица последних итераций сохранена: br_last_iterations.csv")
    print("\nГотово!")
