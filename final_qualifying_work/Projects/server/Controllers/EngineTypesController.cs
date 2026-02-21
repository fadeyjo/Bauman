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
    public class EngineTypesController : ControllerBase
    {
        private readonly AppDbContext _context;

        public EngineTypesController(AppDbContext context)
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
        public async Task<IActionResult> GetAllEngineTypes()
        {
            try
            {
                var engineTypes =
                    await _context.EngineTypes.Select(t => new EngineTypeDto() { TypeName = t.TypeName }).ToListAsync();

                return Ok(engineTypes);
            }
            catch
            {
                return ServerError();
            }
        }
    }
}


