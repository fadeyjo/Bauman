import numpy as np
import matplotlib.pyplot as plt

# Генерация данных
np.random.seed(10)
X = np.random.uniform(0, 10, 10)
y = np.random.choice([-1, 1], 10)
sort_indices = np.argsort(X)
X = X[sort_indices]
y = y[sort_indices]
print("Данные:")
for i, (feature, label) in enumerate(zip(X, y)):
    print(f"{i}: {feature:.2f}, {label}")

plt.figure(figsize=(12, 3))
colors = ['red' if label == -1 else 'blue' for label in y]
plt.scatter(X, [0]*10, c=colors, s=100, alpha=0.7)
plt.yticks([])
plt.xlabel('X')
plt.title('Метки -1 (red), 1 (blue)')
for i, (x, label) in enumerate(zip(X, y)):
    plt.text(x, 0.02, f'{i}', ha='center', va='bottom')
plt.grid(True, alpha=0.3)
plt.show()
