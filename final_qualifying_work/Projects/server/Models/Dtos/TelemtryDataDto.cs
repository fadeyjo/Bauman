using System.ComponentModel.DataAnnotations.Schema;

namespace server.Models.Dtos
{
    public class TelemtryDataDto
    {
        public ulong RecId { get; set; }
        public DateTime RecDatetime { get; set; }
        public byte ServiceId { get; set; }
        public ushort PID { get; set; }
        public byte[] ECUId { get; set; } = null!;
        public byte ResponseDLC { get; set; }
        public byte[]? Response { get; set; }
        public ulong TripId { get; set; }
    }
}
