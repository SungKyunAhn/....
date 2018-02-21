package com.aimir.bo.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.view.AbstractView;

import com.aimir.util.StringUtil;

public class FileDownloadView extends AbstractView {

    public FileDownloadView() {
        // 반드시 octet-stream으로 설정해야함
        super.setContentType("application/octet-stream");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.web.servlet.view.AbstractView#renderMergedOutputModel(java.util.Map,
     * javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void renderMergedOutputModel(Map<String, Object> map, HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        String filePath = StringUtil.nullToBlank(map.get("filePath"));
        String fileName = StringUtil.nullToBlank(map.get("fileName"));
        String realFileName = StringUtil.nullToBlank(map.get("realFileName"));

        if (StringUtil.nullToBlank(filePath).isEmpty() || StringUtil.nullToBlank(fileName).isEmpty()) {
            return;
        }

        String fullFileName;

        if (filePath.endsWith(File.separator) || filePath.endsWith("/")) {
            fullFileName = filePath + fileName;
        } else {
            fullFileName = filePath + File.separator + fileName;
        }

        File file = new File(fullFileName);

        if (!file.exists() || !file.isFile()) {
            return;
        }

        response.setContentType(super.getContentType());
        response.setContentLength((int) file.length());
        response.setHeader("Content-Transfer-Encoding", "binary");
        response.setHeader("Content-Disposition", "attachment;fileName=\"" + (realFileName.isEmpty() ? fileName : realFileName) + "\";");
        OutputStream out = response.getOutputStream();
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(file);
            FileCopyUtils.copy(fis, out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        }
        //out.flush();
    }
}