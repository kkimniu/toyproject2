package com.roommate.domain.notice.repository;

import com.roommate.domain.notice.entity.NoticeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface NoticeRepository {
    long countPublic(@Param("keyword") String keyword);

    List<NoticeEntity> findPublic(@Param("keyword") String keyword,
                                  @Param("limit") int limit,
                                  @Param("offset") int offset);

    Optional<NoticeEntity> findPublicById(@Param("noticeId") Long noticeId);

    long countForAdmin(@Param("keyword") String keyword,
                       @Param("published") Boolean published);

    List<NoticeEntity> findForAdmin(@Param("keyword") String keyword,
                                    @Param("published") Boolean published,
                                    @Param("limit") int limit,
                                    @Param("offset") int offset);

    Optional<NoticeEntity> findForAdminById(@Param("noticeId") Long noticeId);

    int insert(NoticeEntity notice);

    int update(NoticeEntity notice);

    int softDelete(@Param("noticeId") Long noticeId);
}
