using System.ComponentModel.DataAnnotations;

namespace server.Models.Dtos
{
    public class CreateGPSDataDto
    {
        [Required(ErrorMessage = "Дата и время обязательны")]
        public DateTime? RecDatetime { get; set; }

        [Required(ErrorMessage = "ID поездки обязателен")]
        public ulong? TripId { get; set; }

        [Required(ErrorMessage = "Широта обязательна")]
        [Range(-90, 90, ErrorMessage = "Широта должна быть в диапазоне [-90; 90]")]
        public float? LatitudeDEG { get; set; }

        [Required(ErrorMessage = "Долгота обязательна")]
        [Range(-180, 180, ErrorMessage = "Долгота должна быть в диапазоне (-180; 180]")]
        public float? LongitudeDEG { get; set; }

        public float? AccuracyM { get; set; }

        public int? SpeedKMH { get; set; }

        [Range(0, 360, ErrorMessage = "Курс должен быть в диапазоне [0; 360)")]
        public ushort? BearingDEG { get; set; }
    }
}
