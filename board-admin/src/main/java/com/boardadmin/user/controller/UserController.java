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
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
    public String getAdminsPage(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(required = false) Long boardId, @RequestParam(required = false) String search ,Model model) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<User> adminPage = userService.getUsersByRole("ADMIN", pageable);

        if (search != null && !search.isEmpty()) {
        	adminPage = userService.searchAdmins(search, pageable);
        } else {
        	adminPage = userService.getUsersByRole("ADMIN", pageable);
        }
        
        int totalPages = adminPage.getTotalPages() > 0 ? adminPage.getTotalPages() : 1; // 최소 1페이지를 유지

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

        return "useradmin/admins";
    }

    @GetMapping("/users")
    public String getUsersPage(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(required = false) Long boardId, @RequestParam(required = false) String search, Model model) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<User> userPage = userService.getUsersByRole("USER", pageable);

        if (search != null && !search.isEmpty()) {
        	userPage = userService.searchUsers(search, pageable);
        } else {
        	userPage = userService.getUsersByRole("USER", pageable);
        }
        
        
        int totalPages = userPage.getTotalPages() > 0 ? userPage.getTotalPages() : 1; // 최소 1페이지를 유지

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
            model.addAttribute("error", "이미 존재하는 아이디입니다.");
            user.setUserId(""); // 사용자 ID 필드를 비웁니다.
            model.addAttribute("user", user); // 나머지 입력값을 유지합니다.
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
            model.addAttribute("error", "이미 존재하는 아이디입니다.");
            user.setUserId("");
            model.addAttribute("user", user);
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
            // Check if the userId already exists and it's not the same as the current user's userId
            if (!existingUser.getUserId().equals(user.getUserId()) && userService.userExists(user.getUserId())) {
                model.addAttribute("error", "이미 존재하는 아이디입니다.");
                existingUser.setUserId("");
                model.addAttribute("admin", user); 
                return "useradmin/adminEdit";
            } else {
                existingUser.setUserId(user.getUserId());
                existingUser.setEmail(user.getEmail());
                existingUser.setActive(user.isActive());
            }

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

    @PostMapping("/delete/admin/{userIndex}")
    public String deleteAdmin(@PathVariable Integer userIndex) {
        userService.deleteUserByUserIndex(userIndex);
        return "redirect:/user-management";
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
            if (!existingUser.getUserId().equals(user.getUserId()) && userService.userExists(user.getUserId())) {
                model.addAttribute("error", "이미 존재하는 아이디입니다.");
                existingUser.setUserId("");
                model.addAttribute("user", user); 
                return "useradmin/userEdit";
            } else {
                existingUser.setUserId(user.getUserId());
                existingUser.setEmail(user.getEmail());
                existingUser.setActive(user.isActive());
            }
            
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

    @PostMapping("/delete/user/{userIndex}")
    public String deleteUser(@PathVariable Integer userIndex) {
        userService.deleteUserByUserIndex(userIndex);
        return "redirect:/user-management/users";
    }
}
