# PDFBucket   [![Build Status](https://travis-ci.org/pdfbucket/pdfbucket-java.svg?branch=master)](https://travis-ci.org/pdfbucket/pdfbucket-java)

PDFBucket library allows you to integrate easily with the PDFBucket service. Automatically tested against Java version 6, 7, and 8.


## Installation

TODO

## Usage

To encrypt a URL in your code instantiate a PDFBucket object and use its `generateUrl` method.
The new pdfBucket will use `PDF_BUCKET_API_KEY`, `PDF_BUCKET_API_SECRET`, `PDF_BUCKET_API_HOST` (default is `api.pdfbucket.io`) ENV vars:

```java
import io.pdfbucket.pdfbucket;
...

PDFBucket pdfBucket = new PDFBucket.Builder().build();
```

You can also set any the api params, overwriting then ENV vars like this:

```java
PDFBucket otherPDFBucket = new PDFBucket.Builder()
  .apiKey("ABCDEFGHIJKLMNO")
  .apiSecret("1234567890ABCDE")
  .apiHost("api.example.com")
  .build();
```

And you get the encryptedUrl using the generateUrl method (To use encryption you need to install JCE http://stackoverflow.com/a/6481658/491957):

```java
String encryptedUrl = pdfBucket.generateUrl("http://example.com", "landscape", "A4", "2px", "0.7");
```

Also you can pass the plain URL to PDFBucket

```java
String plainUrl = pdfBucket.generatePlainUrl("http://example.com", "landscape", "A4", "2px", "0.7");
```

* Possible values for orientation: "landscape", "portrait"
* Possible values for page size: "Letter", "A4"
* Possible values for margin: https://developer.mozilla.org/en-US/docs/Web/CSS/margin#Formal_syntax
* Possible values for zoom: https://developer.mozilla.org/en-US/docs/Web/CSS/@viewport/zoom#Formal_syntax
