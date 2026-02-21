using Microsoft.EntityFrameworkCore.Metadata.Internal;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Text.Json.Serialization;

namespace server.Models.Entities
{
    [Table("cars")]
    public class Car
    {
        [Key]
        [Column("car_id")]
        public uint CarId { get; set; }

        [Column("person_id")]
        public uint PersonId { get; set; }

        [Column("VIN_number")]
        public string VINNumber { get; set; } = null!;

        [Column("state_number")]
        public string? StateNumber { get; set; }

        [Column("car_config_id")]
        public uint CarConfigId { get; set; }

        [Column("is_archived")]
        public bool IsArchived { get; set; }

        [ForeignKey(nameof(PersonId))]
        public Person Person { get; set; } = null!;

        [ForeignKey(nameof(CarConfigId))]
        public CarConfiguration CarConfiguration { get; set; } = null!;
    }
}
