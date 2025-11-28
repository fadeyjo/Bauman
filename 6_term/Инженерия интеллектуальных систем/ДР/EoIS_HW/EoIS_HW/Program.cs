using System;
using System.Collections.Generic;
using System.Linq;

namespace EoIS_HW
{
    class Program
    {
        static string[] Diagnoses =
        {
            "Кабель не подключен",
            "DHCP-сервер не работает",
            "Неисправен маршрутизатор",
            "Проблема на стороне провайдера",
            "Ошибка в адресе сайта или сайт недоступен",
            "Сеть работает корректно"
        };

        static double[,] KnowledgeBase =
        {
            { 1,   0,   0,   0,   0 },
            { 0.5, 1,   0,   0,   0 },
            { 0,   0.5, 1,   0,   0 },
            { 0,   0,   0.5, 1,   0 },
            { 0,   0,   0,   0.5, 1 },
            { 0,   0,   0,   0,   0 }
        };

        static void Main(string[] args)
        {
            Console.WriteLine("Диагностика сети. Ответьте 'да' или 'нет' на следующие вопросы:");

            bool cableConnected = Ask("Кабель подключён?");
            bool hasIp = Ask("Компьютер получил IP-адрес?");
            bool gatewayPing = Ask("Пингуется шлюз (например, 192.168.0.1)?");
            bool dnsPing = Ask("Пингуется DNS (например, 8.8.8.8)?");
            bool websiteLoads = Ask("Открывается ли нужный сайт?");

            int[] answers = {
                cableConnected ? 1 : 0,
                hasIp ? 1 : 0,
                gatewayPing ? 1 : 0,
                dnsPing ? 1 : 0,
                websiteLoads ? 1 : 0
            };

            double[] scores = new double[Diagnoses.Length];

            for (int i = 0; i < Diagnoses.Length; i++)
                for (int j = 0; j < answers.Length; j++)
                    if (answers[j] == 0)
                        scores[i] += KnowledgeBase[i, j];

            int bestMatchIndex = Array.IndexOf(scores, scores.Max());

            Console.WriteLine("\nДиагноз:");
            Console.WriteLine("- " + Diagnoses[bestMatchIndex]);
        }

        static bool Ask(string question)
        {
            Console.Write(question + " ");
            string answer = Console.ReadLine()?.Trim().ToLower();
            return answer == "да" || answer == "yes";
        }
    }
}
