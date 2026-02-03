using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Text.Json.Serialization;

namespace server.Models
{
    [Table("GPS_data")]
    public class GPSData
    {
        [Key]
        [Column("rec_id")]
        public ulong RecId { get; set; }

        [Column("rec_datetime")]
        public DateTime RecDatetime { get; set; }

        [Column("trip_id")]
        public ulong TripId { get; set; }

        [Column("latitude_deg")]
        public float LatitudeDEG { get; set; }

        [Column("longitude_deg")]
        public float LongitudeDEG { get; set; }

        [Column("accuracy_m")]
        public float? AccuracyM { get; set; }

        [Column("speed_kmh")]
        public float? SpeedKMH { get; set; }

        [Column("bearing_deg")]
        public ushort? BearingDEG { get; set; }

        [ForeignKey(nameof(TripId))]
        [JsonIgnore]
        public Trip Trip { get; set; } = null!;
    }
}
