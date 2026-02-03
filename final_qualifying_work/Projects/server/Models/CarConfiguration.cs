using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Text.Json.Serialization;

namespace server.Models
{
    [Table("car_configurations")]
    public class CarConfiguration
    {
        [Key]
        [Column("car_config_id")]
        public uint CarConfigId { get; set; }

        [Column("car_brand_model_id")]
        public uint CarBrandModelId { get; set; }

        [Column("body_id")]
        public byte BodyId { get; set; }

        [Column("release_year")]
        public ushort ReleaseYear { get; set; }

        [Column("gearbox_id")]
        public byte GearboxId { get; set; }

        [Column("drive_id")]
        public byte DriveId { get; set; }

        [Column("engine_conf_id")]
        public uint EngineConfId { get; set; }

        [Column("vehicle_weight_kg")]
        public ushort VehicleWeightKG { get; set; }

        [ForeignKey(nameof(CarBrandModelId))]
        [JsonIgnore]
        public CarBrandModel CarBrandModel { get; set; } = null!;

        [ForeignKey(nameof(BodyId))]
        [JsonIgnore]
        public CarBody CarBody { get; set; } = null!;

        [ForeignKey(nameof(GearboxId))]
        [JsonIgnore]
        public CarGearbox CarGearbox { get; set; } = null!;

        [ForeignKey(nameof(DriveId))]
        [JsonIgnore]
        public CarDrive CarDrive { get; set; } = null!;

        [ForeignKey(nameof(EngineConfId))]
        [JsonIgnore]
        public EngineConfiguration EngineConfiguration { get; set; } = null!;
    }
}
