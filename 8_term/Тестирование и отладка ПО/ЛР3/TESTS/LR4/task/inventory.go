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
	MinStock int
}

type Inventory struct {
	products map[string]Product
}

func NewInventory() *Inventory {
	return &Inventory{
		products: make(map[string]Product),
	}
}

func (inv *Inventory) AddProduct(name string, quantity, minStock int) string {
	if name == "" {
		return "Ошибка: название товара не может быть пустым"
	}
	if quantity < 0 {
		return "Ошибка: количество не может быть отрицательным"
	}
	if minStock < 0 {
		return "Ошибка: минимальный запас не может быть отрицательным"
	}

	inv.products[name] = Product{
		Name:     name,
		Quantity: quantity,
		MinStock: minStock,
	}
	return "Товар успешно добавлен"
}

func (inv *Inventory) UpdateQuantity(name string, delta int) string {
	p, exists := inv.products[name]
	if !exists {
		return "Ошибка: товар не найден"
	}

	newQty := p.Quantity + delta
	if newQty < 0 {
		return "Ошибка: количество не может стать отрицательным"
	}

	p.Quantity = newQty
	inv.products[name] = p

	if newQty <= p.MinStock {
		return fmt.Sprintf("Обновлено. Внимание: %s — запас критически низкий (%d ≤ %d)", name, newQty, p.MinStock)
	}
	return "Количество обновлено"
}

func (inv *Inventory) GetStatus(name string) string {
	p, exists := inv.products[name]
	if !exists {
		return "Товар не найден"
	}

	status := "нормальный"
	if p.Quantity <= p.MinStock {
		status = "критически низкий"
	}

	return fmt.Sprintf("%s: %d шт. (мин. %d) → %s", name, p.Quantity, p.MinStock, status)
}

// -----------------------------------------------------

func main() {
	inv := NewInventory()
	scanner := bufio.NewScanner(os.Stdin)

	fmt.Println("Система инвентаризации продукции (выйти → exit)")

	for {
		fmt.Print("\nКоманда (add / update / status / exit): ")
		scanner.Scan()
		cmd := strings.ToLower(strings.TrimSpace(scanner.Text()))

		if cmd == "exit" {
			fmt.Println("До свидания!")
			break
		}

		switch cmd {
		case "add":
			fmt.Print("Название товара: ")
			scanner.Scan()
			name := strings.TrimSpace(scanner.Text())

			fmt.Print("Количество: ")
			scanner.Scan()
			qStr := strings.TrimSpace(scanner.Text())
			quantity, _ := strconv.Atoi(qStr)

			fmt.Print("Минимальный запас: ")
			scanner.Scan()
			mStr := strings.TrimSpace(scanner.Text())
			minStock, _ := strconv.Atoi(mStr)

			result := inv.AddProduct(name, quantity, minStock)
			fmt.Println(result)

		case "update":
			fmt.Print("Название товара: ")
			scanner.Scan()
			name := strings.TrimSpace(scanner.Text())

			fmt.Print("Изменение количества (+/-): ")
			scanner.Scan()
			dStr := strings.TrimSpace(scanner.Text())
			delta, _ := strconv.Atoi(dStr)

			result := inv.UpdateQuantity(name, delta)
			fmt.Println(result)

		case "status":
			fmt.Print("Название товара: ")
			scanner.Scan()
			name := strings.TrimSpace(scanner.Text())
			fmt.Println(inv.GetStatus(name))

		default:
			fmt.Println("Неизвестная команда")
		}
	}
}