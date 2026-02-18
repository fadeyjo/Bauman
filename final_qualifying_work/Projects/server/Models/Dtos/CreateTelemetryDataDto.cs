using System.ComponentModel.DataAnnotations;

namespace server.Models.Dtos
{
    public class CreateTelemetryDataDto
    {
        [Required(ErrorMessage = "Дата и время записи обязательны")]
        public DateTime? RecDatetime { get; set; }

        [Required(ErrorMessage = "Сервис OBDII обязателен")]
        public byte? ServiceId { get; set; }

        [Required(ErrorMessage = "PID обязателен")]
        public ushort? PID { get; set; }

        [Required(ErrorMessage = "ID ЭБУ обязатален")]
        public byte[]? ECUId { get; set; }

        [Required(ErrorMessage = "Длина OBDII ответа обязатальна")]
        public byte? ResponseDlc { get; set; }

        public byte[]? Response {  get; set; }

        [Required(ErrorMessage = "ID поездки обязателен")]
        public ulong? TripId { get; set; }
    }
}
