package github.axgiri.AuthenticationService.security;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import github.axgiri.AuthenticationService.service.UserService;
import lombok.RequiredArgsConstructor;
import github.axgiri.AuthenticationService.Enum.RoleEnum;
import github.axgiri.AuthenticationService.model.User;

@RequiredArgsConstructor
@Service("middleware")
public class Middleware {

    private final UserService userService;

    public boolean isCompanyAdmin(String username, Long companyId) {
        User user = userService.getByEmail(username);
        if (user.getCompany().getId() == null) {
            throw new RuntimeException("current user does not belong to any company");
        }
        if (user.getRole() == null) {
            throw new RuntimeException("current user does not have a role assigned");
        }

        return user.getCompany().getId().equals(companyId) && user.getRole() == RoleEnum.ADMIN;
    }

    @Cacheable(value = "fourHoursCache", key="'is_' + #username + '_BelongsToCompany_' + #companyId")
    public boolean isCompanyMember(String username, Long companyId) {
        User user = userService.getByEmail(username);
        if (user.getCompany().getId() == null) {
            return false;
        }
        return user.getCompany().getId().equals(companyId);
    }
    
    public boolean isSameUser(String username, Long userId) {
        User user = userService.getByEmail(username);
        return user.getId().equals(userId);
    }

    public boolean isCompanyModerator(String username, Long companyId) {
        User user = userService.getByEmail(username);
        if (user.getCompany().getId() == null) {
            throw new RuntimeException("current user does not belong to any company");
        }
        if (user.getRole() == null) {
            throw new RuntimeException("current user does not have a role assigned");
        }

        return user.getCompany().getId().equals(companyId) && (user.getRole() == RoleEnum.MODERATOR || user.getRole() == RoleEnum.ADMIN);
    }
    
    public boolean isCompanyModeratorForUser(String username, Long userId) {
        User currentUser = userService.getByEmail(username);
        User targetUser = userService.getById(userId);
        if (targetUser.getCompany().getId() == null) {
            throw new RuntimeException("target user does not belong to any company");
        }
        if (currentUser.getCompany().getId() == null) {
            throw new RuntimeException("current user does not belong to any company");
        }
        if (!currentUser.getCompany().getId().equals(targetUser.getCompany().getId())) {
            return false;
        }
        if (currentUser.getRole() == null) {
            throw new RuntimeException("current user does not have a role assigned");
        }
        return currentUser.getRole() == RoleEnum.MODERATOR || currentUser.getRole() == RoleEnum.ADMIN;
    }
}
