using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace server.Models.Dtos
{
    public class GPSDataDto
    {
        public ulong RecId { get; set; }
        public DateTime RecDatetime { get; set; }
        public ulong TripId { get; set; }
        public float LatitudeDEG { get; set; }
        public float LongitudeDEG { get; set; }
        public float? AccuracyM { get; set; }
        public float? SpeedKMH { get; set; }
        public ushort? BearingDEG { get; set; }
    }
}
