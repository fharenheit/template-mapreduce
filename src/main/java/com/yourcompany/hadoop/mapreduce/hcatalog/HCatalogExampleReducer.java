package com.yourcompany.hadoop.mapreduce.hcatalog;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hcatalog.data.DefaultHCatRecord;
import org.apache.hcatalog.data.HCatRecord;

import java.io.IOException;

public class HCatalogExampleReducer extends Reducer<Text, Text, WritableComparable<Text>, HCatRecord> {

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        // Get parameters from context.getConfiguration();
        // Initialize resources
    }

    @Override
    public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {

        // your logic
        int sum = 0;
        for (Text value : values) {
            sum++;
        }

        HCatRecord record = new DefaultHCatRecord(2);
        record.set(0, key.toString());
        record.set(1, sum);

        // reduce output : null, record
        context.write(null, record);
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        // Close resources
    }
}