namespace server.Models.Dtos
{
    public class AvatarDto
    {
        public uint AvatarId { get; set; }

        public uint PersonId { get; set; }

        public string AvatarUrl { get; set; } = null!;
    }
}
