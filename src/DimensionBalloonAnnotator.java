import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DimensionBalloonAnnotator {
    static class DimensionPosition {
        float x, y;
        String text;
        int number;

        DimensionPosition(float x, float y, String text, int number) {
            this.x = x;
            this.y = y;
            this.text = text;
            this.number = number;
        }
    }

    public static void main(String[] args) throws IOException {
        File file = new File("C:/Users/IMP 04/Downloads/zuelhc1o.pdf"); // Adjust path if needed
        PDDocument document = Loader.loadPDF(file);

        List<DimensionPosition> dimensions = new ArrayList<>();

        PDFTextStripper stripper = new PDFTextStripper() {
            int balloonNumber = 1;

            @Override
            protected void writeString(String string, List<TextPosition> textPositions) throws IOException {
                if (isDimensionText(string)) {
                    float avgX = 0, avgY = 0;
                    for (TextPosition tp : textPositions) {
                        avgX += tp.getXDirAdj();
                        avgY += tp.getYDirAdj();
                    }
                    avgX /= textPositions.size();
                    avgY /= textPositions.size();

                    dimensions.add(new DimensionPosition(avgX, avgY, string, balloonNumber++));
                }
                super.writeString(string, textPositions);
            }

            private boolean isDimensionText(String text) {
                return text.matches("(?i)[⌀R]?[0-9]+(\\.[0-9]+)?");
            }
        };

        stripper.setSortByPosition(true);
        stripper.getText(document); // Extracts text

        // Draw balloons
        PDPage page = document.getPage(0);
        float pageHeight = page.getMediaBox().getHeight();

        PDPageContentStream contentStream = new PDPageContentStream(
                document, page, PDPageContentStream.AppendMode.APPEND, true);

        for (DimensionPosition dim : dimensions) {
            float adjustedY = pageHeight - dim.y - 5;
            drawBalloon(contentStream, dim.x + 10, adjustedY, 10, dim.number);
        }

        contentStream.close();

        File outputFile = new File("numbered.pdf");
        document.save(outputFile);
        document.close();

        System.out.println("✅ Balloons added. Saved as: " + outputFile.getAbsolutePath());
    }

    private static void drawBalloon(PDPageContentStream contentStream, float x, float y, float r, int number) throws IOException {
        drawCircle(contentStream, x, y, r);
        drawBalloonText(contentStream, x, y, number);
    }

    private static void drawBalloonText(PDPageContentStream contentStream, float x, float y, int number) throws IOException {
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 10);

        String text = String.valueOf(number);
        float textWidth = 5 * text.length(); // Approximate width
        float textHeight = 10;

        contentStream.newLineAtOffset(x - textWidth / 2, y - textHeight / 4);
        contentStream.setNonStrokingColor(0f, 0.0f, 0f); // Green color
        contentStream.showText(text);
        contentStream.endText();
    }

    private static void drawCircle(PDPageContentStream contentStream, float x, float y, float r) throws IOException {
        final float k = 0.552284749831f;
        float c = r * k;

        contentStream.moveTo(x + r, y);
        contentStream.curveTo(x + r, y + c, x + c, y + r, x, y + r);
        contentStream.curveTo(x - c, y + r, x - r, y + c, x - r, y);
        contentStream.curveTo(x - r, y - c, x - c, y - r, x, y - r);
        contentStream.curveTo(x + c, y - r, x + r, y - c, x + r, y);
        contentStream.closePath();

        contentStream.setNonStrokingColor(1f); // white fill
        contentStream.setStrokingColor(0f);    // black border
        contentStream.fillAndStroke();
    }
}
