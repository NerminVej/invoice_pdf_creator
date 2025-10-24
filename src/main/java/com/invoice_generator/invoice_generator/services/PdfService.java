package com.invoice_generator.invoice_generator.services;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import org.xhtmlrenderer.pdf.ITextRenderer;

@Service
public class PdfService {

    private final SpringTemplateEngine templateEngine;

    public PdfService(SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public byte[] renderTemplateToPdf(String templateName, Map<String, Object> model, String baseUrl) {
        Context ctx = new Context();
        model.forEach(ctx::setVariable);
        String html = templateEngine.process(templateName, ctx);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            if (baseUrl != null && !baseUrl.isBlank()) {
                renderer.setDocumentFromString(html, baseUrl);
            } else {
                renderer.setDocumentFromString(html);
            }
            renderer.layout();
            renderer.createPDF(out);
            renderer.finishPDF();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to render PDF", e);
        }
    }
}
