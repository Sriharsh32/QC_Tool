import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.File;

public class PDFDimensionAnnotator {

    static float[][] dimensions = {
            {1, 447.21f, 609.75f},
            {2,776.41f,544.50f},
            {3, 803.41f,544.50f },
            {4, 568.63f,779.73f },
            {5, 561.54f,806.73f },
            {6, 418.67f,544.50f},
            {7, 749.41f,428.24f },
            {8, 592.25f,354.50f },
            {9, 552.09f,368.00f},
            {10, 445.67f,542.37f},
            {11,345.54f,542.56f},
            {12,360.55f,542.56f},
            {13,400.444f,620.54f},

    };

    public static void main(String[] args) {
        try  {
            File file = new File("C:/Users/IMP 04/Downloads/zuelhc1o.pdf");
            PDDocument document = Loader.loadPDF(file);
            PDPage page = document.getPage(0);
            PDPageContentStream contentStream = new PDPageContentStream(
                    document, page, PDPageContentStream.AppendMode.APPEND, true, true
            );

            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);

            PDRectangle mediaBox = page.getMediaBox();
            float pageHeight = mediaBox.getHeight();

            for (float[] dim : dimensions) {
                int number = (int) dim[0];
                float x = dim[1];
                float y = dim[2];

                float correctedY = pageHeight - y; // Flip vertically

                contentStream.beginText();
                contentStream.newLineAtOffset(x + 10, correctedY + 10); // Offset for visibility
                contentStream.showText(String.valueOf(number));
                contentStream.endText();
            }




            contentStream.close();
            document.save("annotated_dimensions.pdf");
            System.out.println("Annotated PDF saved as annotated_dimensions.pdf");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
