package cofee.example.cofee.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProgramDTO {

    private Long id;
    private String name;

    private Long departmentId;
    private String departmentname;

    private String duration;
    private String semester;
    private String description;

    private List<CourseResponseDTO> courses;
}