using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using server.Database;
using server.Models.Dtos;

namespace server.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class FuelTypesController : ControllerBase
    {
        private readonly AppDbContext _context;

        public FuelTypesController(AppDbContext context)
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
        public async Task<IActionResult> GetAllFuelTypes()
        {
            try
            {
                var fuelTypes =
                    await _context.FuelTypes.Select(t => new FuelTypeDto() { TypeName = t.TypeName }).ToListAsync();

                return Ok(fuelTypes);
            }
            catch
            {
                return ServerError();
            }
        }
    }
}
