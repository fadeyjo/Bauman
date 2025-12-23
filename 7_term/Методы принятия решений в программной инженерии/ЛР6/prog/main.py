import numpy as np
import itertools
from scipy.optimize import linprog

states = [0, 1]
actions = [0, 1]
N = 3
gamma = 0.9

P = {
    0: np.array([[0.9, 0.1],
                 [0.6, 0.4]]),
    1: np.array([[0.7, 0.3],
                 [0.2, 0.8]])
}

R = {
    0: np.array([[2, -1],
                 [1, -3]]),
    1: np.array([[4, 1],
                 [2, -1]])
}

def expected_reward(s, a):
    return np.sum(P[a][s] * R[a][s])

def brute_force_finite():
    print("\n--- КОНЕЧНЫЙ ГОРИЗОНТ: ПОЛНЫЙ ПЕРЕБОР ---")
    strategies = list(itertools.product(actions, repeat=N))
    results = []

    for strategy in strategies:
        V = np.zeros(len(states))
        for t in reversed(range(N)):
            new_V = np.zeros(len(states))
            for s in states:
                a = strategy[t]
                new_V[s] = expected_reward(s, a) + gamma * np.sum(P[a][s] * V)
            V = new_V
        results.append((strategy, V.copy()))

    best = max(results, key=lambda x: x[1].sum())
    print("Лучшая стратегия:", best[0])
    print("Функция ценности:", best[1])
    return best

def policy_iteration_finite():
    print("\n--- КОНЕЧНЫЙ ГОРИЗОНТ: ИТЕРАЦИИ ПО СТРАТЕГИЯМ ---")
    policy = np.zeros((N, len(states)), dtype=int)
    stable = False

    while not stable:
        V = np.zeros((N + 1, len(states)))
        for t in reversed(range(N)):
            for s in states:
                a = policy[t, s]
                V[t, s] = expected_reward(s, a) + gamma * np.sum(P[a][s] * V[t + 1])

        stable = True
        for t in range(N):
            for s in states:
                values = []
                for a in actions:
                    val = expected_reward(s, a) + gamma * np.sum(P[a][s] * V[t + 1])
                    values.append(val)
                best_a = np.argmax(values)
                if best_a != policy[t, s]:
                    policy[t, s] = best_a
                    stable = False

    print("Оптимальная политика:")
    print(policy)
    print("Функция ценности:", V[0])
    return policy, V[0]

def stationary_strategies():
    return list(itertools.product(actions, repeat=len(states)))

def evaluate_policy(policy, eps=1e-6):
    V = np.zeros(len(states))
    while True:
        new_V = np.zeros(len(states))
        for s in states:
            a = policy[s]
            new_V[s] = expected_reward(s, a) + gamma * np.sum(P[a][s] * V)
        if np.max(np.abs(new_V - V)) < eps:
            break
        V = new_V
    return V

def brute_force_infinite():
    print("\n--- БЕСКОНЕЧНЫЙ ГОРИЗОНТ: ПОЛНЫЙ ПЕРЕБОР ---")
    strategies = stationary_strategies()
    results = []

    for policy in strategies:
        V = evaluate_policy(policy)
        results.append((policy, V))

    best = max(results, key=lambda x: x[1].sum())
    print("Лучшая стратегия:", best[0])
    print("Функция ценности:", best[1])
    return best

def policy_iteration_infinite():
    print("\n--- БЕСКОНЕЧНЫЙ ГОРИЗОНТ: ИТЕРАЦИИ ПО СТРАТЕГИЯМ ---")
    policy = np.zeros(len(states), dtype=int)
    stable = False

    while not stable:
        V = evaluate_policy(policy)
        stable = True
        for s in states:
            values = []
            for a in actions:
                val = expected_reward(s, a) + gamma * np.sum(P[a][s] * V)
                values.append(val)
            best_a = np.argmax(values)
            if best_a != policy[s]:
                policy[s] = best_a
                stable = False

    print("Оптимальная стратегия:", policy)
    print("Функция ценности:", V)
    return policy, V

def linear_programming_solution():
    print("\n--- БЕСКОНЕЧНЫЙ ГОРИЗОНТ: ЛИНЕЙНОЕ ПРОГРАММИРОВАНИЕ ---")
    c = [-1, -1]
    A = []
    b = []

    for s in states:
        for a in actions:
            row = [0, 0]
            row[s] = 1
            row -= gamma * P[a][s]
            A.append(row)
            b.append(expected_reward(s, a))

    res = linprog(c, A_ub=A, b_ub=b, method="highs")
    print("Функция ценности:", res.x)
    return res.x

if __name__ == "__main__":
    brute_force_finite()
    policy_iteration_finite()
    print("\nВсе стационарные стратегии:", stationary_strategies())
    brute_force_infinite()
    policy_iteration_infinite()
    linear_programming_solution()
