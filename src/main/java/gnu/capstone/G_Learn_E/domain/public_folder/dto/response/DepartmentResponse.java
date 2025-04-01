package gnu.capstone.G_Learn_E.domain.public_folder.dto.response;

import gnu.capstone.G_Learn_E.domain.public_folder.entity.Department;


public record DepartmentResponse(Long id, String departmentName) {

    // 정적 팩토리 메서드: Department 엔티티를 DepartmentResponse로 변환
    public static DepartmentResponse from(Department department) {
        return new DepartmentResponse(department.getId(), department.getName());
    }
}