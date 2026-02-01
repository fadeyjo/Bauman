using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace server.Models
{
    [Table("car_brands")]
    public class CarBrand
    {
        [Key]
        [Column("brand_id")]
        public ushort BrandId { get; set; }

        [Column("brand_name")]
        public string BrandName { get; set; } = null!;
    }
}
