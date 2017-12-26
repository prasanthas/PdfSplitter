package com.prasantha.pdfsplitter.pdfsplitter.splitter;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PdfSplitter {

    public void splitPdf() throws IOException {


        File file = new File("payslip.pdf");
        System.out.println("path: "+file.getAbsolutePath());

        PDDocument document = PDDocument.load(file);
        Splitter splitter = new Splitter();
        List<PDDocument> pages = splitter.split(document);

        System.out.println("pages size: "+pages.size());

        Map<String, List<PDDocument>> documentsMap = collectToMap(pages);

        printDocumentsMap(documentsMap);
//        splitByStaffNo(pages);


        System.out.println("Multiple PDF’s created");

        document.close();

    }

    private void printDocumentsMap(Map<String, List<PDDocument>> documentsMap) {
        System.out.println("**********START PRINTING DOCUMENT MAP ******************");

        System.out.println("documents size: " + documentsMap.size());

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
//                merger.setDestinationFileName("output/payslip_"+k+".pdf");
                PDDocument destination = new PDDocument(MemoryUsageSetting.setupMainMemoryOnly());
                v.forEach((pd) -> {
                    try {
                        merger.appendDocument(destination,pd);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    /*
                    PDStream pdStream = new PDStream(pd);
                    try {
//                        merger.addSource(new ByteArrayInputStream(pdStream.toByteArray()));

                        System.out.println("Adding into addSource");

                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/
                });
                /*try {
                    System.out.println("Merging Documents");
                    merger.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
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
        Pattern p = Pattern.compile("(Staff\\s+No\\s+:\\s+)(\\d+)");
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

    private void splitByStaffNo(List<PDDocument> pages) throws IOException {
        Pattern p = Pattern.compile("(Staff\\s+No\\s+:\\s+)(\\d+)");

        PDFTextStripper pdfStripper = new PDFTextStripper();
        List<PDDocument> collectedPdfs = new ArrayList<PDDocument>();
        for (int i = 0; i < pages.size(); i++) {
            PDDocument pd = pages.get(i);
            String text = pdfStripper.getText(pd);

            Matcher matcher = p.matcher(text);

            String previousStaffNo = "";
            if (matcher.find()) {
                String currentStaffNo = matcher.group(2);

                int next = i+1;
                boolean collectedPdf = false;
                if (next < pages.size() ) {
                    PDDocument pd2 = pages.get(next);
                    String text2 = pdfStripper.getText(pd2);
                    Pattern p2 = Pattern.compile("(Staff\\s+No\\s+:\\s+)(\\d+)");
                    Matcher m = p2.matcher(text2);

                    if (m.find()) {
                        if (!currentStaffNo.equals(m.group(2))) {
                            pd.save("output/sampleeeeeee_" + currentStaffNo+ ".pdf");
                            storeCollectedPdfs(collectedPdfs,previousStaffNo);
                            collectedPdf=false;
                            collectedPdfs.clear();
                        }
                    }
                } else {
//                    pd.save("output/sampleeeeeee_" + currentStaffNo+ ".pdf");
                    collectedPdfs.add(pd);
                    collectedPdf = true;
                    previousStaffNo = currentStaffNo;


                }
                pd.save("output/sample_" + currentStaffNo+(i+1)+ ".pdf");
            }
        }
    }

    private void storeCollectedPdfs(List<PDDocument> collectedPdfs,String previousStaffNo) throws IOException {
        if (collectedPdfs.size() > 0) {
            PDFMergerUtility pdfMerger = new PDFMergerUtility();
            pdfMerger.setDestinationFileName(previousStaffNo);
            PDDocument pdDocument = collectedPdfs.get(0);
            for (int i = 1; i < collectedPdfs.size(); i++) {
                if ((i + 1) < collectedPdfs.size()) {
                    pdfMerger.appendDocument(pdDocument, collectedPdfs.get(i));
                }
            }
        }

    }


}
