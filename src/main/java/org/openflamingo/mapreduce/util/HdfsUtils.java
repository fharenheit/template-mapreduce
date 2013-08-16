/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openflamingo.mapreduce.util;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hdfs.DFSClient;
import org.apache.hadoop.hdfs.protocol.HdfsFileStatus;
import org.apache.hadoop.mapreduce.InputSplit;
import org.openflamingo.mapreduce.util.filter.BypassPathFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * HDFS Utility.
 *
 * @author Edward KIM
 * @author Seo Ji Hye
 * @since 0.1
 */
public class HdfsUtils {

    /**
     * SLF4J Logging
     */
    private static Logger logger = LoggerFactory.getLogger(HdfsUtils.class);

    public static final String DEFAULT_UGI = "hadoop,hadoop";

    public static final String HDFS_URL_PREFIX = "hdfs://";

    /**
     * Hadoop HDFS의 DFS Client를 생성한다.
     *
     * @param hdfsUrl HDFS URL
     * @return DFS Client
     * @throws java.io.IOException DFS Client를 생성할 수 없는 경우
     */
    public static DFSClient createDFSClient(String hdfsUrl) throws IOException {
        if (hdfsUrl == null || !hdfsUrl.startsWith("hdfs://")) {
            throw new IllegalArgumentException("HDFS URL이 잘못되었습니다. 요청한 HDFS URL [" + hdfsUrl + "]");
        }
        String url = StringUtils.replace(hdfsUrl, "hdfs://", "");
        String[] parts = url.split(":");
        return createDFSClient(parts[0], Integer.valueOf(parts[1]));
    }

    /**
     * Hadoop HDFS의 DFS Client를 생성한다.
     *
     * @param namenodeIp   Namenode IP
     * @param namenodePort Namenode Port
     * @return DFS Client
     * @throws java.io.IOException DFS Client를 생성할 수 없는 경우
     */
    public static DFSClient createDFSClient(String namenodeIp, int namenodePort) throws IOException {
        Configuration config = new Configuration();
        InetSocketAddress address = new InetSocketAddress(namenodeIp, namenodePort);
        return new DFSClient(address, config);
    }

    /**
     * 지정한 경로를 삭제한다.
     *
     * @param client    DFS Client
     * @param path      삭제할 경로
     * @param recursive Recusive 적용 여부
     * @return 성공시 <tt>true</tt>
     * @throws java.io.IOException 파일을 삭제할 수 없는 경우
     */
    public static boolean remove(DFSClient client, String path, boolean recursive) throws IOException {
        if (client.exists(path)) {
            logger.info("요청한 [{}] 파일이 존재하므로 삭제합니다. Recursive 여부 [{}]", path, recursive);
            return client.delete(path, recursive);
        }
        logger.info("요청한 [{}] 파일이 존재하지 않습니다.", path);
        return false;
    }

    /**
     * 지정한 경로의 파일 목록을 얻는다.
     *
     * @param fs   FileSystem
     * @param path 경로
     * @return 파일 경로 목록
     * @throws java.io.IOException HDFS IO를 처리할 수 없는 경우
     */
    public static List<String> listFiles(FileSystem fs, String path) throws IOException {
        List<String> list = new ArrayList<String>();
        FileStatus[] statuses = fs.listStatus(new Path(path));
        if (statuses != null) {
            for (FileStatus status : statuses) {
                if (!status.isDir()) {
                    String fullyQualifiedHDFSFilename = path + "/" + status.getPath().getName();
                    list.add(fullyQualifiedHDFSFilename);
                }
            }
        }
        return list;
    }

    /**
     * DFS Client의 출력 스트립을 얻는다.
     *
     * @param client    DFS Client
     * @param filename  파일명
     * @param overwrite Overwrite 여부
     * @return 출력 스트림
     * @throws java.io.IOException HDFS IO를 처리할 수 없는 경우
     */
    public static OutputStream getOutputStream(DFSClient client, String filename, boolean overwrite) throws IOException {
        return client.create(filename, overwrite);
    }

