package com.uanid.toy.simplefileserver.controller;

import com.uanid.toy.simplefileserver.Utils;
import com.uanid.toy.simplefileserver.model.BadRequestException;
import com.uanid.toy.simplefileserver.model.FileMeta;
import com.uanid.toy.simplefileserver.model.InvalidPathException;
import com.uanid.toy.simplefileserver.model.api.ExceptionDto;
import com.uanid.toy.simplefileserver.model.api.FileMetaDto;
import com.uanid.toy.simplefileserver.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author uanid
 * @since 2020-04-16
 */
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class ApiController {

    private final StorageService storageService;

    @GetMapping("/ping")
    public String ping(HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_JSON.toString());
        return "\"ping\"";
    }

    private String getRequestPath(HttpServletRequest request) {
        String path = Utils.urlDecode(request.getRequestURI());
        return path.substring("/api".length());
    }

    @PostMapping("/**")
    public void upload(HttpServletRequest request,
                       @RequestParam(value = "file", required = false) MultipartFile file) throws InvalidPathException {
        String requestPath = getRequestPath(request);
        try {
            storageService.uploadFile(requestPath, file.getName(), file.getInputStream());
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot get file inputstream");
        }
    }

    @GetMapping("/**")
    public ResponseEntity<?> listingAndDownload(HttpServletRequest request) throws InvalidPathException, FileNotFoundException {
        String requestPath = getRequestPath(request);

        if (storageService.isFile(requestPath)) {
            Resource file = new InputStreamResource(storageService.getFile(requestPath).getIs());
            return ResponseEntity.ok(file);
        } else {
            List<FileMeta> fileMetas = storageService.listingDir(requestPath);
            List<FileMetaDto> fileMetaDtos = new ArrayList<>();
            for (FileMeta fileMeta : fileMetas) {
                FileMetaDto fileMetaDto = new FileMetaDto();
                BeanUtils.copyProperties(fileMeta, fileMetaDto);
                fileMetaDtos.add(fileMetaDto);
            }
            return ResponseEntity.ok(fileMetaDtos);
        }
    }

    @DeleteMapping("/**")
    public void delete(HttpServletRequest request) throws FileNotFoundException, InvalidPathException {
        String requestPath = getRequestPath(request);
        if (!storageService.delete(requestPath)) {
            throw new IllegalStateException("Delete failed. Maybe target is not empty directory or locked");
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({BadRequestException.class, InvalidPathException.class})
    public ExceptionDto badRequest(Exception e, HttpServletRequest request, Model model) {
        String requestPath = getRequestPath(request);
        ExceptionDto exceptionDto = ExceptionDto.builder()
                .developerMessage("Invalid path " + requestPath)
                .error(e.getClass().getCanonicalName())
                .errorMessage(e.getMessage())
                .build();
        return exceptionDto;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({FileNotFoundException.class})
    public ExceptionDto notFound(Exception e, HttpServletRequest request, Model model) {
        String requestPath = getRequestPath(request);
        ExceptionDto exceptionDto = ExceptionDto.builder()
                .developerMessage("NotFound file or directory in path " + requestPath)
                .error(e.getClass().getCanonicalName())
                .errorMessage(e.getMessage())
                .build();
        return exceptionDto;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({IllegalStateException.class, Exception.class})
    public ExceptionDto internalError(Exception e, HttpServletRequest request, Model model) {
        String requestPath = getRequestPath(request);
        ExceptionDto exceptionDto = ExceptionDto.builder()
                .developerMessage("An unknown error occurred " + requestPath)
                .error(e.getClass().getCanonicalName())
                .errorMessage(e.getMessage())
                .build();
        log.warn("An unknown exception has occurred that not expected", e);
        return exceptionDto;
    }
}
