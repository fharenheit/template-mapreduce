package com.yourcompany.hadoop.mapreduce.hcatalog;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hcatalog.data.HCatRecord;

import java.io.IOException;

public class HCatalogExampleMapper extends Mapper<WritableComparable<Text>, HCatRecord, Text, Text> {

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        // Get parameters from context.getConfiguration();
        // Initialize resources
    }

    @Override
    protected void map(WritableComparable<Text> key, HCatRecord value, Context context) throws IOException, InterruptedException {

        String guid = (String) value.get(0);
        String hServicecode = (String) value.get(1);

        // your logic

        // map output : key, value
        context.write(new Text(guid), new Text(hServicecode));
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        // Close resources
    }
}

