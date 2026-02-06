using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace server.Models.Entities
{
    [Table("engine_types")]
    public class EngineType
    {
        [Key]
        [Column("type_id")]
        public byte TypeId { get; set; }

        [Column("type_name")]
        public string TypeName { get; set; } = null!;
    }
}
