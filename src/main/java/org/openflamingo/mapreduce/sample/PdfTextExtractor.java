package org.openflamingo.mapreduce.sample;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;

import java.awt.print.PrinterException;
import java.io.*;

public class PdfTextExtractor {

    public static void main(String[] args) throws IOException, PrinterException {
        InputStream is = null;

        try {
            is = new BufferedInputStream(new FileInputStream(new File("src/test/resources/hangul.pdf")));
            Parser parser = new PDFParser();
            ContentHandler handler = new BodyContentHandler(System.out); // Output Stream을 이용하여 저장 가능.
            Metadata metadata = new Metadata();
            parser.parse(is, handler, metadata, new ParseContext());

            for (String name : metadata.names()) {
                String value = metadata.get(name);
                if (value != null) {
                    System.out.println("Metadata Name:  " + name);
                    System.out.println("Metadata Value: " + value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
