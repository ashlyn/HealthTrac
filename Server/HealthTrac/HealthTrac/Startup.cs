using Microsoft.Owin;
using Owin;

[assembly: OwinStartup(typeof(HealthTrac.Startup))]
namespace HealthTrac
{
    public partial class Startup
    {
        public void Configuration(IAppBuilder app)
        {
            ConfigureAuth(app);
        }
    }
}
