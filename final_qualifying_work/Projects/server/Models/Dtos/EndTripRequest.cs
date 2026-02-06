using System.ComponentModel.DataAnnotations;

namespace server.Models.Dtos
{
    public class EndTripRequest
    {
        [Required(ErrorMessage = "Дата и время окончания поездки обязательны")]
        public DateTime? EndDatetime { get; set; }
    }
}
