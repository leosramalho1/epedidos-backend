package br.com.inovasoft.epedidos.util;

import com.itextpdf.text.pdf.PdfPageEvent;
import com.itextpdf.tool.xml.ElementList;
import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.exceptions.CssResolverException;
import com.itextpdf.tool.xml.html.TagProcessorFactory;
import com.itextpdf.tool.xml.html.Tags;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.pipeline.css.CSSResolver;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.end.ElementHandlerPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public interface Termo extends PdfPageEvent {

	default CSSResolver configurarCssResolver(String css) throws CssResolverException {
		CSSResolver cssResolver = XMLWorkerHelper.getInstance().getDefaultCssResolver(true);
		if (StringUtils.isNotEmpty(css)) {
			cssResolver.addCss(css, Boolean.TRUE);
		}
		return cssResolver;
	}

	default ElementList parseHtmlParaElementosPDF(byte[] html, String css) throws IOException, CssResolverException {
		ElementList elements = new ElementList();
		// Pipelines
		ElementHandlerPipeline end = new ElementHandlerPipeline(elements, null);
		HtmlPipeline htmlPipeline = new HtmlPipeline(retornaHtmlContext(), end);
		CssResolverPipeline cssPipeline = new CssResolverPipeline(configurarCssResolver(css), htmlPipeline);

		// XML Worker
		XMLWorker worker = new XMLWorker(cssPipeline, true);
		XMLParser parser = new XMLParser(worker);
		parser.parse(new ByteArrayInputStream(html), true);

		return elements;
	}

	default HtmlPipelineContext retornaHtmlContext() {
		HtmlPipelineContext htmlPipelineContext = new HtmlPipelineContext(null);
		TagProcessorFactory htmlTagProcessorFactory = Tags.getHtmlTagProcessorFactory();
		htmlPipelineContext.setTagFactory(htmlTagProcessorFactory);
		return htmlPipelineContext;
	}

}