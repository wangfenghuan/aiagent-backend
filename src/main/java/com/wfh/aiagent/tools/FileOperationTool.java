package com.wfh.aiagent.tools;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import com.wfh.aiagent.constant.FileConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * @Author FengHuan Wang
 * @Date 2025/5/27 20:02
 * @Version 1.0
 */
public class FileOperationTool {

    private final String FILE_DIR = FileConstant.FILE_SAVE_DIR + "/file";

    @Tool(description = "Read content from the current file")
    public String readFile(@ToolParam(description = "Name of a file need to read") String fileName){
        String filePath = FILE_DIR + "/" + fileName;
        try {
            String content = FileUtil.readUtf8String(filePath);
            return content;
        } catch (IORuntimeException e) {
            return "Error reading the file: " + e.getMessage();
        }
    }

    @Tool(description = "Write content to the current file")
    public String writeFile(@ToolParam(description = "Name of the file to write") String fileName,
                            @ToolParam(description = "Content to write to the the file") String content){
        String filePath = FILE_DIR + "/" + fileName;
        try {
            // 创建目录
            FileUtil.mkdir(FILE_DIR);
            FileUtil.writeString(content, filePath, "UTF-8");
            return "File written successfully to:" + filePath;
        }catch (IORuntimeException e){
            return "Error writing the file: " + e.getMessage();
        }
    }
}
