import openpyxl


def calculate(a, b, c):
    result = 0

    # Ветвящийся алгоритм
    if a > b:
        result = a - b
        criterion = "a > b"
    elif a == b:
        result = a * b
        criterion = "a = b"
    else:
        result = b - a
        criterion = "a < b"

    # Циклический алгоритм
    sum_c = 0
    for i in range(1, c + 1):
        sum_c += i

    result = result + sum_c

    return result, criterion


def expected_result(a, b, c):
    """Расчёт ожидаемого результата"""
    if a > b:
        result = a - b
    elif a == b:
        result = a * b
    else:
        result = b - a

    sum_c = sum(range(1, c + 1))

    return result + sum_c


def read_input_file(filename):
    data = []
    with open(filename, "r") as f:
        for line in f:
            a, b, c = map(int, line.split())
            data.append((a, b, c))
    return data


def create_excel(results):
    wb = openpyxl.Workbook()
    ws = wb.active
    ws.title = "Тестирование"

    # Шапка таблицы
    headers = [
        "Критерий формирования теста",
        "№ Теста",
        "a",
        "b",
        "c",
        "Ожидаемый результат",
        "Фактический результат",
    ]

    ws.append(headers)

    for row in results:
        ws.append(row)

    wb.save("test_results.xlsx")


def main():

    data = read_input_file("input.txt")

    results = []

    for i, (a, b, c) in enumerate(data, start=1):

        actual, criterion = calculate(a, b, c)
        expected = expected_result(a, b, c)

        results.append([
            criterion,
            i,
            a,
            b,
            c,
            expected,
            actual
        ])

    create_excel(results)

    print("Тестирование завершено.")
    print("Файл test_results.xlsx создан.")


if __name__ == "__main__":
    main()