package main

import (
	"bufio"
	"fmt"
	"os"
	"strconv"
	"strings"
)

type Product struct {
	Name     string
	Quantity int
}

type Category map[string]Product 

type Inventory struct {
	Categories map[string]Category
}

func NewInventory() *Inventory {
	return &Inventory{
		Categories: map[string]Category{
			"электроника": {
				"Смартфон":  {Name: "Смартфон", Quantity: 42},
				"Наушники":  {Name: "Наушники", Quantity: 118},
				"Зарядка":   {Name: "Зарядка", Quantity: 85},
				"Планшет":   {Name: "Планшет", Quantity: 19},
			},
			"одежда": {
				"Футболка":  {Name: "Футболка", Quantity: 210},
				"Худи":      {Name: "Худи", Quantity: 64},
				"Кроссовки": {Name: "Кроссовки", Quantity: 38},
				"Джинсы":    {Name: "Джинсы", Quantity: 47},
			},
			"продукты": {
				"Вода 1.5л":   {Name: "Вода 1.5л", Quantity: 320},
				"Шоколад":     {Name: "Шоколад", Quantity: 180},
				"Чипсы":       {Name: "Чипсы", Quantity: 95},
				"Энергетик":   {Name: "Энергетик", Quantity: 68},
			},
		},
	}
}

func (i *Inventory) GetStatus(qty int) (emoji, text string) {
	switch {
	case qty >= 100:
		return "✔️", "в наличии много"
	case qty >= 30:
		return "⚠️", "заканчивается"
	case qty > 0:
		return "❗", "срочно заказать!"
	default:
		return "✖️", "отсутствует"
	}
}

func (i *Inventory) ShowCategory(category string, onlyLow bool, maxQty int) {
	cat, exists := i.Categories[category]
	if !exists {
		fmt.Printf("Категория '%s' не найдена\n", category)
		return
	}

	fmt.Printf("\n┌─ Категория: %s ───────────────────────────────┐\n", strings.ToUpper(category))
	found := false

	for _, p := range cat {
		if onlyLow && p.Quantity >= 100 {
			continue
		}
		if maxQty > 0 && p.Quantity > maxQty {
			continue
		}

		emoji, status := i.GetStatus(p.Quantity)
		fmt.Printf("│  %-18s %4d шт   %s %s\n", p.Name, p.Quantity, emoji, status)
		found = true
	}

	if !found {
		fmt.Println("│  Нет товаров, соответствующих условиям")
	}
	fmt.Println("└──────────────────────────────────────────────────┘")
}

func (i *Inventory) LowStockReport() {
	fmt.Println("\n=== Товары, которые заканчиваются или отсутствуют ===")
	issues := false

	for catName, cat := range i.Categories {
		for _, p := range cat {
			emoji, status := i.GetStatus(p.Quantity)
			if emoji == "⚠️" || emoji == "❗" || emoji == "✖️" {
				issues = true
				fmt.Printf("  %-12s %-18s %4d шт   %s %s\n", catName, p.Name, p.Quantity, emoji, status)
			}
		}
	}

	if !issues {
		fmt.Println("  Всё нормально — критически мало товаров нет ✓")
	}
}

func main() {
	app := NewInventory()
	scanner := bufio.NewScanner(os.Stdin)

	fmt.Println("Система инвентаризации склада")
	fmt.Println("Доступные категории: электроника, одежда, продукты")

	for {
		fmt.Println("\n" + strings.Repeat("─", 50))
		fmt.Println("Команды:")
		fmt.Println("  все        — показать все товары")
		fmt.Println("  низкий     — заканчивающиеся товары")
		fmt.Println("  <категория> — посмотреть категорию")
		fmt.Println("  выход / q  — завершить")

		fmt.Print("\n→ ")
		if !scanner.Scan() {
			break
		}
		cmd := strings.ToLower(strings.TrimSpace(scanner.Text()))

		switch cmd {
		case "выход", "exit", "q":
			fmt.Println("\nДо свидания! Хорошего дня.")
			return

		case "все":
			for cat := range app.Categories {
				app.ShowCategory(cat, false, 0)
			}

		case "низкий", "низкие":
			app.LowStockReport()

		default:
			if _, ok := app.Categories[cmd]; ok {
				fmt.Print("Только заканчивающиеся? (да/нет): ")
				scanner.Scan()
				onlyLow := strings.HasPrefix(strings.ToLower(scanner.Text()), "д")

				maxQty := 0
				if !onlyLow {
					fmt.Print("Максимальное количество для показа (Enter = все): ")
					scanner.Scan()
					input := strings.TrimSpace(scanner.Text())
					if input != "" {
						if n, err := strconv.Atoi(input); err == nil {
							maxQty = n
						} else {
							fmt.Println("Некорректное число → показываем все")
						}
					}
				}

				app.ShowCategory(cmd, onlyLow, maxQty)
			} else {
				fmt.Println("Неизвестная команда или категория")
			}
		}

		fmt.Print("\n[Enter для продолжения]")
		scanner.Scan()
	}
}