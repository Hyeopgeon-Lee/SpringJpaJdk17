package kopo.poly.controller;

import jakarta.validation.Valid;
import kopo.poly.controller.response.CommonResponse;
import kopo.poly.dto.MongoDTO;
import kopo.poly.dto.MsgDTO;
import kopo.poly.service.IMongoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/mongo/v1")
@RequiredArgsConstructor
public class MongoController {

    // MongoDB 관련 비즈니스 로직 처리 서비스
    private final IMongoService mongoService;

    /**
     * MongoDB 기본 저장 API
     *
     * @param pDTO          클라이언트로부터 전달받은 데이터
     * @param bindingResult 유효성 검증 결과
     * @return 저장 성공 또는 실패 여부를 담은 응답
     */
    @PostMapping("/basic")
    public ResponseEntity<?> basic(@Valid @RequestBody MongoDTO pDTO, BindingResult bindingResult) throws Exception {

        log.info("{}.basic Start!", this.getClass().getName());

        // 1. DTO 유효성 검사 실패 시 에러 응답 반환
        if (bindingResult.hasErrors()) {
            return CommonResponse.getErrors(bindingResult);
        }

        // 2. 전달받은 DTO 로그 출력
        log.info("Received MongoDTO: {}", pDTO);

        // 3. MongoDB에 데이터 저장 시도
        int res = mongoService.mongoTest(pDTO);

        // 4. 저장 결과 메시지 설정
        String msg = (res == 1) ? "저장 성공하였습니다." : "저장 실패하였습니다.";

        // 5. 결과 메시지를 DTO로 구성
        MsgDTO dto = MsgDTO.builder()
                .result(res)
                .msg(msg)
                .build();

        log.info("{}.basic End!", this.getClass().getName());

        // 6. 공통 응답 객체에 결과 담아 반환
        return ResponseEntity.ok(
                CommonResponse.of(HttpStatus.OK, HttpStatus.OK.series().name(), dto));
    }
}
