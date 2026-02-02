using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace server.Models
{
    public class CreateCarRequestModel
    {
        [Required(ErrorMessage = "ID пользователя обязателен")]
        public uint? PersonId { get; set; }

        [Required(ErrorMessage = "VIN обязателен")]
        [StringLength(17, MinimumLength = 17, ErrorMessage = "Длина VIN должна быть 17")]
        public string VINNumber { get; set; } = null!;

        [RegularExpression(@"^[авекмнорстухАВЕКМНОРСТУХ][0-9]{3}[авекмнорстухАВЕКМНОРСТУХ]{2}[0-9]{2,3}$", ErrorMessage = "Невалидный формат гос. номера")]
        public string? StateNumber { get; set; }

        [Required(ErrorMessage = "Бренд автомобиля обязателен")]
        [StringLength(30, ErrorMessage = "Длина бренда автомобиля должна быть не больше 30")]
        public string BrandName { get; set; } = null!;

        [Required(ErrorMessage = "Модель автомобиля обязательна")]
        [StringLength(30, ErrorMessage = "Длина модели автомобиля должна быть не больше 30")]
        public string ModelName { get; set; } = null!;

        [Required(ErrorMessage = "Кузов автомобиля обязателен")]
        [StringLength(30, ErrorMessage = "Длина кузова автомобиля должна быть не больше 30")]
        public string BodyName { get; set; } = null!;

        [Required(ErrorMessage = "Год выпуска автомобиля обязателен")]
        [RegularExpression(@"^20[0-9]{2}$", ErrorMessage = "Год выпуска автомобиля должен быть не меньше 2000")]
        public ushort? ReleaseYear { get; set; }

        [Required(ErrorMessage = "Тип КПП автомобиля обязателен")]
        [StringLength(30, ErrorMessage = "Длина типа КПП автомобиля должна быть не больше 30")]
        public string GearboxName { get; set; } = null!;

        [Required(ErrorMessage = "Привод автомобиля обязателен")]
        [StringLength(30, ErrorMessage = "Длина привода автомобиля должна быть не больше 30")]
        public string DriveName { get; set; } = null!;

        [Required(ErrorMessage = "Масса автомобиля обязательна")]
        public ushort? VehicleWeightKG { get; set; }

        [Required(ErrorMessage = "Мощность автомобиля обязательна")]
        public ushort? EnginePowerHP { get; set; }

        [Required(ErrorMessage = "Мощность автомобиля обязательна")]
        public float? EnginePowerKW { get; set; }

        [Required(ErrorMessage = "Объём двигателя обязателен")]
        public float? EngineCapacityL { get; set; }

        [Required(ErrorMessage = "Тип двигателя обязателен")]
        [StringLength(30, ErrorMessage = "Длина типа двигателя должна быть не больше 30")]
        public string EngineTypeName { get; set; } = null!;

        [Required(ErrorMessage = "Объём бака обязателен")]
        public byte? TankCapacityL { get; set; }

        [Required(ErrorMessage = "Тип топлива обязателен")]
        [StringLength(30, ErrorMessage = "Длина типа топлива должна быть не больше 30")]
        public string FuelTypeName { get; set; } = null!;
    }
}
