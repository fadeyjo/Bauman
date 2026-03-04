# test_system.py
import random
import os


def generate_tests(filename, questions_per_test, variants_count):
    if not os.path.exists(filename):
        return "файл отсутствует"

    with open(filename, "r", encoding="utf-8") as f:
        questions = f.readlines()

    total_questions = len(questions)

    if total_questions < questions_per_test + 3:
        return "Недостаточно заданий"

    if variants_count > 10:
        return "Количество вариантов не более 10"

    variants = []

    for i in range(variants_count):
        variant = random.sample(questions, questions_per_test)
        variants.append(variant)

    return variants


if __name__ == "__main__":
    filename = input("Имя файла: ")
    q_count = int(input("Количество заданий в тесте: "))
    v_count = int(input("Количество вариантов: "))

    result = generate_tests(filename, q_count, v_count)

    if isinstance(result, str):
        print(result)
    else:
        for i, variant in enumerate(result):
            print(f"\nВариант №{i+1}")
            for q in variant:
                print(q.strip())