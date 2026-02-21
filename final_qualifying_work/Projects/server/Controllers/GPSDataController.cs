using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using server.Database;
using server.Models;
using server.Models.Dtos;
using server.Models.Entities;

namespace server.Controllers
{
    [ApiController]
    [Authorize]
    [Route("api/[controller]")]
    public class GPSDataController : ControllerBase
    {
        private readonly AppDbContext _context;

        public GPSDataController(AppDbContext context)
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

        [HttpGet("rec_id/{recId}")]
        public async Task<IActionResult> GetGPSDataById([FromRoute] ulong recId)
        {
            try
            {
                var record =
                    await _context.GPSData
                        .Where(d => d.RecId == recId)
                        .Select(d =>
                            new GPSDataDto()
                            {
                                RecId = d.RecId,
                                RecDatetime = d.RecDatetime,
                                TripId = d.TripId,
                                LatitudeDEG = d.LatitudeDEG,
                                LongitudeDEG = d.LongitudeDEG,
                                AccuracyM = d.AccuracyM,
                                SpeedKMH = d.SpeedKMH,
                                BearingDEG = d.BearingDEG
                            }
                        )
                        .FirstOrDefaultAsync();

                if (record is null)
                    return Problem(
                        title: "Запись GPS не найдена",
                        statusCode: StatusCodes.Status404NotFound
                    );

                return Ok(record);
            }
            catch
            {
                return ServerError();
            }
        }

        [HttpPost]
        public async Task<IActionResult> CreateGPSData([FromBody] CreateGPSDataDto body)
        {
            if (body.RecDatetime is null)
                return Problem(
                    title: "Не переданы дата и время записи",
                    statusCode: StatusCodes.Status400BadRequest
                );

            if (body.LatitudeDEG is null)
                return Problem(
                    title: "Не передана широта",
                    statusCode: StatusCodes.Status400BadRequest
                );

            if (body.LatitudeDEG < -90 || body.LatitudeDEG > 90)
                return Problem(
                    title: "Широта должна быть в диапазоне [-90; 90]",
                    statusCode: StatusCodes.Status400BadRequest
                );

            if (body.LongitudeDEG is null)
                return Problem(
                    title: "Не передана долгота",
                    statusCode: StatusCodes.Status400BadRequest
                );

            if (body.LatitudeDEG <= -180 || body.LatitudeDEG > 180)
                return Problem(
                    title: "Долгота должна быть в диапазоне (-180; 180]",
                    statusCode: StatusCodes.Status400BadRequest
                );

            if (body.TripId is null)
                return Problem(
                    title: "Не передана поездка",
                    statusCode: StatusCodes.Status400BadRequest
                );

            if (body.BearingDEG is not null && (body.BearingDEG < 0 || body.BearingDEG >= 360))
                return Problem(
                    title: "Курс должен быть в диапазоне [0; 360)",
                    statusCode: StatusCodes.Status400BadRequest
                );

            try
            {
                bool exists = await _context.Trips.AnyAsync(t => t.TripId == body.TripId);
                if (!exists)
                    return Problem(
                        title: "Поездка не найдена",
                        statusCode: StatusCodes.Status404NotFound
                    );

                var record = new GPSData()
                {
                    RecDatetime = (DateTime)body.RecDatetime,
                    TripId = (ulong)body.TripId,
                    LatitudeDEG = (float)body.LatitudeDEG,
                    LongitudeDEG = (float)body.LongitudeDEG,
                    AccuracyM = body.AccuracyM,
                    SpeedKMH = body.SpeedKMH,
                    BearingDEG = body.BearingDEG
                };

                _context.GPSData.Add(record);

                await _context.SaveChangesAsync();

                var rec =
                    await _context.GPSData
                        .Where(d => d.RecId == record.RecId)
                        .Select(d =>
                            new GPSDataDto()
                            {
                                RecId = d.RecId,
                                RecDatetime = d.RecDatetime,
                                TripId = d.TripId,
                                LatitudeDEG = d.LatitudeDEG,
                                LongitudeDEG = d.LongitudeDEG,
                                AccuracyM = d.AccuracyM,
                                SpeedKMH = d.SpeedKMH,
                                BearingDEG = d.BearingDEG
                            }
                        )
                        .FirstOrDefaultAsync();

                return CreatedAtAction(nameof(GetGPSDataById), new { recId = record.RecId }, rec);
            }
            catch
            {
                return ServerError();
            }
        }

        [HttpGet("trip_id/{tripId}")]
        public async Task<IActionResult> GetGPSDataByTripId([FromRoute] ulong tripId)
        {
            try
            {
                bool exists = await _context.GPSData.AnyAsync(d => d.TripId == tripId);

                if (!exists)
                    return Problem(
                        title: "Поездка не найдена",
                        statusCode: StatusCodes.Status404NotFound
                    );

                var records =
                    await _context.GPSData
                        .Where(d => d.TripId == tripId)
                        .Select(d =>
                            new GPSDataDto()
                            {
                                RecId = d.RecId,
                                RecDatetime = d.RecDatetime,
                                TripId = d.TripId,
                                LatitudeDEG = d.LatitudeDEG,
                                LongitudeDEG = d.LongitudeDEG,
                                AccuracyM = d.AccuracyM,
                                SpeedKMH = d.SpeedKMH,
                                BearingDEG = d.BearingDEG
                            }
                        )
                        .ToListAsync();

                return Ok(records);
            }
            catch
            {
                return ServerError();
            }
        }
    }
}
