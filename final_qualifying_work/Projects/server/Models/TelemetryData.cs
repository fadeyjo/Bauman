using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace server.Models
{
    [Table("telemetry_data")]
    public class TelemetryData
    {
        [Key]
        [Column("rec_id")]
        public ulong RecId { get; set; }

        [Column("rec_datetime")]
        public DateTime RecDatetime { get; set; }

        [Column("OBDII_PID_id")]
        public uint OBDIIPIDId { get; set; }

        [Column("ECU_id")]
        public byte[] ECUId { get; set; } = null!;

        [Column("response_dlc")]
        public byte ResponseDLC { get; set; }

        [Column("response")]
        public byte[]? Response { get; set; }

        [Column("trip_id")]
        public ulong TripId { get; set; }

        [ForeignKey(nameof(TripId))]
        public Trip Trip { get; set; } = null!;

        [ForeignKey(nameof(OBDIIPIDId))]
        public OBDIIPID OBDIIPID { get; set; } = null!;
    }
}
