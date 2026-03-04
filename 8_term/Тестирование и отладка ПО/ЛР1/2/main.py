# deanery.py

def check_students_count(n):
    if 1 <= n <= 30:
        return "Корректное значение"
    else:
        return "Ошибка: допустимо от 1 до 30"


if __name__ == "__main__":
    n = int(input("Введите количество студентов: "))
    print(check_students_count(n))
    