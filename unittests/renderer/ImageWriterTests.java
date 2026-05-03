package renderer;

import org.junit.jupiter.api.Test;
import primitives.Color; // וודא שאתה מייבא את מחלקת ה-Color של הפרויקט
    public class ImageWriterTests {

        @Test
        void testImageWriter() {
            // הגדרת קבועים (כדי להימנע מ-Hard Code)
            final int nX = 800;
            final int nY = 500;
            final int step = 50;

            // צבעים בעלי ניגודיות גבוהה (למשל צהוב ואדום כמו בצילום)
            Color backgroundColor = new Color(255, 255, 0); // צהוב
            Color gridColor = new Color(255, 0, 0);        // אדום

            // 1. יצירת האובייקט
            ImageWriter imageWriter = new ImageWriter(nX, nY);

            // 2. מעבר על כל הפיקסלים וצביעתם
            for (int i = 0; i < nY; i++) {
                for (int j = 0; j < nX; j++) {
                    // שימוש באופרטור טרנרי לבדיקה אם אנחנו על קו רשת
                    // אם השארית של המיקום בחלוקה ב-50 היא 0, זהו קו רשת
                    imageWriter.writePixel(j, i,
                            (i % step == 0 || j % step == 0) ? gridColor : backgroundColor
                    );
                }
            }

            // 3. יצירת הקובץ בפועל
            imageWriter.writeToImage("testImageWriter.png");
        }
    }
