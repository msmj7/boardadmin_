package com.boardadmin.user.controller;

import com.boardadmin.board.model.Board;
import com.boardadmin.board.service.BoardService;
import com.boardadmin.user.model.Role;
import com.boardadmin.user.model.User;
import com.boardadmin.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user-management")
public class UserController {

    private final UserService userService;
    
    @Autowired
    private BoardService boardService;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
    }

    @GetMapping
    public String getAdminsPage(@RequestParam(required = false) Long boardId, Model model) {
        List<User> allUsers = userService.getAllUsers();
        List<User> admins = allUsers.stream()
                .filter(user -> userService.hasRole(user, "ADMIN"))
                .collect(Collectors.toList());

        // Board 정보 추가
        if (boardId != null) {
            Board board = boardService.getBoardById(boardId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid board Id:" + boardId));
            model.addAttribute("board", board);
        }

        model.addAttribute("boards", boardService.getAllBoards());
        model.addAttribute("admins", admins);

        return "useradmin/admins";
    }

    @GetMapping("/users")
    public String getUsersPage(@RequestParam(required = false) Long boardId, Model model) {
        List<User> users = userService.getAllUsers().stream()
                .filter(user -> userService.hasRole(user, "USER"))
                .collect(Collectors.toList());

        if (boardId != null) {
            Board board = boardService.getBoardById(boardId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid board Id:" + boardId));
            model.addAttribute("board", board);
        }

        model.addAttribute("boards", boardService.getAllBoards());
        model.addAttribute("users", users);

        return "useradmin/users";
    }
    
    @GetMapping("/admin/{userIndex}")
    public String getAdminDetailPage(@PathVariable Integer userIndex, @RequestParam(required = false) Long boardId, Model model) {
        User admin = userService.getUserByUserIndex(userIndex);
        model.addAttribute("admin", admin);

        if (boardId != null) {
            Board board = boardService.getBoardById(boardId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid board Id:" + boardId));
            model.addAttribute("board", board);
        }

        model.addAttribute("boards", boardService.getAllBoards());
        return "useradmin/adminDetail";
    }

    @GetMapping("/user/{userIndex}")
    public String getUserDetailPage(@PathVariable Integer userIndex, @RequestParam(required = false) Long boardId, Model model) {
        User user = userService.getUserByUserIndex(userIndex);
        model.addAttribute("user", user);

        if (boardId != null) {
            Board board = boardService.getBoardById(boardId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid board Id:" + boardId));
            model.addAttribute("board", board);
        }

        model.addAttribute("boards", boardService.getAllBoards());
        return "useradmin/userDetail";
    }
    
    @GetMapping("/newAdmin")
    public String getAdminCreatePage(@RequestParam(required = false) Long boardId, Model model) {
        model.addAttribute("user", new User());

        if (boardId != null) {
            Board board = boardService.getBoardById(boardId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid board Id:" + boardId));
            model.addAttribute("board", board);
        }

        model.addAttribute("boards", boardService.getAllBoards());
        return "useradmin/newAdmin";
    }

    @GetMapping("/newUser")
    public String getUserCreatePage(@RequestParam(required = false) Long boardId, Model model) {
        model.addAttribute("user", new User());

        if (boardId != null) {
            Board board = boardService.getBoardById(boardId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid board Id:" + boardId));
            model.addAttribute("board", board);
        }

        model.addAttribute("boards", boardService.getAllBoards());
        return "useradmin/newUser";
    }

    @PostMapping("/create/admin")
    public String createAdmin(@ModelAttribute User user, Model model) {
        if (userService.userExists(user.getUserId())) {
            model.addAttribute("error", "User ID already exists");
            return "useradmin/newAdmin";
        }
        Set<Role> roles = new HashSet<>();
        roles.add(userService.getRoleByName("ADMIN"));
        user.setRoles(roles);
        userService.saveUser(user);
        return "redirect:/user-management";
    }

    @PostMapping("/create/user")
    public String createUser(@ModelAttribute User user, Model model) {
        if (userService.userExists(user.getUserId())) {
            model.addAttribute("error", "User ID already exists");
            return "useradmin/newUser";
        }

        Set<Role> roles = new HashSet<>();
        roles.add(userService.getRoleByName("USER"));
        user.setRoles(roles);
        userService.saveUser(user);
        return "redirect:/user-management/users";
    }
    
    @GetMapping("/updatepage/admin/{userIndex}")
    public String getAdminEditPage(@PathVariable Integer userIndex, Model model) {
        User admin = userService.getUserByUserIndex(userIndex);
        model.addAttribute("admin", admin);
        return "useradmin/adminEdit";
    }

    @PostMapping("/update/admin/{userIndex}")
    public String updateAdmin(@PathVariable Integer userIndex, @ModelAttribute User user, @RequestParam(required = false) Long boardId, Model model) {
        User existingUser = userService.getUserByUserIndex(userIndex);
        if (existingUser != null) {
            existingUser.setUserId(user.getUserId());
            existingUser.setEmail(user.getEmail());
            existingUser.setActive(user.isActive());
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                existingUser.setPassword(user.getPassword());
            }
            userService.updateUser(existingUser);
        }

        if (boardId != null) {
            return "redirect:/user-management?boardId=" + boardId;
        } else {
            return "redirect:/user-management";
        }
    }

    @DeleteMapping("/delete/admin/{userIndex}")
    public String deleteAdmin(@PathVariable Integer userIndex, @RequestParam(required = false) Long boardId) {
        userService.deleteUserByUserIndex(userIndex);

        if (boardId != null) {
            return "redirect:/user-management?boardId=" + boardId;
        } else {
            return "redirect:/user-management";
        }
    }

    
    @GetMapping("/updatepage/user/{userIndex}")
    public String getUserEditPage(@PathVariable Integer userIndex, Model model) {
        User user = userService.getUserByUserIndex(userIndex);
        model.addAttribute("user", user);
        return "useradmin/userEdit";
    }
    

    @PostMapping("/update/user/{userIndex}")
    public String updateUser(@PathVariable Integer userIndex, @ModelAttribute User user, @RequestParam(required = false) Long boardId, Model model) {
        User existingUser = userService.getUserByUserIndex(userIndex);
        if (existingUser != null) {
            existingUser.setUserId(user.getUserId());
            existingUser.setEmail(user.getEmail());
            existingUser.setActive(user.isActive());
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                existingUser.setPassword(user.getPassword());
            }
            userService.updateUser(existingUser);
        }

        if (boardId != null) {
            return "redirect:/user-management/users?boardId=" + boardId;
        } else {
            return "redirect:/user-management/users";
        }
    }

    @DeleteMapping("/delete/user/{userIndex}")
    public String deleteUser(@PathVariable Integer userIndex, @RequestParam(required = false) Long boardId) {
        userService.deleteUserByUserIndex(userIndex);

        if (boardId != null) {
            return "redirect:/user-management/users?boardId=" + boardId;
        } else {
            return "redirect:/user-management/users";
        }
    }
}
