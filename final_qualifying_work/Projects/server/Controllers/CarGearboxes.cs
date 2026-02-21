using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using server.Database;
using server.Models.Dtos;

namespace server.Controllers
{
    [ApiController]
    [Authorize]
    [Route("api/[controller]")]
    public class CarGearboxes : ControllerBase
    {
        private readonly AppDbContext _context;

        public CarGearboxes(AppDbContext context)
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

        [HttpGet]
        public async Task<IActionResult> GetAllGearboxes()
        {
            try
            {
                var gearboxes =
                    await _context.CarGearboxes.Select(g => new CarGearboxDto() { GearboxName = g.GearboxName }).ToListAsync();

                return Ok(gearboxes);
            }
            catch
            {
                return ServerError();
            }
        }
    }
}
