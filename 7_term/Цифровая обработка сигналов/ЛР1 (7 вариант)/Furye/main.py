import numpy as np
import matplotlib.pyplot as plt

A = 4
T = 4
D = 3
N = 16

t = np.linspace(-2*T, 2*T, 2000)
w0 = 2 * np.pi / T

def rect_pulse(t):
    return A * ((t % T) < D).astype(float)

def meander(t):
    return A * np.sign(np.sin(w0 * t))

def sawtooth(t):
    return (2*A/np.pi) * np.arctan(np.tan((w0*t)/2))

def triangle(t):
    return (2*A/np.pi) * np.arcsin(np.sin(w0*t))

def fourier_series(func, t, N):
    sums = []
    f = func(t)
    a0 = (2/T) * np.trapz(f * np.ones_like(t), t)
    partial = a0/2 * np.ones_like(t)
    sums.append(partial.copy())
    
    for n in range(1, N+1):
        cos_term = np.cos(n*w0*t)
        sin_term = np.sin(n*w0*t)
        
        an = (2/T) * np.trapz(f * cos_term, t)
        bn = (2/T) * np.trapz(f * sin_term, t)
        
        partial += an * cos_term + bn * sin_term
        sums.append(partial.copy())
    return sums

def plot_fourier(func, title):
    sums = fourier_series(func, t, N)
    plt.figure(figsize=(12, 8))
    for i in [1, 2, 4, 8, 16]:  # промежуточные стадии
        plt.plot(t, sums[i], label=f'{i} гармоник')
    plt.plot(t, func(t), 'k--', label='Оригинал')
    plt.title(title)
    plt.xlabel('t')
    plt.ylabel('Амплитуда')
    plt.legend()
    plt.grid()
    plt.show()

plot_fourier(rect_pulse, 'Прямоугольные импульсы')
plot_fourier(meander, 'Меандр')
plot_fourier(sawtooth, 'Пилообразный сигнал')
plot_fourier(triangle, 'Треугольный сигнал')
