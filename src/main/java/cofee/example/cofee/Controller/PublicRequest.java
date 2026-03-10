package cofee.example.cofee.Controller;

import cofee.example.cofee.Entity.*;
import cofee.example.cofee.Services.*;
import cofee.example.cofee.dto.CourseResponseDTO;
import cofee.example.cofee.dto.DepartmentDTO;
import cofee.example.cofee.dto.FilterRequest;
import cofee.example.cofee.dto.ProgramDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/public")
@RestController
@CrossOrigin(origins = "https://www.studyhouse.online")
public class PublicRequest {
    @Autowired
    private UserService userService;
    @Autowired
    private AddDepartmentService service;
    @Autowired
    private JournelServices journelServices;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AddProgramService addProgramService;
    @Autowired
    private CourseService courseService;

    @PostMapping("/post")
    public ResponseEntity<UserEntity> postall(@RequestBody UserEntity coffeeEntity) {
        try {
            UserEntity coffeeEntity1 = userService.PostnewAll(coffeeEntity);
            return ResponseEntity.ok(coffeeEntity1);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/check")
    public ResponseEntity<?> CheckInfo(@RequestBody UserEntity userEntity) {
        // Find user by email
        UserEntity user = userService.findbyusername(userEntity.getEmail());

        if (user == null) {
            // User not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } else if (passwordEncoder.matches(userEntity.getPassword(), user.getPassword())) {
            // Password matches, return roles
            return ResponseEntity.ok(user.getRoles());
        } else {
            // Password mismatch
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
        }
    }

    @GetMapping("/recent")
    public ResponseEntity<?> getRecentUrls() {
        List<JournelEntries> all = journelServices.getRecent6();

        if (all != null && !all.isEmpty()) {
            // last 6 entries
            List<JournelEntries> lastSix = all.stream()
                    .sorted((a, b) -> b.getId().compareTo(a.getId())) // latest first
                    .limit(6)
                    .toList();

            return new ResponseEntity<>(lastSix, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //    Front page trending corse heart logic
    @PutMapping("/trending/{id}")
    public ResponseEntity<JournelEntries> updatetrend(
            @PathVariable Long id,
            @RequestParam boolean heart) {

        JournelEntries entry = journelServices.findJournalById(id);

        if (entry == null) {
            return ResponseEntity.notFound().build();
        }

        entry.setTrending(heart); // "true" ya "false"
        JournelEntries updated = journelServices.saveJournal(entry);

        return ResponseEntity.ok(updated);
    }

    // get trending papers
    @GetMapping("/trending")
    public ResponseEntity<List<JournelEntries>> getTrendingPapers() {
        List<JournelEntries> all = journelServices.getTrending6();

        if (all != null && !all.isEmpty()) {
            // filter trending + latest 6 entries
            List<JournelEntries> lastSix = all.stream()

                    .sorted((a, b) -> b.getId().compareTo(a.getId())) // latest first
                    .limit(6)
                    .toList();

            return new ResponseEntity<>(lastSix, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // GET ALL
    @GetMapping("/department")
    public List<DepartmentDTO> getAllDepartments() {
        return service.getAllDepartments();
    }

    @GetMapping("/department/{id}")
    public DepartmentDTO getDepartmentById(@PathVariable Long id) {
        return service.getDepartmentById(id);
    }

    @GetMapping("/department/name/{name}")
    public AddDepartment getDepartmentByName(@PathVariable String name) {
        return service.getDepartmentByName(name);
    }

    @GetMapping("/program/name/{name}")
    public Program getProgrambyname(@PathVariable String name) {
        return addProgramService.getProgramByName(name);
    }



    @GetMapping("/program")
    public ResponseEntity<List<ProgramDTO>> getAllPrograms() {
        return ResponseEntity.ok(addProgramService.getAllPrograms());
    }

    //get programm bi id
    @GetMapping("/program/{id}")
    public ProgramDTO getProgramById(@PathVariable Long id) {
        return addProgramService.getProgramById(id);
    }

    // GET ALL courses


    @GetMapping("/course")
    public ResponseEntity<List<CourseResponseDTO>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

//    get course by id
@GetMapping("/course/{id}")
public Course getCourseById(@PathVariable Long id) {
    return courseService.getCourseById(id);
}
    // Filter entries
    @PostMapping("/filter")
    public ResponseEntity<List<JournelEntries>> filter(@RequestBody FilterRequest req) {
        return ResponseEntity.ok(journelServices.filter(req));
    }
//    get courses by program and semester
@GetMapping("/program/{programName}/semester/{semester}")
public List<Course> getCoursesByProgramAndSemester(
        @PathVariable String programName,
        @PathVariable String semester
) {

    return courseService.getCoursesByProgramAndSemester(programName, semester);

}
}
