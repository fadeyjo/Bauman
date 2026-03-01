namespace server.Models.Dtos
{
    public class CarPhotoDto
    {
        public uint PhotoId { get; set; }

        public DateTime CreatedAt { get; set; }

        public uint CarId { get; set; }

        public string PhotoUrl { get; set; } = null!;

        public string ContentType { get; set; } = null!;
    }
}
