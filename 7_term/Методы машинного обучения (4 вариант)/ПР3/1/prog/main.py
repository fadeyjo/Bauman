import numpy as np
import matplotlib.pyplot as plt
from scipy.optimize import minimize_scalar

a = 4
b = -0.25

def J(u):
    return u**2 + a * np.exp(b * u)

def dichotomy_method(func, left, right, eps=1e-5, delta=1e-4, max_iter=10000):
    iteration = 0
    while abs(right - left) > eps and iteration < max_iter:
        x1 = (left + right - delta) / 2
        x2 = (left + right + delta) / 2
        f1, f2 = func(x1), func(x2)

        if f1 < f2:
            right = x2
        else:
            left = x1

        iteration += 1

    return (left + right) / 2

u_min = dichotomy_method(J, -10, 10)
J_min = J(u_min)

res = minimize_scalar(J, bounds=(-10, 10), method='bounded')

print("Метод дихотомии:")
print(f"u_min = {u_min:.6f}, J(u_min) = {J_min:.6f}")
print("\nSciPy minimize_scalar:")
print(f"u_min = {res.x:.6f}, J(u_min) = {res.fun:.6f}")

u = np.linspace(-10, 10, 400)
plt.plot(u, J(u), label='J(u)')
plt.scatter(u_min, J_min, color='red', label='Минимум (дихотомия)')
plt.scatter(res.x, res.fun, color='green', label='Минимум (SciPy)')
plt.title("Нахождение минимума методом дихотомии")
plt.xlabel("u")
plt.ylabel("J(u)")
plt.legend()
plt.grid(True)
plt.show()
