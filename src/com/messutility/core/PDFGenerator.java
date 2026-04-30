package com.messutility.core;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Font;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PDFGenerator {

    public static void generateMonthlyBillsPDF(List<MonthlyBill> bills, String filePath) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph title = new Paragraph("Mess Utility - Month-End Settlement Report", titleFont);
        title.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Paragraph dateStr = new Paragraph("Generated On: " + sdf.format(new Date()));
        dateStr.setSpacingAfter(20);
        document.add(dateStr);

        PdfPTable table = new PdfPTable(2); // 2 columns
        table.setWidthPercentage(100);
        
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        PdfPCell cell1 = new PdfPCell(new Paragraph("Resident Name", headerFont));
        PdfPCell cell2 = new PdfPCell(new Paragraph("Amount (Pay / Owed)", headerFont));
        
        table.addCell(cell1);
        table.addCell(cell2);

        for (MonthlyBill b : bills) {
            table.addCell(b.getResident().getName());
            
            if (b.getAmountDue() > 0) {
                table.addCell("$" + String.format("%.2f", b.getAmountDue()) + " (To Pay)");
            } else if (b.getResident().getNetBalance() > 0) {
                table.addCell("$" + String.format("%.2f", b.getResident().getNetBalance()) + " (Owed to them)");
            } else {
                table.addCell("$0.00");
            }
        }

        document.add(table);
        document.close();
    }
}
