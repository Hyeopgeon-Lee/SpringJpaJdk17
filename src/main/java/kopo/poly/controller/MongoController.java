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
@RequestMapping(value = "/mongo/v1")
@RequiredArgsConstructor
@RestController
public class MongoController {

    private final IMongoService mongoService;

    @PostMapping(value = "basic")
    public ResponseEntity<CommonResponse> basic(@Valid @RequestBody MongoDTO pDTO, BindingResult bindingResult)
            throws Exception {

        log.info("{}.basic Start!", this.getClass().getName());

        if (bindingResult.hasErrors()) { // Spring Validation 맞춰 잘 바인딩되었는지 체크
            return CommonResponse.getErrors(bindingResult); // 유효성 검증 결과에 따른 에러 메시지 전달

        }

        String msg; // 저장 결과 메시지


        log.info("pDTO : {}", pDTO); // 입력 받은 값 확인하기

        int res = mongoService.mongoTest(pDTO);

        if (res == 1) {
            msg = "저장 성공하였습니다.";

        } else {
            msg = "저장 실패하였습니다.";

        }

        MsgDTO dto = MsgDTO.builder().result(res).msg(msg).build();

        log.info("{}.basic End!", this.getClass().getName());

        return ResponseEntity.ok(
                CommonResponse.of(HttpStatus.OK, HttpStatus.OK.series().name(), dto));
    }
}

