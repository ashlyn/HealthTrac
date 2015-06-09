using HealthTrac.Application.Services;
using HealthTrac.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;

namespace HealthTrac.Controllers
{
    public class LeaderboardController : Controller
    {
        private readonly IGroupService _groupService;

        public LeaderboardController(IGroupService groupService)
        {
            _groupService = groupService;
        }

        public ActionResult DistanceLeaderboard(Group group)
        {
            var leaders = _groupService.GetLeaderBoard(group.Id, "Distance");
            var vm = new LeaderboardViewModel { Leaders = leaders };
            return PartialView("_LeaderboardPartial", vm);
        }
    }
}