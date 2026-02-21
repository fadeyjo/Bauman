using System.ComponentModel.DataAnnotations;

namespace server.Models.Dtos
{
    public class LogoutDto
    {
        [Required(ErrorMessage = "Не передан refresh токен")]
        public string RefreshToken { get; set; } = null!;
    }
}
