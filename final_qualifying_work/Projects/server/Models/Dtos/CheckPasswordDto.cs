using System.ComponentModel.DataAnnotations;

namespace server.Models.Dtos
{
    public class CheckPasswordDto
    {
        [Required(ErrorMessage = "Email пользователя обязателен")]
        public string Email { get; set; } = null!;

        [Required(ErrorMessage = "Пароля обязателен")]
        public string Password { get; set; } = null!;
    }
}
