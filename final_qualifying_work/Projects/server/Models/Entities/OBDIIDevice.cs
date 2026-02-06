using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace server.Models.Entities
{
    [Table("OBDII_devices")]
    public class OBDIIDevice
    {
        [Key]
        [Column("device_id")]
        public uint DeviceId { get; set; }

        [Column("MAC_address")]
        public string MACAddress { get; set; } = null!;
    }
}
