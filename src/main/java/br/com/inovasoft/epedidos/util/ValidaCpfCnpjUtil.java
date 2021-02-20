package br.com.inovasoft.epedidos.util;

import org.apache.commons.lang3.StringUtils;

import static org.apache.commons.lang3.StringUtils.isBlank;

public final class ValidaCpfCnpjUtil {

	private static final int[] pesoCPF = {11, 10, 9, 8, 7, 6, 5, 4, 3, 2};
	private static final int[] pesoCNPJ = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

	private ValidaCpfCnpjUtil() { }

	public static boolean isCPFValido(String cpf) {
		if(isBlank(cpf)) return false;

		cpf = cpf.trim().replaceAll("\\D", StringUtils.EMPTY);

		if (StringUtils.length(cpf) != 11) {
			return false;
		}


		for (int j = 0; j < 10; j++) {
			if (padLeft(Integer.toString(j), Character.forDigit(j, 10)).equals(cpf)) {
				return false;
			}
		}

		int digito1 = calcularDigito(cpf.substring(0, 9), pesoCPF);
		int digito2 = calcularDigito(cpf.substring(0, 9) + digito1, pesoCPF);

		return cpf.equals(cpf.substring(0, 9) + digito1 + digito2);
	}

	public static boolean isCNPJValido(String cnpj) {

		if(isBlank(cnpj)) return false;

		cnpj = cnpj.trim().replaceAll("\\D", StringUtils.EMPTY);

		if (StringUtils.length(cnpj) != 14) {
			return false;
		}


		int digito1 = calcularDigito(cnpj.substring(0, 12), pesoCNPJ);
		int digito2 = calcularDigito(cnpj.substring(0, 12) + digito1, pesoCNPJ);

		return cnpj.equals(cnpj.substring(0, 12) + digito1 + digito2);
	}

	private static int calcularDigito(String str, int[] peso) {
		int soma = 0;
		int digito;

		for (int indice = str.length() - 1; indice >= 0; indice--) {
			digito = Integer.parseInt(str.substring(indice, indice + 1));
			soma += digito * peso[peso.length - str.length() + indice];
		}
		soma = 11 - soma % 11;
		return soma > 9 ? 0 : soma;
	}

	private static String padLeft(String text, char character) {
		return String.format("%11s", text).replace(' ', character);
	}

}
