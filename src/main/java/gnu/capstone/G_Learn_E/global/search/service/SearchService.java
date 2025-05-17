package gnu.capstone.G_Learn_E.global.search.service;

import gnu.capstone.G_Learn_E.domain.problem.entity.Problem;
import gnu.capstone.G_Learn_E.domain.problem.entity.ProblemWorkbookMap;
import gnu.capstone.G_Learn_E.domain.user.entity.User;
import gnu.capstone.G_Learn_E.domain.workbook.entity.Workbook;
import gnu.capstone.G_Learn_E.domain.workbook.repository.WorkbookRepository;
import gnu.capstone.G_Learn_E.global.common.dto.response.PageInfo;
import gnu.capstone.G_Learn_E.global.common.dto.serviceToController.WorkbookPaginationResult;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

    private final WorkbookRepository workbookRepository;

    /* 허용 파라미터 */
    private static final Set<String> ALLOWED_RANGE = Set.of("private", "public");
    private static final Set<String> ALLOWED_TYPE  = Set.of("total", "title", "author", "content");
    private static final Set<String> ALLOWED_SORT  = Set.of("createdAt", "title", "author", "relevance");

    /**
     * 워크북 검색
     *
     * @param keyword  검색어
     * @param range    공개 범위: all | private | public
     * @param type     검색 대상: total | title | author | content
     * @param page     0‑base
     * @param size     페이지 크기
     * @param sort     정렬 키: relevance | createdAt | title | author
     * @param order    asc | desc
     *
     * @return  검색 결과 목록 (페이지네이션 반영)
     */
    public WorkbookPaginationResult searchWorkbook(
            User user,
            String keyword,
            String range,
            String type,
            int page,
            int size,
            String sort,
            String order
    ) {
        /* 1) 파라미터 정합성 보정 */
        validateAllParameter(keyword, range, type, page, size, sort, order);

        if (sort.equals("relevance")) {
            return searchByRelevance(user, keyword, range, page, size);
        }

        Pageable pageable = getPageable(page, size, sort, order);

        /* 2) 동적 Specification 생성 */
        Specification<Workbook> spec = Specification.where(buildRangeSpec(range, user.getId()))
                .and(buildKeywordSpec(type, keyword));


        /* 3) 검색 실행 (distinct 필수!) */
        Page<Workbook> result = workbookRepository.findAll(spec, pageable);
        List<Workbook> workbooks = result.getContent();
        PageInfo pageInfo = PageInfo.of(
                result.getTotalElements(),
                result.getTotalPages(),
                page,
                result.hasNext(),
                result.hasPrevious()
        );
        return WorkbookPaginationResult.from(pageInfo, workbooks);
    }

    /*───────────────────────────────────────────────────────────────────────*/
    /* 내부 로직                                                             */
    /*───────────────────────────────────────────────────────────────────────*/

    private void validateAllParameter(
            String keyword,
            String range,
            String type,
            int page,
            int size,
            String sort,
            String order
    ) {
        if (keyword == null || keyword.isBlank()) {
            throw new IllegalArgumentException("Keyword cannot be null or empty");
        }
        if (!ALLOWED_RANGE.contains(range)) {
            throw new IllegalArgumentException("Invalid range: " + range);
        }
        if (!ALLOWED_TYPE.contains(type)) {
            throw new IllegalArgumentException("Invalid type: " + type);
        }
        if (!ALLOWED_SORT.contains(sort)) {
            throw new IllegalArgumentException("Invalid sort: " + sort);
        }
        if (!"asc".equalsIgnoreCase(order) && !"desc".equalsIgnoreCase(order)) {
            throw new IllegalArgumentException("Invalid order: " + order);
        }
        if(sort.equals("relevance")) {
            if(!type.equals("total")) {
                throw new IllegalArgumentException("Relevance sort is only allowed with total type");
            }
            if(!order.equals("desc")) {
                throw new IllegalArgumentException("Relevance sort is only allowed in descending order");
            }
        }
        if (page < 0) {
            throw new IllegalArgumentException("Page number cannot be negative");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Page size must be greater than zero");
        }
        if (size > 100) {
            throw new IllegalArgumentException("Page size cannot exceed 100");
        }
    }

    private WorkbookPaginationResult searchByRelevance(
            User user,
            String keyword,
            String range,
            int page,
            int size
    ) {
        Page<Workbook> workbooks = workbookRepository.searchByRelevance(
                keyword + '*',        // 접두사 검색
                range,
                user.getId(),
                PageRequest.of(page, size)   // native 쿼리 내부에서 score DESC 정렬
        );
        List<Long> ids = workbooks.getContent().stream()
                .map(Workbook::getId)
                .toList();
        PageInfo pageInfo = PageInfo.of(
                workbooks.getTotalElements(),
                workbooks.getTotalPages(),
                page,
                workbooks.hasNext(),
                workbooks.hasPrevious()
        );

        List<Workbook> results = workbookRepository.findAllById(ids);
        return WorkbookPaginationResult.from(pageInfo, results);
    }


    /**
     * 공개 범위에 따른 조건 생성
     * - public: 공개된 문제집
     * - private: 비공개 문제집
     * - all: 모든 문제집
     */
    private Specification<Workbook> buildRangeSpec(String range, Long userId) {
        return switch (range) {
            case "public" -> (r, q, cb) ->
                    cb.isNotEmpty(r.get("subjectWorkbookMaps"));              // 공개 폴더에 속함

            case "private" -> (r, q, cb) -> cb.and(
                    cb.isEmpty(r.get("subjectWorkbookMaps")),                 // 공개 폴더 X
                    cb.equal(r.get("author").get("id"), userId)               // ← 내 문제집만
            );

            default -> null;                                              // all
        };
    }

    /** type 에 맞는 키워드 조건 */
    private Specification<Workbook> buildKeywordSpec(String type, String kwLower) {
        return switch (type) {
            case "title"   -> titleContains(kwLower);
            case "author"  -> authorContains(kwLower);
            case "content" -> contentContains(kwLower);
            case "total"   -> Specification
                    .where(titleContains(kwLower))
                    .or(authorContains(kwLower))
                    .or(contentContains(kwLower));
            default        -> null;
        };
    }

    /* —————————————————————————————————————————————— 단일 조건들 ———— */

    private Specification<Workbook> titleContains(String kwLower) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("name")), "%" + kwLower + "%");
    }

    private Specification<Workbook> authorContains(String kwLower) {
        return (root, query, cb) -> {
            Join<Object, Object> author = root.join("author", JoinType.LEFT);
            return cb.like(cb.lower(author.get("nickname")), "%" + kwLower + "%");
        };
    }

    private Specification<Workbook> contentContains(String kwLower) {
        return (root, query, cb) -> {
            query.distinct(true);

            Subquery<Long> sub = query.subquery(Long.class);
            Root<Problem> p = sub.from(Problem.class);
            Join<Problem, ProblemWorkbookMap> pwm =
                    p.join("problemWorkbookMaps", JoinType.INNER);

            Predicate sameWorkbook = cb.equal(pwm.get("workbook"), root);
            String like = "%" + kwLower + "%";

            Predicate titleLike   = cb.like(cb.lower(p.get("title")),       like);
            Predicate explLike    = cb.like(cb.lower(p.get("explanation")), like);
            Predicate optionsLike = cb.like(cb.lower(p.get("options")),     like);
            Predicate answersLike = cb.like(cb.lower(p.get("answers")),     like);

            sub.select(cb.literal(1L))
                    .where(cb.and(
                            sameWorkbook,
                            cb.or(titleLike, explLike, optionsLike, answersLike)
                    ));

            return cb.exists(sub);
        };
    }

    /** 정렬 키 매핑 + Pageable 생성 */
    private Pageable getPageable(int page, int size, String sort, String order) {
        String sortProp = switch (sort) {
            case "title"  -> "name";             // 워크북 제목
            case "author" -> "author.nickname";  // 작성자 닉네임
            default       -> sort;               // createdAt, updatedAt
        };
        Sort.Direction dir = "asc".equals(order) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return PageRequest.of(page, size, Sort.by(dir, sortProp));
    }
}
