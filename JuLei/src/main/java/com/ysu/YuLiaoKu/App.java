package com.ysu.YuLiaoKu;

import com.ysu.wyh.TextProcessor;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class App {
    public static void main(String[] args) {
        String rootPath = "C:\\Users\\Asus\\Desktop\\大数据课程\\三级项目\\三级项目数据集\\data\\文本分类语料库";
        Util util = new Util(rootPath);
        util.traverseWithJava8(rootPath);
        System.out.println(util.totalDocs);
        System.out.println(util.processedDocs);


    }




}
