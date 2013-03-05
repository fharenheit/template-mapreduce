package com.yourcompany.hadoop.mapreduce;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IOUtils;

import java.io.IOException;

public class Hdfs {

    public static void main(String[] args) throws IOException {
        Hdfs.copy(args[0], args[1]);
    }

    public static void printFileInfo(String path) throws IOException {
        Configuration conf = new Configuration();
        conf.set("fs.default.name", "hdfs://125.141.144.168:9000");
        FileSystem fs = FileSystem.get(conf);

        if (!fs.exists(new Path(path))) {
            System.err.println("지정한 경로의 디렉토리 또는 파일이 존재하지 않습니다.");
            System.exit(-1);
        }

        FileStatus file = fs.getFileStatus(new Path(path));

        System.out.println("Block Size : " + file.getBlockSize());
        System.out.println("Length : " + file.getLen());
        System.out.println("Replication : " + file.getReplication());
    }

    public static void size(String path) throws IOException {
        Configuration conf = new Configuration();
        conf.set("fs.default.name", "hdfs://125.141.144.168:9000");
        FileSystem fs = FileSystem.get(conf);

        if (!fs.exists(new Path(path))) {
            System.err.println("지정한 경로의 디렉토리 또는 파일이 존재하지 않습니다.");
            System.exit(-1);
        }

        long size = 0;
        FileStatus[] files = fs.listStatus(new Path(path));
        for (FileStatus file : files) {
            if (!file.isDir()) {
                size += file.getLen();
            }
        }

        // hadoop fs -ls /movielens_10m | awk '{sum+=$5} END {printf "%.2f MB\n", sum / 1024^2}'
        // sed
        System.out.println("Size: " + org.apache.hadoop.util.StringUtils.byteDesc(size));

    }

    public static void list(String path) throws IOException {
        Configuration conf = new Configuration();
        conf.set("fs.default.name", "hdfs://125.141.144.168:9000");
        FileSystem fs = FileSystem.get(conf);

        if (!fs.exists(new Path(path))) {
            System.err.println("지정한 경로의 디렉토리 또는 파일이 존재하지 않습니다.");
            System.exit(-1);
        }

        long size = 0;
        FileStatus[] files = fs.listStatus(new Path(path));
        for (FileStatus file : files) {
            System.out.println("URI         : " + file.getPath().toUri().toString());
            System.out.println("ToString    : " + file.getPath().toString());
            System.out.println("Name        : " + file.getPath().getName());
            System.out.println("Scheme      : " + file.getPath().toUri().getScheme());
        }

        // hadoop fs -ls /movielens_10m | awk '{sum+=$5} END {printf "%.2f MB\n", sum / 1024^2}'
        // sed
        System.out.println("Size: " + org.apache.hadoop.util.StringUtils.byteDesc(size));
    }

    public static void move(String source, String target) throws IOException {
        Configuration conf = new Configuration();
        conf.set("fs.default.name", "hdfs://125.141.144.168:9000");
        FileSystem fs = FileSystem.get(conf);

        if (!fs.exists(new Path(source))) {
            System.err.println("지정한 경로의 디렉토리 또는 파일이 존재하지 않습니다.");
            System.exit(-1);
        }

        if(fs.rename(new Path(source), new Path(target))) {
            System.out.println("파일을 이동했습니다.");
            System.exit(0);
        }
        System.out.println("파일을 이동하지 못했습니다.");
        System.exit(-1);
    }

    public static void copy(String source, String target) throws IOException {
        Configuration conf = new Configuration();
        conf.set("fs.default.name", "hdfs://125.141.144.168:9000");
        FileSystem fs = FileSystem.get(conf);

        if (!fs.exists(new Path(source))) {
            System.err.println("지정한 경로의 디렉토리 또는 파일이 존재하지 않습니다.");
            System.exit(-1);
        }

        FileStatus sourcePath = fs.getFileStatus(new Path(source));
        if(!sourcePath.isDir()) {
            if(fs.exists(new Path(target))) {
                System.err.println("이미 파일이 있어서 복사할 수 없습니다.");
                System.exit(-1);
            }

            FSDataInputStream in = fs.open(new Path(source));
            FSDataOutputStream out = fs.create(new Path(target));
            IOUtils.copyBytes(in, out, 4096);
            IOUtils.closeStream(out);
            IOUtils.closeStream(in);

            System.out.println("파일을 복사했습니다.");
            System.exit(0);
        }

        System.err.println("디렉토리는 복사할 수 없습니다.");
        System.exit(-1);
    }
}
