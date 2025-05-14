package gnu.capstone.G_Learn_E.global.common.dto.response;

import gnu.capstone.G_Learn_E.domain.public_folder.entity.College;
import gnu.capstone.G_Learn_E.domain.public_folder.entity.Department;
import gnu.capstone.G_Learn_E.domain.public_folder.entity.Subject;

public record PublicPath(
        Long collegeId,
        String collegeName,
        Long departmentId,
        String departmentName,
        Long subjectId,
        String subjectName
) {

    public static PublicPath of(
            Long collegeId,
            String collegeName,
            Long departmentId,
            String departmentName,
            Long subjectId,
            String subjectName
    ) {
        return new PublicPath(
                collegeId,
                collegeName,
                departmentId,
                departmentName,
                subjectId,
                subjectName
        );
    }

    public static PublicPath from(
            College college,
            Department department,
            Subject subject
    ) {
        return new PublicPath(
                college.getId(),
                college.getName(),
                department.getId(),
                department.getName(),
                subject.getId(),
                subject.getName()
        );
    }
}
