
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt

from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler

from tensorflow import keras
from tensorflow.keras import layers


def generate_housing_data(n_samples=1000, random_state=42):
    np.random.seed(random_state)

    size = np.random.normal(150, 50, n_samples)          # площадь, м2
    bedrooms = np.random.randint(1, 6, n_samples)        # число спален
    bathrooms = np.random.randint(1, 4, n_samples)       # число ванных
    location_score = np.random.uniform(1, 10, n_samples) # «качество» района
    age = np.random.exponential(20, n_samples)           # возраст дома, лет

    base_price = 100000
    price = (
        base_price
        + size * 2000
        + bedrooms * 50000
        + bathrooms * 30000
        + location_score * 15000
        - age * 3000
        + size * location_score * 100 
        + np.random.normal(0, 50000, n_samples)  # шум
    )

    data = pd.DataFrame(
        {
            "size": size,
            "bedrooms": bedrooms,
            "bathrooms": bathrooms,
            "location_score": location_score,
            "age": age,
            "price": price,
        }
    )
    return data


def visualize_housing_data(data):
    fig, axes = plt.subplots(2, 2, figsize=(15, 10))

    axes[0, 0].scatter(data["size"], data["price"], alpha=0.6)
    axes[0, 0].set_xlabel("Площадь (м²)")
    axes[0, 0].set_ylabel("Цена")
    axes[0, 0].set_title("Цена и площадь")

    bedroom_price = data.groupby("bedrooms")["price"].mean()
    axes[0, 1].bar(bedroom_price.index, bedroom_price.values)
    axes[0, 1].set_xlabel("Спальни")
    axes[0, 1].set_ylabel("Средняя цена")
    axes[0, 1].set_title("Цена и кол-во спален")

    axes[1, 0].hist(data["price"], bins=50, alpha=0.7)
    axes[1, 0].set_xlabel("Цена")
    axes[1, 0].set_ylabel("Частота")
    axes[1, 0].set_title("Распределение цен")

    axes[1, 1].scatter(data["location_score"], data["price"], alpha=0.6)
    axes[1, 1].set_xlabel("Оценка локации")
    axes[1, 1].set_ylabel("Цена")
    axes[1, 1].set_title("Цена и локация")

    plt.tight_layout()
    plt.show()


def main():
    housing_data = generate_housing_data(2000)
    print(housing_data.head())
    print(f"\nРазмер датасета: {housing_data.shape}")

    visualize_housing_data(housing_data)

    X = housing_data[["size", "bedrooms", "bathrooms", "location_score", "age"]].values
    y = housing_data["price"].values

    X_train, X_test, y_train, y_test = train_test_split(
        X, y, test_size=0.2, random_state=42
    )

    scaler = StandardScaler()
    X_train_scaled = scaler.fit_transform(X_train)
    X_test_scaled = scaler.transform(X_test)

    model = keras.Sequential(
        [
            layers.Dense(64, activation="relu", input_shape=(X_train_scaled.shape[1],)),
            layers.Dense(32, activation="relu"),
            layers.Dense(1), 
        ]
    )
    model.compile(optimizer="adam", loss="mse", metrics=["mae"])

    history = model.fit(
        X_train_scaled,
        y_train,
        epochs=50,
        batch_size=32,
        validation_split=0.2,
        verbose=1,
    )

    test_loss, test_mae = model.evaluate(X_test_scaled, y_test, verbose=0)
    print(f"\nTest MSE: {test_loss:.2f}")
    print(f"Test MAE: {test_mae:.2f}")

    example_inputs = np.array(
        [
            [150, 1, 1, 5.0, 10],  
            [130, 2, 2, 8.5, 5],   
            [200, 3, 2, 9.0, 15],   
        ]
    )

    example_inputs_scaled = scaler.transform(example_inputs)
    predictions = model.predict(example_inputs_scaled)

    for i, (features, pred) in enumerate(zip(example_inputs, predictions)):
        size, beds, baths, loc, age = features
        print(f"\nПример {i+1}:")
        print(
            f"Площадь: {size} м², спальни: {beds}, ванные: {baths}, "
            f"локация: {loc:.1f}, возраст: {age} лет"
        )
        print(f"Прогнозируемая цена: {pred[0]:.2f} у.е.")


if __name__ == "__main__":
    main()
