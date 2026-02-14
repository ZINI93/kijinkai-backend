package com.kijinkai.domain.post.service.postimage;

import com.kijinkai.domain.post.entity.PostImageJpaEntity;
import com.kijinkai.domain.post.entity.PostJpaEntity;
import com.kijinkai.domain.post.factory.PostImageEntityFactory;
import com.kijinkai.domain.post.repository.postimage.PostImageJpaEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;


@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PostImageApplicationServiceImpl implements PostImageApplicationService {

    private final PostImageJpaEntityRepository postImageRepository;
    private final PostImageEntityFactory postImageEntityFactory;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    @Transactional
    public PostImageJpaEntity saveImage(PostJpaEntity post, MultipartFile file) {

        // 파일 존재 여부 및 크기 체크
        validateFile(file);

        try {

            // 물리적 파일 저장
            String originalName = StringUtils.getFilename(file.getOriginalFilename());
            String savedName = savePhysicalFile(file, originalName);
            String accessUrl = "/images/" + savedName;

            // DB 저장
            PostImageJpaEntity image = postImageEntityFactory.createImage(post, originalName, savedName, accessUrl);
            return postImageRepository.save(image);


        } catch (IOException e) {
            log.error("파일 저장 실패: {}", e.getMessage());
            throw new RuntimeException("파일 저장 중 오류가 발생했습니다.", e);
        }
    }




    @Override
    @Transactional
    public PostImageJpaEntity updatePostImage(PostJpaEntity post, MultipartFile file, boolean isImageDeleted){

        //기존 이미지 조회
        PostImageJpaEntity oldImage = postImageRepository.findByPostPostId(post.getPostId()).orElse(null);

        if (isImageDeleted){
            if (oldImage != null){

                // 물리 삭제
                deletePhysicalFile(oldImage.getStoredFileName());

                // db 삭제
                postImageRepository.delete(oldImage);
            }
            return null;
        }

        // 파일 검증
        if (file == null || file.isEmpty()) {
            return oldImage; // 새 파일이 없으면 기존 유지
        }

        validateFile(file);

        try{

            String originalName = StringUtils.getFilename(file.getOriginalFilename());
            String savedName = savePhysicalFile(file, originalName);
            String accessUrl = "/images/" + savedName;

            PostImageJpaEntity resultImage;

            if (oldImage != null){
                // 기존 이미지가 있다면 업데이트 및 기존 물리 파일 삭제
                String oldFileName = oldImage.getStoredFileName();
                oldImage.updateImage(originalName, savedName, accessUrl);
                resultImage = oldImage;

                deletePhysicalFile(oldFileName);
            }else {
                // 기존 이미지 없다면 새로 생성
                resultImage = postImageEntityFactory.createImage(post, originalName, savedName, accessUrl);
                postImageRepository.save(resultImage);
            }

            return resultImage;

        } catch (IOException e) {
            log.error("파일 처리 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("이미지 수정 중 오류가 발생했습니다.", e);
        }

    }

    @Override
    public PostImageJpaEntity getImageByPostId(Long postId) {
        return postImageRepository.findByPostPostId(postId)
                .orElse(null);

    }


    @Override
    public List<PostImageJpaEntity> getImageByPostIds(List<Long> postId) {
        return postImageRepository.findByPostPostIdIn(postId);

    }


    private String savePhysicalFile(MultipartFile file, String originalName) throws IOException {
        String savedName = UUID.randomUUID() + "_" + originalName;

        // Paths.get()을 사용하여 안전하게 경로 조합
        Path uploadPath = Paths.get(uploadDir);

        // 폴더가 없으면 생성 (Files.createDirectories는 부모 폴더까지 안전하게 생성)
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path targetLocation = uploadPath.resolve(savedName);
        file.transferTo(targetLocation.toFile());

        return savedName;
    }

    private void deletePhysicalFile(String fileName) {
        if (fileName == null || fileName.isEmpty()) return;

        try {
            Path fileToDelete = Paths.get(uploadDir).resolve(fileName);
            if (Files.deleteIfExists(fileToDelete)) {
                log.info("물리 파일 삭제 성공: {}", fileName);
            }
        } catch (IOException e) {
            log.error("물리 파일 삭제 실패: {}, 원인: {}", fileName, e.getMessage());
        }
    }

    // helper

    // 확장자 체크 헬퍼 메서드
    private boolean isValidExtension(String fileName) {
        if (fileName == null) return false;
        String lowerName = fileName.toLowerCase();
        return lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg") ||
                lowerName.endsWith(".png") || lowerName.endsWith(".gif") ||
                lowerName.endsWith(".webp");
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다.");
        }

        if (!isValidExtension(file.getOriginalFilename())) {
            throw new IllegalArgumentException("지원하지 않는 파일 확장자입니다.");
        }
    }

}
