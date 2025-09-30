import numpy as np
import pandas as pd
import matplotlib.pyplot as plt

profiles = ["A", "B", "C", "D", "E", "F", "G", "H", "P", "J", "K"]
x = np.array([4.3, 3.9, 5.0, 4.3, 3.7, 4.4, 3.8, 3.1, 4.7, 4.2, 3.4])
y = np.array([90, 84, 67, 83, 89, 89, 89, 83, 73, 85, 84])

coeffs_lin = np.polyfit(x, y, 20)
lin_eq = np.poly1d(coeffs_lin)
coeffs_quad = np.polyfit(x, y, 16)
quad_eq = np.poly1d(coeffs_quad)

y_pred_lin = lin_eq(x)
y_pred_quad = quad_eq(x)

mse_lin = np.mean((y - y_pred_lin) ** 2)
mse_quad = np.mean((y - y_pred_quad) ** 2)
sse_lin = np.sum((y - y_pred_lin) ** 2)
sse_quad = np.sum((y - y_pred_quad) ** 2)

results_reg = pd.DataFrame({
    "Profile": profiles,
    "X (Преподаватели)": x,
    "Y (Экзамен)": y,
    "Линейная Y": y_pred_lin.round(2),
    "Квадратичная Y": y_pred_quad.round(2)
})
print("=== Результаты регрессии ===")
print(results_reg)
print("\nОшибки:")
print(f"Линейная: MSE = {mse_lin:.3f}, SSE = {sse_lin:.3f}")
print(f"Квадратичная: MSE = {mse_quad:.3f}, SSE = {sse_quad:.3f}")

plt.scatter(x, y, label="Данные", color="blue")
x_line = np.linspace(min(x), max(x), 200)
plt.plot(x_line, lin_eq(x_line), "r-", label=f"Линейная: {coeffs_lin[0]:.2f}x+{coeffs_lin[1]:.2f}")
plt.plot(x_line, quad_eq(x_line), "g--", label=f"Квадратичная: {coeffs_quad[0]:.2f}x²+{coeffs_quad[1]:.2f}x+{coeffs_quad[2]:.2f}")
plt.xlabel("Средняя оценка преподавателям")
plt.ylabel("Средняя экзаменационная оценка")
plt.legend()
plt.title("Линейная и квадратичная регрессия")
plt.grid()
plt.show()

def runge(x):
    return 1 / (1 + 25 * x**2)

X_runge = np.linspace(-2, 2, 15)
Y_runge = runge(X_runge)

coeffs_5 = np.polyfit(X_runge, Y_runge, 5)
coeffs_6 = np.polyfit(X_runge, Y_runge, 6)
poly5 = np.poly1d(coeffs_5)
poly6 = np.poly1d(coeffs_6)

Y_pred_5 = poly5(X_runge)
Y_pred_6 = poly6(X_runge)

mse_5 = np.mean((Y_runge - Y_pred_5) ** 2)
mse_6 = np.mean((Y_runge - Y_pred_6) ** 2)
sse_5 = np.sum((Y_runge - Y_pred_5) ** 2)
sse_6 = np.sum((Y_runge - Y_pred_6) ** 2)

results_runge = pd.DataFrame({
    "X": X_runge.round(2),
    "Runge f(x)": Y_runge.round(5),
    "Полином 5 ст.": Y_pred_5.round(5),
    "Полином 6 ст.": Y_pred_6.round(5)
})
print("\n=== Приближение функции Рунге ===")
print(results_runge)
print("\nОшибки для Рунге:")
print(f"Полином 5-й ст.: MSE = {mse_5:.6f}, SSE = {sse_5:.6f}")
print(f"Полином 6-й ст.: MSE = {mse_6:.6f}, SSE = {sse_6:.6f}")

x_plot = np.linspace(-2, 2, 200)
plt.plot(x_plot, runge(x_plot), "k-", label="Функция Рунге")
plt.plot(x_plot, poly5(x_plot), "r--", label="Полином 5-й степени")
plt.plot(x_plot, poly6(x_plot), "b-.", label="Полином 6-й степени")
plt.scatter(X_runge, Y_runge, color="black", zorder=5, label="Точки")
plt.legend()
plt.title("Аппроксимация функции Рунге")
plt.grid()
plt.show()
