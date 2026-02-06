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

        [HttpGet("vin/{vin}")]
        public async Task<IActionResult> GetCarByVIN([FromRoute] string vin)
        {
            if (vin.Length != 17)
                return BadRequest(new BadRequestResponse("Длина VIN номера должна быть 17 символов"));

            Car? car;
            try
            {
                car = await _context.Cars.FirstOrDefaultAsync(c => c.VINNumber.ToUpper() == vin.ToUpper());
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
                return StatusCode(500, new InternalServerErrorResponse(ex.Message));
            }

            if (car is null)
                return NotFound();

            return Ok(car);
        }

        [HttpPost]
        public async Task<IActionResult> CreateCar([FromBody] CreateCarRequest body)
        {
            if (!ModelState.IsValid)
                return BadRequest(ModelState);

            if (body.PersonId is null)
                return BadRequest(new BadRequestResponse("В теле запроса не передан ID пользователя"));

            if (body.EnginePowerHP is null)
                return BadRequest(new BadRequestResponse("В теле запроса не передана мощность двигателя (лс)"));

            if (body.EnginePowerKW is null)
                return BadRequest(new BadRequestResponse("В теле запроса не передана мощность двигателя (кВт)"));

            if (body.EngineCapacityL is null)
                return BadRequest(new BadRequestResponse("В теле запроса не передан объём двигателя (л)"));

            if (body.TankCapacityL is null)
                return BadRequest(new BadRequestResponse("В теле запроса не передан объём бака (л)"));

            if (body.ReleaseYear is null)
                return BadRequest(new BadRequestResponse("В теле запроса не передан год выпуска автомобиля"));

            if (body.VehicleWeightKG is null)
                return BadRequest(new BadRequestResponse("В теле запроса не передана масса автомобиля (кг)"));

            try
            {
                bool exists = await _context.Cars.AnyAsync(c => c.VINNumber == body.VINNumber.ToUpper());

                if (exists)
                    return BadRequest(new BadRequestResponse("Автомобиль с данным VIN номером уже существует"));

                if (!string.IsNullOrWhiteSpace(body.StateNumber))
                {
                    exists = await _context.Cars.AnyAsync(c => c.StateNumber != null && c.StateNumber == body.StateNumber.ToUpper());
                    if (exists)
                        return BadRequest(new BadRequestResponse("Автомобиль с данным государственным номером уже существует"));
                }

                var engineType =
                    await _context.EngineTypes
                        .Where(et => et.TypeName == body.EngineTypeName)
                        .Select (et => new { et.TypeId })
                        .FirstOrDefaultAsync();
                if (engineType is null)
                    return BadRequest(new BadRequestResponse("Неизвестный тип двигателя"));

                var fuelType =
                    await _context.FuelTypes
                        .Where(ft => ft.TypeName == body.FuelTypeName)
                        .Select(ft => new { ft.TypeId })
                        .FirstOrDefaultAsync();
                if (fuelType is null)
                    return BadRequest(new BadRequestResponse("Неизвестный тип топлива"));

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
                    return StatusCode(500, new InternalServerErrorResponse("Не удалось определить конфигурацию двигателя"));

                var carBrand =
                    await _context.CarBrands
                        .Where(cb => cb.BrandName == body.BrandName)
                        .Select(cb => new { cb.BrandId })
                        .FirstOrDefaultAsync();
                if (carBrand is null)
                    return BadRequest(new BadRequestResponse("Неизвестный бренд автомобиля"));

                var carBrandModel =
                    await _context.CarBrandsModels
                        .Where(cbm => cbm.BrandId == carBrand.BrandId && cbm.ModelName == body.ModelName)
                        .Select(cbm => new { cbm.CarBrandModelId })
                        .FirstOrDefaultAsync();
                if (carBrandModel is null)
                    return BadRequest(new BadRequestResponse("Неизвестная модель автомобиля"));

                var carBody =
                    await _context.CarBodies
                        .Where(cb => cb.BodyName == body.BodyName)
                        .Select(cbm => new { cbm.BodyId })
                        .FirstOrDefaultAsync();
                if (carBody is null)
                    return BadRequest(new BadRequestResponse("Неизвестный кузов автомобиля"));

                var carGearbox =
                    await _context.CarGearboxes
                        .Where(cg => cg.GearboxName == body.GearboxName)
                        .Select(cg => new { cg.GearboxId })
                        .FirstOrDefaultAsync();
                if (carGearbox is null)
                    return BadRequest(new BadRequestResponse("Неизвестный тип КПП"));

                var carDrive =
                    await _context.CarDrives
                        .Where(cd => cd.DriveName == body.DriveName)
                        .Select(cd => new { cd.DriveId })
                        .FirstOrDefaultAsync();
                if (carDrive is null)
                    return BadRequest(new BadRequestResponse("Неизвестный тип привода автомобиля"));

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
                    return StatusCode(500, new InternalServerErrorResponse("Не удалось определить конфигурацию автомобиля"));

                Car newCar = new ()
                {
                    PersonId = (uint)body.PersonId,
                    VINNumber = body.VINNumber.ToUpper(),
                    StateNumber = body.StateNumber?.ToUpper(),
                    CarConfigId = (uint)carConfigurationId
                };

                _context.Cars.Add(newCar);

                await _context.SaveChangesAsync();

                return CreatedAtAction(nameof(GetCarByVIN), new { vin = newCar.VINNumber }, new { newCar.VINNumber });
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
                return StatusCode(500, new InternalServerErrorResponse(ex.Message));
            }
        }

        [HttpGet("person_id/{personId}")]
        public async Task<IActionResult> GetCarsByPersonId([FromRoute] uint personId)
        {
            try
            {
                bool exists = await _context.Persons.AnyAsync(p => p.PersonId == personId);

                if (!exists)
                    return BadRequest(new BadRequestResponse("Пользователя не существует"));

                var cars =
                    await _context.Cars
                        .Where(c => c.PersonId == personId)
                        .Select(c => new
                        {
                            c.CarId,
                            c.PersonId,
                            c.VINNumber,
                            c.StateNumber,
                            c.CarConfiguration.CarBody.BodyName,
                            c.CarConfiguration.ReleaseYear,
                            c.CarConfiguration.CarGearbox.GearboxName,
                            c.CarConfiguration.CarDrive.DriveName,
                            c.CarConfiguration.VehicleWeightKG,
                            c.CarConfiguration.CarBrandModel.CarBrand.BrandName,
                            c.CarConfiguration.CarBrandModel.ModelName,
                            c.CarConfiguration.EngineConfiguration.EnginePowerHP,
                            c.CarConfiguration.EngineConfiguration.EnginePowerKW,
                            c.CarConfiguration.EngineConfiguration.EngineCapacityL,
                            c.CarConfiguration.EngineConfiguration.TankCapacityL,
                            EngineTypeName = c.CarConfiguration.EngineConfiguration.EngineType.TypeName,
                            FuelTypeName = c.CarConfiguration.EngineConfiguration.FuelType.TypeName
                        })
                        .ToListAsync();

                return Ok(cars);
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
                return StatusCode(500, new InternalServerErrorResponse(ex.Message));
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
