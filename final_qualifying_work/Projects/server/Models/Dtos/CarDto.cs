using server.Models.Entities;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Text.Json.Serialization;

namespace server.Models.Dtos
{
    public class CarDto
    {
        public uint CarId { get; set; }
        public uint PersonId { get; set; }
        public string VINNumber { get; set; } = null!;
        public string? StateNumber { get; set; }
        public string ModelName { get; set; } = null!;
        public string BrandName { get; set; } = null!;
        public string BodyName { get; set; } = null!;
        public ushort ReleaseYear { get; set; }
        public string GearboxName { get; set; } = null!;
        public string DriveName { get; set; } = null!;
        public ushort EnginePowerHP { get; set; }
        public float EnginePowerKW { get; set; }
        public string EngineTypeName { get; set; } = null!;
        public float EngineCapacityL { get; set; }
        public byte TankCapacityL { get; set; }
        public string FuelTypeName { get; set; } = null!;
        public ushort VehicleWeightKG { get; set; }
        public bool IsArchived { get; set; }
    }
}
