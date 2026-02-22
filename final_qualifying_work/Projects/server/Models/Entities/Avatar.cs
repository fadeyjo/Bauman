using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace server.Models.Entities
{
    [Table("avatars")]
    public class Avatar
    {
        [Key]
        [Column("avatar_id")]
        public uint AvatarId { get; set; }

        [Column("avatar_url")]
        public string AvatarUrl { get; set; } = null!;

        [Column("person_id")]
        public uint PersonId { get; set; }
    }
}
