using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace server.Models
{
    [Table("car_drives")]
    public class CarDrive
    {
        [Key]
        [Column("drive_id")]
        public byte DriveId { get; set; }

        [Column("drive_name")]
        public string DriveName { get; set; } = null!;
    }
}
