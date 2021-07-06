package br.com.inovasoft.epedidos.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.exceptions.CssResolverException;
import com.itextpdf.tool.xml.exceptions.RuntimeWorkerException;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.end.PdfWriterPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
@NoArgsConstructor
public class RelatorioUtil {


	static final String SRC_RELATORIOS = "relatorios" + File.separator;

	private String css;
	private Termo pageEventHelper;

	@Inject
	MustacheUtil mustacheUtil;

	public RelatorioUtil(String css) {
		this.css = css;
	}

	public static RelatorioUtil css(String css) {
		return new RelatorioUtil(css);
	}

	public byte[] gerarRelatorioPedidosFechados(Object objetoAtual) throws DocumentException, IOException, CssResolverException {

		String estilo = retornaresource(SRC_RELATORIOS + "ordersClosed.css");
		String template = retornaresource(SRC_RELATORIOS + "ordersClosed.html");

		String relatorio = mustacheUtil.preencheAtributosMustache(template, objetoAtual);
		Map<String, String> parametros = new HashMap<>();

		return RelatorioUtil.css(estilo).geraPdf(relatorio, objetoAtual.toString(), new Rodape(parametros));
	}

	private static String retornaresource(String path) {

		ClassLoader classLoader = RelatorioUtil.class.getClassLoader();

		try (InputStream inputStream = classLoader.getResourceAsStream(path)) {
			if(inputStream != null) {
				return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
			}
		} catch (IOException e) {
			log.error("Não foi possível recuperar o recurso solicitado.", e);
		}

		return StringUtils.EMPTY;
	}

	public byte[] geraPdf(String relatorioHtml, String titulo, Termo pdfPageEventHelper) throws DocumentException, IOException, CssResolverException {

		pageEventHelper = pdfPageEventHelper;

		// criacao do pdf
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Document document = new Document();

		PdfWriter pdfWriter = PdfWriter.getInstance(document, out);

		document.addAuthor("LR4Tech 2021");
		document.addCreationDate();
		document.addProducer();
		document.addCreator("www.lr4tech.com");
		document.addTitle(titulo);
		document.setPageSize(PageSize.A4);
		document.setMargins(0, 0, 0, 0);
		document.open();

		InputStream stream = new ByteArrayInputStream(relatorioHtml.getBytes(StandardCharsets.UTF_8));
		XMLWorker worker = configurarParser(document, pdfWriter, css);
		XMLParser xmlParser = new XMLParser(worker);

		try {
			xmlParser.parse(stream, true);
		} catch (RuntimeWorkerException e) {
			log.error("Esse erro normalmente acontece quando seu html tem problemas. " +
					"Verifique se seu HTML tem as tags (html e body) e teste retirar essas tags, " +
					"deixando apenas o conteúdo do body.", e);
		}

		pdfWriter.setPageEvent(pageEventHelper);

		document.close();
		pdfWriter.close();

		return out.toByteArray();
	}

	protected XMLWorker configurarParser(Document document, PdfWriter pdfWriter, String css) throws CssResolverException {
		PdfWriterPipeline pdfWriterPipeline = new PdfWriterPipeline(document, pdfWriter);
		
		HtmlPipeline htmlPipeline = null;
		
		try {			
			htmlPipeline = new HtmlPipeline(pageEventHelper.retornaHtmlContext(), pdfWriterPipeline);
		} catch (RuntimeException e) {
			log.error("Erro na conversão da imagem: " + e.getMessage());
		}
		
		CssResolverPipeline cssResolverPipeline = new CssResolverPipeline(pageEventHelper.configurarCssResolver(css), htmlPipeline);
		return new XMLWorker(cssResolverPipeline, true);
	}


	public byte[] mergePdfFiles(List<byte[]> pdfs) throws IOException, DocumentException {

		List<InputStream> inputPdfList = pdfs.stream()
				.map(ByteArrayInputStream::new)
				.collect(Collectors.toList());

		//Create document and pdfReader objects.
		Document document = new Document();
		List<PdfReader> readers = new ArrayList<>();
		int totalPages = 0;

		//Create pdf Iterator object using inputPdfList.

		// Create reader list for the input pdf files.
		for (InputStream pdf : inputPdfList) {
			PdfReader pdfReader = new PdfReader(pdf);
			readers.add(pdfReader);
			totalPages = totalPages + pdfReader.getNumberOfPages();
		}
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		// Create writer for the outputStream
		PdfWriter writer = PdfWriter.getInstance(document, outputStream);

		//Open document.
		document.open();

		//Contain the pdf data.
		PdfContentByte pageContentByte = writer.getDirectContent();

		PdfImportedPage pdfImportedPage;
		int currentPdfReaderPage = 1;

		// Iterate and process the reader list.
		for (PdfReader pdfReader : readers) {
			//Create page and add content.
			while (currentPdfReaderPage <= pdfReader.getNumberOfPages()) {
				document.newPage();
				pdfImportedPage =
						writer.getImportedPage(pdfReader, currentPdfReaderPage);
				pageContentByte.addTemplate(pdfImportedPage, 0, 0);
				currentPdfReaderPage++;
			}
			currentPdfReaderPage = 1;
		}

		//Close document and outputStream.
		outputStream.flush();
		document.close();
		outputStream.close();

		return outputStream.toByteArray();
	}

}


