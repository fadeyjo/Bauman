using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using server.Database;
using server.Models.Dtos;
using server.Models.Entities;

namespace server.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class TripsController : ControllerBase
    {
        private readonly AppDbContext _context;

        public TripsController(AppDbContext context)
        {
            _context = context;
        }

        private ObjectResult ServerError()
        {
            return Problem(
                title: "Внутренняя ошибка сервера",
                statusCode: StatusCodes.Status500InternalServerError
            );
        }

        [HttpPost]
        public async Task<IActionResult> StartTrip([FromBody] StartTripDto body)
        {
            if (body.CarId is null)
                return Problem(
                    title: "Не передан ID автомобиля",
                    statusCode: StatusCodes.Status400BadRequest
                );

            if (body.StartDatetime is null)
                return Problem(
                    title: "Не переданы дата и время начала поездки",
                    statusCode: StatusCodes.Status400BadRequest
                );

            try
            {
                bool carExists = await _context.Cars.AnyAsync(c => c.CarId == body.CarId);

                if (!carExists)
                    return Problem(
                        title: "Автомобиль не найден",
                        statusCode: StatusCodes.Status404NotFound
                    );

                uint? deviceId = await GetDeviceIdByMACAddress(body.MACAddress);

                if (deviceId is null)
                {
                    var newDevice = new OBDIIDevice
                    {
                        MACAddress = body.MACAddress.ToUpper()
                    };

                    _context.OBDIIDevices.Add(newDevice);
                    await _context.SaveChangesAsync();

                    deviceId = newDevice.DeviceId;
                }

                if (deviceId is null)
                    return ServerError();

                var newTrip = new Trip()
                {
                    StartDatetime = (DateTime)body.StartDatetime,
                    DeviceId = (uint)deviceId,
                    CarId = (uint)body.CarId
                };

                _context.Trips.Add(newTrip);
                await _context.SaveChangesAsync();

                var tripRes =
                    await _context.Trips
                        .Where(t => t.TripId == newTrip.TripId)
                        .Select(t => 
                            new TripDto()
                            {
                                TripId = t.TripId,
                                StartDatetime = t.StartDatetime,
                                MACAddress = t.OBDIIDevice.MACAddress,
                                VINNumber = t.Car.VINNumber,
                                EndDatetime = t.EndDatetime
                            }
                        )
                        .FirstOrDefaultAsync();

                return CreatedAtAction(
                    nameof(GetTripById),
                    new { tripId = newTrip.TripId },
                    tripRes
                );
            }
            catch
            {
                return ServerError();
            }
        }

        [HttpGet("trip_id/{tripId}")]
        public async Task<IActionResult> GetTripById([FromRoute] ulong tripId)
        {
            try
            {
                var trip =
                    await _context.Trips
                        .Where(t => t.TripId == tripId)
                        .Select(t =>
                            new TripDto()
                            {
                                TripId = t.TripId,
                                StartDatetime = t.StartDatetime,
                                MACAddress = t.OBDIIDevice.MACAddress,
                                VINNumber = t.Car.VINNumber,
                                EndDatetime = t.EndDatetime
                            }
                        )
                        .FirstOrDefaultAsync();

                if (trip is null)
                    return Problem(
                        title: "Поездка не найдена",
                        statusCode: StatusCodes.Status404NotFound
                    );

                return Ok(trip);
            }
            catch
            {
                return ServerError();
            }
        }

        [HttpPut("end/{tripId}")]
        public async Task<IActionResult> EndTrip(
            [FromRoute] ulong tripId,
            [FromBody] EndTripDto body)
        {
            if (body.EndDatetime is null)
                return Problem(
                    title: "Не переданы дата и время окончания поездки",
                    statusCode: StatusCodes.Status400BadRequest
                );

            try
            {
                var trip = await _context.Trips
                    .FirstOrDefaultAsync(t => t.TripId == tripId);

                if (trip is null)
                    return Problem(
                        title: "Поездка не найдена",
                        statusCode: StatusCodes.Status404NotFound
                    );

                if (trip.StartDatetime >= body.EndDatetime)
                    return Problem(
                        title: "Окончание поездки должно быть позже начала",
                        statusCode: StatusCodes.Status400BadRequest
                    );

                trip.EndDatetime = body.EndDatetime;
                await _context.SaveChangesAsync();

                return NoContent();
            }
            catch
            {
                return ServerError();
            }
        }

        private async Task<uint?> GetDeviceIdByMACAddress(string macAddress)
        {
            var device =
                await _context.OBDIIDevices
                    .Where(d => d.MACAddress == macAddress.ToUpper())
                    .Select(d => new { d.DeviceId })
                    .FirstOrDefaultAsync();

            return device?.DeviceId;
        }
    }
}
