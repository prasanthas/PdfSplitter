package com.prasantha.pdfsplitter.pdfsplitter.splitter;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class PdfSplitterTest {

    @Test
    public void testPdfSplittter() throws Exception {
        System.out.println("start test");
        PdfSplitter pdfSplitter = new PdfSplitter();
        pdfSplitter.splitPdf();


        System.out.println("end test");
    }

}
