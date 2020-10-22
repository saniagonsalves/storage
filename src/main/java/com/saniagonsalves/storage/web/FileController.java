package com.saniagonsalves.storage.web;

import com.saniagonsalves.storage.service.FileService;
import com.saniagonsalves.storage.web.dto.FileInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Slf4j
@Controller
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        super();
        this.fileService = fileService;
    }

    @RequestMapping(value = "/list")
    String listFiles(Principal principal, Model model) {
        model.addAttribute("files", fileService.listFiles(principal, false));
        return "index";
    }

    @RequestMapping(value = "/adminlist")
    String adminListFiles(Principal principal, Model model) {
        model.addAttribute("files", fileService.listFiles(principal, true));
        return "admin";
    }

    @RequestMapping(value = "/upload")
    public String uploadFile(@RequestParam(value = "fileToUpload") MultipartFile file,
                             Principal principal,
                             RedirectAttributes redirectAttributes) {

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message",
                                                 "file not selected");
        } else {
            FileInfoDto fileInfoDto = fileService.saveFile(file, principal);
            redirectAttributes.addFlashAttribute("message",
                                                 "uploaded " + file.getOriginalFilename() + "! id " +
                                                 fileInfoDto.getFileId());
        }
        return "redirect:/list";
    }

    @ModelAttribute("fileIdToUpdate")
    public long getLong() {
        return 0L;
    }

    @RequestMapping(value = "/update")
    public String updateFile(@RequestParam(value = "fileIdUpdate") long fileIdUpdate,
                             @RequestParam(value = "fileToUpdate") MultipartFile file,
                             Principal principal,
                             RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message",
                                                 "file not selected");
        } else {
            FileInfoDto fileInfoDto = fileService.updateFile(file, fileIdUpdate, principal);
            redirectAttributes.addFlashAttribute("message",
                                                 "updated " + file.getOriginalFilename() + "! id " +
                                                 fileInfoDto.getFileId());
        }
        return "redirect:/list";
    }

    @RequestMapping(value = "/delete")
    public String deleteFile(@RequestParam(value = "fileid") long fileId, Principal principal) {
        if (fileService.deleteFile(fileId, principal)) {
            return "redirect:/list?success";
        } else {
            return "redirect:/list?failed";
        }
    }
}
