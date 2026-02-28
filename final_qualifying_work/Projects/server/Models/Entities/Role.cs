using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace server.Models.Entities
{
    [Table("roles")]
    public class Role
    {
        [Key]
        [Column("role_id")]
        public byte RoleId { get; set; }

        [Column("role_name")]
        public string RoleName { get; set; } = null!;
    }
}
