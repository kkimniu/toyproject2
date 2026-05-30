package com.roommate.domain.file.repository;

import com.roommate.domain.file.entity.TempUploadFileEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface TempUploadFileRepository {

    TempUploadFileEntity findById(@Param("tempFileId") Long tempFileId);

    TempUploadFileEntity findByIdAndMemberId(@Param("tempFileId") Long tempFileId, @Param("memberId") Long memberId);

    TempUploadFileEntity findByIdAndSignupKey(@Param("tempFileId") Long tempFileId, @Param("signupKey") String signupKey);

    List<TempUploadFileEntity> findExpired(@Param("threshold") LocalDateTime threshold);

    void save(TempUploadFileEntity entity);

    void updateUsed(TempUploadFileEntity entity);

    void updateUsedAndPath(@Param("tempFileId") Long tempFileId, @Param("used") Integer used, @Param("tempPath") String tempPath);

    void deleteById(@Param("tempFileId") Long tempFileId);

}
