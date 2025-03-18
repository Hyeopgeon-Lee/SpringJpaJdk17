package kopo.poly.controller.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

@Getter
@Setter
public class CommonResponse<T> {
    private HttpStatus httpStatus; // HTTP 상태 코드
    private String message; // HTTP 상태 메시지

    private T data; // 전달할 데이터

    /*
     * 메시지 구조 만들기
     * */
    @Builder
    public CommonResponse(HttpStatus httpStatus, String message, T data) {
        this.httpStatus = httpStatus;
        this.message = message;
        this.data = data;
    }

    /*
     * API 요청에 대한 Http 상태 정보 포함하여 응답 메시지 생성
     * */
    public static <T> CommonResponse<T> of(HttpStatus httpStatus, String message, T data) {
        return new CommonResponse<>(httpStatus, message, data);
    }

    /*
     * API 요청의 유효성 검증 실패하면, 오류 메시지 전달
     * */
    public static ResponseEntity<CommonResponse> getErrors(BindingResult bindingResult) {
        return ResponseEntity.badRequest()
                .body(CommonResponse.of(HttpStatus.BAD_REQUEST,
                        HttpStatus.BAD_REQUEST.series().name(),
                        bindingResult.getAllErrors()));
    }
}
