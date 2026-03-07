package main

import (
    "bufio"
    "fmt"
    "os"
    "strconv"
    "strings"

    "inventory-cli/inventory" 
)

func main() {
    inv := inventory.NewInventory()

    // Пример начальных данных
    _ = inv.AddProduct("A001", "Смартфон X", inventory.CategoryElectronics, 29990, 15)
    _ = inv.AddProduct("B102", "Футболка хлопок", inventory.CategoryClothing, 1490, 40)
    _ = inv.AddProduct("C777", "Шоколад горький", inventory.CategoryFood, 180, 200)

    scanner := bufio.NewScanner(os.Stdin)

    fmt.Println("Система учёта товаров (инвентаризация)")

    for {
        fmt.Println("\nКоманды: add, sell, info, list, exit")
        fmt.Print("→ ")

        if !scanner.Scan() {
            break
        }
        cmd := strings.TrimSpace(scanner.Text())
        parts := strings.Fields(cmd)

        if len(parts) == 0 {
            continue
        }

        switch parts[0] {
        case "add":
            if len(parts) < 6 {
                fmt.Println("Использование: add SKU НАЗВАНИЕ КАТЕГОРИЯ ЦЕНА КОЛИЧЕСТВО")
                continue
            }
            sku := parts[1]
            name := parts[2]
            cat := inventory.Category(parts[3])
            priceStr := parts[4]
            qtyStr := parts[5]

            price, _ := strconv.ParseFloat(priceStr, 64)
            qty, _ := strconv.Atoi(qtyStr)

            err := inv.AddProduct(sku, name, cat, price, qty)
            if err != nil {
                fmt.Println("Ошибка:", err)
            } else {
                fmt.Println("Товар добавлен/обновлён")
            }

        case "sell":
            if len(parts) < 3 {
                fmt.Println("Использование: sell SKU КОЛИЧЕСТВО")
                continue
            }
            sku := parts[1]
            qty, _ := strconv.Atoi(parts[2])

            msg, err := inv.Sell(sku, qty)
            if err != nil {
                fmt.Println("Ошибка:", err)
            } else {
                fmt.Println(msg)
            }

        case "info":
            if len(parts) < 2 {
                fmt.Println("Использование: info SKU")
                continue
            }
            info, err := inv.GetProductInfo(parts[1])
            if err != nil {
                fmt.Println("Ошибка:", err)
            } else {
                fmt.Println(info)
            }

        case "list":
            for _, line := range inv.ListProducts() {
                fmt.Println(line)
            }

        case "exit":
            fmt.Println("До свидания!")
            return

        default:
            fmt.Println("Неизвестная команда")
        }
    }
}