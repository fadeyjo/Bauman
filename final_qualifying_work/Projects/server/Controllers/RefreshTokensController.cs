using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Options;
using server.Database;
using server.JwtService;
using server.Models.Dtos;
using server.Models.Entities;
using System.Security.Claims;
using System.Security.Cryptography;

namespace server.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class RefreshTokensController: ControllerBase
    {
        private readonly AppDbContext _context;

        private readonly JwtService.JwtService _jwtService;

        private readonly JwtOptions _jwtOptions;

        public RefreshTokensController(AppDbContext context, JwtService.JwtService jwtService, IOptions<JwtOptions> options)
        {
            _context = context;
            _jwtService = jwtService;
            _jwtOptions = options.Value;
        }

        private ObjectResult ServerError()
        {
            return Problem(
                title: "Внутренняя ошибка сервера",
                statusCode: StatusCodes.Status500InternalServerError
            );
        }

        [HttpPost("refresh")]
        public async Task<IActionResult> Refresh([FromBody] RefreshTokensDto body)
        {
            try
            {
                var tokens =
                    await _context.RefreshTokens.Include(rt => rt.Person).Where(rt => !rt.IsRevoked).ToListAsync();

                if (tokens is null)
                    return Problem(
                        title: "Пользователь не авторизован",
                        statusCode: StatusCodes.Status401Unauthorized
                    );

                RefreshToken? storedToken = null;
                foreach (var token in tokens)
                {
                    if (!BCrypt.Net.BCrypt.Verify(body.RefreshToken, token.TokenHash)) continue;

                    storedToken = token;

                    break;
                }

                if (storedToken is null)
                    return Problem(
                        title: "Пользователь не авторизован",
                        statusCode: StatusCodes.Status401Unauthorized
                    );

                if (storedToken.Expires < DateTime.UtcNow)
                    return Problem(
                        title: "Пользователь не авторизован",
                        statusCode: StatusCodes.Status401Unauthorized
                    );

                var person = storedToken.Person;

                storedToken.IsRevoked = true;

                string accessToken = _jwtService.GenerateAccessToken(person);
                string refreshToken = _jwtService.GenerateRefreshToken();

                string newTokenHash = BCrypt.Net.BCrypt.HashPassword(refreshToken);

                _context.RefreshTokens.Add(
                    new RefreshToken()
                    {
                        TokenHash = newTokenHash,
                        Expires = DateTime.UtcNow.AddDays(_jwtOptions.RefreshTokenDays),
                        PersonId = person.PersonId,
                        IsRevoked = false
                    }
                );

                await _context.SaveChangesAsync();

                return Ok(
                    new AuthorizedDto()
                    {
                        AccessToken = accessToken,
                        RefreshToken = refreshToken,
                    }    
                );
            }
            catch
            {
                return ServerError();
            }
        }

        [HttpPost("check_token")]
        public async Task<IActionResult> CheckToken([FromBody] CheckTokenDto body)
        {
            try
            {
                var tokens =
                    await _context.RefreshTokens
                        .Include(rt => rt.Person)
                        .Where(rt => !rt.IsRevoked)
                        .ToListAsync();

                if (tokens is null)
                    return Problem(
                        title: "Пользователь не авторизован",
                        statusCode: StatusCodes.Status401Unauthorized
                    );

                RefreshToken? valToken = null;
                foreach (var t in tokens)
                {
                    if (!BCrypt.Net.BCrypt.Verify(body.RefreshToken, t.TokenHash)) continue;

                    valToken = t;
                    break;
                }

                if (
                    valToken is null ||
                    valToken.Expires < DateTime.UtcNow
                )
                    return Problem(
                        title: "Пользователь не авторизован",
                        statusCode: StatusCodes.Status401Unauthorized
                    );

                return NoContent();
            }
            catch
            {
                return ServerError();
            }
        }
    }
}
