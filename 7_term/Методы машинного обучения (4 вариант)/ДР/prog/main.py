import pandas as pd
import numpy as np
import math

N = 24
np.random.seed(42)

names = [f"Patient_{i}" for i in range(1, N+1)]
birth_years = np.random.randint(1971, 1998, N)
employ = np.random.choice(["Office", "Factory", "Store", "IT", "Hospital"], N)
salary = np.random.randint(30000, 150000, N)
albumin = np.random.uniform(3.0, 5.5, N)
transferrin = np.random.uniform(200, 400, N)
ferritin = np.random.uniform(20, 300, N)

def calc_cost(by):
    if by < 1991:
        return (math.log(2013 - by) + 1) * 11000
    else:
        return (math.log2(2013 - by) + 1) * 10000

cost = [calc_cost(by) for by in birth_years]

years_obs = list(range(2010, 2014))

def total_social_deduction(by):
    total_cost = 0
    for y in years_obs:
        if by < 1991:
            total_cost += (math.log(y - by) + 1) * 11000
        else:
            total_cost += (math.log2(y - by) + 1) * 10000
    return total_cost * 0.13

social_deduction = [total_social_deduction(by) for by in birth_years]

df = pd.DataFrame({
    "Nrow": range(1, N+1),
    "Name": names,
    "BirthYear": birth_years,
    "Employ": employ,
    "Salary": salary,
    "Cost": cost,
    "Albumin": albumin,
    "TransFerrin": transferrin,
    "Ferritin": ferritin,
    "SocialDeduction": social_deduction
})

df_sorted = df.sort_values(by="Cost")

print("=== Исходный датафрейм ===")
print(df)

print("\n=== Датафрейм, отсортированный по стоимости лечения (Cost) ===")
print(df_sorted)

print("\n=== Минимальная стоимость лечения ===")
print(df_sorted.iloc[0])

print("\n=== Максимальная стоимость лечения ===")
print(df_sorted.iloc[-1])
