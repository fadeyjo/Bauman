using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using server.Database;
using server.Models.Dtos;
using server.Models.Entities;
using System;

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

        private ObjectResult ServerError()
        {
            return Problem(
                title: "Внутренняя ошибка сервера",
                statusCode: StatusCodes.Status500InternalServerError
            );
        }

        [HttpGet("email/{email}")]
        public async Task<IActionResult> GetPersonByEmail([FromRoute] string email)
        {
            PersonDto? person;
            try
            {
                person =
                    await _context.Persons
                        .Where(p => p.Email == email)
                        .Select(p =>
                            new PersonDto()
                            {
                                PersonId = p.PersonId,
                                Email = p.Email,
                                Phone = p.Phone,
                                LastName = p.LastName,
                                FirstName = p.FirstName,
                                Patronymic = p.Patronymic,
                                Birth = p.Birth,
                                DriveLisense = p.DriveLisense
                            }
                        ).
                        FirstOrDefaultAsync();
            }
            catch
            {
                return ServerError();
            }

            if (person is null)
                return Problem(
                    title: "Пользователь не найден",
                    statusCode: StatusCodes.Status404NotFound
                );

            return Ok(person);
        }

        [HttpPost("check_password")]
        public async Task<IActionResult> CheckPassword([FromBody] CheckPasswordDto body)
        {
            try
            {
                string hashedPassword;

                var person =
                        await _context.Persons
                            .Where(p => p.Email == body.Email)
                            .Select(p => new { p.HashedPassword })
                            .FirstOrDefaultAsync();
                if (person is null)
                    return Problem(
                        title: "Пользователь не найден",
                        statusCode: StatusCodes.Status404NotFound
                    );
                hashedPassword = person.HashedPassword;

                string pass = BCrypt.Net.BCrypt.HashPassword(body.Password);

                bool confirmed = BCrypt.Net.BCrypt.Verify(body.Password, hashedPassword);

                var personRes =
                    await _context.Persons
                        .Where(p => p.Email == body.Email)
                        .Select(p =>
                            new PersonDto()
                            {
                                PersonId = p.PersonId,
                                Email = p.Email,
                                Phone = p.Phone,
                                LastName = p.LastName,
                                FirstName = p.FirstName,
                                Patronymic = p.Patronymic,
                                Birth = p.Birth,
                                DriveLisense = p.DriveLisense
                            }
                        ).
                        FirstOrDefaultAsync();

                return confirmed ?
                    Ok(personRes) :
                    Problem(
                        title: "Пользолватель не авторизован",
                        statusCode: StatusCodes.Status401Unauthorized
                    );
            }
            catch
            {
                return ServerError();
            }
        }

        [HttpPost]
        public async Task<IActionResult> CreatePerson([FromBody] CreatePersonDto body)
        {
            if (body.Birth is null)
                return Problem(
                    title: "Не передана дата рождения",
                    statusCode: StatusCodes.Status400BadRequest
                );

            if (body.RightLevel is null)
                return Problem(
                   title: "Не передан уровень прав пользователя",
                   statusCode: StatusCodes.Status400BadRequest
               );

            if (body.RightLevel == 0 && string.IsNullOrWhiteSpace(body.DriveLisense))
                return Problem(
                   title: "Не передано ВУ пользователя",
                   statusCode: StatusCodes.Status400BadRequest
                );

            try
            {
                bool exists = await _context.Persons.AnyAsync(p => p.Email == body.Email);
                if (exists)
                    return Problem(
                        title: "Пользователь с данным email уже существует",
                        statusCode: StatusCodes.Status409Conflict
                    );

                exists = await _context.Persons.AnyAsync(p => p.Phone == body.Phone);
                if (exists)
                    return Problem(
                        title: "Пользователь с данным номером телефона уже существует",
                        statusCode: StatusCodes.Status409Conflict
                    );

                if (!string.IsNullOrWhiteSpace(body.DriveLisense))
                {
                    exists = await _context.Persons.AnyAsync(p => p.DriveLisense == body.DriveLisense);
                    if (exists)
                        return Problem(
                            title: "Пользователь с данным ВУ уже существует",
                            statusCode: StatusCodes.Status409Conflict
                        );
                }

                exists = await _context.AccessRights.AnyAsync(a => a.RightLevel == body.RightLevel);
                if (!exists)
                    return Problem(
                        title: "Неизвестный уровень прав пользователя",
                        statusCode: StatusCodes.Status400BadRequest
                    );

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

                var personRes =
                    await _context.Persons
                        .Where(p => p.PersonId == person.PersonId)
                        .Select(p =>
                            new PersonDto()
                            {
                                PersonId = p.PersonId,
                                Email = p.Email,
                                Phone = p.Phone,
                                LastName = p.LastName,
                                FirstName = p.FirstName,
                                Patronymic = p.Patronymic,
                                Birth = p.Birth,
                                DriveLisense = p.DriveLisense
                            }
                        ).
                        FirstOrDefaultAsync();

                return CreatedAtAction(nameof(GetPersonByEmail), new { email = person.Email }, personRes);
            }
            catch
            {
                return ServerError();
            }
        }

        [HttpPut]
        public async Task<IActionResult> UpdatePersonInfo([FromBody] UpdatePersonInfoDto body)
        {
            if (body.Birth is null)
                return Problem(
                    title: "Не передана дата рождения",
                    statusCode: StatusCodes.Status400BadRequest
                );

            try
            {
                var person =
                    await _context.Persons.FirstOrDefaultAsync(p => p.PersonId == body.PersonId);

                if (person is null)
                    return Problem(
                        title: "Пользователь не найден",
                        statusCode: StatusCodes.Status404NotFound
                    );

                if (person.RightLevel == 0 && string.IsNullOrWhiteSpace(body.DriveLisense))
                    return Problem(
                       title: "Не передано ВУ пользователя",
                       statusCode: StatusCodes.Status400BadRequest
                    );

                bool exists =
                    await _context.Persons.AnyAsync(p => p.Email == body.Email && p.PersonId != body.PersonId);

                if (exists)
                    return Problem(
                        title: "Пользователь с данным email уже существует",
                        statusCode: StatusCodes.Status409Conflict
                    );

                exists =
                    await _context.Persons.AnyAsync(p => p.Phone == body.Phone && p.PersonId != body.PersonId);

                if (exists)
                    return Problem(
                        title: "Пользователь с данным номером телефона уже существует",
                        statusCode: StatusCodes.Status409Conflict
                    );

                if (!string.IsNullOrWhiteSpace(body.DriveLisense))
                {
                    exists =
                        await _context.Persons.AnyAsync(p => p.DriveLisense == body.DriveLisense && p.PersonId != body.PersonId);

                    if (exists)
                        return Problem(
                            title: "Пользователь с данным ВУ уже существует",
                            statusCode: StatusCodes.Status409Conflict
                        );
                }

                person.Email = body.Email;
                person.Phone = body.Phone;
                person.LastName = body.LastName;
                person.FirstName = body.FirstName;
                person.Patronymic = body.Patronymic;
                person.Birth = (DateOnly)body.Birth;
                person.DriveLisense = body.DriveLisense;

                await _context.SaveChangesAsync();

                return NoContent();
            }
            catch
            {
                return ServerError();
            }
        }
    }
}
