using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace server.Models
{
    [Table("car_gearboxes")]
    public class CarGearbox
    {
        [Key]
        [Column("gearbox_id")]
        public byte GearboxId { get; set; }

        [Column("gearbox_name")]
        public string GearboxName { get; set; } = null!;
    }
}
