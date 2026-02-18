using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using server.Database;
using server.Models.Dtos;
using server.Models.Entities;

namespace server.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class TelemetryDataController : ControllerBase
    {
        private readonly AppDbContext _context;

        public TelemetryDataController(AppDbContext context)
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
        public async Task<IActionResult> GetTelemtryDataById([FromRoute] ulong recId)
        {
            try
            {
                var record =
                    await _context.TelemetryData
                        .Where(td => td.RecId == recId)
                        .Select(
                            d => new TelemtryDataDto()
                            {
                                RecId = d.RecId,
                                RecDatetime = d.RecDatetime,
                                ServiceId = d.OBDIIPID.ServiceId,
                                PID = d.OBDIIPID.PID,
                                ECUId = d.ECUId,
                                ResponseDLC = d.ResponseDLC,
                                Response = d.Response,
                                TripId = d.TripId
                            }
                        )
                        .FirstOrDefaultAsync();

                if (record is null)
                    return Problem(
                        title: "Запись телеметрии не найдена",
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
        public async Task<IActionResult> CreateTelemetryData([FromBody] CreateTelemetryDataDto body)
        {
            if (body.RecDatetime is null)
                return Problem(
                    title: "Не переданы дата и время записи",
                    statusCode: StatusCodes.Status400BadRequest
                );

            if (body.ECUId is null || body.ECUId.Length == 0 || body.ECUId.All(b => b == 0))
                return Problem(
                    title: "Не передан или получен нулевой ID ЭБУ",
                    statusCode: StatusCodes.Status400BadRequest
                );

            if (body.ResponseDlc is null)
                return Problem(
                    title: "Не передана длина OBDII ответа",
                    statusCode: StatusCodes.Status400BadRequest
                );

            if (body.TripId is null)
                return Problem(
                    title: "Не передана поездка",
                    statusCode: StatusCodes.Status400BadRequest
                );

            if (body.Response is not null && (body.Response.Length == 0 || body.Response.All(b => b == 0)))
                return Problem(
                    title: "Получен нулевой OBDII ответ",
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

                var OBDIIPID =
                    await _context.OBDIIPIDs
                    .Where(p => p.ServiceId == body.ServiceId && p.PID == body.PID)
                    .Select(p => new { p.OBDIIPIDId })
                    .FirstAsync();

                uint OBDIIPIDId = OBDIIPID is null ? 0 : OBDIIPID.OBDIIPIDId;

                TelemetryData record = new ()
                {
                    RecDatetime = (DateTime)body.RecDatetime,
                    OBDIIPIDId = OBDIIPIDId,
                    ECUId = body.ECUId,
                    ResponseDLC = (byte)body.ResponseDlc,
                    Response = body.Response,
                    TripId = (ulong)body.TripId
                };

                _context.TelemetryData.Add(record);

                await _context.SaveChangesAsync();

                var recordRes =
                    await _context.TelemetryData
                        .Where(td => td.RecId == record.RecId)
                        .Select(
                            d => new TelemtryDataDto()
                            {
                                RecId = d.RecId,
                                RecDatetime = d.RecDatetime,
                                ServiceId = d.OBDIIPID.ServiceId,
                                PID = d.OBDIIPID.PID,
                                ECUId = d.ECUId,
                                ResponseDLC = d.ResponseDLC,
                                Response = d.Response,
                                TripId = d.TripId
                            }
                        )
                        .FirstOrDefaultAsync();

                return CreatedAtAction(nameof(GetTelemtryDataById), new { recId = record.RecId }, recordRes);
            }
            catch
            {
                return ServerError();
            }
        }

        [HttpGet("trip_id/{tripId}")]
        public async Task<IActionResult> GetTelemetryDataByTripId([FromRoute] ulong tripId)
        {
            try
            {
                bool exists = await _context.TelemetryData.AnyAsync(d => d.TripId == tripId);

                if (!exists)
                    return Problem(
                        title: "Поездка не найдена",
                        statusCode: StatusCodes.Status404NotFound
                    );

                var records =
                    await _context.TelemetryData
                        .Where(d => d.TripId == tripId)
                        .Select(
                            d => new TelemtryDataDto()
                            {
                                RecId = d.RecId,
                                RecDatetime = d.RecDatetime,
                                ServiceId = d.OBDIIPID.ServiceId,
                                PID = d.OBDIIPID.PID,
                                ECUId = d.ECUId,
                                ResponseDLC = d.ResponseDLC,
                                Response = d.Response,
                                TripId = d.TripId
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
