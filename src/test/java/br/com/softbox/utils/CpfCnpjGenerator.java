package br.com.softbox.utils;
import org.apache.commons.lang3.RandomStringUtils;



public class CpfCnpjGenerator {
    private enum DocType { CPF, CNPJ };
    private static final Integer[] weightCPF = { 11, 10, 9, 8, 7, 6, 5, 4, 3, 2 };
    private static final Integer[] weightCNPJ = { 6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2 };

    private static Integer digitCalc(String str, Integer[] weight) {
        int sum = 0;
        for (int index = str.length() - 1, digit; index >= 0; index--) {
            digit = Integer.parseInt(str.substring(index, index + 1));
            sum += digit * weight[weight.length - str.length() + index];
        }
        sum = 11 - sum % 11;
        return sum > 9 ? 0 : sum;
    }

    private static String generate(DocType docType) {
        Integer docLength;
        Integer[] weight;
        if(docType == DocType.CPF) {
            docLength = 9;
            weight = weightCPF;
        } else if(docType == DocType.CNPJ) {
            docLength = 12;
            weight = weightCNPJ;
        } else {
            return null;
        }
        String doc = RandomStringUtils.randomNumeric(docLength);
        Integer firstDigit = digitCalc(doc, weight);
        Integer secondDigit = digitCalc(doc + firstDigit, weight);
        return doc + firstDigit + secondDigit;
    }

    public static String cpf() {
        return generate(DocType.CPF);
    }

    public static String cnpj() {
        return generate(DocType.CNPJ);
    }

}
