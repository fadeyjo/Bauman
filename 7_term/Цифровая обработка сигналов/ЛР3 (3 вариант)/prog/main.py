import numpy as np
import matplotlib.pyplot as plt
from scipy.signal import butter, cheby1, cheby2, ellip, bessel, freqs, lp2bp, lp2bs

n = 5      # порядок фильтра
Rp = 4     # неравномерность в полосе пропускания (дБ) - для Chebyshev/elliptic
Rs = 40    # подавление в полосе заграждения (дБ)
Wc = 1     # нормированная частота среза (рад/с)

filters = {
    "Баттерворт": butter(n, Wc, btype="low", analog=True, output="ba"),
    "Чебышев I": cheby1(n, Rp, Wc, btype="low", analog=True, output="ba"),
    "Чебышев II": cheby2(n, Rs, Wc, btype="low", analog=True, output="ba"),
    "Эллиптический": ellip(n, Rp, Rs, Wc, btype="low", analog=True, output="ba"),
    "Бессель": bessel(n, Wc, btype="low", analog=True, output="ba", norm="phase")
}

w = np.logspace(-1, 2, 500)
plt.figure(figsize=(12, 8))

for name, (b, a) in filters.items():
    w_, h = freqs(b, a, w)
    plt.semilogx(w_, 20 * np.log10(abs(h)), label=name)

plt.title("АЧХ аналоговых фильтров (порядок = 5)")
plt.xlabel("Частота [рад/с]")
plt.ylabel("Амплитуда [дБ]")
plt.grid(True, which="both", ls="--")
plt.legend()
plt.show()


b, a = butter(n, Wc, analog=True)

w1, w2 = 0.5, 2.0

b_bp, a_bp = lp2bp(b, a, wo=np.sqrt(w1*w2), bw=w2-w1)

b_bs, a_bs = lp2bs(b, a, wo=np.sqrt(w1*w2), bw=w2-w1)

plt.figure(figsize=(12, 6))

w, h = freqs(b_bp, a_bp, w)
plt.semilogx(w, 20 * np.log10(abs(h)), label="Полосовой")

w, h = freqs(b_bs, a_bs, w)
plt.semilogx(w, 20 * np.log10(abs(h)), label="Режекторный")

plt.title("Преобразование фильтра-прототипа (Баттерворт 5 порядка)")
plt.xlabel("Частота [рад/с]")
plt.ylabel("Амплитуда [дБ]")
plt.grid(True, which="both", ls="--")
plt.legend()
plt.show()
