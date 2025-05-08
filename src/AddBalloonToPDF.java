
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import java.io.File;
import java.io.IOException;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.apache.pdfbox.text.TextPosition;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.pdmodel.PDPage;
public class AddBalloonToPDF {
    public static void main(String[] args) {
        try {
            File file = new File("C:/Users/IMP 04/Downloads/zuelhc1o.pdf");
           // File file = new File("C:\\Users\\IMP 04\\Downloads\\zuelhc1o");
           // PDDocument document = PDDocument.load(file, "");
            PDDocument document = Loader.loadPDF(file);
            PDPage page = document.getPage(0);
            PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true);
            drawBalloon(contentStream, 150, 700, "1");
            drawBalloon(contentStream, 300, 650, "2");
            drawBalloon(contentStream, 450, 600, "3");
            contentStream.close();
            document.save("annotated_zuelhc1o.pdf");
            document.close();
            System.out.println("Annotated PDF saved!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void drawBalloon(PDPageContentStream contentStream, float centerX, float centerY, String number) throws IOException {
        float radius = 15;
        float k = 0.55228475f; // Control point constant for bezier circle approximatio
        float c = k * radius;
        float x = centerX;
        float y = centerY;
        contentStream.setLineWidth(1);
        contentStream.moveTo(x, y + radius);
        contentStream.curveTo(x + c, y + radius, x + radius, y + c, x + radius, y);
        contentStream.curveTo(x + radius, y - c, x + c, y - radius, x, y - radius);
        contentStream.curveTo(x - c, y - radius, x - radius, y - c, x - radius, y);
        contentStream.curveTo(x - radius, y + c, x - c, y + radius, x, y + radius);
        contentStream.closePath();
        contentStream.stroke();
        // Draw text in a center
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD_OBLIQUE), 12);
        contentStream.newLineAtOffset(x - 4, y - 4); // crude centering
        contentStream.showText(number);
        contentStream.endText();
    }

}
