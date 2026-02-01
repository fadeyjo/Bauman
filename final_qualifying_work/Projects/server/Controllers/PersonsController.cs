using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using server.Database;
using server.Models;

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
                person = await _context.Persons.Include(p => p.AccessRight).FirstOrDefaultAsync(p => p.PersonId == id);
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
                return StatusCode(500, ex.Message);
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
                person = await _context.Persons.Include(p => p.AccessRight).FirstOrDefaultAsync(p => p.Email == email);
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
                return StatusCode(500, ex.Message);
            }

            if (person is null)
                return NotFound();

            return Ok(person);
        }

        [HttpPost("check_password")]
        public async Task<IActionResult> CheckPassword([FromBody] CheckPasswordRequestModel body)
        {
            if (!ModelState.IsValid)
                return BadRequest(ModelState);

            string hashedPassword;

            try
            {
                var person = await _context.Persons.Where(p => p.PersonId == body.PersonId).Select(p => new { p.HashedPassword }).FirstOrDefaultAsync();
                if (person is null)
                    return NotFound();
                hashedPassword = person.HashedPassword;
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
                return StatusCode(500, ex.Message);
            }

            bool confirmed = BCrypt.Net.BCrypt.Verify(body.Password, hashedPassword);

            return Ok(new { confirmed });
        }

        [HttpPost]
        public async Task<IActionResult> CreatePerson([FromBody] CreatePersonRequestModel request)
        {
            if (!ModelState.IsValid)
                return BadRequest(ModelState);

            try
            {
                bool exists = await _context.Persons.AnyAsync(p => p.Email == request.Email);
                if (exists)
                    return BadRequest("Пользователь с таким email уже существует");

                if (request.RightLevel is null)
                    return BadRequest("Поле прав доступа обязательно");

                exists = await _context.Persons.AnyAsync(p => p.Phone == request.Phone);
                if (exists)
                    return BadRequest("Пользователь с таким номером телефона уже существует");

                if (!string.IsNullOrWhiteSpace(request.DriveLisense))
                {
                    exists = await _context.Persons.AnyAsync(p => p.DriveLisense == request.DriveLisense);
                    if (exists)
                        return BadRequest("Пользователь с такими правами уже существует");
                }

                exists = await _context.AccessRights.AnyAsync(a => a.RightLevel == request.RightLevel);
                if (!exists)
                    return BadRequest("Передан неизвестный уровень прав");

                var person = new Person
                {
                    Email = request.Email,
                    Phone = request.Phone,
                    LastName = request.LastName,
                    FirstName = request.FirstName,
                    Patronymic = string.IsNullOrWhiteSpace(request.Patronymic) ? null : request.Patronymic,
                    Birth = request.Birth,
                    HashedPassword = BCrypt.Net.BCrypt.HashPassword(request.Password),
                    DriveLisense = string.IsNullOrWhiteSpace(request.DriveLisense) ? null : request.DriveLisense,
                    RightLevel = (byte)request.RightLevel
                };

                _context.Persons.Add(person);
                await _context.SaveChangesAsync();

                return CreatedAtAction(nameof(GetPersonById), new { id = person.PersonId }, new { person.PersonId });
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
                return StatusCode(500, ex.Message);
            }
        }
    }
}
