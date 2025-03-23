package gnu.capstone.G_Learn_E.domain.problem.serialization.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gnu.capstone.G_Learn_E.global.common.serialization.Option;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;
import java.util.List;

@Converter
public class OptionListConverter implements AttributeConverter<List<Option>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<Option> attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            // TODO : 예외처리 변경
            throw new RuntimeException("객관식 보기 직렬화 실패", e);
        }
    }

    @Override
    public List<Option> convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, new TypeReference<List<Option>>() {});
        } catch (IOException e) {
            // TODO : 예외처리 변경
            throw new RuntimeException("객관식 보기 역직렬화 실패", e);
        }
    }
}
