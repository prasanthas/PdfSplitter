package com.prasantha.pdfsplitter.pdfsplitter.splitter;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PdfSplitter {

    public void splitPdf() throws IOException {


        File file = new File("payslip.pdf");
        System.out.println("path: "+file.getAbsolutePath());

        PDDocument document = PDDocument.load(file);
        Splitter splitter = new Splitter();
        List<PDDocument> pages = splitter.split(document);

        System.out.println("size: "+pages.size());

        splitByStaffNo(pages);
        
        /*Iterator<PDDocument> iterator = pages.listIterator();
        PDFTextStripper pdfStripper = new PDFTextStripper();
//Saving each page as an individual document
        int i = 1;
        while(iterator.hasNext()) {
            PDDocument pd = iterator.next();
            pd.save("output/sample"+ i++ +".pdf");

            String text = pdfStripper.getText(pd);
//            System.out.println("text: "+text);

           Pattern p = Pattern.compile("(Staff\\s+No\\s+:\\s+)(\\d+)");
            Matcher matcher = p.matcher(text);

            if(matcher.find()) {
//                System.out.println(text);
                System.out.println(matcher.group(0));
                String currentStaffNo = matcher.group(1);
                System.out.println(currentStaffNo);
                //check next page for the number and collatae then
            }

        }*/
        System.out.println("Multiple PDF’s created");

        document.close();

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
//                            storeCollectedPdfs(collectedPdfs,previousStaffNo);
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
