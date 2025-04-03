package web_anime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import web_anime.entity.FileUpload;

public interface FileUploadRepository extends JpaRepository<FileUpload, Long> {
}
