package io.pdfbucket.pdfbucket;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.*;

/**
 * Created by sanrodari on 6/28/16.
 */
public final class PDFBucket {
    private String apiKey;
    private String apiSecret;
    private String apiHost;

    public static final String DEFAULT_HOST = "api.pdfbucket.io";

    private PDFBucket(String apiKey, String apiSecret, String apiHost) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.apiHost = apiHost;
    }

    public String generateUrl(String uri, String orientation, String pageSize, String margin, String zoom) {
        if (uri == null || uri.trim().isEmpty()) {
            throw new PDFBucketException(new IllegalArgumentException("Invalid uri value, must be not blank"));
        } else if (orientation == null || orientation != "portrait" && orientation != "landscape"){
            throw new PDFBucketException(new IllegalArgumentException("Invalid orientation value, must be portrait or landscape"));
        } else if (pageSize == null || pageSize != "A4" && pageSize != "Letter") {
            throw new PDFBucketException(new IllegalArgumentException("Invalid pageSize value, must be A4 or Letter"));
        } else {
            try {
                StringBuilder sb = new StringBuilder();

                sb.append("/api/convert?");
                sb.append(generateQueryString("orientation", orientation));
                sb.append(generateQueryString("page_size", pageSize));
                sb.append(generateQueryString("margin", margin));
                sb.append(generateQueryString("zoom", zoom));
                sb.append(generateQueryString("api_key", apiKey));
                sb.append(generateQueryString("encrypted_uri", encrypt(apiSecret, uri), true));

                return new URL("https", apiHost, sb.toString()).toString();
            } catch (MalformedURLException e) {
                throw new PDFBucketException(e);
            }
        }
    }

    public String generatePlainUrl(String uri, String orientation, String pageSize, String margin, String zoom) {
        if (uri == null || uri.trim().isEmpty()) {
            throw new PDFBucketException(new IllegalArgumentException("Invalid uri value, must be not blank"));
        } else if (orientation == null || orientation != "portrait" && orientation != "landscape"){
            throw new PDFBucketException(new IllegalArgumentException("Invalid orientation value, must be portrait or landscape"));
        } else if (pageSize == null || pageSize != "A4" && pageSize != "Letter") {
            throw new PDFBucketException(new IllegalArgumentException("Invalid pageSize value, must be A4 or Letter"));
        } else {
            try {
                StringBuilder sb = new StringBuilder();

                sb.append("/api/convert?");
                sb.append(generateQueryString("orientation", orientation));
                sb.append(generateQueryString("page_size", pageSize));
                sb.append(generateQueryString("margin", margin));
                sb.append(generateQueryString("zoom", zoom));
                sb.append(generateQueryString("api_key", apiKey));
                sb.append(generateQueryString("signature", sign(apiSecret, apiKey, uri, orientation, pageSize, margin, zoom)));
                sb.append(generateQueryString("uri", uri, true));

                return new URL("https", apiHost, sb.toString()).toString();
            } catch (MalformedURLException e) {
                throw new PDFBucketException(e);
            }
        }
    }

    private String sign(String apiSecret, String apiKey, String uri, String orientation, String pageSize, String margin, String zoom) {
        try {
            String params = String.format("%s,%s,%s,%s,%s,%s%s", apiKey, uri, orientation, pageSize, margin, zoom, apiSecret);
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(params.getBytes("UTF-8"));

            return new BigInteger(1, crypt.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            throw new PDFBucketException(e);
        } catch (UnsupportedEncodingException e) {
            throw new PDFBucketException(e);
        }
    }

    private String generateQueryString(String name, String value, boolean end) {
        try {
            if(!end) {
                return String.format("%s=%s&", name, URLEncoder.encode(value, "UTF-8"));
            } else {
                return String.format("%s=%s", name, URLEncoder.encode(value, "UTF-8"));
            }
        } catch (UnsupportedEncodingException e) {
            throw new PDFBucketException(e);
        }
    }

    private String generateQueryString(String name, String value) {
        return generateQueryString(name, value, false);
    }

    private String encrypt(String apiSecret, String uri) {
        try {
            byte[] apiSecretBytes = DatatypeConverter.parseBase64Binary(apiSecret);
            SecretKeySpec secretKeySpec = new SecretKeySpec(apiSecretBytes, "AES");

            byte[] ivBytes = new byte[16];
            new SecureRandom().nextBytes(ivBytes);

            Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(ivBytes));
            byte[] encrypted = cipher.doFinal(uri.getBytes("UTF-8"));

            return DatatypeConverter.printBase64Binary(concat(ivBytes, encrypted));
        } catch (NoSuchAlgorithmException e) {
            throw new PDFBucketException(e);
        } catch (NoSuchPaddingException e) {
            throw new PDFBucketException(e);
        } catch (InvalidKeyException e) {
            throw new PDFBucketException(e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new PDFBucketException(e);
        } catch (IllegalBlockSizeException e) {
            throw new PDFBucketException(e);
        } catch (BadPaddingException e) {
            throw new PDFBucketException(e);
        } catch (UnsupportedEncodingException e) {
            throw new PDFBucketException(e);
        }
    }

    private byte[] concat(byte[] a, byte[] b) {
        int aLen = a.length;
        int bLen = b.length;
        byte[] c = new byte[aLen + bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }


    public static final class Builder {
        private String apiKey;
        private String apiSecret;
        private String apiHost;

        public Builder() {
            apiKey = System.getenv("PDF_BUCKET_API_KEY");
            apiSecret = System.getenv("PDF_BUCKET_API_SECRET");
            apiHost = System.getenv("PDF_BUCKET_API_HOST");
            apiHost = apiHost == null ? DEFAULT_HOST : apiHost;
        }

        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public Builder apiSecret(String apiSecret) {
            this.apiSecret = apiSecret;
            return this;
        }

        public Builder apiHost(String apiHost) {
            this.apiHost = apiHost;
            return this;
        }

        public PDFBucket build() {
            if (apiKey == null || apiKey.trim().isEmpty()) {
                throw new PDFBucketException(new IllegalArgumentException("bucket apiKey is required"));
            }

            if (apiSecret == null || apiSecret.trim().isEmpty()) {
                throw new PDFBucketException(new IllegalArgumentException("bucket apiSecret is required"));
            }

            return new PDFBucket(apiKey, apiSecret, apiHost);
        }
    }

}
