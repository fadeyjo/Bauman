using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Options;
using server.Database;
using server.Models.Dtos;
using server.Models.Entities;
using server.Utils.StoreService;
using System.Security.Claims;

namespace server.Controllers
{
    [ApiController]
    [Authorize]
    [Route("api/[controller]")]
    public class CarPhotosController: ControllerBase
    {
        private readonly AppDbContext _context;

        private readonly StoreOptions _storeOptions;

        public CarPhotosController(AppDbContext context, IOptions<StoreOptions> storeOptions)
        {
            _context = context;
            _storeOptions = storeOptions.Value;
        }

        private ObjectResult ServerError()
        {
            return Problem(
                title: "Внутренняя ошибка сервера",
                statusCode: StatusCodes.Status500InternalServerError
            );
        }

        [HttpPost("car_id/{carId}")]
        public async Task<IActionResult> NewCarPhoto([FromForm] IFormFile file, [FromRoute] uint carId)
        {
            try
            {
                bool exists = await _context.Cars.AnyAsync(c => c.CarId == carId);

                if (!exists)
                    return Problem(
                        title: "Автомобиль не найден",
                        statusCode: StatusCodes.Status404NotFound
                    );

                if (file is null)
                    return Problem(
                        title: "Не передан файл",
                        statusCode: StatusCodes.Status400BadRequest
                    );

                string carFolder = Path.Combine(_storeOptions.CarPhotosPath, carId.ToString());

                if (!Directory.Exists(carFolder))
                    Directory.CreateDirectory(carFolder);

                uint newId = GetNewId(carFolder);

                var extension = Path.GetExtension(file.FileName);
                string newFileName = $"photo_{newId}{extension}";

                string filePath = Path.Combine(carFolder, newFileName);

                using (var stream = new FileStream(filePath, FileMode.Create))
                    await file.CopyToAsync(stream);

                var createdAt = DateTime.UtcNow;

                var carPhoto = new CarPhoto()
                {
                    PhotoUrl = $"{carId}/{newFileName}",
                    CreatedAt = createdAt,
                    CarId = carId,
                    ContentType = file.ContentType
                };

                _context.CarPhotos.Add(carPhoto);

                await _context.SaveChangesAsync();

                var carPhotoResposne = new CarPhotoDto()
                {
                    PhotoId = carPhoto.PhotoId,
                    CreatedAt = createdAt,
                    CarId = carId,
                    PhotoUrl = carPhoto.PhotoUrl,
                    ContentType = file.ContentType
                };

                return CreatedAtAction(nameof(GetPhotoById), new { photoId = carPhoto.PhotoId }, carPhotoResposne);
            }
            catch
            {
                return ServerError();
            }
        }

        [HttpGet("photo_id/{photoId}")]
        public async Task<IActionResult> GetPhotoById([FromRoute] uint photoId)
        {
            try
            {
                var photo =
                    await _context.CarPhotos.FirstOrDefaultAsync(a => a.PhotoId == photoId);

                if (photo is null)
                    return Problem(
                        title: "Фото не найдено",
                        statusCode: StatusCodes.Status404NotFound
                    );

                string fullPath = Path.Combine(_storeOptions.CarPhotosPath, photo.PhotoUrl);

                if (!System.IO.File.Exists(fullPath))
                    return Problem(
                        title: "Фото не найдено",
                        statusCode: StatusCodes.Status404NotFound
                    );

                FileInfo file = new FileInfo(fullPath);

                var bytes = System.IO.File.ReadAllBytes(fullPath);

                return File(bytes, photo.ContentType);
            }
            catch
            {
                return ServerError();
            }
        }

        [HttpGet("last/{carId}")]
        public async Task<IActionResult> GetLastPhoto(uint carId)
        {
            try
            {
                bool exists = await _context.Cars.AnyAsync(c => c.CarId == carId);
                if (!exists)
                    return NotFound();

                string carFolder = Path.Combine(_storeOptions.CarPhotosPath, carId.ToString());
                string? lastPhotoName = GetLastPhotoName(carFolder);

                FileInfo file =
                    string.IsNullOrWhiteSpace(lastPhotoName)
                        ? new FileInfo(Path.Combine(_storeOptions.CarPhotosPath, "standart.png"))
                        : new FileInfo(Path.Combine(carFolder, lastPhotoName));

                if (!file.Exists)
                    return NotFound();

                var bytes = await System.IO.File.ReadAllBytesAsync(file.FullName);

                Response.Headers["Cache-Control"] = "public,max-age=86400";

                return File(bytes, GetContentType(file.Extension));
            }
            catch
            {
                return ServerError();
            }
        }

        private static uint GetNewId(string carFolder)
        {
            var filesInDir =
                    Directory.GetFiles(carFolder)
                        .Select(Path.GetFileName)
                        .ToList();

            uint newIndex = 1;
            uint? lastPhotoId = null;

            if (filesInDir is not null)
                foreach (var f in filesInDir)
                {
                    if (string.IsNullOrWhiteSpace(f)) continue;

                    string[] words = f.Split("_")[1].Split(".");

                    uint index = Convert.ToUInt32(words[0]);

                    if (lastPhotoId is not null && index <= lastPhotoId) continue;

                    lastPhotoId = index;
                }

            if (lastPhotoId is not null)
                newIndex = (uint)lastPhotoId + 1;

            return newIndex;
        }

        private static string? GetLastPhotoName(string userFolder)
        {
            if (!Directory.Exists(userFolder))
                return null;

            var filesInDir =
                    Directory.GetFiles(userFolder)
                        .Select(Path.GetFileName)
                        .ToList();

            string? lastPhotoName = null;
            uint? lastPhotoId = null;

            if (filesInDir is not null)
                foreach (var f in filesInDir)
                {
                    if (string.IsNullOrWhiteSpace(f)) continue;

                    string[] words = f.Split("_")[1].Split(".");

                    uint index = Convert.ToUInt32(words[0]);

                    if (lastPhotoId is not null && index <= lastPhotoId) continue;

                    lastPhotoId = index;
                    lastPhotoName = f;
                }


            return lastPhotoName;
        }

        private static string GetContentType(string extension)
        {
            return extension.ToLower() switch
            {
                ".png" => "image/png",
                ".jpg" => "image/jpeg",
                ".jpeg" => "image/jpeg",
                ".webp" => "image/webp",
                ".gif" => "image/gif",
                _ => "application/octet-stream"
            };
        }
    }
}
