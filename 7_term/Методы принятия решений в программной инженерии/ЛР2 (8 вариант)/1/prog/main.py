import numpy as np
import matplotlib.pyplot as plt
from scipy.optimize import linprog
from math import floor

A = [
    [6, 4],
    [5, 5],
    [4, 6]
]

T = [
    480 * (1 - 0.10),
    480 * (1 - 0.14),
    480 * (1 - 0.12)
]

c = [-1, -1]

res = linprog(c, A_ub=A, b_ub=T, bounds=[(0, None), (0, None)], method='highs')

x1, x2 = res.x
max_value = -res.fun

print("Оптимальное решение:")
print(f"x1 = {floor(x1)}  (CH-1)")
print(f"x2 = {floor(x2)}  (CH-2)")
print(f"Максимальное количество чипов: Z = {floor(max_value)}")

x = np.linspace(0, 100, 200)
y1 = (T[0] - 6*x)/4
y2 = (T[1] - 5*x)/5
y3 = (T[2] - 4*x)/6

plt.figure(figsize=(8,6))
plt.plot(x, y1, label='6x₁ + 4x₂ ≤ 432')
plt.plot(x, y2, label='5x₁ + 5x₂ ≤ 412.8')
plt.plot(x, y3, label='4x₁ + 6x₂ ≤ 422.4')

plt.fill_between(x, 0, np.minimum(np.minimum(y1, y2), y3), alpha=0.3)
plt.plot(x1, x2, 'ro', label=f'Оптимум ({x1:.1f}, {x2:.1f})')

plt.xlim(0, 80)
plt.ylim(0, 80)
plt.xlabel("x₁ (CH-1)")
plt.ylabel("x₂ (CH-2)")
plt.legend()
plt.title("Графическое решение задачи линейного программирования")
plt.grid(True)
plt.show()

reductions = [0.09, 0.13, 0.11]
T_new = [480 * (1 - r) for r in reductions]

res_new = linprog(c, A_ub=A, b_ub=T_new, bounds=[(0, None), (0, None)], method='highs')
new_value = -res_new.fun

delta = new_value - max_value
print("\nАнализ чувствительности:")
for i, (old, new) in enumerate(zip(T, T_new), 1):
    print(f"Линия {i}: время увеличилось с {old:.1f} до {new:.1f} мин")

print(f"Рост выпуска при снижении профилактики на 1% на всех линиях: {delta:.2f} чипов/смену")

print("\nСтоимость 1% уменьшения профилактики (в мин/1%):")
for i in range(3):
    print(f"Линия {i+1}: +{480 * 0.01:.1f} мин доступного времени")
