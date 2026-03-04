using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace server.Models.Dtos
{
    public class CreateCarDto
    {
        [Required(ErrorMessage = "Введите VIN")]
        [StringLength(17, MinimumLength = 17, ErrorMessage = "Длина VIN должна быть 17 символов")]
        public string VINNumber { get; set; } = null!;

        [RegularExpression(@"^[авекмнорстухАВЕКМНОРСТУХ][0-9]{3}[авекмнорстухАВЕКМНОРСТУХ]{2}[0-9]{2,3}$", ErrorMessage = "Некорректный формат")]
        public string? StateNumber { get; set; }

        [Required(ErrorMessage = "Введите марку")]
        [StringLength(30, ErrorMessage = "Длина марки автомобиля должна быть не больше 30 символов")]
        public string BrandName { get; set; } = null!;

        [Required(ErrorMessage = "Введите модель")]
        [StringLength(30, ErrorMessage = "Длина модели автомобиля должна быть не больше 30 символов")]
        public string ModelName { get; set; } = null!;

        [Required(ErrorMessage = "Введите кузов")]
        [StringLength(30, ErrorMessage = "Длина кузова автомобиля должна быть не больше 30 символов")]
        public string BodyName { get; set; } = null!;

        [Required(ErrorMessage = "Введлите год выпуска")]
        [RegularExpression(@"^20[0-9]{2}$", ErrorMessage = "Год выпуска автомобиля должен быть не меньше 2000")]
        public ushort? ReleaseYear { get; set; }

        [Required(ErrorMessage = "Введите КПП")]
        [StringLength(30, ErrorMessage = "Длина типа КПП автомобиля должна быть не больше 30 символов")]
        public string GearboxName { get; set; } = null!;

        [Required(ErrorMessage = "Введите привод")]
        [StringLength(30, ErrorMessage = "Длина привода автомобиля должна быть не больше 30 символов")]
        public string DriveName { get; set; } = null!;

        [Required(ErrorMessage = "Введите массу")]
        public ushort? VehicleWeightKG { get; set; }

        [Required(ErrorMessage = "Введите мощность (л.с.)")]
        public ushort? EnginePowerHP { get; set; }

        [Required(ErrorMessage = "Введите мощность (кВт)")]
        public float? EnginePowerKW { get; set; }

        [Required(ErrorMessage = "Введите объём двигателя")]
        public float? EngineCapacityL { get; set; }

        [Required(ErrorMessage = "Введите объём бака")]
        public byte? TankCapacityL { get; set; }

        [Required(ErrorMessage = "Введите тип топлива")]
        [StringLength(30, ErrorMessage = "Длина типа топлива должна быть не больше 30 символов")]
        public string FuelTypeName { get; set; } = null!;
    }
}
