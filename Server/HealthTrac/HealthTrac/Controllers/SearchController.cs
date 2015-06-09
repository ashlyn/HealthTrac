using System.Web.Mvc;
using HealthTrac.Application.Services;
using HealthTrac.Models;

namespace HealthTrac.Controllers
{
    public class SearchController : Controller
    {
        private readonly IUserService _userService;
        private readonly IGroupService _groupService;

        public SearchController(IUserService userService, IGroupService groupService)
        {
            _userService = userService;
            _groupService = groupService;
        }

        // GET: Search
        public ActionResult Index(string key)
        {
            var users = _userService.SearchForUsers(key);
            var groups = _groupService.Search(key);
            return View(new SearchViewModel {Users = users, Groups = groups, Key = key});
        }
    }
}