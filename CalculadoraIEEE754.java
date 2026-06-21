import java.util.Scanner;

public class CalculadoraIEEE754 {

    public static String[] floatToIEEE754Manual(double num) {
        String[] campos = new String[3]; 
        
        if (num == 0.0) {
            campos[0] = "0";
            campos[1] = "00000000";
            campos[2] = "00000000000000000000000";
            return campos;
        }

        campos[0] = (num < 0) ? "1" : "0";
        num = Math.abs(num);

        long inteira = (long) num;
        double fracionaria = num - inteira;

        String binInteira = "";
        if (inteira > 0) {
            long temp = inteira;
            while (temp > 0) {
                binInteira = (temp % 2) + binInteira;
                temp /= 2;
            }
        }

        StringBuilder binFrac = new StringBuilder();
        int contador = 0;
        while (fracionaria > 0 && contador < 150) {
            fracionaria *= 2;
            int bit = (int) fracionaria;
            binFrac.append(bit);
            fracionaria -= bit;
            contador++;
        }
        String strFrac = binFrac.toString();

        int expoenteReal = 0;
        String mantissaCompleta = "";

        if (!binInteira.isEmpty()) {
            expoenteReal = binInteira.length() - 1;
            mantissaCompleta = binInteira.substring(1) + strFrac;
        } else {
            int primeiroUm = strFrac.indexOf("1");
            expoenteReal = -(primeiroUm + 1);
            mantissaCompleta = strFrac.substring(primeiroUm + 1);
        }

        int expoenteBias = expoenteReal + 127;
        String binExpoente = "";
        int tempExp = expoenteBias;
        while (tempExp > 0) {
            binExpoente = (tempExp % 2) + binExpoente;
            tempExp /= 2;
        }
        while (binExpoente.length() < 8) {
            binExpoente = "0" + binExpoente;
        }
        campos[1] = binExpoente;

        if (mantissaCompleta.length() >= 23) {
            campos[2] = mantissaCompleta.substring(0, 23);
        } else {
            StringBuilder sb = new StringBuilder(mantissaCompleta);
            while (sb.length() < 23) {
                sb.append("0");
            }
            campos[2] = sb.toString();
        }

        return campos;
    }

    public static double ieee754ToFloat(String[] campos) {
        int s = Integer.parseInt(campos[0]);
        
        int e = 0;
        for (int i = 0; i < campos[1].length(); i++) {
            if (campos[1].charAt(i) == '1') {
                e += Math.pow(2, campos[1].length() - 1 - i);
            }
        }
        int expoenteReal = e - 127;

        double f = 1.0;
        for (int i = 0; i < campos[2].length(); i++) {
            if (campos[2].charAt(i) == '1') {
                f += Math.pow(2, -(i + 1));
            }
        }

        return Math.pow(-1, s) * f * Math.pow(2, expoenteReal);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== CALCULADORA SIMULADA IEEE 754 (32 BITS) - JAVA ===");
        
        System.out.print("Digite o primeiro número decimal (ex: 0.1): ");
        double num1 = scanner.nextDouble();
        
        System.out.print("Digite o segundo número decimal (ex: 0.2): ");
        double num2 = scanner.nextDouble();

        String[] campos1 = floatToIEEE754Manual(num1);
        String[] campos2 = floatToIEEE754Manual(num2);

        System.out.printf("\nNúmero %s em IEEE 754:\n", num1);
        System.out.printf("  Sinal: %s | Expoente: %s | Mantissa: %s\n", campos1[0], campos1[1], campos1[2]);
        
        System.out.printf("Número %s em IEEE 754:\n", num2);
        System.out.printf("  Sinal: %s | Expoente: %s | Mantissa: %s\n", campos2[0], campos2[1], campos2[2]);

        double val1Simulado = ieee754ToFloat(campos1);
        double val2Simulado = ieee754ToFloat(campos2);
        double resultadoSimuladoDec = val1Simulado + val2Simulado;

        String[] camposResultado = floatToIEEE754Manual(resultadoSimuladoDec);

        System.out.println("\n--- Resultado da Operação (Soma) ---");
        System.out.println("Resultado Dec. da Calculadora Simulada (32-bits): " + resultadoSimuladoDec);
        System.out.printf("Resultado em formato IEEE 754: %s %s %s\n", camposResultado[0], camposResultado[1], camposResultado[2]);
    
        double resultadoNativo = num1 + num2;
        System.out.println("Resultado Nativo do Java (64-bits / Double):       " + resultadoNativo);

        double diferenca = Math.abs(resultadoNativo - resultadoSimuladoDec);
        System.out.println("Diferença absoluta detectada: " + diferenca);

        scanner.close();
    }
}