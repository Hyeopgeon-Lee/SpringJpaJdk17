package kopo.poly.config;

import ch.qos.logback.core.AppenderBase;
import lombok.Setter;
import org.fluentd.logger.FluentLogger;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * FluentdAppender는 Logback의 Appender를 확장하여 Fluentd 서버로 로그를 전송하는 기능을 제공합니다.
 * 이 클래스는 Logback XML 설정을 통해 초기화되며, FluentLogger를 사용해 로그 데이터를 전송합니다.
 *
 * @param <E> 로그 이벤트 객체의 타입
 */
@Setter
@Component
public class FluentdAppender<E> extends AppenderBase<E> {

    // Fluentd와 통신을 담당하는 FluentLogger 객체
    private FluentLogger fluentLogger;

    // Logback XML에서 설정값을 주입하기 위한 Setter 메서드들
    // Fluentd 서버의 호스트명 또는 IP 주소 (Logback XML에서 설정)

    private String remoteHost;

    // Fluentd 서버의 포트 번호 (Logback XML에서 설정)
    private int port;

    // Fluentd 로그 태그 (Logback XML에서 설정)
    private String tag;

    /**
     * Logback 초기화 과정에서 호출되며 FluentLogger를 생성합니다.
     */
    @Override
    public void start() {
        // FluentLogger 초기화 (remoteHost, port, tag를 기반으로 생성)
        fluentLogger = FluentLogger.getLogger(tag, remoteHost, port);
        super.start();
    }

    /**
     * 로그 이벤트가 발생할 때 호출되어 로그를 Fluentd로 전송합니다.
     *
     * @param eventObject 로그 이벤트 객체
     */
    @Override
    protected void append(E eventObject) {
        if (fluentLogger != null && eventObject != null) {
            // 로그 데이터를 Map으로 생성
            Map<String, Object> data = new HashMap<>();
            data.put("message", eventObject.toString());
            // Fluentd로 로그 전송
            fluentLogger.log("log", data);
        }
    }

    /**
     * Logback 종료 시 호출되며 FluentLogger를 닫습니다.
     */
    @Override
    public void stop() {
        if (fluentLogger != null) {
            fluentLogger.close();
        }
        super.stop();
    }

}
