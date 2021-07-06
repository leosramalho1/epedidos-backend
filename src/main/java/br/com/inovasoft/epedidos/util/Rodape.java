package br.com.inovasoft.epedidos.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class Rodape extends PdfPageEventHelper implements Termo {

	private final Map<String, String> parametros;

	@Override
	public void onEndPage(PdfWriter writer, Document document) {

	}
}