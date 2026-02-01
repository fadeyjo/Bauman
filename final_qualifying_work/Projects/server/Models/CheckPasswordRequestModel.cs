using System.ComponentModel.DataAnnotations;

namespace server.Models
{
    public class CheckPasswordRequestModel
    {
        [Required(ErrorMessage = "Поле ID человека обязательно")]
        public uint? PersonId { get; set; }

        [Required(ErrorMessage = "Поле пароля обязательно")]
        public string Password { get; set; } = null!;
    }
}
