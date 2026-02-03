using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Text.Json.Serialization;

namespace server.Models
{
    [Table("car_brands_models")]
    public class CarBrandModel
    {
        [Key]
        [Column("car_brand_model_id")]
        public uint CarBrandModelId { get; set; }

        [Column("model_name")]
        public string ModelName { get; set; } = null!;

        [Column("brand_id")]
        public ushort BrandId { get; set; }

        [ForeignKey(nameof(BrandId))]
        [JsonIgnore]
        public CarBrand CarBrand { get; set; } = null!;
    }
}
