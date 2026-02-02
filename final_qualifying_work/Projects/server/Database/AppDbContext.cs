using Microsoft.EntityFrameworkCore;
using server.Models;

namespace server.Database
{
    public class AppDbContext : DbContext
    {
        public AppDbContext(DbContextOptions options) : base(options) { }

        public DbSet<AccessRight> AccessRights => Set<AccessRight>();
        public DbSet<Person> Persons => Set<Person>();
        public DbSet<CarBody> CarBodies => Set<CarBody>();
        public DbSet<CarGearbox> CarGearboxes => Set<CarGearbox>();
        public DbSet<FuelType> FuelTypes => Set<FuelType>();
        public DbSet<CarDrive> CarDrives => Set<CarDrive>();
        public DbSet<EngineType> EngineTypes => Set<EngineType>();
        public DbSet<EngineConfiguration> EngineConfigurations => Set<EngineConfiguration>();
        public DbSet<CarBrand> CarBrands => Set<CarBrand>();
        public DbSet<CarBrandModel> CarBrandsModels => Set<CarBrandModel>();
        public DbSet<CarConfiguration> CarConfigurations => Set<CarConfiguration>();
        public DbSet<Car> Cars => Set<Car>();
        public DbSet<OBDIIService> OBDIIServices => Set<OBDIIService>();
        public DbSet<OBDIIPID> OBDIIPIDs => Set<OBDIIPID>();
        public DbSet<OBDIIDevice> OBDIIDevices => Set<OBDIIDevice>();
        public DbSet<Trip> Trips => Set<Trip>();
        public DbSet<TelemetryData> TelemetryData => Set<TelemetryData>();
        public DbSet<GPSData> GPSData => Set<GPSData>();

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder);

            modelBuilder.Entity<AccessRight>(entity =>
            {
                entity.HasKey(a => a.RightLevel);
            });

            modelBuilder.Entity<Person>(entity =>
            {
                entity.HasKey(p => p.PersonId);

                entity.Property(p => p.Birth).HasColumnType("date");

                entity.HasIndex(p => p.Email).IsUnique();

                entity.HasIndex(p => p.Phone).IsUnique();

                entity.HasIndex(p => p.DriveLisense).IsUnique();

                entity.HasOne(p => p.AccessRight).WithMany().HasForeignKey(p => p.RightLevel).OnDelete(DeleteBehavior.Cascade);
            });

            modelBuilder.Entity<CarBody>(entity =>
            {
                entity.HasKey(cb => cb.BodyId);

                entity.HasIndex(cb => cb.BodyName).IsUnique();
            });

            modelBuilder.Entity<CarGearbox>(entity =>
            {
                entity.HasKey(cg => cg.GearboxId);

                entity.HasIndex(cg => cg.GearboxName).IsUnique();
            });

            modelBuilder.Entity<FuelType>(entity =>
            {
                entity.HasKey(ft => ft.TypeId);

                entity.HasIndex(ft => ft.TypeName).IsUnique();
            });

            modelBuilder.Entity<CarDrive>(entity =>
            {
                entity.HasKey(cd => cd.DriveId);

                entity.HasIndex(cd => cd.DriveName).IsUnique();
            });

            modelBuilder.Entity<EngineType>(entity =>
            {
                entity.HasKey(et => et.TypeId);

                entity.HasIndex(et => et.TypeName).IsUnique();
            });

            modelBuilder.Entity<EngineConfiguration>(entity =>
            {
                entity.HasKey(ec => ec.EngineConfigId);

                entity.HasOne(ec => ec.EngineType).WithMany().HasForeignKey(ec => ec.EngineTypeId).OnDelete(DeleteBehavior.Cascade);

                entity.HasOne(ec => ec.FuelType).WithMany().HasForeignKey(ec => ec.FuelTypeId).OnDelete(DeleteBehavior.Cascade);

                entity.HasIndex(ec => new {
                    ec.EnginePowerHP,
                    ec.EnginePowerKW,
                    ec.EngineTypeId,
                    ec.EngineCapacityL,
                    ec.TankCapacityL,
                    ec.FuelTypeId
                }).IsUnique();
            });

