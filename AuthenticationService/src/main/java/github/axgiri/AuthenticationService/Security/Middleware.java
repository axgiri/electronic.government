package github.axgiri.AuthenticationService.Security;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import github.axgiri.AuthenticationService.Enum.RoleEnum;
import github.axgiri.AuthenticationService.Model.User;
import github.axgiri.AuthenticationService.Repository.UserRepository;

@Service("middleware")
@Transactional(readOnly = true)
public class Middleware {

    private final UserRepository userRepository;

    public Middleware(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean isCompanyAdmin(String username, Long companyId) {
        User user = findUserByEmail(username);
        if (user.getCompany() == null) {
            throw new RuntimeException("current user does not belong to any company");
        }
        if (user.getRole() == null) {
            throw new RuntimeException("current user does not have a role assigned");
        }

        return user.getCompany().getId().equals(companyId) && user.getRole() == RoleEnum.ADMIN;
    }

    public boolean isCompanyMember(String username, Long companyId) {
        User user = findUserByEmail(username);
        if (user.getCompany() == null) {
            return false;
        }
        return user.getCompany().getId().equals(companyId);
    }

    public boolean isSameUser(String username, Long userId) {
        User user = findUserByEmail(username);
        return user.getId().equals(userId);
    }

    public boolean isCompanyModerator(String username, Long companyId) {
        User user = findUserByEmail(username);
        if (user.getCompany() == null) {
            throw new RuntimeException("current user does not belong to any company");
        }
        if (user.getRole() == null) {
            throw new RuntimeException("current user does not have a role assigned");
        }

        return user.getCompany().getId().equals(companyId) && user.getRole() == RoleEnum.MODERATOR;
    }
    public boolean isUserOrCompanyModerator(String username, Long userId) {
        User currentUser = findUserByEmail(username);
        User targetUser = findUserById(userId);
        if (currentUser.getId().equals(targetUser.getId())) {
            return true;
        }
        return isCompanyModeratorForUser(username, userId);
    }

    public boolean isCompanyModeratorForUser(String username, Long userId) {
        User targetUser = findUserById(userId);
        if (targetUser.getCompany() == null) {
            throw new RuntimeException("target user does not belong to any company");
        }
        return isCompanyModerator(username, targetUser.getCompany().getId());
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("user with email " + email + " not found"));
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("user with id " + id + " not found"));
    }
}

