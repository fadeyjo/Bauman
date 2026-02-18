using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Storage.Json;
using Microsoft.VisualBasic.FileIO;
using server.Database;
using server.Models;
using server.Models.Dtos;
using server.Models.Entities;
using System;

namespace server.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class CarsController : ControllerBase
    {
        private readonly AppDbContext _context;

        public CarsController(AppDbContext context)
        {
            _context=context;
        }

        private ObjectResult ServerError()
        {
            return Problem(
                title: "Внутренняя ошибка сервера",
                statusCode: StatusCodes.Status500InternalServerError
            );
        }

        [HttpGet("vin/{vin}")]
        public async Task<IActionResult> GetCarByVIN([FromRoute] string vin)
        {
            if (vin.Length != 17)
                return Problem(
                    title: "VIN должен содержать 17 символов",
                    statusCode: StatusCodes.Status400BadRequest
                );

            try
            {
                var car = await _context.Cars
                    .Where(c => c.VINNumber == vin.ToUpper())
                    .Select(c => new CarDto()
                    {
                        CarId = c.CarId,
                        PersonId = c.PersonId,
                        VINNumber = c.VINNumber,
                        StateNumber= c.StateNumber,
                        BodyName = c.CarConfiguration.CarBody.BodyName,
                        ReleaseYear = c.CarConfiguration.ReleaseYear,
                        GearboxName= c.CarConfiguration.CarGearbox.GearboxName,
                        DriveName= c.CarConfiguration.CarDrive.DriveName,
                        VehicleWeightKG= c.CarConfiguration.VehicleWeightKG,
                        BrandName= c.CarConfiguration.CarBrandModel.CarBrand.BrandName,
                        ModelName= c.CarConfiguration.CarBrandModel.ModelName,
                        EnginePowerHP= c.CarConfiguration.EngineConfiguration.EnginePowerHP,
                        EnginePowerKW = c.CarConfiguration.EngineConfiguration.EnginePowerKW,
                        EngineCapacityL= c.CarConfiguration.EngineConfiguration.EngineCapacityL,
                        TankCapacityL= c.CarConfiguration.EngineConfiguration.TankCapacityL,
                        EngineTypeName = c.CarConfiguration.EngineConfiguration.EngineType.TypeName,
                        FuelTypeName = c.CarConfiguration.EngineConfiguration.FuelType.TypeName
                    })
                    .FirstOrDefaultAsync();

                if (car is null)
                    return Problem(
                        title: "Автомобиль не найден",
                        statusCode: StatusCodes.Status404NotFound
                    );

                return Ok(car);
            }
            catch
            {
                return ServerError();
            }
        }

        [HttpPost]
        public async Task<IActionResult> CreateCar([FromBody] CreateCarDto body)
        {
            if (body.PersonId is null)
                return Problem(
                    title: "Не передан ID пользователя",
                    statusCode: StatusCodes.Status400BadRequest
                );

            if (body.EnginePowerHP is null)
                return Problem(
                    title: "Не передана мощность двигателя (лс)",
                    statusCode: StatusCodes.Status400BadRequest
                );

            if (body.EnginePowerKW is null)
                return Problem(
                    title: "Не передана мощность двигателя (кВт)",
                    statusCode: StatusCodes.Status400BadRequest
                );

            if (body.EngineCapacityL is null)
                return Problem(
                    title: "Не передан объём двигателя (л)",
                    statusCode: StatusCodes.Status400BadRequest
                );

            if (body.TankCapacityL is null)
                return Problem(
                    title: "Не передан объём бака (л)",
                    statusCode: StatusCodes.Status400BadRequest
                );

            if (body.ReleaseYear is null)
                return Problem(
                    title: "Не передан год выпуска автомобиля",
                    statusCode: StatusCodes.Status400BadRequest
                );

            if (body.VehicleWeightKG is null)
                return Problem(
                    title: "Не передана масса автомобиля",
                    statusCode: StatusCodes.Status400BadRequest
                );

            try
            {
                bool exists = await _context.Cars.AnyAsync(c => c.VINNumber == body.VINNumber.ToUpper());

                if (exists)
                    if (exists)
                        return Problem(
                            title: "Автомобиль с данным VIN уже существует",
                            statusCode: StatusCodes.Status409Conflict
                        );

                if (!string.IsNullOrWhiteSpace(body.StateNumber))
                {
                    exists = await _context.Cars.AnyAsync(c => c.StateNumber == body.StateNumber.ToUpper());
                    if (exists)
                        return Problem(
                            title: "Автомобиль с данным государственным номером уже существует",
                            statusCode: StatusCodes.Status409Conflict
                        );
                }

                var engineType =
                    await _context.EngineTypes
                        .Where(et => et.TypeName == body.EngineTypeName)
                        .Select (et => new { et.TypeId })
                        .FirstOrDefaultAsync();
                if (engineType is null)
                    return Problem(
                        title: "Неизвестный тип двигателя",
                        statusCode: StatusCodes.Status400BadRequest
                    );

                var fuelType =
                    await _context.FuelTypes
                        .Where(ft => ft.TypeName == body.FuelTypeName)
                        .Select(ft => new { ft.TypeId })
                        .FirstOrDefaultAsync();
                if (fuelType is null)
                    return Problem(
                        title: "Неизвестный тип топлива",
                        statusCode: StatusCodes.Status400BadRequest
                    );

                uint? engineConfigurationId = await GetEngineConfigurationId(
                    (ushort)body.EnginePowerHP, (float)body.EnginePowerKW,
                    engineType.TypeId, (float)body.EngineCapacityL,
                    (byte)body.TankCapacityL, fuelType.TypeId
                );

                if (engineConfigurationId is null)
                {
                    var newEngineConfiguration = new EngineConfiguration()
                    {
                        EnginePowerHP = (ushort)body.EnginePowerHP,
                        EnginePowerKW = (float)body.EnginePowerKW,
                        EngineTypeId = engineType.TypeId,
                        EngineCapacityL = (float)body.EngineCapacityL,
                        TankCapacityL = (byte)body.TankCapacityL,
                        FuelTypeId = fuelType.TypeId
                    };

                    _context.EngineConfigurations.Add(newEngineConfiguration);

                    await _context.SaveChangesAsync();

                    engineConfigurationId = newEngineConfiguration.EngineConfigId;
                }

                if (engineConfigurationId is null)
                    return ServerError();

                var carBrand =
                    await _context.CarBrands
                        .Where(cb => cb.BrandName == body.BrandName)
                        .Select(cb => new { cb.BrandId })
                        .FirstOrDefaultAsync();
                if (carBrand is null)
                    return ServerError();

                var carBrandModel =
                    await _context.CarBrandsModels
                        .Where(cbm => cbm.BrandId == carBrand.BrandId && cbm.ModelName == body.ModelName)
                        .Select(cbm => new { cbm.CarBrandModelId })
                        .FirstOrDefaultAsync();
                if (carBrandModel is null)
                    return Problem(
                        title: "Неизвестная модель автомобиля",
                        statusCode: StatusCodes.Status400BadRequest
                    );

                var carBody =
                    await _context.CarBodies
                        .Where(cb => cb.BodyName == body.BodyName)
                        .Select(cbm => new { cbm.BodyId })
                        .FirstOrDefaultAsync();
                if (carBody is null)
                    return Problem(
                        title: "Неизвестный кузов автомобиля",
                        statusCode: StatusCodes.Status400BadRequest
                    );

                var carGearbox =
                    await _context.CarGearboxes
                        .Where(cg => cg.GearboxName == body.GearboxName)
                        .Select(cg => new { cg.GearboxId })
                        .FirstOrDefaultAsync();
                if (carGearbox is null)
                    return Problem(
                        title: "Неизвестный тип КПП автомобиля",
                        statusCode: StatusCodes.Status400BadRequest
                    );

                var carDrive =
                    await _context.CarDrives
                        .Where(cd => cd.DriveName == body.DriveName)
                        .Select(cd => new { cd.DriveId })
                        .FirstOrDefaultAsync();
                if (carDrive is null)
                    return Problem(
                        title: "Неизвестный тип привода автомобиля",
                        statusCode: StatusCodes.Status400BadRequest
                    );

                uint? carConfigurationId = await GetCarConfigurationId(
                    carBrandModel.CarBrandModelId, carBody.BodyId,
                    (ushort)body.ReleaseYear, carGearbox.GearboxId,
                    carDrive.DriveId, (uint)engineConfigurationId,
                    (ushort)body.VehicleWeightKG
                );

                if (carConfigurationId is null)
                {
                    var newCarConfiguration = new CarConfiguration()
                    {
                        CarBrandModelId = carBrandModel.CarBrandModelId,
                        BodyId = carBody.BodyId,
                        ReleaseYear = (ushort)body.ReleaseYear,
                        GearboxId = carGearbox.GearboxId,
                        DriveId = carDrive.DriveId,
                        EngineConfId = (uint)engineConfigurationId,
                        VehicleWeightKG = (ushort)body.VehicleWeightKG
                    };

                    _context.CarConfigurations.Add(newCarConfiguration);

                    await _context.SaveChangesAsync();

                    carConfigurationId = newCarConfiguration.CarConfigId;
                }

                if (carConfigurationId is null)
                    return ServerError();

                Car newCar = new ()
                {
                    PersonId = (uint)body.PersonId,
                    VINNumber = body.VINNumber.ToUpper(),
                    StateNumber = body.StateNumber?.ToUpper(),
                    CarConfigId = (uint)carConfigurationId
                };

                _context.Cars.Add(newCar);

                await _context.SaveChangesAsync();

                var car = await _context.Cars
                    .Where(c => c.VINNumber == body.VINNumber.ToUpper())
                    .Select(c => new CarDto()
                    {
                        CarId = c.CarId,
                        PersonId = c.PersonId,
                        VINNumber = c.VINNumber,
                        StateNumber= c.StateNumber,
                        BodyName = c.CarConfiguration.CarBody.BodyName,
                        ReleaseYear = c.CarConfiguration.ReleaseYear,
                        GearboxName= c.CarConfiguration.CarGearbox.GearboxName,
                        DriveName= c.CarConfiguration.CarDrive.DriveName,
                        VehicleWeightKG= c.CarConfiguration.VehicleWeightKG,
                        BrandName= c.CarConfiguration.CarBrandModel.CarBrand.BrandName,
                        ModelName= c.CarConfiguration.CarBrandModel.ModelName,
                        EnginePowerHP= c.CarConfiguration.EngineConfiguration.EnginePowerHP,
                        EnginePowerKW = c.CarConfiguration.EngineConfiguration.EnginePowerKW,
                        EngineCapacityL= c.CarConfiguration.EngineConfiguration.EngineCapacityL,
                        TankCapacityL= c.CarConfiguration.EngineConfiguration.TankCapacityL,
                        EngineTypeName = c.CarConfiguration.EngineConfiguration.EngineType.TypeName,
                        FuelTypeName = c.CarConfiguration.EngineConfiguration.FuelType.TypeName
                    })
                    .FirstOrDefaultAsync();

                if (car is null)
                    return Problem(
                        title: "Автомобиль не найден",
                        statusCode: StatusCodes.Status404NotFound
                    );

                return CreatedAtAction(nameof(GetCarByVIN), new { vin = newCar.VINNumber }, car);
            }
            catch
            {
                return ServerError();
            }
        }

        [HttpGet("person_id/{personId}")]
        public async Task<IActionResult> GetCarsByPersonId([FromRoute] uint personId)
        {
            try
            {
                bool exists = await _context.Persons.AnyAsync(p => p.PersonId == personId);

                if (!exists)
                    return Problem(
                        title: "Пользователь не найден",
                        statusCode: StatusCodes.Status404NotFound
                    );

                var cars = await _context.Cars
                    .Where(c => c.PersonId == personId)
                    .Select(c => new CarDto()
                    {
                        CarId = c.CarId,
                        PersonId = c.PersonId,
                        VINNumber = c.VINNumber,
                        StateNumber= c.StateNumber,
                        BodyName = c.CarConfiguration.CarBody.BodyName,
                        ReleaseYear = c.CarConfiguration.ReleaseYear,
                        GearboxName= c.CarConfiguration.CarGearbox.GearboxName,
                        DriveName= c.CarConfiguration.CarDrive.DriveName,
                        VehicleWeightKG= c.CarConfiguration.VehicleWeightKG,
                        BrandName= c.CarConfiguration.CarBrandModel.CarBrand.BrandName,
                        ModelName= c.CarConfiguration.CarBrandModel.ModelName,
                        EnginePowerHP= c.CarConfiguration.EngineConfiguration.EnginePowerHP,
                        EnginePowerKW = c.CarConfiguration.EngineConfiguration.EnginePowerKW,
                        EngineCapacityL= c.CarConfiguration.EngineConfiguration.EngineCapacityL,
                        TankCapacityL= c.CarConfiguration.EngineConfiguration.TankCapacityL,
                        EngineTypeName = c.CarConfiguration.EngineConfiguration.EngineType.TypeName,
                        FuelTypeName = c.CarConfiguration.EngineConfiguration.FuelType.TypeName
                    })
                    .ToListAsync();

                return Ok(cars);
            }
            catch
            {
                return ServerError();
            }
        }

        [HttpPut]
        public async Task<IActionResult> UpdateCarInfo([FromBody] UpdateCarInfoDto body)
        {
            if (body.EnginePowerHP is null)
                return Problem(
                    title: "Не передана мощность двигателя (лс) автомобиля",
                    statusCode: StatusCodes.Status400BadRequest
                );

            if (body.EnginePowerKW is null)
                return Problem(
                    title: "Не передана мощность двигателя (кВт) автомобиля",
                    statusCode: StatusCodes.Status400BadRequest
                );

            if (body.EngineCapacityL is null)
                return Problem(
                    title: "Не передан объём двигателя (л) автомобиля",
                    statusCode: StatusCodes.Status400BadRequest
                );

            if (body.TankCapacityL is null)
                return Problem(
                    title: "Не передан объём бака (л) автомобиля",
                    statusCode: StatusCodes.Status400BadRequest
                );

            if (body.ReleaseYear is null)
                return Problem(
                    title: "Не передан год выпуска автомобиля",
                    statusCode: StatusCodes.Status400BadRequest
                );

            if (body.VehicleWeightKG is null)
                return Problem(
                    title: "Не передана масса (кг) автомобиля",
                    statusCode: StatusCodes.Status400BadRequest
                );

            try
            {
                var car =
                    await _context.Cars.FirstOrDefaultAsync(c => c.CarId == body.CarId);

                if (car is null)
                    return Problem(
                        title: "Автомобиль не найден",
                        statusCode: StatusCodes.Status404NotFound
                    );

                bool exists =
                    await _context.Cars.AnyAsync(c => c.VINNumber == body.VINNumber && c.CarId != body.CarId);

                if (exists)
                    return Problem(
                        title: "Автомобиль с данным VIN уже существует",
                        statusCode: StatusCodes.Status409Conflict
                    );

                if (!string.IsNullOrWhiteSpace(body.StateNumber))
                {
                    exists =
                        await _context.Cars.AllAsync(c => c.StateNumber == body.StateNumber && c.CarId != body.CarId);

                    if (exists)
                        return Problem(
                        title: "Автомобиль с данным государственным номером уже существует",
                        statusCode: StatusCodes.Status409Conflict
                    );
                }

                var engineType =
                    await _context.EngineTypes
                        .Where(et => et.TypeName == body.EngineTypeName)
                        .Select(et => new { et.TypeId })
                        .FirstOrDefaultAsync();
                if (engineType is null)
                    return Problem(
                        title: "Неизвестный тип двигателя",
                        statusCode: StatusCodes.Status400BadRequest
                    );

                var fuelType =
                    await _context.FuelTypes
                        .Where(ft => ft.TypeName == body.FuelTypeName)
                        .Select(ft => new { ft.TypeId })
                        .FirstOrDefaultAsync();
                if (fuelType is null)
                    return Problem(
                        title: "Неизвестный тип топлива",
                        statusCode: StatusCodes.Status400BadRequest
                    );

                uint? engineConfigurationId = await GetEngineConfigurationId(
                    (ushort)body.EnginePowerHP, (float)body.EnginePowerKW,
                    engineType.TypeId, (float)body.EngineCapacityL,
                    (byte)body.TankCapacityL, fuelType.TypeId
                );

                if (engineConfigurationId is null)
                {
                    var newEngineConfiguration = new EngineConfiguration()
                    {
                        EnginePowerHP = (ushort)body.EnginePowerHP,
                        EnginePowerKW = (float)body.EnginePowerKW,
                        EngineTypeId = engineType.TypeId,
                        EngineCapacityL = (float)body.EngineCapacityL,
                        TankCapacityL = (byte)body.TankCapacityL,
                        FuelTypeId = fuelType.TypeId
                    };

                    _context.EngineConfigurations.Add(newEngineConfiguration);

                    await _context.SaveChangesAsync();

                    engineConfigurationId = newEngineConfiguration.EngineConfigId;
                }

                if (engineConfigurationId is null)
                    return ServerError();

                var carBrand =
                    await _context.CarBrands
                        .Where(cb => cb.BrandName == body.BrandName)
                        .Select(cb => new { cb.BrandId })
                        .FirstOrDefaultAsync();
                if (carBrand is null)
                    return Problem(
                        title: "Неизвестный бренд автомобиля",
                        statusCode: StatusCodes.Status400BadRequest
                    );

                var carBrandModel =
                    await _context.CarBrandsModels
                        .Where(cbm => cbm.BrandId == carBrand.BrandId && cbm.ModelName == body.ModelName)
                        .Select(cbm => new { cbm.CarBrandModelId })
                        .FirstOrDefaultAsync();
                if (carBrandModel is null)
                    return Problem(
                        title: "Неизвестная модель автомобиля",
                        statusCode: StatusCodes.Status400BadRequest
                    );

                var carBody =
                    await _context.CarBodies
                        .Where(cb => cb.BodyName == body.BodyName)
                        .Select(cbm => new { cbm.BodyId })
                        .FirstOrDefaultAsync();
                if (carBody is null)
                    return Problem(
                        title: "Неизвестный тип кузова автомобиля",
                        statusCode: StatusCodes.Status400BadRequest
                    );

                var carGearbox =
                    await _context.CarGearboxes
                        .Where(cg => cg.GearboxName == body.GearboxName)
                        .Select(cg => new { cg.GearboxId })
                        .FirstOrDefaultAsync();
                if (carGearbox is null)
                    return Problem(
                        title: "Неизвестный тип КПП автомобиля",
                        statusCode: StatusCodes.Status400BadRequest
                    );

                var carDrive =
                    await _context.CarDrives
                        .Where(cd => cd.DriveName == body.DriveName)
                        .Select(cd => new { cd.DriveId })
                        .FirstOrDefaultAsync();
                if (carDrive is null)
                    return Problem(
                        title: "Неизвестный тип привода автомобиля",
                        statusCode: StatusCodes.Status400BadRequest
                    );

                uint? carConfigurationId = await GetCarConfigurationId(
                    carBrandModel.CarBrandModelId, carBody.BodyId,
                    (ushort)body.ReleaseYear, carGearbox.GearboxId,
                    carDrive.DriveId, (uint)engineConfigurationId,
                    (ushort)body.VehicleWeightKG
                );

                if (carConfigurationId is null)
                {
                    var newCarConfiguration = new CarConfiguration()
                    {
                        CarBrandModelId = carBrandModel.CarBrandModelId,
                        BodyId = carBody.BodyId,
                        ReleaseYear = (ushort)body.ReleaseYear,
                        GearboxId = carGearbox.GearboxId,
                        DriveId = carDrive.DriveId,
                        EngineConfId = (uint)engineConfigurationId,
                        VehicleWeightKG = (ushort)body.VehicleWeightKG
                    };

                    _context.CarConfigurations.Add(newCarConfiguration);

                    await _context.SaveChangesAsync();

                    carConfigurationId = newCarConfiguration.CarConfigId;
                }

                if (carConfigurationId is null)
                    return ServerError();

                car.VINNumber = body.VINNumber.ToUpper();
                car.StateNumber = body.StateNumber?.ToUpper();
                car.CarConfigId = (uint)carConfigurationId;

                await _context.SaveChangesAsync();

                return NoContent();
            }
            catch
            {
                return ServerError();
            }
        }

        private async Task<uint?> GetEngineConfigurationId(
            ushort enginePowerHP, float enginePowerKW,
            byte engineTypeId, float engineCapacityL,
            byte tankCapacityL, byte fuelTypeId
        )
        {
            var engineConfiguration =
                await _context.EngineConfigurations
                    .Where(ec =>
                        ec.EnginePowerHP == enginePowerHP &&
                        ec.EnginePowerKW == enginePowerKW &&
                        ec.EngineTypeId == engineTypeId &&
                        ec.EngineCapacityL == engineCapacityL &&
                        ec.TankCapacityL == tankCapacityL &&
                        ec.FuelTypeId == fuelTypeId
                    )
                    .Select(ec => new { ec.EngineConfigId })
                    .FirstOrDefaultAsync();

            if (engineConfiguration is null)
                return null;

            return engineConfiguration.EngineConfigId;
        }

        private async Task<uint?> GetCarConfigurationId(
            uint carBrandModelId, byte bodyId,
            ushort releaseYear, byte gearboxId,
            byte driveId, uint engineConfId,
            ushort vehicleWeightKG
        )
        {
            var carConfiguration =
            await _context.CarConfigurations
                .Where(cc =>
                    cc.CarBrandModelId == carBrandModelId &&
                    cc.BodyId == bodyId &&
                    cc.ReleaseYear == releaseYear &&
                    cc.GearboxId == gearboxId &&
                    cc.DriveId == driveId &&
                    cc.EngineConfId == engineConfId &&
                    cc.VehicleWeightKG == vehicleWeightKG
                )
                .Select(cc => new { cc.CarConfigId })
                .FirstOrDefaultAsync();

            if (carConfiguration is null)
                return null;

            return carConfiguration.CarConfigId;
        }
    }
}
