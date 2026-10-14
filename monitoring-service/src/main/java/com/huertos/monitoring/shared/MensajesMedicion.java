package com.huertos.monitoring.shared;

public final class MensajesMedicion {

    public static final String PARCELA_AUSENTE =
            "La medición no dice de qué parcela es. Una lectura de humedad, pH o aire solo se guarda si está amarrada a una zona ya dada de alta.";

    public static final String PARCELA_BLOQUEADA =
            "El código de parcela trae etiquetas o operadores de consulta. Se espera el UUID que devolvió el registro al crear la zona, en texto plano.";

    public static final String HUMEDAD_AUSENTE =
            "La lectura llegó sin humedad del suelo. Sin ese porcentaje no se puede avisar si la parcela necesita riego.";

    public static final String TEMPERATURA_AUSENTE =
            "La lectura llegó sin temperatura. Sin los grados del aire junto a la parcela el estado queda incompleto.";

    public static final String PH_AUSENTE =
            "La lectura llegó sin pH. Sin la acidez del suelo no se puede recomendar una enmienda ni decir si la parcela está en rango de cultivo.";

    public static final String NITROGENO_AUSENTE =
            "La lectura llegó sin nitrógeno. El estado nutricional de la parcela queda incompleto si falta este nutriente.";

    public static final String FOSFORO_AUSENTE =
            "La lectura llegó sin fósforo. El estado nutricional de la parcela queda incompleto si falta este nutriente.";

    public static final String POTASIO_AUSENTE =
            "La lectura llegó sin potasio. El estado nutricional de la parcela queda incompleto si falta este nutriente.";

    public static final String AIRE_AUSENTE =
            "La lectura llegó sin calidad del aire. Sin el índice local no se puede avisar a los voluntarios si conviene acortar la faena.";

    private MensajesMedicion() {
    }

    public static String parcelaInvalida(String citado) {
        return "El valor " + citado
                + " no es el código de una parcela de esta red. La medición tiene que llevar el UUID completo que se entregó al dar de alta la zona.";
    }

    public static String noFinito(String canal) {
        return "El canal " + canal + " no es un número finito. El sensor tiene que reportar un decimal real, no un valor vacío o indefinido.";
    }

    public static String humedadFuera(double valor) {
        return "La humedad del suelo llegó en " + Citas.numero(valor)
                + " y el sensor la expresa como porcentaje entre 0 y 100. Esa cifra no se guardó para no distorsionar el estado de la parcela.";
    }

    public static String temperaturaFuera(double valor) {
        return "La temperatura llegó en " + Citas.numero(valor)
                + " °C y los sensores de la red solo aceptan entre -20 y 60. Revisa si el dispositivo envió otra unidad; la medición no se guardó.";
    }

    public static String phFuera(double valor) {
        return "El pH llegó en " + Citas.numero(valor)
                + " y la escala de suelo va de 0 a 14. Un valor fuera de esa escala no describe la parcela, así que la medición se rechazó.";
    }

    public static String nitrogenoFuera(double valor) {
        return "El nitrógeno llegó en " + Citas.numero(valor)
                + " y el sensor de la red solo admite de 0 a 500. Esa lectura no entra al historial de la parcela.";
    }

    public static String fosforoFuera(double valor) {
        return "El fósforo llegó en " + Citas.numero(valor)
                + " y el sensor de la red solo admite de 0 a 500. Esa lectura no entra al historial de la parcela.";
    }

    public static String potasioFuera(double valor) {
        return "El potasio llegó en " + Citas.numero(valor)
                + " y el sensor de la red solo admite de 0 a 500. Esa lectura no entra al historial de la parcela.";
    }

    public static String aireFuera(double valor) {
        return "La calidad del aire llegó en " + Citas.numero(valor)
                + " y el índice de la red va de 0 a 500. Un índice fuera de esa escala no se publica como estado de la parcela.";
    }
}
