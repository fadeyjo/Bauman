using System.ComponentModel.DataAnnotations;

namespace server.Models.Dtos
{
    public class CheckPasswordDto
    {
        [Required(ErrorMessage = "Введите email")]
        [EmailAddress(ErrorMessage = "Некорректный формат email")]
        public string Email { get; set; } = null!;

        [Required(ErrorMessage = "Введите пароль")]
        public string Password { get; set; } = null!;
    }
}
