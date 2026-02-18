using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Text.Json.Serialization;

namespace server.Models.Entities
{
    [Table("trips")]
    public class Trip
    {
        [Key]
        [Column("trip_id")]
        public ulong TripId { get; set; }

        [Column("start_datetime")]
        public DateTime StartDatetime { get; set; }

        [Column("device_id")]
        public uint DeviceId { get; set; }

        [Column("car_id")]
        public uint CarId { get; set; }

        [Column("end_datetime")]
        public DateTime? EndDatetime { get; set; }

        [ForeignKey(nameof(DeviceId))]
        public OBDIIDevice OBDIIDevice { get; set; } = null!;

        [ForeignKey(nameof(CarId))]
        public Car Car { get; set; } = null!;
    }
}
