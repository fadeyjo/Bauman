import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import os

a11 = 0.5
two_a12 = 0.5
a12 = two_a12 / 2.0
a22 = 2.5
two_a13 = 0.0
a13 = two_a13 / 2.0
two_a23 = -9.5
a23 = two_a23 / 2.0

A = np.array([[a11, a12],
              [a12, a22]], dtype=float)
b = np.array([a13, a23], dtype=float)

assert np.allclose(A, A.T), "Матрица A должна быть симметричной."

def raw_f(x):
    x = np.asarray(x).reshape(2,)
    return (A[0,0]*x[0]**2 + 2*A[0,1]*x[0]*x[1] +
            A[1,1]*x[1]**2 + 2*b[0]*x[0] + 2*b[1]*x[1])

def grad(x):
    x = np.asarray(x).reshape(2,)
    return 2 * (A.dot(x) + b)

def conjugate_gradient_quadratic(x0, eps=1e-6, max_iter=100, count_function_calls=True):
    x = np.asarray(x0, dtype=float).reshape(2,)
    f_evals = 0

    def f_counted(x_local):
        nonlocal f_evals
        if count_function_calls:
            f_evals += 1
        return raw_f(x_local)

    g = grad(x)
    r = -g
    d = r.copy()
    trajectory = [x.copy()]

    f_initial = f_counted(x)
    nit = 0

    while nit < max_iter:
        Ad = A.dot(d)
        denom = d.dot(Ad)
        if abs(denom) < 1e-20:
            break
        alpha = r.dot(r) / denom
        x = x + alpha * d
        trajectory.append(x.copy())
        nit += 1

        r_new = r - alpha * Ad
        g_new = -r_new
        if np.linalg.norm(g_new, ord=2) < eps:
            f_final = f_counted(x)
            return {
                "x": x,
                "fval": f_final,
                "nit": nit,
                "f_evals": f_evals,
                "trajectory": np.array(trajectory)
            }

        beta = r_new.dot(r_new) / (r.dot(r))
        d = r_new + beta * d
        r = r_new
        g = g_new

    f_final = f_counted(x)
    return {
        "x": x,
        "fval": f_final,
        "nit": nit,
        "f_evals": f_evals,
        "trajectory": np.array(trajectory)
    }

epsilons = [1e-1, 1e-3, 1e-6, 1e-9]
initial_points = [
    np.array([0.0, 0.0]),
    np.array([5.0, 5.0]),
    np.array([-5.0, 10.0]),
    np.array([10.0, -10.0]),
    np.array([1.0, -1.0])
]

results = []
for x0 in initial_points:
    for eps in epsilons:
        out = conjugate_gradient_quadratic(x0, eps=eps, max_iter=100, count_function_calls=True)
        results.append({
            "x0": f"[{x0[0]:.3g}, {x0[1]:.3g}]",
            "eps": eps,
            "iterations": out["nit"],
            "f_evals": out["f_evals"],
            "x_found": f"[{out['x'][0]:.8g}, {out['x'][1]:.8g}]",
            "f_at_x": out["fval"]
        })

df = pd.DataFrame(results)
df = df[["x0", "eps", "iterations", "f_evals", "x_found", "f_at_x"]]

out_excel_path = "conjugate_gradient_results.xlsx"
df.to_excel(out_excel_path, index=False)
print("Таблица сохранена в", out_excel_path)
print(df)

vis_eps = 1e-6
plots_dir = "cg_plots"
os.makedirs(plots_dir, exist_ok=True)

x_star = -np.linalg.solve(A, b)

span = 6.0
x_vals = np.linspace(x_star[0] - span, x_star[0] + span, 300)
y_vals = np.linspace(x_star[1] - span, x_star[1] + span, 300)
X, Y = np.meshgrid(x_vals, y_vals)
Z = np.vectorize(lambda xx, yy: raw_f([xx, yy]))(X, Y)

for i, x0 in enumerate(initial_points):
    out = conjugate_gradient_quadratic(x0, eps=vis_eps, max_iter=100, count_function_calls=False)
    traj = out["trajectory"]

    plt.figure(figsize=(6,5))
    CS = plt.contour(X, Y, Z, levels=30)
    plt.clabel(CS, inline=1, fontsize=8)
    traj = np.array(traj)
    plt.plot(traj[:,0], traj[:,1], marker='o')
    plt.scatter([traj[0,0]], [traj[0,1]], marker='s')
    plt.scatter([traj[-1,0]], [traj[-1,1]], marker='*')
    plt.title(f"Trajectory (start={x0.tolist()}, eps={vis_eps})")
    plt.xlabel("x")
    plt.ylabel("y")
    plt.axis('equal')
    filename = os.path.join(plots_dir, f"trajectory_start_{i}.png")
    plt.savefig(filename, dpi=200, bbox_inches='tight')
    plt.close()
