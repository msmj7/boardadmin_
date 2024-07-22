package com.boardadmin.user.controller;

import com.boardadmin.board.model.Board;
import com.boardadmin.board.service.BoardService;
import com.boardadmin.user.model.Role;
import com.boardadmin.user.model.User;
import com.boardadmin.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.domain.Page;

@Controller
@RequestMapping
public class UserController {

    protected final UserService userService;
    protected final BoardService boardService;

    public UserController(UserService userService, BoardService boardService) {
        this.userService = userService;
        this.boardService = boardService;
    }

    @GetMapping("/admin/admins")
    public String getAdminsPage(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(required = false) Long boardId, @RequestParam(required = false) String search, Model model) {
        Page<User> adminPage = userService.getAdminsPage(page, size, search);

        int totalPages = adminPage.getTotalPages() > 0 ? adminPage.getTotalPages() : 1;

        if (boardId != null) {
            Board board = boardService.getBoardById(boardId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid board Id:" + boardId));
            model.addAttribute("board", board);
        }

        model.addAttribute("boards", boardService.getAllBoards());
        model.addAttribute("admins", adminPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("search", search);
        model.addAttribute("pageSize", size);

        return "admin/admins";
    }

    @GetMapping("/admin/users")
    public String getUsersPage(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(required = false) Long boardId, @RequestParam(required = false) String search, Model model) {
        Page<User> userPage = userService.getUsersPage(page, size, search);

        int totalPages = userPage.getTotalPages() > 0 ? userPage.getTotalPages() : 1;

        if (boardId != null) {
            Board board = boardService.getBoardById(boardId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid board Id:" + boardId));
            model.addAttribute("board", board);
        }

        model.addAttribute("boards", boardService.getAllBoards());
        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("search", search);
        model.addAttribute("pageSize", size);

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
        return "admin/adminDetail";
    }

    @GetMapping("/admin/users/{userIndex}")
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

    @GetMapping("/admin/newAdmin")
    public String getAdminCreatePage(@RequestParam(required = false) Long boardId, Model model) {
        model.addAttribute("user", new User());

        if (boardId != null) {
            Board board = boardService.getBoardById(boardId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid board Id:" + boardId));
            model.addAttribute("board", board);
        }

        model.addAttribute("boards", boardService.getAllBoards());
        return "admin/newAdmin";
    }

    @GetMapping("/admin/newUser")
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

    @PostMapping("/admin/create/admin")
    public String createAdmin(@ModelAttribute User user, Model model) {
        if (userService.userExists(user.getUserId())) {
            model.addAttribute("error", "이미 존재하는 아이디입니다.");
            user.setUserId(""); // 사용자 ID 필드를 비웁니다.
            model.addAttribute("user", user); // 나머지 입력값을 유지합니다.
            return "admin/newAdmin";
        }
        Set<Role> roles = new HashSet<>();
        roles.add(userService.getRoleByName("ADMIN"));
        user.setRoles(roles);
        userService.saveUser(user);
        return "redirect:/admin/admins";
    }

    @PostMapping("/admin/create/user")
    public String createUser(@ModelAttribute User user, Model model) {
        if (userService.userExists(user.getUserId())) {
            model.addAttribute("error", "이미 존재하는 아이디입니다.");
            user.setUserId("");
            model.addAttribute("user", user);
            return "useradmin/newUser";
        }

        Set<Role> roles = new HashSet<>();
        roles.add(userService.getRoleByName("USER"));
        user.setRoles(roles);
        userService.saveUser(user);
        return "redirect:/admin/users";
    }

    @GetMapping("/admin/updatepage/admin/{userIndex}")
    public String getAdminEditPage(@PathVariable Integer userIndex, Model model) {
        User admin = userService.getUserByUserIndex(userIndex);
        model.addAttribute("admin", admin);
        return "admin/adminEdit";
    }

    @PostMapping("/admin/update/admin/{userIndex}")
    public String updateAdmin(@PathVariable Integer userIndex, @ModelAttribute User user, @RequestParam(required = false) Long boardId, Model model) {
        if (!userService.updateUser(userIndex, user)) {
            model.addAttribute("error", "이미 존재하는 아이디입니다.");
            model.addAttribute("admin", user);
            return "admin/adminEdit";
        }
        if (boardId != null) {
            return "redirect:/admin?boardId=" + boardId;
        } else {
            return "redirect:/admin/admins";
        }
    }

    @PostMapping("/admin/delete/admin/{userIndex}")
    public String deleteAdmin(@PathVariable Integer userIndex) {
        userService.deleteUserByUserIndex(userIndex);
        return "redirect:/admin/admins";
    }

    @GetMapping("/admin/updatepage/user/{userIndex}")
    public String getUserEditPage(@PathVariable Integer userIndex, Model model) {
        User user = userService.getUserByUserIndex(userIndex);
        model.addAttribute("user", user);
        return "useradmin/userEdit";
    }

    @PostMapping("/admin/update/user/{userIndex}")
    public String updateUser(@PathVariable Integer userIndex, @ModelAttribute User user, @RequestParam(required = false) Long boardId, Model model) {
        if (!userService.updateUser(userIndex, user)) {
            model.addAttribute("error", "이미 존재하는 아이디입니다.");
            model.addAttribute("user", user);
            return "useradmin/userEdit";
        }
        if (boardId != null) {
            return "redirect:/admin/users?boardId=" + boardId;
        } else {
            return "redirect:/admin/users";
        }
    }

    @PostMapping("/admin/delete/user/{userIndex}")
    public String deleteUser(@PathVariable Integer userIndex) {
        userService.deleteUserByUserIndex(userIndex);
        return "redirect:/admin/users";
    }
}
