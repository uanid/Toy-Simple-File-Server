package com.uanid.toy.simplefileserver.controller;

import com.uanid.toy.simplefileserver.model.BadRequestException;
import com.uanid.toy.simplefileserver.model.DownloadableFileMeta;
import com.uanid.toy.simplefileserver.model.FileMeta;
import com.uanid.toy.simplefileserver.model.InvalidPathException;
import com.uanid.toy.simplefileserver.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * @author uanid
 * @since 2020-01-24
 */
@Slf4j
@Controller
@RequestMapping
@RequiredArgsConstructor
public class DefaultController {

    private final FileStorageService storageService;

    @GetMapping("/ping")
    @ResponseBody
    public ResponseEntity<String> ping(HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_JSON.toString());
        return new ResponseEntity<>("\"pong\"", HttpStatus.OK);
    }

    @PostMapping("/**")
    public String upload(HttpServletRequest request, Model model,
                         @RequestParam(value = "requestType", required = true) String requestType,
                         @RequestParam(value = "directory", required = false) String directoryName,
                         @RequestParam(value = "target", required = false) String deleteTarget,
                         @RequestParam(value = "file", required = false) MultipartFile file) throws BadRequestException, InvalidPathException, IOException {
        String requestPath = request.getRequestURI();
        if (requestPath.endsWith("/")) {
            requestPath = requestPath.substring(0, requestPath.length() - 1);
        }

        if (requestType.equals("file") && file != null) {
            storageService.uploadFile(requestPath, file.getOriginalFilename(), file.getInputStream());
        } else if (requestType.equals("directory") && directoryName != null) {
            storageService.createDirectory(requestPath + "/" + directoryName);
        } else if (requestType.equals("delete") && deleteTarget != null) {
            if (!storageService.delete(deleteTarget)) {
                throw new IllegalStateException("Delete failed. Maybe target is not empty directory or locked");
            }
        } else {
            throw new BadRequestException("cannot resolve uploadType. uploadType is available only 'file' and 'directory'");
        }
        model.addAttribute("dirPath", requestPath);
        return "redirect:" + requestPath;

    }

    @GetMapping("/**")
    public String listingAndDownload(HttpServletRequest request, HttpServletResponse response, Model model) throws InvalidPathException, FileNotFoundException {
        String requestPath = request.getRequestURI();

        if (storageService.isFile(requestPath)) {
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM.toString());
            DownloadableFileMeta meta = storageService.getFile(requestPath);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=\"%s\"", meta.getName()));
            try {
                IOUtils.copy(meta.getIs(), response.getOutputStream());
            } catch (IOException e) {
                throw new IllegalStateException("Failed copy file to outputStream");
            }
            return null;
        } else {
            List<FileMeta> fileMetas = storageService.listingDir(requestPath);

            if (requestPath.endsWith("/")) {
                model.addAttribute("path", requestPath.substring(0, requestPath.length() - 1));
            } else {
                model.addAttribute("path", requestPath);
            }

            if (requestPath.equals("/")) {
                model.addAttribute("isRootPath", true);
            } else {
                int i1 = requestPath.lastIndexOf('/');
                String prevPath = requestPath.substring(0, i1);
                model.addAttribute("prevPath", prevPath.isEmpty() ? "/" : prevPath);
                model.addAttribute("isRootPath", false);
            }

            fileMetas.sort((m1, m2) -> {
                if (m1.isDirectory() == m2.isDirectory()) {
                    return m1.getName().compareTo(m2.getName());
                } else {
                    return m1.isDirectory() ? -1 : 1;
                }
            });

            model.addAttribute("files", fileMetas);
            model.addAttribute("dirPath", requestPath);
            return "fileViewer";
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({BadRequestException.class, InvalidPathException.class})
    public String a(Exception e, HttpServletRequest request, Model model) {
        String requestPath = request.getRequestURI();
        model.addAttribute("message", "Invalid directory path " + requestPath);
        model.addAttribute("error", e.getClass().getCanonicalName());
        model.addAttribute("errorMessage", e.getMessage());
        return "error";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({FileNotFoundException.class})
    public String a2(Exception e, HttpServletRequest request, Model model) {
        String requestPath = request.getRequestURI();
        model.addAttribute("message", "Not found");
        model.addAttribute("error", e.getClass().getCanonicalName());
        model.addAttribute("errorMessage", e.getMessage());
        return "error";
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(IllegalStateException.class)
    public String a3(Exception e, HttpServletRequest request, Model model) {
        String requestPath = request.getRequestURI();
        model.addAttribute("message", "An internal error occurred " + requestPath);
        model.addAttribute("error", e.getClass().getCanonicalName());
        model.addAttribute("errorMessage", e.getMessage());
        return "error";
    }
}
