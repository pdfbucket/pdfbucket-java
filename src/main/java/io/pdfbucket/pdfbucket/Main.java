package io.pdfbucket.pdfbucket;

import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by sanrodari on 6/28/16.
 */
public class Main {
    public static void main(String[] args) {
        PDFBucket pdfBucket = new PDFBucket.Builder()
                .apiHost("staging.pdfbucket.io")
                .apiKey("LDMN2H2AD5OOL973DHTHDDSI5SDRL3VT")
                // http://stackoverflow.com/questions/3862800/invalidkeyexception-illegal-key-size
                // 32 key bytes
                .apiSecret("e5utyNlt1FgAdmFgDKjHjod5AsLK1mrSHSfR/85yGWY=")
                .build();

        String encryptedUrl =
            pdfBucket.generateUrl(
                "http://www.rae.es/consultas/tilde-en-las-mayusculas",
                "portrait",
                "A4",
                "0",
                "1");
        System.out.println(encryptedUrl);

        String plainUrl =
                pdfBucket.generatePlainUrl(
                        "http://www.rae.es/consultas/tilde-en-las-mayusculas",
                        "portrait",
                        "A4",
                        "0",
                        "1");
        System.out.println(plainUrl);
    }
}
