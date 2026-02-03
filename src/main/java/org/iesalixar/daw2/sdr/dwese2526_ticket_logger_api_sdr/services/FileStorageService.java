package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.services;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    @Value("${app.upload-root}")
    private String uploadRootPath;

    private static final String UPLOADS_SUBDIR = "uploads";

    public String saveFile(MultipartFile file){
        if (file == null || file.isEmpty()){
            logger.warn("Intento de guardar un archivo nulo o vacio");
            return null;
        }
        try{
            String originalFilename = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);

            String uniqueFileName = UUID.randomUUID().toString();
            if (!fileExtension.isBlank()){
                uniqueFileName += "." + fileExtension;
            }

            Path uploadsDir = Paths.get(uploadRootPath).resolve(UPLOADS_SUBDIR);
            Files.createDirectories(uploadsDir);
            Path filePath = uploadsDir.resolve(uniqueFileName);
            Files.write(filePath, file.getBytes());

            logger.info("Archivo {} guardado con éxito en {}", uniqueFileName, filePath);

            return "/uploads/"+ uniqueFileName;
        } catch (IOException e) {
            logger.error("Error al guardar el archivo: {}", e.getMessage(), e);
            return null;
        }
    }

    public void deleteFile(String filePathOrWebPath){
        if (filePathOrWebPath == null || filePathOrWebPath.isBlank()){
            logger.warn("Se ha intentado eliminar un archivo vacio");
            return;
        }
        try{
            String fileName = normalizeFilemane(filePathOrWebPath);

            Path uploadDir = Paths.get(uploadRootPath).resolve(UPLOADS_SUBDIR);
            Path filePath = uploadDir.resolve(fileName);

            Files.deleteIfExists(filePath);
            logger.info("Archivo {} eliminado con éxito ({})", fileName, filePath);


        }catch (IOException e){
            logger.error("Error al eliminar el archivo {} : {}", filePathOrWebPath, e.getMessage(), e);
        }
    }

    private String getFileExtension(String fileName){
        if (fileName != null){
            int lastDot = fileName.lastIndexOf(".");
            if (lastDot > 0 && lastDot < fileName.length()-1){
                return fileName.substring(lastDot + 1);
            }
        }
        return "";
    }



    private String normalizeFilemane(String filePathOrWebPath){
        String value = filePathOrWebPath.trim();

        if (value.startsWith("/uploads/")){
            value = value.substring("/uploads/".length());
        }
        int lastSlash = value.lastIndexOf("/");
        if (lastSlash >= 0 && lastSlash < value.length() -1 ){
            value = value.substring(lastSlash + 1);
        }
        return value;
    }











}
