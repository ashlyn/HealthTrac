﻿using System.Web.Http;

namespace HealthTrac
{
    public static class WebApiConfig
    {
        public static void Register(HttpConfiguration config)
        {
            // Web API configuration and services

            // Web API routes
            config.MapHttpAttributeRoutes();

            config.Routes.MapHttpRoute(
                name: "DefaultApi",
                routeTemplate: "api/{controller}/{id}",
                defaults: new { id = RouteParameter.Optional }
            );

            config.Routes.MapHttpRoute(
                name: "SearchByName",
                routeTemplate: "api/{controller}/{action}/{name}"
            );

            config.Routes.MapHttpRoute(
                name: "FindSocialUser",
                routeTemplate: "api/{controller}/{action}/{socialKey}"
            );
        }
    }
}