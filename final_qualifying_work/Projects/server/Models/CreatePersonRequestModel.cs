using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Text.Json.Serialization;

namespace server.Models
{
    public class CreatePersonRequestModel
    {

        [Required(ErrorMessage = "Поле email обязательно")]
        [EmailAddress(ErrorMessage = "Невалидный формат email")]
        [StringLength(320, ErrorMessage = "Длина email не должна превышать 320 символов")]
        public string Email { get; set; } = null!;

        [Required(ErrorMessage = "Поле номера телефона обязательно")]
        [RegularExpression(@"^\+7[0-9]{10}$", ErrorMessage = "Невалидный формат номера телефона")]
        public string Phone { get; set; } = null!;

        [Required(ErrorMessage = "Поле фамилии обязательно")]
        [StringLength(50, MinimumLength = 2, ErrorMessage = "Длина фамилии должна быть от 2 до 50")]
        public string LastName { get; set; } = null!;

        [Required(ErrorMessage = "Поле имени обязательно")]
        [StringLength(50, MinimumLength = 2, ErrorMessage = "Длина имени должна быть от 2 до 50")]
        public string FirstName { get; set; } = null!;

        [StringLength(50, MinimumLength = 2, ErrorMessage = "Длина отчества должна быть от 2 до 50")]
        public string? Patronymic { get; set; }

        [Required(ErrorMessage = "Поле даты рождения обязательно")]
        public DateOnly? Birth { get; set; }

        [Required(ErrorMessage = "Поле захешированного пароля обязательно")]
        [StringLength(32, MinimumLength = 8, ErrorMessage = "Длина пароля должна быть от 8 до 32")]
        public string Password { get; set; } = null!;

        [StringLength(10, MinimumLength = 10, ErrorMessage = "Длина прав должна быть 10")]
        public string? DriveLisense { get; set; }

        [Required(ErrorMessage = "Поле прав доступа обязательно")]
        public byte? RightLevel { get; set; }
    }
}
