using System;
using System.Data.Entity;
using HealthTrac.Application;
using HealthTrac.Application.Services;
using HealthTrac.Data_Access;
using HealthTrac.Models;
using Microsoft.AspNet.Identity;
using Microsoft.AspNet.Identity.EntityFramework;
using Microsoft.Practices.ServiceLocation;
using Microsoft.Practices.Unity;

namespace HealthTrac.App_Start
{
    /// <summary>
    /// Specifies the Unity configuration for the main container.
    /// </summary>
    public class UnityConfig
    {
        #region Unity Container
        private static Lazy<IUnityContainer> container = new Lazy<IUnityContainer>(() =>
        {
            var container = new UnityContainer();
            RegisterTypes(container);
            return container;
        });

        /// <summary>
        /// Gets the configured Unity container.
        /// </summary>
        public static IUnityContainer GetConfiguredContainer()
        {
            return container.Value;
        }
        #endregion

        /// <summary>Registers the type mappings with the Unity container.</summary>
        /// <param name="container">The unity container to configure.</param>
        /// <remarks>There is no need to register concrete types such as controllers or API controllers (unless you want to 
        /// change the defaults), as Unity allows resolving a concrete type even if it was not previously registered.</remarks>
        public static void RegisterTypes(IUnityContainer container)
        {
            // NOTE: To load from web.config uncomment the line below. Make sure to add a Microsoft.Practices.Unity.Configuration to the using statements.
            // container.LoadConfiguration();

            //register application units & services
            container.RegisterType(typeof(IUnitOfWork), typeof(UnitOfWork));
            container.RegisterType(typeof(IUserService), typeof(UserService));
            container.RegisterType(typeof(IActivityService), typeof(ActivityService));
            container.RegisterType(typeof(IBadgeService), typeof(BadgeService));
            container.RegisterType(typeof (IChallengeService), typeof (ChallengeService));
            container.RegisterType(typeof (IEndOfDayReportService), typeof (EndOfDayReportService));
            container.RegisterType(typeof (IFeedEventService), typeof (FeedEventService));
            container.RegisterType(typeof(IFoodService), typeof(FoodService));
            container.RegisterType(typeof(IGeoPointService), typeof(GeoPointService));
            container.RegisterType(typeof(IGoalService), typeof(GoalService));
            container.RegisterType(typeof(IGroupService), typeof(GroupService));
            container.RegisterType(typeof(IMembershipService), typeof(MembershipService));
            container.RegisterType(typeof(IMoodService), typeof(MoodService));
            container.RegisterType(typeof (IUserBadgeService), typeof (UserBadgeService));
            container.RegisterType(typeof(IUserMoodService), typeof(UserMoodService));

            //register repositories
            container.RegisterType(typeof(IUserRepository), typeof(UserRepository));
            container.RegisterType(typeof(IActivityRepository), typeof(ActivityRepository));
            container.RegisterType(typeof(IBadgeRepository), typeof(BadgeRepository));
            container.RegisterType(typeof (IChallengeRepository), typeof (ChallengeRepository));
            container.RegisterType(typeof (IEndOfDayReportRepository), typeof (EndOfDayReportRepository));
            container.RegisterType(typeof (IFeedEventRepository), typeof (FeedEventRepository));
            container.RegisterType(typeof(IFoodRepository), typeof(FoodRepository));
            container.RegisterType(typeof(IGeoPointRepository), typeof(GeoPointRepository));
            container.RegisterType(typeof(IGoalRepository), typeof(GoalRepository));
            container.RegisterType(typeof(IGroupRepository), typeof(GroupRepository));
            container.RegisterType(typeof(IMembershipRepository), typeof(MembershipRepository));
            container.RegisterType(typeof(IMoodRepository), typeof(MoodRepository));
            container.RegisterType(typeof (IUserBadgeRepository), typeof (UserBadgeRepository));
            container.RegisterType(typeof(IUserMoodRepository), typeof(UserMoodRepository));

            container.RegisterType<UserManager<User>>(new PerResolveLifetimeManager());
            container.RegisterType<IUserStore<User>, UserStore<User>>(new PerResolveLifetimeManager());
            container.RegisterType<DbContext, ApplicationDbContext >(new HierarchicalLifetimeManager());

            //register db connections
            container.RegisterType<ApplicationDbContext, ApplicationDbContext>(new HierarchicalLifetimeManager());
            container.RegisterInstance(new ApplicationContextAdapter(container.Resolve<ApplicationDbContext>()), new HierarchicalLifetimeManager());
            container.RegisterType<IDbSetFactory>(new InjectionFactory(c => c.Resolve<ApplicationContextAdapter>()));
            container.RegisterType<IDbContext>(new InjectionFactory(c => c.Resolve<ApplicationContextAdapter>()));

            ServiceLocator.SetLocatorProvider(() => new UnityServiceLocator(container));
        }
    }
}
