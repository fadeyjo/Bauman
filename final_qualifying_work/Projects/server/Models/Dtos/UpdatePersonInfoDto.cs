using System.ComponentModel.DataAnnotations;

namespace server.Models.Dtos
{
    public class UpdatePersonInfoDto
    {
        [Required(ErrorMessage = "Email обязателен")]
        [EmailAddress(ErrorMessage = "Невалидный формат email")]
        [StringLength(320, ErrorMessage = "Длина email не должна превышать 320 символов")]
        public string Email { get; set; } = null!;

        [Required(ErrorMessage = "Номер телефона обязателен")]
        [RegularExpression(@"^\+7[0-9]{10}$", ErrorMessage = "Невалидный формат номера телефона")]
        public string Phone { get; set; } = null!;

        [Required(ErrorMessage = "Фамилия обязательна")]
        [StringLength(50, MinimumLength = 2, ErrorMessage = "Длина фамилии должна быть от 2 до 50 символов")]
        public string LastName { get; set; } = null!;

        [Required(ErrorMessage = "Имя обязательно")]
        [StringLength(50, MinimumLength = 2, ErrorMessage = "Длина имени должна быть от 2 до 50 символов")]
        public string FirstName { get; set; } = null!;

        [StringLength(50, MinimumLength = 2, ErrorMessage = "Длина отчества должна быть от 2 до 50 символов")]
        public string? Patronymic { get; set; }

        [Required(ErrorMessage = "Даты рождения обязательна")]
        public DateOnly? Birth { get; set; }

        [StringLength(10, MinimumLength = 10, ErrorMessage = "Длина прав должна быть 10 символов")]
        public string? DriveLisense { get; set; }
    }
}
