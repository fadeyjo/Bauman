using System.ComponentModel.DataAnnotations;

namespace server.Models
{
    public class StartTripRequestModel
    {
        [Required(ErrorMessage = "Дата и время начала поездки обязательны")]
        public DateTime? StartDatetime { get; set; }

        [Required(ErrorMessage = "MAC-адрес устройства обязателен")]
        [RegularExpression(@"[0-9a-fA-F]:[0-9a-fA-F]:[0-9a-fA-F]:[0-9a-fA-F]:[0-9a-fA-F]:[0-9a-fA-F]")]
        public string MACAddress { get; set; } = null!;

        [Required(ErrorMessage = "ID автомобиля обязателен")]
        public uint? CarId { get; set; }
    }
}
