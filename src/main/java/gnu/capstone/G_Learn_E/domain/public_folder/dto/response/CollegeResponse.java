package gnu.capstone.G_Learn_E.domain.public_folder.dto.response;

import gnu.capstone.G_Learn_E.domain.public_folder.entity.College;

public record CollegeResponse(
        Long id,
        String collegeName
) {
    public static CollegeResponse from(College college) {
        return new CollegeResponse(college.getId(), college.getName());
    }
}
