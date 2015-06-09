using System.Collections.Generic;
using System.Configuration;
using System.Linq;
using System.Text;
using System.Web;
using System.Web.Mvc;
using HealthTrac.Models;

namespace HealthTrac.Utilities
{
    public static class ViewHelper {
        public static MvcHtmlString DisplayBadge(this HtmlHelper helper, Badge badge) {
            TagBuilder liTag = new TagBuilder("li");
            liTag.AddCssClass("list-group-item");
            TagBuilder img = new TagBuilder("img");
            img.AddCssClass(".img-responsive");
            img.MergeAttribute("src", badge.ImageUrl);
            TagBuilder caption = new TagBuilder("div");
            TagBuilder headingTag = new TagBuilder("h4");
            headingTag.AddCssClass("list-group-item-heading");
            headingTag.SetInnerText(badge.Name);
            TagBuilder pTag = new TagBuilder("p");
            pTag.AddCssClass("list-group-item-text");
            pTag.SetInnerText(badge.Description);
            caption.InnerHtml = headingTag + pTag.ToString();
            liTag.InnerHtml = img.ToString() + caption;
            return MvcHtmlString.Create(liTag.ToString());
        }

        public static MvcHtmlString DisplayGroup(this HtmlHelper helper, Group group)
        {
            TagBuilder aTag = new TagBuilder("a");
            aTag.AddCssClass("list-group-item");
            aTag.MergeAttribute("href", string.Format("/group/details/{0}", group.Id));
            TagBuilder img = new TagBuilder("img");
            img.AddCssClass(".img-responsive");
            img.MergeAttribute("src", group.ImageUrl);
            TagBuilder caption = new TagBuilder("div");
            TagBuilder headingTag = new TagBuilder("h4");
            headingTag.AddCssClass("list-group-item-heading");
            headingTag.SetInnerText(group.GroupName);
            TagBuilder pTag = new TagBuilder("p");
            pTag.AddCssClass("list-group-item-text");
            pTag.SetInnerText(group.Description);
            caption.InnerHtml = headingTag + pTag.ToString();
            aTag.InnerHtml = img + caption.ToString();
            return MvcHtmlString.Create(aTag.ToString());
        }

        public static MvcHtmlString DisplayActivity(this HtmlHelper helper, Activity activity)
        {
            TagBuilder liTag = new TagBuilder("li");
            liTag.AddCssClass("list-group-item");
            TagBuilder headingTag = new TagBuilder("h4");
            headingTag.AddCssClass("list-group-item-heading");
            headingTag.SetInnerText(activity.Name);
            TagBuilder pTag = new TagBuilder("p");
            pTag.AddCssClass("list-group-item-text");
            pTag.SetInnerText(string.Format("{0:0.00} minutes", activity.Duration / 60));
            liTag.InnerHtml = headingTag + pTag.ToString();
            return MvcHtmlString.Create(liTag.ToString());
        }

        public static MvcHtmlString DisplayUser(this HtmlHelper helper, User user)
        {
            TagBuilder aTag = new TagBuilder("a");
            aTag.AddCssClass("list-group-item");
            aTag.MergeAttribute("href", string.Format("/user/details/{0}", user.Id));
            TagBuilder img = new TagBuilder("img");
            img.AddCssClass(".img-responsive");
            img.MergeAttribute("src", user.ImageUrl);
            TagBuilder caption = new TagBuilder("div");
            TagBuilder headingTag = new TagBuilder("h4");
            headingTag.AddCssClass("list-group-item-heading");
            headingTag.SetInnerText(user.FullName);
            TagBuilder pTag = new TagBuilder("p");
            pTag.AddCssClass("list-group-item-text");
            pTag.SetInnerText("Location:  " + user.Location);
            caption.InnerHtml = headingTag + pTag.ToString();
            aTag.InnerHtml = img + caption.ToString();
            return MvcHtmlString.Create(aTag.ToString());
        }

