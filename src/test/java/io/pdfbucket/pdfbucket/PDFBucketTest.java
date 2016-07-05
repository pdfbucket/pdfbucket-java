package io.pdfbucket.pdfbucket;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by sanrodari on 7/5/16.
 */
public class PDFBucketTest {
    @Test
    public void createPDFBucketWithValidParams() {
        Assert.assertEquals("lolo", "lala");

        Assert.assertNotNull(buildValidPDFBucket());
    }

    @Test
    public void createPDFBucketWithInvalidParams() {
        try {
            new PDFBucket.Builder()
                    .apiKey("PIQ7T3GOM7D36R0O67Q97UM3F0I6CPB5")
                    .build();
        } catch (PDFBucketException e) {
            Assert.assertTrue(e.getMessage().contains("bucket apiSecret is required"));
        }

        try {
            new PDFBucket.Builder()
                    .apiSecret("HieMN8dvi5zfSbKvqxKccxDo3LozqOIrY59U/jrZY54=")
                    .build();
        } catch (PDFBucketException e) {
            Assert.assertTrue(e.getMessage().contains("bucket apiKey is required"));
        }
    }

    @Test
    public void generateUrlWhenValidParams() {
        PDFBucket pdfBucket = buildValidPDFBucket();

        String encryptedUrl = pdfBucket.generateUrl("https://www.joyent.com/", "landscape", "A4", "2px", "0.7");
        Assert.assertNotNull(encryptedUrl);
        Assert.assertTrue(encryptedUrl.contains("encrypted_uri"));
    }

    @Test
    public void generatePlainUrlWhenValidParams() {
        PDFBucket pdfBucket = buildValidPDFBucket();

        String plainUrl = pdfBucket.generatePlainUrl("https://www.joyent.com/", "landscape", "A4", "2px", "0.7");
        Assert.assertNotNull(plainUrl);
        Assert.assertTrue(plainUrl.contains("uri"));
        Assert.assertTrue(plainUrl.contains("joyent"));
    }

    @Test
    public void throwsWhenUriIsBlankOrNull() {
        PDFBucket pdfBucket = buildValidPDFBucket();

        try {
            pdfBucket.generatePlainUrl(null, "landscape", "A4", "2px", "0.7");
        } catch (PDFBucketException e) {
            Assert.assertTrue(e.getMessage().contains("Invalid uri value, must be not blank"));
        }

        try {
            pdfBucket.generatePlainUrl("    ", "landscape", "A4", "2px", "0.7");
        } catch (PDFBucketException e) {
            Assert.assertTrue(e.getMessage().contains("Invalid uri value, must be not blank"));
        }
    }

    @Test
    public void throwsWhenOrientationInvalid() {
        PDFBucket pdfBucket = buildValidPDFBucket();

        try {
            pdfBucket.generatePlainUrl("https://www.joyent.com/", null, "A4", "2px", "0.7");
        } catch (PDFBucketException e) {
            Assert.assertTrue(e.getMessage().contains("Invalid orientation value, must be portrait or landscape"));
        }

        try {
            pdfBucket.generatePlainUrl("https://www.joyent.com/", "something", "A4", "2px", "0.7");
        } catch (PDFBucketException e) {
            Assert.assertTrue(e.getMessage().contains("Invalid orientation value, must be portrait or landscape"));
        }
    }

    @Test
    public void throwsWhenPageSizeInvalid() {
        PDFBucket pdfBucket = buildValidPDFBucket();

        try {
            pdfBucket.generatePlainUrl("https://www.joyent.com/", "landscape", null, "2px", "0.7");
        } catch (PDFBucketException e) {
            Assert.assertTrue(e.getMessage().contains("Invalid pageSize value, must be A4 or Letter"));
        }

        try {
            pdfBucket.generatePlainUrl("https://www.joyent.com/", "landscape", "something", "2px", "0.7");
        } catch (PDFBucketException e) {
            Assert.assertTrue(e.getMessage().contains("Invalid pageSize value, must be A4 or Letter"));
        }
    }

    private PDFBucket buildValidPDFBucket() {
        return new PDFBucket.Builder()
                .apiKey("PIQ7T3GOM7D36R0O67Q97UM3F0I6CPB5")
                .apiSecret("HieMN8dvi5zfSbKvqxKccxDo3LozqOIrY59U/jrZY54=")
                .build();
    }
}
