using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Text.Json.Serialization;

namespace server.Models
{
    [Table("engine_configurations")]
    public class EngineConfiguration
    {
        [Key]
        [Column("engine_config_id")]
        public uint EngineConfigId { get; set; }

        [Column("engine_power_hp")]
        public ushort EnginePowerHP { get; set; }

        [Column("engine_power_kW")]
        public float EnginePowerKW { get; set; }

        [Column("engine_type_id")]
        public byte EngineTypeId { get; set; }

        [Column("engine_capacity_l")]
        public float EngineCapacityL { get; set; }

        [Column("tank_capacity_l")]
        public byte TankCapacityL { get; set; }

        [Column("fuel_type_id")]
        public byte FuelTypeId { get; set; }

        [ForeignKey(nameof(EngineTypeId))]
        [JsonIgnore]
        public EngineType EngineType { get; set; } = null!;

        [ForeignKey(nameof(FuelTypeId))]
        [JsonIgnore]
        public FuelType FuelType { get; set; } = null!;
    }
}
