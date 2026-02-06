using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using server.Database;
using server.Models;
using server.Models.Dtos;
using server.Models.Entities;

namespace server.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class GPSDataController : ControllerBase
    {
        private readonly AppDbContext _context;

        public GPSDataController(AppDbContext context)
        {
            _context = context;
        }

        [HttpGet("rec_id/{recId}")]
        public async Task<IActionResult> GetGPSDataById([FromRoute] ulong recId)
        {
            try
            {
                var record =
                    await _context.GPSData.FirstOrDefaultAsync(d => d.RecId == recId);

                if (record is null)
                    return NotFound();

                return Ok(record);
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
                return StatusCode(500, new InternalServerErrorResponse(ex.Message));
            }
        }

        [HttpPost]
        public async Task<IActionResult> CreateGPSData([FromBody] CreateGPSDataRequest body)
        {
            if (body.RecDatetime is null)
                return BadRequest(new BadRequestResponse("В теле запроса не переданы дата и время записи"));

            if (body.LatitudeDEG is null)
                return BadRequest(new BadRequestResponse("В теле запроса не передана широта"));

            if (body.LatitudeDEG < -90 || body.LatitudeDEG > 90)
                return BadRequest(new BadRequestResponse("Широта должна быть в диапазоне [-90; 90]"));

            if (body.LongitudeDEG is null)
                return BadRequest(new BadRequestResponse("В теле запроса не передана долгота"));

            if (body.LatitudeDEG <= -180 || body.LatitudeDEG > 180)
                return BadRequest(new BadRequestResponse("Долгота должна быть в диапазоне (-180; 180]"));

            if (body.TripId is null)
                return BadRequest(new BadRequestResponse("В теле запроса не передан ID поездки"));

            if (body.BearingDEG is not null && (body.BearingDEG < 0 || body.BearingDEG >= 360))
                return BadRequest(new BadRequestResponse("Курс должен быть в диапазоне [0; 360)"));

            try
            {
                bool exists = await _context.Trips.AnyAsync(t => t.TripId == body.TripId);
                if (!exists)
                    return BadRequest(new BadRequestResponse("Такой поездки не существует"));

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

                return CreatedAtAction(nameof(GetGPSDataById), new { recId = record.RecId }, new { recId = record.RecId });
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
                return StatusCode(500, new InternalServerErrorResponse(ex.Message));
            }
        }
    }
}
