# triangle_test_from_file.py

import os
from openpyxl import Workbook
from datetime import datetime


def triangle_type(a, b, c):
    # Проверка существования треугольника
    if a <= 0 or b <= 0 or c <= 0:
        return "Не треугольник"

    if a + b <= c or a + c <= b or b + c <= a:
        return "Не треугольник"

    if a == b == c:
        return "Равносторонний"
    elif a == b or a == c or b == c:
        return "Равнобедренный"
    else:
        return "Неравносторонний"


def process_file(input_filename):
    if not os.path.exists(input_filename):
        print("Входной файл отсутствует")
        return

    # 🔹 Создаем НОВЫЙ Excel файл при каждом запуске
    timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
    output_filename = f"triangle_test_results_{timestamp}.xlsx"

    wb = Workbook()
    ws = wb.active
    ws.title = "Результаты тестов"

    # Шапка таблицы
    ws.append([
        "№ теста",
        "a",
        "b",
        "c",
        "Ожидаемый тип (по программе)"
    ])

    with open(input_filename, "r", encoding="utf-8") as f:
        lines = f.readlines()

    test_number = 1

    for line in lines:
        parts = line.strip().split()

        if len(parts) != 3:
            continue  # пропускаем некорректные строки

        try:
            a, b, c = map(int, parts)
        except ValueError:
            continue  # пропускаем строки с нечисловыми значениями

        result = triangle_type(a, b, c)

        ws.append([test_number, a, b, c, result])
        test_number += 1

    wb.save(output_filename)

    print(f"Результаты сохранены в файл: {output_filename}")


if __name__ == "__main__":
    input_file = "input.txt"
    process_file(input_file)
    