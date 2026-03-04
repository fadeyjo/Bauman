# geometry_from_file.py

import math
import os
from openpyxl import Workbook
from datetime import datetime


def distance(p1, p2):
    return math.sqrt((p1[0] - p2[0])**2 + (p1[1] - p2[1])**2)


def is_square(p1, p2, p3, p4):
    points = [p1, p2, p3, p4]
    dists = []

    for i in range(4):
        for j in range(i + 1, 4):
            dists.append(distance(points[i], points[j]))

    dists.sort()

    return (
        dists[0] > 0 and
        dists[0] == dists[1] == dists[2] == dists[3] and
        dists[4] == dists[5]
    )


def process_file(input_filename):
    if not os.path.exists(input_filename):
        print("Входной файл отсутствует")
        return

    # 🔹 Новый Excel файл при каждом запуске
    timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
    output_filename = f"geometry_test_results_{timestamp}.xlsx"

    wb = Workbook()
    ws = wb.active
    ws.title = "Результаты тестов"

    # Шапка таблицы
    ws.append([
        "№ теста",
        "x1", "y1",
        "x2", "y2",
        "x3", "y3",
        "x4", "y4",
        "Результат"
    ])

    with open(input_filename, "r", encoding="utf-8") as f:
        lines = f.readlines()

    test_number = 1

    for line in lines:
        parts = line.strip().split()

        if len(parts) != 8:
            continue  # пропуск некорректных строк

        try:
            coords = list(map(float, parts))
        except ValueError:
            continue  # пропуск нечисловых данных

        p1 = (coords[0], coords[1])
        p2 = (coords[2], coords[3])
        p3 = (coords[4], coords[5])
        p4 = (coords[6], coords[7])

        result = "Это квадрат" if is_square(p1, p2, p3, p4) else "Это не квадрат"

        ws.append([
            test_number,
            coords[0], coords[1],
            coords[2], coords[3],
            coords[4], coords[5],
            coords[6], coords[7],
            result
        ])

        test_number += 1

    wb.save(output_filename)

    print(f"Результаты сохранены в файл: {output_filename}")


if __name__ == "__main__":
    input_file = "input.txt"
    process_file(input_file)
