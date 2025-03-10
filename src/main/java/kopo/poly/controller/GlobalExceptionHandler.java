package kopo.poly.controller;

import jakarta.persistence.OptimisticLockException;
import kopo.poly.controller.response.CommonResponse;
import kopo.poly.dto.MsgDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

/**
 * 전역 예외 처리를 담당하는 클래스
 *
 * @ControllerAdvice를 사용하여 애플리케이션 전체에서 발생하는 예외를 중앙에서 처리함.
 */
@RestController
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * JPA의 낙관적 락(Optimistic Lock) 충돌 예외 처리
     * - 동시에 여러 사용자가 동일한 데이터를 수정할 때 발생
     * - 클라이언트에게 `409 Conflict` 상태 코드와 함께 메시지를 반환
     *
     * @param e 발생한 OptimisticLockException 예외 객체
     * @return ResponseEntity<CommonResponse> - HTTP 응답 객체
     */
    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<CommonResponse<MsgDTO>> handleOptimisticLockException(OptimisticLockException e) {

        // 응답 메시지 생성
        MsgDTO dto = MsgDTO.builder()
                .result(0)
                .msg("다른 사용자가 먼저 변경했습니다. 다시 시도해주세요. error : " + e.getMessage())
                .build();

        // HTTP 409 상태 코드와 함께 반환
        return ResponseEntity.ok(
                CommonResponse.of(HttpStatus.CONFLICT, HttpStatus.CONFLICT.series().name(), dto));
    }

    /**
     * 잘못된 요청(`IllegalArgumentException`) 발생 시 예외 처리
     * - 클라이언트가 잘못된 데이터를 입력한 경우 (예: 존재하지 않는 ID 조회 등)
     * - 클라이언트에게 `400 Bad Request` 상태 코드와 함께 메시지를 반환
     *
     * @param e 발생한 IllegalArgumentException 예외 객체
     * @return ResponseEntity<CommonResponse> - HTTP 응답 객체
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CommonResponse<String>> handleIllegalArgumentException(IllegalArgumentException e) {

        return ResponseEntity.ok(
                CommonResponse.of(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.series().name(), e.getMessage()));
    }

    /**
     * 서버 내부 오류(Exception) 발생 시 예외 처리
     * - 예상하지 못한 오류가 발생한 경우 처리
     * - 클라이언트에게 `500 Internal Server Error` 상태 코드와 함께 메시지를 반환
     *
     * @param e 발생한 Exception 예외 객체
     * @return ResponseEntity<CommonResponse> - HTTP 응답 객체
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<String>> handleException(Exception e) {

        return ResponseEntity.ok(
                CommonResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.series().name(),
                        e.getMessage()));
    }
}