    /**
     * DFS Client의 입력 스트립을 얻는다.
     *
     * @param client   DFS Client
     * @param filename 파일 경로
     * @return 입력 스트림
     * @throws java.io.IOException HDFS IO를 처리할 수 없는 경우
     */
    public static InputStream getInputStream(DFSClient client, String filename) throws IOException {
        return client.open(filename);
    }

    /**
     * 출력 스트림을 종료한다.
     *
     * @param outputStream 출력 스트림
     * @throws java.io.IOException 출력 스트림을 종료할 수 없는 경우
     */
    public static void closeOuputStream(OutputStream outputStream) throws IOException {
        outputStream.close();
    }

    /**
     * 입력 스트림을 종료한다.
     *
     * @param inputStream 입력 스트림
     * @throws java.io.IOException 입력 스트림을 종료할 수 없는 경우
     */
    public static void closeInputStream(InputStream inputStream) throws IOException {
        inputStream.close();
    }

    /**
     * Input Split의 파일명을 반환한다.
     * Input Split은 기본적으로 <tt>file + ":" + start + "+" + length</tt> 형식으로 구성되어 있다.
     *
     * @param inputSplit Input Split
     * @return 파일명
     */
    public static String getFilename(InputSplit inputSplit) {
        String filename = org.openflamingo.mapreduce.util.FileUtils.getFilename(inputSplit.toString());
        int start = filename.indexOf(":");
        return filename.substring(0, start);
    }

    /**
     * FileSystem을 반환한다.
     *
     * @param hdfsUrl HDFS URL
     * @return FileSystem
     * @throws java.io.IOException FileSystem을 얻을 수 없는 경우
     */
    public static FileSystem getFileSystem(String hdfsUrl) throws IOException {
        Configuration configuration = new Configuration();
        configuration.set("fs.default.name", hdfsUrl);
        return FileSystem.get(configuration);
    }

    /**
     * 지정한 경로가 존재하는지 확인한다.
     *
     * @param client DFS Client
     * @param path   존재 여부를 판단할 경로
     * @return 존재하면 <tt>true</tt>
     * @throws java.io.IOException HDFS IO를 처리할 수 없는 경우
     */
    public static boolean exists(DFSClient client, String path) throws IOException {
        return client.exists(path);
    }

    /**
     * 지정한 경로가 파일인지 확인한다.
     *
     * @param client DFS Client
     * @param path   경로
     * @return 파일인 경우 <tt>true</tt>
     * @throws java.io.IOException HDFS IO를 처리할 수 없는 경우
     */
    public static boolean isFile(DFSClient client, String path) throws IOException {
        HdfsFileStatus status = client.getFileInfo(path);
        return !status.isDir();
    }

    /**
     * 지정한 경로가 디렉토리인지 확인한다.
     *
     * @param fs   {@link org.apache.hadoop.fs.FileSystem}
     * @param path 경로
     * @return 디렉토리인 경우 <tt>true</tt>
     * @throws java.io.IOException HDFS IO를 처리할 수 없는 경우
     */
    public static boolean isDirectory(FileSystem fs, String path) throws IOException {
        try {
            FileStatus status = fs.getFileStatus(new Path(path));
            return status.isDir();
        } catch (FileNotFoundException ex) {
            return false;
        }
    }

    /**
     * 문자열을 지정한 파일로 저장한다.
     *
     * @param client  DFS Client
     * @param path    저장할 파일의 절대 경로
     * @param content 저장할 파일의 문자열 내용
     * @throws java.io.IOException HDFS IO를 처리할 수 없는 경우
     */
    public static void saveFile(DFSClient client, String path, String content) throws IOException {
        OutputStream outputStream = getOutputStream(client, path, true);
        org.openflamingo.mapreduce.util.FileUtils.copy(content.getBytes(), outputStream);
        outputStream.close();
    }

