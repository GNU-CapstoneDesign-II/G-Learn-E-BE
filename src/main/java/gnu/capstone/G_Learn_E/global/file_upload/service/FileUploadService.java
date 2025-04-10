package gnu.capstone.G_Learn_E.global.file_upload.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import gnu.capstone.G_Learn_E.domain.public_folder.entity.College;
import gnu.capstone.G_Learn_E.domain.public_folder.entity.Department;
import gnu.capstone.G_Learn_E.domain.public_folder.entity.Subject;
import gnu.capstone.G_Learn_E.domain.public_folder.enums.SubjectGrade;
import gnu.capstone.G_Learn_E.domain.public_folder.repository.CollegeRepository;
import gnu.capstone.G_Learn_E.domain.public_folder.repository.DepartmentRepository;
import gnu.capstone.G_Learn_E.domain.public_folder.repository.SubjectRepository;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileUploadService {
    private final CollegeRepository collegeRepository;
    private final DepartmentRepository departmentRepository;
    private final SubjectRepository subjectRepository;


    @Async // 비동기로 실행
    public void processUploadedFile(Path filePath) {
        log.info("파일 처리 시작: {}", filePath);

        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream is = Files.newInputStream(filePath);
             JsonParser parser = objectMapper.getFactory().createParser(is)) {

            // 최상위는 { ... } 객체라고 가정
            if (parser.nextToken() == JsonToken.START_OBJECT) {
                // 내부 필드를 순회
                while (parser.nextToken() != JsonToken.END_OBJECT) {
                    String fieldName = parser.getCurrentName();
                    // "colleges" 배열을 만나면
                    if ("colleges".equals(fieldName)) {
                        parser.nextToken(); // START_ARRAY ( [ )

                        // colleges 배열 파싱
                        while (parser.nextToken() == JsonToken.START_OBJECT) {
                            // 한 'CollegeDTO'만큼 파싱
                            CollegeDTO collegeDto = objectMapper.readValue(parser, CollegeDTO.class);

                            // 이제 이 DTO만 DB에 저장 (메모리에 많이 쌓이지 않음)
                            saveCollegeAndChildren(collegeDto);
                        }
                        // colleges 배열 끝나면 ], 다음 token으로 이동
                    } else {
                        // 혹시 다른 필드가 있으면 건너뛰기
                        parser.skipChildren();
                    }
                }
            }
            log.info("파일 처리 완료: {}", filePath);
        } catch (IOException e) {
            log.error("파일 처리 중 오류: {}", e.getMessage(), e);
        } finally {
            // 처리 후 파일 삭제
            try {
                Files.deleteIfExists(filePath);
                log.info("임시 파일 삭제: {}", filePath);
            } catch (IOException ex) {
                log.error("파일 삭제 중 오류: {}", ex.getMessage(), ex);
            }
        }
    }

    // 하나의 CollegeDTO(및 하위 departments, subjects)를 DB에 반영
    @Transactional // 부분 트랜잭션
    public void saveCollegeAndChildren(CollegeDTO collegeDto) {
        // [1] College 매핑/조회
        College college = collegeRepository.findByName(collegeDto.getName())
                .orElseGet(() -> College.builder()
                        .name(collegeDto.getName())
                        .build());

        // 아래처럼 필요시 업데이트 가능 (ex: college.setDescription(...))

        // [2] Department 매핑
        if (collegeDto.getDepartments() != null) {
            for (DepartmentDTO deptDto : collegeDto.getDepartments()) {
                Department department = departmentRepository
                        .findByNameAndCollegeId(deptDto.getName(), college.getId())
                        .orElseGet(() -> {
                            Department build = Department.builder()
                                    .name(deptDto.getName())
                                    .college(college)
                                    .build();
                            college.getDepartments().add(build);
                            return build;
                        });

                // [3] Subject 매핑
                if (deptDto.getSubjects() != null) {
                    for (SubjectDTO subjDto : deptDto.getSubjects()) {
                        // 이미 존재하면 스킵
                        if (subjectRepository.existsByNameAndGradeAndDepartmentId(subjDto.getName(), getSubjectGrade(subjDto), department.getId())) {
                            continue;
                        }
                        SubjectGrade grade = getSubjectGrade(subjDto);
                        Subject subject = Subject.builder()
                                .name(subjDto.getName())
                                .department(department)
                                .grade(grade)
                                .build();

                        // department.getSubjects()에 subject와 name, grade가 같은 항목이 없으면 저장
                        boolean duplicated = department.getSubjects().stream()
                                .anyMatch(s -> s.getName().equals(subject.getName()) && s.getGrade() == subject.getGrade());
                        if(!duplicated) {
                            department.getSubjects().add(subject);
                        }
                    }
                }
            }
        }

        // 최종 저장
        collegeRepository.save(college);
        // Cascade.ALL 설정에 따라 Department / Subject도 함께 반영 가능
    }

    private static SubjectGrade getSubjectGrade(SubjectDTO subjDto) {
        SubjectGrade grade = SubjectGrade.NO_GRADE_DISTINCT;
        switch (subjDto.getGrade()) {
            case 1 -> grade = SubjectGrade.FIRST_YEAR;
            case 2 -> grade = SubjectGrade.SECOND_YEAR;
            case 3 -> grade = SubjectGrade.THIRD_YEAR;
            case 4 -> grade = SubjectGrade.FOURTH_YEAR;
            case 5 -> grade = SubjectGrade.FIFTH_YEAR;
            case 6 -> grade = SubjectGrade.SIXTH_YEAR;
        }
        return grade;
    }


    @Getter
    @Setter
    @NoArgsConstructor
    public static class RootDTO {
        private List<CollegeDTO> colleges;
    }

    @Getter @Setter
    @NoArgsConstructor
    public static class CollegeDTO {
        private String name;
        private List<DepartmentDTO> departments;
    }

    @Getter @Setter
    @NoArgsConstructor
    public static class DepartmentDTO {
        private String name;
        private List<SubjectDTO> subjects;
    }

    @Getter @Setter
    @NoArgsConstructor
    public static class SubjectDTO {
        private String name;
        private Integer grade;
    }
}
