import numpy as np
import matplotlib.pyplot as plt

N = 24
t = np.arange(N)
x = 15 * np.sin(3*t)

impulse_expansion = np.zeros((N, N))
for k in range(N):
    impulse_expansion[k, :] = x[k] * (t == k)

step_expansion = np.zeros((N, N))
for k in range(N):
    coeff = x[k] - (x[k-1] if k > 0 else 0)
    step_expansion[k, :] = coeff * (t >= k)

x_even = (x + x[::-1]) / 2
x_odd = (x - x[::-1]) / 2

alternating = (-1)**t * x

plt.figure(figsize=(14,10))

plt.subplot(3,2,1)
plt.stem(t, x, basefmt=" ")
plt.title("Исходный сигнал x[n] = 15*sin(3n)")
plt.xlabel("n"); plt.ylabel("x[n]")

plt.subplot(3,2,2)
for k in range(N):
    plt.stem(t, impulse_expansion[k], linefmt='C0-', markerfmt='C0o', basefmt=" ")
plt.title("Импульсное разложение")
plt.xlabel("n"); plt.ylabel("Σ x[k]δ[n-k]")

plt.subplot(3,2,3)
for k in range(N):
    plt.step(t, step_expansion[k], where="post", alpha=0.5)
plt.plot(t, x, 'r--', label="Сумма")
plt.legend()
plt.title("Ступенчатое разложение")
plt.xlabel("n"); plt.ylabel("Σ (x[k]-x[k-1])u[n-k]")

plt.subplot(3,2,4)
plt.stem(t, x_even, linefmt="C1-", markerfmt="C1o", basefmt=" ", label="Четная часть")
plt.stem(t, x_odd, linefmt="C2-", markerfmt="C2s", basefmt=" ", label="Нечетная часть")
plt.legend()
plt.title("Четно-нечетное разложение")
plt.xlabel("n")

plt.subplot(3,2,5)
plt.stem(t, alternating, basefmt=" ")
plt.title("Чередующееся разложение")
plt.xlabel("n"); plt.ylabel("y[n]")

plt.tight_layout()
plt.show()