            modelBuilder.Entity<CarBrand>(entity =>
            {
                entity.HasKey(cbr => cbr.BrandId);

                entity.HasIndex(cbr => cbr.BrandName).IsUnique();
            });

            modelBuilder.Entity<CarBrandModel>(entity =>
            {
                entity.HasKey(cbm => cbm.CarBrandModelId);

                entity.HasIndex(cbm => new {
                    cbm.ModelName,
                    cbm.BrandId
                }).IsUnique();

                entity.HasOne(cbm => cbm.CarBrand).WithMany().HasForeignKey(cbm => cbm.BrandId).OnDelete(DeleteBehavior.Cascade);
            });

            modelBuilder.Entity<CarConfiguration>(entity =>
            {
                entity.HasKey(cc => cc.CarConfigId);

                entity.HasOne(cc => cc.CarBrandModel).WithMany().HasForeignKey(cc => cc.CarBrandModelId).OnDelete(DeleteBehavior.Cascade);

                entity.HasOne(cc => cc.CarBody).WithMany().HasForeignKey(cc => cc.BodyId).OnDelete(DeleteBehavior.Cascade);

                entity.HasOne(cc => cc.CarGearbox).WithMany().HasForeignKey(cc => cc.GearboxId).OnDelete(DeleteBehavior.Cascade);

                entity.HasOne(cc => cc.CarDrive).WithMany().HasForeignKey(cc => cc.DriveId).OnDelete(DeleteBehavior.Cascade);

                entity.HasOne(cc => cc.EngineConfiguration).WithMany().HasForeignKey(cc => cc.EngineConfId).OnDelete(DeleteBehavior.Cascade);
            });

            modelBuilder.Entity<Car>(entity =>
            {
                entity.HasKey(c => c.CarId);

                entity.HasOne(c => c.Person).WithMany().HasForeignKey(c => c.PersonId).OnDelete(DeleteBehavior.Cascade);

                entity.HasOne(c => c.CarConfiguration).WithMany().HasForeignKey(c => c.CarConfigId).OnDelete(DeleteBehavior.Cascade);

                entity.HasIndex(c => c.VINNumber).IsUnique();

                entity.HasIndex(c => c.StateNumber).IsUnique();
            });

            modelBuilder.Entity<OBDIIService>(entity =>
            {
                entity.HasKey(s => s.ServiceId);

                entity.HasIndex(s => s.ServiceDescription).IsUnique();
            });

            modelBuilder.Entity<OBDIIPID>(entity =>
            {
                entity.HasKey(p => p.OBDIIPIDId);

                entity.HasOne(p => p.OBDIIService).WithMany().HasForeignKey(p => p.ServiceId).OnDelete(DeleteBehavior.Cascade);

                entity.HasIndex(p => new
                {
                    p.ServiceId,
                    p.PID
                }).IsUnique();
            });

            modelBuilder.Entity<OBDIIDevice>(entity =>
            {
                entity.HasKey(d => d.DeviceId);

                entity.HasIndex(d => d.MACAddress).IsUnique();
            });

            modelBuilder.Entity<Trip>(entity =>
            {
                entity.HasKey(t => t.TripId);

                entity.HasOne(t => t.OBDIIDevice).WithMany().HasForeignKey(t => t.DeviceId).OnDelete(DeleteBehavior.Cascade);

                entity.HasOne(t => t.Car).WithMany().HasForeignKey(t => t.CarId).OnDelete(DeleteBehavior.Cascade);
            });

            modelBuilder.Entity<TelemetryData>(entity =>
            {
                entity.HasKey(t => t.RecId);

                entity.HasOne(t => t.Trip).WithMany().HasForeignKey(t => t.TripId).OnDelete(DeleteBehavior.Cascade);

                entity.HasOne(t => t.OBDIIPID).WithMany().HasForeignKey(t => t.OBDIIPIDId).OnDelete(DeleteBehavior.Cascade);
            });

            modelBuilder.Entity<GPSData>(entity =>
            {
                entity.HasKey(g => g.RecId);

                entity.HasOne(g => g.Trip).WithMany().HasForeignKey(t => t.TripId).OnDelete(DeleteBehavior.Cascade);
            });
        }
    }
}