    /**
     * 지정한 경로의 파일 정보를 얻어온다.
     *
     * @param client DFS Client
     * @param path   파일 정보를 얻어올 경로
     * @return 파일 정보
     * @throws java.io.IOException HDFS IO를 처리할 수 없는 경우
     */
    public static HdfsFileStatus getFileInfo(DFSClient client, String path) throws IOException {
        return client.getFileInfo(path);
    }

    /**
     * 다운로드한 로컬 파일 시스템에 존재하는 파일을 지정한 HDFS에 업로드한다.
     *
     * @param hdfsUrl          HDFS URL
     * @param filename         HDFS의 Path에 저장할 파일명
     * @param hdfsPath         HDFS의 Path
     * @param downloadFilePath 로컬 파일 시스템에 있는 다운로드한 파일
     * @throws java.io.IOException HDFS 작업을 실패한 경우
     */
    public static void uploadToHdfs(String hdfsUrl, String filename, String hdfsPath, String downloadFilePath) throws IOException {
        String hdfsFullPath = hdfsPath + "/" + filename;
        File inputFile = new File(downloadFilePath);
        DFSClient dfsClient = HdfsUtils.createDFSClient(hdfsUrl);
        copyFromLocalFileToHdfsFile(inputFile, dfsClient, hdfsFullPath);
        dfsClient.close();
    }

    /**
     * 로컬 파일 시스템의 파일을 HDFS로 복사한다.
     *
     * @param inputFile 로컬 파일 시스템의 입력 파일
     * @param client    DFSClient
     * @param hdfsPath  HDFS의 출력 파일 경로
     * @throws java.io.IOException 파일을 복사할 수 없는 경우
     */
    public static void copyFromLocalFileToHdfsFile(File inputFile, DFSClient client, String hdfsPath) throws IOException {
        OutputStream outputStream = HdfsUtils.getOutputStream(client, hdfsPath, true);
        InputStream inputStream = new FileInputStream(inputFile);
        org.openflamingo.mapreduce.util.FileUtils.copy(inputStream, outputStream);
    }


    /**
     * HDFS 상에서 지정한 파일을 다른 디렉토리로 파일을 이동시킨다.
     *
     * @param conf            Hadoop Configuration
     * @param path            이동할 파일
     * @param prefixToAppend  파일을 이동할 때 파일명의 prefix에 추가할 문자열
     * @param targetDirectory 목적 디렉토리
     * @throws java.io.IOException 파일을 이동할 수 없는 경우
     */
    public static void moveFileToDirectory(Configuration conf, String path, String prefixToAppend, String targetDirectory) throws IOException {
        FileSystem fileSystem = FileSystem.get(conf);
        FileStatus[] statuses = fileSystem.listStatus(new Path(path));
        for (FileStatus fileStatus : statuses) {
            String filename = prefixToAppend + "_" + fileStatus.getPath().getName();
            if (!isExist(conf, targetDirectory + "/" + filename)) {
                fileSystem.rename(fileStatus.getPath(), new Path(targetDirectory + "/" + filename));
            } else {
                throw new RuntimeException("\t  Warn: '" + fileStatus.getPath() + "' cannot moved. Already exists.");
            }
        }
    }

