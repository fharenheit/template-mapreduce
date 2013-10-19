package org.openflamingo.mapreduce.sample;

import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PdfTextExtractionMapper implements Mapper<BytesWritable, BytesWritable, NullWritable, Text> {

    String filename; // Old MapReduce API

    String lineDelimiter; // Delimiter per line

    String keyValueDelimiter; // Delimiter between filename and content

    @Override
    public void configure(JobConf job) {
        filename = job.get("map.input.file");
        lineDelimiter = job.get("line.delimiter", "    ");
        keyValueDelimiter = job.get("kv.delimiter", "####");
    }

    @Override
    public void map(BytesWritable key, BytesWritable value, OutputCollector<NullWritable, Text> collector, Reporter reporter) throws IOException {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Parser parser = new AutoDetectParser();
            ContentHandler handler = new BodyContentHandler(baos);
            Metadata metadata = new Metadata();
            parser.parse(new ByteArrayInputStream(value.getBytes()), handler, metadata, new ParseContext());
            String body = new String(baos.toByteArray());

            Text text = new Text(filename + keyValueDelimiter + body.replaceAll("\n", lineDelimiter));
            collector.collect(NullWritable.get(), text);
        } catch (Exception ex) {
            reporter.getCounter("STAT", "PARSE_ERROR").increment(1);
        }
    }

    @Override
    public void close() throws IOException {
    }

}

