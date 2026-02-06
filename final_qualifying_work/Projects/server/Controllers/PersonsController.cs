using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using server.Database;
using server.Models.Dtos;
using server.Models.Entities;

namespace server.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class PersonsController : ControllerBase
    {
        private readonly AppDbContext _context;

        public PersonsController(AppDbContext context)
        {
            _context=context;
        }

        [HttpGet("id/{id}")]
        public async Task<IActionResult> GetPersonById([FromRoute] uint id)
        {
            Person? person;
            try
            {
                person = await _context.Persons.FirstOrDefaultAsync(p => p.PersonId == id);
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
                return StatusCode(500, new InternalServerErrorResponse(ex.Message));
            }
            
            if (person is null)
                return NotFound();

            return Ok(person);
        }

        [HttpGet("email/{email}")]
        public async Task<IActionResult> GetPersonByEmail([FromRoute] string email)
        {
            Person? person;
            try
            {
                person = await _context.Persons.FirstOrDefaultAsync(p => p.Email == email);
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
                return StatusCode(500, new InternalServerErrorResponse(ex.Message));
            }

            if (person is null)
                return NotFound();

            return Ok(person);
        }

        [HttpPost("check_password")]
        public async Task<IActionResult> CheckPassword([FromBody] CheckPasswordRequest body)
        {
            if (!ModelState.IsValid)
                return BadRequest(ModelState);

            string hashedPassword;

            try
            {
                var person =
                    await _context.Persons
                        .Where(p => p.Email == body.Email)
                        .Select(p => new { p.HashedPassword })
                        .FirstOrDefaultAsync();
                if (person is null)
                    return NotFound();
                hashedPassword = person.HashedPassword;
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
                return StatusCode(500, new InternalServerErrorResponse(ex.Message));
            }

            bool confirmed = BCrypt.Net.BCrypt.Verify(body.Password, hashedPassword);

            return Ok(new { confirmed });
        }

        [HttpPost]
        public async Task<IActionResult> CreatePerson([FromBody] CreatePersonRequest body)
        {
            if (!ModelState.IsValid)
                return BadRequest(ModelState);

            if (body.Birth is null)
                return BadRequest(new BadRequestResponse("В теле запроса не передана дата рождения пользователя"));

            if (body.RightLevel is null)
                return BadRequest(new BadRequestResponse("В теле запроса не передан уровень прав пользователя"));

            try
            {
                bool exists = await _context.Persons.AnyAsync(p => p.Email == body.Email);
                if (exists)
                    return BadRequest(new BadRequestResponse("Пользователь с таким email уже существует"));

                exists = await _context.Persons.AnyAsync(p => p.Phone == body.Phone);
                if (exists)
                    return BadRequest(new BadRequestResponse("Пользователь с таким номером телефона уже существует"));

                if (!string.IsNullOrWhiteSpace(body.DriveLisense))
                {
                    exists = await _context.Persons.AnyAsync(p => p.DriveLisense == body.DriveLisense);
                    if (exists)
                        return BadRequest(new BadRequestResponse("Пользователь с таким ВУ уже существует"));
                }

                exists = await _context.AccessRights.AnyAsync(a => a.RightLevel == body.RightLevel);
                if (!exists)
                    return BadRequest(new BadRequestResponse("Неизвестный уровеь прав пользователя"));

                var person = new Person
                {
                    Email = body.Email,
                    Phone = body.Phone,
                    LastName = body.LastName,
                    FirstName = body.FirstName,
                    Patronymic = string.IsNullOrWhiteSpace(body.Patronymic) ? null : body.Patronymic,
                    Birth = (DateOnly)body.Birth,
                    HashedPassword = BCrypt.Net.BCrypt.HashPassword(body.Password),
                    DriveLisense = string.IsNullOrWhiteSpace(body.DriveLisense) ? null : body.DriveLisense,
                    RightLevel = (byte)body.RightLevel
                };

                _context.Persons.Add(person);
                await _context.SaveChangesAsync();

                return CreatedAtAction(nameof(GetPersonById), new { id = person.PersonId }, new { person.PersonId });
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
                return StatusCode(500, new InternalServerErrorResponse(ex.Message));
            }
        }
    }
}
