import math


def triangle_type(a, b, c):
    if a + b <= c or a + c <= b or b + c <= a:
        return "Не треугольник"
    if a == b == c:
        return "Равносторонний"
    if a == b or b == c or a == c:
        return "Равнобедренный"
    sides = sorted([a, b, c])
    if sides[0] ** 2 + sides[1] ** 2 == sides[2] ** 2:
        return "Прямоугольный"
    if sides[0] ** 2 + sides[1] ** 2 > sides[2] ** 2:
        return "Остроугольный"
    return "Тупоугольный"


def is_square(x1, y1, x2, y2, x3, y3, x4, y4):
    def dist_sq(x1, y1, x2, y2):
        return (x1 - x2) ** 2 + (y1 - y2) ** 2

    dists = sorted([
        dist_sq(x1, y1, x2, y2),
        dist_sq(x1, y1, x3, y3),
        dist_sq(x1, y1, x4, y4),
        dist_sq(x2, y2, x3, y3),
        dist_sq(x2, y2, x4, y4),
        dist_sq(x3, y3, x4, y4)
    ])
    return dists[0] == dists[1] == dists[2] == dists[3] and dists[4] == dists[5]


def is_rhombus(x1, y1, x2, y2, x3, y3, x4, y4):
    def dist_sq(x1, y1, x2, y2):
        return (x1 - x2) ** 2 + (y1 - y2) ** 2

    dists = sorted([
        dist_sq(x1, y1, x2, y2),
        dist_sq(x1, y1, x3, y3),
        dist_sq(x1, y1, x4, y4),
        dist_sq(x2, y2, x3, y3),
        dist_sq(x2, y2, x4, y4),
        dist_sq(x3, y3, x4, y4)
    ])
    return dists[0] == dists[1] == dists[2] == dists[3]


def line_circle_intersection(k, b, r):
    D = (2 * k * b) ** 2 - 4 * (1 + k ** 2) * (b ** 2 - r ** 2)
    if D < 0:
        return "Не пересекаются"
    if D == 0:
        return "Касаются в одной точке"
    return "Пересекаются в двух точках"


def circle_circle_intersection(x1, y1, r1, x2, y2, r2):
    d = math.sqrt((x1 - x2) ** 2 + (y1 - y2) ** 2)
    if d > r1 + r2:
        return "Не пересекаются"
    if d == r1 + r2:
        return "Касаются в одной точке"
    if d < abs(r1 - r2):
        return "Не пересекаются (одна окружность внутри другой)"
    if d == 0 and r1 == r2:
        return "Совпадают"
    return "Пересекаются в двух точках"


def main():
    choice = int(input("Выберите вариант (1-5): "))
    if choice == 1:
        a, b, c = map(int, input("Введите три стороны треугольника: ").split())
        print(triangle_type(a, b, c))
    elif choice == 2:
        coords = list(map(int, input("Введите координаты 4-х вершин четырех-угольника: ").split()))
        print("Квадрат" if is_square(*coords) else "Не квадрат")
    elif choice == 3:
        coords = list(map(int, input("Введите координаты 4-х вершин четырех-угольника: ").split()))
        print("Ромб" if is_rhombus(*coords) else "Не ромб")
    elif choice == 4:
        k, b, r = map(float, input("Введите k, b, R для прямой и окружности: ").split())
        print(line_circle_intersection(k, b, r))
    elif choice == 5:
        x1, y1, r1, x2, y2, r2 = map(float, input("Введите X1, Y1, R1, X2, Y2, R2 для двух окружностей: ").split())
        print(circle_circle_intersection(x1, y1, r1, x2, y2, r2))
    else:
        print("Неверный выбор")


if __name__ == "__main__":
    main()
