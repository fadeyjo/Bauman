using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Options;
using server.Database;
using server.JwtService;
using server.Models.Dtos;
using server.Models.Entities;
using System;
using System.Security.Claims;

namespace server.Controllers
{
    [ApiController]
    [Authorize]
    [Route("api/[controller]")]
    public class PersonsController : ControllerBase
    {
        private readonly AppDbContext _context;

        private readonly JwtService.JwtService _jwtService;

        private readonly JwtOptions _jwtOptions;

        private readonly StoreService.StoreOptions _storeOptions;

        public PersonsController(AppDbContext context, JwtService.JwtService jwtService, IOptions<JwtOptions> options, StoreService.StoreOptions storeOptions)
        {
            _context = context;
            _jwtService = jwtService;
            _jwtOptions = options.Value;
            _storeOptions=storeOptions;
        }

        private ObjectResult ServerError()
        {
            return Problem(
                title: "Внутренняя ошибка сервера",
                statusCode: StatusCodes.Status500InternalServerError
            );
        }

        [HttpGet("email")]
        public async Task<IActionResult> GetPersonByEmail()
        {
            string? email = User.FindFirst(ClaimTypes.Email)?.Value;
            if (string.IsNullOrWhiteSpace(email))
                return Problem(
                    title: "Пользователь не авторизован",
                    statusCode: StatusCodes.Status401Unauthorized
                );

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

        [HttpPut("logout")]
        public async Task<IActionResult> Logout()
        {
            try
            {
                var userIdClaim = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;

                if (userIdClaim == null)
                    return Unauthorized();

                uint userId = uint.Parse(userIdClaim);

                var tokens = await _context.RefreshTokens
                    .Where(rt => rt.PersonId == userId && !rt.IsRevoked)
                    .ToListAsync();

                foreach (var token in tokens)
                    token.IsRevoked = true;

                await _context.SaveChangesAsync();

                return NoContent();
            }
            catch
            {
                return ServerError();
            }
        }

        [AllowAnonymous]
        [HttpPost("login")]
        public async Task<IActionResult> Login([FromBody] LoginDto body)
        {
            Person? person;

            try
            {
                string hashedPassword;

                person =
                        await _context.Persons
                            .Where(p => p.Email == body.Email)
                            .FirstOrDefaultAsync();

                if (person is null)
                    return Problem(
                        title: "Пользователь не найден",
                        statusCode: StatusCodes.Status404NotFound
                    );
                hashedPassword = person.HashedPassword;

                bool confirmed = BCrypt.Net.BCrypt.Verify(body.Password, hashedPassword);

                if (!confirmed)
                    return Problem(
                        title: "Пользолватель не авторизован",
                        statusCode: StatusCodes.Status401Unauthorized
                    );

                string accessToken = _jwtService.GenerateAccessToken(person);
                string refreshToken = _jwtService.GenerateRefreshToken();

                string refreshHash = BCrypt.Net.BCrypt.HashPassword(refreshToken);

                _context.RefreshTokens.Add(new RefreshToken
                {
                    TokenHash = refreshHash,
                    Expires = DateTime.UtcNow.AddDays(_jwtOptions.RefreshTokenDays),
                    PersonId = person.PersonId,
                    IsRevoked = false
                });

                await _context.SaveChangesAsync();

                return Ok(
                    new AuthorizedDto()
                    {
                        AccessToken = accessToken,
                        RefreshToken = refreshToken
                    });
            }
            catch
            {
                return ServerError();
            }
        }

        [AllowAnonymous]
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

                var avatar = new Avatar()
                {
                    AvatarUrl = Path.Combine(_storeOptions.AvatarsPath, "standart.png"),
                    PersonId = person.PersonId
                };

                _context.Avatars.Add(avatar);

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

            object? personIdObj = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
            if (personIdObj is null)
                return Problem(
                    title: "Пользователль не авторизован",
                    statusCode: StatusCodes.Status401Unauthorized
                );

            uint personId = Convert.ToUInt32(personIdObj);

            try
            {
                var person =
                    await _context.Persons.FirstOrDefaultAsync(p => p.PersonId == personId);

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
                    await _context.Persons.AnyAsync(p => p.Email == body.Email && p.PersonId != personId);

                if (exists)
                    return Problem(
                        title: "Пользователь с данным email уже существует",
                        statusCode: StatusCodes.Status409Conflict
                    );

                exists =
                    await _context.Persons.AnyAsync(p => p.Phone == body.Phone && p.PersonId != personId);

                if (exists)
                    return Problem(
                        title: "Пользователь с данным номером телефона уже существует",
                        statusCode: StatusCodes.Status409Conflict
                    );

                if (!string.IsNullOrWhiteSpace(body.DriveLisense))
                {
                    exists =
                        await _context.Persons.AnyAsync(p => p.DriveLisense == body.DriveLisense && p.PersonId != personId);

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
