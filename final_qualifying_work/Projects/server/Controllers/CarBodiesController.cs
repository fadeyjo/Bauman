using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using server.Database;
using server.Models.Dtos;

namespace server.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class CarBodiesController : ControllerBase
    {
        private readonly AppDbContext _context;

        private ObjectResult ServerError()
        {
            return Problem(
                title: "Внутренняя ошибка сервера",
                statusCode: StatusCodes.Status500InternalServerError
            );
        }

        public CarBodiesController(AppDbContext context)
        {
            _context = context;
        }

        [HttpGet]
        public async Task<IActionResult> GetAllBodies()
        {
            try
            {
                var bodies =
                    await _context.CarBodies.Select(b => new CarBodyDto() { BodyName = b.BodyName }).ToListAsync();

                return Ok(bodies);
            }
            catch
            {
                return ServerError();
            }
        }
    }
}
