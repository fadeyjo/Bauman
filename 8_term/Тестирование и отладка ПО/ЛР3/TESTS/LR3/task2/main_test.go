package main

import (
	"bufio"
	"fmt"
	"os"
	"reflect"
	"sort"
	"strings"
	"testing"
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

func (i *Inventory) GetStatus(qty int) string {
	switch {
	case qty >= 100:
		return "много"
	case qty >= 30:
		return "мало"
	case qty > 0:
		return "критично"
	default:
		return "нет"
	}
}

func (i *Inventory) GetCategoryItems(category string, onlyLow bool, maxQty int) []string {
	cat, exists := i.Categories[category]
	if !exists {
		return []string{"Ошибка: категория не найдена"}
	}

	var lines []string
	lines = append(lines, "Категория: "+strings.ToUpper(category))

	for name, p := range cat {
		if onlyLow && p.Quantity >= 100 {
			continue
		}
		if maxQty > 0 && p.Quantity > maxQty {
			continue
		}

		status := i.GetStatus(p.Quantity)
		line := fmt.Sprintf("%s|%s", name, status)
		lines = append(lines, line)
	}

	if len(lines) == 1 {
		lines = append(lines, "Нет подходящих товаров")
	}

	sort.Strings(lines[1:])
	return lines
}

func (i *Inventory) GetLowStockReport() []string {
	var items []string
	for catName, cat := range i.Categories {
		for name, p := range cat {
			status := i.GetStatus(p.Quantity)
			if status == "мало" || status == "критично" || status == "нет" {
				line := fmt.Sprintf("%s|%s|%s|%d", catName, name, status, p.Quantity)
				items = append(items, line)
			}
		}
	}
	sort.Strings(items)
	return items
}

func RunConsole() {
	inv := NewInventory()
	scanner := bufio.NewScanner(os.Stdin)

	fmt.Println("Система инвентаризации (демо + тесты)")
	fmt.Println("Команды: все, низкий, <категория>, выход")

	for {
		fmt.Print("\n> ")
		if !scanner.Scan() {
			break
		}
		cmd := strings.TrimSpace(strings.ToLower(scanner.Text()))

		switch cmd {
		case "выход", "q", "exit":
			fmt.Println("До свидания!")
			return
		case "все":
			for cat := range inv.Categories {
				fmt.Println(strings.Join(inv.GetCategoryItems(cat, false, 0), "\n"))
			}
		case "низкий":
			fmt.Println(strings.Join(inv.GetLowStockReport(), "\n"))
		default:
			if _, ok := inv.Categories[cmd]; ok {
				fmt.Println(strings.Join(inv.GetCategoryItems(cmd, false, 0), "\n"))
			} else {
				fmt.Println("Неизвестная команда")
			}
		}
	}
}

func TestInventoryInitialization(t *testing.T) {
	inv := NewInventory()
	gotKeys := reflect.ValueOf(inv.Categories).MapKeys()
	var got []string
	for _, k := range gotKeys {
		got = append(got, k.String())
	}
	sort.Strings(got)

	want := []string{"одежда", "продукты", "электроника"}
	if !reflect.DeepEqual(got, want) {
		t.Errorf("got %v, want %v", got, want)
	}
}

func TestGetStatusLarge(t *testing.T) {
	inv := NewInventory()
	got := inv.GetStatus(150)
	want := "много"
	if got != want {
		t.Errorf("got %q, want %q", got, want)
	}
}

func TestGetStatusMedium(t *testing.T) {
	inv := NewInventory()
	got := inv.GetStatus(30)
	want := "мало"
	if got != want {
		t.Errorf("got %q, want %q", got, want)
	}
}

func TestGetStatusCritical(t *testing.T) {
	inv := NewInventory()
	got := inv.GetStatus(10)
	want := "критично"
	if got != want {
		t.Errorf("got %q, want %q", got, want)
	}
}

func TestGetStatusNone(t *testing.T) {
	inv := NewInventory()
	got := inv.GetStatus(0)
	want := "нет"
	if got != want {
		t.Errorf("got %q, want %q", got, want)
	}
}

func TestGetCategoryItemsFoodFull(t *testing.T) {
	inv := NewInventory()
	got := inv.GetCategoryItems("продукты", false, 0)

	want := []string{
		"Категория: ПРОДУКТЫ",
		"Вода 1.5л|много",
		"Чипсы|мало",
		"Шоколад|много",
		"Энергетик|мало",
	}

	if !reflect.DeepEqual(got, want) {
		t.Errorf("got:\n%q\nwant:\n%q", got, want)
	}
}

func TestGetCategoryItemsClothingLowOnly(t *testing.T) {
	inv := NewInventory()
	got := inv.GetCategoryItems("одежда", true, 0)

	want := []string{
		"Категория: ОДЕЖДА",
		"Джинсы|мало",
		"Кроссовки|мало",
		"Худи|мало",
	}

	if !reflect.DeepEqual(got, want) {
		t.Errorf("got:\n%q\nwant:\n%q", got, want)
	}
}

func TestGetCategoryItemsInvalid(t *testing.T) {
	inv := NewInventory()
	got := inv.GetCategoryItems("игрушки", false, 0)
	want := []string{"Ошибка: категория не найдена"}
	if !reflect.DeepEqual(got, want) {
		t.Errorf("got %v, want %v", got, want)
	}
}

func TestGetLowStockReportExact(t *testing.T) {
	inv := NewInventory()
	got := inv.GetLowStockReport()

	want := []string{
		"одежда|Джинсы|мало|47",
		"одежда|Кроссовки|мало|38",
		"одежда|Худи|мало|64",
		"продукты|Чипсы|мало|95",
		"продукты|Энергетик|мало|68",
		"электроника|Зарядка|мало|85",
		"электроника|Планшет|критично|19",
		"электроника|Смартфон|мало|42",
	}

	if !reflect.DeepEqual(got, want) {
		t.Errorf("got:\n%q\nwant:\n%q", got, want)
	}
}

func TestGetCategoryItemsFoodMaxQty50(t *testing.T) {
	inv := NewInventory()
	got := inv.GetCategoryItems("продукты", false, 50)

	want := []string{
		"Категория: ПРОДУКТЫ",
		"Нет подходящих товаров",
	}

	if !reflect.DeepEqual(got, want) {
		t.Errorf("got:\n%q\nwant:\n%q", got, want)
	}
}

func main() {
	if len(os.Args) > 1 && strings.HasSuffix(os.Args[0], ".test") {
		return
	}
	RunConsole()
}