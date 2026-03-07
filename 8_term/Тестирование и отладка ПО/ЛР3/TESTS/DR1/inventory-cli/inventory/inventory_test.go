package inventory

import (
    "testing"
	"strings"
)

func TestAddProduct(t *testing.T) {
    inv := NewInventory()

    err := inv.AddProduct("TEST1", "Тестовый товар", CategoryOther, 1000, 50)
    if err != nil {
        t.Fatalf("ожидали успешное добавление, получили ошибку: %v", err)
    }

    err = inv.AddProduct("", "Пустой артикул", CategoryOther, 500, 10)
    if err == nil {
        t.Error("ожидали ошибку при пустом SKU")
    }

    err = inv.AddProduct("TEST2", "Отрицательная цена", CategoryClothing, -100, 5)
    if err == nil {
        t.Error("ожидали ошибку при отрицательной цене")
    }
}

func TestSell(t *testing.T) {
    inv := NewInventory()
    _ = inv.AddProduct("P001", "Монитор", CategoryElectronics, 12000, 8)

    _, err := inv.Sell("P001", 3)
    if err != nil {
        t.Errorf("ожидали успешную продажу 3 шт, ошибка: %v", err)
    }

    info, _ := inv.GetProductInfo("P001")
    if !strings.Contains(info, "остаток: 5") {
        t.Errorf("ожидали остаток 5, получили: %s", info)
    }

    _, err = inv.Sell("P001", 10)
    if err == nil {
        t.Error("ожидали ошибку при продаже больше остатка")
    }
}

func TestInvalidCategory(t *testing.T) {
    inv := NewInventory()
    err := inv.AddProduct("BAD", "Что-то странное", "invalid_cat", 999, 1)
    if err == nil {
        t.Error("должна быть ошибка на неизвестную категорию")
    }
}