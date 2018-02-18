package com.prasantha.pdfsplitter.pdfsplitter.splitter;

import org.junit.Test;

public class PdfSplitterTest {

    @Test
    public void testPdfSplittter() throws Exception {
        System.out.println("start test");
        PdfSplitter pdfSplitter = new PdfSplitter();
        pdfSplitter.splitPdf("payslip.pdf");
//        pdfSplitter.splitPdf();


        System.out.println("end test");
    }

}
