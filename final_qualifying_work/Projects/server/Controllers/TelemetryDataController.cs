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

        [HttpGet("rec_id/{recId}")]
        public async Task<IActionResult> GetTelemtryDataById([FromRoute] ulong recId)
        {
            try
            {
                var record =
                    await _context.TelemetryData
                        .Include(td => td.OBDIIPID)
                        .FirstOrDefaultAsync(td => td.RecId == recId);

                if (record is null)
                    return BadRequest(new BadRequestResponse("Записи телеметрии с таким ID не существует"));

                return Ok(record);
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
                return StatusCode(500, new InternalServerErrorResponse(ex.Message));
            }
        }

        [HttpPost]
        public async Task<IActionResult> CreateTelemetryData([FromBody] CreateTelemetryDataRequest body)
        {
            if (!ModelState.IsValid)
                return BadRequest(ModelState);

            if (body.RecDatetime is null)
                return BadRequest(new BadRequestResponse("В теле запроса не получены дата и время записи"));

            if (body.ECUId is null || body.ECUId.Length == 0 || body.ECUId.All(b => b == 0))
                return BadRequest(new BadRequestResponse("В теле запроса не передан или получен нулевой ID ЭБУ"));

            if (body.ResponseDlc is null)
                return BadRequest(new BadRequestResponse("В теле запроса не получена длина OBDII ответа"));

            if (body.TripId is null)
                return BadRequest(new BadRequestResponse("В теле запроса не получен ID поездки"));

            if (body.Response is not null && (body.Response.Length == 0 || body.Response.All(b => b == 0)))
                return BadRequest(new BadRequestResponse("В теле запроса получен нулевой OBDII ответ"));

            try
            {
                bool exists = await _context.Trips.AnyAsync(t => t.TripId == body.TripId);
                if (!exists)
                    return BadRequest(new BadRequestResponse("Такой поездки не существует"));

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

                return CreatedAtAction(nameof(GetTelemtryDataById), new { recId = record.RecId }, new { recId = record.RecId });
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
                return StatusCode(500, new InternalServerErrorResponse(ex.Message));
            }
        }
    }
}
