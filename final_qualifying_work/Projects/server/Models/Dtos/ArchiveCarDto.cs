using System.ComponentModel.DataAnnotations;

namespace server.Models.Dtos
{
    public class ArchiveCarDto
    {
        [Required(ErrorMessage = "Не передан ID автомобиля")]
        public uint CarId { get; set; }

        [Required(ErrorMessage = "Не передано состояние")]
        public bool Archive {  get; set; }
    }
}
