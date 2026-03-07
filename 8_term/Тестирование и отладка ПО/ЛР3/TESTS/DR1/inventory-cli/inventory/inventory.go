package inventory

import "fmt"

// Category — категория товара
type Category string

const (
    CategoryElectronics Category = "electronics"
    CategoryClothing    Category = "clothing"
    CategoryFood        Category = "food"
    CategoryOther       Category = "other"
)

// Product — структура товара
type Product struct {
    Name     string
    Category Category
    Price    float64
    Stock    int
}

// Inventory — склад (карта артикул → товар)
type Inventory struct {
    items map[string]*Product // ключ — артикул (SKU)
}

// NewInventory создаёт пустой склад
func NewInventory() *Inventory {
    return &Inventory{
        items: make(map[string]*Product),
    }
}

// AddProduct добавляет или обновляет товар
func (inv *Inventory) AddProduct(sku, name string, cat Category, price float64, qty int) error {
    if sku == "" {
        return fmt.Errorf("артикул не может быть пустым")
    }
    if qty < 0 {
        return fmt.Errorf("количество не может быть отрицательным")
    }
    if price < 0 {
        return fmt.Errorf("цена не может быть отрицательной")
    }

    validCats := map[Category]bool{
        CategoryElectronics: true,
        CategoryClothing:    true,
        CategoryFood:        true,
        CategoryOther:       true,
    }

    if !validCats[cat] {
        return fmt.Errorf("неизвестная категория: %s", cat)
    }

    p := &Product{
        Name:     name,
        Category: cat,
        Price:    price,
        Stock:    qty,
    }

    inv.items[sku] = p
    return nil
}

// Sell — продажа (уменьшение остатка)
func (inv *Inventory) Sell(sku string, qty int) (string, error) {
    if qty <= 0 {
        return "", fmt.Errorf("количество продажи должно быть > 0")
    }

    prod, exists := inv.items[sku]
    if !exists {
        return "", fmt.Errorf("товар с артикулом %s не найден", sku)
    }

    if prod.Stock < qty {
        return "", fmt.Errorf("недостаточно товара %s на складе (есть %d, нужно %d)", prod.Name, prod.Stock, qty)
    }

    prod.Stock -= qty

    total := float64(qty) * prod.Price
    return fmt.Sprintf("Продано %d × %s за %.2f", qty, prod.Name, total), nil
}

// GetProductInfo — информация о товаре
func (inv *Inventory) GetProductInfo(sku string) (string, error) {
    prod, exists := inv.items[sku]
    if !exists {
        return "", fmt.Errorf("товар не найден")
    }
    return fmt.Sprintf("%s (%s), цена: %.2f, остаток: %d", prod.Name, prod.Category, prod.Price, prod.Stock), nil
}

// ListProducts — список всех товаров
func (inv *Inventory) ListProducts() []string {
    var list []string
    for sku, p := range inv.items {
        list = append(list, fmt.Sprintf("%s → %s (остаток %d)", sku, p.Name, p.Stock))
    }
    return list
}