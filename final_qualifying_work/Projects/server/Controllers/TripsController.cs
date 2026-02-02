using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using server.Database;
using server.Models;

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

        [HttpPost]
        public async Task<IActionResult> StartTrip([FromBody] StartTripRequestModel body)
        {
            if (!ModelState.IsValid)
                return BadRequest(ModelState);

            if (body.CarId is null)
                return BadRequest("ID автомобиля обязателен");

            if (body.StartDatetime is null)
                return BadRequest("Дата и время поездки обязательны");

            body.MACAddress = body.MACAddress.ToUpper();

            try
            {
                bool exists = await _context.Cars.AnyAsync(c => c.CarId == body.CarId);

                if (!exists)
                    return BadRequest("Автомобиля с данным ID не существует");

                uint? deviceId = await GetDeviceIdByMACAddress(body.MACAddress);

                if (deviceId is null)
                {
                    var newOBDIIDevice = new OBDIIDevice {MACAddress = body.MACAddress};

                    _context.OBDIIDevices.Add(newOBDIIDevice);

                    await _context.SaveChangesAsync();

                    deviceId = newOBDIIDevice.DeviceId;
                }

                if (deviceId is null)
                    return StatusCode(500, "Не удалось найти контроллер");

                var newTrip = new Trip()
                {
                    StartDatetime = (DateTime)body.StartDatetime,
                    DeviceId = (uint)deviceId,
                    CarId = (uint)body.CarId
                };

                _context.Trips.Add(newTrip);

                await _context.SaveChangesAsync();

                return CreatedAtAction(nameof(GetTripById), new { tripId = newTrip.TripId }, new { tripId = newTrip.TripId });
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
                return StatusCode(500, ex.Message);
            }
        }

        [HttpGet("trip_id/{tripId}")]
        public async Task<IActionResult> GetTripById([FromRoute] ulong tripId)
        {
            Trip? trip;
            try
            {
                trip = await _context.Trips.FirstOrDefaultAsync(t => t.TripId == tripId);
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
                return StatusCode(500, ex.Message);
            }

            if (trip is null)
                return NotFound();

            return Ok(trip);
        }

        [HttpPut("end/{tripId}")]
        public async Task<IActionResult> EndTrip([FromRoute] ulong tripId, [FromBody] EndTripRequestModel body)
        {
            try
            {
                var trip = await _context.Trips.FirstOrDefaultAsync(t => t.TripId == tripId);

                if (trip is null)
                    return BadRequest("Поездка не найдена");

                if (trip.StartDatetime >= body.EndDatetime)
                    return BadRequest("Окончание поездки должно быть позже начала");

                trip.EndDatetime = body.EndDatetime;

                await _context.SaveChangesAsync();

                return Ok(new { tripId });
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
                return StatusCode(500, ex.Message);
            }
        }

        private async Task<uint?> GetDeviceIdByMACAddress(string MACAddress)
        {
            var device = await _context.OBDIIDevices.Where(d => d.MACAddress.ToUpper() == MACAddress.ToUpper()).Select(d => new { d.DeviceId }).FirstOrDefaultAsync();

            return device is null ? null : device.DeviceId;
        }
    }
}
