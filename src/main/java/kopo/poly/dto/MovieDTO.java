package kopo.poly.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Builder
public record MovieDTO(

        String collectTime, // 수집시간
        String rank, // 영화순위
        String name, // 영화제목
        String reserve, // 예매율
        String score, // 영화평점
        String openDay, // 개봉일

        @NotBlank(message = "음성 명령 메시지는 필수 입력 사항입니다.")
        String speechCommand //음성명령 메시지
) {
}

