package com.kijinkai.domain.post.factory;

import com.kijinkai.domain.post.entity.PostImageJpaEntity;
import com.kijinkai.domain.post.entity.PostJpaEntity;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
public class PostImageEntityFactory {


    public PostImageJpaEntity createImage(PostJpaEntity post, String originalFileName,String storedFileName, String imageUrl){

        Objects.requireNonNull(post, "post는 null일 수 없습니다");
        Objects.requireNonNull(originalFileName, "originalFileName는 필수입니다");
        Objects.requireNonNull(storedFileName, "storedFileName는 필수입니다");
        Objects.requireNonNull(imageUrl, "imageUrl는 필수입니다");

        return PostImageJpaEntity.builder()
                .postImageUuid(UUID.randomUUID())
                .post(post)
                .originalFileName(originalFileName)
                .storedFileName(storedFileName)
                .imageUrl(imageUrl)
                .sortOrder(0)  // 기본적으로 한장의 사진 업로드 제공
                .build();


    }
}
