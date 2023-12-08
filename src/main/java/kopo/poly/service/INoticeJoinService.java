package kopo.poly.service;


import kopo.poly.dto.NoticeDTO;

import java.util.List;

public interface INoticeJoinService {

    /**
     * JoinColumn 어노테이션을 활용한 공지사항 전체 가져오기
     * <p>
     * 회원정보 테이블 조인
     */
    List<NoticeDTO> getNoticeListUsingJoinColumn();

    /**
     * NativeQuery 사용하여 공지사항 전체 가져오기
     */
    List<NoticeDTO> getNoticeListUsingNativeQuery();

}
