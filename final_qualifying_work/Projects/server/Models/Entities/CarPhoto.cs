using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace server.Models.Entities
{
    [Table("car_photos")]
    public class CarPhoto
    {
        [Key]
        [Column("photo_id")]
        public uint PhotoId { get; set; }

        [Column("created_at")]
        public DateTime CreatedAt { get; set; }

        [Column("photo_url")]
        public string PhotoUrl { get; set; } = null!;

        [Column("car_id")]
        public uint CarId { get; set; }

        [Column("content_type")]
        public string ContentType { get; set; } = null!;

        [ForeignKey(nameof(CarId))]
        public Car Car { get; set; } = null!;
    }
}
