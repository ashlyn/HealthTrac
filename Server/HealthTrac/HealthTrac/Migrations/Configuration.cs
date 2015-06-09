using System.Collections.Generic;
using System.Data.Entity.Migrations;
using HealthTrac.Models;

namespace HealthTrac.Migrations
{
    internal sealed class Configuration : DbMigrationsConfiguration<HealthTrac.Models.ApplicationDbContext>
    {
        public Configuration()
        {
            AutomaticMigrationsEnabled = true;
            AutomaticMigrationDataLossAllowed = true;
            ContextKey = "HealthTrac.Models.ApplicationDbContext";
        }

        protected override void Seed(ApplicationDbContext context)
        {
            //Seed data for groups
            //Uncomment to seed for testing purposes
            //Seeded groups will not start out with users/memberships, so they will not adhere to the 
            //"delete when the last user leaves" rule
            /*context.Groups.AddOrUpdate(g => g.GroupName,
                new Group { GroupName = "Runnrs4Lyfe", Description = "Friendly group of lifetime runners (50+)", ImageUrl = @"http://i.imgur.com/A7F8ZDp.png", GroupMembers = new List<Membership> { } },
                new Group { GroupName = "Pizza team", Description = "We are teh pizza eaters", ImageUrl = @"http://i.imgur.com/8u90xhH.png", GroupMembers = new List<Membership> { } },
                new Group { GroupName = "Team Team", Description = "Team team Team Team", ImageUrl = @"http://i.imgur.com/aidzS63.png", GroupMembers = new List<Membership> { } },
                new Group { GroupName = "Cheer for life!", Description = "Lyk dis if u cheer evertim", ImageUrl = @"http://i.imgur.com/cawYNKA.png", GroupMembers = new List<Membership> { } },
                new Group { GroupName = "Fight Club", Description = "You don't talk about it", ImageUrl = @"http://i.imgur.com/1xXBpoV.png", GroupMembers = new List<Membership> { } }
            );*/

            context.Badges.AddOrUpdate(b => b.Id,
                new Badge { Id = 1, Name = "Join the Club", Description = "Created a profile!", ImageUrl = @"http://i.imgur.com/YIXDwLt.png" },
                new Badge { Id = 2,  Name = "Baby's first steps", Description = "Walked more than 100 steps in a day!", ImageUrl = @"http://i.imgur.com/br2lJaz.png" },
                new Badge { Id = 3, Name = "Marathoner", Description = "Ran 26 miles in a day!", ImageUrl = @"http://i.imgur.com/Opd9NNi.png" },
                new Badge { Id = 4, Name = "Couch Potato", Description = "Taken less than 5000 steps a day for a week!", ImageUrl = @"http://i.imgur.com/1dzpKnL.png" },
                new Badge { Id = 5, Name = "Training Wheels", Description = "Biked 10 miles in one activity", ImageUrl = @"http://i.imgur.com/dMuJjsI.png" },
                new Badge { Id = 6, Name = "Lance Armstrong", Description = "Biked 100 miles in a day!", ImageUrl = @"http://i.imgur.com/FL4VwAG.png" },
                new Badge { Id = 7, Name = "Power Walker", Description = "Walk 10 miles in one activity", ImageUrl = @"http://i.imgur.com/axjjPIR.png" },
                new Badge { Id = 8, Name = "Yogging", Description = "Jogged 10 miles in one activity!", ImageUrl = @"http://i.imgur.com/iSzclz4.png" },
                new Badge { Id = 9, Name = "Groupie", Description = "Member of 10 or more groups", ImageUrl = @"http://i.imgur.com/AQ48QbM.png" },
                new Badge { Id = 10,  Name = "One of Us!", Description = "Joined a group!", ImageUrl = @"http://i.imgur.com/eD27Qz1.png"},
                new Badge { Id = 11, Name = "Look at me. I am the captain now", Description = "Became a Group Admin", ImageUrl = @"http://i.imgur.com/zA8rijq.png" },
                new Badge { Id = 12, Name = "Literally Tobin", Description = "Banned a member from a group.", ImageUrl = @"http://i.imgur.com/lay40IT.png" }
            );

            context.Moods.AddOrUpdate(m => m.Type,
                new Mood { Type = "angry", ImageUrl = @"http://i.imgur.com/kClMrGV.png" },
                new Mood { Type = "anxious", ImageUrl = @"http://i.imgur.com/HJwmzpO.png" },
                new Mood { Type = "accomplished", ImageUrl = @"http://i.imgur.com/loh7Uvu.png" },
                new Mood { Type = "fabulous", ImageUrl = @"http://i.imgur.com/g5TYEBn.png" },
                new Mood { Type = "happy", ImageUrl = @"http://i.imgur.com/Z54l7Uz.png" },
                new Mood { Type = "motivated", ImageUrl = @"http://i.imgur.com/N1u5cxZ.png" },
                new Mood { Type = "sad", ImageUrl = @"http://i.imgur.com/ZZfDpFI.png" },
                new Mood { Type = "salty", ImageUrl = @"http://i.imgur.com/wnuMWSv.png" },
                new Mood { Type = "sick", ImageUrl = @"http://i.imgur.com/TqKBTvv.png" },
                new Mood { Type = "sweaty", ImageUrl = @"http://i.imgur.com/Tvn6NK7.png" },
                new Mood { Type = "tired", ImageUrl = @"http://i.imgur.com/s4bm7IR.png" },
                new Mood { Type = "victorious", ImageUrl = @"http://i.imgur.com/S4YZt5j.png" }
            );

            context.SaveChanges();
        }
    }
}
