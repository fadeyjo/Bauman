using System.ComponentModel.DataAnnotations.Schema;
using System.Text.Json.Serialization;

namespace server.Models.Dtos
{
    public class PersonDto
    {
        public uint PersonId { get; set; }
        public string Email { get; set; } = null!;
        public string Phone { get; set; } = null!;
        public string LastName { get; set; } = null!;
        public string FirstName { get; set; } = null!;
        public string? Patronymic { get; set; }
        public DateOnly Birth { get; set; }
        public string? DriveLisense { get; set; }
    }
}
