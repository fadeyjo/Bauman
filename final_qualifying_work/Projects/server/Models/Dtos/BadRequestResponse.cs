using System.ComponentModel.DataAnnotations;

namespace server.Models.Dtos
{
    public class BadRequestResponse
    {
        [Required(ErrorMessage = "Текст ошибки обязателен")]
        public string Error { get; set; } = null!;

        public BadRequestResponse(string error)
        { 
            Error = error;
        }
    }
}
