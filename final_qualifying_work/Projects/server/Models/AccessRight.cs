using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace server.Models
{
    [Table("access_rights")]
    public class AccessRight
    {
        [Key]
        [Column("right_level")]
        public byte RightLevel { get; set; }

        [Required(ErrorMessage = "Поле описания прав обязательно")]
        [Column("right_description")]
        public string RightDescription { get; set; } = null!;
    }
}
