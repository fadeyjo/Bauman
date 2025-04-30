using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace EoIS_HW
{
    class Program
    {
        static void Main(string[] args)
        {
            Console.WriteLine("Диагностика сети. Ответьте 'да' или 'нет':");

            bool cableConnected = Ask("Кабель подключён?");
            bool ledBlinking = Ask("Горит индикатор сетевой карты?");
            bool hasIp = Ask("Компьютер получил IP-адрес?");
            bool gatewayPing = Ask("Пингуется шлюз (например, 192.168.0.1)?");
            bool dnsPing = Ask("Пингуется DNS (например, 8.8.8.8)?");
            bool websiteLoads = Ask("Открывается ли нужный сайт?");

            var explanation = new List<string>();

            if (!cableConnected)
            {
                explanation.Add("Нет подключения — проверьте сетевой кабель.");
            }
            else if (!hasIp)
            {
                explanation.Add("Кабель есть, но нет IP — проверьте DHCP на роутере.");
            }
            else if (!gatewayPing)
            {
                explanation.Add("Нет связи с маршрутизатором — возможно он неисправен.");
            }
            else if (!dnsPing)
            {
                explanation.Add("Нет выхода в интернет — проблема на стороне провайдера.");
            }
            else if (!websiteLoads)
            {
                explanation.Add("DNS и интернет есть — проверьте корректность адреса сайта или попробуйте другой.");
            }
            else
            {
                explanation.Add("Сеть работает корректно.");
            }

            Console.WriteLine("\nДиагноз:");
            foreach (var line in explanation)
                Console.WriteLine("- " + line);
        }

        static bool Ask(string question)
        {
            Console.Write(question + " ");
            string answer = Console.ReadLine()?.ToLower();
            return answer == "да" || answer == "yes";
        }
    }
}
