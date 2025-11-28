import numpy as np
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D
from scipy.optimize import minimize

def f(x):
    x1, x2 = x
    return x1**3 + x2**2 - x1*x2 - 2*x1 + 3*x2 - 4

def grad_f(x):
    x1, x2 = x
    df_dx1 = 3*x1**2 - x2 - 2
    df_dx2 = 2*x2 - x1 + 3
    return np.array([df_dx1, df_dx2])

def gradient_descent(start, learning_rate=0.01, eps=1e-6, max_iter=10000):
    x = np.array(start, dtype=float)
    path = [x.copy()]
    
    for i in range(max_iter):
        grad = grad_f(x)
        x_new = x - learning_rate * grad
        
        if np.linalg.norm(x_new - x) < eps:
            break
        x = x_new
        path.append(x.copy())
    
    return x, f(x), np.array(path)

start = [0, 0]
learning_rate = 0.01

x_min, f_min, path = gradient_descent(start, learning_rate)

print("Минимум (градиентный спуск):")
print(f"x1 = {x_min[0]:.6f}, x2 = {x_min[1]:.6f}")
print(f"f(x1, x2) = {f_min:.6f}")

res = minimize(f, start)
print("\nМинимум (scipy.optimize.minimize):")
print(f"x1 = {res.x[0]:.6f}, x2 = {res.x[1]:.6f}")
print(f"f(x1, x2) = {res.fun:.6f}")

x1_vals = np.linspace(-3, 3, 100)
x2_vals = np.linspace(-3, 3, 100)
X1, X2 = np.meshgrid(x1_vals, x2_vals)
Z = f([X1, X2])

fig = plt.figure(figsize=(10, 7))
ax = fig.add_subplot(111, projection='3d')
ax.plot_surface(X1, X2, Z, cmap='viridis', alpha=0.7)
ax.plot(path[:,0], path[:,1], [f(p) for p in path], color='red', marker='o', label='Траектория спуска')

ax.set_title('Минимизация функции методом градиентного спуска')
ax.set_xlabel('x1')
ax.set_ylabel('x2')
ax.set_zlabel('f(x1, x2)')
ax.legend()
plt.show()
