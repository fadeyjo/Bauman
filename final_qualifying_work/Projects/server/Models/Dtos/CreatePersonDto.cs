using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Text.Json.Serialization;

namespace server.Models.Dtos
{
    public class CreatePersonDto
    {

        [Required(ErrorMessage = "Введите email")]
        [EmailAddress(ErrorMessage = "Некорректный формат email")]
        [StringLength(320, ErrorMessage = "Длина email не должна превышать 320 символов")]
        public string Email { get; set; } = null!;

        [Required(ErrorMessage = "Введите номер телефона")]
        [RegularExpression(@"^\+7[0-9]{10}$", ErrorMessage = "Номер телефона должен содержать 11 цифр")]
        public string Phone { get; set; } = null!;

        [Required(ErrorMessage = "Введите фамилию")]
        [StringLength(50, MinimumLength = 2, ErrorMessage = "Минимальная длина 2, максимальная - 50")]
        public string LastName { get; set; } = null!;

        [Required(ErrorMessage = "Введите имя")]
        [StringLength(50, MinimumLength = 2, ErrorMessage = "Минимальная длина 2, максимальная - 50")]
        public string FirstName { get; set; } = null!;

        [StringLength(50, MinimumLength = 2, ErrorMessage = "Минимальная длина 2, максимальная - 50")]
        public string? Patronymic { get; set; }

        [Required(ErrorMessage = "Введите дату рождения")]
        public DateOnly? Birth { get; set; }

        [Required(ErrorMessage = "Пароль обязателен")]
        [StringLength(32, MinimumLength = 8, ErrorMessage = "Длина пароля должна быть от 8 до 32 символов")]
        public string Password { get; set; } = null!;

        [StringLength(10, MinimumLength = 10, ErrorMessage = "ВУ должно состоять из 10 цифр")]
        public string? DriveLisense { get; set; }

        [Required(ErrorMessage = "Уровень прав доступа обязателен")]
        public byte? RightLevel { get; set; }
    }
}
