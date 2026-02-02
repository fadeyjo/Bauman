using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace server.Models
{
    [Table("OBDII_services")]
    public class OBDIIService
    {
        [Key]
        [Column("service_id")]
        public byte ServiceId { get; set; }

        [Column("service_description")]
        public string ServiceDescription { get; set; } = null!;
    }
}
