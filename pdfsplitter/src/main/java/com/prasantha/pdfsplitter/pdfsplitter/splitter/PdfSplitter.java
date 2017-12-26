package com.prasantha.pdfsplitter.pdfsplitter.splitter;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PdfSplitter {

    public void splitPdf() throws IOException {
//        File file = new File("payslip.pdf");
        File file = new File("PayslipSample.pdf");
        System.out.println("path: "+file.getAbsolutePath());

        PDDocument document = PDDocument.load(file);
        Splitter splitter = new Splitter();
        List<PDDocument> pages = splitter.split(document);

        System.out.println("pages size: "+pages.size());

        Map<String, List<PDDocument>> documentsMap = collectToMap(pages);

        splitPdfs(documentsMap);

        System.out.println("Multiple PDF’s created");

        document.close();

    }

    private void splitPdfs(Map<String, List<PDDocument>> documentsMap) {
        System.out.println("**********START PRINTING DOCUMENT MAP ******************");

        System.out.println("documents size: " + documentsMap.size());

        File out = new File("output");
        if (!out.exists()) {
            out.mkdir();
        }

        documentsMap.forEach((k,v)->{
            System.out.println("K: "+k);
            System.out.println("V: "+v.size());

            if (v.size() == 1) {
                try {
                    v.get(0).save("output/payslip_"+k+".pdf");
                } catch (IOException e) {
                   e.printStackTrace();

                }
            } else {
                System.out.println("STREAM THROUGH AND CONCAT");
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
                    destination.save("output/payslip_"+k+".pdf");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        System.out.println("**********END PRINTING DOCUMENT MAP ******************");
    }

    private Map<String, List<PDDocument>> collectToMap(List<PDDocument> pages) throws IOException {
//        Pattern p = Pattern.compile("(Staff\\s+No\\s+:\\s+)(\\d+)");
        Pattern p = Pattern.compile("(Staff:\\s+)(\\d+)");
        PDFTextStripper pdfStripper = new PDFTextStripper();
        Map<String, List<PDDocument>> documentsMap = new HashMap<String, List<PDDocument>>();
        for (PDDocument pd: pages) {
            String text = pdfStripper.getText(pd);

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
        }
        return documentsMap;
    }
}
