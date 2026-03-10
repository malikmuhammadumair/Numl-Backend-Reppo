package cofee.example.cofee.Repository;

import cofee.example.cofee.Entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    // Optional: you can add findByCode or other queries later
    List<Course> findByProgram_NameAndSemester(String programName, String semester);

    Optional<Course> findByCode(String code);

}