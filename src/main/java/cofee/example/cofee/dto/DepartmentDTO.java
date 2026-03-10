package cofee.example.cofee.dto;



import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDTO {

    private Long id;
    private String departmentname;
    private String slug;
    private String description;

    private List<ProgramDTO> programs;

}