    /**
     * HDFS 상에서 지정한 파일을 다른 디렉토리로 파일을 이동시킨다.
     *
     * @param conf            Hadoop Configuration
     * @param delayFiles      이동할 파일 목록
     * @param targetDirectory 목적 디렉토리
     * @throws java.io.IOException 파일을 이동할 수 없는 경우
     */
    public static void moveFilesToDirectory(Configuration conf, List<String> delayFiles, String targetDirectory) throws IOException {
        for (String path : delayFiles) {
            String filename = FileUtils.getFilename(path);
            String delayedFilePrefix = filename.split("-")[0];
            String outputHead = delayedFilePrefix.replaceAll("delay", "");
            String outputMiddle = delayedFilePrefix.substring(0, 5);    // todo
            String outputTail = filename.replaceAll(delayedFilePrefix, "");

            System.out.println("Acceleration Dir " + targetDirectory + "/" + outputHead + "_" + outputMiddle + outputTail);
            makeDirectoryIfNotExists(targetDirectory, conf);

            FileSystem fileSystem = FileSystem.get(conf);
            fileSystem.rename(
                    new Path(path),
                    new Path(targetDirectory + "/" + outputHead + "_" + outputMiddle + outputTail));

            System.out.println("\t Moved: '" + path + "' --> '" + targetDirectory + "'");
        }
    }

    /**
     * HDFS 상에서 지정한 파일을 다른 디렉토리로 파일을 이동시킨다.
     *
     * @param conf            Hadoop Configuration
     * @param paths           이동할 파일 목록
     * @param prefixToAppend  파일을 이동할 때 파일명의 prefix에 추가할 문자열
     * @param targetDirectory 목적 디렉토리
     * @throws java.io.IOException 파일을 이동할 수 없는 경우
     */
    public static void moveFilesToDirectory(Configuration conf, List<String> paths, String prefixToAppend, String targetDirectory) throws IOException {
        for (String file : paths) {
            try {
                HdfsUtils.moveFileToDirectory(conf, file, prefixToAppend, targetDirectory);
                System.out.println("\t Moved: '" + file + "' --> '" + targetDirectory + "'");
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            }
        }
    }

    /**
     * 디렉토리가 존재하지 않는다면 생성한다.
     *
     * @param directory 디렉토리
     * @param hdfsUrl   HDFS URL
     * @throws java.io.IOException HDFS 작업을 실패한 경우
     */
    public static void makeDirectoryIfNotExists(String directory, String hdfsUrl) throws IOException {
        Configuration conf = new Configuration();
        conf.set("fs.default.name", hdfsUrl);
        conf.set("hadoop.job.ugi", DEFAULT_UGI);
        FileSystem fileSystem = FileSystem.get(conf);
        if (!isDirectory(fileSystem, directory)) {
            logger.info("HDFS에 [{}] 디렉토리가 존재하지 않아서 생성합니다.", directory);
            fileSystem.mkdirs(new Path(directory));
        }
    }

    /**
     * 해당 HDFS 디렉토리에 있는 모든 파일 목록을 반환한다.
     *
     * @param hdfsUrl         HDFS URL
     * @param hdfsDirectories HDFS 디렉토리 목록
     * @return HDFS 디렉토리에 포함되어 있는 모든 파일 목록
     * @throws java.io.IOException HDFS에 접근할 수 없거나, 파일 목록을 알아낼 수 없는 경우
     */
    public static String[] getHdfsFiles(String hdfsUrl, List<String> hdfsDirectories) throws IOException {
        List<String> filesInDirectories = new ArrayList<String>();
        FileSystem fs = HdfsUtils.getFileSystem(hdfsUrl);
        for (String hdfsDirectory : hdfsDirectories) {
            List<String> files = HdfsUtils.listFiles(fs, hdfsDirectory);
            filesInDirectories.addAll(files);
        }
        return StringUtils.toStringArray(filesInDirectories);
    }

    /**
     * HDFS의 해당 경로의 모든 파일에서 지정한 확장자를 가진 파일 목록을 반환한다.
     *
     * @param hdfsUrl HDFS URL
     * @param ext     확장자(예: <tt>.dat</tt>)
     * @param path    경로
     * @return "<tt>.dat</tt>" 확장자를 가진 파일 목록
     * @throws java.io.IOException HDFS 작업을 실패한 경우
     */
    public static String[] getHdfsFiles(String hdfsUrl, String ext, String path) throws IOException {
        ArrayList<String> files = new ArrayList<String>();
        DFSClient client = HdfsUtils.createDFSClient(hdfsUrl);
        makeDirectoryIfNotExists(path, hdfsUrl);
        client.close();
        return StringUtils.toStringArray(files);
    }

