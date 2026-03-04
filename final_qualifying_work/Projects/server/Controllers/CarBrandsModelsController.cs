using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using server.Database;

namespace server.Controllers
{
    [ApiController]
    [Authorize]
    [Route("api/[controller]")]
    public class CarBrandsModelsController : ControllerBase
    {
        private readonly AppDbContext _context;

        public CarBrandsModelsController(AppDbContext context)
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

        [HttpGet("{brandName}")]
        public async Task<IActionResult> GetAllModelsByBrand([FromRoute] string brandName)
        {
            try
            {
                bool exists =
                    await _context.CarBrands.AnyAsync(cb => cb.BrandName == brandName);

                if (!exists)
                    return Problem(
                        title: "Такая марка не существует",
                        statusCode: StatusCodes.Status404NotFound
                    );

                var models =
                    await _context.CarBrandsModels
                        .Include(b => b.CarBrand)
                        .Where(b => b.CarBrand.BrandName == brandName)
                        .Select(b => b.ModelName)
                        .ToListAsync();

                return Ok(models);
            }
            catch
            {
                return ServerError();
            }
        }
    }
}
