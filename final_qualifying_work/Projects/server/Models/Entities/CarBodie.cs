using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace server.Models.Entities
{
    [Table("car_bodies")]
    public class CarBody
    {
        [Key]
        [Column("body_id")]
        public byte BodyId { get; set; }

        [Column("body_name")]
        public string BodyName { get; set; } = null!;
    }
}