    /**
     * 지정한 경로에 파일이 존재하는지 확인한다.
     *
     * @param hdfsUrl HDFS URL
     * @param path    존재 여부를 확인할 절대 경로
     * @return 존재한다면 <tt>true</tt>
     * @throws java.io.IOException 파일 존재 여부를 알 수 없거나, HDFS에 접근할 수 없는 경우
     */
    public static boolean isExist(String hdfsUrl, String path) throws IOException {
        DFSClient client = HdfsUtils.createDFSClient(hdfsUrl);
        HdfsFileStatus status = client.getFileInfo(path);
        if (status != null && !status.isDir()) {
            client.close();
            return true;
        }
        client.close();
        return false;
    }

    /**
     * 디렉토리가 존재하지 않는다면 생성한다.
     *
     * @param directory 디렉토리
     * @param conf      Hadoop Configuration
     * @throws java.io.IOException HDFS 작업을 실패한 경우
     */
    public static void makeDirectoryIfNotExists(String directory, Configuration conf) throws IOException {
        FileSystem fileSystem = FileSystem.get(conf);
        if (!isExist(conf, directory) && !isDirectory(fileSystem, directory)) {
            fileSystem.mkdirs(new Path(directory));
        }
    }

    /**
     * 지정한 경로에 파일이 존재하는지 확인한다.
     *
     * @param path 존재 여부를 확인할 절대 경로
     * @return 존재한다면 <tt>true</tt>
     * @throws java.io.IOException 파일 존재 여부를 알 수 없거나, HDFS에 접근할 수 없는 경우
     */
    public static boolean isExist(String path) throws IOException {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
        FileStatus status = fs.getFileStatus(new Path(path));
        return status != null;
    }

    /**
     * 지정한 경로에 파일이 존재하는지 확인한다.
     *
     * @param conf Haodop Job Configuration
     * @param path 존재 여부를 확인할 절대 경로
     * @return 존재한다면 <tt>true</tt>
     * @throws java.io.IOException 파일 존재 여부를 알 수 없거나, HDFS에 접근할 수 없는 경우
     */
    public static boolean isExist(Configuration conf, String path) throws IOException {
        FileSystem fs = FileSystem.get(conf);
        return fs.exists(new Path(path));
    }

    /**
     * HDFS에서 지정한 디렉토리의 모든 파일을 삭제한다.
     *
     * @param hdfsUrl       HDFS URL
     * @param hdfsDirectory 파일을 삭제할 HDFS Directory URL
     * @throws java.io.IOException 파일을 삭제할 수 없는 경우
     */
    public static void deleteFromHdfs(String hdfsUrl, String hdfsDirectory) throws IOException {
        Configuration conf = new Configuration();
        conf.set("fs.default.name", hdfsUrl);
        FileSystem fs = FileSystem.get(conf);
        FileStatus[] statuses = fs.globStatus(new Path(hdfsDirectory));
        for (int i = 0; i < statuses.length; i++) {
            FileStatus fileStatus = statuses[i];
            fs.delete(fileStatus.getPath(), true);
        }
    }

    /**
     * HDFS에서 지정한 디렉토리의 모든 파일을 삭제한다.
     *
     * @param hdfsDirectory 파일을 삭제할 HDFS Directory URL
     * @throws java.io.IOException 파일을 삭제할 수 없는 경우
     */
    public static void deleteFromHdfs(String hdfsDirectory) throws IOException {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
        FileStatus[] statuses = fs.globStatus(new Path(hdfsDirectory));
        for (int i = 0; i < statuses.length; i++) {
            FileStatus fileStatus = statuses[i];
            fs.delete(fileStatus.getPath(), true);
        }
    }

