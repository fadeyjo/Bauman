using System.ComponentModel.DataAnnotations;

namespace server.Models.Dtos
{
    public class StartTripDto
    {
        [Required(ErrorMessage = "Дата и время начала поездки обязательны")]
        public DateTime? StartDatetime { get; set; }

        [Required(ErrorMessage = "MAC-адрес устройства обязателен")]
        [RegularExpression(@"[0-9a-fA-F]:[0-9a-fA-F]:[0-9a-fA-F]:[0-9a-fA-F]:[0-9a-fA-F]:[0-9a-fA-F]", ErrorMessage = "Невалидный формат адреса ESP32")]
        public string MACAddress { get; set; } = null!;

        [Required(ErrorMessage = "ID автомобиля обязателен")]
        public uint? CarId { get; set; }
    }
}
