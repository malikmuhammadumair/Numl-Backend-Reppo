package cofee.example.cofee.Services;

import cofee.example.cofee.Entity.AddDepartment;
import cofee.example.cofee.Entity.Program;
import cofee.example.cofee.Repository.AddDepartmentRepository;
import cofee.example.cofee.Repository.ProgramRepository;
import cofee.example.cofee.dto.CourseResponseDTO;
import cofee.example.cofee.dto.ProgramDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AddProgramService {

    @Autowired
    private ProgramRepository programRepository;

    @Autowired
    private AddDepartmentRepository departmentRepository;

    // CREATE Program
    public ProgramDTO createProgram(Long departmentId, Program program){

        AddDepartment department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        program.setDepartment(department);

        Program savedProgram = programRepository.save(program);

        return convertToDTO(savedProgram);
    }

    // GET All Programs
    public List<ProgramDTO> getAllPrograms() {

        return programRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // UPDATE Program
    public ProgramDTO updateProgram(Long id, Program updatedProgram) {

        Optional<Program> optional = programRepository.findById(id);

        if (optional.isPresent()) {

            Program program = optional.get();

            program.setName(updatedProgram.getName());
            program.setDuration(updatedProgram.getDuration());
            program.setSemester(updatedProgram.getSemester());
            program.setDescription(updatedProgram.getDescription());

            if(updatedProgram.getDepartment() != null){

                Long deptId = updatedProgram.getDepartment().getId();

                AddDepartment department = departmentRepository.findById(deptId)
                        .orElseThrow(() -> new RuntimeException("Department not found"));

                program.setDepartment(department);
            }

            Program saved = programRepository.save(program);

            return convertToDTO(saved);
        }

        return null;
    }

    // DELETE Program
    public String deleteProgram(Long id) {

        if (programRepository.existsById(id)) {

            programRepository.deleteById(id);

            return "Program deleted successfully";
        }

        return "Program not found";
    }

    // GET Program By ID
    public ProgramDTO getProgramById(Long id) {

        Program program = programRepository.findById(id).orElse(null);

        if(program == null){
            return null;
        }

        return convertToDTO(program);
    }

    // DTO MAPPER
    private ProgramDTO convertToDTO(Program program){

        ProgramDTO dto = new ProgramDTO();

        dto.setId(program.getId());
        dto.setName(program.getName());

        // ✅ null safe department mapping
        if(program.getDepartment() != null){
            dto.setDepartmentId(program.getDepartment().getId());
            dto.setDepartmentname(program.getDepartment().getDepartmentname());
        }

        dto.setDuration(program.getDuration());
        dto.setSemester(program.getSemester());
        dto.setDescription(program.getDescription());

        // courses mapping
        if(program.getCourses() != null){

            dto.setCourses(
                    program.getCourses()
                            .stream()
                            .map(course -> {
                                CourseResponseDTO c = new CourseResponseDTO();

                                c.setId(course.getId());
                                c.setName(course.getName());
                                c.setCode(course.getCode());
                                c.setCreditHours(course.getCreditHours());
                                c.setDescription(course.getDescription());

                                if(course.getProgram() != null){
                                    c.setProgramId(course.getProgram().getId());
                                    c.setProgramName(course.getProgram().getName());
                                }

                                return c;
                            })
                            .toList()
            );

        }

        return dto;
    }}