import numpy as np
from scipy.optimize import linprog
import math

def gomory_first_algorithm():
    print("=== ПЕРВЫЙ АЛГОРИТМ ГОМОРИ ===")

    c = [3, 4]
    A_ub = [[-5, -2], [-2, -5]]
    b_ub = [-33, -35]
    bounds = [(0, None), (0, None)]

    iteration = 0
    max_iterations = 15
    best_solution = None
    best_z = float('inf')

    while iteration < max_iterations:
        result = linprog(c, A_ub=A_ub, b_ub=b_ub, bounds=bounds, method='highs')

        if not result.success:
            print("Задача не имеет допустимого решения")
            break

        x1, x2 = result.x
        z = result.fun

        print(f"\n--- Итерация {iteration} ---")
        print(f"Решение: x1 = {x1:.6f}, x2 = {x2:.6f}, z = {z:.6f}")

        x1_frac = x1 - math.floor(x1)
        x2_frac = x2 - math.floor(x2)

        print(f"Дробные части: x1 = {x1_frac:.6f}, x2 = {x2_frac:.6f}")

        if x1_frac < 1e-6 and x2_frac < 1e-6:
            print(" Найдено целочисленное решение!")
            best_solution = [round(x1), round(x2)]
            best_z = z
            break

        candidate = [round(x1), round(x2)]
        candidate_z = 3 * candidate[0] + 4 * candidate[1]

        if (5 * candidate[0] + 2 * candidate[1] >= 12 and
                2 * candidate[0] + 5 * candidate[1] >= 14 and
                candidate_z < best_z):
            best_solution = candidate
            best_z = candidate_z
            print(f"Лучшее целочисленное приближение: x1={candidate[0]}, x2={candidate[1]}, z={candidate_z}")

        if x1_frac >= x2_frac:
            print(f"Добавляем отсечение для x1 (дробная часть = {x1_frac:.6f})")

            A_ub1 = A_ub + [[1, 0]]
            b_ub1 = b_ub + [math.floor(x1)]

            A_ub2 = A_ub + [[-1, 0]]
            b_ub2 = b_ub + [-math.ceil(x1)]

            result1 = linprog(c, A_ub=A_ub1, b_ub=b_ub1, bounds=bounds, method='highs')
            result2 = linprog(c, A_ub=A_ub2, b_ub=b_ub2, bounds=bounds, method='highs')

            if result1.success and result2.success:
                if result1.fun <= result2.fun:
                    A_ub, b_ub = A_ub1, b_ub1
                    print(f"Выбрана ветвь: x1 <= {math.floor(x1)}")
                else:
                    A_ub, b_ub = A_ub2, b_ub2
                    print(f"Выбрана ветвь: x1 >= {math.ceil(x1)}")
            elif result1.success:
                A_ub, b_ub = A_ub1, b_ub1
                print(f"Выбрана ветвь: x1 <= {math.floor(x1)}")
            elif result2.success:
                A_ub, b_ub = A_ub2, b_ub2
                print(f"Выбрана ветвь: x1 >= {math.ceil(x1)}")
            else:
                print("Обе ветви недопустимы - возвращаем лучшее приближение")
                break

        else:
            print(f"Добавляем отсечение для x2 (дробная часть = {x2_frac:.6f})")

            A_ub1 = A_ub + [[0, 1]]
            b_ub1 = b_ub + [math.floor(x2)]

            A_ub2 = A_ub + [[0, -1]]
            b_ub2 = b_ub + [-math.ceil(x2)]

            result1 = linprog(c, A_ub=A_ub1, b_ub=b_ub1, bounds=bounds, method='highs')
            result2 = linprog(c, A_ub=A_ub2, b_ub=b_ub2, bounds=bounds, method='highs')

            if result1.success and result2.success:
                if result1.fun <= result2.fun:
                    A_ub, b_ub = A_ub1, b_ub1
                    print(f"Выбрана ветвь: x2 <= {math.floor(x2)}")
                else:
                    A_ub, b_ub = A_ub2, b_ub2
                    print(f"Выбрана ветвь: x2 >= {math.ceil(x2)}")
            elif result1.success:
                A_ub, b_ub = A_ub1, b_ub1
                print(f"Выбрана ветвь: x2 <= {math.floor(x2)}")
            elif result2.success:
                A_ub, b_ub = A_ub2, b_ub2
                print(f"Выбрана ветвь: x2 >= {math.ceil(x2)}")
            else:
                print("Обе ветви недопустимы - возвращаем лучшее приближение")
                break

        iteration += 1

    if best_solution:
        print(f"\n Оптимальное целочисленное решение: x1={best_solution[0]}, x2={best_solution[1]}, z={best_z}")
        return best_solution, best_z
    else:
        print("Целочисленное решение не найдено")
        return None, float('inf')

gomory_first_algorithm()