        public static MvcHtmlString DisplayLeader(this HtmlHelper helper, User user, double rank, double value)
        {
            TagBuilder aTag = new TagBuilder("a");
            aTag.AddCssClass("list-group-item");
            aTag.MergeAttribute("href", string.Format("/user/details/{0}", user.Id));
            TagBuilder headingTag = new TagBuilder("h4");
            headingTag.AddCssClass("list-group-item-heading");
            headingTag.SetInnerText(string.Format("#{0}:  {1}", rank, user.FullName));
            TagBuilder pTag = new TagBuilder("p");
            pTag.AddCssClass("list-group-item-text");
            pTag.SetInnerText(value + " miles");
            aTag.InnerHtml = headingTag.ToString() + pTag.ToString();
            return MvcHtmlString.Create(aTag.ToString());
        }

        public static MvcHtmlString DisplayGoal(this HtmlHelper helper, Goal goal)
        {
            TagBuilder containerDiv = new TagBuilder("div");
            containerDiv.AddCssClass("container");
            TagBuilder captionDiv = new TagBuilder("div");
            captionDiv.AddCssClass("carousel-caption");
            captionDiv.InnerHtml = goal.CreateGoalChart().ToHtmlString();
            containerDiv.InnerHtml = captionDiv.ToString();
            return MvcHtmlString.Create(containerDiv.ToString());
        }

        public static MvcHtmlString DisplayGoals(this HtmlHelper helper, IList<Goal> goals)
        {
            var urlHelper = new UrlHelper(helper.ViewContext.RequestContext);
            StringBuilder indicators = new StringBuilder();
            TagBuilder inner = new TagBuilder("div");
            inner.AddCssClass("carousel-inner");
            inner.MergeAttribute("role", "listbox");
            inner.MergeAttribute("style", "background-color:  transparent;");
            
            //inner.InnerHtml = img.ToString();
            for (int i = 0; i < goals.Count; i++)
            {
                TagBuilder img = new TagBuilder("img");
                img.AddCssClass("placeholder");
                img.MergeAttribute("src", urlHelper.Content("~/Content/goal_placeholder.png"));
                TagBuilder indicator = new TagBuilder("li");
                indicator.MergeAttribute("data-target", "#myCarousel");
                indicator.MergeAttribute("data-slide-to", i.ToString());

                TagBuilder item = new TagBuilder("div");
                if (i == 0)
                {
                    indicator.AddCssClass("active");
                    item.AddCssClass("item active");
                }
                else
                {
                    item.AddCssClass("item");
                }
                indicator.MergeAttribute("style", "background-color:  #238795;");
                indicator.MergeAttribute("onclick", "setTimeout(function() {$(window).trigger('resize')}, 200);");
                indicators.Append(indicator);
                item.InnerHtml = img + helper.DisplayGoal(goals[i]).ToHtmlString();
                inner.InnerHtml = inner.InnerHtml + item;
            }
            TagBuilder carousel = new TagBuilder("div");
            carousel.MergeAttribute("id", "myCarousel");
            carousel.AddCssClass("carousel slide");
            carousel.MergeAttribute("data-ride", "carousel");
            carousel.MergeAttribute("data-interval", "false");
            TagBuilder ol = new TagBuilder("ol");
            ol.AddCssClass("carousel-indicators");
            ol.InnerHtml = indicators.ToString();

            #region navigation
            TagBuilder left = new TagBuilder("a");
            left.AddCssClass("left carousel-control");
            left.MergeAttribute("href", @"#myCarousel");
            left.MergeAttribute("role", "button");
            left.MergeAttribute("data-slide", "prev");
            left.MergeAttribute("onclick", "setTimeout(function() {$(window).trigger('resize')}, 200);");
            left.MergeAttribute("style", "background-image: none;");

            TagBuilder leftGlyph = new TagBuilder("span");
            leftGlyph.AddCssClass("glyphicon glyphicon-chevron-left");
            leftGlyph.MergeAttribute("aria-hidden", "true");
            leftGlyph.MergeAttribute("style", "color:  #238795;");

            TagBuilder leftText = new TagBuilder("span");
            leftText.AddCssClass("sr-only");
            leftText.SetInnerText("Previous");

            left.InnerHtml = leftGlyph.ToString() + leftText;

            TagBuilder right = new TagBuilder("a");
            right.AddCssClass("right carousel-control");
            right.MergeAttribute("href", @"#myCarousel");
            right.MergeAttribute("role", "button");
            right.MergeAttribute("data-slide", "next");
            right.MergeAttribute("onclick", "setTimeout(function() {$(window).trigger('resize')}, 200);");
            right.MergeAttribute("style", "background-image: none;");

            TagBuilder rightGlyph = new TagBuilder("span");
            rightGlyph.AddCssClass("glyphicon glyphicon-chevron-right");
            rightGlyph.MergeAttribute("aria-hidden", "true");
            rightGlyph.MergeAttribute("style", "color:  #238795;");

            TagBuilder rightText = new TagBuilder("span");
            rightText.AddCssClass("sr-only");
            rightText.SetInnerText("Next");

            right.InnerHtml = rightGlyph.ToString() + rightText;
            #endregion navigation
            carousel.InnerHtml = ol.ToString() + inner + left + right;
            return MvcHtmlString.Create(carousel.ToString());
        }

