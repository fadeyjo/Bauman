import numpy as np
from scipy.optimize import linprog
import math

def gomory_second_algorithm():
    print("\n=== ВТОРОЙ АЛГОРИТМ ГОМОРИ ===")

    # Исходная задача
    c = [3, 4]
    A_ub = [[-5, -2], [-2, -5]]
    b_ub = [-33, -35]
    bounds = [(0, None), (0, None)]

    iteration = 0
    max_iterations = 10

    while iteration < max_iterations:
        result = linprog(c, A_ub=A_ub, b_ub=b_ub, bounds=bounds, method='highs')

        if not result.success:
            print("Задача не имеет допустимого решения")
            return None

        x1, x2 = result.x
        z = result.fun

        print(f"\n--- Итерация {iteration} ---")
        print(f"Решение: x1 = {x1:.6f}, x2 = {x2:.6f}")
        print(f"Целевая функция: z = {z:.6f}")

        # Проверяем целочисленность только x2
        x2_int = abs(x2 - round(x2)) < 1e-6

        if x2_int:
            print(" Найдено решение с целым x2!")
            return [x1, round(x2)], 3 * x1 + 4 * round(x2)

        # Добавляем отсечение только для x2
        x2_floor = math.floor(x2)
        frac_x2 = x2 - x2_floor

        print(f"Дробная часть x2: {frac_x2:.6f}")
        print(f"Добавляем отсечение: x2 <= {x2_floor}")

        # Отсечение: x2 <= floor(x2)
        new_A = [0, 1]  # x2 <= floor(x2)
        new_b = x2_floor

        A_ub.append(new_A)
        b_ub.append(new_b)

        iteration += 1

    print("Достигнут лимит итераций")

    # Возвращаем лучшее найденное решение с округлением x2
    result = linprog(c, A_ub=A_ub, b_ub=b_ub, bounds=bounds, method='highs')
    if result.success:
        x1, x2 = result.x
        return [x1, round(x2)], 3 * x1 + 4 * round(x2)

    return None

gomory_second_algorithm()
