package kopo.poly.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

/**
 * MongoDB 데이터 저장용 DTO
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Builder
public record MongoDTO(

        @NotBlank(message = "제목은 필수 입력 사항입니다.")
        @Size(max = 100, message = "제목의 길이는 100글자까지 입력가능합니다.")
        String userName, // 이름

        @NotBlank(message = "주소는 필수 입력 사항입니다.")
        @Size(max = 1000, message = "제목의 길이는 1000글자까지 입력가능합니다.")
        String addr, // 주소

        @NotBlank(message = "이메일은 필수 입력 사항입니다.")
        @Size(max = 100, message = "이메일은 길이는 100글자까지 입력가능합니다.")
        String email // 이메일
) {
}

