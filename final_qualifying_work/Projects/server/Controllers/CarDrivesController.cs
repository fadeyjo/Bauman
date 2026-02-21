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
    public class CarDrivesController : ControllerBase
    {
        private readonly AppDbContext _context;

        private ObjectResult ServerError()
        {
            return Problem(
                title: "Внутренняя ошибка сервера",
                statusCode: StatusCodes.Status500InternalServerError
            );
        }

        public CarDrivesController(AppDbContext context)
        {
            _context = context;
        }

        [HttpGet]
        public async Task<IActionResult> GetAllCarDrives()
        {
            try
            {
                var drives =
                    await _context.CarDrives.Select(d => new CarDriveDto() { DriveName = d.DriveName }).ToListAsync();

                return Ok(drives);
            }
            catch
            {
                return ServerError();
            }
        }
    }
}

