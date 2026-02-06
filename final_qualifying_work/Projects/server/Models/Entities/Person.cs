using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Text.Json.Serialization;

namespace server.Models.Entities
{
    [Table("persons")]
    public class Person
    {
        [Key]
        [Column("person_id")]
        public uint PersonId { get; set; }

        [Column("email")]
        public string Email { get; set; } = null!;

        [Column("phone")]
        public string Phone { get; set; } = null!;

        [Column("last_name")]
        public string LastName { get; set; } = null!;

        [Column("first_name")]
        public string FirstName { get; set; } = null!;

        [Column("patronymic")]
        public string? Patronymic { get; set; }

        [Column("birth")]
        public DateOnly Birth { get; set; }

        [Column("hashed_password")]
        [JsonIgnore]
        public string HashedPassword { get; set; } = null!;

        [Column("drive_lisense")]
        public string? DriveLisense { get; set; }

        [Column("right_level")]
        public byte RightLevel { get; set; }

        [ForeignKey(nameof(RightLevel))]
        [JsonIgnore]
        public AccessRight AccessRight { get; set; } = null!;
    }
}