    /**
     * 해당 경로에 있는 파일을 MERGE한다.
     *
     * @param hdfsUrl HDFS URL
     * @param path    HDFS Path
     * @throws java.io.IOException Get Merge할 수 없는 경우
     */
    public static void merge(String hdfsUrl, String path) throws IOException {
        // 입력 경로의 모든 파일을 Get Merge하여 임시 파일에 기록한다.
        Configuration conf = new Configuration();
        conf.set("fs.default.name", hdfsUrl);
        FileSystem fileSystem = FileSystem.get(conf);
        Path source = new Path(path);
        if (!fileSystem.getFileStatus(source).isDir()) {
            // 이미 파일이라면 더이상 Get Merge할 필요없다.
            return;
        }
        Path target = new Path(path + "_temporary");
        FileUtil.copyMerge(fileSystem, source, fileSystem, target, true, conf, null);

        // 원 소스 파일을 삭제한다.
        fileSystem.delete(source, true);

        // 임시 파일을 원 소스 파일명으로 대체한다.
        Path in = new Path(path + "_temporary");
        Path out = new Path(path);
        fileSystem.rename(in, out);

        // 임시 디렉토리를 삭제한다.
        fileSystem.delete(new Path(path + "_temporary"), true);
    }

    /**
     * HDFS의 해당 경로의 모든 파일에서 가장 최신 파일 하나를 반환한다.
     *
     * @param conf       Hadoop Configuration
     * @param path       경로
     * @param pathFilter 파일을 필터링하는 필터
     * @return 가장 최신 파일
     * @throws java.io.IOException HDFS 작업을 실패한 경우
     */
    public static String getLatestFile(Configuration conf, String path, PathFilter pathFilter) throws IOException {
        List<SortableFileStatus> files = new ArrayList<SortableFileStatus>();
        FileSystem fs = FileSystem.get(conf);
        FileStatus[] statuses = fs.listStatus(new Path(path), pathFilter != null ? pathFilter : new BypassPathFilter());
        if (statuses != null) {
            for (FileStatus fileStatus : statuses) {
                if (!fileStatus.isDir()) {
                    files.add(new SortableFileStatus(fileStatus));
                }
            }
        }
        Collections.sort(files);
        FileStatus fileStatus = files.get(0).fileStatus;
        return fileStatus.getPath().toUri().getPath();
    }

    /**
     * 지정한 경로를 삭제한다.
     *
     * @param configuration Hadoop Configuration
     * @param path          삭제할 경로
     * @throws java.io.IOException 삭제할 수 없는 경우
     */
    public static void delete(Configuration configuration, String path) throws IOException {
        FileSystem fileSystem = FileSystem.get(configuration);
        Path source = new Path(path);
        fileSystem.delete(source, true);
    }

    /**
     * HDFS의 해당 경로의 모든 파일에서 prefix로 시작하는 파일 목록을 반환한다.
     *
     * @param conf       Configuration
     * @param path       경로
     * @param prefix     파일의 Prefix
     * @param pathFilter 파일을 필터링하는 필터
     * @return 지정한 prefix로 파일명이 시작하는 파일 목록
     * @throws java.io.IOException HDFS 작업을 실패한 경우
     */
    public static List<String> getPrefixFiles(Configuration conf, String path, String prefix, PathFilter pathFilter) throws IOException {
        List<String> files = new ArrayList<String>();
        FileSystem fs = FileSystem.get(conf);
        FileStatus[] statuses = fs.listStatus(new Path(path), pathFilter != null ? pathFilter : new BypassPathFilter());
        if (statuses != null) {
            for (FileStatus fileStatus : statuses) {
                if (!fileStatus.isDir() && fileStatus.getPath().getName().startsWith(prefix)) {
                    files.add(fileStatus.getPath().toUri().getPath());
                }
            }
        }
        return files;
    }
}
