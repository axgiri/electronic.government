package github.axgiri.AuthenticationService.Security;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import github.axgiri.AuthenticationService.DTO.UserDTO;
import github.axgiri.AuthenticationService.Enum.RoleEnum;
import github.axgiri.AuthenticationService.Service.UserService;

@Service("middleware")
@Transactional(readOnly = true)
public class Middleware {

    private final UserService userService;

    public Middleware(UserService userService) {
        this.userService = userService;
    }

    public boolean isCompanyAdmin(String username, Long companyId) {
        UserDTO userDTO = userService.getByEmail(username);
        if (userDTO.getCompanyId() == null) {
            throw new RuntimeException("current user does not belong to any company");
        }
        if (userDTO.getRole() == null) {
            throw new RuntimeException("current user does not have a role assigned");
        }

        return userDTO.getCompanyId().equals(companyId) && userDTO.getRole() == RoleEnum.ADMIN;
    }

    @Cacheable(value = "fourHoursCache", key="'is_' + #username + '_BelongsToCompany_' + #companyId")
    public boolean isCompanyMember(String username, Long companyId) {
        UserDTO userDTO = userService.getByEmail(username);
        if (userDTO.getCompanyId() == null) {
            return false;
        }
        return userDTO.getCompanyId().equals(companyId);
    }
    
    public boolean isSameUser(String username, Long userId) {
        UserDTO userDTO = userService.getByEmail(username);
        return userDTO.getId().equals(userId);
    }

    public boolean isCompanyModerator(String username, Long companyId) {
        UserDTO userDTO = userService.getByEmail(username);
        if (userDTO.getCompanyId() == null) {
            throw new RuntimeException("current user does not belong to any company");
        }
        if (userDTO.getRole() == null) {
            throw new RuntimeException("current user does not have a role assigned");
        }

        return userDTO.getCompanyId().equals(companyId) && (userDTO.getRole() == RoleEnum.MODERATOR || userDTO.getRole() == RoleEnum.ADMIN);
    }
    
    public boolean isCompanyModeratorForUser(String username, Long userId) {
        UserDTO currentUser = userService.getByEmail(username);
        UserDTO targetUser = userService.getById(userId);
        if (targetUser.getCompanyId() == null) {
            throw new RuntimeException("target user does not belong to any company");
        }
        if (currentUser.getCompanyId() == null) {
            throw new RuntimeException("current user does not belong to any company");
        }
        if (!currentUser.getCompanyId().equals(targetUser.getCompanyId())) {
            return false;
        }
        if (currentUser.getRole() == null) {
            throw new RuntimeException("current user does not have a role assigned");
        }
        return currentUser.getRole() == RoleEnum.MODERATOR || currentUser.getRole() == RoleEnum.ADMIN;
    }
}
