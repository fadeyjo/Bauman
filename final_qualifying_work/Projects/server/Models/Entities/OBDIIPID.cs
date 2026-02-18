using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Text.Json.Serialization;

namespace server.Models.Entities
{
    [Table("OBDII_PIDs")]
    public class OBDIIPID
    {
        [Key]
        [Column("OBDII_PID_id")]
        public uint OBDIIPIDId { get; set; }

        [Column("service_id")]
        public byte ServiceId { get; set; }

        [Column("PID")]
        public ushort PID { get; set; }

        [Column("PID_description")]
        public string PIDDescription { get; set; } = null!;

        [ForeignKey(nameof(ServiceId))]
        public OBDIIService OBDIIService { get; set; } = null!;
    }
}
