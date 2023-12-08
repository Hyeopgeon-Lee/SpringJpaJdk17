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

    /**
     * JPQL 사용하여 공지사항 전체 가져오기
     */
    List<NoticeDTO> getNoticeListUsingJPQL();

    /**
     * QueryDSL 활용한 Fetch Join 적용된 공지사항 전체 가져오기
     */
    List<NoticeDTO> getNoticeListForQueryDSL();

    /**
     * QueryDSL 활용한 공지사항 상세 정보가져오기
     *
     * @param pDTO 공지사항 상세 가져오기 위한 정보
     * @param type 조회수 증가여부(true : 증가, false : 증가안함
     */
    NoticeDTO getNoticeInfoForQueryDSL(NoticeDTO pDTO, boolean type) throws Exception;

}
