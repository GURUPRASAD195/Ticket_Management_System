//package com.example.Ticket_Management_System.Controllers;
//
//import java.util.List;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import com.example.Ticket_Management_System.entity.Role;
//import com.example.Ticket_Management_System.service.Role;
//
//@RestController
//@RequestMapping("/api/roles")
//public class RoleController {
//
//    private final Role roleService;
//
//    public RoleController(Role roleService) {
//        this.roleService = roleService;
//    }
//
//    @PreAuthorize("hasRole('ADMIN')")
//    @PostMapping
//    public ResponseEntity<Role> createRole(@RequestBody Role role) {
//        Role savedRole = roleService.createRole(role);
//        return new ResponseEntity<>(savedRole, HttpStatus.CREATED);
//    }
//
//    @PreAuthorize("hasRole('ADMIN')")
//    @GetMapping
//    public ResponseEntity<List<Role>> getAllRoles() {
//        return ResponseEntity.ok(roleService.getAllRoles());
//    }
//
//    @PreAuthorize("hasRole('ADMIN')")
//    @GetMapping("/{id}")
//    public ResponseEntity<Role> getRoleById(@PathVariable Long id) {
//        return ResponseEntity.ok(roleService.getRoleById(id));
//    }
//
//    @PreAuthorize("hasRole('ADMIN')")
//    @PutMapping("/{id}")
//    public ResponseEntity<Role> updateRole(
//            @PathVariable Long id,
//            @RequestBody Role role) {
//        return ResponseEntity.ok(roleService.updateRole(id, role));
//    }
//
//    @PreAuthorize("hasRole('ADMIN')")
//    @DeleteMapping("/{id}")
//    public ResponseEntity<String> deleteRole(@PathVariable Long id) {
//        roleService.deleteRole(id);
//        return ResponseEntity.ok("Role deleted successfully");
//    }
//}
