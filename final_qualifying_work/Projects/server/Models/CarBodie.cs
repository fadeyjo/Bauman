using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace server.Models
{
    [Table("car_bodies")]
    public class CarBodie
    {
        [Key]
        [Column("bodie_id")]
        public byte BodieId { get; set; }

        [Column("bodie_name")]
        public string BodieName { get; set; } = null!;
    }
}
