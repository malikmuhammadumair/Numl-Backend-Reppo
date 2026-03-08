package cofee.example.cofee.Services;

import cofee.example.cofee.Entity.AddDepartment;
import cofee.example.cofee.Repository.AddDepartmentRepository;
import cofee.example.cofee.Repository.ProgramRepository;
import cofee.example.cofee.dto.DepartmentDTO;
import cofee.example.cofee.dto.ProgramDTO;
import cofee.example.cofee.dto.CourseResponseDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddDepartmentService {

    @Autowired
    private AddDepartmentRepository repository;

    // ADD Department
    public AddDepartment addDepartment(AddDepartment dept) {
        return repository.save(dept);
    }

    // GET All Departments
    public List<DepartmentDTO> getAllDepartments() {
        return repository.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    // GET Department By ID
    public DepartmentDTO getDepartmentById(Long id) {

        AddDepartment dept = repository.findById(id).orElse(null);

        if (dept == null) {
            return null;
        }

        return convertToDTO(dept);
    }

    public AddDepartment getDepartmentByName(String name){
        return repository.findByDepartmentname(name);
    }

    // UPDATE Department
    public AddDepartment updateDepartment(Long id, AddDepartment updatedDept) {

        return repository.findById(id).map(dept -> {

            dept.setDepartmentname(updatedDept.getDepartmentname());
            dept.setSlug(updatedDept.getSlug());
            dept.setDescription(updatedDept.getDescription());

            return repository.save(dept);

        }).orElse(null);
    }

    // DELETE Department
    public String deleteDepartment(Long id) {

        if (repository.existsById(id)) {

            repository.deleteById(id);

            return "Department deleted successfully";
        }

        return "Department not found";
    }

    // DTO MAPPER
    private DepartmentDTO convertToDTO(AddDepartment dept) {

        DepartmentDTO dto = new DepartmentDTO();

        dto.setId(dept.getId());
        dto.setDepartmentname(dept.getDepartmentname());
        dto.setSlug(dept.getSlug());
        dto.setDescription(dept.getDescription());

        if (dept.getPrograms() != null) {

            dto.setPrograms(
                    dept.getPrograms()
                            .stream()
                            .map(program -> {

                                ProgramDTO p = new ProgramDTO();

                                p.setId(program.getId());
                                p.setName(program.getName());
                                p.setDuration(program.getDuration());
                                p.setSemester(program.getSemester());
                                p.setDescription(program.getDescription());

                                p.setDepartmentId(dept.getId());
                                p.setDepartmentname(dept.getDepartmentname());

                                // Courses Mapping
                                if (program.getCourses() != null) {

                                    p.setCourses(
                                            program.getCourses()
                                                    .stream()
                                                    .map(course -> {

                                                        CourseResponseDTO c = new CourseResponseDTO();

                                                        c.setId(course.getId());
                                                        c.setName(course.getName());
                                                        c.setCode(course.getCode());
                                                        c.setCreditHours(course.getCreditHours());
                                                        c.setDescription(course.getDescription());

                                                        return c;

                                                    })
                                                    .toList()
                                    );
                                }

                                return p;

                            })
                            .toList()
            );
        }

        return dto;
    }

}