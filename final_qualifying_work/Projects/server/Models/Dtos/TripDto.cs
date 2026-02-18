using System.ComponentModel.DataAnnotations.Schema;

namespace server.Models.Dtos
{
    public class TripDto
    {
        public ulong TripId { get; set; }
        public DateTime StartDatetime { get; set; }
        public string MACAddress { get; set; } = null!;
        public string VINNumber { get; set; } = null!;
        public DateTime? EndDatetime { get; set; }
    }
}
