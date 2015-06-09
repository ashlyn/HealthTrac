using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using HealthTrac.Data_Access;
using HealthTrac.Models;

namespace HealthTrac.Application.Services
{
    public class MembershipService : IMembershipService
    {
        private readonly IMembershipRepository _membershipRepository;
        private readonly IFeedEventService _feedEventService;
        private readonly IUserBadgeService _userBadgeService;
        private readonly IUnitOfWork _unit;

        public MembershipService(IMembershipRepository membershipRepository, IFeedEventService feedEventService, IUserBadgeService userBadgeService, IUnitOfWork unit)
        {
            _membershipRepository = membershipRepository;
            _feedEventService = feedEventService;
            _userBadgeService = userBadgeService;
            _unit = unit;
        }

        public IList<Membership> GetMemberships()
        {
            return _membershipRepository.ReadAll().ToList();
        }

        public async Task<Membership> FindMembership(long id)
        {
            return await _membershipRepository.GetById(id);
        }

        public IList<Membership> GetUserMemberships(string userId)
        {
            var memberships = _membershipRepository.GetByUser(userId);
            return memberships.ToList();
        }

        public IList<Membership> GetUserInvites(string userId)
        {
            var memberships = _membershipRepository.GetInvitesByUser(userId);
            return memberships.ToList();
        }

        public IList<Membership> GetGroupMemberships(long groupId)
        {
            var memberships = _membershipRepository.GetByGroup(groupId);
            return memberships.ToList();
        }

        public async Task<long> CreateMembership(Membership membership)
        {
            _membershipRepository.Create(membership);
            await _unit.Commit();
            if (membership.Status == Status.Member || membership.Status == Status.Admin)
            {
                await _feedEventService.GenerateFeedEvent(membership);
                _userBadgeService.CheckGroupBadgeProgress(membership.UserId);
                await _unit.Commit();
            }
            return membership.Id;
        }

        public async Task UpdateMembership(Membership membership)
        {
            _membershipRepository.Update(membership);
            if (membership.Status == Status.Left || membership.Status == Status.Member)
            {
                await _feedEventService.GenerateFeedEvent(membership);
                _userBadgeService.CheckGroupBadgeProgress(membership.UserId);
            }
            await _unit.Commit();
        }

        public async Task DeleteMembership(long id)
        {
            var membership = await _membershipRepository.GetById(id);
            await _membershipRepository.Delete(id);
            await _unit.Commit();
        }

        #region Disposing
        private bool _disposed;

        public void Dispose()
        {
            Dispose(true);
            GC.SuppressFinalize(this);
        }

        ~MembershipService()
        {
            Dispose(false);
        }

        protected virtual void Dispose(bool disposing)
        {
            if (_disposed)
            {
                return;
            }
            if (disposing)
            {

            }
            _disposed = true;
        }
        #endregion
    }
}