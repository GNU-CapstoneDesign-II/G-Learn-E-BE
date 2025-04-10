package gnu.capstone.G_Learn_E.domain.public_folder.dto.response;

import gnu.capstone.G_Learn_E.domain.public_folder.entity.Subject;

public record SubjectResponse(Long id, String subjectName, String grade) {

    public static SubjectResponse from(Subject subject) {
        return new SubjectResponse(subject.getId(), subject.getName(), subject.getGrade().getGrade());
    }
}

