using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using server.Database;
using System.Security.Claims;
using server.Models.Entities;
using server.Models.Dtos;

namespace server.Controllers
{
    [ApiController]
    [Authorize]
    [Route("api/[controller]")]
    public class AvatarsController: ControllerBase
    {
        private readonly AppDbContext _context;

        private readonly StoreService.StoreOptions _storeOptions;
    
        public AvatarsController(AppDbContext context, StoreService.StoreOptions storeOptions)
        {
            _context=context;
            _storeOptions = storeOptions;
        }

        private ObjectResult ServerError()
        {
            return Problem(
                title: "Внутренняя ошибка сервера",
                statusCode: StatusCodes.Status500InternalServerError
            );
        }

        [HttpPost]
        public async Task<IActionResult> NewAvatar([FromForm] IFormFile file)
        {
            try
            {
                object? personClaims = User.FindFirst(ClaimTypes.NameIdentifier);
                if (personClaims is null)
                    return Problem(
                        title: "Пользователь не авторизован",
                        statusCode: StatusCodes.Status401Unauthorized
                    );

                uint personId = Convert.ToUInt32(personClaims);

                bool exists =
                    await _context.Persons.AnyAsync(p => p.PersonId == personId);

                if (!exists)
                    return Problem(
                        title: "Пользователь не найден",
                        statusCode: StatusCodes.Status404NotFound
                    );

                if (file is null)
                    return Problem(
                        title: "Не переадн файл",
                        statusCode: StatusCodes.Status400BadRequest
                    );

                string userFolder = Path.Combine(_storeOptions.AvatarsPath, personId.ToString());

                if (!Directory.Exists(userFolder))
                    Directory.CreateDirectory(userFolder);

                uint newId = GetNewId(userFolder);

                string newFileName = $"avatar_{newId}.png";

                string filePath = Path.Combine(userFolder, newFileName);

                using (var stream = new FileStream(filePath, FileMode.Create))
                    await file.CopyToAsync(stream);

                var avatar = new Avatar()
                {
                    AvatarUrl = $"{personId}/{newFileName}",
                    PersonId = personId
                };

                _context.Avatars.Add(avatar);

                await _context.SaveChangesAsync();

                return Ok(new AvatarDto
                {
                    AvatarId = avatar.AvatarId,
                    PersonId = avatar.PersonId,
                    AvatarUrl = avatar.AvatarUrl
                });
            }
            catch
            {
                return ServerError();
            }
        }

        [HttpGet("avatar_id/{avatarId}")]
        public async Task<IActionResult> GetAvatarById([FromRoute] uint avatarId)
        {
            try
            {
                var avatar =
                    await _context.Avatars.FirstOrDefaultAsync(a => a.AvatarId == avatarId);

                if (avatar is null)
                    return Problem(
                        title: "Аватар не найден",
                        statusCode: StatusCodes.Status404NotFound
                    );

                string fullPath = Path.Combine(_storeOptions.AvatarsPath, avatar.AvatarUrl);

                if (!System.IO.File.Exists(fullPath))
                    return Problem(
                        title: "Аватар не найден",
                        statusCode: StatusCodes.Status404NotFound
                    );

                var bytes = System.IO.File.ReadAllBytes(fullPath);

                return File(bytes, "image/png");
            }
            catch
            {
                return ServerError();
            }
        }

        [HttpGet("last")]
        public IActionResult GetLastAvatar()
        {
            try
            {
                var personClaim = User.FindFirst(ClaimTypes.NameIdentifier);
                if (personClaim is null)
                    return Problem(
                        title: "Пользователь не авторизован",
                        statusCode: StatusCodes.Status401Unauthorized
                    );

                uint personId = Convert.ToUInt32(personClaim.Value);

                string userFolder = Path.Combine(_storeOptions.AvatarsPath, personId.ToString());

                if (!Directory.Exists(userFolder))
                    return Problem(
                        title: "Аватар не найден",
                        statusCode: StatusCodes.Status404NotFound
                    );

                var file = new DirectoryInfo(userFolder)
                    .GetFiles("avatar_*.png")
                    .OrderByDescending(f => f.Name)
                    .FirstOrDefault();

                if (file == null)
                    return Problem(
                        title: "Аватар не найден",
                        statusCode: StatusCodes.Status404NotFound
                    );

                var bytes = System.IO.File.ReadAllBytes(file.FullName);

                return File(bytes, "image/png");
            }
            catch
            {
                return ServerError();
            }
        }

        private static uint GetNewId(string userFolder)
        {
            var filesInDir =
                    Directory.GetFiles(userFolder)
                        .Select(Path.GetFileName)
                        .ToList();

            uint newIndex = 1;
            uint? lastPhotoId = null;

            if (filesInDir is not null)
                foreach (var f in filesInDir)
                {
                    if (string.IsNullOrWhiteSpace(f)) continue;

                    string[] words = f.Split("_");

                    uint index = Convert.ToUInt32(words[1]);

                    if (lastPhotoId is null)
                    {
                        lastPhotoId = index;
                        continue;
                    }

                    if (index > lastPhotoId)
                        lastPhotoId = index;
                }

            if (lastPhotoId is not null)
                newIndex = (uint)lastPhotoId + 1;

            return newIndex;
        }
    }
}
