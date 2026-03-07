package main

import (
	"testing"
	"strings"
)

func TestAddProduct(t *testing.T) {
	inv := NewInventory()

	tests := []struct {
		name     string
		qty      int
		min      int
		expected string
	}{
		{"Яблоки", 150, 30, "Товар успешно добавлен"},
		{"", 100, 20, "Ошибка: название товара не может быть пустым"},
		{"Груши", -5, 10, "Ошибка: количество не может быть отрицательным"},
		{"Бананы", 80, -1, "Ошибка: минимальный запас не может быть отрицательным"},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			got := inv.AddProduct(tt.name, tt.qty, tt.min)
			if got != tt.expected {
				t.Errorf("AddProduct() = %q, ожидалось %q", got, tt.expected)
			}
		})
	}
}

func TestUpdateQuantity(t *testing.T) {
	inv := NewInventory()
	inv.AddProduct("Молоко", 40, 10)

	t.Run("normal-update", func(t *testing.T) {
		got := inv.UpdateQuantity("Молоко", -15)
		want := "Количество обновлено"
		if got != want {
			t.Errorf("got %q, want %q", got, want)
		}
	})

	t.Run("low-stock-warning", func(t *testing.T) {
		got := inv.UpdateQuantity("Молоко", -20)
		if !strings.Contains(got, "критически низкий") {
			t.Errorf("ожидается предупреждение о низком запасе, получено: %q", got)
		}
	})

	t.Run("not-found", func(t *testing.T) {
		got := inv.UpdateQuantity("Хлеб", 10)
		want := "Ошибка: товар не найден"
		if got != want {
			t.Errorf("got %q, want %q", got, want)
		}
	})

	t.Run("negative-quantity", func(t *testing.T) {
		got := inv.UpdateQuantity("Молоко", -100)
		want := "Ошибка: количество не может стать отрицательным"
		if got != want {
			t.Errorf("got %q, want %q", got, want)
		}
	})
}