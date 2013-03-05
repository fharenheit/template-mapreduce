package com.yourcompany.hadoop.mapreduce;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;

public class Hdfs {

    public static void main(String[] args) throws IOException {
        Hdfs.list("/movielens_10m");
    }

    public static void printFileInfo(String path) throws IOException {
        Configuration conf = new Configuration();
        conf.set("fs.default.name", "hdfs://192.168.1.1:9000");
        FileSystem fs = FileSystem.get(conf);

        if(!fs.exists(new Path(path))) {
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
        conf.set("fs.default.name", "hdfs://192.168.1.1:9000");
        FileSystem fs = FileSystem.get(conf);

        if(!fs.exists(new Path(path))) {
            System.err.println("지정한 경로의 디렉토리 또는 파일이 존재하지 않습니다.");
            System.exit(-1);
        }

        long size = 0;
        FileStatus[] files = fs.listStatus(new Path(path));
        for (FileStatus file : files) {
            if(!file.isDir()) {
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

        if(!fs.exists(new Path(path))) {
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
}
