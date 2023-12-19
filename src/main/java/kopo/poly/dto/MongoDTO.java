package kopo.poly.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

/**
 * MongoDB 데이터 저장용 DTO
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Builder
public record MongoDTO(

        String userName, // 이름
        String addr, // 주소
        String email // 이메일
) {
}

