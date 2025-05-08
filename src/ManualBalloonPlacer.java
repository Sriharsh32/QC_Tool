import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class ManualBalloonPlacer {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            // Load the drawing PDF
            File file = new File("C:/Users/IMP 04/Downloads/zuelhc1o.pdf");
            PDDocument document = Loader.loadPDF(file);
            PDPage page = document.getPage(0);

            PDPageContentStream contentStream = new PDPageContentStream(
                    document, page,
                    PDPageContentStream.AppendMode.APPEND, true);

            int balloonNumber = 1;
            while (true) {
                System.out.print("Enter X coordinate (or 'done' to finish): ");
                String inputX = scanner.nextLine();
                if (inputX.equalsIgnoreCase("done")) break;

                System.out.print("Enter Y coordinate: ");
                String inputY = scanner.nextLine();

                try {
                    float x = Float.parseFloat(inputX);
                    float y = Float.parseFloat(inputY);
                    drawBalloon(contentStream, x, y, String.valueOf(balloonNumber));
                    balloonNumber++;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number. Please try again.");
                }
            }

            contentStream.close();
            document.save("annotated_zuelhc1o_manual.pdf");
            document.close();

            System.out.println("Finished. Balloons added and saved to 'annotated_zuelhc1o_manual.pdf'.");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    private static void drawBalloon(PDPageContentStream contentStream, float x, float y, String number) throws IOException {
        float radius = 12;
        float k = 0.55228475f;
        float c = k * radius;

        contentStream.setLineWidth(1);
        contentStream.moveTo(x, y + radius);
        contentStream.curveTo(x + c, y + radius, x + radius, y + c, x + radius, y);
        contentStream.curveTo(x + radius, y - c, x + c, y - radius, x, y - radius);
        contentStream.curveTo(x - c, y - radius, x - radius, y - c, x - radius, y);
        contentStream.curveTo(x - radius, y + c, x - c, y + radius, x, y + radius);
        contentStream.closePath();
        contentStream.stroke();

        // Draw number inside
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD_OBLIQUE), 15);
        contentStream.newLineAtOffset(x - 3, y - 3);
        contentStream.showText(number);
        contentStream.endText();
    }
}