        public static MvcHtmlString DisplayTweetButton(this HtmlHelper helper, string description)
        {
            description = ManipulateDescription(description);
            TagBuilder a = new TagBuilder("a");
            a.AddCssClass("twitter-share-button btn btn-primary");
            a.MergeAttribute("href", string.Format("https://twitter.com/share?text={0}&count=none&hashtags=raik383H,insights,healthtrac", description));
            a.MergeAttribute("data-size", "large");
            a.MergeAttribute("data-count", "none");
            a.SetInnerText("Share");

            TagBuilder script = new TagBuilder("script");
            script.SetInnerText("window.twttr=(function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0],t=window.twttr" +
                                "||{};if(d.getElementById(id))return t;js=d.createElement(s);js.id=id;" +
                                "js.src='https://platform.twitter.com/widgets.js';fjs.parentNode.insertBefore(js,fjs);" +
                                "t._e=[];t.ready=function(f){t._e.push(f);};return t;}(document,'script','twitter-wjs'));");
            return MvcHtmlString.Create(a.ToString() + script);
        }

        public static MvcHtmlString DisplayFacebookButton(this HtmlHelper helper, string description)
        {
            description = ManipulateDescription(description);
            var link = @"https://www.facebook.com/dialog/feed?app_id=" + ConfigurationManager.AppSettings["appId"] + "&ref=site&display=page&" +
                       "name=HealthTrac" +
                       "&description=" + description +
                       "&picture=" + "http://i.imgur.com/h6IY4KP.png" +
                       "&link=http://se7.azurewebsites.net" +
                       "&display=popup" +
                       "&redirect_uri=" + HttpContext.Current.Request.Url.AbsoluteUri;
            TagBuilder a = new TagBuilder("a");
            a.AddCssClass("facebook-share-button btn btn-primary");
            a.MergeAttribute("href", link);
            a.SetInnerText("Share");
            return MvcHtmlString.Create(a.ToString());
        }

        private static string ManipulateDescription(string description)
        {
            description = description.Replace("You were", "I was");
            description = description.Replace("you were", "I was");
            description = description.Replace("Your", "My");
            description = description.Replace("your", "my");
            description = description.Replace("You", "I");
            description = description.Replace("you", "I");
            description = description.Replace("You're", "I'm");
            description = description.Replace("you're", "I'm");
            description += " You can track your activity with HealthTrac too!";
            return description;
        }

        public static T[,] ToMultidimensional<T>(this T[][] array)
        {
            int rows = array.Length;
            int cols = array.Max(s => s.Length);
            T[,] a = new T[rows, cols];

            for (int i = 0; i < rows; i++)
            {
                for (int j = 0; j < cols; j++)
                {
                    a[i, j] = array[i][j];
                }
            }
            return a;
        }
    }
}