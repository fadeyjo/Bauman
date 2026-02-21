using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace server.Models.Entities
{
    [Table("refresh_tokens")]
    public class RefreshToken
    {
        [Key]
        [Column("token_id")]
        public ulong TokenId { get; set; }

        [Column("token_hash")]
        public string TokenHash { get; set; } = null!;

        [Column("expires")]
        public DateTime Expires { get; set; }

        [Column("is_revoked")]
        public bool IsRevoked { get; set; }

        [Column("person_id")]
        public uint PersonId { get; set; }

        [ForeignKey(nameof(PersonId))]
        public Person Person { get; set; } = null!;
    }
}
