package com.prasantha.pdfsplitter.pdfsplitter.splitter;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PdfSplitter {

    private static final Logger logger = LogManager.getLogger(PdfSplitter.class);

    public void splitPdf(String fileName, Splitter p) throws IOException {

        System.out.println("PPPP: "+p);

//        File file = new File("payslip.pdf");
        File file = new File(fileName);
//        File file = new File("PayslipSample.pdf");
        logger.debug("path: "+file.getAbsolutePath());

        PDDocument document = PDDocument.load(file);
        Splitter splitter = new Splitter();
        List<PDDocument> pages = splitter.split(document);

        logger.debug("pages size: "+pages.size());

        Map<String, List<PDDocument>> documentsMap = collectToMap(pages);

        splitPdfs(documentsMap);

        logger.debug("Multiple PDF’s created");

        document.close();

    }

    private void splitPdfs(Map<String, List<PDDocument>> documentsMap) {

        logger.debug("documents size: " + documentsMap.size());

        File out = new File("output");
        if (!out.exists()) {
            out.mkdir();
        }
        

        String date = getDate();

        documentsMap.forEach((k,v)->{
            logger.debug("K: "+k);
            logger.debug("V: "+v.size());

            if (v.size() == 1) {
                try {
                    v.get(0).save("output/"+date+"-"+k+".pdf");
                } catch (IOException e) {
                   e.printStackTrace();

                }
            } else {
                logger.debug("STREAM THROUGH AND CONCAT");
                PDFMergerUtility merger = new PDFMergerUtility();
                PDDocument destination = new PDDocument(MemoryUsageSetting.setupMainMemoryOnly());
                v.forEach((pd) -> {
                    try {
                        merger.appendDocument(destination,pd);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                });

                try {
                    destination.save("output/"+date+"-"+k+".pdf");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private String getDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String date = sdf.format(new Date());
        return date;
    }

    private Map<String, List<PDDocument>> collectToMap(List<PDDocument> pages) throws IOException {
        Pattern p = Pattern.compile("(Staff\\s+No\\s+:\\s+)(\\d+)");
//        Pattern p = Pattern.compile("(Staff:\\s+)(\\d+)");
        PDFTextStripper pdfStripper = new PDFTextStripper();
        Map<String, List<PDDocument>> documentsMap = new HashMap<String, List<PDDocument>>();

        pages.forEach(pd -> {
            String text = null;
            try {
                text = pdfStripper.getText(pd);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Matcher matcher = p.matcher(text);

            if (matcher.find()) {
                String staffNo = matcher.group(2);
                if (documentsMap.containsKey(staffNo)) {
                    documentsMap.get(staffNo).add(pd);
                } else {
                    ArrayList<PDDocument> pdDocuments = new ArrayList<>();
                    pdDocuments.add(pd);
                    documentsMap.put(staffNo,pdDocuments);
                }
            }
        });

        return documentsMap;
    }
}
