package kopo.poly.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Builder
public record RedisDTO(

        String name, // 이름
        String email, // 이메일
        String addr, // 주소
        String text, // 테스트 문구
        float order // 정렬 순서

) {

}
