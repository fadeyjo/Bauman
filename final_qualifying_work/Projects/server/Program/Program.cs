
using Microsoft.EntityFrameworkCore;
using server.Database;

namespace server
{
    public class Program
    {
        public static void Main(string[] args)
        {
            var builder = WebApplication.CreateBuilder(args);

            builder.WebHost.ConfigureKestrel(options => {
                options.ListenAnyIP(5000);
                options.ListenAnyIP(
                    5001,
                    listenOptions =>
                    {
                        listenOptions.UseHttps("C:\\cert\\myapi.pfx", "123456");
                    }
                );
            });

            // Add services to the container.

            builder.Services.AddControllers();
            // Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
            builder.Services.AddEndpointsApiExplorer();
            builder.Services.AddSwaggerGen();

            try
            {
                builder.Services.AddDbContext<AppDbContext>(options =>
                {
                    options.UseMySql(
                        builder.Configuration.GetConnectionString("DefaultConnection"),
                        ServerVersion.AutoDetect(
                            builder.Configuration.GetConnectionString("DefaultConnection")
                        )
                    );
                });
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
                return;
            }

            var app = builder.Build();

            using (var scope = app.Services.CreateScope())
            {
                var context = scope.ServiceProvider.GetRequiredService<AppDbContext>();

                if (!context.Database.CanConnect())
                {
                    Console.WriteLine("Не удалось подключиться к базе данных");
                    return;
                }
            }

            // Configure the HTTP request pipeline.
            if (app.Environment.IsDevelopment())
            {
                app.UseSwagger();
                app.UseSwaggerUI();
            }

            app.UseHttpsRedirection();

            app.UseAuthorization();


            app.MapControllers();

            app.Run();
        }
    }
}
