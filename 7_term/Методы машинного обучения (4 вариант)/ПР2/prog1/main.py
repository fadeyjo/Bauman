import numpy as np
import matplotlib.pyplot as plt
from sklearn.preprocessing import PolynomialFeatures
from sklearn.linear_model import LinearRegression
from sklearn.metrics import mean_squared_error

def runge_function(x):
    return 1 / (1 + 25 * x**2)

l = 15
X_train = np.array([4 * ((i - 1) / (l - 1)) - 2 for i in range(1, l + 1)])
y_train = runge_function(X_train)
X_test = np.array([4 * ((i - 0.5) / (l - 1)) - 2 for i in range(1, l)])
y_test = runge_function(X_test)

max_degree = 10
results = []

for degree in range(1, max_degree + 1):
    poly = PolynomialFeatures(degree=degree)
    X_poly = poly.fit_transform(X_train.reshape(-1, 1))
    model = LinearRegression().fit(X_poly, y_train)

    y_pred_train = model.predict(X_poly)
    mse_train = mean_squared_error(y_train, y_pred_train)

    X_test_poly = poly.transform(X_test.reshape(-1, 1))
    y_pred_test = model.predict(X_test_poly)
    mse_test = mean_squared_error(y_test, y_pred_test)

    results.append((degree, mse_train, mse_test, model, poly))
    print(f"Степень {degree}: MSE(train)={mse_train:.6f}, MSE(test)={mse_test:.6f}")

best_degree, best_train_mse, best_test_mse, best_model, best_poly = min(
    results, key=lambda t: t[1]  # выбор по ошибке на обучающей выборке
)

print(f"\nОптимальная степень полинома: {best_degree}")
print(f"MSE(train)={best_train_mse:.6f}, MSE(test)={best_test_mse:.6f}")

x_plot = np.linspace(-2, 2, 400)
y_plot = runge_function(x_plot)
y_poly_plot = best_model.predict(best_poly.transform(x_plot.reshape(-1, 1)))

plt.figure(figsize=(10, 6))
plt.plot(x_plot, y_plot, label="Функция Рунге", color="blue")
plt.plot(x_plot, y_poly_plot, label=f"Оптимальный полином степени {best_degree}", color="red")
plt.scatter(X_train, y_train, color="black", marker="o", label="Обучающая выборка")
plt.scatter(X_test, y_test, color="green", marker="x", label="Контрольная выборка")
plt.title("Аппроксимация функции Рунге оптимальным полиномом")
plt.xlabel("x")
plt.ylabel("y")
plt.legend()
plt.grid(True)
plt.show()
