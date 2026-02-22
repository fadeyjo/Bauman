using System.ComponentModel.DataAnnotations;

namespace server.Models.Dtos
{
    public class CheckTokenDto
    {
        [Required(ErrorMessage = "Не передан токен")]
        public string RefreshToken { get; set; } = null!;
    }
}
