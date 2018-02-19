package com.prasantha.pdfsplitter.pdfsplitter.splitter;

import org.apache.pdfbox.multipdf.Splitter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PdfSplitterTest {

    @Autowired
    private Splitter splitter;

    @Test
    public void testPdfSplittter() throws Exception {
        System.out.println("start test");
        PdfSplitter pdfSplitter = new PdfSplitter();
        System.out.println("splitter: "+splitter);
        pdfSplitter.splitPdf("payslip.pdf",splitter);
//        pdfSplitter.splitPdf();


        System.out.println("end test");
    }

}
