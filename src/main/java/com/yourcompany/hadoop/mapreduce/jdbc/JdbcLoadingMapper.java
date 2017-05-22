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
package com.yourcompany.hadoop.mapreduce.jdbc;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JdbcLoadingMapper extends Mapper<LongWritable, Text, NullWritable, Text> {

    private String username;
    private String password;
    private String driver;
    private String url;
    private DriverManagerDataSource dataSource;
    private JdbcTemplate template;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        Configuration configuration = context.getConfiguration();
        this.username = configuration.get("username");
        this.password = configuration.get("password");
        this.driver = configuration.get("driver");
        this.url = configuration.get("url");

        this.dataSource = new DriverManagerDataSource();
        this.dataSource.setDriverClassName(driver);
        this.dataSource.setUrl(url);
        this.dataSource.setUsername(username);
        this.dataSource.setPassword(password);

        this.template = new JdbcTemplate(this.dataSource);
    }

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String row = value.toString();
        String[] columns = StringUtils.splitPreserveAllTokens(row, "|");

        // Auto Increment Primary Key를 SQL Update이후에 가져온다.
        long primaryKey = updateTableA(columns);
    }

    public long updateTableA(final String[] columns) {
        final String sql = "INSERT INTO FARMAR (NAME) VALUES(?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(
                new PreparedStatementCreator() {
                    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                        PreparedStatement pst = con.prepareStatement(sql, new String[]{"ID"}); // 자동생성되는 Primary Key 컬럼명
                        pst.setString(1, columns[0]);
                        return pst;
                    }
                },
                keyHolder);
        return (Long) keyHolder.getKey();
    }


    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
    }
}