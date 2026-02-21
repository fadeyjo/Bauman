namespace server.Models.Dtos
{
    public class AuthorizedDto
    {
        public string AccessToken { get; set; } = null!;

        public string RefreshToken { get; set; } = null!;
    }
}
