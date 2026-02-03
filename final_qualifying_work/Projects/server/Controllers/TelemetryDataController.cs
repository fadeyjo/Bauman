using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using server.Database;
using server.Models;

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

        [HttpGet("rec_id/{recId}")]
        public async Task<IActionResult> GetTelemtryDataById([FromRoute] ulong recId)
        {
            try
            {
                var record =
                    await _context.TelemetryData
                        .Include(td => td.OBDIIPID)
                        .FirstOrDefaultAsync(td => td.RecId == recId);

                if (record == null)
                    return BadRequest("Записи телеметрии с таким ID не существует");

                return Ok(record);
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
                return StatusCode(500, ex.Message);
            }
        }

        [HttpPost]
        public async Task<IActionResult> CreateTelemetryData([FromBody] CreateTelemetryDataRequestModel body)
        {
            if (body.RecDatetime is null)
                return BadRequest("Дата и время записи обязательны");

            if (body.ECUId is null || body.ECUId.Length == 0 || body.ECUId.All(b => b == 0))
                return BadRequest("ID ЭБУ обязателен");

            if (body.ResponseDlc is null)
                return BadRequest("Длина ответа обязательна");

            if (body.TripId is null)
                return BadRequest("ID поездки обязателен");

            if (body.Response is not null && (body.Response.Length == 0 || body.Response.All(b => b == 0)))
                return BadRequest("Получен некорректный ответ OBDII");

            try
            {
                bool exists = await _context.Trips.AnyAsync(t => t.TripId == body.TripId);
                if (!exists)
                    return BadRequest("Такой поездки не существует");

                var OBDIIPID =
                    await _context.OBDIIPIDs
                    .Where(p => p.ServiceId == body.ServiceId && p.PID == body.PID)
                    .Select(p => new { p.OBDIIPIDId })
                    .FirstAsync();

                uint OBDIIPIDId = OBDIIPID is null ? 0 : OBDIIPID.OBDIIPIDId;

                TelemetryData record = new TelemetryData()
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

                return CreatedAtAction(nameof(GetTelemtryDataById), new { recId = record.RecId }, new { record.RecId });
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
                return StatusCode(500, ex.Message);
            }
        }
    }
}
