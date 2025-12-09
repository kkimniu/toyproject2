package com.roommate.domain.file.repository;

import com.roommate.domain.file.entity.TempUploadFileEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface TempUploadFileRepository {

    TempUploadFileEntity findById(@Param("tempFileId") Long tempFileId);

    List<TempUploadFileEntity> findExpired(@Param("threshold") LocalDateTime threshold);

    void save(TempUploadFileEntity entity);

    void updateUsed(TempUploadFileEntity entity);

    void deleteById(@Param("tempFileId") Long tempFileId);

}
