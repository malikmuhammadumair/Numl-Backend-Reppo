package cofee.example.cofee.Services;

import cofee.example.cofee.Entity.Course;
import cofee.example.cofee.Entity.Program;
import cofee.example.cofee.Repository.CourseRepository;
import cofee.example.cofee.Repository.ProgramRepository;
import cofee.example.cofee.dto.CourseResponseDTO;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final ProgramRepository programRepository;

    public CourseService(CourseRepository courseRepository,
                         ProgramRepository programRepository) {
        this.courseRepository = courseRepository;
        this.programRepository = programRepository;
    }

    // CREATE course
    public Course createCourse(Long programId, Course course){

        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new RuntimeException("Program not found"));

        course.setProgram(program);

        return courseRepository.save(course);
    }

    // GET all courses (DTO version)
    public List<CourseResponseDTO> getAllCourses() {

        List<Course> courses = courseRepository.findAll();

        return courses.stream().map(course -> {

            CourseResponseDTO dto = new CourseResponseDTO();

            dto.setId(course.getId());
            dto.setName(course.getName());
            dto.setCode(course.getCode());
            dto.setCreditHours(course.getCreditHours());
            dto.setDescription(course.getDescription());

            if (course.getProgram() != null) {
                dto.setProgramId(course.getProgram().getId());
                dto.setProgramName(course.getProgram().getName());
            }

            return dto;

        }).collect(Collectors.toList());
    }

    // UPDATE course
    public Course updateCourse(Long id, Course updatedCourse) {
        Optional<Course> optional = courseRepository.findById(id);

        if (optional.isPresent()) {

            Course course = optional.get();

            course.setName(updatedCourse.getName());
            course.setCode(updatedCourse.getCode());
            course.setCreditHours(updatedCourse.getCreditHours());
            course.setDescription(updatedCourse.getDescription());

            return courseRepository.save(course);
        }

        return null;
    }

    // DELETE course
    public String deleteCourse(Long id) {

        if (courseRepository.existsById(id)) {
            courseRepository.deleteById(id);
            return "Course deleted successfully";
        }

        return "Course not found";
    }

    public Course getCourseById(Long id) {
        return courseRepository.findById(id).orElse(null);
    }
}