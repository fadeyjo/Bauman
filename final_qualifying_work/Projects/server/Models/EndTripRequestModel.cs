using System.ComponentModel.DataAnnotations;

namespace server.Models
{
    public class EndTripRequestModel
    {
        [Required(ErrorMessage = "Дата и время окончания поездки обязательны")]
        public DateTime? EndDatetime { get; set; }
    }
}
