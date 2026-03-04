using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using server.Database;

namespace server.Controllers
{
    [ApiController]
    [Authorize]
    [Route("api/[controller]")]
    public class CarBrandsController : ControllerBase
    {
        private readonly AppDbContext _context;

        public CarBrandsController(AppDbContext context)
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
        public async Task<IActionResult> GetAllBrands()
        {
            try
            {
                var brands =
                    await _context.CarBrands.Select(b => b.BrandName).ToListAsync();

                return Ok(brands);
            }
            catch
            {
                return ServerError();
            }
        }
    }
}
