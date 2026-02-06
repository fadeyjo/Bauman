using System.ComponentModel.DataAnnotations;

namespace server.Models.Dtos
{
    public class InternalServerErrorResponse
    {
        [Required(ErrorMessage = "Текст ошибки обязателен")]
        public string Error { get; set; } = null!;

        public InternalServerErrorResponse(string error)
        {
            Error = error;
        }
    }
}
