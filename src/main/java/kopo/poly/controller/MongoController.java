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
@RequestMapping(value = "/mongo")
@RequiredArgsConstructor
@RestController
public class MongoController {

    private final IMongoService mongoService;

    @PostMapping(value = "test")
    public ResponseEntity test(@Valid @RequestBody MongoDTO pDTO, BindingResult bindingResult) throws Exception {

        log.info(this.getClass().getName() + ".test Start!");

        if (bindingResult.hasErrors()) { // Spring Validation 맞춰 잘 바인딩되었는지 체크
            return CommonResponse.getErrors(bindingResult); // 에러 메시지를 전달
        }

        String msg; // 저장 결과 메시지

        // 반드시, 값을 받았으면, 꼭 로그를 찍어서 값이 제대로 들어오는지 파악해야 함
        log.info("pDTO " + pDTO);

        int res = mongoService.mongoTest(pDTO);


        if (res == 1) {
            msg = "저장 성공하였습니다.";

        } else {
            msg = "저장 실패하였습니다.";
        }

        // 결과 메시지 전달하기
        MsgDTO dto = MsgDTO.builder().msg(msg).build();

        log.info(this.getClass().getName() + ".test End!");

        return ResponseEntity.ok(
                CommonResponse.of(HttpStatus.OK, HttpStatus.OK.series().name(), dto));
    }


}

