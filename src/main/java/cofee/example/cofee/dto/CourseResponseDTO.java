package cofee.example.cofee.dto;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CourseResponseDTO {

    private Long id;
    private String name;
    private String code;
    private int creditHours;
    private String description;

    private Long programId;
    private String programName;
}
